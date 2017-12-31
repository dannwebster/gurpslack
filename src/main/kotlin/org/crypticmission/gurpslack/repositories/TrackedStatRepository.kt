package org.crypticmission.gurpslack.repositories

import org.crypticmission.gurpslack.entities.TrackedStat
import org.slf4j.LoggerFactory
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository

/**
 */

@Component
class TrackedStatService(val trackedStatRepository: TrackedStatRepository) {
    private val logger = LoggerFactory.getLogger(TrackedStatService::class.java)

    companion object {
        val DEFAULT_STAT = 10
    }

    fun getTrackedStatMap(characterKey: String): Map<String, Int> =
            trackedStatRepository
                    .findByCharacterKey(characterKey)
                    .map { stat -> Pair(stat.statName, stat.value) }
                    .toMap()

    fun modifyByCharacterKeyAndStatName(characterKey: String, statName: String, adjustment: Int): TrackedStat {
        return doSaveOrUpdateByCharacterKeyAndStatName("adjusting", characterKey, statName, {t -> t.value + adjustment })
    }

    fun saveOrUpdateByCharacterKeyAndStatName(characterKey: String, statName: String, newValue: Int): TrackedStat {
        return doSaveOrUpdateByCharacterKeyAndStatName("setting", characterKey, statName, {t -> newValue})
    }

    fun doSaveOrUpdateByCharacterKeyAndStatName(action: String, characterKey: String, statName: String, newValueSupplier: (TrackedStat) -> Int): TrackedStat {
        val trackedStat = trackedStatRepository.findOneByCharacterKeyAndStatName(characterKey, statName) ?:
                TrackedStat(characterKey, statName, DEFAULT_STAT)
        val newValue = newValueSupplier(trackedStat)
        logger.debug("${action} ${if (trackedStat.id == 0L)  "new" else "existing"} ${characterKey}.${statName} from ${trackedStat.value} to ${newValue}")
        trackedStat.value = newValue
        trackedStatRepository.save(trackedStat)
        return trackedStat
    }
}

@Repository
interface TrackedStatRepository: CrudRepository<TrackedStat, Long> {
    fun findByCharacterKey(characterKey: String): List<TrackedStat>
    fun findOneByCharacterKeyAndStatName(characterKey: String, statName: String): TrackedStat?
}
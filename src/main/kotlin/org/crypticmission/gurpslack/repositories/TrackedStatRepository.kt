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

   fun getTrackedStatMap(characterKey: String): Map<String, Int> =
       trackedStatRepository
               .findByCharacterKey(characterKey)
               .map { stat -> Pair(stat.statName, stat.value) }
               .toMap()

    fun saveOrUupdateByCharacterKeyAndStatName(characterKey: String, statName: String, value: Int): TrackedStat {
        val old = trackedStatRepository.findOneByCharacterKeyAndStatName(characterKey, statName)
        val new = if (old != null) {
            old.value = value
            trackedStatRepository.save(old)
        } else {
            val new = TrackedStat(characterKey, statName, value)
            trackedStatRepository.save(new)
        }
        return new
    }
}

@Repository
interface TrackedStatRepository: CrudRepository<TrackedStat, Long> {
    fun findByCharacterKey(characterKey: String): List<TrackedStat>
    fun findOneByCharacterKeyAndStatName(characterKey: String, statName: String): TrackedStat
}
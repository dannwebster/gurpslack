package org.crypticmission.gurpslack.controllers

import org.crypticmission.gurpslack.entities.TrackedAmount
import org.crypticmission.gurpslack.repositories.CharacterRepository
import org.crypticmission.gurpslack.repositories.CharacterSheetRepository
import org.crypticmission.gurpslack.repositories.TrackedAmountRespository
import org.crypticmission.gurpslack.repositories.TrackedStatService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import java.time.Clock

/**
 */
@Controller
class CharacterStatusController(val characterSheetRepository: CharacterSheetRepository,
                                val characterRepository: CharacterRepository,
                                val clock: Clock) {

    private val logger = LoggerFactory.getLogger(CharacterStatusController::class.java)

    @GetMapping("/character/{key}/stats")
    fun getStats(@PathVariable("key") key: String): ModelAndView {
        val char = characterRepository.getByKey(key)
        return ModelAndView("stats.html", mapOf(
                "characterKey" to key,
                "character" to char,
                "lastUpdated" to clock.instant().toString()
        ))
    }
}

@RestController
class CharacterStatusUpdateController(val trackedStatService: TrackedStatService,
                                      val clock: Clock) {
    private val logger = LoggerFactory.getLogger(CharacterStatusUpdateController::class.java)

    @PostMapping("/character/{key}/stats/{stat}/adjustment")
    fun adjustStat(@PathVariable("key") key: String,
                   @PathVariable("stat") statName: String,
                   @RequestParam("adjustment") adjustment: Int): Map<String, Any> {
        logger.debug("adjusting ${key} stat ${statName} from by ${adjustment}");
        val newValue = trackedStatService.modifyByCharacterKeyAndStatName(key, statName, adjustment)
        logger.debug("updated ${key} stat ${statName} by ${adjustment} to ${newValue.value}");
        return mapOf(
                "statName" to statName,
                "value" to newValue.value,
                "lastUpdated" to clock.instant().toString()
                )
    }

    @PutMapping("/character/{key}/stats/{stat}")
    fun updateStat(@PathVariable("key") key: String,
                   @PathVariable("stat") statName: String,
                   @RequestParam("value") value: Int): Map<String, Any> {
        logger.debug("updating ${key} stat ${statName} to ${value}");
        val newValue = trackedStatService.saveOrUpdateByCharacterKeyAndStatName(key, statName, value)
        logger.debug("updated ${key} stat ${statName} to ${value}");
        return mapOf(
                "statName" to statName,
                "value" to newValue.value,
                "lastUpdated" to clock.instant().toString()
                )
    }


}

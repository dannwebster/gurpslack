package org.crypticmission.gurpslack.controllers

import org.crypticmission.gurpslack.repositories.CharacterRepository
import org.crypticmission.gurpslack.repositories.CharacterSheetRepository
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
                                val trackedStatService: TrackedStatService,
                                val clock: Clock) {

    private val logger = LoggerFactory.getLogger(CharacterStatusController::class.java)

    @GetMapping("/character/{key}/stats")
    fun getStats(@PathVariable("key") key: String): ModelAndView {
        val char = characterRepository.getByKey(key)
        val meta = characterSheetRepository.findOneByCharacterKey(key)
        val playerName = meta?.userName ?: "GM"
        return ModelAndView("stats.html", mapOf(
                "key" to key,
                "character" to char,
                "playerName" to playerName,
                "lastUpdated" to clock.instant()
        ))
    }
}

@RestController
class CharacterStatusUpdateController(val trackedStatService: TrackedStatService) {
    private val logger = LoggerFactory.getLogger(CharacterStatusUpdateController::class.java)

    @PostMapping("/character/{key}/stats/{stat}")
    fun updateStat(@PathVariable("key") key: String,
                   @PathVariable("stat") statName: String,
                   @RequestParam("value") value: Int): Pair<String, Int> {
        logger.debug("updating ${key} stat ${statName} to ${value}");
        trackedStatService.saveOrUpdateByCharacterKeyAndStatName(key, statName, value)
        logger.debug("updated ${key} stat ${statName} to ${value}");
        return Pair(statName, value)
    }

}

package org.crypticmission.gurpslack.controllers

import javassist.NotFoundException
import org.crypticmission.gurpslack.entities.TrackedAmount
import org.crypticmission.gurpslack.repositories.CharacterRepository
import org.crypticmission.gurpslack.repositories.TrackedAmountRespository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import java.time.Clock

/**
 */
@RestController
class TrackedAmountRestController(
        val trackedAmountRespository: TrackedAmountRespository,
        val clock: Clock
        ) {

    @PutMapping("/character/{characterKey}/amounts/{abbreviation}")
    fun adjustAmount(@PathVariable("characterKey") characterKey: String,
                     @PathVariable("abbreviation") abbreviation: String,
                     @RequestParam("value") value: Int
    ): Map<String, Any> {
        val trackedAmount = trackedAmountRespository.findOneByCharacterKeyAndAbbreviation(characterKey, abbreviation)?:
            throw NotFoundException("amount ${characterKey}.${abbreviation} not found")
        trackedAmount.currentValue = value
        trackedAmountRespository.save(trackedAmount)
        return mapOf(
                "value" to value,
                "characterKey" to characterKey,
                "lastUpdated" to clock.instant().toString()
        )
    }
}

@Controller
class TrackedAmountController(
        val characterRepository: CharacterRepository,
        val trackedAmountRespository: TrackedAmountRespository,
        val clock: Clock
        ) {

    private val logger = LoggerFactory.getLogger(TrackedAmountController::class.java)

    @GetMapping("/character/{characterKey}/amounts/form")
    fun blankAmountForm(@PathVariable("characterKey") characterKey: String): ModelAndView {
        logger.debug("getting new amount form for ${characterKey}")
        val character = characterRepository.getByKey(characterKey)
        val trackedAmount = TrackedAmount()
        return ModelAndView("trackedAmountForm.html", mapOf(
                "character" to character,
                "trackedAmount" to trackedAmount,
                "characterKey" to characterKey,
                "lastUpdated" to clock.instant().toString()
        ))
    }

    @GetMapping("/character/{characterKey}/amounts/{abbreviation}")
    fun amountPage(@PathVariable("characterKey") characterKey: String,
                   @PathVariable("abbreviation") abbreviation: String): ModelAndView {
        logger.debug("getting amount page for ${characterKey}.${abbreviation}")
        val character = characterRepository.getByKey(characterKey)
        val trackedAmount = trackedAmountRespository.findOneByCharacterKeyAndAbbreviation(characterKey, abbreviation)
        return ModelAndView("trackedAmount.html", mapOf(
                "character" to character,
                "trackedAmount" to trackedAmount
        ))

    }

    @GetMapping("/character/{characterKey}/amounts/{abbreviation}/form")
    fun amountForm(@PathVariable("characterKey") characterKey: String,
                   @PathVariable("abbreviation") abbreviation: String
    ): ModelAndView {
        logger.debug("getting amount form for ${characterKey}.${abbreviation}")
        val character = characterRepository.getByKey(characterKey)
        val trackedAmount = trackedAmountRespository.findOneByCharacterKeyAndAbbreviation(characterKey, abbreviation)
        return ModelAndView("trackedAmountForm.html", mapOf(
                "character" to character,
                "trackedAmount" to trackedAmount,
                "characterKey" to characterKey,
                "lastUpdated" to clock.instant().toString()
        ))
    }

    @PostMapping("/character/{characterKey}/amounts")
    fun createOrUpdateAmount(@PathVariable("characterKey") characterKey: String,
                     @ModelAttribute trackedAmount: TrackedAmount
    ): ModelAndView {
        logger.debug("creating or saving amount for ${trackedAmount.toString()}")
        trackedAmountRespository.save(trackedAmount)
        val url = "redirect:/character/${characterKey}/amounts/${trackedAmount.abbreviation}"
        return ModelAndView(url)
    }

}
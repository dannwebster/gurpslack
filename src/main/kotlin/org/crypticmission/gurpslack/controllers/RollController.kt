package org.crypticmission.gurpslack.controllers

import me.ramswaroop.jbot.core.slack.models.RichMessage
import org.crypticmission.gurpslack.model.*
import org.crypticmission.gurpslack.repositories.CharacterRepository
import org.crypticmission.gurpslack.repositories.Randomizer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController



@RestController
class RollController(val npcRepository: CharacterRepository) {
    companion object {
        private val logger = LoggerFactory.getLogger(RollController::class.java)
    }
    val randomizer: Randomizer = Randomizer.system()

    @Value("\${slashCommandToken}")
    lateinit var slackToken: String

    /**
     * Slash Command handler. When a user types for example "/app help"
     * then slack sends a POST request to this endpoint. So, this endpoint
     * should match the url you set while creating the Slack Slash Command.

     * @param slashData
     * @return
     */
    @PostMapping(value = "/roll")
    fun roll(slashData: SlashData) : RichMessage {
        val spec = when(slashData.text.isBlank()) {
            true -> RollSpec.DEFAULT
            false -> RollSpec.fromString(slashData.text)
        }
        val rollDetail = spec?.roll(randomizer)
        val message = when (rollDetail) {
            null -> RichMessage("${slashData.text} is not a valid rollSpec")
            else -> RichMessage(rollDetail.messageWithEmoji)
        }
        message.responseType = "in_channel"
        return message.encodedMessage()
    }

    @PostMapping(value = "/dmg")
    fun rollDmg(slashData: SlashData) : RichMessage {
        val spec = DamageSpec.fromString(slashData.text)
        val damageRollOutcome = spec?.roll(randomizer)
        val message = when (damageRollOutcome) {
            null -> RichMessage("${slashData.text} is not a valid rollSpec")
            else -> RichMessage(damageRollOutcome.messageWithEmoji)
        }
        message.responseType = "in_channel"
        return message.encodedMessage()

    }

    @PostMapping(value = "/rollattr")
    fun rollattr(slashData: SlashData) : RichMessage {
        val data = slashData.text.split("""\s""")
        val characterAbbrev = data[0]
        val attributeName = data[1]

        val character = npcRepository.get(characterAbbrev)
        if (character != null) {
            val outcome = character.rollVsAttribute(attributeName)
            return RichMessage(outcome.message + outcome.rollOutcome.emoji()).encodedMessage()
        } else {
            return RichMessage("No character with abbreviation ${characterAbbrev}").encodedMessage()
        }
    }
}
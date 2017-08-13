package org.crypticmission.gurpslack.controllers

import me.ramswaroop.jbot.core.slack.models.RichMessage
import org.crypticmission.gurpslack.model.*
import org.crypticmission.gurpslack.repositories.CharacterRepository
import org.crypticmission.gurpslack.repositories.Randomizer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController


fun RichMessage.inChannel(inChannel: Boolean) = when(inChannel) {
    true -> {this.responseType = "in_channel"; this}
    false -> this
}

@RestController
class RollController(val npcRepository: CharacterRepository) {
    companion object {
        private val logger = LoggerFactory.getLogger(RollController::class.java)
    }
    val randomizer: Randomizer = Randomizer.system()

    @Value("\${slashCommandToken}")
    lateinit var slackToken: String

    @PostMapping("/gmroll", "/gm")
    fun gmRoll(slashData: SlashData) : RichMessage {
       return roll(slashData, false)
    }

    @PostMapping(value = "/roll")
    fun roll(slashData: SlashData, inChannel: Boolean = true) : RichMessage {
        val spec = when(slashData.text.isBlank()) {
            true -> RollSpec.DEFAULT
            false -> RollSpec.fromString(slashData.text)
        }
        val rollDetail = spec?.roll(randomizer)
        val message = when (rollDetail) {
            null -> RichMessage("${slashData.text} is not a valid rollSpec")
            else -> RichMessage(rollDetail.messageWithEmoji)
        }
        return message.inChannel(inChannel).encodedMessage()
    }

    @PostMapping("/dmg", "/damage")
    fun rollDmg(slashData: SlashData, inChannel: Boolean = true) : RichMessage {
        val spec = DamageSpec.fromString(slashData.text)
        val damageRollOutcome = spec?.roll(randomizer)
        val message = when (damageRollOutcome) {
            null -> RichMessage("${slashData.text} is not a valid rollSpec")
            else -> RichMessage(damageRollOutcome.messageWithEmoji)
        }
        message.responseType = "in_channel"
        return message.inChannel(inChannel).encodedMessage()

    }

    @PostMapping("/rollvs", "/rollattr")
    fun rollattr(slashData: SlashData, inChannel: Boolean = true) : RichMessage {
        val data = slashData.text.split("""\s+""".toRegex())
        val characterAbbrev = data[0]
        val attributeName = data[1]

        val character = npcRepository.get(characterAbbrev)
        val message = when (character) {
            null -> RichMessage("No character with abbreviation ${characterAbbrev}")
            else -> {
                val outcome = character.rollVsAttribute(attributeName)
                RichMessage(outcome.message)
            }
        }
        return message.inChannel(inChannel).encodedMessage()
    }
}
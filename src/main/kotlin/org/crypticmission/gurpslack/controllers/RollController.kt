package org.crypticmission.gurpslack.controllers

import me.ramswaroop.jbot.core.slack.models.RichMessage
import org.crypticmission.gurpslack.model.RollDetails
import org.crypticmission.gurpslack.model.RollSpec
import org.crypticmission.gurpslack.repositories.CharacterRepository
import org.crypticmission.gurpslack.repositories.Randomizer
import org.crypticmission.gurpslack.toSignedString
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController


fun RollDetails.emoji() = this.rollValues.map{ ":d6-${it}:"} + this.adds.toSignedString()

@RestController
class RollController(val npcRepository: CharacterRepository) {
    companion object {
        private val logger = LoggerFactory.getLogger(RollController::class.java)
    }

    @Value("\${slashCommandToken}")
    lateinit var slackToken: String

    val randomizer = Randomizer.system()

    /**
     * Slash Command handler. When a user types for example "/app help"
     * then slack sends a POST request to this endpoint. So, this endpoint
     * should match the url you set while creating the Slack Slash Command.

     * @param slashData
     * @return
     */
    @PostMapping(value = "/roll")
    fun roll(slashData: SlashData) : RichMessage {
        val spec = RollSpec.spec(slashData.text)
        val roll = spec.roll(randomizer)
        val richMessage = RichMessage("Rolled ${roll.total} on ${spec.canonical} (${roll.emoji()}) ")
        richMessage.responseType = "in_channel"
        return richMessage.encodedMessage()
    }

    @PostMapping(value = "/rollvs")
    fun rollvs(slashData: SlashData) : RichMessage {
        val data = slashData.text.split("""\s""")
        val characterAbbrev = data[0]
        val attributeName = data[1]

        val character = npcRepository.get(characterAbbrev)
        if (character != null) {
            val outcome = character.rollVsAttribute(attributeName)
            return RichMessage(outcome.message + outcome.rollDetails.emoji()).encodedMessage()
        } else {
            return RichMessage("No character with abbreviation ${characterAbbrev}").encodedMessage()
        }
    }
}
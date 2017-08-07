package org.crypticmission.gurpslack.controllers

import me.ramswaroop.jbot.core.slack.models.RichMessage
import org.crypticmission.gurpslack.commands.RollSpec
import org.crypticmission.gurpslack.util.Randomizer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RollController {
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
        // validate token
//        if (slashData.token != slackToken) {
//            return RichMessage("Sorry! You're not lucky enough to use our slack command. token: ${slashData.token}")
//        }

        val spec = RollSpec.spec(slashData.text)
        val roll = spec.roll(randomizer)
        val richMessage = RichMessage("Rolled ${roll} on ${spec.canonical}")
        richMessage.responseType = "in_channel"
        return richMessage.encodedMessage()
    }
}
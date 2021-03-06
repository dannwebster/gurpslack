package org.crypticmission.gurpslack.controllers

import org.crypticmission.gurpslack.slack.RichMessage
import org.crypticmission.gurpslack.message.richMessage
import org.crypticmission.gurpslack.model.*
import org.crypticmission.gurpslack.repositories.Randomizer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController



@RestController
class RollController(val randomizer: Randomizer) {
    companion object {
        private val logger = LoggerFactory.getLogger(RollController::class.java)
    }

    @Value("\${slashCommandToken}")
    lateinit var slackToken: String

    @PostMapping("/gmroll", "/gm")
    fun gmRoll(slashData: SlashData) = doRoll(slashData, false)

    @PostMapping(value = "/roll")
    fun roll(slashData: SlashData)  = doRoll(slashData, true)

    @PostMapping("/gmdmg", "/gmdamage")
    fun gmRollDmg(slashData: SlashData) = doRollDmg(slashData, false)

    @PostMapping("/dmg", "/damage")
    fun rollDmg(slashData: SlashData)  = doRollDmg(slashData, true)

    fun doRoll(slashData: SlashData, inChannel: Boolean) : RichMessage {
        val spec = when(slashData.text.isBlank()) {
            true -> RollSpec.DEFAULT
            false -> parseRollSpec(slashData.text)
        }
        val rollDetail = spec?.roll(randomizer)
        val message = when (rollDetail) {
            null -> RichMessage("${slashData.text} is not a valid rollSpec")
            else -> richMessage(rollDetail)
        }
        return message.inChannel(inChannel).encodedMessage()
    }

    fun doRollDmg(slashData: SlashData, inChannel: Boolean = true) : RichMessage {
        val damage = parseDamage(slashData.text)
        val dr = parseDr(slashData.text)
        val damageRollOutcome = damage?.rollVsDr(randomizer, dr)
        val message = when (damageRollOutcome) {
            null -> RichMessage("${slashData.text} is not a valid rollSpec")
            else -> richMessage(damageRollOutcome)
        }
        return message.inChannel(inChannel).encodedMessage()

    }
}
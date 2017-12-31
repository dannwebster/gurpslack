package org.crypticmission.gurpslack.controllers

import org.crypticmission.gurpslack.slack.CharacterSections
import org.crypticmission.gurpslack.slack.CharacterSections.*
import org.crypticmission.gurpslack.slack.RichMessage
import org.crypticmission.gurpslack.repositories.CharacterRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PcController(
        @Value("\${web.app.hostname}") val urlHostname: String,
        val characterRepository: CharacterRepository) {
    companion object {
        private val logger = LoggerFactory.getLogger(PcController::class.java)
    }

    @Value("\${slashCommandToken}")
    lateinit var slackToken: String

    @PostMapping("/pc")
    fun getPc(slashData: SlashData) =
            doGetPc(slashData, values())

    @PostMapping("/attributes")
    fun getPcAttributes(slashData: SlashData) =
            doGetPc(slashData, arrayOf(PRIMARY_ATTRIBUTES, DERIVED_ATTRIBUTES, TRACKED_STATS))

    @PostMapping("/skills")
    fun getPcSkills(slashData: SlashData) =
            doGetPc(slashData, arrayOf(SKILLS))

    @PostMapping("/attacks")
    fun getPcDamage(slashData: SlashData) =
            doGetPc(slashData, arrayOf(MELEE_ATTACKS, RANGED_ATTACKS))

    fun doGetPc(slashData: SlashData, sections: Array<CharacterSections>): RichMessage {
        logger.debug("doGetPC: ${slashData}")
        val username = slashData.user_name.trim()
        logger.debug("getting PC for username ${username}")
        val keyPcPair = characterRepository.getByUserName(username)
        logger.debug("pc for username ${username} is ${keyPcPair?.second?.characterName} for key ${keyPcPair?.first}")
        return when (keyPcPair) {
            null -> RichMessage("CharacterRoller with username '${username}' does not exist")
            else -> org.crypticmission.gurpslack.message.richMessage(urlHostname, keyPcPair.first, keyPcPair.second, sections)
        }.encodedMessage()
    }

}
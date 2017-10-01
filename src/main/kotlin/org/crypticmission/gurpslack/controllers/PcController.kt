package org.crypticmission.gurpslack.controllers

import me.ramswaroop.jbot.core.slack.models.RichMessage
import org.crypticmission.gurpslack.model.*
import org.crypticmission.gurpslack.repositories.CharacterRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PcController(val npcRepository: CharacterRepository) {
    companion object {
        private val logger = LoggerFactory.getLogger(PcController::class.java)
    }

    @Value("\${slashCommandToken}")
    lateinit var slackToken: String

    @PostMapping("/pc")
    fun getPc(slashData: SlashData) =
            doGetPc(slashData, CharacterSections.values())

    @PostMapping("/attributes")
    fun getPcAttributes(slashData: SlashData) =
            doGetPc(slashData, arrayOf(CharacterSections.PRIMARY_ATTRIBUTES, CharacterSections.DERIVED_ATTRIBUTES))

    @PostMapping("/skills")
    fun getPcSkills(slashData: SlashData) =
            doGetPc(slashData, arrayOf(CharacterSections.SKILLS))

    @PostMapping("/attacks")
    fun getPcDamage(slashData: SlashData) =
            doGetPc(slashData, arrayOf(CharacterSections.MELEE_ATTACKS, CharacterSections.RANGED_ATTACKS))

    fun doGetPc(slashData: SlashData, sections: Array<CharacterSections>): RichMessage {
        val username = slashData.userName.trim()
        val npc = npcRepository.getByUserName(username)
        return when (npc) {
            null -> RichMessage("CharacterRoller with username ${username} does not exist")
            else -> richMessage(username, npc, sections)
        }.encodedMessage()
    }

}
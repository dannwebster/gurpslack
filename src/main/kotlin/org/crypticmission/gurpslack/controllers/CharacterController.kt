package org.crypticmission.gurpslack.controllers

import me.ramswaroop.jbot.core.slack.models.RichMessage
import org.crypticmission.gurpslack.model.*
import org.crypticmission.gurpslack.repositories.CharacterRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CharacterController(val npcRepository: CharacterRepository) {
    companion object {
        private val logger = LoggerFactory.getLogger(CharacterController::class.java)
    }

    @Value("\${slashCommandToken}")
    lateinit var slackToken: String

    @PostMapping(value = "/delnpc")
    fun delNpc(slashData: SlashData): RichMessage {
        val characterAbbrev = slashData.text

        val removed = npcRepository.remove(characterAbbrev)
        return when (removed) {
            null -> RichMessage("No character with abbreviation ${characterAbbrev} exists, so none removed")
            else -> RichMessage("Removed Character ${removed.characterName} with abbreviation ${characterAbbrev}")
        }.encodedMessage()
    }

    @PostMapping("/npc", "/getnpc")
    fun getNpc(slashData: SlashData): RichMessage {
        val abbrev = slashData.text.trim()
        val npc = npcRepository.get(abbrev)
        return when (npc) {
            null -> RichMessage("Character with abbreviation ${abbrev} does not exist")
            else -> richMessage(npc)
        }.encodedMessage()
    }

    @PostMapping("/npcs", "/listnpcs")
    fun listNpcs(slashData: SlashData): RichMessage {
        return RichMessage(npcRepository
                .list()
                .joinToString("\n") { (key, character) -> "${key} => ${character.characterName}" })
                .encodedMessage()
    }

    @PostMapping(value = "/addnpc")
    fun addNpc(slashData: SlashData): RichMessage {
        val (characterAbbrev, characterName) = parseName(slashData.text) ?:
            return RichMessage("Can't add character from data '${slashData.text}'")

        return when (npcRepository.add(characterAbbrev, characterName)) {
            true -> RichMessage("Created Character ${characterName} with abbreviation ${characterAbbrev}")
            false -> RichMessage("Character with abbreviation ${characterAbbrev} already exists")
        }.encodedMessage()
    }

    @PostMapping(value = "/addattack")
    fun addDmg(slashData: SlashData): RichMessage {
        val attackData = parseAttack(slashData.text) ?:
                return RichMessage("Can't add an attack from data '${slashData.text}'")
        val key = attackData.first
        val character = npcRepository.get(key)
        return when (character) {
            null -> RichMessage("No character with abbreviation ${key}")
            else -> {
                val attack = attackData.second
                character.addAttack(attack)
                return RichMessage("Created Attack ${attack.attackName} for character ${character.characterName}")
            }
        }.encodedMessage()
    }

    @PostMapping(value = "/addskill")
    fun addSkill(slashData: SlashData): RichMessage {
        return doAdd("skill", slashData) { character, skill -> character.addSkill(skill) }
    }

    @PostMapping(value = "/addattr")
    fun addAttr(slashData: SlashData): RichMessage {
        return doAdd("attribute", slashData) { character, attribute -> character.addAttribute(attribute) }
    }

    fun doAdd(type: String, slashData: SlashData, addFunc: (character: Character, attribute: Attribute) -> Unit): RichMessage {
        val attributeData = parseAttribute(slashData.text) ?:
                return RichMessage("Can't add ${type} from data '${slashData.text}'")
        val key = attributeData.first
        val character = npcRepository.get(key)
        return when (character) {
            null -> RichMessage("No character with abbreviation ${key}")
            else -> {
                val attribute = attributeData.second
                addFunc(character, attribute)
                return RichMessage("Created ${type} ${attribute.name} for character ${character.characterName}")
            }
        }.encodedMessage()
    }
}


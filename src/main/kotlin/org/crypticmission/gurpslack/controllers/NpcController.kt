package org.crypticmission.gurpslack.controllers

import me.ramswaroop.jbot.core.slack.models.RichMessage
import org.crypticmission.gurpslack.model.*
import org.crypticmission.gurpslack.model.CharacterSections.*
import org.crypticmission.gurpslack.repositories.CharacterRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class NpcController(val characterRepository: CharacterRepository) {
    companion object {
        private val logger = LoggerFactory.getLogger(NpcController::class.java)
    }

    @Value("\${slashCommandToken}")
    lateinit var slackToken: String

    @PostMapping("/delnpc")
    fun delNpc(slashData: SlashData): RichMessage {
        val characterAbbrev = slashData.text

        val removed = characterRepository.removeByKey(characterAbbrev)
        return when (removed) {
            null -> RichMessage("No character with abbreviation ${characterAbbrev} exists, so none removed")
            else -> RichMessage("Removed CharacterRoller ${removed.characterName} with abbreviation ${characterAbbrev}")
        }.encodedMessage()
    }

    @PostMapping("/npc")
    fun getNpcAttributes(slashData: SlashData) =
            doGetNpc(slashData, arrayOf(PRIMARY_ATTRIBUTES, DERIVED_ATTRIBUTES))

    @PostMapping("/npcs")
    fun getNpcSkills(slashData: SlashData) =
            doGetNpc(slashData, arrayOf(SKILLS))

    @PostMapping("/npcd")
    fun getNpcDamage(slashData: SlashData) =
            doGetNpc(slashData, arrayOf(MELEE_ATTACKS, RANGED_ATTACKS))

    fun doGetNpc(slashData: SlashData, sections: Array<CharacterSections>): RichMessage {
        val key = slashData.text.trim()
        val npc = characterRepository.getByKey(key)
        return when (npc) {
            null -> RichMessage("CharacterRoller with abbreviation ${key} does not exist")
            else -> richMessage(key, npc, sections)
        }.encodedMessage()
    }

    @PostMapping("/npclist")
    fun listNpcs(slashData: SlashData): RichMessage {
        return RichMessage(characterRepository
                .listByKey()
                .joinToString("\n") { (key, character) -> "${key} => ${character.characterName}" })
                .encodedMessage()
    }

    @PostMapping("/addnpc")
    fun addNpc(slashData: SlashData): RichMessage {
        val (characterAbbrev, characterName) = parseName(slashData.text) ?:
            return RichMessage("Can't add character from data '${slashData.text}'")

        return when (characterRepository.add(characterAbbrev, null, characterName)) {
            true -> RichMessage("Created CharacterRoller ${characterName} with abbreviation ${characterAbbrev}")
            false -> RichMessage("CharacterRoller with abbreviation ${characterAbbrev} already exists")
        }.encodedMessage()
    }

    @PostMapping("/addmelee")
    fun addMeleeAttack(slashData: SlashData): RichMessage {
        val attackData = parseAttack(slashData.text) ?:
                return RichMessage("Can't add an ranged attack from data '${slashData.text}'")
        val key = attackData.first
        val character = characterRepository.getByKey(key)
        return when (character) {
            null -> RichMessage("No character with abbreviation ${key}")
            else -> {
                val attack = attackData.second
                character.addMeleeAttack(attack)
                return RichMessage("Created Melee Attack ${attack.attackName} for character ${character.characterName}")
            }
        }.encodedMessage()
    }

    @PostMapping(value = "/addranged")
    fun addRangedAttack(slashData: SlashData): RichMessage {
        val attackData = parseAttack(slashData.text) ?:
                return RichMessage("Can't add an melee attack from data '${slashData.text}'")
        val key = attackData.first
        val character = characterRepository.getByKey(key)
        return when (character) {
            null -> RichMessage("No character with abbreviation ${key}")
            else -> {
                val attack = attackData.second
                character.addRangedAttack(attack)
                return RichMessage("Created Ranged Attack ${attack.attackName} for character ${character.characterName}")
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

    fun doAdd(type: String, slashData: SlashData, addFunc: (characterRoller: CharacterRoller, attribute: Attribute) -> Unit): RichMessage {
        val attributeData = parseAttribute(slashData.text) ?:
                return RichMessage("Can't add ${type} from data '${slashData.text}'")
        val key = attributeData.first
        val character = characterRepository.getByKey(key)
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


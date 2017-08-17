package org.crypticmission.gurpslack.controllers

import me.ramswaroop.jbot.core.slack.models.RichMessage
import org.crypticmission.gurpslack.model.Attribute
import org.crypticmission.gurpslack.repositories.CharacterRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

fun parseName(nameLine: String) : Pair<String, String>? {
    val parts = nameLine.split("""\s+""".toRegex())
    return when (parts.size) {
        0 -> null
        1 -> Pair(parts[0], parts[0])
        else -> Pair(parts[0], parts.drop(1).joinToString (" "))
    }
}

fun parseAttribute(attributeLine: String) : Triple<String, String, Int>? {
    val parts = attributeLine.split("""[\s:]+""".toRegex())
    return when (parts.size) {
        2 -> Triple(parts[0], parts[1], 10)
        3 -> Triple(parts[0], parts[1], parts[3].toInt())
        else -> null
    }
}

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
        val message = when (removed) {
            null -> RichMessage("No character with abbreviation ${characterAbbrev} exists, so none removed")
            else -> RichMessage("Removed Character ${removed.characterName} with abbreviation ${characterAbbrev}")
        }
        return message.encodedMessage()
    }

    @PostMapping("/npc", "/getnpc")
    fun getNpc(slashData: SlashData): RichMessage {

        val abbrev = slashData.text.trim()
        val npc = npcRepository.get(abbrev)
        val message =  when (npc) {
            null -> RichMessage("Character with abbreviation ${abbrev} does not exist")
            else -> RichMessage(npc.message())
        }
        return message.encodedMessage()
    }

    @PostMapping("/npcs", "/listnpcs")
    fun listNpcs(slashData: SlashData): RichMessage {
        val message = RichMessage(npcRepository
                .list()
                .joinToString("\n") { (key, character) -> "${key} => ${character.characterName}" })
        return message.encodedMessage()
    }

    @PostMapping(value = "/addnpc")
    fun addNpc(slashData: SlashData): RichMessage {
        val (characterAbbrev, characterName) = parseName(slashData.text) ?:
            return RichMessage("Can't add character from data '${slashData.text}'")
        val message =  when (npcRepository.add(characterAbbrev, characterName)) {
            true -> RichMessage("Created Character ${characterName} with abbreviation ${characterAbbrev}")
            false -> RichMessage("Character with abbreviation ${characterAbbrev} already exists")
        }
        return message.encodedMessage()
    }

    @PostMapping(value = "/addattr")
    fun attr(slashData: SlashData): RichMessage {
        val attributeData = parseAttribute(slashData.text) ?:
                return RichMessage("Can't add attribute from data '${slashData.text}'")
        val key = attributeData.first
        val character = npcRepository.get(key)
        return when (character) {
            null -> RichMessage("No character with abbreviation ${key}")
            else -> {
                val attribute = Attribute(attributeData.second, attributeData.third)
                character.addAttribute(attribute)
                return RichMessage("Created Attribute ${attribute} for character ${character.characterName}")
            }
        }
    }
}


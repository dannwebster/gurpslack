package org.crypticmission.gurpslack.controllers

import me.ramswaroop.jbot.core.slack.models.RichMessage
import org.crypticmission.gurpslack.model.Attribute
import org.crypticmission.gurpslack.repositories.CharacterRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

//val NPC_REGEX = """\s*((\w+)\s+)(\w+)\s*""".toRegex()
fun parseName(nameLine: String) : Pair<String, String> {
    val parts = nameLine.split("""\s+""".toRegex())
    val abbrev = parts.first()
    val nameParts = parts.drop(1)
    val name = when (nameParts.isEmpty()) {
        true -> abbrev
        false -> nameParts.joinToString(" ")
    }
    return Pair(abbrev, name)
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
        if (removed != null) {
            return RichMessage("Removed Character ${removed.characterName} with abbreviation ${characterAbbrev}")
        } else {
            return RichMessage("No character with abbreviation ${characterAbbrev} exists, so none removed")
        }
    }


    @PostMapping(value = "/addnpc")
    fun addNpc(slashData: SlashData): RichMessage {

        val (characterName, characterAbbrev) = parseName(slashData.text)

        val message =  when (npcRepository.add(characterName, characterAbbrev)) {
            true -> RichMessage("Created Character ${characterName} with abbreviation ${characterAbbrev}")
            false -> RichMessage("Character with abbreviation ${characterAbbrev} already exists")
        }
        message.responseType = "in_channel"
        return message.encodedMessage()
    }

    @PostMapping(value = "/addattr")
    fun attr(slashData: SlashData): RichMessage {
        val data = slashData.text.split("""\s""")
        val characterAbbrev = data[0]
        val attributeName = data[1]
        val value = data[2].toInt()
        val character = npcRepository.get(characterAbbrev)
        if (character != null) {
            val attribute = Attribute(attributeName, value)
            character.addAttribute(attribute)
            return RichMessage("Created Attribute ${attribute} for character ${character.characterName}")
        } else {
            return RichMessage("No character with abbreviation ${characterAbbrev}")
        }
    }
}


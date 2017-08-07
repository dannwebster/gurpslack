package org.crypticmission.gurpslack.controllers

import me.ramswaroop.jbot.core.slack.models.RichMessage
import org.crypticmission.gurpslack.model.Attribute
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
        if (removed != null) {
            return RichMessage("Removed Character ${removed.characterName} with abbreviation ${characterAbbrev}")
        } else {
            return RichMessage("No character with abbreviation ${characterAbbrev} exists, so none removed")
        }
    }

    @PostMapping(value = "/addnpc")
    fun addNpc(slashData: SlashData): RichMessage {
        val data = slashData.text.split("""\s""")
        val characterName = data[0]
        val characterAbbrev = data[1]

        if (npcRepository.add(characterName, characterAbbrev)) {
            return RichMessage("Created Character ${characterName} with abbreviation ${characterAbbrev}")
        } else {
            return RichMessage("Character with abbreviation ${characterAbbrev} already exists")
        }
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


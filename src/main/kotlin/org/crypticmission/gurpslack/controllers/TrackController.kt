package org.crypticmission.gurpslack.controllers

import org.crypticmission.gurpslack.message.RichMessage
import org.crypticmission.gurpslack.message.richMessage
import org.crypticmission.gurpslack.model.CharacterRoller
import org.crypticmission.gurpslack.repositories.CharacterRepository
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

/**
 */
@RestController
class TrackController(val characterRepository: CharacterRepository) {
    private val logger = LoggerFactory.getLogger(TrackController::class.java)

    @PostMapping("/npctrack")
    fun getNpcValueTrack(slashData: SlashData) : RichMessage {
        val key = slashData.text.substringBefore(" ").trim()
        val trait = slashData.text.substringAfter(" ").trim()
        val char = characterRepository.getByKey(key)

        return if (char == null)
            RichMessage("could not find character for npc key ${key}")
        else
            trackValue(char, key, trait)
    }

    @PostMapping("/track")
    fun getPcValueTrack(slashData: SlashData) : RichMessage {
        val trait = slashData.text.trim()
        val pair = characterRepository.getByUserName(slashData.user_name)

        return if (pair != null) {
            val (key, char) = pair
            trackValue(char, key, trait)
        } else {
            RichMessage("could not find character for user ${slashData.user_name}")
        }
    }

    fun trackValue(characterRoller: CharacterRoller, key: String, trait: String) : RichMessage {
        logger.debug("${characterRoller.characterName} : ${key} = ${characterRoller.trackedValues}")
        val trackedValue = characterRoller.trackedValues.get(trait)
        return if (trackedValue != null) {
            richMessage(key, trackedValue)
        } else {
            RichMessage("could not find trait ${trait} for character ${characterRoller.characterName}")
        }
    }
}
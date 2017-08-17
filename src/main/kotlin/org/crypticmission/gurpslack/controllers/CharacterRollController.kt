package org.crypticmission.gurpslack.controllers

import me.ramswaroop.jbot.core.slack.models.RichMessage
import org.crypticmission.gurpslack.model.Character
import org.crypticmission.gurpslack.model.CharacterAttributeRollOutcome
import org.crypticmission.gurpslack.model.richMessage
import org.crypticmission.gurpslack.repositories.CharacterRepository
import org.crypticmission.gurpslack.repositories.Randomizer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

/**
 */

@RestController
class CharacterRollController(val npcRepository: CharacterRepository) {
    companion object {
        private val logger = LoggerFactory.getLogger(RollController::class.java)
    }

    val randomizer: Randomizer = Randomizer.system()

    @Value("\${slashCommandToken}")
    lateinit var slackToken: String

    @PostMapping("/gm-skill", "/gm-rollskill")
    fun gmRollSkill(slashData: SlashData, inChannel: Boolean = false) =
            rollVs("skill", slashData, inChannel) { character, name, mod -> character.rollVsSkill(name, mod)}

    @PostMapping("/skill", "/rollskill")
    fun rollSkill(slashData: SlashData, inChannel: Boolean = true) =
            rollVs("skill", slashData, inChannel) { character, name, mod -> character.rollVsSkill(name, mod)}

    @PostMapping("/gm-attr", "/gm-rollattr")
    fun gmRollAttr(slashData: SlashData, inChannel: Boolean = false) =
            rollVs("attribute", slashData, inChannel) { character, name, mod -> character.rollVsAttribute(name, mod)}

    @PostMapping("/attr", "/rollattr")
    fun rollAttr(slashData: SlashData, inChannel: Boolean = true) =
            rollVs("attribute", slashData, inChannel) { character, name, mod -> character.rollVsAttribute(name, mod)}

    @PostMapping("/gm-attack", "/gm-rollattack")
    fun gmRollAttack(slashData: SlashData, inChannel: Boolean = false) : RichMessage =
        rollAttack(slashData, inChannel)

    @PostMapping("/attack", "/rollattack")
    fun rollAttack(slashData: SlashData, inChannel: Boolean = true) : RichMessage {
        val data = slashData.text.tokenize()
        val key = data[0]
        val attackName = data[1]
        val dr = parseDr(slashData.text)

        val character = npcRepository.get(key)
        return when (character) {
            null -> RichMessage("No character with abbreviation ${key}").inChannel(false).encodedMessage()
            else -> richMessage(character.rollAttackDamage(attackName, dr)).inChannel(inChannel).encodedMessage()
        }
    }

    fun rollVs(type: String, slashData: SlashData, inChannel: Boolean,
               doRoll: (character: Character, name: String, modifier: Int) -> CharacterAttributeRollOutcome) : RichMessage {
        val vsData = parseVsData(slashData.text)
        return when (vsData) {
            null -> RichMessage("Could not roll vs a ${type} from data '${slashData.text}'").inChannel(false).encodedMessage()
            else -> {
                val (characterKey, attributeName, modifier) = vsData
                val character = npcRepository.get(characterKey)
                when (character) {
                    null -> RichMessage("No character with abbreviation ${characterKey}")
                    else -> richMessage(doRoll(character, attributeName, modifier))
                }.inChannel(inChannel).encodedMessage()
            }
        }
    }
}
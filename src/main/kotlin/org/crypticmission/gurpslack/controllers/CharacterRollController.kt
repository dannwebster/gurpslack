package org.crypticmission.gurpslack.controllers

import me.ramswaroop.jbot.core.slack.models.RichMessage
import org.crypticmission.gurpslack.model.*
import org.crypticmission.gurpslack.repositories.CharacterRepository
import org.crypticmission.gurpslack.repositories.Randomizer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

/**
 */

@RestController
class CharacterRollController(val randomizer: Randomizer, val npcRepository: CharacterRepository) {
    companion object {
        private val logger = LoggerFactory.getLogger(RollController::class.java)
    }

    @Value("\${slashCommandToken}")
    lateinit var slackToken: String

    @PostMapping("/gmskill", "/gmrollskill")
    fun gmRollSkill(slashData: SlashData) =
            rollVs("skill", slashData, false) { character, name, mod -> character.rollVsSkill(name, mod)}

    @PostMapping("/skill", "/rollskill")
    fun rollSkill(slashData: SlashData) =
            rollVs("skill", slashData, true) { character, name, mod -> character.rollVsSkill(name, mod)}

    @PostMapping("/gmattr", "/gmrollattr")
    fun gmRollAttr(slashData: SlashData, inChannel: Boolean = false) =
            rollVs("attribute", slashData, inChannel) { character, name, mod -> character.rollVsAttribute(name, mod)}

    @PostMapping("/attr", "/rollattr")
    fun rollAttr(slashData: SlashData) =
            rollVs("attribute", slashData, true) { character, name, mod -> character.rollVsAttribute(name, mod)}

    @PostMapping("/gmmeleeattack")
    fun gmRollMeleeAttack(slashData: SlashData) = doRollAttack(slashData, false, false)

    @PostMapping("/gmrangedattack")
    fun gmRollRangedAttack(slashData: SlashData) = doRollAttack(slashData, true,false)

    @PostMapping("/meleeattack")
    fun rollMeleeAttack(slashData: SlashData) = doRollAttack(slashData, false,true)

    @PostMapping("/rangedeattack")
    fun rollRangedAttack(slashData: SlashData) = doRollAttack(slashData, true,true)

    fun doRollAttack(slashData: SlashData, isRanged: Boolean, inChannel: Boolean = true) : RichMessage {
        val data = slashData.text.tokenize()
        val key = data[0]
        val attackName = data[1]
        val dr = parseDr(slashData.text)

        val character = npcRepository.get(key)
        return when (character) {
            null -> RichMessage("No character with abbreviation ${key}").inChannel(false).encodedMessage()
            else -> {
                val outcome = if (isRanged)
                    character.rollRangedAttackDamage(attackName, dr)
                else
                    character.rollMeleeAttackDamage(attackName, dr)
                when (outcome) {
                    null -> RichMessage("Cannot find attack ${attackName} for '${character.characterName}'")
                    else -> richMessage(outcome).inChannel(inChannel).encodedMessage()
                }
            }
        }
    }

    fun rollVs(type: String, slashData: SlashData, inChannel: Boolean,
               doRoll: (characterRoller: CharacterRoller, name: String, modifier: Int) -> CharacterAttributeRollOutcome?) : RichMessage {
        val vsData = parseVsData(slashData.text)
        return when (vsData) {
            null -> RichMessage("Could not roll vs a ${type} from data '${slashData.text}'").inChannel(false).encodedMessage()
            else -> {
                val (characterKey, attributeName, modifier) = vsData
                val character = npcRepository.get(characterKey)
                when (character) {
                    null -> RichMessage("No character with abbreviation ${characterKey}")
                    else -> {
                        val outcome = doRoll(character, attributeName, modifier)
                        when (outcome) {
                            null -> RichMessage("Cannot find ${type} ${attributeName} for '${character.characterName}'")
                            else -> richMessage(outcome)
                        }
                    }
                }.inChannel(inChannel).encodedMessage()
            }
        }
    }
}
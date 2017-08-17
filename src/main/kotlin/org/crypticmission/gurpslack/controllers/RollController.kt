package org.crypticmission.gurpslack.controllers

import me.ramswaroop.jbot.core.slack.models.RichMessage
import org.crypticmission.gurpslack.model.*
import org.crypticmission.gurpslack.repositories.CharacterRepository
import org.crypticmission.gurpslack.repositories.Randomizer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController


fun RichMessage.inChannel(inChannel: Boolean) = when(inChannel) {
    true -> {this.responseType = "in_channel"; this}
    false -> this
}

@RestController
class RollController(val npcRepository: CharacterRepository) {
    companion object {
        private val logger = LoggerFactory.getLogger(RollController::class.java)
    }
    val randomizer: Randomizer = Randomizer.system()

    @Value("\${slashCommandToken}")
    lateinit var slackToken: String

    @PostMapping("/gmroll", "/gm")
    fun gmRoll(slashData: SlashData) : RichMessage {
       return roll(slashData, false)
    }

    @PostMapping(value = "/roll")
    fun roll(slashData: SlashData, inChannel: Boolean = true) : RichMessage {
        val spec = when(slashData.text.isBlank()) {
            true -> RollSpec.DEFAULT
            false -> parseRollSpec(slashData.text)
        }
        val rollDetail = spec?.roll(randomizer)
        val message = when (rollDetail) {
            null -> RichMessage("${slashData.text} is not a valid rollSpec")
            else -> richMessage(rollDetail)
        }
        return message.inChannel(inChannel).encodedMessage()
    }

    @PostMapping("/dmg", "/damage")
    fun rollDmg(slashData: SlashData, inChannel: Boolean = true) : RichMessage {
        val damage = parseDamage(slashData.text)
        val dr = parseDr(slashData.text)
        val damageRollOutcome = damage?.rollVsDr(dr, randomizer)
        val message = when (damageRollOutcome) {
            null -> RichMessage("${slashData.text} is not a valid rollSpec")
            else -> richMessage(damageRollOutcome)
        }
        return message.inChannel(inChannel).encodedMessage()

    }

    @PostMapping("/skill", "/rollskill")
    fun rollSkill(slashData: SlashData, inChannel: Boolean = true) =
        rollVs("skill", slashData, inChannel) { character, name, mod -> character.rollVsSkill(name, mod)}

    @PostMapping("/rollvs", "/rollattr")
    fun rollAttr(slashData: SlashData, inChannel: Boolean = true) =
        rollVs("attribute", slashData, inChannel) { character, name, mod -> character.rollVsAttribute(name, mod)}

    fun rollVs(type: String, slashData: SlashData, inChannel: Boolean,
               doRoll: (character: Character, name: String, modifier: Int) -> CharacterAttributeRollOutcome) : RichMessage {
        val vsData = parseVsData(slashData.text)
        return when (vsData) {
            null -> RichMessage("Could not roll vs a ${type} from data '${slashData.text}'")
            else -> {
                val (characterKey, attributeName, modifier) = vsData
                val character = npcRepository.get(characterKey)
                when (character) {
                    null -> RichMessage("No character with abbreviation ${characterKey}")
                    else -> richMessage(doRoll(character, attributeName, modifier))
                }
            }
        }
    }


    @PostMapping("/rollattack", "/attack")
    fun rollAttack(slashData: SlashData, inChannel: Boolean = true) : RichMessage {
        val data = slashData.text.tokenize()
        val key = data[0]
        val attackName = data[1]
        val dr = parseDr(slashData.text)

        val character = npcRepository.get(key)
        val message = when (character) {
            null -> RichMessage("No character with abbreviation ${key}")
            else -> {
                val outcome = character.rollAttackDamage(attackName, dr)
                richMessage(outcome)
            }
        }
        return message.inChannel(inChannel).encodedMessage()
    }
}
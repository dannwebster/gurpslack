package org.crypticmission.gurpslack.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import me.ramswaroop.jbot.core.slack.models.RichMessage
import org.crypticmission.gurpslack.message.*
import org.crypticmission.gurpslack.model.*
import org.crypticmission.gurpslack.repositories.CharacterRepository
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class InteractiveMessageController(val characterRepository: CharacterRepository) {
    private val logger = LoggerFactory.getLogger(InteractiveMessageController::class.java)

    val shotsFiredCache = ValueCache<Int>(1)
    val marginOfSuccessCache = ValueCache<Int>(0)
    val modifierCache = ValueCache<Int>(0)
    val damageResistanceCache = ValueCache<Int>(0)
    val visibilityCache = ValueCache<Boolean>(true)

    val objectMapper = ObjectMapper()

    @PostMapping(path = arrayOf("/buttons"), consumes = arrayOf(APPLICATION_FORM_URLENCODED_VALUE))
    fun handleButtons(@RequestParam("payload") buttonJson: String): RichMessage {
        logger.info(buttonJson)
        val messageData = objectMapper.readValue(buttonJson, MessageData::class.java)

        val action = messageData.actions.first()

        val message = "Pressed button ${action.name} and got value ${action.value} "
        logger.info(message)

        val richMessage = when (action.type) {
            "button" -> doButtonMessage(action, message, messageData)
            "select" -> when(action.name) {
                "shots-fired" -> doShotsFired(action, messageData, shotsFiredCache, marginOfSuccessCache)
                "success-margin" -> doMarginOfSuccess(action, messageData, shotsFiredCache, marginOfSuccessCache)
                "modifier" -> doModifier(action, message, messageData, modifierCache)
                "visibility" -> doVisibility(action, message, messageData, visibilityCache)
                "dr" -> doDamageResistance(action, message, messageData, damageResistanceCache)
                else -> RichMessage("unable to find action '${action.type}'")
            }
            else -> RichMessage("action type must be 'button' or 'select', but is '${action.type}'")
        }

        val inChannel = visibilityCache.getValue(messageData)
        return richMessage
                .withCallback(messageData.callbackId)
                .replaceOriginal(false)
                .inChannel(inChannel)
                .encodedMessage()
    }

    private fun shotsFiredMessage(messageData: MessageData, shotsFired: Int, marginOfSuccess: Int): RichMessage {
        return RichMessage(
"""
Next attack made by ${messageData.user.name}:
> *- Shots Fired:* ${shotsFired}
> *- Margin of Success:* ${marginOfSuccess}
""".trimMargin()
        )
    }

    private fun doShotsFired(action: ActionTaken, messageData: MessageData, shotsFiredCache: ValueCache<Int>, marginOfSuccessCache: ValueCache<Int>): RichMessage {
        val shotsFired = action.selectedValue()?.toInt() ?: 0
        shotsFiredCache.putValue(messageData, shotsFired)
        val marginOfSuccess = marginOfSuccessCache.getValue(messageData)
        return shotsFiredMessage(messageData, shotsFired, marginOfSuccess)
    }

    private fun doMarginOfSuccess(action: ActionTaken, messageData: MessageData, shotsFiredCache: ValueCache<Int>, marginOfSuccessCache: ValueCache<Int>): RichMessage {
        val marginOfSuccess = action.selectedValue()?.toInt() ?: 0
        marginOfSuccessCache.putValue(messageData, marginOfSuccess)
        val shotsFired = shotsFiredCache.getValue(messageData)
        return shotsFiredMessage(messageData, shotsFired, marginOfSuccess)
    }

    private fun doModifier(action: ActionTaken, message: String, messageData: MessageData, modifierCache: ValueCache<Int>): RichMessage {
        val modifier = action.selectedValue()?.toInt() ?: 0
        modifierCache.putValue(messageData, modifier)
        return RichMessage("modifying next roll by ${modifier.toSignedStringWithZero()} for next skill or attribute roll " +
                "made by ${messageData.user.name} ")
    }

    private fun doDamageResistance(action: ActionTaken, message: String, messageData: MessageData, damageResistanceCache: ValueCache<Int>): RichMessage {
        val damageResistance = action.selectedValue()?.toInt() ?: 0
        damageResistanceCache.putValue(messageData, damageResistance)
        return RichMessage("applying DR ${damageResistance.toSignedStringWithZero()} to next damage roll " +
                "made by ${messageData.user.name}")
    }

    private fun doVisibility(action: ActionTaken, message: String, messageData: MessageData, visibilityCache: ValueCache<Boolean>): RichMessage  {
        val visibility = VisibilityOption.fromValue(action.selectedValue())
        visibilityCache.putValue(messageData, visibility.isInChannel)
        return RichMessage("next roll will be visible to ${visibility.menuOption.text}")
    }

    private fun doButtonMessage(action: ActionTaken, message: String, messageData: MessageData): RichMessage {
        val (characterKey, traitName) = action.value?.split("@") ?: throw IllegalArgumentException("value must be set")

        val key = characterKey.toKey()
        val traitKey = traitName.toKey()
        val richMessage: RichMessage = when (action.name) {

            "skill" -> skill(key, traitKey, modifierCache.getAndClearValue(messageData))
            "attribute" -> attribute(key, traitKey, modifierCache.getAndClearValue(messageData))

            "showTrackedStat" -> showTrackedStat(key, traitKey)
            "incTrackedStat" -> changeTrackedStat(key, traitKey, 1)
            "decTrackedStat" -> changeTrackedStat(key, traitKey, -1)

            "meleeAttack" -> meleeAttack(key, traitKey, damageResistanceCache.getAndClearValue(messageData))
            "rangedAttack" -> rangedAttack(
                    key,
                    traitKey,
                    damageResistanceCache.getAndClearValue(messageData),
                    shotsFiredCache.getAndClearValue(messageData),
                    marginOfSuccessCache.getAndClearValue(messageData)
            )

            else -> ReplaceOriginalRichMessage("unable to find action from message ${message}", messageData.callbackId, false)
        }

        logger.info("outcome ${richMessage.text}")

        return richMessage
    }


    fun showTrackedStat(key: String, traitName: String) : RichMessage = changeTrackedStat(key, traitName, 0)

    fun changeTrackedStat(key: String, traitName: String, change: Int) : RichMessage {
        logger.debug("Changing stat ${traitName} for ${key} by ${change}")
        val character = characterRepository.getByKey(key) ?: return RichMessage("could not find character with key ${key}")
        val stat = character.modifyTrackedStat(traitName, change) ?: return RichMessage("could not find tracked stat ${traitName} for character ${character.characterName}")
        return richMessage(key, stat).replaceOriginal(true)
    }

    fun skill(key: String, traitName: String, modifier: Int) = roll(
            "skill",
            { cr:CharacterRoller -> cr.rollVsSkill(traitName, modifier)?.let{ richMessage(it) }},
            key,
            traitName)

    fun attribute(key: String, traitName: String, modifier: Int) = roll(
            "attribute",
            { cr:CharacterRoller -> cr.rollVsAttribute(traitName, modifier)?.let{ richMessage(it) }},
            key,
            traitName)

    fun meleeAttack(key: String, traitName: String, damageResistance: Int) = roll(
            "attack",
            { cr:CharacterRoller -> cr.rollMeleeAttackDamage(traitName, damageResistance )?.let{ richMessage(it) }},
            key,
            traitName)

    fun rangedAttack(key: String, traitName: String, damageResistance: Int, shotsFired: Int, marginOfSuccess: Int) =
            roll("attack",
            { cr:CharacterRoller ->
                cr.rollRangedAttackDamage(traitName, damageResistance, shotsFired, marginOfSuccess)?.let{ richMessage(it) }
            },
            key,
            traitName)


    fun roll(type: String,
             rollFunction: (CharacterRoller) -> RichMessage?,
             characterKey: String,
             traitName: String) =
            characterRepository
             .getByKey(characterKey)
             ?.let { characterRoller ->
                 rollFunction(characterRoller) ?: RichMessage("unable to find ${type} '${traitName}' for character key '${characterKey}'")
            }
            ?: RichMessage("unable to find character '${characterKey}'")

}
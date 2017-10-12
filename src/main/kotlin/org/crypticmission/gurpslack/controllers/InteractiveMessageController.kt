package org.crypticmission.gurpslack.controllers

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import me.ramswaroop.jbot.core.slack.models.RichMessage
import org.crypticmission.gurpslack.model.*
import org.crypticmission.gurpslack.repositories.CharacterRepository
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

class Option() {
    lateinit var value: String
}

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
class Action() {
    lateinit var name: String
    lateinit var type: String
    var value: String? = null
    var selectedOptions: List<Option>? = null

    fun selectedValue(): String? = this.selectedOptions?.first()?.value
}

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
class User() {
    lateinit var id: String
    lateinit var name: String
}

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
class MessageData() {
    lateinit var user: User
    lateinit var token: String
    lateinit var actions: Array<Action>
    lateinit var callbackId: String
    lateinit var responseUrl: String
}

open class CallbackMessage(text: String, val calllback_id: String) : RichMessage(text)

fun RichMessage.withCallback(calllback_id: String) = CallbackMessage(this.text, calllback_id)
fun CallbackMessage.replaceOriginal(replaceOriginal: Boolean) = ReplaceOriginalRichMessage(this.text, this.calllback_id, replaceOriginal)

class ReplaceOriginalRichMessage(text: String, callback_id: String, val replace_original: Boolean) : CallbackMessage(text, callback_id)

class ValueCache<T>(val defaultValue: T) {
    private val cache : MutableMap<String, T> = mutableMapOf()

    fun putValue(messageData: MessageData, value: T) { cache[messageData.user.name] = value }
    fun getValue(messageData: MessageData) = cache.getOrDefault(messageData.user.name, defaultValue)
    fun getAndClearValue(messageData: MessageData) : T {
        val v = cache.getOrDefault(messageData.user.name, defaultValue)
        cache.remove(messageData.user.name)
        return v;
    }

}
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
                else -> throw IllegalArgumentException("select action name must be 'modifier' or 'visibility', but is '${action.type}'")
            }
            else -> throw IllegalArgumentException("action type must be 'button' or 'select', but is '${action.type}'")
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
> *- Shots Fired:* ${shotsFired.toSignedStringWithZero()}
> *- Margin of Success:* ${marginOfSuccess.toSignedStringWithZero()}
""".trimMargin()
        )
    }

    private fun doShotsFired(action: Action, messageData: MessageData, shotsFiredCache: ValueCache<Int>, marginOfSuccessCache: ValueCache<Int>): RichMessage {
        val shotsFired = action.selectedValue()?.toInt() ?: 0
        shotsFiredCache.putValue(messageData, shotsFired)
        val marginOfSuccess = marginOfSuccessCache.getValue(messageData)
        return shotsFiredMessage(messageData, shotsFired, marginOfSuccess)
    }

    private fun doMarginOfSuccess(action: Action, messageData: MessageData, shotsFiredCache: ValueCache<Int>, marginOfSuccessCache: ValueCache<Int>): RichMessage {
        val marginOfSuccess = action.selectedValue()?.toInt() ?: 0
        marginOfSuccessCache.putValue(messageData, marginOfSuccess)
        val shotsFired = shotsFiredCache.getValue(messageData)
        return shotsFiredMessage(messageData, shotsFired, marginOfSuccess)
    }

    private fun doModifier(action: Action, message: String, messageData: MessageData, modifierCache: ValueCache<Int>): RichMessage {
        val modifier = action.selectedValue()?.toInt() ?: 0
        modifierCache.putValue(messageData, modifier)
        return RichMessage("modifying next roll by ${modifier.toSignedStringWithZero()} for next skill or attribute roll " +
                "made by ${messageData.user.name} ")
    }

    private fun doDamageResistance(action: Action, message: String, messageData: MessageData, damageResistanceCache: ValueCache<Int>): RichMessage {
        val damageResistance = action.selectedValue()?.toInt() ?: 0
        damageResistanceCache.putValue(messageData, damageResistance)
        return RichMessage("applying DR ${damageResistance.toSignedStringWithZero()} to next damage roll " +
                "made by ${messageData.user.name}")
    }

    private fun doVisibility(action: Action, message: String, messageData: MessageData, visibilityCache: ValueCache<Boolean>): RichMessage  {
        val visibility = VisibilityOption.fromValue(action.selectedValue())
        visibilityCache.putValue(messageData, visibility.isInChannel)
        return RichMessage("next roll will be visible to ${visibility.option.text}")
    }

    private fun doButtonMessage(action: Action, message: String, messageData: MessageData): RichMessage {
        val (characterKey, traitName) = action.value?.split("@") ?: throw IllegalArgumentException("value must be set")

        val richMessage: RichMessage = when (action.name) {

            "skill" -> skill(characterKey.toKey(), traitName.toKey(), modifierCache.getAndClearValue(messageData))
            "attribute" -> attribute(characterKey.toKey(), traitName.toKey(), modifierCache.getAndClearValue(messageData))

            "meleeAttack" -> meleeAttack(characterKey.toKey(), traitName.toKey(), damageResistanceCache.getAndClearValue(messageData))
            "rangedAttack" -> rangedAttack(
                    characterKey.toKey(),
                    traitName.toKey(),
                    damageResistanceCache.getAndClearValue(messageData),
                    shotsFiredCache.getAndClearValue(messageData),
                    marginOfSuccessCache.getAndClearValue(messageData)
            )

            else -> null
        } ?: ReplaceOriginalRichMessage("unable to find action when ${message}", messageData.callbackId, false)

        logger.info("outcome ${richMessage.text}")

        return richMessage
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
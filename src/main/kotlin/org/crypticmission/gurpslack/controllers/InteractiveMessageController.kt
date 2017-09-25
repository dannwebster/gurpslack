package org.crypticmission.gurpslack.controllers

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import me.ramswaroop.jbot.core.slack.models.RichMessage
import org.crypticmission.gurpslack.model.CharacterRoller
import org.crypticmission.gurpslack.model.richMessage
import org.crypticmission.gurpslack.model.toKey
import org.crypticmission.gurpslack.model.toSignedString
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

    fun selectedValueOr(defaultValue: String): String = this.selectedOptions?.first()?.value ?: defaultValue
}

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
class ButtonData() {
    lateinit var token: String
    lateinit var actions: Array<Action>
    lateinit var callbackId: String
    lateinit var responseUrl: String
}

open class CallbackMessage(text: String, val calllback_id: String) : RichMessage(text)

//fun RichMessage.withCallback(calllback_id: String) = CallbackMessage(this.text, calllback_id)

class ReplaceOriginalRichMessage(text: String, callback_id: String, val replace_original: Boolean) : CallbackMessage(text, callback_id)

@RestController
class InteractiveMessageController(val characterRepository: CharacterRepository) {
    private val logger = LoggerFactory.getLogger(InteractiveMessageController::class.java)

    val objectMapper = ObjectMapper()

    @PostMapping(path = arrayOf("/buttons"), consumes = arrayOf(APPLICATION_FORM_URLENCODED_VALUE))
    fun handleButtons(@RequestParam("payload") buttonJson: String): RichMessage {
        logger.info(buttonJson)
        val buttonData = objectMapper.readValue(buttonJson, ButtonData::class.java)

        val action = buttonData.actions.first()

        val message = "Pressed button ${action.name} and got value ${action.value} "
        logger.info(message)

        val richMessage = when (action.type) {
            "button" -> doButtonMessage(action, message, buttonData)
            "select" -> when(action.name) {
                "modifier" -> doModifier(action, message, buttonData)
                "visibility" -> doVisibility(action, message, buttonData)
                else -> throw IllegalArgumentException("select action name must be 'modifier' or 'visibility', but is '${action.type}'")
            }
            else -> throw IllegalArgumentException("action type must be 'button' or 'select', but is '${action.type}'")
        }

        return richMessage
                .inChannel(true)
                .encodedMessage()
    }

    private fun doModifier(action: Action, message: String, buttonData: ButtonData): RichMessage =
        RichMessage("modifying next roll by ${action.selectedValueOr("+0")}")

    private fun doVisibility(action: Action, message: String, buttonData: ButtonData): RichMessage =
        RichMessage("next roll will be visible to ${action.selectedValueOr("me only")}")

    private fun doButtonMessage(action: Action, message: String, buttonData: ButtonData): RichMessage {
        val (characterKey, traitName, modifierString) = action.value?.split("@") ?: throw IllegalArgumentException("value must be set")
        val modifier = modifierString.toIntOrNull() ?: 0
        logger.info("looking up ${action.name} ${traitName}${modifier.toSignedString()} for character ${characterKey}")

        val richMessage: RichMessage = when (action.name) {
            "skill" -> skill(characterKey.toKey(), traitName.toKey(), modifier)
            "meleeAttack" -> meleeAttack(characterKey.toKey(), traitName.toKey(), modifier)
            "rangedAttack" -> rangedAttack(characterKey.toKey(), traitName.toKey(), modifier)
            "attribute" -> attribute(characterKey.toKey(), traitName.toKey(), modifier)
            else -> null
        } ?: ReplaceOriginalRichMessage("unable to find action when ${message}", buttonData.callbackId, false)

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

    fun rangedAttack(key: String, traitName: String, damageResistance: Int) = roll(
            "attack",
            { cr:CharacterRoller -> cr.rollRangedAttackDamage(traitName, damageResistance )?.let{ richMessage(it) }},
            key,
            traitName)


    fun roll(type: String,
             rollFunction: (CharacterRoller) -> RichMessage?,
             characterKey: String,
             traitName: String) = characterRepository
             .get(characterKey)
             ?.let { characterRoller ->
                 rollFunction(characterRoller) ?: RichMessage("unable to find ${type} '${traitName}' for character key '${characterKey}'")
            }
            ?: RichMessage("unable to find character '${characterKey}'")

}
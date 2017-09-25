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

class Action() {
    lateinit var name: String
    lateinit var value: String
    lateinit var type: String
}

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
class ButtonData() {
    lateinit var token: String
    lateinit var actions: Array<Action>
    lateinit var callbackId: String
    lateinit var responseUrl: String
}
/**
 * {
"actions": [
{
"name": "recommend",
"value": "yes",
"type": "button"
}
],
"callback_id": "comic_1234_xyz",
"team": {
"id": "T47563693",
"domain": "watermelonsugar"
},
"channel": {
"id": "C065W1189",
"name": "forgotten-works"
},
"user": {
"id": "U045VRZFT",
"name": "brautigan"
},
"action_ts": "1458170917.164398",
"message_ts": "1458170866.000004",
"attachment_id": "1",
"token": "xAB3yVzGS4BQ3O9FACTa8Ho4",
"original_message": {"text":"New comic book alert!","attachments":[{"title":"The Further Adventures of Slackbot","fields":[{"title":"Volume","value":"1","short":true},{"title":"Issue","value":"3","short":true}],"author_name":"Stanford S. Strickland","author_icon":"https://api.slack.comhttps://a.slack-edge.com/bfaba/img/api/homepage_custom_integrations-2x.png","image_url":"http://i.imgur.com/OJkaVOI.jpg?1"},{"title":"Synopsis","text":"After @episod pushed exciting changes to a devious new branch back in Issue 1, Slackbot notifies @don about an unexpected deploy..."},{"fallback":"Would you recommend it to customers?","title":"Would you recommend it to customers?","callback_id":"comic_1234_xyz","color":"#3AA3E3","attachment_type":"default","actions":[{"name":"recommend","text":"Recommend","type":"button","value":"recommend"},{"name":"no","text":"No","type":"button","value":"bad"}]}]},
"response_url": "https://hooks.slack.com/actions/T47563693/6204672533/x7ZLaiVMoECAW50Gw1ZYAXEM"
}
 */
open class CallbackMessage(text: String, val calllback_id: String) : RichMessage(text)

fun RichMessage.withCallback(calllback_id: String) = CallbackMessage(this.text, calllback_id)

class ReplaceOriginalRichMessage(text: String, callback_id: String, val replace_original: Boolean) : CallbackMessage(text, callback_id)

@RestController
class ButtonController(val characterRepository: CharacterRepository) {
    private val logger = LoggerFactory.getLogger(ButtonController::class.java)

    val objectMapper = ObjectMapper()

    @PostMapping(path = arrayOf("/buttons"), consumes = arrayOf(APPLICATION_FORM_URLENCODED_VALUE))
    fun handleButtons(@RequestParam("payload") buttonJson: String) : RichMessage{
        logger.info(buttonJson)
        val buttonData = objectMapper.readValue(buttonJson, ButtonData::class.java)

        val action = buttonData.actions.first()

        val message = "Pressed button ${action.name} and got value ${action.value} "
        logger.info(message)

        val (characterKey, traitName, modifierString) = action.value.split("@")
        val modifier = modifierString.toIntOrNull() ?: 0
        logger.info("looking up ${action.name} ${traitName}${modifier.toSignedString()} for character ${characterKey}")

        val richMessage : RichMessage = when (action.name) {
            "skill" -> skill(characterKey.toKey(), traitName.toKey(), modifier)
            "meleeAttack" -> meleeAttack(characterKey.toKey(), traitName.toKey(), modifier)
            "rangedAttack" -> rangedAttack(characterKey.toKey(), traitName.toKey(), modifier)
            "attribute" -> attribute(characterKey.toKey(), traitName.toKey(), modifier)
            else -> null
        } ?: ReplaceOriginalRichMessage("unable to find action when ${message}", buttonData.callbackId, false)

        logger.info("outcome ${richMessage.text}")
        return richMessage
                .inChannel(true)
                .encodedMessage()
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
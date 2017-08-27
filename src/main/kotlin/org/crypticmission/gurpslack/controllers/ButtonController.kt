package org.crypticmission.gurpslack.controllers

import me.ramswaroop.jbot.core.slack.models.RichMessage
import org.crypticmission.gurpslack.model.RollOutcome
import org.crypticmission.gurpslack.model.richMessage
import org.crypticmission.gurpslack.repositories.CharacterRepository
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

class Action() {
    lateinit var name: String
    lateinit var value: String
    lateinit var type: String
}
class ButtonData() {
    lateinit var actions: List<Action>
    lateinit var callback_id: String
    lateinit var response_url: String
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
class CallbackMessage(text: String, val calllback_id: String) : RichMessage(text) {
}

fun RichMessage.withCallback(calllback_id: String) = CallbackMessage(this.text, calllback_id)

@RestController
class ButtonController(val characterRepository: CharacterRepository) {
    private val logger = LoggerFactory.getLogger(ButtonController::class.java)

    @PostMapping("/buttons")
    fun handleButtons(buttonData: ButtonData) : RichMessage{
        val action = buttonData.actions.first()
        val message = "Pressed button ${action.name} and got value ${action.value} "
        val (key, name) = action.value.split("@")
        logger.info(message)
        val richMessage : RichMessage = when (action.name) {
            "skill" -> skill(key, name)
            "attack" -> attack(key, name)
            "attribute" -> attribute(key, name)
            else -> null
        } ?: RichMessage("unable to find action when " + message)
        logger.info("outcome ${richMessage}")
        return richMessage
                .withCallback(buttonData.callback_id)
                .inChannel(true)
                .encodedMessage()
    }

    fun skill(key: String, name: String) = characterRepository.get(key)?.rollVsSkill(name, 0)?.let { richMessage(it)}
    fun attack(key: String, name: String) = characterRepository.get(key)?.rollAttackDamage(name, 0)?.let { richMessage(it)}
    fun attribute(key: String, name: String) = characterRepository.get(key)?.rollVsAttribute(name, 0)?.let { richMessage(it)}
}
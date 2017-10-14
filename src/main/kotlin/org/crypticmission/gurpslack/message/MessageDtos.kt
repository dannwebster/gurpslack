package org.crypticmission.gurpslack.message

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import me.ramswaroop.jbot.core.slack.models.RichMessage

class Option() {
    lateinit var value: String
}

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
class ActionTaken() {
    lateinit var name: String
    lateinit var type: String
    var value: String? = null
    var selectedOptions: List<Option>? = null

    fun selectedValue(): String? = this.selectedOptions?.first()?.value
}

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
class MessageData() {
    lateinit var user: User
    lateinit var token: String
    lateinit var actions: Array<ActionTaken>
    lateinit var callbackId: String
    lateinit var responseUrl: String
}

open class CallbackMessage(text: String, val calllback_id: String) : RichMessage(text)

fun RichMessage.withCallback(calllback_id: String) : CallbackMessage {
    val msg = CallbackMessage(this.text, calllback_id)
    msg.attachments = this.attachments
    return msg
}

fun CallbackMessage.replaceOriginal(replaceOriginal: Boolean) : ReplaceOriginalRichMessage {
    val msg =ReplaceOriginalRichMessage(this.text, this.calllback_id, replaceOriginal)
    msg.attachments = this.attachments
    return msg
}

class ReplaceOriginalRichMessage(text: String, callback_id: String, val replace_original: Boolean) : CallbackMessage(text, callback_id)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
class User() {
    lateinit var id: String
    lateinit var name: String
}
enum class MenuType() { DAMAGE, MOD, TRACKED }
enum class CharacterSections(val menuType: MenuType) {
    PRIMARY_ATTRIBUTES(MenuType.MOD),
    DERIVED_ATTRIBUTES(MenuType.MOD),
    TRACKED_STATS(MenuType.TRACKED),
    SKILLS(MenuType.MOD),
    MELEE_ATTACKS(MenuType.DAMAGE),
    RANGED_ATTACKS(MenuType.DAMAGE)
}
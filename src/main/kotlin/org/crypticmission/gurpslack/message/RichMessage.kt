package org.crypticmission.gurpslack.message

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

/**
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class RichMessage(
        val text: String,
        val username: String? = null,
        val iconEmoji: String? = null,
        val channel: String? = null,
        var responseType: String? = null,
        val callbackId: String? = null,
        val replaceOriginal: Boolean = false,
        val attachments: Array<Attachment>? = null
) {

    fun inChannel(inChannel: Boolean = true): RichMessage = this.copy(responseType = when(inChannel) {
        true -> "in_channel"
        false -> "ephemeral"
    })
    fun encodedMessage(): RichMessage = this.copy(text = this.text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;"))
    fun withCallback(callbackId: String): RichMessage = this.copy(callbackId = callbackId)
    fun replaceOriginal(replaceOriginal: Boolean = true): RichMessage = this.copy(replaceOriginal = replaceOriginal)
    fun withAttachments(attachments: Array<Attachment>) = this.copy(attachments = attachments)

}


interface Action {
    val name: String
    val text: String
    val type: String
}

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class Button(override val name: String,
                  override val text: String,
                  val value: String) : Action {
    override val type = "button"
}

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class Menu(override val name: String,
                override val text: String,
                val options: List<MenuOption>? = emptyList()) : Action {
    override val type = "select"
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class Field(
    val title: String,
    val value: String,
    val shortEnough: Boolean)

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class Attachment(
        val text: String? = null,
        val actions: List<Action>? = null,
        val callbackId: String? = null,

        val fallback: String? = null,
        val color: String? = null,
        val pretext: String? = null,
        val authorName: String? = null,
        val authorLink: String? = null,
        val authorIcon: String? = null,
        val title: String? = null,
        val titleLink: String? = null,
        val fields: Array<Field>? = null,
        val imageUrl: String? = null,
        val thumbUrl: String? = null,
        val footer: String? = null,
        val footerIcon: String? = null,
        val ts: String? = null,
        val mrkdwnIn: List<String> = listOf("text", "pretext"))

package org.crypticmission.gurpslack.model

import me.ramswaroop.jbot.core.slack.models.Attachment
import me.ramswaroop.jbot.core.slack.models.RichMessage

/**
 */

data class Action(val name: String, val text: String, val type: String, val value: String)

class ActionAttachment(val actions: List<Action>, val callback_id: String, val mrkdwn_in: List<String> = listOf("text", "pretext")) : Attachment()


fun richMessage(key: String, characterRoller: CharacterRoller): RichMessage {
    with (characterRoller){
        val msg = "*Character Name: ${characterName}*\n" +
                "_*Attributes:*_\n" +
                attributes.values
                        .map { "    " + it.toString() }
                        .joinToString("\n", postfix = "\n") +
                "_*Skills:*_\n" +
                skills.values
                        .map { "    " + it.toString() }
                        .joinToString("\n", postfix = "\n") +
                "_*Attacks*_:\n" +
                attacks.values
                        .map { attack -> "    ${attack.attackName}: ${attack.damageSpec.canonical}"  }
                        .sorted()
                        .joinToString("\n", postfix = "\n")

        val attributeAttachment = attributeAttachment(key, characterRoller.attributes.values)

        val skillAttachment = skillAttachment(key, characterRoller.skills.values)

        val richMessage = RichMessage(msg)
        richMessage.attachments = arrayOf(
                attributeAttachment,
                skillAttachment
        )
        return richMessage
    }
}

private fun skillAttachment(key: String, skills: Collection<Attribute>): ActionAttachment {
    val skillButtons = skills.map { Action("skill", "${it.name}: ${it.level}", "button", "${key}@${it.name}") }
    val skillAttachment = ActionAttachment(skillButtons, "${key}-skills")
    skillAttachment.text = "_*Skills:*_"
    return skillAttachment
}

private fun attributeAttachment(key: String, attributes: Collection<Attribute>): ActionAttachment {
    val attributeButtons = attributes.map { Action("attribute", "${it.name}: ${it.level}", "button", "${key}@${it.name}") }
    val attributeAttachment = ActionAttachment(attributeButtons, "${key}-attributes")
    attributeAttachment.text = "_*Attributes:*_"
    return attributeAttachment
}

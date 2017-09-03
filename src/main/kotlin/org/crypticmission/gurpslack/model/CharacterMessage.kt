package org.crypticmission.gurpslack.model

import me.ramswaroop.jbot.core.slack.models.Attachment
import me.ramswaroop.jbot.core.slack.models.RichMessage
import org.slf4j.LoggerFactory

/**
 */

private val logger = LoggerFactory.getLogger("CharacterMessage")

data class Action(val name: String, val text: String, val type: String, val value: String)

class ActionAttachment(text: String?, val actions: List<Action>,
                       val callback_id: String,
                       val mrkdwn_in: List<String> = listOf("text", "pretext")) : Attachment() {
    init {
        super.setText(text)
    }
}


fun message(characterRoller: CharacterRoller) = with (characterRoller) {
    "*Character Name: ${characterName}*\n" +
            "_*Primary Attributes:*_\n" +
            primaryAttributes()
                    .map { "    " + it.toString() }
                    .joinToString("\n", postfix = "\n") +
            "_*Derived Attributes:*_\n" +
            derivedAttributes()
                    .map { "    " + it.toString() }
                    .joinToString("\n", postfix = "\n") +
            "_*Skills:*_\n" +
            skills.values
                    .map { "    " + it.toString() }
                    .joinToString("\n", postfix = "\n") +
            "_*Melee Attacks*_:\n" +
            meleeAttacks.values
                    .map { attack -> "    ${attack.attackName}: ${attack.damageSpec.canonical}" }
                    .sorted()
                    .joinToString("\n", postfix = "\n") +
            "_*Ranged Attacks*_:\n" +
            rangedAttacks.values
                    .map { attack -> "    ${attack.attackName}: ${attack.damageSpec.canonical}" }
                    .sorted()
                    .joinToString("\n", postfix = "\n")
}

fun richMessage(key: String, characterRoller: CharacterRoller): RichMessage {
    with (characterRoller){
        val msg = "*Character Name: ${characterName}*\n"

        val primaryAttributesAttachment = attributesAttachment(key, "Primary", characterRoller.primaryAttributes())
        val derivedAttributesAttachment = attributesAttachment(key, "Derived", characterRoller.derivedAttributes())

        val skillAttachments = skillAttachment(key, characterRoller.skills.values)
        val meleeAttackAttachments = attackAttachment(key, "melee", characterRoller.meleeAttacks.values)
        val rangedAttackAttachments = attackAttachment(key, "ranged", characterRoller.rangedAttacks.values)

        val richMessage = RichMessage(msg)
        richMessage.attachments =
                arrayOf(primaryAttributesAttachment) +
//                arrayOf(derivedAttributesAttachment)
//                skillAttachments +
//                meleeAttackAttachments +
                rangedAttackAttachments

        return richMessage
    }
}

private fun buttonValue(characterKey: String, traitName: String, modifier: Int) =
        "${characterKey.toKey()}@${traitName.toKey()}@${modifier}"

private fun skillAttachment(key: String, skills: Collection<Attribute>): List<ActionAttachment> =
        skills
                .map { attribute -> skillToButton(key, attribute) }
                .groupBy(3)
                .mapIndexed { index, list ->
                    ActionAttachment( when(index)  {
                        0 -> "_*Skills:*_"
                        else -> null

                    }, list, "${key}-skills-${index}") }

private fun attackAttachment(key: String, type: String, attacks: Collection<Attack>): List<ActionAttachment> =
        attacks
                .map { attack -> attackToButton(key, type, attack) }
                .groupBy(3)
                .mapIndexed { index, list ->
                    ActionAttachment( when(index)  {
                        0 -> "_*${type.capitalize()} Attacks:*_"
                        else -> null

                    }, list, "${key}-${type}-attacks-${index}") }

fun skillToButton(key: String, attribute: Attribute) =
        Action("skill", "${attribute.name}: ${attribute.level}", "button",
                buttonValue(key, attribute.name, 0))

fun attackToButton(key: String, type: String, attack: Attack) =
        Action("${type}Attack", "${attack.attackName}: ${attack.damageSpec.canonical}", "button",
                buttonValue(key, attack.attackName, 0))

fun <T> List<T>.groupBy(groupSize: Int): List<List<T>> =
        this.withIndex()
                .groupBy { it.index / groupSize }.values
                .map { it.map { it.value }}

private fun attributesAttachment(key: String, type: String, attributes: Collection<Attribute>): ActionAttachment {
    val attributeButtons = attributes
            .map { Action("attribute", "${it.name}: ${it.level}", "button", buttonValue(key, it.name, 0)) }
    val attributeAttachment = ActionAttachment("_*${type} Attributes:*_", attributeButtons, "${key}-attributes")
    return attributeAttachment
}

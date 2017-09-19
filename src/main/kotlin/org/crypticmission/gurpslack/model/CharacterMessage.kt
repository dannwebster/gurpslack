package org.crypticmission.gurpslack.model

import me.ramswaroop.jbot.core.slack.models.Attachment
import me.ramswaroop.jbot.core.slack.models.RichMessage
import org.crypticmission.gurpslack.model.CharacterSections.*
import org.slf4j.LoggerFactory

/**
 */

private val logger = LoggerFactory.getLogger("CharacterMessage")

data class Option(val text: String, val value: String)

interface Action {
    val name: String
    val text: String
    val type: String
}

data class Button(override val name: String,
                  override val text: String,
                  override val type: String,
                  val value: String) : Action

data class Menu(override val name: String,
                override val text: String,
                override val type: String,
                val options: List<Option>? = emptyList()) : Action

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

enum class CharacterSections {
    PRIMARY_ATTRIBUTES, DERIVED_ATTRIBUTES, SKILLS, MELEE_ATTACKS, RANGED_ATTACKS
}
fun richMessage(key: String, characterRoller: CharacterRoller,
                sections : Array<CharacterSections> = values()): RichMessage {
    with (characterRoller){
        val msg = "*Character Name: ${characterName}*\n"

        val attachments = (optionsAttachment(key) + sections.map { section -> when(section) {
            PRIMARY_ATTRIBUTES -> attributesAttachments(key, "Primary", characterRoller.primaryAttributes())
            DERIVED_ATTRIBUTES -> attributesAttachments(key, "Derived", characterRoller.derivedAttributes())
            SKILLS -> skillAttachments(key, characterRoller.skills.values)
            MELEE_ATTACKS -> attackAttachments(key, "melee", characterRoller.meleeAttacks.values)
            RANGED_ATTACKS -> attackAttachments(key, "ranged", characterRoller.rangedAttacks.values)
        } }.flatten()).toTypedArray()


        val richMessage = RichMessage(msg)
        richMessage.attachments = attachments

        return richMessage
    }
}

private fun buttonValue(characterKey: String, traitName: String, modifier: Int) =
        "${characterKey.toKey()}@${traitName.toKey()}@${modifier}"

private fun optionsAttachment(key: String): List<ActionAttachment> = listOf(
        ActionAttachment(key, listOf(
            Menu("modifiers", "Modifier", "select", options = modifiers())
        ), "${key}-modifier"),
        ActionAttachment(key, listOf(
            Menu("visibility", "Visibility", "select", options = visibility())
        ), "${key}-visibility")
)

private fun modifiers() = (-10 .. 10).map { Option(it.toSignedString(), it.toString()) }
private fun visibility() = listOf(Option("Me Only", "me"), Option("Open to the Channel", "channel"))

private fun skillAttachments(key: String, skills: Collection<Attribute>): List<ActionAttachment> =
        skills
                .map { attribute -> skillToButton(key, attribute) }
                .groupBy(3)
                .mapIndexed { index, list ->
                    ActionAttachment( when(index)  {
                        0 -> "_*Skills:*_"
                        else -> null

                    }, list, "${key}-skills-${index}") }

private fun attackAttachments(key: String, type: String, attacks: Collection<Attack>): List<ActionAttachment> =
        attacks
                .map { attack -> attackToButton(key, type, attack) }
                .groupBy(3)
                .mapIndexed { index, list ->
                    ActionAttachment( when(index)  {
                        0 -> "_*${type.capitalize()} Attacks:*_"
                        else -> null

                    }, list, "${key}-${type}-attacks-${index}") }

fun skillToButton(key: String, attribute: Attribute) =
        Button("skill", "${attribute.name}: ${attribute.level}", "button",
                buttonValue(key, attribute.name, 0))

fun attackToButton(key: String, type: String, attack: Attack) =
        Button("${type}Attack", "${attack.attackName}: ${attack.damageSpec.canonical}", "button",
                buttonValue(key, attack.attackName, 0))

fun <T> List<T>.groupBy(groupSize: Int): List<List<T>> =
        this.withIndex()
                .groupBy { it.index / groupSize }.values
                .map { it.map { it.value }}

private fun attributesAttachments(key: String, type: String, attributes: Collection<Attribute>): List<ActionAttachment> =
        listOf(attributesAttachment(key, type, attributes))

private fun attributesAttachment(key: String, type: String, attributes: Collection<Attribute>): ActionAttachment {
    val attributeButtons = attributes
            .map { Button("attribute", "${it.name}: ${it.level}", "button", buttonValue(key, it.name, 0)) }
    val attributeAttachment = ActionAttachment("_*${type} Attributes:*_", attributeButtons, "${key}-attributes")
    return attributeAttachment
}

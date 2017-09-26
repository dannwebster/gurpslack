package org.crypticmission.gurpslack.model

import me.ramswaroop.jbot.core.slack.models.Attachment
import me.ramswaroop.jbot.core.slack.models.RichMessage
import org.crypticmission.gurpslack.model.CharacterSections.*
import org.slf4j.LoggerFactory

/**
 */

private val logger = LoggerFactory.getLogger("CharacterMessage")

data class Option(val text: String, val value: String)

enum class VisibilityOption(val option: Option, val isInChannel: Boolean) {
    OPEN(Option("The Channel", "open"), true),
    PRIVATE(Option("Me Only", "private"), false);

    companion object {
        fun fromValue(value: String?) = when(value) {
            "open" -> OPEN
            "private" -> PRIVATE
            else -> OPEN
        }
    }
}

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

enum class MenuType() { DR, MOD }
enum class CharacterSections(val menuType: MenuType) {
    PRIMARY_ATTRIBUTES(MenuType.MOD),
    DERIVED_ATTRIBUTES(MenuType.MOD),
    SKILLS(MenuType.MOD),
    MELEE_ATTACKS(MenuType.DR),
    RANGED_ATTACKS(MenuType.DR),
}

fun richMessage(key: String, characterRoller: CharacterRoller,
                sections : Array<CharacterSections> = values()): RichMessage {
    with (characterRoller){
        val msg = "*Character Name: ${characterName}*\n"

        val attachments = (sections.map { section -> when(section) {
            PRIMARY_ATTRIBUTES -> attributesAttachments(key, "Primary", characterRoller.primaryAttributes())
            DERIVED_ATTRIBUTES -> attributesAttachments(key, "Derived", characterRoller.derivedAttributes())
            SKILLS -> skillAttachments(key, characterRoller.skills.values)
            MELEE_ATTACKS -> attackAttachments(key, "melee", characterRoller.meleeAttacks.values)
            RANGED_ATTACKS -> attackAttachments(key, "ranged", characterRoller.rangedAttacks.values)
        } }
                .flatten()
                + optionsAttachment(key, sections))
                .toTypedArray()


        val richMessage = RichMessage(msg)
        richMessage.attachments = attachments

        return richMessage
    }
}

private fun optionsAttachment(key: String, sections : Array<CharacterSections>): ActionAttachment = ActionAttachment(
        "*Options*",
        menuOptions(sections),
        "${key}-visibility")

private fun buttonValue(characterKey: String, traitName: String) = "${characterKey.toKey()}@${traitName.toKey()}"

val DR_MENU = Menu("dr", "Damage Resistance", "select", options = dr())
val MODIFIER_MENU = Menu("modifier", "Modifier", "select", options = modifiers())
val VISIBILITY_MENU = Menu("visibility", "Visibility", "select", options = visibility())

private fun menuOptions(sections: Array<CharacterSections>) : List<Menu> {
    val set = sections.map { it.menuType }.toSet()
    val options = mutableListOf(VISIBILITY_MENU)
    if (set.contains(MenuType.MOD)) options += MODIFIER_MENU
    if (set.contains(MenuType.DR)) options += DR_MENU
    return options
}

private fun modifiers() = (-10 .. 10).map { Option(it.toSignedStringWithZero(), it.toString()) }
private fun dr() = (0 .. 10).map { Option(it.toSignedStringWithZero(), it.toString()) }
private fun visibility() = VisibilityOption.values().map { it.option }

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
                buttonValue(key, attribute.name))

fun attackToButton(key: String, type: String, attack: Attack) =
        Button("${type}Attack", "${attack.attackName}: ${attack.damageSpec.canonical}", "button",
                buttonValue(key, attack.attackName))

fun <T> List<T>.groupBy(groupSize: Int): List<List<T>> =
        this.withIndex()
                .groupBy { it.index / groupSize }.values
                .map { it.map { it.value }}

private fun attributesAttachments(key: String, type: String, attributes: Collection<Attribute>): List<ActionAttachment> =
        listOf(attributesAttachment(key, type, attributes))

private fun attributesAttachment(key: String, type: String, attributes: Collection<Attribute>): ActionAttachment {
    val attributeButtons = attributes
            .map { Button("attribute", "${it.name}: ${it.level}", "button", buttonValue(key, it.name)) }
    val attributeAttachment = ActionAttachment("_*${type} Attributes:*_", attributeButtons, "${key}-attributes")
    return attributeAttachment
}

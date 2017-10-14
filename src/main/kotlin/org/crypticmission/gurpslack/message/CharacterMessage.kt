package org.crypticmission.gurpslack.message

import org.crypticmission.gurpslack.message.CharacterSections.*
import org.crypticmission.gurpslack.model.*
import org.slf4j.LoggerFactory

/**
 */

private val logger = LoggerFactory.getLogger("CharacterMessage")

data class MenuOption(val text: String, val value: String)

enum class VisibilityOption(val menuOption: MenuOption, val isInChannel: Boolean) {
    OPEN(MenuOption("The Channel", "open"), true),
    PRIVATE(MenuOption("Me Only", "private"), false);

    companion object {
        fun fromValue(value: String?) = when(value) {
            "open" -> OPEN
            "private" -> PRIVATE
            else -> OPEN
        }
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
                    .sortedBy { skill -> skill.name }
                    .map { "    " + it.toString() }
                    .joinToString("\n", postfix = "\n") +
            "_*Melee Attacks*_:\n" +
            meleeAttacks.values
                    .sortedBy { attack -> attack.attackName }
                    .map { attack -> "    ${attack.attackName}: ${attack.damageSpec.canonical}" }
                    .joinToString("\n", postfix = "\n") +
            "_*Ranged Attacks*_:\n" +
            rangedAttacks.values
                    .sortedBy { attack -> attack.attackName }
                    .map { attack -> "    ${attack.attackName}: ${attack.damageSpec.canonical}" }
                    .joinToString("\n", postfix = "\n")
}



@JvmOverloads
fun richMessage(key: String, characterRoller: CharacterRoller,
                sections : Array<CharacterSections> = values()): RichMessage {
    with (characterRoller){
        val msg = "*Character Name: ${characterName}*\n"

        val attachments = (sections.map { section -> when(section) {
            PRIMARY_ATTRIBUTES -> attributesAttachments(key, "Primary", characterRoller.primaryAttributes())
            DERIVED_ATTRIBUTES -> attributesAttachments(key, "Derived", characterRoller.derivedAttributes())
            TRACKED_STATS -> trackedStatsAttachments(key, characterRoller.trackedValues)
            SKILLS -> skillAttachments(key, characterRoller.skills.values)
            MELEE_ATTACKS -> attackAttachments(key, "melee", characterRoller.meleeAttacks.values)
            RANGED_ATTACKS -> attackAttachments(key, "ranged", characterRoller.rangedAttacks.values)
        } }
                .flatten()
                + optionsAttachment(key, sections))
                .toTypedArray()


        val richMessage = RichMessage(msg)
        richMessage.withAttachments(attachments)

        return richMessage
    }
}

private fun optionsAttachment(key: String, sections : Array<CharacterSections>): Attachment = Attachment(
        "*Options*",
        menuOptions(sections),
        "${key}-visibility")


val SUCCESS_MARGIN_MENU = Menu("success-margin", "Margin of Success", options = successMargin())
val RATE_OF_FIRE_MENU = Menu("shots-fired", "Shots Fired", options = shotsFired())
val DR_MENU = Menu("dr", "Damage Resistance", options = dr())
val MODIFIER_MENU = Menu("modifier", "Modifier", options = modifiers())
val VISIBILITY_MENU = Menu("visibility", "Visibility", options = visibility())

private fun menuOptions(sections: Array<CharacterSections>) : List<Menu> {
    val set = sections.map { it.menuType }.toSet()
    val options = mutableListOf(VISIBILITY_MENU)
    if (set.contains(MenuType.MOD)) options += MODIFIER_MENU
    if (set.contains(MenuType.DAMAGE)) {
        options += DR_MENU
        options += SUCCESS_MARGIN_MENU
        options += RATE_OF_FIRE_MENU
    }
    return options
}

private fun successMargin() = (0 .. 10).map { MenuOption(it.toSignedStringWithZero(), it.toString()) }
private fun shotsFired() = (1 .. 30).map { MenuOption(it.toString(), it.toString()) }
private fun modifiers() = (-10 .. 10).map { MenuOption(it.toSignedStringWithZero(), it.toString()) }
private fun dr() = (0 .. 10).map { MenuOption(it.toSignedStringWithZero(), it.toString()) }
private fun visibility() = VisibilityOption.values().map { it.menuOption }



private fun skillAttachments(key: String, skills: Collection<Attribute>): List<Attachment> =
        skills
                .sortedBy { skill -> skill.name }
                .map { attribute -> skillToButton(key, attribute) }
                .groupBy(3)
                .mapIndexed { index, list ->
                    Attachment(when (index) {
                        0 -> "_*Skills:*_"
                        else -> null

                    }, list, "${key}-skills-${index}")
                }

private fun attackAttachments(key: String, type: String, attacks: Collection<Attack>): List<Attachment> =
        attacks
                .sortedBy { attack -> attack.attackName }
                .map { attack -> attackToButton(key, type, attack) }
                .groupBy(3)
                .mapIndexed { index, list ->
                    Attachment(when (index) {
                        0 -> "_*${type.capitalize()} Attacks:*_"
                        else -> null

                    }, list, "${key}-${type}-attacks-${index}")
                }

fun skillToButton(key: String, attribute: Attribute) =
        Button("skill", "${attribute.name}: ${attribute.level}",
                buttonValue(key, attribute.name))

fun attackToButton(key: String, type: String, attack: Attack) =
        Button("${type}Attack", "${attack.attackName}: ${attack.damageSpec.canonical}",
                buttonValue(key, attack.attackName))

fun <T> List<T>.groupBy(groupSize: Int): List<List<T>> =
        this.withIndex()
                .groupBy { it.index / groupSize }.values
                .map { it.map { it.value }}

private fun attributesAttachments(key: String, type: String, attributes: Collection<Attribute>): List<Attachment> =
        listOf(attributesAttachment(key, type, attributes))

private fun attributesAttachment(key: String, type: String, attributes: Collection<Attribute>): Attachment {
    val attributeButtons = attributes
            .map { Button("attribute", "${it.name}: ${it.level}", buttonValue(key, it.name)) }
    val attributeAttachment = Attachment("_*${type} Attributes:*_", attributeButtons, "${key}-attributes")
    return attributeAttachment
}

fun buttonValue(characterKey: String, traitName: String) = "${characterKey.toKey()}@${traitName.toKey()}"

package org.crypticmission.gurpslack.model

import me.ramswaroop.jbot.core.slack.models.RichMessage

/**
 */
fun String.toKey() = this.toLowerCase().trim()

fun String.toTitleCase(): String {
    val titleCase = StringBuilder()
    var nextTitleCase = true;

    this.toCharArray().forEach { c ->
        val nextC = when (nextTitleCase) {
            true -> Character.toUpperCase(c)
            false -> Character.toLowerCase(c)
        }
        nextTitleCase = c.isWhitespace()
        titleCase.append(nextC)
    }
    return titleCase.toString()
}

fun Int.toSignedStringWithZero(): String =
        if (this == 0) "+0"
        else this.toSignedString()

fun Int.toSignedString(): String =
        if (this > 0) "+${this}"
        else if (this < 0) "${this}"
        else ""

fun RollOutcome.emoji() = this.rollValues.map{ ":d6-${it}:"}.joinToString(" ") + this.adds.toSignedString()

fun message(attribute: Attribute) = with (attribute) {
    "${name}: ${level}"
}

fun message(modifiedAttribute: ModifiedAttribute) = with (modifiedAttribute) {
    val effectiveName = "${attribute.name}${modifier.toSignedString()}"
    "${effectiveName} (${effectiveLevel})"
}

fun message(attributeRollOutcome: AttributeRollOutcome, actor: String? = null) = with (attributeRollOutcome)  {
"""
* *Attempt*: ${actor?.plus(" ") ?: ""}Rolled vs ${modifiedAttribute}
* *Outcome*: ${(isCriticalString+isSuccessString).toTitleCase()}${if (!isCritical) " by " + Math.abs(margin) else ""}
* *Roll*: ${rollOutcome.emoji()} = ${rollOutcome.total}
* *Effective Level*: ${modifiedAttribute.effectiveLevel}
* *Margin of ${isSuccessString.toTitleCase()}*: ${Math.abs(margin)}
* *Attribute*: ${message(modifiedAttribute.attribute)}
* *Modifier*: ${modifiedAttribute.modifier.toSignedStringWithZero()}
""".trimIndent()
}


fun message(characterAttackRollOutcome: CharacterAttackRollOutcome) = with (characterAttackRollOutcome) {
    message(attackRollOutcome.damageRollOutcome, attackRollOutcome.attackName, characterName)
}

fun message(attackRollOutcome: AttackRollOutcome) = with (attackRollOutcome) {
    message(damageRollOutcome, attackName)
}

fun message(damageRollOutcome: DamageRollOutcome, attack: String? = null, actor: String? = null) = with (damageRollOutcome) {
        "Dealt *${totalDamage}* ${damageSpec.damageType.longForm} damage after DR:\n" +
                "This attack causes ${damageSpec.rollSpec.canonical} ${damageSpec.damageType.shortForm} vs DR ${damageResistance}.\n" +
                "Rolled ${damageSpec.rollSpec.canonical} => ${rollOutcome.emoji()} => ${rollOutcome.total}.\n" +
                "`${totalDamage} = [(${rollOutcome.total} impact damage - DR ${damageResistance}) * ${damageSpec.damageType.multiplier} " +
                "for ${damageSpec.damageType.longForm}]`\n"
}

fun message(rollOutcome: RollOutcome) = with (rollOutcome) {
    "${emoji()} => Rolled *${total}* on ${rollSpec.canonical}"
}


fun message(characterAttributeRollOutcome: CharacterAttributeRollOutcome) = with (characterAttributeRollOutcome) {
    message(attributeRollOutcome, characterName)
}

fun doRichMessage(s: String) = RichMessage(s)

fun richMessage(outcome: RollOutcome) = doRichMessage(message(outcome))
fun richMessage(outcome: DamageRollOutcome) = doRichMessage(message(outcome))
fun richMessage(outcome: AttackRollOutcome) = doRichMessage(message(outcome))
fun richMessage(outcome: AttributeRollOutcome) = doRichMessage(message(outcome))
fun richMessage(outcome: CharacterAttributeRollOutcome) = doRichMessage(message(outcome))
fun richMessage(outcome: CharacterAttackRollOutcome) = doRichMessage(message(outcome))


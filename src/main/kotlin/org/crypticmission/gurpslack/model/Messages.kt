package org.crypticmission.gurpslack.model

import me.ramswaroop.jbot.core.slack.models.RichMessage

/**
 */
fun String.toKey() = this.toLowerCase().trim()

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

fun message(attributeRollOutcome: AttributeRollOutcome) = with (attributeRollOutcome)  {
    "${(isCriticalString+isSuccessString).toUpperCase()}: " +
            "A roll of ${rollOutcome.emoji()} => ${rollOutcome.total} vs " +
            "${message(modifiedAttribute)} " +
            "was a ${isCriticalString}${isSuccessString} " +
            "with a margin of ${isSuccessString} of ${margin}\n"
}



fun message(attackRollOutcome: AttackRollOutcome) = with (attackRollOutcome) {
   "'*${attackName}*' ${message(damageRollOutcome)}"
}

fun message(damageRollOutcome: DamageRollOutcome) = with (damageRollOutcome) {
        "Dealt *${totalDamage}* ${damageSpec.damageType.longForm} damage after DR:\n" +
                "This attack causes ${damageSpec.rollSpec.canonical} ${damageSpec.damageType.shortForm} vs DR ${damageResistance}.\n" +
                "Rolled ${damageSpec.rollSpec.canonical} => ${rollOutcome.emoji()} => ${rollOutcome.total}.\n" +
                "`${totalDamage} = [(${rollOutcome.total} impact damage - DR ${damageResistance}) * ${damageSpec.damageType.multiplier} " +
                "for ${damageSpec.damageType.longForm}]`\n"
}

fun message(rollOutcome: RollOutcome) = with (rollOutcome) {
    "${emoji()} => Rolled *${total}* on ${rollSpec.canonical}"
}

fun message(characterAttackRollOutcome: CharacterAttackRollOutcome) = with (characterAttackRollOutcome) {
    with (attackRollOutcome) {
        "${characterName} rolled damage for ${attackName}:\n${damageRollOutcome.toString()}"
    }
}

fun message(characterAttributeRollOutcome: CharacterAttributeRollOutcome) = with (characterAttributeRollOutcome) {
    "${characterName} rolled vs. ${message(attributeRollOutcome.modifiedAttribute)}:\n" +
            "${attributeRollOutcome.toString()}"
}

fun doRichMessage(s: String) = RichMessage(s)

fun richMessage(outcome: RollOutcome) = doRichMessage(message(outcome))
fun richMessage(outcome: DamageRollOutcome) = doRichMessage(message(outcome))
fun richMessage(outcome: AttackRollOutcome) = doRichMessage(message(outcome))
fun richMessage(outcome: AttributeRollOutcome) = doRichMessage(message(outcome))
fun richMessage(outcome: CharacterAttributeRollOutcome) = doRichMessage(message(outcome))
fun richMessage(outcome: CharacterAttackRollOutcome) = doRichMessage(message(outcome))


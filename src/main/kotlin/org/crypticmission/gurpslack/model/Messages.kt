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
*Roll:* ${actor?.plus(" ") ?: ""}Rolled vs ${modifiedAttribute}
> *- Outcome:* ${(isCriticalString+isSuccessString).toTitleCase()}${if (!isCritical) " by " + Math.abs(margin) else ""}
> *- Roll:* ${rollOutcome.emoji()} = ${rollOutcome.total}
> *- Effective Level:* ${modifiedAttribute.effectiveLevel}
> *- Margin of ${isSuccessString.toTitleCase()}:* ${Math.abs(margin)}
> *- Attribute:* ${message(modifiedAttribute.attribute)}
> *- Modifier:* ${modifiedAttribute.modifier.toSignedStringWithZero()}
""".trimIndent()
}

fun message(stat: TrackedValue) = with (stat) {
    val effect = effect()
    """
    |_*${stat.name}:*_
    |  Max ${shortName}: ${maxValue}
    |  Current ${shortName}: ${currentValue}
    |  Effects: ${effect.status}${if (effect.details != null) " (" + effect.details + ")" else ""}
    """.trimIndent().trimMargin("|")
}

fun message(characterAttackRollOutcome: CharacterAttackRollOutcome) = with (characterAttackRollOutcome) {
    message(attackRollOutcome.damageRollOutcome, attackRollOutcome.attackName, characterName)
}

fun message(attackRollOutcome: AttackRollOutcome) = with (attackRollOutcome) {
    message(damageRollOutcome, attackName)
}

fun message(damageRollOutcome: DamageRollOutcome, attackName: String? = null, actor: String? = null) =
        damageRollOutcome.multiShotMessage(attackName, actor)

private fun DamageRollOutcome.linePrefix() = (if (isMultiShot()) "\n>   • " else " ")

private fun DamageRollOutcome.multiShotMessage(attackName: String?, actor: String?) =
    messageHeader(actor, attackName) +
            multiShotPart() +
            rollDetailsPart() +
            mathPart()

private fun DamageRollOutcome.messageHeader(actor: String?, attackName: String?): String {
    return """
    |*Damage Roll:* ${actor?.plus("'s attack") ?: "Attack"}${attackName?.let { " with " + attackName } ?: ""}${if (isMultiShot()) " hits ${rollOutcomes.size} times" else ""} vs DR ${damageResistance}
    |> *- Outcome:* ${totalFinalDamage} ${damageSpec.damageType.longForm} damage after DR
    |> *- Damage:* ${damageSpec.canonical}
    |> *- DR:* ${damageResistance}
    """.trimMargin("|").trimIndent() + "\n"
}

private fun DamageRollOutcome.multiShotPart() =
    when (this.multiShotDescriptor) {
        null -> ""
        else -> with(this.multiShotDescriptor) {
            "> *- Hits:* ${hits} hits from ${shotsFired} shots fired = " +
                    "`max of (${shotsFired} or [1 hit + (made by ${marginOfSuccess} / recoil of ${recoil})])`\n"
        }
    }

private fun DamageRollOutcome.rollDetailsPart(): String  =
    "> *- Roll${if (isMultiShot()) "s" else ""}:*" +
    this.rollOutcomes.map{ rollOutcome ->
         linePrefix() + "${rollOutcome.total} = ${rollOutcome.emoji()}"
    }.joinToString("") + "\n"

private fun DamageRollOutcome.mathPart(): String =
    "> *- Details:*" + totalLine() +
    this.damageDetails.map { damageDetails ->
        linePrefix() + "`${damageDetails.finalDamage} = [(${damageDetails.rollOutcome.total} impact damage - DR ${this.damageResistance}) * ${this.damageSpec.damageType.multiplier} for ${this.damageSpec.damageType.longForm}]`"
    }.joinToString("")

private fun DamageRollOutcome.totalLine(): String =
        if (isMultiShot())
            " ${totalFinalDamage} = " + this.damageDetails
                            .map { it.finalDamage.toString() }
                            .joinToString(" + ")
        else ""

fun message(rollOutcome: RollOutcome) = with (rollOutcome) {
    "${emoji()} => Rolled *${total}* on ${rollSpec.canonical}"
}


fun message(characterAttributeRollOutcome: CharacterAttributeRollOutcome) = with (characterAttributeRollOutcome) {
    message(attributeRollOutcome, characterName)
}

fun doRichMessage(s: String) = RichMessage(s)

fun richMessage(outcome: TrackedValue) = doRichMessage(message(outcome))
fun richMessage(outcome: RollOutcome) = doRichMessage(message(outcome))
fun richMessage(outcome: DamageRollOutcome) = doRichMessage(message(outcome))
fun richMessage(outcome: AttackRollOutcome) = doRichMessage(message(outcome))
fun richMessage(outcome: AttributeRollOutcome) = doRichMessage(message(outcome))
fun richMessage(outcome: CharacterAttributeRollOutcome) = doRichMessage(message(outcome))
fun richMessage(outcome: CharacterAttackRollOutcome) = doRichMessage(message(outcome))


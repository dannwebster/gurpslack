package org.crypticmission.gurpslack.model

/**
 */
enum class DamageType(val longForm: String, val shortForm: String, val multiplier: Double) {
    cru("crushing", "cru", 1.0),
    cut("cutting", "cut", 1.5),
    imp("impaling", "imp", 2.0),
    pi_plus_plus("huge piercing", "pi++", 2.0),
    pi_plus("large piercing", "pi+", 1.5),
    pi("piercing", "pi", 1.0),
    burn("burning", "burn", 1.0);

    companion object {
        val LONG_REGEX = DamageType.values()
                .joinToString("|")
                .toRegex()

        val SHORT_REGEX = DamageType.values()
                .map { it.shortForm }
                .map { it.replace("""\+""".toRegex(), """\\+""") }
                .joinToString("|")
                .toRegex()
        fun fromLongForm(longForm: String) = DamageType.values().find { type -> type.longForm.equals(longForm) }
        fun fromShortForm(shortForm: String) = DamageType.values().find { type -> type.shortForm.equals(shortForm) }
    }
}

data class DamageRollOutcome(val damageSpec: DamageSpec, val rollOutcome: RollOutcome, val attackName: String = "attack") {
    val impactDamage =  (rollOutcome.total - damageSpec.damageResistance)
    val totalDamage = Math.floor(impactDamage * damageSpec.damageType.multiplier).toInt()

    val message =
            "${totalDamage} ${damageSpec.damageType.longForm} damage after DR: ${attackName} causes " +
            "${damageSpec.rollSpec.canonical} ${damageSpec.damageType.shortForm} vs DR ${damageSpec.damageResistance}. " +
            "Rolled ${damageSpec.rollSpec.canonical} = ${rollOutcome.total}. " +
            "[(${rollOutcome.total} impact damageSpec - DR ${damageSpec.damageResistance}) * ${damageSpec.damageType.multiplier} " +
                    "for ${damageSpec.damageType.longForm}]"

    val messageWithEmoji = "${message} (${rollOutcome.emoji()})"
    override fun toString() = message
}
package org.crypticmission.gurpslack.model

/**
 */
enum class DamageType(val longForm: String, val shortForm: String, val multiplier: Double) {
    cru("crushing", "cru", 1.0),
    cut("cutting", "cut", 1.5),
    imp("impaling", "imp", 2.0),
    pi_plus_plus("huge piercing", "pi++", 2.0),
    pi_plus("large pierceing", "pi+", 1.5),
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

data class DamageRollOutcome(val damageSpec: DamageSpec, val rollDetails: RollDetails, val attackName: String = "attack") {
    val impactDamage = Math.floor((rollDetails.total - damageSpec.damageResistance) * damageSpec.damageType.multiplier).toInt()

    val message =
            "${impactDamage} DAMAGE: ${attackName} causes " +
            "${damageSpec.rollSpec.canonical} ${damageSpec.damageType.shortForm} vs DR ${damageSpec.damageResistance}. " +
            "Rolled ${damageSpec.rollSpec.canonical} = ${rollDetails.total}. " +
            "[(${rollDetails.total} impact damageSpec - DR ${damageSpec.damageResistance}) * ${damageSpec.damageType.multiplier} " +
                    "for ${damageSpec.damageType.longForm}]"

    val messageWithEmoji = "${message} ${rollDetails.emoji()}"
    override fun toString() = message
}
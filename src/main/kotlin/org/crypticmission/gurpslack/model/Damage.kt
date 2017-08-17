package org.crypticmission.gurpslack.model

import org.crypticmission.gurpslack.repositories.Randomizer

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

fun String.dmgType() = DamageType.fromLongForm(this) ?: DamageType.fromShortForm(this) ?: DamageType.cru

fun firstValue(regex: Regex, text: String) : String? {
    val m = regex.find(text)
    m ?: return null
    val g = m.groups[0]
    g ?: return null
    return text.substring(g.range)
}

data class Attack(val attackName: String, val damageSpec: DamageSpec) {
    fun rollVsDr(damageResistance: Int, rand: Randomizer) =
            AttackRollOutcome(attackName, damageSpec.rollVsDr(damageResistance, rand))
}

data class DamageSpec(val rollSpec: RollSpec, val damageType: DamageType = DamageType.cru) {
    val canonical = "${rollSpec.canonical} ${damageType.shortForm}"
    fun rollVsDr(damageResistance: Int, rand: Randomizer): DamageRollOutcome =
            DamageRollOutcome(this, rollSpec.roll(rand), damageResistance)

}
data class AttackRollOutcome(val attackName: String, val damageRollOutcome: DamageRollOutcome)

data class DamageRollOutcome(val damageSpec: DamageSpec,
                             val rollOutcome: RollOutcome, val damageResistance: Int) {
    val impactDamage =  Math.max(rollOutcome.total - damageResistance, 0)
    val totalDamage = Math.floor(impactDamage * damageSpec.damageType.multiplier).toInt()

    override fun toString() = message(this)
}
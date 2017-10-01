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
    val impactDamage =  rollOutcome.total
    val damageAfterDr =  Math.max(rollOutcome.total - damageResistance, 0)
    val totalDamage = Math.floor(damageAfterDr * damageSpec.damageType.multiplier).toInt()

    override fun toString() = message(this)
}
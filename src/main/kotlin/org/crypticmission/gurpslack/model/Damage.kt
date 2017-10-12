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


data class Attack(val attackName: String, val damageSpec: DamageSpec, val recoil: Int = 1) {
    fun rollVsDr(damageResistance: Int, rand: Randomizer, hits: Int) =
            AttackRollOutcome(attackName, damageSpec.rollVsDr(damageResistance, rand, hits))
}

data class DamageSpec(val rollSpec: RollSpec, val damageType: DamageType = DamageType.cru) {
    val canonical = "${rollSpec.canonical} ${damageType.shortForm}"

    fun rollVsDr(damageResistance: Int, rand: Randomizer, hits:Int): DamageRollOutcome =
            DamageRollOutcome(this, (1 .. hits).map{rollSpec.roll(rand)}, damageResistance)

}
data class AttackRollOutcome(val attackName: String, val damageRollOutcome: DamageRollOutcome)

data class DamageDetails(val rollOutcome: RollOutcome, val impactDamage: Int, val damageAfterDr: Int, val finalDamage: Int)

data class DamageRollOutcome(val damageSpec: DamageSpec,
                             val rollOutcomes: List<RollOutcome>,
                             val damageResistance: Int) {
    constructor(damageSpec: DamageSpec, rollOutcome: RollOutcome, damageResistance: Int) :
            this(damageSpec, listOf(rollOutcome), damageResistance)


    val damageDetails: List<DamageDetails> = rollOutcomes.map { rollOutcome ->
        val impact = rollOutcome.total
        val afterDr = Math.max(rollOutcome.total - damageResistance, 0)
        val finalDamage = (afterDr * (damageSpec.damageType.multiplier)).toInt()

        DamageDetails(rollOutcome, impact, afterDr, finalDamage)
    }

    val totalImpactDamage: Int = damageDetails.map { it.impactDamage }.sum()
    val totalDamageAfterDr: Int = damageDetails.map { it.damageAfterDr }.sum()
    val totalFinalDamage: Int = damageDetails.map { it.finalDamage }.sum()

    fun isMultiShot() = damageDetails.size > 1
    fun only() = if (!isMultiShot()) damageDetails.first() else throw IllegalStateException("should be only 1 roll")
    override fun toString() = message(this)
}

package org.crypticmission.gurpslack.model

import org.crypticmission.gurpslack.message.message
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


data class Attack(val attackName: String, val damageSpec: DamageSpec, val recoil: Int? = null) {

    fun rollVsDr(rand: Randomizer, damageResistance: Int): AttackRollOutcome =
        AttackRollOutcome(attackName, damageSpec.rollVsDr(rand, damageResistance))

    fun rollMultiVsDr(rand: Randomizer, damageResistance: Int, shotsFired: Int, marginOfSuccess: Int): AttackRollOutcome =
        AttackRollOutcome(
                attackName,
                damageSpec.rollMultiVsDr(
                        rand,
                        damageResistance,
                        MultiShotDescriptor(
                                shotsFired,
                                recoil ?: 1,
                                marginOfSuccess
                        )
                )
        )
}

data class MultiShotDescriptor(val shotsFired: Int, val recoil: Int, val marginOfSuccess: Int) {
    val hits = calculateHits(shotsFired, marginOfSuccess, recoil)

    companion object {
        fun calculateHits(shotsFired: Int, marginOfSuccess: Int, recoil: Int) =
                if (shotsFired < 1 || marginOfSuccess < 0)
                    0
                else
                    Math.min(1 + (marginOfSuccess / recoil).toInt(), shotsFired)
    }
}

data class DamageSpec(val rollSpec: RollSpec, val damageType: DamageType = DamageType.cru) {
    val canonical = "${rollSpec.canonical} ${damageType.shortForm}"

    fun rollVsDr(rand: Randomizer, damageResistance: Int) =
            DamageRollOutcome.singleHit(rand, this, damageResistance)


    fun rollMultiVsDr(rand: Randomizer, damageResistance: Int, multiShotDescriptor: MultiShotDescriptor) =
            DamageRollOutcome.multiHit(rand, this, damageResistance, multiShotDescriptor)

    operator fun times(multiplicand: Int) = DamageSpec(rollSpec * multiplicand, damageType)

}

data class AttackRollOutcome(val attackName: String, val damageRollOutcome: DamageRollOutcome)

data class DamageDetails(val rollOutcome: RollOutcome, val impactDamage: Int, val damageAfterDr: Int, val finalDamage: Int)

data class DamageRollOutcome private constructor(val damageSpec: DamageSpec,
                             val rollOutcomes: List<RollOutcome>,
                             val damageResistance: Int,
                             val multiShotDescriptor: MultiShotDescriptor?) {

    companion object {
        fun singleHit(rand: Randomizer, damageSpec: DamageSpec, damageResistance: Int) =
            DamageRollOutcome(damageSpec, listOf(damageSpec.rollSpec.roll(rand)), damageResistance, null)

        fun multiHit(rand: Randomizer, damageSpec: DamageSpec, damageResistance: Int, multiShotDescriptor: MultiShotDescriptor): DamageRollOutcome =
                DamageRollOutcome(
                damageSpec,
                (1..multiShotDescriptor.hits).map { damageSpec.rollSpec.roll(rand) },
                damageResistance,
                multiShotDescriptor)
    }

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

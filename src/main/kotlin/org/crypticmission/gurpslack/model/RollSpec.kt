package org.crypticmission.gurpslack.model

import org.crypticmission.gurpslack.repositories.Randomizer

data class RollOutcome(val rollSpec: RollSpec, val rollValues: List<Int>, val adds: Int) {
    val total = rollValues.sum() + adds
    val message = message(this)
    override fun toString(): String = message
}

data class RollSpec(val dice: Int, val sides: Int, val adds: Int=0) {
    val max = dice * sides + adds
    val min = dice * 1 + adds

    companion object {
        val DEFAULT = RollSpec(3, 6)
    }

    val addString = if (adds == 0) "" else if (adds > 0) "+${adds}" else adds.toString()
    val canonical = "${dice}d${sides}${addString}"

    fun toDamage(type: DamageType) = DamageSpec(this, type)
    override fun toString() = canonical

    fun roll(rand: Randomizer): RollOutcome =
            RollOutcome(this,
                1.rangeTo(dice).map { rand.random(sides) },
                adds)
}


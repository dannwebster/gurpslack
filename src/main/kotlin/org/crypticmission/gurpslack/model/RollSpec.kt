package org.crypticmission.gurpslack.model

import org.crypticmission.gurpslack.repositories.Randomizer
import org.crypticmission.gurpslack.toSignedString

data class RollDetails(val rollValues: List<Int>, val adds: Int) {
    val total = rollValues.sum() + adds
    fun emoji() = this.rollValues.map{ ":d6-${it}:"}.joinToString() + this.adds.toSignedString()
}

data class RollSpec(val dice: Int, val sides: Int, val adds: Int=0) {

    val rolls: List<Int> = listOf()
    val max = dice * sides + adds
    val min = dice * 1 + adds

    companion object {
        val REGEX: Regex = """(\d*)[Dd](\d+)?([\+-]\d+)?""".toRegex()

        fun MatchResult.d(index: Int, default: Int) =
                if (this.groupValues[index] == "") default else this.groupValues[index].toInt()

        fun fromString(rollSpec: String) : RollSpec? {
            val matchResult = REGEX.matchEntire(rollSpec.trim())
            matchResult ?: return null

            val dice = matchResult.d(1, 1)
            val sides = matchResult.d(2, 6)
            val adds = matchResult.d(3, 0)

            return RollSpec(dice, sides, adds)
        }

    }

    fun modify(mod: Int) = RollSpec(dice, sides, adds+mod)

    val addString = if (adds == 0) "" else if (adds > 0) "+${adds}" else adds.toString()
    val canonical = "${dice}d${sides}${addString}"

    fun toDamage(type: DamageType) = DamageSpec(this, type)
    override fun toString() = canonical

    fun roll(rand: Randomizer): RollDetails =
            RollDetails(
                1.rangeTo(dice).map { rand.random(sides) },
                adds)
}


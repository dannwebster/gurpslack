package org.crypticmission.gurpslack.model

import org.crypticmission.gurpslack.repositories.Randomizer

data class RollSpec(val dice: Int, val sides: Int, val adds: Int=0) {

    val max = dice * sides + adds
    val min = dice * 1 + adds

    companion object {
        val REGEX: Regex = """(\d*)[Dd](\d+)?([\+-]\d+)?""".toRegex()

        fun MatchResult.d(index: Int, default: Int) =
                if (this.groupValues[index] == "") default else this.groupValues[index].toInt()

        fun spec(rollSpec: String) : RollSpec {
            val matchResult = REGEX.matchEntire(rollSpec.trim())
            matchResult ?: throw IllegalArgumentException("'${rollSpec}' is not a valid roll specification")

            val dice = matchResult.d(1, 1)
            val sides = matchResult.d(2, 6)
            val adds = matchResult.d(3, 0)

            return RollSpec(dice, sides, adds)
        }

    }

    fun modify(mod: Int) = RollSpec(dice, sides, adds+mod)

    val addString = if (adds == 0) "" else if (adds > 0) "+${adds}" else adds.toString()
    val canonical = "${dice}d${sides}${addString}"

    override fun toString() = canonical

    fun roll(rand: Randomizer) =
            1.rangeTo(dice)
                    .map { rand.random(sides) }
                    .sum() + adds
}

package org.crypticmission.gurpslack.commands

import org.crypticmission.gurpslack.util.Randomizer

class RollSpec(val dice: Int, val sides: Int, val adds: Int=0) {

    val max = dice * sides + adds
    val min = dice * 1 + adds

    companion object {
        val regex: Regex = """(\d*)[Dd](\d+)([\+-]\d+)?""".toRegex()

        fun MatchResult.d(index: Int, default: Int) =
                if (this.groupValues[index] == "") default else this.groupValues[index].toInt()

        fun spec(rollSpec: String) : RollSpec {
            val matchResult = regex.matchEntire(rollSpec.trim())
            matchResult ?: throw IllegalArgumentException("'${rollSpec}' is not a valid roll specification")

            val dice = matchResult.d(1, 1)
            val sides = matchResult.d(2, 6)
            val adds = matchResult.d(3, 0)

            return RollSpec(dice, sides, adds)
        }

    }

    fun modify(mod: Int) = RollSpec(dice, sides, adds+mod)

    fun roll(rand: Randomizer) =
            1.rangeTo(dice)
                    .map { rand.random(sides) }
                    .sum() + adds
}

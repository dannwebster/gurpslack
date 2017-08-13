package org.crypticmission.gurpslack.model

import org.crypticmission.gurpslack.repositories.Randomizer

fun String.dmgType() = DamageType.fromLongForm(this) ?: DamageType.fromShortForm(this) ?: DamageType.cru

fun firstValue(regex: Regex, text: String) : String? {
    val m = regex.find(text)
    m ?: return null
    val g = m.groups[0]
    g ?: return null
    return text.substring(g.range)

}

data class DamageSpec(val rollSpec: RollSpec, val damageType: DamageType = DamageType.cru, val damageResistance: Int = 0) {

    val canonical = "${rollSpec.canonical} ${damageType.shortForm}"

    companion object {
        val DR_REGEX = """vs\.?\s*DR\s*:?\s*(\d+)""".toRegex(RegexOption.IGNORE_CASE)

        fun fromString(damageSpec: String): DamageSpec? {
            val rollSpecString = firstValue(RollSpec.REGEX, damageSpec) ?: return null
            val damageTypeString = firstValue(DamageType.SHORT_REGEX, damageSpec)
            val damageType = damageTypeString?.dmgType() ?: DamageType.cru
            val dr= DR_REGEX.find(damageSpec)?.groupValues?.get(1)?.toInt() ?: 0

            return RollSpec.fromString(rollSpecString)?.
                    toDamage(damageType)?.
                    vsDr(dr)
        }
    }

    fun vsDr(dr: Int) = DamageSpec(this.rollSpec, this.damageType, this.damageResistance + dr)
    fun roll(rand: Randomizer): DamageRollOutcome =
            DamageRollOutcome(this, rollSpec.roll(rand))

    fun roll(rand: Randomizer, attackName: String): DamageRollOutcome =
            DamageRollOutcome(this, rollSpec.roll(rand), attackName)
}
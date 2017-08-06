package org.crypticmission.gurpslack.commands

import org.crypticmission.gurpslack.util.Randomizer

interface Rollable {
    val name: String
    val rollSpec: RollSpec
    fun modify(modifier: Int): Rollable
    fun roll(randomizer: Randomizer) : Int = this.rollSpec.roll(randomizer)
}

data class Damage(override val name: String, override val rollSpec: RollSpec = Attribute.ROLL_SPEC) : Rollable {
    override fun modify(modifier: Int) = Damage(name, rollSpec.modify(modifier))
}

data class Attribute(override val name: String, val value: Int, override val rollSpec: RollSpec = Attribute.ROLL_SPEC)  : Rollable {

    override fun modify(modifier: Int) = ModifiedAttribute(this, modifier)

    companion object {
        val ROLL_SPEC = RollSpec(3, 6)
    }

    override fun toString(): String = "${name}: ${value}"
}

data class ModifiedAttribute(val attribute: Attribute, val modifier: Int=0) : Rollable{

    override val rollSpec get() = attribute.rollSpec
    val value = attribute.value + modifier
    val modString = if (modifier == 0) "" else if (modifier < 0) "-${-1*modifier}" else "+${modifier}"
    override val name = "${attribute.name}${modString}"

    override fun modify(modifier: Int) = ModifiedAttribute(this.attribute, this.modifier+modifier)

    fun rollVs(randomizer: Randomizer) = RollOutcome(this, this.roll(randomizer))
    override fun toString() = "${name} (${value})"
}

data class RollOutcome(val modifiedAttribute: ModifiedAttribute, val roll: Int) {
    val isSuccess = roll <= modifiedAttribute.value
    val margin = Math.abs((roll - modifiedAttribute.value))
    val isCritical =
            roll == 3 ||
                    roll == 4 ||
                    roll == 17 ||
                    roll == 18 ||
                    margin >= 10

    val isCriticalString = if (isCritical) "critical " else ""
    val isSuccessString = if (isSuccess) "success" else "failure"

    override fun toString() = "${(isCriticalString+isSuccessString).toUpperCase()}: " +
            "A roll of ${roll} vs ${modifiedAttribute} was a ${isCriticalString}${isSuccessString} " +
            "with a margin of ${isSuccessString} of ${margin}"

}

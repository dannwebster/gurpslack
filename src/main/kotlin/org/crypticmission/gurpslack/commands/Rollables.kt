package org.crypticmission.gurpslack.commands

import org.crypticmission.gurpslack.util.Randomizer

interface Rollable {
    val name: String
    val rollSpec: RollSpec
    fun modify(modifier: Int): Rollable
    fun roll(randomizer: Randomizer) : Int = this.rollSpec.roll(randomizer)
    fun rollVs(randomizer: Randomizer) : Outcome
}

enum class DamageType(name: String, multiplier: Double) {
    cru("crushing", 1.0),
    cut("cutting", 1.5),
    imp("impaling", 2.0),
    pi("piercing", 1.0),
    pi_plus("large pierceing", 1.5),
    pi_plus_plus("huge piercing", 2.0),
    burn("burning", 1.0);

    companion object {
        fun fromName(name: String) = DamageType.values().find { type -> type.name.equals(name) }
    }
}

data class Damage(override val name: String, override val rollSpec: RollSpec, val type: DamageType = DamageType.cru) : Rollable {
    override fun modify(modifier: Int) = Damage(name, rollSpec.modify(modifier))
    override fun rollVs(randomizer: Randomizer) = DamageRollOutcome(this.modify(0), rollSpec.roll(randomizer))
}

data class Attribute(override val name: String, val value: Int, override val rollSpec: RollSpec = Attribute.ROLL_SPEC)  : Rollable {

    override fun modify(modifier: Int) = ModifiedAttribute(this, modifier)
    override fun rollVs(randomizer: Randomizer) = AttributeRollOutcome(this.modify(0), rollSpec.roll(randomizer))

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

    override fun rollVs(randomizer: Randomizer) = AttributeRollOutcome(this, this.roll(randomizer))
    override fun toString() = "${name} (${value})"
}

interface Outcome {
    val message: String
}

data class DamageRollOutcome(val damage: Damage, val roll: Int) : Outcome{
    override val message =""
    override fun toString() = message
}

data class AttributeRollOutcome(val modifiedAttribute: ModifiedAttribute, val roll: Int) : Outcome {
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

    override val message = "${(isCriticalString+isSuccessString).toUpperCase()}: " +
            "A roll of ${roll} vs ${modifiedAttribute} was a ${isCriticalString}${isSuccessString} " +
            "with a margin of ${isSuccessString} of ${margin}"

    override fun toString() = ""
}

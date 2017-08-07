package org.crypticmission.gurpslack.commands

import org.crypticmission.gurpslack.util.Randomizer

interface Rollable {
    val name: String
    val rollSpec: RollSpec
    fun modify(modifier: Int): Rollable
    fun roll(randomizer: Randomizer) : Int = this.rollSpec.roll(randomizer)
    fun rollVs(randomizer: Randomizer, modifier: Int=0) : Outcome
}

enum class DamageType(val longForm: String, val shortForm: String, val multiplier: Double) {
    cru("crushing", "cru", 1.0),
    cut("cutting", "cut", 1.5),
    imp("impaling", "imp", 2.0),
    pi("piercing", "pi", 1.0),
    pi_plus("large pierceing", "pi+", 1.5),
    pi_plus_plus("huge piercing", "pi++", 2.0),
    burn("burning", "burn", 1.0);

    companion object {
        fun fromLongForm(longForm: String) = DamageType.values().find { type -> type.longForm.equals(longForm) }
        fun fromShortForm(shortForm: String) = DamageType.values().find { type -> type.shortForm.equals(shortForm) }
    }
}

data class Damage(override val name: String, override val rollSpec: RollSpec, val type: DamageType) : Rollable {
    override fun modify(modifier: Int) = Damage(name, rollSpec.modify(modifier), type)
    override fun rollVs(randomizer: Randomizer, dr: Int) = DamageRollOutcome(this, rollSpec.roll(randomizer), dr)
}

data class Attribute(override val name: String, val value: Int, override val rollSpec: RollSpec = Attribute.ROLL_SPEC)  : Rollable {

    companion object {
        val ROLL_SPEC = RollSpec(3, 6)
    }
    override fun modify(modifier: Int) = ModifiedAttribute(this, modifier)
    override fun rollVs(randomizer: Randomizer, modifier: Int) = AttributeRollOutcome(this.modify(modifier), rollSpec.roll(randomizer))
    override fun toString(): String = "${name}: ${value}"
}

data class ModifiedAttribute(val attribute: Attribute, val modifier: Int=0) : Rollable{

    override val rollSpec get() = attribute.rollSpec
    val value = attribute.value + modifier
    val modString = if (modifier == 0) "" else if (modifier < 0) "-${-1*modifier}" else "+${modifier}"
    override val name = "${attribute.name}${modString}"

    override fun modify(modifier: Int) = ModifiedAttribute(this.attribute, this.modifier+modifier)

    override fun rollVs(randomizer: Randomizer, modifier: Int) =
            AttributeRollOutcome(this.modify(modifier), this.roll(randomizer))

    override fun toString() = "${name} (${value})"
}

interface Outcome {
    val message: String
}

data class DamageRollOutcome(val damage: Damage, val roll: Int, val dr: Int) : Outcome{
    val amount = Math.floor((roll - dr) * damage.type.multiplier).toInt()

    override val message =
            "${amount} DAMAGE: ${damage.name} causes " +
            "${damage.rollSpec.canonical} ${damage.type.shortForm} vs DR ${dr}. " +
            "Rolled ${damage.rollSpec.canonical} = ${roll}. " +
            "[(${roll} impact damage - DR ${dr}) * ${damage.type.multiplier} for ${damage.type.longForm}]"

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

    override fun toString() = message
}

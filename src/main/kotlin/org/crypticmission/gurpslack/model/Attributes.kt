package org.crypticmission.gurpslack.model

import org.crypticmission.gurpslack.repositories.Randomizer

data class Attribute(val name: String, val level: Int, val modifier: Int=0, val rollSpec: RollSpec = Attribute.ROLL_SPEC)  {

    companion object {
        val ROLL_SPEC = RollSpec(3, 6)
    }
    val effectiveLevel = level + modifier
    val modString = if (modifier == 0) "" else if (modifier < 0) "-${-1*modifier}" else "+${modifier}"
    val effectiveName = "${name}${modString}"
    val message = "${name}: ${level}"
    val messageWithModifiers = "${effectiveName}: ${effectiveLevel}"

    fun modify(modifier: Int) = Attribute(this.name, this.level, this.modifier + modifier, this.rollSpec)
    fun roll(randomizer: Randomizer) =
            AttributeRollOutcome(this, this.rollSpec.roll(randomizer))
    override fun toString(): String = message
    fun toStringWithModifiers(): String = messageWithModifiers

}

data class AttributeRollOutcome(val attribute: Attribute, val rollOutcome: RollOutcome) {
    val isSuccess = rollOutcome.total <= attribute.effectiveLevel
    val margin = Math.abs(rollOutcome.total - attribute.effectiveLevel)
    val isCritical = with(rollOutcome) {
        total == 3 ||
        total == 4 ||
        total == 17 ||
        total == 18 ||
        margin >= 10
    }

    val isCriticalString = if (isCritical) "critical " else ""
    val isSuccessString = if (isSuccess) "success" else "failure"

    override fun toString() = message(this)

}

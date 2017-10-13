package org.crypticmission.gurpslack.model

import org.crypticmission.gurpslack.message.message
import org.crypticmission.gurpslack.repositories.Randomizer

data class Attribute(val name: String, val level: Int, val rollSpec: RollSpec = Attribute.ROLL_SPEC)  {

    companion object {
        val ROLL_SPEC = RollSpec(3, 6)
    }

    fun modify(modifier: Int = 0) = ModifiedAttribute(this, modifier)
    fun rollWithModifier(modifier: Int, randomizer: Randomizer) =
            AttributeRollOutcome(this.modify(modifier), this.rollSpec.roll(randomizer))
    override fun toString(): String = message(this)
}

data class ModifiedAttribute(val attribute: Attribute, val modifier: Int = 0) {
    val effectiveLevel = attribute.level + modifier
    fun modify(modifier: Int = 0) = this.copy(modifier = this.modifier + modifier)
    fun rollWithModifier(modifier: Int, randomizer: Randomizer) =
            AttributeRollOutcome(this.modify(modifier), this.attribute.rollSpec.roll(randomizer))
    override fun toString(): String = message(this)
}

data class AttributeRollOutcome(val modifiedAttribute: ModifiedAttribute, val rollOutcome: RollOutcome) {
    val isSuccess = rollOutcome.total <= modifiedAttribute.effectiveLevel
    val margin = Math.abs(rollOutcome.total - modifiedAttribute.effectiveLevel)
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

package org.crypticmission.gurpslack.model

import org.crypticmission.gurpslack.repositories.Randomizer

data class CharacterAttributeRollOutcome(val characterName: String, val attributeRollOutcome: AttributeRollOutcome) {
    override fun toString() = message(this)
}

data class CharacterAttackRollOutcome(val characterName: String, val attackRollOutcome: AttackRollOutcome) {
    override fun toString() = message(this)
}

class Character(val characterName: String,
                val randomizer: Randomizer = Randomizer.system(),
                attributes: Map<String, Attribute> = emptyMap(),
                skills: Map<String, Attribute> = emptyMap(),
                attacks: Map<String, Attack> = emptyMap()) {

    val attributes = HashMap<String, Attribute>(attributes)
    val skills = HashMap<String, Attribute>(skills)
    val attacks = HashMap<String, Attack>(attacks)

    companion object {
        val DEFAULT_ATTIRIBUTE_VALUE = 10
        val DEFAULT_DAMAGE_ROLL_SPEC = RollSpec(1, 6)
        val DEFAULT_DAMAGE_ROLL_TYPE = DamageType.cru
        val DEFAULT_DAMAGE_SPEC = DamageSpec(DEFAULT_DAMAGE_ROLL_SPEC, DEFAULT_DAMAGE_ROLL_TYPE)
    }

    override fun toString() = message(this)

    fun rollVsSkill(name: String, modifier: Int): CharacterAttributeRollOutcome =
            CharacterAttributeRollOutcome(characterName, getSkill(name).rollWithModifier(modifier, randomizer))

    fun rollVsAttribute(name: String, modifier: Int): CharacterAttributeRollOutcome =
            CharacterAttributeRollOutcome(
                    this.characterName, getAttribute(name).rollWithModifier(modifier, randomizer))

    fun rollAttackDamage(attackName: String, damageResistance: Int): CharacterAttackRollOutcome =
            CharacterAttackRollOutcome(characterName, getAttack(attackName).rollVsDr(damageResistance, randomizer))

    fun getSkill(name: String) = skills.getOrPut(name.toKey()) {Attribute(name, DEFAULT_ATTIRIBUTE_VALUE)}
    fun getAttribute(name: String) = attributes.getOrPut(name.toKey()) {Attribute(name, DEFAULT_ATTIRIBUTE_VALUE)}
    fun getAttack(name: String) = attacks.getOrPut(name.toKey()) { Attack(name, DEFAULT_DAMAGE_SPEC) }

    fun addAttack(newAttack: Attack)  = addAttacks(listOf(newAttack))
    fun addAttribute(newAttribute: Attribute) = addAttributes(listOf(newAttribute))
    fun addSkill(newSkill: Attribute) = addSkill(listOf(newSkill))

    fun addAttacks(newAttacks: Iterable<Attack>) =
            newAttacks.forEach { attack -> attacks[attack.attackName.toKey()] = attack }

    fun addAttributes(newAttributes: Iterable<Attribute>) =
            newAttributes.forEach { attribute -> attributes[attribute.name.toKey()] = attribute }

    fun addSkill(newSkills: Iterable<Attribute>) =
            newSkills.forEach { skill -> skills[skill.name.toKey()] = skill }
}
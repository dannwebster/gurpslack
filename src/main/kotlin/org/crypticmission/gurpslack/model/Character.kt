package org.crypticmission.gurpslack.model

import org.crypticmission.gurpslack.repositories.Randomizer

data class CharacterAttributeRollOutcome(val characterName: String, val attributeRollOutcome: AttributeRollOutcome) {
    override fun toString() = message(this)
}

data class CharacterAttackRollOutcome(val characterName: String, val attackRollOutcome: AttackRollOutcome) {
    override fun toString() = message(this)
}

fun String.toKey() = this.toUpperCase()

class Character(val characterName: String,
                val randomizer: Randomizer = Randomizer.system(),
                attributes: Map<String, Attribute> = emptyMap(),
                attacks: Map<String, Attack> = emptyMap()) {

    val attributes = HashMap<String, Attribute>(attributes)
    val attacks = HashMap<String, Attack>(attacks)

    companion object {
        val DEFAULT_ATTIRIBUTE_VALUE = 10
        val DEFAULT_DAMAGE_ROLL_SPEC = RollSpec(1, 6)
        val DEFAULT_DAMAGE_ROLL_TYPE = DamageType.cru
        val REGEX: Regex = """(\w+)([\+-]\d+)?""".toRegex()
    }

    override fun toString() = message(this)

    fun rollVsAttribute(name: String, modifier: Int = 0): CharacterAttributeRollOutcome =
            CharacterAttributeRollOutcome(
                    this.characterName,
                    attributes.getOrPut(name)
                    {Attribute(name, DEFAULT_ATTIRIBUTE_VALUE)}
                            .modify(modifier)
                            .roll(randomizer)
            )

    fun rollAttackDamage(attackName: String, damageResistance: Int = 0): CharacterAttackRollOutcome =
            CharacterAttackRollOutcome(
                    this.characterName,
                    attacks.getOrPut(attackName)
                    {Attack(attackName, DamageSpec(DEFAULT_DAMAGE_ROLL_SPEC, DEFAULT_DAMAGE_ROLL_TYPE))}
                            .rollVsDr(damageResistance, randomizer)
            )

    fun addAttack(newAttack: Attack)  = addAttacks(listOf(newAttack))
    fun addAttribute(newAttribute: Attribute) = addAttributes(listOf(newAttribute))

    fun addAttacks(newAttacks: Iterable<Attack>) =
        newAttacks.forEach { attack -> attacks[attack.attackName.toKey()] = attack }

    fun addAttributes(newAttributes: Iterable<Attribute>) =
            newAttributes.forEach { attribute -> attributes[attribute.name.toKey()] = attribute }
}
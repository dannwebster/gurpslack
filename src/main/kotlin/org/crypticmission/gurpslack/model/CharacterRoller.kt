package org.crypticmission.gurpslack.model

import org.crypticmission.gurpslack.repositories.Randomizer

data class CharacterAttributeRollOutcome(val characterName: String, val attributeRollOutcome: AttributeRollOutcome) {
    override fun toString() = message(this)
}

data class CharacterAttackRollOutcome(val characterName: String, val attackRollOutcome: AttackRollOutcome) {
    override fun toString() = message(this)
}

class CharacterRoller(val randomizer: Randomizer = Randomizer.system(),
                      val characterName: String,
                      attributes: Map<String, Attribute> = emptyMap(),
                      skills: Map<String, Attribute> = emptyMap(),
                      attacks: Map<String, Attack> = emptyMap()) {

    val attributes = HashMap<String, Attribute>(attributes.mapKeys { (k, _) -> k.toKey() })
    val skills = HashMap<String, Attribute>(skills.mapKeys { (k, _) -> k.toKey() })
    val attacks = HashMap<String, Attack>(attacks.mapKeys { (k, _) -> k.toKey() })

    fun rollVsSkill(name: String, modifier: Int): CharacterAttributeRollOutcome? {
        val skill = getSkill(name)
        return when (skill) {
            null -> null
            else -> CharacterAttributeRollOutcome(characterName, skill.rollWithModifier(modifier, randomizer))
        }
    }

    fun rollVsAttribute(name: String, modifier: Int): CharacterAttributeRollOutcome? {
        val attribute = getAttribute(name)
        return when (attribute) {
            null -> null
            else -> CharacterAttributeRollOutcome(characterName, attribute.rollWithModifier(modifier, randomizer))
        }
    }

    fun rollAttackDamage(attackName: String, damageResistance: Int): CharacterAttackRollOutcome? {
        val attack = getAttack(attackName)
        return when (attack) {
            null -> null
            else -> CharacterAttackRollOutcome(characterName, attack.rollVsDr(damageResistance, randomizer))
        }
    }

    fun getSkill(name: String) = skills[name.toKey()]
    fun getAttribute(name: String) = attributes[name.toKey()]
    fun getAttack(name: String) = attacks[name.toKey()]

    fun addAttack(newAttack: Attack)  = addAttacks(listOf(newAttack))
    fun addAttribute(newAttribute: Attribute) = addAttributes(listOf(newAttribute))
    fun addSkill(newSkill: Attribute) = addSkill(listOf(newSkill))

    fun String.isPrimary() = (this == "ht" || this == "st" || this == "iq" || this == "dx")

    fun primaryAttributes() = attributes.filterKeys { it.isPrimary() }
    fun derivedAttributes() = attributes.filterKeys { !it.isPrimary() }

    fun addAttacks(newAttacks: Iterable<Attack>) =
            newAttacks.forEach { attack -> attacks[attack.attackName.toKey()] = attack }

    fun addAttributes(newAttributes: Iterable<Attribute>) =
            newAttributes.forEach { attribute -> attributes[attribute.name.toKey()] = attribute }

    fun addSkill(newSkills: Iterable<Attribute>) =
            newSkills.forEach { skill -> skills[skill.name.toKey()] = skill }
}
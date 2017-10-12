package org.crypticmission.gurpslack.model

import org.crypticmission.gurpslack.repositories.Randomizer

data class CharacterAttributeRollOutcome(val characterName: String, val attributeRollOutcome: AttributeRollOutcome) {
    override fun toString() = message(this)
}

data class CharacterAttackRollOutcome(val characterName: String, val attackRollOutcome: AttackRollOutcome) {
    override fun toString() = message(this)
}

data class TrackedValue(val name: String, val max: Int, var current: Int) {
    fun change(amount: Int) { current = Math.min(max, current + amount)}
}

class CharacterRoller(val randomizer: Randomizer = Randomizer.system(),
                      val characterName: String,
                      attributes: Map<String, Attribute> = emptyMap(),
                      skills: Map<String, Attribute> = emptyMap(),
                      meleeAttacks: Map<String, Attack> = emptyMap(),
                      rangedAttacks: Map<String, Attack> = emptyMap()) {

    val attributes = HashMap<String, Attribute>(attributes.mapKeys { (k, _) -> k.toKey() })
    val skills = HashMap<String, Attribute>(skills.mapKeys { (k, _) -> k.toKey() })
    val meleeAttacks = HashMap<String, Attack>(meleeAttacks.mapKeys { (k, _) -> k.toKey() })
    val rangedAttacks = HashMap<String, Attack>(rangedAttacks.mapKeys { (k, _) -> k.toKey() })
//    val trackedStats = trackedValues.map { Pair(it.name, it) }.toMap()

//    fun modifyTrackedStat(statName: String, change: Int) {
//        trackedStats.get(statName)?.change(change)
//    }

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

    fun rollMeleeAttackDamage(attackName: String, damageResistance: Int): CharacterAttackRollOutcome? {
        val attack = getMeleeAttack(attackName)
        return when (attack) {
            null -> null
            else -> CharacterAttackRollOutcome(characterName, attack.rollVsDr(randomizer, damageResistance))
        }
    }

    fun rollRangedAttackDamage(attackName: String, damageResistance: Int, shotsFired: Int = 1, marginOfSuccess: Int = 0): CharacterAttackRollOutcome? {
        val attack = getRangedAttack(attackName)

        return when (attack) {
            null -> null
            else -> {
                CharacterAttackRollOutcome(characterName, attack.rollMultiVsDr(randomizer, damageResistance, shotsFired, marginOfSuccess))
            }
        }
    }


    fun getSkill(name: String) = skills[name.toKey()]
    fun getAttribute(name: String) = attributes[name.toKey()]
    fun getMeleeAttack(name: String) = meleeAttacks[name.toKey()]
    fun getRangedAttack(name: String) = rangedAttacks[name.toKey()]

    fun addMeleeAttack(newAttack: Attack)  = addMeleeAttacks(listOf(newAttack))
    fun addRangedAttack(newAttack: Attack)  = addRangedAttacks(listOf(newAttack))
    fun addAttribute(newAttribute: Attribute) = addAttributes(listOf(newAttribute))
    fun addSkill(newSkill: Attribute) = addSkill(listOf(newSkill))

    fun String.isPrimary() = (this == "ht" || this == "st" || this == "iq" || this == "dx")

    fun primaryAttributes() = attributes.filterKeys { it.isPrimary() }.values
    fun derivedAttributes() = attributes.filterKeys { !it.isPrimary() }.values

    fun addMeleeAttacks(newAttacks: Iterable<Attack>) =
            newAttacks.forEach { attack -> meleeAttacks[attack.attackName.toKey()] = attack }

    fun addRangedAttacks(newAttacks: Iterable<Attack>) =
            newAttacks.forEach { attack -> rangedAttacks[attack.attackName.toKey()] = attack }

    fun addAttributes(newAttributes: Iterable<Attribute>) =
            newAttributes.forEach { attribute -> attributes[attribute.name.toKey()] = attribute }

    fun addSkill(newSkills: Iterable<Attribute>) =
            newSkills.forEach { skill -> skills[skill.name.toKey()] = skill }
}




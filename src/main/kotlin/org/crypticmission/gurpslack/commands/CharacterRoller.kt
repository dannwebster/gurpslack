package org.crypticmission.gurpslack.commands

import org.crypticmission.gurpslack.commands.RollSpec.Companion.d
import org.crypticmission.gurpslack.util.Randomizer

class CharacterRoller(val characterName: String,
                      val randomizer: Randomizer = Randomizer.system(),
                      attributes: Map<String, Attribute> = emptyMap(),
                      damages: Map<String, Damage> = emptyMap()) {

    val attributes = HashMap<String, Attribute>(attributes)
    val damages = HashMap<String, Damage>(damages)

    companion object {
        val DEFAULT_ATTIRIBUTE_VALUE = 10
        val DEFAULT_DAMAGE_ROLL_SPEC = RollSpec(1, 6)
        val REGEX: Regex = """(\w+)([\+-]\d+)?""".toRegex()
    }

    fun rollVsModifiedAttribute(attributeSpec: String, modifier: Int = 0): AttributeRollOutcome {
        val matchResult = REGEX.matchEntire(attributeSpec.trim())
        matchResult ?: throw IllegalArgumentException("'${attributeSpec}' is not a valid attribute specification")

        val name = matchResult.groupValues[1]
        val specModifier = matchResult.d(2, 0)
        return rollVsAttribute(name, modifier+specModifier)
    }

    fun rollVsAttribute(name: String, modifier: Int = 0): AttributeRollOutcome =
        attributes.getOrPut(name)
            {Attribute(name, DEFAULT_ATTIRIBUTE_VALUE)}
                .modify(modifier)
                .rollVs(randomizer)

    fun rollDamage(name: String, modifier: Int = 0): DamageRollOutcome =
        damages.getOrPut(name)
            {Damage(name, DEFAULT_DAMAGE_ROLL_SPEC)}
                .modify(modifier)
                .rollVs(randomizer)

    fun addDamage(newDamage: Damage) = addDamages(arrayOf(newDamage)) //replace with varArg
    fun addAttribute(newAttribute: Attribute) = addAttributes(arrayOf(newAttribute)) //replace with varArg

    fun addDamages(newDamages: Array<Damage>) =
            newDamages.forEach { damage -> damages[damage.name] = damage }

    fun addAttributes(newAttributes: Array<Attribute>) =
            newAttributes.forEach { attribute -> attributes[attribute.name] = attribute }
}
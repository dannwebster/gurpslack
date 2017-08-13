package org.crypticmission.gurpslack.model

import org.crypticmission.gurpslack.model.RollSpec.Companion.d
import org.crypticmission.gurpslack.repositories.Randomizer

class CharacterRoller(val characterName: String,
                      val randomizer: Randomizer = Randomizer.system(),
                      attributes: Map<String, Attribute> = emptyMap(),
                      damages: Map<String, DamageSpec> = emptyMap()) {

    val attributes = HashMap<String, Attribute>(attributes)
    val damages = HashMap<String, DamageSpec>(damages)

    companion object {
        val DEFAULT_ATTIRIBUTE_VALUE = 10
        val DEFAULT_DAMAGE_ROLL_SPEC = RollSpec(1, 6)
        val DEFAULT_DAMAGE_ROLL_TYPE = DamageType.cru
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
                .roll(randomizer)

    fun rollDamage(attackName: String, dr: Int = 0): DamageRollOutcome =
            damages.getOrPut(attackName)
            {DamageSpec(DEFAULT_DAMAGE_ROLL_SPEC, DEFAULT_DAMAGE_ROLL_TYPE)}
                    .vsDr(dr)
                    .roll(randomizer, attackName)

    fun addDamage(name: String, newDamage: DamageSpec) { damages[name] = newDamage }
    fun addAttribute(newAttribute: Attribute) = addAttributes(arrayOf(newAttribute)) //replace with varArg

    fun addDamages(newDamages: Map<String, DamageSpec>) = damages.putAll(newDamages)

    fun addAttributes(newAttributes: Array<Attribute>) =
            newAttributes.forEach { attribute -> attributes[attribute.name] = attribute }
}
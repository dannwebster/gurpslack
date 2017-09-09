package org.crypticmission.gurpslack.loader

import org.crypticmission.gurpslack.model.*
import org.crypticmission.gurpslack.repositories.Randomizer
import org.jdom2.input.SAXBuilder
import org.jonnyzzz.kotlin.xml.bind.*
import org.jonnyzzz.kotlin.xml.bind.jdom.JDOM
import org.jonnyzzz.kotlin.xml.bind.jdom.JXML
import org.springframework.stereotype.Component
import java.io.Reader

/**
<HP>1</HP>
<FP>1</FP>
<total_points>358</total_points>
<ST>10</ST>
<DX>13</DX>
<IQ>14</IQ>
<HT>11</HT>
<will>5</will>
<perception>2</perception>
<speed>0</speed>
<move>0</move>
 */


class SkillData {
    val name by JXML / "name" / XText
    val specialization by JXML / "specialization" / XText
    val techLevel by JXML / "tech_level" / XText
    val difficultyS by JXML / "difficulty" / XText
    val pointsS by JXML / "points" / XText
    val baseAttribute : String by lazy { difficultyS?.substringBefore("/")  ?: throw IllegalArgumentException("can't parse difficulty ${difficultyS} for skill ${name}")}
    val difficulty : String by lazy { difficultyS?.substringAfter("/") ?: throw IllegalArgumentException("can't parse difficulty ${difficultyS} for skill ${name}")}
    val points : Int by lazy { pointsS?.toInt() ?: 0 }

    val fullName : String by lazy { name +
            when (techLevel) {
                null -> ""
                "" -> ""
                else -> "/TL${techLevel}"
            }  +
            when (specialization) {
                null -> ""
                "" -> ""
                else -> " (${specialization})"
            }
    }

    fun toAttribute(characterData: CharacterData) =
        Attribute(fullName, getLevel(characterData, baseAttribute, difficulty, points))

}

fun parseAttack(description: String, usage: String?, damage: String?, thrust: RollSpec, swing: RollSpec): Attack =
        damage?.let {
            val name = description + (usage?.let { " (${usage})" } ?: "")
            if (damage.startsWith("sw")) {
                val mods = damage.substringAfter("sw").substringBefore(" ").toIntOrNull() ?: 0
                val type = parseDamageType(damage.substringAfter(" "))
                Attack(name, DamageSpec(swing.modify(mods), type))
            } else if (damage.startsWith("thr")) {
                val mods = damage.substringAfter("thr").substringBefore(" ").toIntOrNull() ?: 0
                val type = parseDamageType(damage.substringAfter(" "))
                Attack(name, DamageSpec(thrust.modify(mods), type))
            } else
                damage.let { parseDamage(it)?.let { Attack(name, it) } } ?:
                        throw IllegalStateException("unable to create ranged attack ${name} for ${damage}")
        } ?: throw IllegalStateException("null damage string value")

class MeleeAttackData {
    val damage by JXML / "damage" / XText
    val minSt by JXML / "strength" / XText
    val usage by JXML / "usage" / XText
    val reach by JXML / "reach" / XText
    val parry by JXML / "parry" / XText
    val block by JXML / "block" / XText

    fun toAttack(description: String, thrust: RollSpec, swing: RollSpec): Attack =
            parseAttack(description, this.usage, this.damage, thrust, swing)
}

class RangedAttackData {
    val damage by JXML / "damage" / XText
    val minSt by JXML / "strength" / XText
    val accuracyText by JXML / "accuracy" / XText
    val range by JXML / "range" / XText
    val rateOfFire by JXML / "rate_of_fire" / XText
    val shots by JXML / "shots" / XText
    val bulk by JXML / "bulk" / XText
    val recoil by JXML / "recoil" / XText
    fun toAttack(description: String, thrust: RollSpec, swing: RollSpec): Attack =
        parseAttack(description, null, this.damage, thrust, swing)

}

class Equipment {
    val description by JXML / "description" / XText
    val rangedAttackData by JXML / XElements("ranged_weapon") / XSub(RangedAttackData::class.java)
    val meleeAttackData by JXML / XElements("melee_weapon") / XSub(MeleeAttackData::class.java)
}

class CharacterData {
    val name by JXML / "profile" / "name" / XText
    val playerName by JXML / "profile" / "player_name" / XText
    val skillData by JXML / "skill_list" / XElements("skill") / XSub(SkillData::class.java)
    val equipment by JXML / "equipment_list" / XElements("equipment") / XSub(Equipment::class.java)
    val containedEquipment by JXML / "equipment_list" / "equipment_container" / XElements("equipment") / XSub(Equipment::class.java)
//    val containedEquipment by JXML / "equipment_list" / "equipment_container" / XElements("equipment") / XSub(Equipment::class.java)
//    val equipment : List<Equipment> by lazy { (uncontainedEquipment ?: emptyList()) + (containedEquipment ?: emptyList()) }

    val stS by JXML / "ST" / XText
    val dxS by JXML / "DX" / XText
    val iqS by JXML / "IQ" / XText
    val htS by JXML / "HT" / XText
    val willS by JXML / "will" / XText
    val perS by JXML / "perception" / XText

    val st: Int by lazy { stS?.toInt() ?: throw IllegalStateException("no value for ST") }
    val dx: Int by lazy { dxS?.toInt() ?: throw IllegalStateException("no value for DX") }
    val iq: Int by lazy { iqS?.toInt() ?: throw IllegalStateException("no value for IQ")  }
    val ht: Int by lazy { htS?.toInt() ?: throw IllegalStateException("no value for HT")  }
    val will: Int by lazy { (willS?.toInt() ?: 0) + 10 }
    val per: Int by lazy { (perS?.toInt() ?: 0) + 10 }

    val attributes: Map<String, Attribute> by lazy {
        listOf(
                Attribute("ST", st),
                Attribute("DX", dx),
                Attribute("IQ", iq),
                Attribute("HT", ht),
                Attribute("Will", will),
                Attribute("Per", per)
        ).map { Pair(it.name, it) }.toMap()
    }

    val skills : Map<String, Attribute> by lazy {
        skillData
                ?.map { it.toAttribute(this) }
                ?.map { Pair(it.name, it) }
                ?.toMap()
                ?: throw IllegalStateException("trouble mapping skills")
    }

    val thrust: RollSpec by lazy { thrust(st) }
    val swing: RollSpec by lazy { swing(st) }

    val meleeAttacks : Map<String, Attack> by lazy {
        equipment?.let {
            it.map { data -> data.description?.let { name -> data.meleeAttackData?.let {attacks -> Pair(name, attacks)}}}
                    .filterNotNull()
                    .flatMap { (name, attacks) -> attacks.map { it.toAttack(name, thrust, swing)} }
                    .map { attack -> Pair(attack.attackName, attack)}
                    .toMap()

        } ?: throw IllegalStateException("trouble mapping melee attacks")
    }

    val rangedAttacks : Map<String, Attack> by lazy {
        equipment?.let {
            it.map { data -> data.description?.let { name -> data.rangedAttackData?.let {attacks -> Pair(name, attacks)}}}
                    .filterNotNull()
                    .flatMap { (name, attacks) -> attacks.map { it.toAttack(name, thrust, swing)} }
                    .map { attack -> Pair(attack.attackName, attack)}
                    .toMap()

        } ?: throw IllegalStateException("trouble mapping melee attacks")
    }

    fun toRoller(randomizer: Randomizer) = toRoller(randomizer, this)
}

fun toRoller(randomizer: Randomizer, characterData: CharacterData) = with(characterData) {
    println("melee")
    println(meleeAttacks)
    println("ranged")
    println(rangedAttacks)
    CharacterRoller(randomizer, name?: "UNKNOWN" , attributes, skills, meleeAttacks, rangedAttacks)
}

@Component
class CharacterLoader {
    fun load(reader: Reader) : CharacterData? {
        val doc = SAXBuilder().build(reader)
        val characterData = JDOM.load(doc.rootElement, CharacterData::class.java)
        with(characterData) {
            println("loaded equipment Data ${equipment?.size}")
            println("contained loaded equipment Data ${containedEquipment?.size}")
            println(equipment)
            println("loaded equipment Data")
            println(equipment)
            println("loaded melee")
            println(meleeAttacks)
            println("loaded ranged")
            println(rangedAttacks)
        }
        return characterData
    }
}
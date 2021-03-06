package org.crypticmission.gurpslack.loader

import org.crypticmission.gurpslack.model.*
import org.crypticmission.gurpslack.repositories.Randomizer
import org.jdom2.Document
import org.jdom2.input.SAXBuilder
import org.jonnyzzz.kotlin.xml.bind.*
import org.jonnyzzz.kotlin.xml.bind.jdom.JDOM
import org.jonnyzzz.kotlin.xml.bind.jdom.JXML
import org.springframework.stereotype.Component
import java.io.Reader
import java.io.StringReader

fun parseAttack(description: String, usage: String?, damage: String?, thrust: RollSpec, swing: RollSpec, recoil: Int? = null): Attack =
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
                damage.let { parseDamage(it)?.let { Attack(name, it, recoil) } } ?:
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

fun String?.toSplitRecoil() : Pair<Int, Int?>? =
        if (this == null) {
            null
        } else if (this.contains("/")) {
            val shot = this.substringBefore("/").toInt()
            val slugs = this.substringAfter("/").toInt()
            Pair(shot, slugs)
        } else {
            Pair(this.toInt(), null)
        }

val SLUG_DAMAGE_MULTIPLIER = 4

class RangedAttackData {
    val damage by JXML / "damage" / XText
    val minSt by JXML / "strength" / XText
    val accuracyText by JXML / "accuracy" / XText
    val range by JXML / "range" / XText
    val rateOfFire by JXML / "rate_of_fire" / XText
    val shots by JXML / "shots" / XText
    val bulk by JXML / "bulk" / XText
    val recoil by JXML / "recoil" / XText

    fun toAttacks(description: String, thrust: RollSpec, swing: RollSpec): Pair<Attack, Attack?> {
        val splitRecoils = recoil.toSplitRecoil()
        if (splitRecoils == null) {
            return Pair(
                    parseAttack(description, null, this.damage, thrust, swing, null),
                    null
            )
        } else if (splitRecoils.second == null) {
            return Pair(
                    parseAttack(description, null, this.damage, thrust, swing, splitRecoils.first),
                    null
            )
        } else {
            val baseAttack: Attack = parseAttack(description, null, this.damage, thrust, swing, splitRecoils.first)
            val shotAttack = baseAttack.copy(attackName = "Buckshot: " + baseAttack.attackName)
            val slugDamage = (shotAttack.damageSpec * SLUG_DAMAGE_MULTIPLIER).copy(damageType = DamageType.pi_plus_plus)
            val slugAttack = baseAttack.copy(attackName = "Slugs: " + baseAttack.attackName, damageSpec = slugDamage)

            return Pair(shotAttack, slugAttack)
        }
    }

}

class Equipment {
    val description by JXML / "description" / XText
    val rangedAttackData by JXML / XElements("ranged_weapon") / XSub(RangedAttackData::class.java)
    val meleeAttackData by JXML / XElements("melee_weapon") / XSub(MeleeAttackData::class.java)
}
class EquipmentContainer {
    val description by JXML / "description" / XText
    val equipment by JXML / XElements("equipment") / XSub(Equipment::class.java)
    val equipmentContainers by JXML / XElements("equipment_container") / XSub(EquipmentContainer::class.java)
}
class SkillContainer {
    val name by JXML / "name" / XText
    val skill by JXML / XElements("skill") / XSub(SkillData::class.java)
    val skillContainers by JXML / XElements("skill_container") / XSub(SkillContainer::class.java)
}

class CharacterData {
    val name by JXML / "profile" / "name" / XText
    val playerName by JXML / "profile" / "player_name" / XText
    private val uncontainedSkills by JXML / "skill_list" / XElements("skill") / XSub(SkillData::class.java)
    private val skillContainers by JXML / "skill_list" / XElements("skill_container") / XSub(SkillContainer::class.java)
    private val uncontainedEquipment by JXML / "equipment_list" / XElements("equipment") / XSub(Equipment::class.java)
    private val equipmentContainers by JXML / "equipment_list" / XElements("equipment_container") / XSub(EquipmentContainer::class.java)

    private val stS by JXML / "ST" / XText
    private val dxS by JXML / "DX" / XText
    private val iqS by JXML / "IQ" / XText
    private val htS by JXML / "HT" / XText
    private val hpS by JXML / "HP" / XText
    private val fpS by JXML / "FP" / XText
    private val willS by JXML / "will" / XText
    private val perS by JXML / "perception" / XText
    private val currentHpS by JXML / "current_hp" / XText
    private val currentFpS by JXML / "current_fp" / XText

    val st: Int by lazy { stS?.toInt() ?: throw IllegalStateException("no value for ST") }
    val dx: Int by lazy { dxS?.toInt() ?: throw IllegalStateException("no value for DX") }
    val iq: Int by lazy { iqS?.toInt() ?: throw IllegalStateException("no value for IQ")  }
    val ht: Int by lazy { htS?.toInt() ?: throw IllegalStateException("no value for HT")  }
    val hp: Int by lazy { hpS?.toInt() ?: throw IllegalStateException("no value for HP")  }
    val fp: Int by lazy { fpS?.toInt() ?: throw IllegalStateException("no value for FP")  }
    val maxHp: Int by lazy { st + hp }
    val maxFp: Int by lazy { ht + fp }
    val currentHp: Int by lazy { currentHpS?.toInt() ?: maxHp }
    val currentFp: Int by lazy { currentFpS?.toInt() ?: maxFp }
    val will: Int by lazy { (willS?.toInt() ?: 0) + 10 }
    val per: Int by lazy { (perS?.toInt() ?: 0) + 10 }
    val equipment: List<Equipment> by lazy {
        ((uncontainedEquipment ?: listOf<Equipment>()) +
                extractContainedEquipment(equipmentContainers, mutableListOf()))
                .sortedBy { equipment -> equipment.description }
    }

    private fun extractContainedEquipment(equipmentContainers: List<EquipmentContainer>?,
                                          equipmentList: MutableList<Equipment>) : List<Equipment> {
        if (equipmentContainers == null) return equipmentList
        equipmentContainers.forEach { equipmentContainer ->
            equipmentContainer.equipment?.forEach { equipment -> equipmentList.add(equipment) }
            extractContainedEquipment(equipmentContainer.equipmentContainers, equipmentList)
        }
        return equipmentList

    }

    private fun extractContainedSkills(skillContainers: List<SkillContainer>?,
                                          skillList: MutableList<SkillData>) : List<SkillData> {
        if (skillContainers == null) return skillList
        skillContainers.forEach { skillContainer ->
            skillContainer.skill?.forEach { skill -> skillList.add(skill) }
            extractContainedSkills(skillContainer.skillContainers, skillList)
        }
        return skillList

    }

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
        ((uncontainedSkills ?: listOf<SkillData>()) +
                extractContainedSkills(skillContainers, mutableListOf()))
                ?.sortedBy { skill -> skill.fullName }
                ?.map { it.toAttribute(this) }
                ?.map { Pair(it.name, it) }
                ?.toMap()
                ?: throw IllegalStateException("trouble mapping skills")
    }

    val thrust: RollSpec by lazy { thrust(st) }
    val swing: RollSpec by lazy { swing(st) }

    val meleeAttacks : Map<String, Attack> by lazy {
        equipment.map { data -> data.description?.let { name -> data.meleeAttackData?.let {attacks -> Pair(name, attacks)}}}
                .filterNotNull()
                .flatMap { (name, attacks) -> attacks.map { it.toAttack(name, thrust, swing)} }
                .map { attack -> Pair(attack.attackName, attack)}
                .toMap()
    }

    val rangedAttacks : Map<String, Attack> by lazy {
        equipment.map { data -> data.description?.let { name -> data.rangedAttackData?.let {attacks -> Pair(name, attacks)}}}
                .filterNotNull()
                .flatMap { (name, attacks) -> attacks.map { it.toAttacks(name, thrust, swing).toList() } }
                .flatMap { it.filterNotNull() }
                .map { attack -> Pair(attack.attackName, attack)}
                .toMap()
    }

    fun toRoller(randomizer: Randomizer) = toRoller(randomizer, this)
}

fun toRoller(randomizer: Randomizer, characterData: CharacterData) = with(characterData) {
    val trackedStats = listOf(
            TrackedValue.hp(characterData.maxHp, characterData.currentHp),
            TrackedValue.fp(characterData.maxFp, characterData.currentFp),
            TrackedValue.wp(characterData.will, characterData.will)
    )
    CharacterRoller(randomizer, name?: "UNKNOWN" , characterData.playerName?: "GM",
            attributes, skills, meleeAttacks, rangedAttacks, trackedStats)
}

@Component
class CharacterLoader {
    fun load(xml: String) : CharacterData? = load(StringReader(xml))

    fun load(reader: Reader) : CharacterData? {
        val doc = SAXBuilder().build(reader)
        return fromDoc(doc)
    }

    fun fromDoc(doc: Document) = JDOM.load(doc.rootElement, CharacterData::class.java)
}
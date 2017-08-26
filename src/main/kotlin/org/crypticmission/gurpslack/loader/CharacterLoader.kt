package org.crypticmission.gurpslack.loader

import org.crypticmission.gurpslack.model.Attribute
import org.crypticmission.gurpslack.model.CharacterRoller
import org.crypticmission.gurpslack.repositories.Randomizer
import org.jdom2.input.SAXBuilder
import org.jonnyzzz.kotlin.xml.bind.XAnyElements
import org.jonnyzzz.kotlin.xml.bind.XSub
import org.jonnyzzz.kotlin.xml.bind.XText
import org.jonnyzzz.kotlin.xml.bind.jdom.JDOM
import org.jonnyzzz.kotlin.xml.bind.jdom.JXML
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
fun relativeLevel(difficulty: String, points: Int) : Int {
    val offset = when (difficulty) {
        "E" -> 0
        "A" -> -1
        "H" -> -2
        "VH" -> -3
        else -> throw IllegalArgumentException("don't understand difficulty '${difficulty}'")
    }
    val level = when (points) {
        1 -> 0
        in 2..3 -> 1
        else -> 1 + (points/4)
    }
    return offset + level
}

fun getLevel(characterData: CharacterData, baseAttribute: String, difficulty:String, points: Int) : Int {
    val attribute = characterData.attributes.get(baseAttribute) ?: throw IllegalStateException("no attribute called '${baseAttribute}'")
    val relativeLevel = relativeLevel(difficulty, points)
    return attribute.level + relativeLevel
}

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

class CharacterData {
    val name by JXML / "profile" / "name" / XText
    val playerName by JXML / "profile" / "player_name" / XText
    val skillData by JXML / "skill_list" / XAnyElements / XSub(SkillData::class.java)

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
}

fun toRoller(randomizer: Randomizer, characterData: CharacterData) = with(characterData) {
        CharacterRoller(randomizer, name?: "UNKNOWN" , attributes, skills)
    }

class CharacterLoader {
    fun load(reader: Reader) : CharacterData? {
        val doc = SAXBuilder().build(reader)
        val characterData = JDOM.load(doc.rootElement, CharacterData::class.java)
        return characterData
    }
}
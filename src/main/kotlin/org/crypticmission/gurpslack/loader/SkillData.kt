package org.crypticmission.gurpslack.loader

import org.crypticmission.gurpslack.model.Attribute
import org.jonnyzzz.kotlin.xml.bind.XText
import org.jonnyzzz.kotlin.xml.bind.jdom.JXML

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
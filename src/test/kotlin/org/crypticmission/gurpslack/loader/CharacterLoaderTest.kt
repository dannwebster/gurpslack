package org.crypticmission.gurpslack.loader

import org.crypticmission.gurpslack.model.parseDamage
import org.junit.Assert.*
import org.junit.Ignore
import org.junit.Test

/**
 */
class CharacterLoaderTest {
    @Test fun shouldParseNoraWhenLoadedFromFile() {
        // given
        val reader = this::class.java.getResourceAsStream("/NoraBlackburn.gcs").bufferedReader()
        val subject = CharacterLoader()

        // when
        val character = subject.load(reader) ?: throw IllegalArgumentException()

        // then
        assertNotNull(character)
        assertEquals("Nora Blackburn", character.name)
        assertEquals("Tinsley Webster", character.playerName)
    }

    @Test fun shouldLoadEquipmentFromContainer() {
        // given
        val reader = this::class.java.getResourceAsStream("/Everett O'Connell.gcs").bufferedReader()
        val subject = CharacterLoader()

        // when
        val character = subject.load(reader) ?: throw AssertionError("failed to read character")

        // then
        assertNotNull(character)
        assertEquals("Everett O'Connel", character.name)
        assertEquals("Stewart", character.playerName)

        val pistol = character.rangedAttacks.get("FN-Browning High Power, 9x19mm") ?:
                throw AssertionError("no pistol Damage")
        assertEquals("FN-Browning High Power, 9x19mm", pistol.attackName)
        assertEquals(parseDamage("2d+2 pi"), pistol.damageSpec)
        assertEquals(6, character.rangedAttacks.size)
        assertEquals(2, character.meleeAttacks.size)

    }
    @Test fun shouldParseRcWhenLoadedFromFile() {
        // given
        val reader = this::class.java.getResourceAsStream("/rc-cleveland.gcs").bufferedReader()
        val subject = CharacterLoader()

        // when
        val character = subject.load(reader) ?: throw AssertionError("failed to read character")

        // then
        assertNotNull(character)
        assertEquals("R.C. Cleveland", character.name)
        assertEquals("marcfaletti", character.playerName)

        assertEquals("10", character.stS)
        assertEquals("13", character.dxS)
        assertEquals("14", character.iqS)
        assertEquals("11", character.htS)
        assertEquals(10, character.st)
        assertEquals(13, character.dx)
        assertEquals(14, character.iq)
        assertEquals(11, character.ht)
        assertEquals(15, character.will)
        assertEquals(12, character.per)

        assertEquals(39, character.skillData?.size)
        val skillData = character.skillData?.get(0) ?: throw AssertionError("no skillData")
        assertEquals("Acting", skillData.name)
        assertEquals("IQ", skillData.baseAttribute)
        assertEquals("A", skillData.difficulty)
        assertEquals(1, skillData.points)

        val skills = character.skills
        assertEquals(39, skills.size)
        val skill = character.skills.get("Acting") ?: throw AssertionError("no Acting Skill")
        assertEquals("Acting", skill.name)
        assertEquals(13, skill.level)

        val spiritLore = character.skills.get("Hidden Lore (Spirit Lore)")
        assertEquals(13, spiritLore?.level)

        val guns = character.skills.get("Guns/TL5^ (Pistol)")
        assertEquals(14, guns?.level)

        val swungKnife = character.meleeAttacks.get("Large Knife (Swung)") ?: throw AssertionError("no Large Knife (Swung)")
        assertEquals("Large Knife (Swung)", swungKnife.attackName)
        assertEquals(parseDamage("1d6-2 cut"), swungKnife.damageSpec)

        val thrustKnife = character.meleeAttacks.get("Large Knife (Thrust)") ?: throw AssertionError("no Large Knife (Thrust)")
        assertEquals("Large Knife (Thrust)", thrustKnife.attackName)
        assertEquals(parseDamage("1d6-2 imp"), thrustKnife.damageSpec)

        val rifle = character.rangedAttacks.get("Lever-Action Carbine, .30") ?: throw AssertionError("no rifleDamage")
        assertEquals("Lever-Action Carbine, .30", rifle.attackName)
        assertEquals(parseDamage("5d pi"), rifle.damageSpec)
//j
//        <damage>sw-2 cut</damage>
//        <strength>6</strength>
//        <usage>Swung</usage>
//        <reach>C,1</reach>
//        <parry>-1</parry>
//        <block>No</block>
    }
}
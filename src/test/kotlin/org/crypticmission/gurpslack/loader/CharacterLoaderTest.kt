package org.crypticmission.gurpslack.loader

import org.junit.Assert.*
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

    @Test fun shouldParseRcWhenLoadedFromFile() {
        // given
        val reader = this::class.java.getResourceAsStream("/rc-cleveland.gcs").bufferedReader()
        val subject = CharacterLoader()

        // when
        val character = subject.load(reader) ?: throw IllegalArgumentException()

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
        val skillData = character.skillData?.get(0) ?: throw IllegalArgumentException()
        assertEquals("Acting", skillData.name)
        assertEquals("IQ", skillData.baseAttribute)
        assertEquals("A", skillData.difficulty)
        assertEquals(1, skillData.points)

        val skills = character.skills
        skills.forEach { t, u -> println(t) }
        assertEquals(39, skills.size)
        val skill = character.skills.get("Acting") ?: throw IllegalArgumentException()
        assertEquals("Acting", skill.name)
        assertEquals(13, skill.level)

        val spiritLore = character.skills.get("Hidden Lore (Spirit Lore)")
        assertEquals(13, spiritLore?.level)

        val guns = character.skills.get("Guns/TL5^ (Pistol)")
        assertEquals(14, guns?.level)

    }
}
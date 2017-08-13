package org.crypticmission.gurpslack.repositories

import org.junit.Test
import org.junit.Assert.*

/**
 */
class CharacterRepositoryTest {
    @Test fun shouldCreateCharacterWhenOneDoesNotExist() {
        // given
        val subject = CharacterRepository()

        // when
        val wasAdded = subject.add("foo", "Foo Bar")
        val added = subject.get("foo") ?: throw IllegalArgumentException()

        // then
        assertEquals(true, wasAdded)
        assertEquals("Foo Bar", added.characterName)
    }

    @Test fun shouldNotCreateCharacterWhenOneDoesExist() {
        // given
        val subject = CharacterRepository()
        val wasAdded = subject.add("foo", "Foo Bar")
        assertEquals(true, wasAdded)

        // when
        val wasAddedAgain = subject.add("foo", "Foo Bar")
        val added = subject.get("foo") ?: throw IllegalArgumentException()

        // then
        assertEquals(false, wasAddedAgain)
        assertEquals("Foo Bar", added.characterName)
    }
}
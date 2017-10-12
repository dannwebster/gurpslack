package org.crypticmission.gurpslack.repositories

import org.crypticmission.gurpslack.model.CharacterRoller
import org.junit.Test
import org.junit.Assert.*

/**
 */
class CharacterRollerRepositoryTest {

    @Test fun shouldCreateCharacterWhenOneDoesNotExist() {
        // given
        val subject = CharacterRepository()

        // when
        val wasAdded = subject.add("foo", null,"Foo Bar")
        val added = subject.getByKey("foo") ?: throw IllegalArgumentException()

        // then
        assertEquals(true, wasAdded)
        assertEquals("Foo Bar", added.characterName)
    }

    @Test fun shouldNotCreateCharacterWhenOneDoesExist() {
        // given
        val subject = CharacterRepository()
        val wasAdded = subject.add("foo", null,"Foo Bar")
        assertEquals(true, wasAdded)

        // when
        val wasAddedAgain = subject.add("foo", null,"Foo Bar")
        val added = subject.getByKey("foo") ?: throw IllegalArgumentException()

        // then
        assertEquals(false, wasAddedAgain)
        assertEquals("Foo Bar", added.characterName)
    }

    @Test
    fun shouldBeAccessibleByUserNameWhenAddedWithUsername() {
        // given
        val subject = CharacterRepository()
        val wasAdded = subject.add("foo", "UserName","Foo Bar")
        assertEquals(true, wasAdded)

        // when
        val wasAddedAgain = subject.add("foo", null,"Foo Bar")
        val added = subject.getByUserName("username") ?: throw IllegalArgumentException()

        // then
        assertEquals("foo", added.first)
        assertEquals("Foo Bar", added.second.characterName)
        assertEquals(false, wasAddedAgain)
    }

    @Test
    fun shouldBeAccessibleByUserNameWhenPutWithUsername() {
        // given
        val subject = CharacterRepository()
        val roller = CharacterRoller(Randomizer.MAX, "characterName",
                emptyMap(),
                emptyMap())
        subject.put("foo", "username", roller)

        // when
        val retrieved = subject.getByUserName("username") ?: throw IllegalArgumentException()

        // then
        assertEquals("foo", retrieved.first)
        assertEquals(roller, retrieved.second)
    }
}
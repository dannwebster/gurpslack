package org.crypticmission.gurpslack.model

import org.crypticmission.gurpslack.controllers.parseDamage
import org.crypticmission.gurpslack.controllers.parseDr
import org.junit.Test

import org.junit.Assert.*

/**
 */
class DamageSpecTest {

    @Test fun shouldReturnNullWhenDamageIsInvalid() {
        // given
        val spec = "foo"

        // when
        val subject = parseDamage(spec)

        // then
        assertNull(subject)
    }

    @Test
    fun shouldShowFullStringFromDefaults() {
        // given
        val spec = "2d6+1"

        // when
        val subject = parseDamage(spec)

        // then
        assertNotNull(subject)
        assertEquals(RollSpec(2, 6, 1), subject?.rollSpec)
        assertEquals(DamageType.cru, subject?.damageType)
        assertEquals("2d6+1 cru", subject?.canonical)
    }

    @Test
    fun rollDmgTest() {
        // given
        val spec = "2d6+1 pi++"

        // when
        val subject = parseDamage(spec)

        // then
        assertNotNull(subject)
        assertEquals(RollSpec(2, 6, 1), subject?.rollSpec)
        assertEquals(DamageType.pi_plus_plus, subject?.damageType)
        assertEquals("2d6+1 pi++", subject?.canonical)
    }

    @Test
    fun rollDmgTestVsDr() {
        // given
        val spec = "2d6+1 pi++ vs DR 2"

        // when
        val subject = parseDamage(spec)
        val dr = parseDr(spec)

        // then
        assertNotNull(subject)
        assertEquals(RollSpec(2, 6, 1), subject?.rollSpec)
        assertEquals(DamageType.pi_plus_plus, subject?.damageType)
        assertEquals(2, dr)
        assertEquals("2d6+1 pi++", subject?.canonical)
    }

}
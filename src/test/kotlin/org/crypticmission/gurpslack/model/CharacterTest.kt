package org.crypticmission.gurpslack.model

import org.crypticmission.gurpslack.repositories.Randomizer
import org.junit.Assert.*
import org.junit.Test

/**
 */
class CharacterTest {
    @Test fun shouldCreatProperMessageWhenMessageCalled() {
        // given
        val subject = Character("Foo Bar", Randomizer.MAX)

        subject.addAttribute(Attribute("ST", 10))
        subject.addAttribute(Attribute("DX", 14))
        subject.addAttribute(Attribute("IQ", 12))
        subject.addAttribute(Attribute("HT", 13))

        subject.addDamage("punch", RollSpec.DEFAULT.toDamage(DamageType.cru))
        subject.addDamage("sword slash", RollSpec.DEFAULT.toDamage(DamageType.cut))
        subject.addDamage("sword stab", RollSpec.DEFAULT.toDamage(DamageType.imp))

        val expected =
"""Character Name: Foo Bar
Attributes:
    ST: 10
    DX: 14
    IQ: 12
    HT: 13
Damage Rolls:
    punch: 3d6 cru
    sword slash: 3d6 cut
    sword stab: 3d6 imp
"""

        // when
        val message = subject.message()

        // then
        assertEquals(expected, message)

    }

    @Test fun shouldRollDefaultWhenRollingMissingAttribute() {
        // given
        val subject = Character("character-name", Randomizer.MAX)

        // when
        val outcome = subject.rollVsAttribute("FOO", -3)

        // then
        assertEquals("CRITICAL FAILURE: A roll of 18 vs FOO-3 (7) was a critical failure with a margin of failure of 11", outcome.message)

    }

    @Test fun shouldRollVsAttributeWhenAttributeIsAvailable() {
        // given
        val subject = Character("character-name", Randomizer.MAX)
        subject.addAttribute(Attribute("BAR", 15))

        // when
        val outcome = subject.rollVsAttribute("BAR", -3)

        // then
        assertEquals("CRITICAL FAILURE: A roll of 18 vs BAR-3 (12) was a critical failure with a margin of failure of 6", outcome.message)
    }

    @Test fun shouldRollVsDefaultWhenRollingMissingDamage() {
        // given
        val subject = Character("character-name", Randomizer.MAX)

        // when
        val damageOutcome = subject.rollDamage("damageSpec-name")

        // then
        assertEquals("Dealt *6* crushing damage after DR: damageSpec-name causes 1d6 cru vs DR 0. Rolled 1d6 = 6. [(6 impact damage - DR 0) * 1.0 for crushing]", damageOutcome.message)

    }
}
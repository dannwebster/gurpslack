package org.crypticmission.gurpslack.model

import org.crypticmission.gurpslack.repositories.Randomizer
import org.junit.Assert.*
import org.junit.Test

/**
 */
class CharacterRollerTest {

    @Test fun shouldRollDefaultWhenRollingMissingAttribute() {
        // given
        val subject = CharacterRoller("character-name", Randomizer.MAX)

        // when
        val outcome = subject.rollVsAttribute("FOO", -3)

        // then
        assertEquals("CRITICAL FAILURE: A roll of 18 vs FOO-3 (7) was a critical failure with a margin of failure of 11", outcome.message)

    }

    @Test fun shouldRollVsAttributeWhenAttributeIsAvailable() {
        // given
        val subject = CharacterRoller("character-name", Randomizer.MAX)
        subject.addAttribute(Attribute("BAR", 15))

        // when
        val outcome = subject.rollVsAttribute("BAR", -3)

        // then
        assertEquals("CRITICAL FAILURE: A roll of 18 vs BAR-3 (12) was a critical failure with a margin of failure of 6", outcome.message)
    }

    @Test fun shouldRollVsDefaultWhenRollingMissingDamage() {
        // given
        val subject = CharacterRoller("character-name", Randomizer.MAX)

        // when
        val damageOutcome = subject.rollDamage("damageSpec-name")

        // then
        assertEquals("6 crushing damage after DR: damageSpec-name causes 1d6 cru vs DR 0. Rolled 1d6 = 6. [(6 impact damageSpec - DR 0) * 1.0 for crushing]", damageOutcome.message)

    }
}
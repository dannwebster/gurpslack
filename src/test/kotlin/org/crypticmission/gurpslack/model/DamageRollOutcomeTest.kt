package org.crypticmission.gurpslack.model

import org.crypticmission.gurpslack.repositories.Randomizer
import org.junit.Test
import org.junit.Assert.*
/**
 */

class DamageRollOutcomeTest {
    @Test fun shouldCorrectStringWhenNoDr() {
        // given
        val spec = DamageSpec.fromString("2d6+1 cut") ?: throw IllegalArgumentException()
        val rand = Randomizer.MAX

        // when
        val outcome = spec.roll(rand)

        // then
        assertEquals("19 cutting damage after DR: attack causes 2d6+1 cut vs DR 0. " +
                "Rolled 2d6+1 = 13. [(13 impact damageSpec - DR 0) * 1.5 for cutting]", outcome.message)

    }

    @Test fun shouldCorrectStringWhenHasDr() {
        // given
        val spec = DamageSpec.fromString("2d6+1 pi+ vs. DR: 3") ?: throw IllegalArgumentException()
        val rand = Randomizer.MAX

        // when
        val outcome = spec.roll(rand)

        // then
        assertEquals("15 large piercing damage after DR: attack causes 2d6+1 pi+ vs DR 3. " +
                "Rolled 2d6+1 = 13. [(13 impact damageSpec - DR 3) * 1.5 for large piercing]", outcome.message)

    }

    @Test fun shouldCorrectStringWhenHasDrAndName() {
        // given
        val spec = DamageSpec.fromString("2d6+1 imp vs. DR: 3") ?: throw IllegalArgumentException()
        val rand = Randomizer.MAX

        // when
        val outcome = spec.roll(rand, "sword stab")

        // then
        assertEquals("20 impaling damage after DR: sword stab causes 2d6+1 imp vs DR 3. " +
                "Rolled 2d6+1 = 13. [(13 impact damageSpec - DR 3) * 2.0 for impaling]", outcome.message)

    }
}
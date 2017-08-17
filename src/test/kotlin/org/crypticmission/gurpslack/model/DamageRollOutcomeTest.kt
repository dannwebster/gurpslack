package org.crypticmission.gurpslack.model

import org.crypticmission.gurpslack.controllers.parseDamage
import org.crypticmission.gurpslack.controllers.parseDr
import org.crypticmission.gurpslack.repositories.Randomizer
import org.junit.Test
import org.junit.Assert.*
/**
 */

class DamageRollOutcomeTest {
    @Test fun shouldCorrectStringWhenNoDr() {
        // given
        val damageLine = "2d6+1 cut"
        val spec = parseDamage(damageLine) ?: throw IllegalArgumentException()
        val dr = parseDr(damageLine)
        val rand = Randomizer.MAX

        // when
        val outcome = spec.rollVsDr(dr, rand)

        // then
        assertEquals("Dealt *19* cutting damage after DR:\n" +
                "This attack causes 2d6+1 cut vs DR 0.\n" +
                "Rolled 2d6+1 => :d6-6: :d6-6:+1 => 13.\n" +
                "`[(13 impact damage - DR 0) * 1.5 for cutting]`", outcome.toString())

    }

    @Test fun shouldDealZeroDamageWhenDamageIsLessThanDR() {
        // given
        val damageLine = "2d6+1 cut vs dr 80"
        val spec = parseDamage(damageLine) ?: throw IllegalArgumentException()
        val dr = parseDr(damageLine)
        val rand = Randomizer.MAX

        // when
        val outcome = spec.rollVsDr(dr, rand)

        // then
        assertEquals("Dealt *0* cutting damage after DR:\n" +
                "This attack causes 2d6+1 cut vs DR 80.\n" +
                "Rolled 2d6+1 => :d6-6: :d6-6:+1 => 13.\n" +
                "`[(13 impact damage - DR 80) * 1.5 for cutting]`", outcome.toString())

    }

    @Test fun shouldCorrectStringWhenHasDr() {
        // given
        val damageLine = "2d6+1 pi+ vs. DR: 3"
        val spec = parseDamage(damageLine) ?: throw IllegalArgumentException()
        val dr = parseDr(damageLine)
        val rand = Randomizer.MAX

        // when
        val outcome = spec.rollVsDr(dr, rand)

        // then
        assertEquals("Dealt *15* large piercing damage after DR:\n" +
                "This attack causes 2d6+1 pi+ vs DR 3.\n" +
                "Rolled 2d6+1 => :d6-6: :d6-6:+1 => 13.\n" +
                "`[(13 impact damage - DR 3) * 1.5 for large piercing]`", outcome.toString())

    }

    @Test fun shouldCorrectStringWhenHasDrAndName() {
        // given
        val damageLine = "2d6+1 imp vs. DR: 3"
        val spec = parseDamage(damageLine) ?: throw IllegalArgumentException()
        val dr = parseDr(damageLine)
        val rand = Randomizer.MAX

        // when
        val outcome = spec.rollVsDr(dr, rand)

        // then
        assertEquals("Dealt *20* impaling damage after DR:\n" +
                "This attack causes 2d6+1 imp vs DR 3.\n" +
                "Rolled 2d6+1 => :d6-6: :d6-6:+1 => 13.\n" +
                "`[(13 impact damage - DR 3) * 2.0 for impaling]`", outcome.toString())

    }
}
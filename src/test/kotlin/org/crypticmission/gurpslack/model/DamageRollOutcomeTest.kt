package org.crypticmission.gurpslack.model

import org.crypticmission.gurpslack.repositories.Randomizer
import org.junit.Test
import org.junit.Assert.*
/**
 */

class DamageRollOutcomeTest {
    @Test fun shouldDealZeroDamageWhenDamageIsLessThanDR() {
        // given
        val damageLine = "2d6+1 cut vs dr 80"
        val spec = parseDamage(damageLine) ?: throw IllegalArgumentException()
        val dr = parseDr(damageLine)
        val rand = Randomizer.MAX

        // when
        val outcome = spec.rollVsDr(dr, rand, 1)

        // then
        assertEquals(DamageType.cut, outcome.damageSpec.damageType)
        assertEquals(0, outcome.only().finalDamage)
        assertEquals(0, outcome.only().damageAfterDr)
        assertEquals(13, outcome.only().impactDamage)
    }

    @Test fun shouldCorrectStringWhenHasDr() {
        // given
        val damageLine = "2d6+1 pi+ vs. DR: 3"
        val spec = parseDamage(damageLine) ?: throw IllegalArgumentException()
        val dr = parseDr(damageLine)
        val rand = Randomizer.MAX

        // when
        val outcome = spec.rollVsDr(dr, rand, 1)

        // then
        assertEquals(DamageType.pi_plus, outcome.damageSpec.damageType)
        assertEquals(13, outcome.only().impactDamage)
        assertEquals(10, outcome.only().damageAfterDr)
        assertEquals(15, outcome.only().finalDamage)
    }

    @Test fun shouldCorrectStringWhenHasDrAndName() {
        // given
        val damageLine = "2d6+1 imp vs. DR: 3"
        val spec = parseDamage(damageLine) ?: throw IllegalArgumentException()
        val dr = parseDr(damageLine)
        val rand = Randomizer.MAX

        // when
        val outcome = spec.rollVsDr(dr, rand, 1)

        // then
        assertEquals(DamageType.imp, outcome.damageSpec.damageType)
        assertEquals(20, outcome.totalFinalDamage)
        assertEquals(10 , outcome.totalDamageAfterDr)
        assertEquals(13 , outcome.totalImpactDamage)
    }
}
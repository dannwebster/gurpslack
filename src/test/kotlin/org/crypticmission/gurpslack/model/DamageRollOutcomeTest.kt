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
        val outcome = spec.rollVsDr(dr, rand)

        // then
        assertEquals(DamageType.cut, outcome.damageSpec.damageType)
        assertEquals(0, outcome.totalDamage)
        assertEquals(0, outcome.impactDamage)
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
        assertEquals(DamageType.pi_plus, outcome.damageSpec.damageType)
        assertEquals(15, outcome.totalDamage)
        assertEquals(10, outcome.impactDamage)
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
        assertEquals(DamageType.imp, outcome.damageSpec.damageType)
        assertEquals(20, outcome.totalDamage)
        assertEquals(10 , outcome.impactDamage)
    }
}
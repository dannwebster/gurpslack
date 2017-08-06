package org.crypticmission.gurpslack.commands

import org.crypticmission.gurpslack.util.Randomizer
import org.junit.Test
import org.junit.Assert.*

/**
 */

class RollablesTest {
    val THREE_D6 = RollSpec(3, 6)
    val ROLL_1 = Randomizer.MIN
    val ROLL_3 = Randomizer.specified(3)
    val ROLL_5 = Randomizer.specified(5)
    val ROLL_6 = Randomizer.MAX

    @Test fun shouldFormatAttributeString() {
        // given
        val subject = Attribute("HT", 10)

        // when
        val s = subject.toString()

        // then
        assertEquals(s, "HT: 10")

    }

    @Test fun shouldFormatPositiveModifiedAttributeString() {
        // given
        val subject = ModifiedAttribute(Attribute("HT", 10), +3)

        // when
        val s = subject.toString()

        // then
        assertEquals(s, "HT+3 (13)")

    }

    @Test fun shouldFormatNegativeModifiedAttributeString() {
        // given
        val subject = ModifiedAttribute(Attribute("HT", 10), -3)

        // when
        val s = subject.toString()

        // then
        assertEquals(s, "HT-3 (7)")

    }

    @Test fun shouldSucccedWhenRollIsLower() {
        // given
        val subject = ModifiedAttribute(Attribute("HT", 10), +3)

        // when
        val outcome = subject.rollVs(ROLL_3)

        // then
        assertEquals(true, outcome.isSuccess)
        assertEquals(4, outcome.margin)
        assertEquals(false, outcome.isCritical)
        assertEquals("SUCCESS: A roll of 9 vs HT+3 (13) was a success with a margin of success of 4", outcome.message)
    }

    @Test fun shouldCriticallyFailWhenRollIs18() {
        // given
        val subject = ModifiedAttribute(Attribute("HT", 10), +3)

        // when
        val outcome = subject.rollVs(ROLL_6)

        // then
        assertEquals(false, outcome.isSuccess)
        assertEquals(5, outcome.margin)
        assertEquals(true, outcome.isCritical)
        assertEquals("CRITICAL FAILURE: A roll of 18 vs HT+3 (13) was a critical failure with a margin of failure of 5", outcome.message)
    }

    @Test fun shouldCriticallySucceedWhenRollIs3() {
        // given
        val subject = ModifiedAttribute(Attribute("HT", 10), +3)

        // when
        val outcome = subject.rollVs(ROLL_1)

        // then
        assertEquals(true, outcome.isSuccess)
        assertEquals(10, outcome.margin)
        assertEquals(true, outcome.isCritical)
        assertEquals("CRITICAL SUCCESS: A roll of 3 vs HT+3 (13) was a critical success with a margin of success of 10", outcome.message)
    }

    @Test fun shouldFailWhenRollIsHigher() {
        // given
        val subject = ModifiedAttribute(Attribute("HT", 10), +3)

        // when
        val outcome = subject.rollVs(ROLL_5)

        // then
        assertEquals(false, outcome.isSuccess)
        assertEquals(2, outcome.margin)
        assertEquals(false, outcome.isCritical)
        assertEquals("FAILURE: A roll of 15 vs HT+3 (13) was a failure with a margin of failure of 2", outcome.message)
    }

    @Test fun shouldGetDamageStringWhenRollingAgainstDamage() {
        // given
        val subject = Damage("damage-name", RollSpec.spec("1d+1"))

        // when
        val outcome = subject.rollVs(Randomizer.MAX)

        // then
        assertEquals("7 Total Damage [(7 impact damage - 2 DR) * 1.5 for cutting] = A hit with damage-name against DR 2 caused 1d+1 cut damage. Rolled 7 impact damage vs DR: 2 ", outcome.message)

    }
}

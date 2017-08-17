package org.crypticmission.gurpslack.model

import org.crypticmission.gurpslack.repositories.Randomizer
import org.junit.Test
import org.junit.Assert.*

/**
 */

class AttributeTest {
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

    @Test fun shouldFormatPositiveAttributeString() {
        // given
        val subject = Attribute("HT", 10, +3)

        // when
        val s = subject.toStringWithModifiers()

        // then
        assertEquals(s, "HT+3: 13")

    }

    @Test fun shouldFormatNegativeAttributeString() {
        // given
        val subject = Attribute("HT", 10, -3)

        // when
        val s = subject.toStringWithModifiers()

        // then
        assertEquals(s, "HT-3: 7")

    }

    @Test fun shouldSucccedWhenRollIsLower() {
        // given
        val subject = Attribute("HT", 10, +3)

        // when
        val outcome = subject.roll(ROLL_3)

        // then
        assertEquals(true, outcome.isSuccess)
        assertEquals(4, outcome.margin)
        assertEquals(false, outcome.isCritical)
        assertEquals("SUCCESS: A roll of :d6-3: :d6-3: :d6-3: => 9 vs HT+3 (13) was a success with a margin of success of 4\n", outcome.toString())
    }

    @Test fun shouldCriticallyFailWhenRollIs18() {
        // given
        val subject = Attribute("HT", 10, +3)

        // when
        val outcome = subject.roll(ROLL_6)

        // then
        assertEquals(false, outcome.isSuccess)
        assertEquals(5, outcome.margin)
        assertEquals(true, outcome.isCritical)
        assertEquals("CRITICAL FAILURE: A roll of :d6-6: :d6-6: :d6-6: => 18 vs HT+3 (13) was a critical failure with a margin of failure of 5\n", outcome.toString())
    }

    @Test fun shouldCriticallySucceedWhenRollIs3() {
        // given
        val subject = Attribute("HT", 10, +3)

        // when
        val outcome = subject.roll(ROLL_1)

        // then
        assertEquals(true, outcome.isSuccess)
        assertEquals(10, outcome.margin)
        assertEquals(true, outcome.isCritical)
        assertEquals("CRITICAL SUCCESS: A roll of :d6-1: :d6-1: :d6-1: => 3 vs HT+3 (13) was a critical success with a margin of success of 10\n", outcome.toString())
    }

    @Test fun shouldFailWhenRollIsHigher() {
        // given
        val subject = Attribute("HT", 10, +3)

        // when
        val outcome = subject.roll(ROLL_5)

        // then
        assertEquals(false, outcome.isSuccess)
        assertEquals(2, outcome.margin)
        assertEquals(false, outcome.isCritical)
        assertEquals("FAILURE: A roll of :d6-5: :d6-5: :d6-5: => 15 vs HT+3 (13) was a failure with a margin of failure of 2\n", outcome.toString())
    }
}

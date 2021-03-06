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
        val subject = Attribute("HT", 10).modify(+3)

        // when
        val s = subject.toString()

        // then
        assertEquals(s, "HT+3 (13)")

    }

    @Test fun shouldFormatNegativeAttributeString() {
        // given
        val subject = Attribute("HT", 10).modify(-3)

        // when
        val s = subject.toString()

        // then
        assertEquals(s, "HT-3 (7)")

    }

    @Test fun shouldSucccedWhenRollIsLower() {
        // given
        val subject = Attribute("HT", 10)

        // when
        val outcome = subject.rollWithModifier(+3, ROLL_3)

        // then
        assertEquals(true, outcome.isSuccess)
        assertEquals(4, outcome.margin)
        assertEquals(false, outcome.isCritical)
    }

    @Test fun shouldCriticallyFailWhenRollIs18() {
        // given
        val subject = Attribute("HT", 10)

        // when
        val outcome = subject.rollWithModifier(+3, ROLL_6)

        // then
        assertEquals(false, outcome.isSuccess)
        assertEquals(5, outcome.margin)
        assertEquals(true, outcome.isCritical)
    }

    @Test fun shouldCriticallySucceedWhenRollIs3() {
        // given
        val subject = Attribute("HT", 10)

        // when
        val outcome = subject.rollWithModifier(+3, ROLL_1)

        // then
        assertEquals(true, outcome.isSuccess)
        assertEquals(10, outcome.margin)
        assertEquals(true, outcome.isCritical)
    }

    @Test fun shouldFailWhenRollIsHigher() {
        // given
        val subject = Attribute("HT", 10)

        // when
        val outcome = subject.rollWithModifier(+3, ROLL_5)

        // then
        assertEquals(false, outcome.isSuccess)
        assertEquals(2, outcome.margin)
        assertEquals(false, outcome.isCritical)
    }
}

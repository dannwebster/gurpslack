package org.crypticmission.gurpslack.model

import org.crypticmission.gurpslack.repositories.Randomizer
import org.junit.Test
import org.junit.Assert.*
import org.junit.Rule
import org.junit.rules.ExpectedException

/**
 */
class RollSpecTest {
    @Test fun shouldCreateMaxValuesWhenRolledWithConstRandomizerAndIntValues() {
        // given
        val subject = RollSpec(3, 6, 1)

        // when
        val max = subject.roll(Randomizer.MAX)

        // then
        assertEquals(3 * 6 + 1, max.total)
        assertEquals(RollOutcome(subject, listOf(6, 6, 6), 1), max)

    }

    @Test fun shouldCreateMaxValuesWhenRolledWithConstRandomizerAndStringValueWithPositiveMod() {
        // given
        val spec = "3d6+1"
        val subject = parseRollSpec(spec) ?: throw IllegalArgumentException()

        // when
        val max = subject.roll(Randomizer.MAX)

        // then
        assertEquals(3 * 6 + 1, max.total)
        assertEquals(RollOutcome(subject, listOf(6, 6, 6), 1), max)

    }

    @Test fun shouldCreateMaxValuesWhenRolledWithConstRandomizerAndStringValueWithCapitalD() {
        // given
        val spec = "3D6+1"
        val subject = parseRollSpec(spec) ?: throw IllegalArgumentException()

        // when
        val max = subject.roll(Randomizer.MAX)

        // then
        assertEquals(3 * 6 + 1, max.total)
        assertEquals(RollOutcome(subject, listOf(6, 6, 6), 1), max)

    }

    @Test fun shouldCreateMaxValuesWhenRolledWithConstRandomizerAndStringValueWithNegativeMod() {
        // given
        val spec = "3d6-1"
        val subject = parseRollSpec(spec) ?: throw IllegalArgumentException()

        // when
        val max = subject.roll(Randomizer.MAX)

        // then
        assertEquals(3 * 6 - 1, max.total)
        assertEquals(RollOutcome(subject, listOf(6, 6, 6), -1), max)

    }

    @Test fun shouldCreateMaxValuesWhenRolledWithConstRandomizerAndStringValueNoMod() {
        // given
        val spec = "3d6"
        val subject = parseRollSpec(spec) ?: throw IllegalArgumentException()

        // when
        val max = subject.roll(Randomizer.MAX)

        // then
        assertEquals(3 * 6 + 0, max.total)
        assertEquals(RollOutcome(subject, listOf(6, 6, 6), 0), max)

    }

    @Test fun shouldCreateMaxValuesWhenRolledWithConstRandomizerAndImplicitDice() {
        // given
        val spec = "d6+1"
        val subject = parseRollSpec(spec) ?: throw IllegalArgumentException()

        // when
        val max = subject.roll(Randomizer.MAX)

        // then
        assertEquals(1 * 6 + 1, max.total)

    }

    @Test fun shouldCreateMaxValuesWhenRolledWithConstRandomizerAndImplicitDiceAndNoMod() {
        // given
        val spec = "d6"
        val subject = parseRollSpec(spec) ?: throw IllegalArgumentException()

        // when
        val max = subject.roll(Randomizer.MAX)

        // then
        assertEquals(1 * 6 + 0, max.total)
        assertEquals(RollOutcome(subject, listOf(6), 0), max)

    }

    @Test fun shouldUse6WhenDiceIsImplicitWithAdds() {
        // given
        val spec = "1d+1"

        // when
        val subject = parseRollSpec(spec) ?: throw IllegalArgumentException()

        // then
        assertEquals("1d6+1", subject.canonical)
    }

    @Test fun shouldUse6WhenDiceIsImplicitNoAdds() {
        // given
        val spec = "2d"

        // when
        val subject = parseRollSpec(spec) ?: throw IllegalArgumentException()

        // then
        assertEquals("2d6", subject.canonical)
    }


    @Rule @JvmField val ex = ExpectedException.none()

    @Test fun shouldReturnNullWhenRollSpecIsInvalid() {
        // given
        val spec = "foo"

        // when
        val subject = parseRollSpec(spec)

        // then
        assertNull(subject)

    }

    @Test fun shouldCreateEqualRollSpecWhenParsingToString() {
        // given
        val a = parseRollSpec("3d6+3") // full
        val b = parseRollSpec("3D6+3") // full with Capital D
        val c = parseRollSpec("3d6") // implicit Ads
        val d = parseRollSpec("d6+3") // implicit dice
        val e = parseRollSpec("d6") // implicit dice and adds
        val f = parseRollSpec("1d") // implicit dice and adds

        // when

        // then
        assertEquals(a, parseRollSpec(a.toString()))
        assertEquals(b, parseRollSpec(b.toString()))
        assertEquals(c, parseRollSpec(c.toString()))
        assertEquals(d, parseRollSpec(d.toString()))
        assertEquals(e, parseRollSpec(e.toString()))
        assertEquals(f, parseRollSpec(f.toString()))
    }

}
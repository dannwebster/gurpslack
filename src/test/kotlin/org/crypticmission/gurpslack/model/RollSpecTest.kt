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
        assertEquals(3 * 6 + 1, max)

    }

    @Test fun shouldCreateMaxValuesWhenRolledWithConstRandomizerAndStringValueWithPositiveMod() {
        // given
        val spec = "3d6+1"
        val subject = RollSpec.spec(spec)

        // when
        val max = subject.roll(Randomizer.MAX)

        // then
        assertEquals(3 * 6 + 1, max)

    }

    @Test fun shouldCreateMaxValuesWhenRolledWithConstRandomizerAndStringValueWithCapitalD() {
        // given
        val spec = "3D6+1"
        val subject = RollSpec.spec(spec)

        // when
        val max = subject.roll(Randomizer.MAX)

        // then
        assertEquals(3 * 6 + 1, max)

    }

    @Test fun shouldCreateMaxValuesWhenRolledWithConstRandomizerAndStringValueWithNegativeMod() {
        // given
        val spec = "3d6-1"
        val subject = RollSpec.spec(spec)

        // when
        val max = subject.roll(Randomizer.MAX)

        // then
        assertEquals(3 * 6 - 1, max)

    }

    @Test fun shouldCreateMaxValuesWhenRolledWithConstRandomizerAndStringValueNoMod() {
        // given
        val spec = "3d6"
        val subject = RollSpec.spec(spec)

        // when
        val max = subject.roll(Randomizer.MAX)

        // then
        assertEquals(3 * 6 + 0, max)

    }

    @Test fun shouldCreateMaxValuesWhenRolledWithConstRandomizerAndImplicitDice() {
        // given
        val spec = "d6+1"
        val subject = RollSpec.spec(spec)

        // when
        val max = subject.roll(Randomizer.MAX)

        // then
        assertEquals(1 * 6 + 1, max)

    }

    @Test fun shouldCreateMaxValuesWhenRolledWithConstRandomizerAndImplicitDiceAndNoMod() {
        // given
        val spec = "d6"
        val subject = RollSpec.spec(spec)

        // when
        val max = subject.roll(Randomizer.MAX)

        // then
        assertEquals(1 * 6 + 0, max)

    }

    @Test fun shouldUse6WhenDiceIsImplicitWithAdds() {
        // given
        val spec = "1d+1"

        // when
        val subject = RollSpec.spec(spec)

        // then
        assertEquals("1d6+1", subject.canonical)
    }

    @Test fun shouldUse6WhenDiceIsImplicitNoAdds() {
        // given
        val spec = "2d"

        // when
        val subject = RollSpec.spec(spec)

        // then
        assertEquals("2d6", subject.canonical)
    }


    @Rule @JvmField val ex = ExpectedException.none()

    @Test fun shouldThrowExceptionWhenRollSpecIsInvalid() {
        // given
        val spec = "foo"
        ex.expect(IllegalArgumentException::class.java)
        ex.expectMessage("'foo' is not a valid roll specification")
        ex.reportMissingExceptionWithMessage("Should be invalid specification ${spec}")

        // when
        RollSpec.spec(spec) // throws exception

        // then

    }

    @Test fun shouldCreateEqualRollSpecWhenParsingToString() {
        // given
        val a = RollSpec.spec("3d6+3") // full
        val b = RollSpec.spec("3D6+3") // full with Capital D
        val c = RollSpec.spec("3d6") // implicit Ads
        val d = RollSpec.spec("d6+3") // implicit dice
        val e = RollSpec.spec("d6") // implicit dice and adds
        val f = RollSpec.spec("1d") // implicit dice and adds

        // when

        // then
        assertEquals(a, RollSpec.spec(a.toString()))
        assertEquals(b, RollSpec.spec(b.toString()))
        assertEquals(c, RollSpec.spec(c.toString()))
        assertEquals(d, RollSpec.spec(d.toString()))
        assertEquals(e, RollSpec.spec(e.toString()))
        assertEquals(f, RollSpec.spec(f.toString()))
    }

}
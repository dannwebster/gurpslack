package org.crypticmission.gurpslack.commands

import org.crypticmission.gurpslack.util.Randomizer
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

    @Rule @JvmField val ex = ExpectedException.none()

    @Test fun shouldThrowExceptionWhenRollSpecIsInvalid() {
        // given
        val spec = "foo"
        ex.expect(IllegalArgumentException::class.java)
        ex.expectMessage("'foo' is not a valid roll specification")
        ex.reportMissingExceptionWithMessage("Should be invalid specification ${spec}")

        // when
        val subject = RollSpec.spec(spec)

        // then

    }

}
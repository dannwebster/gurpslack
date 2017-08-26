package org.crypticmission.gurpslack.model

import org.crypticmission.gurpslack.repositories.Randomizer
import org.junit.Assert.*
import org.junit.Test

/**
 */
class CharacterRollerTest {
    @Test fun shouldCreatProperMessageWhenMessageCalled() {
        // given
        val subject = CharacterRoller(Randomizer.MAX, "Foo Bar")

        subject.addAttribute(Attribute("ST", 10))
        subject.addAttribute(Attribute("DX", 14))
        subject.addAttribute(Attribute("IQ", 12))
        subject.addAttribute(Attribute("HT", 13))

        subject.addSkill(Attribute("Area Knowledge (USA)", 15))

        subject.addAttack(Attack("punch", RollSpec.DEFAULT.toDamage(DamageType.cru)))
        subject.addAttack(Attack("sword slash", RollSpec.DEFAULT.toDamage(DamageType.cut)))
        subject.addAttack(Attack("sword stab", RollSpec.DEFAULT.toDamage(DamageType.imp)))
        subject.addAttack(Attack("sword stab", RollSpec(1,6).toDamage(DamageType.imp)))

        val expected =
"""*Character Name: Foo Bar*
_*Attributes:*_
    ST: 10
    DX: 14
    IQ: 12
    HT: 13
_*Skills:*_
    Area Knowledge (USA): 15
_*Attacks*_:
    punch: 3d6 cru
    sword slash: 3d6 cut
    sword stab: 1d6 imp
"""

        // when
        val message = richMessage(subject).text

        // then
        assertEquals(expected, message)

    }

    @Test fun shouldReturnNullWhenRollingMissingAttribute() {
        // given
        val subject = CharacterRoller(Randomizer.MAX, "character-name")

        // when
        val outcome = subject.rollVsAttribute("FOO", -3)

        // then
        assertNull(outcome)

    }

    @Test fun shouldRollVsAttributeWhenAttributeIsAvailable() {
        // given
        val subject = CharacterRoller(Randomizer.MAX, "character-name")
        subject.addAttribute(Attribute("BAR", 15))

        // when
        val outcome = subject.rollVsAttribute("BAR", -3)

        // then
        assertEquals("character-name rolled vs. BAR:\n" +
                "CRITICAL FAILURE: A roll of :d6-6: :d6-6: :d6-6: => 18 vs BAR-3 (12) was a critical failure with a margin of failure of 6\n", outcome.toString())
    }

    @Test fun shouldReturnNullWhenRollingMissingDamage() {
        // given
        val subject = CharacterRoller(Randomizer.MAX, "character-name")

        // when
        val attackOutcome = subject.rollAttackDamage("attack-name", 0)

        // then
        assertNull(attackOutcome)

    }
}
package org.crypticmission.gurpslack.model

import org.crypticmission.gurpslack.repositories.Randomizer
import org.junit.Assert.*
import org.junit.Test

/**
 */
class CharacterTest {
    @Test fun shouldCreatProperMessageWhenMessageCalled() {
        // given
        val subject = Character("Foo Bar", Randomizer.MAX)

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
        val message = subject.toString()

        // then
        assertEquals(expected, message)

    }

    @Test fun shouldRollDefaultWhenRollingMissingAttribute() {
        // given
        val subject = Character("character-name", Randomizer.MAX)

        // when
        val outcome = subject.rollVsAttribute("FOO", -3)

        // then
        assertEquals("character-name rolled vs. FOO:\n" +
                "CRITICAL FAILURE: A roll of :d6-6: :d6-6: :d6-6: => 18 vs FOO-3 (7) was a critical failure with a margin of failure of 11\n", outcome.toString())

    }

    @Test fun shouldRollVsAttributeWhenAttributeIsAvailable() {
        // given
        val subject = Character("character-name", Randomizer.MAX)
        subject.addAttribute(Attribute("BAR", 15))

        // when
        val outcome = subject.rollVsAttribute("BAR", -3)

        // then
        assertEquals("character-name rolled vs. BAR:\n" +
                "CRITICAL FAILURE: A roll of :d6-6: :d6-6: :d6-6: => 18 vs BAR-3 (12) was a critical failure with a margin of failure of 6\n", outcome.toString())
    }

    @Test fun shouldRollVsDefaultWhenRollingMissingDamage() {
        // given
        val subject = Character("character-name", Randomizer.MAX)

        // when
        val attackOutcome = subject.rollAttackDamage("attack-name", 0)

        // then
        assertEquals("character-name rolled damage for attack-name:\n" +
                "Dealt *6* crushing damage after DR:\n" +
                "This attack causes 1d6 cru vs DR 0.\n" +
                "Rolled 1d6 => :d6-6: => 6.\n" +
                "`6 = [(6 impact damage - DR 0) * 1.0 for crushing]`\n", attackOutcome.toString())

    }
}
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

        subject.addMeleeAttack(Attack("punch", RollSpec.DEFAULT.toDamage(DamageType.cru)))
        subject.addMeleeAttack(Attack("sword slash", RollSpec.DEFAULT.toDamage(DamageType.cut)))

        subject.addRangedAttack(Attack("rifle", RollSpec.DEFAULT.toDamage(DamageType.pi)))
        subject.addRangedAttack(Attack("pistol", RollSpec(1,6).toDamage(DamageType.pi_plus)))

        val expected =
"""*Character Name: Foo Bar*
_*Primary Attributes:*_
    ST: 10
    DX: 14
    IQ: 12
    HT: 13
_*Derived Attributes:*_

_*Skills:*_
    Area Knowledge (USA): 15
_*Melee Attacks*_:
    punch: 3d6 cru
    sword slash: 3d6 cut
_*Ranged Attacks*_:
    pistol: 1d6 pi+
    rifle: 3d6 pi
"""

        // when
        val message = message(subject)

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
        val outcome = subject.rollVsAttribute("BAR", -3) ?: throw AssertionError("no successful return")

        // then
        assertEquals("character-name", outcome.characterName)
        assertEquals(true, outcome.attributeRollOutcome.isCritical)
        assertEquals(false, outcome.attributeRollOutcome.isSuccess)
        assertEquals(6, outcome.attributeRollOutcome.margin)
    }

    @Test fun shouldReturnNullWhenRollingMissingDamage() {
        // given
        val subject = CharacterRoller(Randomizer.MAX, "character-name")

        // when
        val attackOutcome = subject.rollMeleeAttackDamage("attack-name", 0)

        // then
        assertNull(attackOutcome)

    }

    @Test
    fun shouldUseRecoilAndHitsWhenCalculatingHits() {
        // no hits if no shots
        assertEquals(0, calculateHits(0, 1, 1))

        // no hits if negative margin
        assertEquals(0, calculateHits(1, -1, 1))

        // one hit on exact roll, regardless of recoil
        assertEquals(1, calculateHits(1, 0, 1))
        assertEquals(1, calculateHits(1, 0,2))
        assertEquals(1, calculateHits(1, 0, 3))

        // one extra hit per full recoil multiple of success margin (if recoil is 1) up to a max of shots fired
        assertEquals(2, calculateHits(3, 1, 1))
        assertEquals(3, calculateHits(3, 2, 1))
        assertEquals(3, calculateHits(3, 3, 1))

        // one extra hit per full recoil multiple of success margin (if recoil is 2) up to a max of shots fired
        assertEquals(1, calculateHits(3, 1,2))
        assertEquals(2, calculateHits(3, 2,2))
        assertEquals(2, calculateHits(3, 3,2))
        assertEquals(3, calculateHits(3, 4,2))
        assertEquals(3, calculateHits(3, 5,2))
        assertEquals(3, calculateHits(3, 6,2))
        assertEquals(3, calculateHits(3, 7,2))
    }
}
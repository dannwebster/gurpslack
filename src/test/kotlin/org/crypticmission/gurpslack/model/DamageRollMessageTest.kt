package org.crypticmission.gurpslack.model

import org.crypticmission.gurpslack.repositories.Randomizer
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 */
class DamageRollMessageTest {
    val rand = Randomizer.specified(2, 3)
    val rollSpec = RollSpec(2, 6, 4)
    val damageSpec = DamageSpec(rollSpec, DamageType.pi_plus_plus)

    val damageRollOutcome = DamageRollOutcome.singleHit(rand, damageSpec, 2)
    val attackRollOutcome = AttackRollOutcome("Rifle", damageRollOutcome)
    val characterAttackRollOutcome = CharacterAttackRollOutcome("R. C. Cleveland", attackRollOutcome)

    val multiShotDescriptor = MultiShotDescriptor(5, 2, 5)
    val multiDamageRollOutcome = DamageRollOutcome.multiHit(rand, damageSpec, 2, multiShotDescriptor)
    val multiAttackRollOutcome = AttackRollOutcome("Rifle", multiDamageRollOutcome)
    val multiCharacterAttackRollOutcome = CharacterAttackRollOutcome("R. C. Cleveland", multiAttackRollOutcome)

    @Test
    fun shouldCreateABulletedMessageWhenDamageRollVsDr() {
        // when
        val msg = message(damageRollOutcome )

        // then
        assertEquals(
                """
                *Damage Roll:* Attack vs DR 2
                > *- Outcome:* 14 huge piercing damage after DR
                > *- Damage:* 2d6+4 pi++
                > *- DR:* 2
                > *- Roll:* 9 = :d6-2: :d6-3:+4
                > *- Details:* `14 = [(9 impact damage - DR 2) * 2.0 for huge piercing]`
                """.trimIndent(), msg)
    }

    @Test
    fun shouldCreateABulletedMessageWhenAttackRollVsDr() {
        // when
        val msg = message(attackRollOutcome)

        // then
        assertEquals(
                """
                *Damage Roll:* Attack with Rifle vs DR 2
                > *- Outcome:* 14 huge piercing damage after DR
                > *- Damage:* 2d6+4 pi++
                > *- DR:* 2
                > *- Roll:* 9 = :d6-2: :d6-3:+4
                > *- Details:* `14 = [(9 impact damage - DR 2) * 2.0 for huge piercing]`
                """.trimIndent(), msg)
    }

    @Test
    fun shouldCreateABulletedMessageWhenCharacterAttackRollVsDr() {
        // when
        val msg = message(characterAttackRollOutcome)

        // then
        assertEquals(
                """
                *Damage Roll:* R. C. Cleveland's attack with Rifle vs DR 2
                > *- Outcome:* 14 huge piercing damage after DR
                > *- Damage:* 2d6+4 pi++
                > *- DR:* 2
                > *- Roll:* 9 = :d6-2: :d6-3:+4
                > *- Details:* `14 = [(9 impact damage - DR 2) * 2.0 for huge piercing]`
                """.trimIndent(), msg)
    }

    @Test
    fun shouldCreateABulletedMessageWhenMultiDamageRollVsDr() {
        // when
        val msg = message(multiDamageRollOutcome)

        // then
        assertEquals(
                """
                *Damage Roll:* Attack hits 3 times vs DR 2
                > *- Outcome:* 42 huge piercing damage after DR
                > *- Damage:* 2d6+4 pi++
                > *- DR:* 2
                > *- Hits:* 3 hits from 5 shots fired = `max of (5 or [1 hit + (made by 5 / recoil of 2)])`
                > *- Rolls:*
                >   • 9 = :d6-2: :d6-3:+4
                >   • 9 = :d6-2: :d6-3:+4
                >   • 9 = :d6-2: :d6-3:+4
                > *- Details:* 42 = 14 + 14 + 14
                >   • `14 = [(9 impact damage - DR 2) * 2.0 for huge piercing]`
                >   • `14 = [(9 impact damage - DR 2) * 2.0 for huge piercing]`
                >   • `14 = [(9 impact damage - DR 2) * 2.0 for huge piercing]`
                """.trimIndent(), msg)
    }

    @Test
    fun shouldCreateABulletedMessageWhenMultiAttackRollVsDr() {
        // when
        val msg = message(multiAttackRollOutcome)

        // then
        assertEquals(
                """
                *Damage Roll:* Attack with Rifle hits 3 times vs DR 2
                > *- Outcome:* 42 huge piercing damage after DR
                > *- Damage:* 2d6+4 pi++
                > *- DR:* 2
                > *- Hits:* 3 hits from 5 shots fired = `max of (5 or [1 hit + (made by 5 / recoil of 2)])`
                > *- Rolls:*
                >   • 9 = :d6-2: :d6-3:+4
                >   • 9 = :d6-2: :d6-3:+4
                >   • 9 = :d6-2: :d6-3:+4
                > *- Details:* 42 = 14 + 14 + 14
                >   • `14 = [(9 impact damage - DR 2) * 2.0 for huge piercing]`
                >   • `14 = [(9 impact damage - DR 2) * 2.0 for huge piercing]`
                >   • `14 = [(9 impact damage - DR 2) * 2.0 for huge piercing]`
                """.trimIndent(), msg)
    }

    @Test
    fun shouldCreateBulletedMessageWithMultipleLinesWhenAttackRollWithMultipleHits() {
        // when
        val msg = message(multiCharacterAttackRollOutcome)

        // then
        assertEquals(
                """
                *Damage Roll:* R. C. Cleveland's attack with Rifle hits 3 times vs DR 2
                > *- Outcome:* 42 huge piercing damage after DR
                > *- Damage:* 2d6+4 pi++
                > *- DR:* 2
                > *- Hits:* 3 hits from 5 shots fired = `max of (5 or [1 hit + (made by 5 / recoil of 2)])`
                > *- Rolls:*
                >   • 9 = :d6-2: :d6-3:+4
                >   • 9 = :d6-2: :d6-3:+4
                >   • 9 = :d6-2: :d6-3:+4
                > *- Details:* 42 = 14 + 14 + 14
                >   • `14 = [(9 impact damage - DR 2) * 2.0 for huge piercing]`
                >   • `14 = [(9 impact damage - DR 2) * 2.0 for huge piercing]`
                >   • `14 = [(9 impact damage - DR 2) * 2.0 for huge piercing]`
                """.trimIndent(), msg)

    }
}
package org.crypticmission.gurpslack.model

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 */
class DamageRollMessageTest {
    val rollSpec = RollSpec(2, 6, 4)
    val damageSpec = DamageSpec(rollSpec, DamageType.pi_plus_plus)
    val rollOutcome = RollOutcome(rollSpec, listOf(2, 3), 4)
    val damageRollOutcome = DamageRollOutcome(damageSpec, rollOutcome, 2)
    val attackRollOutcome = AttackRollOutcome("Rifle", damageRollOutcome)
    val characterAttackRollOutcome = CharacterAttackRollOutcome("R. C. Cleveland", attackRollOutcome)

    @Test
    fun shouldCreateABulletedMessageWhenDamageRollVsDr() {
        // when
        val msg = message(damageRollOutcome )

        // then
        assertEquals(
                """
                *Damage Roll:* Attack vs DR 2
                > *- Outcome*: 14 huge piercing damage after DR
                > *- Roll*: :d6-2: :d6-3:+4 = 9
                > *- Damage:* 2d6+4 pi++
                > *- DR*: 2
                > *- Details:* `[(9 impact damage - DR 2) * 2.0 for huge piercing] = 14`
                """.trimIndent(), msg)
    }

    @Test
    fun shouldCreateABulletedMessageWhenAttackRollVsDr() {
        // when
        val msg = message(attackRollOutcome)

        // then
        assertEquals(
                """
                *Damage Roll:* Attacks with Rifle vs DR 2
                > *- Outcome*: 14 huge piercing damage after DR
                > *- Roll*: :d6-2: :d6-3:+4 = 9
                > *- Damage:* 2d6+4 pi++
                > *- DR*: 2
                > *- Details:* `[(9 impact damage - DR 2) * 2.0 for huge piercing] = 14`
                """.trimIndent(), msg)
    }

    @Test
    fun shouldCreateABulletedMessageWhenCharaacterAttackRollVsDr() {
        // when
        val msg = message(characterAttackRollOutcome)

        // then
        assertEquals(
                """
                *Damage Roll:* R. C. Cleveland Attacks with Rifle vs DR 2
                > *- Outcome*: 14 huge piercing damage after DR
                > *- Roll*: :d6-2: :d6-3:+4 = 9
                > *- Damage:* 2d6+4 pi++
                > *- DR*: 2
                > *- Details:* `[(9 impact damage - DR 2) * 2.0 for huge piercing] = 14`
                """.trimIndent(), msg)
    }
}
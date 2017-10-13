package org.crypticmission.gurpslack.model

import org.crypticmission.gurpslack.message.message
import org.crypticmission.gurpslack.message.toTitleCase
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 */
class AttributeRollMessagesTest {
    val rollSpec = RollSpec(3,6)
    val greatRollOutcome = RollOutcome(rollSpec, listOf(1, 1, 1), 0)
    val goodRollOutcome = RollOutcome(rollSpec, listOf(3, 3, 3), 0)
    val badRollOutcome = RollOutcome(rollSpec, listOf(5, 5, 5), 0)
    val terribleRollOutcome = RollOutcome(rollSpec, listOf(6, 6, 6), 0)

    val attribute = Attribute("HT", 11, rollSpec)
    val unmodifiedAttribute = attribute.modify(0)
    val plussedAttribute = attribute.modify(2)
    val minusedAttribute = attribute.modify(-2)

    val unmodifiedAttributeRollCriticalSuccess = AttributeRollOutcome(unmodifiedAttribute, greatRollOutcome)
    val unmodifiedAttributeRollSuccess = AttributeRollOutcome(unmodifiedAttribute, goodRollOutcome)
    val unmodifiedAttributeRollFailure = AttributeRollOutcome(unmodifiedAttribute, badRollOutcome)
    val unmodifiedAttributeRollCriticalFailure = AttributeRollOutcome(unmodifiedAttribute, terribleRollOutcome)

    val plussedAttributeRollCriticalSuccess = AttributeRollOutcome(plussedAttribute, greatRollOutcome)
    val minusedAttributeRollCriticalSuccess = AttributeRollOutcome(minusedAttribute, greatRollOutcome)

    val characterRollOutcome = CharacterAttributeRollOutcome("Foo Barbazon", minusedAttributeRollCriticalSuccess)
    @Test
    fun shouldTitleCaseStringsWhenUsingStringDotToTitleCase() {
        assertEquals("Things", "things".toTitleCase())
        assertEquals("Things To Do", "things to do".toTitleCase())
        assertEquals("Things To Do", "THINGS TO DO".toTitleCase())
        assertEquals("Things To Do", "tHiNgS tO Do".toTitleCase())
    }

    @Test
    fun shouldCreateASimpleMessageWhenMessagingAnAttribute() {
        // when
        val msg = message(attribute)

        // then
        assertEquals("HT: 11", msg)
    }

    @Test
    fun shouldCreateABulletPointedMessageWhenMessagingACriticalSuccessRollOutcome() {
    // when
        val msg = message(unmodifiedAttributeRollCriticalSuccess)

        // then
        assertEquals(
                """
                *Roll:* Rolled vs HT (11)
                > *- Outcome:* Critical Success
                > *- Roll:* :d6-1: :d6-1: :d6-1: = 3
                > *- Effective Level:* 11
                > *- Margin of Success:* 8
                > *- Attribute:* HT: 11
                > *- Modifier:* +0
                """.trimIndent(), msg)
    }
    @Test
    fun shouldCreateABulletPointedMessageWhenMessagingASuccessRollOutcome() {
        // when
        val msg = message(unmodifiedAttributeRollSuccess)

        // then
        assertEquals(
                """
                *Roll:* Rolled vs HT (11)
                > *- Outcome:* Success by 2
                > *- Roll:* :d6-3: :d6-3: :d6-3: = 9
                > *- Effective Level:* 11
                > *- Margin of Success:* 2
                > *- Attribute:* HT: 11
                > *- Modifier:* +0
                """.trimIndent(), msg)
    }

    @Test
    fun shouldCreateABulletPointedMessageWhenMessagingAFailureRollOutcome() {
        // when
        val msg = message(unmodifiedAttributeRollFailure)

        // then
        assertEquals(
                """
                *Roll:* Rolled vs HT (11)
                > *- Outcome:* Failure by 4
                > *- Roll:* :d6-5: :d6-5: :d6-5: = 15
                > *- Effective Level:* 11
                > *- Margin of Failure:* 4
                > *- Attribute:* HT: 11
                > *- Modifier:* +0
                """.trimIndent(), msg)
    }
    @Test
    fun shouldCreateABulletPointedMessageWhenMessagingACriticalFailureRollOutcome() {
        // when
        val msg = message(unmodifiedAttributeRollCriticalFailure)

        // then
        assertEquals(
                """
                *Roll:* Rolled vs HT (11)
                > *- Outcome:* Critical Failure
                > *- Roll:* :d6-6: :d6-6: :d6-6: = 18
                > *- Effective Level:* 11
                > *- Margin of Failure:* 7
                > *- Attribute:* HT: 11
                > *- Modifier:* +0
                """.trimIndent(), msg)
    }

    @Test
    fun shouldCreateABulletPointedMessageWhenMessagingAPlussedAttributeCriticalSuccessRollOutcome() {
        // when
        val msg = message(plussedAttributeRollCriticalSuccess)

        // then
        assertEquals(
                """
                *Roll:* Rolled vs HT+2 (13)
                > *- Outcome:* Critical Success
                > *- Roll:* :d6-1: :d6-1: :d6-1: = 3
                > *- Effective Level:* 13
                > *- Margin of Success:* 10
                > *- Attribute:* HT: 11
                > *- Modifier:* +2
                """.trimIndent(), msg)
    }

    @Test
    fun shouldCreateABulletPointedMessageWhenMessagingAMinusedAttributeCriticalSuccessRollOutcome() {
        // when
        val msg = message(minusedAttributeRollCriticalSuccess)

        // then
        assertEquals(
                """
                *Roll:* Rolled vs HT-2 (9)
                > *- Outcome:* Critical Success
                > *- Roll:* :d6-1: :d6-1: :d6-1: = 3
                > *- Effective Level:* 9
                > *- Margin of Success:* 6
                > *- Attribute:* HT: 11
                > *- Modifier:* -2
                """.trimIndent(), msg)
    }

    @Test
    fun shouldCreateABulletPointedMessageWhenMessagingAMinusedAttributeCriticalSuccessCharacterRollOutcome() {
        // when
        val msg = message(characterRollOutcome)

        // then
        assertEquals(
                """
                *Roll:* Foo Barbazon Rolled vs HT-2 (9)
                > *- Outcome:* Critical Success
                > *- Roll:* :d6-1: :d6-1: :d6-1: = 3
                > *- Effective Level:* 9
                > *- Margin of Success:* 6
                > *- Attribute:* HT: 11
                > *- Modifier:* -2
                """.trimIndent(), msg)
    }
}
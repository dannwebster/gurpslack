package org.crypticmission.gurpslack.model

import org.junit.Assert.*
import org.junit.Test

/**
 */
class TrackedValueTest {
    fun isContiguous(name: String, maxValue: Int, trackedValue: TrackedValue) {
        val max = trackedValue.maxValue
        assertEquals(max, trackedValue.effects.first().range.first)
        val min = trackedValue.minValue
//        println("\t Testing ${name}=${maxValue} for range ${max}..${min}")
        (max downTo min).forEach { currentValue ->
            trackedValue.currentValue = currentValue
            val effect = trackedValue.effect()
//            with (effect){
//                println("\t\t\trange for ${name}=${maxValue} at value=${currentValue}: ${range} -> ${status} : ${details}")
//            }
        }
    }

    fun isContiguousForRange(name: String, range: IntRange, trackedValuesEffectDescriptions: List<TrackedValueEffectDescription>) {
//        println("*** Testing ${name} ***")
        range.forEach { maxValue ->
            val trackedValue = TrackedValue.create(name, name, maxValue, maxValue, trackedValuesEffectDescriptions)
            isContiguous(name, maxValue, trackedValue)
//            trackedValue.effects.forEach {
//                println(it.toString())
//            }
        }
    }

    @Test
    fun shouldCalculateCorrectMinValuesWhenCreatingStats() {
        assertEquals(-99, TrackedValue.hp(10, 10).minValue)
        assertEquals(-9, TrackedValue.fp(10, 10).minValue)
        assertEquals(-9, TrackedValue.wp(10, 10).minValue)
        assertEquals(0, TrackedValue.ammo("pistol", 10, 10).minValue)

    }
    @Test
    fun shouldIncrementCurrentValueWhenUsingPlusEqualsOnAMaxedStat() {
        // given
        val subject = TrackedValue.fp(11, 11)

        // when
        subject += 1

        // then
        assertEquals(11, subject.maxValue)
        assertEquals(12, subject.currentValue)
    }
    @Test
    fun shouldAllowCurrentGreaterThanMax() {
        // given
        val subject = TrackedValue.fp(11, 12)

        // then
        assertEquals(11, subject.maxValue)
        assertEquals(12, subject.currentValue)
    }
    @Test
    fun willpowerDescriptionsShouldBeContiguousForWPsOf1to20() {
        isContiguousForRange("WP", 1..20, WP_TRACKED_VALUE_EFFECT_DESCRIPTORS)
    }

    @Test
    fun hitPointDescriptionsShouldBeContiguousForWPsOf1to20() {
        isContiguousForRange("HP", 1..20, HP_TRACKED_VALUE_EFFECT_DESCRIPTORS)
    }
    @Test
    fun fatigueDescriptionsShouldBeContiguousForHPsOf1to20() {
        isContiguousForRange("FP", 1..20, FP_TRACKED_VALUE_EFFECT_DESCRIPTORS)
    }

    @Test
    fun ammoDescriptionsShouldBeContiguousForHPsOf1to20() {
        isContiguousForRange("Ammo", 1..20, AMMO_TRACKED_VALUE_EFFECT_DESCRIPTORS)
    }
}
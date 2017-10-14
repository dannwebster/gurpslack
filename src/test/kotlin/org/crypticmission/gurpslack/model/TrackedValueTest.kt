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
package org.crypticmission.gurpslack.util

import java.security.SecureRandom
import java.time.Clock
import java.time.Instant

/**
 */

interface Randomizer {
    companion object {
        val MAX = AlwaysMaxRandomizer()
        fun specified(value: Int) = SpecifiedRandomizer(value)
        fun system() = SystemRandomizer(Clock.systemUTC().instant())
        val MIN = specified(1)
    }
    fun random(max: Int): Int
}

class SpecifiedRandomizer(val value: Int): Randomizer {
    override fun random(max: Int) =
            if (value <= max) value
            else throw IllegalArgumentException("value ${value} > max {$max}")
}

class AlwaysMaxRandomizer(): Randomizer {
    override fun random(max: Int) = max
}

class SystemRandomizer(seed: Instant): Randomizer {
    val seedArray = arrayOf(
        (seed.toEpochMilli() and 0xFF000000).toByte(),
        (seed.toEpochMilli() and 0xFF000000).toByte(),
        (seed.toEpochMilli() and 0xFF000000).toByte(),
        (seed.toEpochMilli() and 0xFF000000).toByte()
    ).toByteArray()

    val rand = SecureRandom(seedArray)

    override fun random(max: Int) = rand.nextInt(max) + 1
}

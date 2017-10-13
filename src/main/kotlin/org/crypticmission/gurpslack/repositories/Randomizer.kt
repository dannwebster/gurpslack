package org.crypticmission.gurpslack.repositories

import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.time.Clock
import java.time.Instant
import javax.swing.plaf.synth.SynthEditorPaneUI

/**
 */

@Component
class ComponentRandomizer() : SystemRandomizer(Clock.systemUTC().instant())

interface Randomizer {
    companion object {
        val MAX = AlwaysMaxRandomizer()
        fun specified(vararg values: Int) = SpecifiedRandomizer(*values)
        fun system() = SystemRandomizer(Clock.systemUTC().instant())
        val MIN = specified(1)
    }
    fun random(max: Int): Int
}

class SpecifiedRandomizer(vararg val values: Int): Randomizer {
    var current = 0
    override fun random(max: Int): Int {
        if (values[current] > max) throw IllegalArgumentException("value values[${current}]=${values[current]} > max {$max}")
        val value = values[current]
        current = (current + 1) % values.size
        return value
    }
}

class AlwaysMaxRandomizer(): Randomizer {
    override fun random(max: Int) = max
}

open class SystemRandomizer(seed: Instant): Randomizer {
    val seedArray = arrayOf(
        (seed.toEpochMilli() and 0xFF000000).toByte(),
        (seed.toEpochMilli() and 0x00FF0000).toByte(),
        (seed.toEpochMilli() and 0x0000FF00).toByte(),
        (seed.toEpochMilli() and 0x000000FF).toByte()
    ).toByteArray()

    val rand = SecureRandom(seedArray)

    override fun random(max: Int) = rand.nextInt(max) + 1
}

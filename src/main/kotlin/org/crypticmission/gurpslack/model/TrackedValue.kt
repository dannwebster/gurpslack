package org.crypticmission.gurpslack.model


fun IntProgression.containsExcludeLast(i: Int) =
        if (i == this.last)
            false
        else
            this.contains(i)

data class TrackedValue private constructor(val name: String,
                        val shortName: String,
                        val maxValue: Int,
                        val effects: List<TrackedValueEffect>) {

    val minValue = effects.last().range.last + 1

    var currentValue: Int = maxValue
        set(newValue: Int) =
            if (newValue >= minValue)
                field = newValue
            else
                throw IllegalStateException("${newValue} for ${name} (${shortName}) is less than the lowest allowed " +
                        "value of ${minValue} as defined by effect ${effects.last()}")

    companion object {
        fun create(name: String,
                   shortName: String,
                   maxValue: Int,
                   effectDescriptions: List<TrackedValueEffectDescription>) =
                TrackedValue(name, shortName, maxValue, toEffects(maxValue, effectDescriptions))

        fun toEffects(maxValue: Int, effectDescriptions: List<TrackedValueEffectDescription>) =
                effectDescriptions.map { it.toEffect(maxValue) }.filterNotNull()

        fun hp(maxValue: Int) =
                create("Hit Points", "HP", maxValue, HP_TRACKED_VALUE_EFFECT_DESCRIPTORS)

        fun fp(maxValue: Int) =
                create("Fatigue", "FP", maxValue, FP_TRACKED_VALUE_EFFECT_DESCRIPTORS)

        fun wp(maxValue: Int) =
                create("Will Power", "WP", maxValue, WP_TRACKED_VALUE_EFFECT_DESCRIPTORS)

        fun ammo(attackName: String, capacity: Int) =
                create(attackName + "Ammo", "Shots", capacity, AMMO_TRACKED_VALUE_EFFECT_DESCRIPTORS)
    }

    operator fun plusAssign(amount: Int) { this.currentValue += amount }
    operator fun minusAssign(amount: Int) { this.currentValue -= amount }

    fun effect() = effects.find { it.range.containsExcludeLast(currentValue) } ?:
            throw IllegalStateException("value ${currentValue} was not contained in the effects for ${shortName}. Effects: ${effects}")
}

data class TrackedValueEffectDescription(val startGenerator: (Int) -> Int, val endGenerator: (Int) -> Int, val status: String, val detailGenerator: (Int) -> String?) {

    constructor(startGenerator: (Int) -> Int, endGenerator: (Int) -> Int, status: String, detail: String?) :
            this(startGenerator, endGenerator, status, static(detail))

    fun toEffect(maxStatValue: Int) : TrackedValueEffect? {
        val rangeStart = startGenerator(maxStatValue)
        val rangeEnd = endGenerator(maxStatValue)
        val details = detailGenerator(maxStatValue)
        return if (rangeEnd >= rangeStart) null else TrackedValueEffect( (rangeStart downTo rangeEnd), status, details)
    }
}


data class TrackedValueEffect(val range: IntProgression, val status: String, val details: String?)

val stat = { i: Int -> i }
val oneThird = { i: Int -> i / 3 }
val twoThirds = oneThird.times(2)
val oneHalf = { i: Int -> (i / 2) }
val zero = { _: Int -> 0 }
val one = { _: Int -> 1 }

fun negative(f: (Int) -> Int) = { i: Int -> f(i) * -1}
fun ((Int) -> Int).minusOne() = { i: Int -> this(i) - 1 }
fun ((Int) -> Int).times(x: Int) = { i: Int -> this(i) * x }
fun static(s: String?) = { _: Int -> s }

val HP_TRACKED_VALUE_EFFECT_DESCRIPTORS = listOf(
        TrackedValueEffectDescription(stat, oneThird, "OK", "No Effects"),
        TrackedValueEffectDescription(oneThird, zero, "Reeling", "Halve your Move, Dodge, and ST (round up)."),
        TrackedValueEffectDescription(zero, negative(stat), "Verge of Collapse", "Do Nothing, or make an HT roll. On a failure, fall unconscious.")
) +
        (1..4).map {x ->
            TrackedValueEffectDescription(
                    negative(stat).times(x),
                    negative(stat).times(x + 1),
                "Verge of Death - Level ${x})",
                {fullHp: Int -> "At HP=-${fullHp * x}, roll vs HT or die immediately. Otherwise Do Nothing, or make an HT-${x} roll; failure causes collapse."})  } +
listOf(
        TrackedValueEffectDescription(negative(stat).times(5), negative(stat).times(5), "Instant Death", {fullHp: Int -> "At HP=-${fullHp * 5}, you die immediately"}),
        TrackedValueEffectDescription(negative(stat).times(5), negative(stat).times(10), "Dead", "Your body is being destroyed"),
        TrackedValueEffectDescription(negative(stat).times(10), negative(stat).times(10), "Total Bodily Destruction", { fullHp: Int -> "At HP=-${fullHp * 10}, your body is totally destroyed. There is nothing left of you to resurrect"})
)

val FP_TRACKED_VALUE_EFFECT_DESCRIPTORS = listOf(
        TrackedValueEffectDescription(stat, oneThird, "OK", "No Effects"),
        TrackedValueEffectDescription(oneThird, zero, "Very Tired", "Halve your Move, Dodge, and ST (round up). This does not affect ST-based quantities, such as HP and damage."),
        TrackedValueEffectDescription(zero, negative(stat), "Verge of Collapse", "More FP Loss causes HP Loss. Will or Do Nothing; failure causes collapse."),
        TrackedValueEffectDescription(negative(stat), negative(stat).minusOne(), "Unconsious", "Awaken when you reach positive FP")
)

val WP_TRACKED_VALUE_EFFECT_DESCRIPTORS = listOf(
        TrackedValueEffectDescription(stat, twoThirds, "OK", "No Effects"),
        TrackedValueEffectDescription(twoThirds, oneThird, "Vulnerable", "Will Rolls are at -2; Self Control Rolls are one degree harder; Weirdness Magnet"),
        TrackedValueEffectDescription(oneThird, zero, "Defenseless", "Will Rolls are at -5; Self Control Rolls automatically fail; Draw nearby supernatural entities"),
        TrackedValueEffectDescription(zero, negative(stat), "Raving", "Will Rolls are at -10; Self Control Disadvantages are now Compulsions; Draw distant supernatural entities"),
        TrackedValueEffectDescription(negative(stat), negative(stat).minusOne(), "Lost", "No longer a rational entity; your soul has left your body and you are a vessel for supernatural inhabitation")
)

val AMMO_TRACKED_VALUE_EFFECT_DESCRIPTORS = listOf(
        TrackedValueEffectDescription(stat, stat.minusOne(), "Full up", null),
        TrackedValueEffectDescription(stat.minusOne(), oneHalf, "More than half", null),
        TrackedValueEffectDescription(oneHalf, oneHalf.minusOne(), "Half empty", null),
        TrackedValueEffectDescription(oneHalf.minusOne(), oneThird, "Less than half", null),
        TrackedValueEffectDescription(oneThird, one, "Getting Low...", null),
        TrackedValueEffectDescription(one, zero, "Last Shot!", null),
        TrackedValueEffectDescription(zero, negative(one), "Empty", "RELOAD!")
)


package org.crypticmission.gurpslack.controllers

import org.crypticmission.gurpslack.model.*

/**
 */
val DR_REGEX = """vs\.?\s*DR\s*:?\s*(\d+)""".toRegex(RegexOption.IGNORE_CASE)

fun parseName(nameLine: String) : Pair<String, String>? {
    val parts = nameLine.split("""\s+""".toRegex())
    return when (parts.size) {
        0 -> null
        1 -> Pair(parts[0], parts[0])
        else -> Pair(parts[0], parts.drop(1).joinToString (" "))
    }
}

fun parseAttribute(attributeLine: String) : Triple<String, String, Int>? {
    val parts = attributeLine.split("""[\s:]+""".toRegex())
    return when (parts.size) {
        2 -> Triple(parts[0], parts[1], 10)
        3 -> Triple(parts[0], parts[1], parts[2].toInt())
        else -> null
    }
}

fun parseDamage(damageLine: String) : DamageSpec? {
    val rollSpecString = firstValue(RollSpec.REGEX, damageLine) ?: return null
    val damageTypeString = firstValue(DamageType.SHORT_REGEX, damageLine)
    val damageType = damageTypeString?.dmgType() ?: DamageType.cru
    return RollSpec.fromString(rollSpecString)?.toDamage(damageType)
}


fun parseDr(damageLine: String) = DR_REGEX.find(damageLine)?.groupValues?.get(1)?.toInt() ?: 0
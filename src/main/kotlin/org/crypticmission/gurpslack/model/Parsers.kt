package org.crypticmission.gurpslack.model

/**
 */
val DR_REGEX = """vs\.?\s*DR\s*:?\s*(\d+)""".toRegex(RegexOption.IGNORE_CASE)
val ROLL_SPEC_REGEX: Regex = """(\d*)[Dd](\d+)?([\+-]\d+)?""".toRegex()
val LONG_DAMAGE_TYPE_REGEX = DamageType.values()
        .joinToString("|")
        .toRegex()

val MODIFIER_REGEX = """(\s*[+-]\d+)$""".toRegex()
val SHORT_DAMAGE_TYPE_REGEX = DamageType.values()
        .map { it.shortForm }
        .map { it.replace("""\+""".toRegex(), """\\+""") }
        .joinToString("|")
        .toRegex()

fun String.tokenize() = this.split("""\s+""".toRegex())

fun MatchResult.d(index: Int, default: Int) =
                if (this.groupValues[index] == "") default else this.groupValues[index].toInt()


fun firstValue(regex: Regex, text: String) : String? {
    val m = regex.find(text)
    m ?: return null
    val g = m.groups[0]
    g ?: return null
    return text.substring(g.range)
}

fun parseName(nameLine: String) : Pair<String, String>? {
    val parts = nameLine.tokenize()
    return when (parts.size) {
        in 0..1 -> null
        else -> Pair(parts[0], parts.drop(1).joinToString (" "))
    }
}

fun parseAttribute(attributeLine: String) : Pair<String, Attribute>? {
    val parts = attributeLine.tokenize()
    return when (parts.size) {
        in 0..2 -> null
        else -> Pair(parts.first(), Attribute(parts.drop(1).dropLast(1).joinToString(" "), parts.last().toInt()))
    }
}

fun parseLongDamageType(longForm: String) = DamageType.values().find { type -> type.longForm.equals(longForm) }
fun parseShortDamageType(shortForm: String) = DamageType.values().find { type -> type.shortForm.equals(shortForm) }

fun parseDamageType(damageType: String?) = when(damageType) {
    null -> DamageType.cru
    else -> parseLongDamageType(damageType) ?: parseShortDamageType(damageType) ?: DamageType.cru
}

fun parseRollSpec(rollSpec: String) : RollSpec? {
    val matchResult = ROLL_SPEC_REGEX.matchEntire(rollSpec.trim())
    matchResult ?: return null
    val dice = matchResult.d(1, 1)
    val sides = matchResult.d(2, 6)
    val adds = matchResult.d(3, 0)

    return RollSpec(dice, sides, adds)
}

fun parseDamage(damageLine: String) : DamageSpec? {
    val rollSpecString = firstValue(ROLL_SPEC_REGEX, damageLine) ?: return null
    val damageTypeString = firstValue(SHORT_DAMAGE_TYPE_REGEX, damageLine)
    val damageType = parseDamageType(damageTypeString)
    return parseRollSpec(rollSpecString)?.toDamage(damageType)
}

fun parseAttack(attackLine: String) : Pair<String, Attack>? {
    val parts = attackLine.tokenize()
    return when (parts.size) {
        in 0..3 -> null
        else -> {
            val key = parts[0]
            val attackName = parts[1]
            val rollSpec = parseRollSpec(parts[2])
            val type = parseDamageType(parts[3])
            when (rollSpec) {
                null -> null
                else -> {
                    val damage = DamageSpec(rollSpec, type)
                    val attack = Attack(attackName, damage)
                    return Pair(key, attack)
                }
            }
        }
    }
}


fun parseVsData(vsDataLine: String): Triple<String, String, Int>? {
    val data = vsDataLine.tokenize()
    return when (data.size) {
        in 0..1 -> null
        else -> {
            val characterKey = data.first()
            val modString = firstValue(MODIFIER_REGEX, data.last()) ?: ""
            val mod = modString.toIntOrNull() ?: 0
            val name = data.drop(1).joinToString(" ").dropLast(modString.length).trim()
            Triple(characterKey, name, mod)
        }
    }
}

fun parseDr(damageLine: String) = DR_REGEX.find(damageLine)?.groupValues?.get(1)?.toInt() ?: 0
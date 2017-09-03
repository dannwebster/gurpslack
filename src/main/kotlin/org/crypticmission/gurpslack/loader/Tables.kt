package org.crypticmission.gurpslack.loader

import org.crypticmission.gurpslack.model.DamageSpec
import org.crypticmission.gurpslack.model.RollSpec
import org.crypticmission.gurpslack.model.parseRollSpec

/**
 */
fun relativeLevel(difficulty: String, points: Int) : Int {
    val offset = when (difficulty) {
        "E" -> 0
        "A" -> -1
        "H" -> -2
        "VH" -> -3
        else -> throw IllegalArgumentException("don't understand difficulty '${difficulty}'")
    }
    val level = when (points) {
        1 -> 0
        in 2..3 -> 1
        else -> 1 + (points/4)
    }
    return offset + level
}

fun getLevel(characterData: CharacterData, baseAttribute: String, difficulty:String, points: Int) : Int {
    val attribute = characterData.attributes.get(baseAttribute) ?: throw IllegalStateException("no attribute called '${baseAttribute}'")
    val relativeLevel = relativeLevel(difficulty, points)
    return attribute.level + relativeLevel
}

/*
1 1d-6 1d-5 27 3d-1 5d+1
2 1d-6 1d-5 28 3d-1 5d+1
3 1d-5 1d-4 29 3d 5d+2
4 1d-5 1d-4 30 3d 5d+2
5 1d-4 1d-3 31 3d+1 6d-1
6 1d-4 1d-3 32 3d+1 6d-1
7 1d-3 1d-2 33 3d+2 6d
8 1d-3 1d-2 34 3d+2 6d
9 1d-2 1d-1 35 4d-1 6d+1
10 1d-2 1d 36 4d-1 6d+1
11 1d-1 1d+1 37 4d 6d+2
12 1d-1 1d+2 38 4d 6d+2
13 1d 2d-1 39 4d+1 7d-1
14 1d 2d 40 4d+1 7d-1
15 1d+1 2d+1 45 5d 7d+1
16 1d+1 2d+2 50 5d+2 8d-1
17 1d+2 3d-1 55 6d 8d+1
18 1d+2 3d 60 7d-1 9d
19 2d-1 3d+1 65 7d+1 9d+2
20 2d-1 3d+2 70 8d 10d
21 2d 4d-1 75 8d+2 10d+2
22 2d 4d 80 9d 11d
23 2d+1 4d+1 85 9d+2 11d+2
24 2d+1 4d+2
*/

fun thrust(st: Int) = parseRollSpec(when(st) {
    1 -> "1d-6"
    2 -> "1d-6"
    3 -> "1d-5"
    4 -> "1d-5"
    5 -> "1d-4"
    6 -> "1d-4"
    7 -> "1d-3"
    8 -> "1d-3"
    9 -> "1d-2"
    10 -> "1d-2"
    11 -> "1d-1"
    12 -> "1d-1"
    13 -> "1d"
    14 -> "1d"
    15 -> "1d+1"
    16 -> "1d+1"
    17 -> "1d+2"
    18 -> "1d+2"
    19 -> "2d-1"
    20 -> "2d-1"
    else -> throw IllegalArgumentException("can only handle ST 1-20 for calculating Thrust damage. ${st} out of range")
}) ?: throw IllegalArgumentException("couldn't parse rollSpace for st ${st}")

fun swing(st: Int) = parseRollSpec(when(st){
    1 -> "1d-5"
    2 -> "1d-5"
    3 -> "1d-4"
    4 -> "1d-4"
    5 -> "1d-3"
    6 -> "1d-3"
    7 -> "1d-2"
    8 -> "1d-2"
    9 -> "1d-1"
    10 -> "1d"
    11 -> "1d+1"
    12 -> "1d+2"
    13 -> "2d-1"
    14 -> "2d"
    15 -> "2d+1"
    16 -> "2d+2"
    17 -> "3d-1"
    18 -> "3d"
    19 -> "3d+1"
    20 -> "3d+2"
    else -> throw IllegalArgumentException("can only handle ST 1-20 for calculating Swing damage. ${st} out of range")
}) ?: throw IllegalArgumentException("couldn't parse rollSpace for st ${st}")

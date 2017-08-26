package org.crypticmission.gurpslack.loader

import org.junit.Assert.*
import org.junit.Test

/**
 */
class SkillDataTest {
    @Test fun shouldReturnCorrectDifficultiesForEasySkills() {
        assertEquals(0, relativeLevel("E", 1))

        assertEquals(1, relativeLevel("E", 2))
        assertEquals(1, relativeLevel("E", 3))

        assertEquals(2, relativeLevel("E", 4))
        assertEquals(2, relativeLevel("E", 5))
        assertEquals(2, relativeLevel("E", 6))
        assertEquals(2, relativeLevel("E", 7))

        assertEquals(3, relativeLevel("E", 8))
        assertEquals(3, relativeLevel("E", 8))

        assertEquals(4, relativeLevel("E", 12))
        assertEquals(4, relativeLevel("E", 13))

        assertEquals(5, relativeLevel("E", 16))
        assertEquals(5, relativeLevel("E", 17))

        assertEquals(6, relativeLevel("E", 20))
    }

    @Test fun shouldReturnCorrectDifficultiesForAverageSkills() {
        assertEquals(-1, relativeLevel("A", 1))

        assertEquals(0, relativeLevel("A", 2))
        assertEquals(0, relativeLevel("A", 3))

        assertEquals(1, relativeLevel("A", 4))
        assertEquals(1, relativeLevel("A", 5))
        assertEquals(1, relativeLevel("A", 6))
        assertEquals(1, relativeLevel("A", 7))

        assertEquals(2, relativeLevel("A", 8))
        assertEquals(2, relativeLevel("A", 8))

        assertEquals(3, relativeLevel("A", 12))
        assertEquals(3, relativeLevel("A", 13))

        assertEquals(4, relativeLevel("A", 16))
        assertEquals(4, relativeLevel("A", 17))

        assertEquals(5, relativeLevel("A", 20))
    }
}
package org.crypticmission.gurpslack.model

import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 */
class MultiShotDescriptorTest {

    @Test
    fun shouldUseRecoilAndHitsWhenCalculatingHits() {
        with (MultiShotDescriptor) {

            // no hits if no shots
            assertEquals(0, calculateHits(0, 1, 1))

            // no hits if negative margin
            assertEquals(0, calculateHits(1, -1, 1))

            // one hit on exact roll, regardless of recoil
            assertEquals(1, calculateHits(1, 0, 1))
            assertEquals(1, calculateHits(1, 0, 2))
            assertEquals(1, calculateHits(1, 0, 3))

            // one extra hit per full recoil multiple of success margin (if recoil is 1) up to a max of shots fired
            assertEquals(2, calculateHits(3, 1, 1))
            assertEquals(3, calculateHits(3, 2, 1))
            assertEquals(3, calculateHits(3, 3, 1))

            // one extra hit per full recoil multiple of success margin (if recoil is 2) up to a max of shots fired
            assertEquals(1, calculateHits(3, 1, 2))
            assertEquals(2, calculateHits(3, 2, 2))
            assertEquals(2, calculateHits(3, 3, 2))
            assertEquals(3, calculateHits(3, 4, 2))
            assertEquals(3, calculateHits(3, 5, 2))
            assertEquals(3, calculateHits(3, 6, 2))
            assertEquals(3, calculateHits(3, 7, 2))

        }
    }
}
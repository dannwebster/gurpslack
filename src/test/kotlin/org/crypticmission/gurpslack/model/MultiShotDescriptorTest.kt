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
            assertEquals(0, calculateHits(0, 1, 1.0))

            // no hits if negative margin
            assertEquals(0, calculateHits(1, -1, 1.0))

            // one hit on exact roll, regardless of recoil
            assertEquals(1, calculateHits(1, 0, 1.0))
            assertEquals(1, calculateHits(1, 0, 2.0))
            assertEquals(1, calculateHits(1, 0, 3.0))

            // one extra hit per full recoil multiple of success margin (if recoil is 1) up to a max of shots fired
            assertEquals(2, calculateHits(3, 1, 1.0))
            assertEquals(3, calculateHits(3, 2, 1.0))
            assertEquals(3, calculateHits(3, 3, 1.0))

            // one extra hit per full recoil multiple of success margin (if recoil is 2) up to a max of shots fired
            assertEquals(1, calculateHits(3, 1, 2.0))
            assertEquals(2, calculateHits(3, 2, 2.0))
            assertEquals(2, calculateHits(3, 3, 2.0))
            assertEquals(3, calculateHits(3, 4, 2.0))
            assertEquals(3, calculateHits(3, 5, 2.0))
            assertEquals(3, calculateHits(3, 6, 2.0))
            assertEquals(3, calculateHits(3, 7, 2.0))

            // fractional recoil
            assertEquals(1, calculateHits(3, 0, 0.25))
            assertEquals(3, calculateHits(3, 1, 0.25))
            assertEquals(4, calculateHits(4, 1, 0.25))
            assertEquals(5, calculateHits(5, 1, 0.25))
            assertEquals(5, calculateHits(9, 1, 0.25))
            assertEquals(9, calculateHits(9, 2, 0.25))
        }
    }
}
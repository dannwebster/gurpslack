package org.crypticmission.gurpslack.controllers

import org.crypticmission.gurpslack.repositories.Randomizer
import org.junit.Test
import org.junit.Assert.*

/**
 */
class RollControllerTest {
    @Test fun shouldBeInChannelWhenCallingRoll() {
        // given
        val subject = RollController(Randomizer.MAX)
        val data = SlashData()
        data.text = ""

        // when
        val outcome = subject.roll(data)

        // then
        assertEquals("in_channel", outcome.responseType)

    }

    @Test fun shouldBeEphemeralWhenCallingGmRoll() {
        // given
        val subject = RollController(Randomizer.MAX)
        val data = SlashData()
        data.text = ""

        // when
        val outcome = subject.gmRoll(data)

        // then
        assertEquals("ephemeral", outcome.responseType)

    }
}
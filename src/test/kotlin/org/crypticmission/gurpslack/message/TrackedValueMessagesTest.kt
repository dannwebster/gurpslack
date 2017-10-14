package org.crypticmission.gurpslack.message

import org.crypticmission.gurpslack.model.TrackedValue
import org.junit.Assert.*
import org.junit.Test

/**
 */
class TrackedValueMessagesTest {
    @Test
    fun shouldGenerate2ButtonsWhenCreatingMessage() {
        // given
        val subject = TrackedValue.fp(10, 10)

        // when
        val msg = richMessage("foo", subject)

        // then
        assertEquals(1, msg.attachments.size)
        assertEquals(2, (msg.attachments.first() as ActionAttachment).actions.size  )

    }
}
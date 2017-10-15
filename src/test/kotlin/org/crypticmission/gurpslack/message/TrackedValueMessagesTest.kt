package org.crypticmission.gurpslack.message

import org.crypticmission.gurpslack.model.TrackedValue
import org.crypticmission.gurpslack.slack.Attachment
import org.crypticmission.gurpslack.slack.Button
import org.crypticmission.gurpslack.slack.Menu
import org.junit.Assert.*
import org.junit.Test

/**
 */
class TrackedValueMessagesTest {
    @Test
    fun shouldGenerate2ButtonsAndAMenuWhenCreatingMessage() {
        // given
        val subject = TrackedValue.fp(10, 10)

        // when
        val msg = richMessage("foo", subject)

        // then
        assertEquals(1, msg.attachments?.size)
        assertEquals(2, (msg.attachments?.first() as Attachment).actions?.filter { it is Button }?.size  )
        assertEquals(1, (msg.attachments?.first() as Attachment).actions?.filter { it is Menu }?.size  )

    }
}
package org.crypticmission.gurpslack.message

import me.ramswaroop.jbot.core.slack.models.RichMessage
import org.crypticmission.gurpslack.model.*

/**
 */

fun message(stat: TrackedValue) = with (stat) {
    val effect = effect()
    """
    |_*${shortMessage(stat)}:*_
    |> - *Max:* ${maxValue} ${key}
    |> - *Current:* ${currentValue} ${key}
    |> - *Effects:* ${effect.status}${if (effect.details != null) " (" + effect.details + ")" else ""}
    """.trimIndent().trimMargin("|")
}

fun richMessage(key: String, stat: TrackedValue) : CallbackMessage {
    val m = RichMessage(message(stat))
    val callbackId = "${key}-${stat.key}-tracked-detail"
    val attachments = arrayOf(ActionAttachment(null, trackedIncDec(key, stat), callbackId))
    m.attachments = attachments
    return m.withCallback(callbackId)
}

fun shortMessage(stat: TrackedValue) = with (stat) {
    "${stat.valueName} (${key}): ${currentValue} of ${maxValue} (${effect().status})"
}

fun trackedStatsAttachments(key: String, trackedStats: Map<String, TrackedValue>): List<ActionAttachment> =
            listOf(ActionAttachment(
                    "_*Tracked Stats*_",
                    trackedStats.values.map { stat -> toDetailButton(key, stat) },
                    "${key}-tracked-stats")
            )


private fun toDetailButton(key: String, stat: TrackedValue): Action =
        Button("showTrackedStat", shortMessage(stat), buttonValue(key, stat.key)
        )
private fun trackedIncDec(key: String, stat: TrackedValue): List<Action> =
        listOf(
                Button("incTrackedStat", "+", buttonValue(key, stat.key)),
                Button("decTrackedStat", "-", buttonValue(key, stat.key))
        )
package org.crypticmission.gurpslack.message

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

fun richMessage(key: String, stat: TrackedValue) : RichMessage {
    val callbackId = "${key}-${stat.key}-tracked-detail"
    val attachments = arrayOf(Attachment(null, trackedIncDec(key, stat), callbackId))
    return RichMessage(message(stat),
            callbackId = callbackId,
            attachments = attachments)
}

fun shortMessage(stat: TrackedValue) = with (stat) {
    "${stat.valueName} (${key}): ${currentValue} of ${maxValue} (${effect().status})"
}

fun trackedStatsAttachments(key: String, trackedStats: Map<String, TrackedValue>): List<Attachment> =
            listOf(Attachment(
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
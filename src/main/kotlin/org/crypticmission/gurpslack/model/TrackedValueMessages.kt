package org.crypticmission.gurpslack.model

/**
 */

fun message(stat: TrackedValue) = with (stat) {
    val effect = effect()
    """
    |_*${stat.name}:*_
    |  Max ${shortName}: ${maxValue}
    |  Current ${shortName}: ${currentValue}
    |  Effects: ${effect.status}${if (effect.details != null) " (" + effect.details + ")" else ""}
    """.trimIndent().trimMargin("|")
}

fun shortMessage(stat: TrackedValue) = with (stat) {
    "${stat.name} (${shortName}): ${currentValue} of ${maxValue} (${effect().status})"
}

fun trackedStatsAttachments(key: String, trackedStats: Map<String, TrackedValue>): List<ActionAttachment> =
            listOf(ActionAttachment(
                    "_*Tracked Stats*_",
                    trackedStats.values.map { stat -> toDetailButton(key, stat) },
                       "${key}-tracked-stats")
            )


private fun toDetailButton(key: String, stat: TrackedValue): Action =
        Button("showTrackedStat", shortMessage(stat), buttonValue(key, stat.name)
)
private fun trackedIncDec(key: String, stat: TrackedValue): List<Action> =
        listOf(
                Button("incTrackedStat", "+", buttonValue(key, stat.name)),
                Button("decTrackedStat", "-", buttonValue(key, stat.name))
        )
package org.crypticmission.gurpslack.message

import org.crypticmission.gurpslack.controllers.SelectType
import org.crypticmission.gurpslack.model.*
import org.crypticmission.gurpslack.slack.*

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
//    + "\n" + stat.emoji()
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

fun trackedStatsAttachments(hostname: String, key: String, trackedStats: Map<String, TrackedValue>): List<Attachment> =
            listOf(Attachment(
                    title = "_*Tracked Stats*_",
                    text = toText(trackedStats),
                    callbackId = "${key}-tracked-stats",
                    titleLink = "http://${hostname}/character/${key}/stats")
            )


private fun toText(trackedStats: Map<String, TrackedValue>): String =
        trackedStats.values.map { stat -> shortMessage(stat) }.joinToString("/n")

private fun trackedIncDec(key: String, stat: TrackedValue): List<Action> =
        listOf(
                Button("incTrackedStat", "+", buttonValue(key, stat.key)),
                Button("decTrackedStat", "-", buttonValue(key, stat.key)),
                Menu(SelectType.ChangeAmount.commandString, "Change", changeValues(key, stat.key))
        )

private fun changeValues(characterKey: String, statKey: String): List<MenuOption> =
        (-10..10).map { MenuOption(it.toSignedStringWithZero(), "${characterKey}@${statKey}@${it.toString()}") }


private fun TrackedValue.emoji(): String =
        this.effects.map {
            it.emoji(this.currentValue)
        }.joinToString("\n")

private fun TrackedValueEffect.emoji(value: Int): String =
    this.range.map {
        if (it > value) {
            ":new_moon:"
        }  else {
           ":full_moon:"
        }
    }.joinToString("")

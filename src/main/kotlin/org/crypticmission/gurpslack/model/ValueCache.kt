package org.crypticmission.gurpslack.model

import org.crypticmission.gurpslack.slack.MessageData

class ValueCache<T>(val defaultValue: T) {
    private val cache : MutableMap<String, T> = mutableMapOf()

    fun putValue(messageData: MessageData, value: T) { cache[messageData.user.name] = value }
    fun getValue(messageData: MessageData) = cache.getOrDefault(messageData.user.name, defaultValue)
    fun getAndClearValue(messageData: MessageData) : T {
        val v = cache.getOrDefault(messageData.user.name, defaultValue)
        cache.remove(messageData.user.name)
        return v;
    }

}
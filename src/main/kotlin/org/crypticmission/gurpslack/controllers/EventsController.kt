package org.crypticmission.gurpslack.controllers

import org.springframework.web.bind.annotation.PostMapping

class EventData() {
    lateinit var token: String
    lateinit var challenge: String
    lateinit var type: String
}
/**
 */
class EventsController {
    @PostMapping("/events")
    fun gmRollSkill(eventData: EventData) : EventData {
        with (eventData) {
            println("token: ${token} ")
            println("challenge: ${challenge} ")
            println("type: ${type} ")
        }
        return eventData
    }
}
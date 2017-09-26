package org.crypticmission.gurpslack.controllers

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

class EventData() {
    lateinit var token: String
    lateinit var challenge: String
    lateinit var type: String
}
/**
 */
@RestController
class EventsController {

    private val logger = LoggerFactory.getLogger(EventsController::class.java)

    @PostMapping("/events")
    fun gmRollSkill(eventData: EventData) : EventData {
        with (eventData) {
            logger.info("token: ${token} ")
            logger.info("challenge: ${challenge} ")
            logger.info("type: ${type} ")
        }
        return eventData
    }
}
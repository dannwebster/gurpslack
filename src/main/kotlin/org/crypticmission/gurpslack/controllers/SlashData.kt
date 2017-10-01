package org.crypticmission.gurpslack.controllers

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

/**
 * @TODO: make snakecase work so I don't have to have these wonky snakeCase variable names
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class SlashData (
    var token: String = "",
    var team_id: String = "",
    var team_domain: String = "",
    var channel_id: String = "",
    var channel_name: String = "",
    var user_id: String = "",
    var user_name: String = "",
    var command: String = "",
    var text: String = "",
    var response_url: String = ""
)

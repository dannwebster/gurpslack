package org.crypticmission.gurpslack.controllers

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

/**
 */

@Configuration
class Config {
    @Bean
    fun clock() = Clock.systemUTC()
}
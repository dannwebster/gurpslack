package org.crypticmission.gurpslack

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication(scanBasePackages = arrayOf("org.crypticmission.gurpslack"))
class GurpslackApplication

fun main(args: Array<String>) {
    SpringApplication.run(GurpslackApplication::class.java, *args)
}

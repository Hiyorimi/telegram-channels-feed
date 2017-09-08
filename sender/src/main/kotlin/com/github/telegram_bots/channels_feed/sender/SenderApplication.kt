package com.github.telegram_bots.channels_feed.sender

import mu.KLogging
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.core.env.Environment
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
class SenderApplication(private val env: Environment) : ApplicationRunner {
    override fun run(args: ApplicationArguments) {
        logger.info { env }
    }

    companion object : KLogging() {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(SenderApplication::class.java, *args)
        }
    }
}

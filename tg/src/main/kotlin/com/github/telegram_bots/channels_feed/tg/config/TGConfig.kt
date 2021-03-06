package com.github.telegram_bots.channels_feed.tg.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.badoualy.telegram.api.Kotlogram
import com.github.badoualy.telegram.api.TelegramApiStorage
import com.github.badoualy.telegram.api.TelegramApp
import com.github.badoualy.telegram.api.TelegramClient
import com.github.telegram_bots.channels_feed.tg.config.properties.TGProperties
import org.davidmoten.rx.jdbc.Database
import org.springframework.boot.autoconfigure.amqp.RabbitProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.env.Environment
import java.net.URI

@Configuration
class TGConfig {
    @Bean
    fun objectMapper(): ObjectMapper = ObjectMapper().findAndRegisterModules()

    @Bean
    fun database(env: Environment): Database = Database.from(env.getProperty("spring.datasource.url"), 10)

    @Bean
    @Primary
    fun rabbitProperties(env: Environment): RabbitProperties {
        val uri = env.getProperty("spring.rabbitmq.url").let(::URI)
        val (user, pass) = uri.userInfo?.split(":")
                .let { it?.getOrNull(0) to it?.getOrNull(1) }

        return RabbitProperties().apply {
            host = uri.host
            port = uri.port
            username = user
            password = pass
            virtualHost = uri.path
        }
    }

    @Bean
    fun telegramApp(props: TGProperties): TelegramApp {
        return TelegramApp(
                props.apiId,
                props.apiHash,
                props.model,
                props.sysVersion,
                props.appVersion,
                props.langCode
        )
    }

    @Bean
    fun telegramClient(app: TelegramApp, cfgStorage: TelegramApiStorage): TelegramClient {
        return Kotlogram.getDefaultClient(app, cfgStorage)
    }
}

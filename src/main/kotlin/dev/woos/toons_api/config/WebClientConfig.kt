package dev.woos.toons_api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {

    @Bean
    fun webClient(): WebClient {
        return WebClient
            .builder()
            .codecs {
                it.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)
            }
            .build()
    }

}
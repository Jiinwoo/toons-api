package dev.woos.toons_api.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.web.ReactivePageableHandlerMethodArgumentResolver
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.ViewResolverRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer
import org.thymeleaf.spring6.ISpringWebFluxTemplateEngine
import org.thymeleaf.spring6.SpringWebFluxTemplateEngine
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver
import org.thymeleaf.spring6.view.reactive.ThymeleafReactiveViewResolver
import org.thymeleaf.templatemode.TemplateMode


@Configuration
@EnableWebFlux
class WebfluxConfig(
    @Value("\${cors.allowed-origins}") private val allowedOrigins: List<String>
) : WebFluxConfigurer, ApplicationContextAware {
    private lateinit var ctx: ApplicationContext
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        ctx = applicationContext
    }

    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.addCustomResolver(ReactivePageableHandlerMethodArgumentResolver())
    }

    override fun configureViewResolvers(registry: ViewResolverRegistry) {
        registry.viewResolver(ThymeleafReactiveViewResolver().apply {
            applicationContext = ctx
            templateEngine = SpringWebFluxTemplateEngine().apply {
                setTemplateResolver(SpringResourceTemplateResolver().apply {
                    setApplicationContext(ctx)
                    prefix = "classpath:/templates/"
                    suffix = ".html"
                    templateMode = TemplateMode.HTML
                    isCacheable = true
                    checkExistence = false
                })
            }
        })
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins(*allowedOrigins.toTypedArray())
            .allowedMethods("*")
            .allowedHeaders("*")
            .allowCredentials(true)
    }

    @Bean
    fun objectMapper(): ObjectMapper {
        return Jackson2ObjectMapperBuilder.json()
            .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .modulesToInstall(KotlinModule.Builder().enable(KotlinFeature.NullIsSameAsDefault).build())
            .build()
    }
}
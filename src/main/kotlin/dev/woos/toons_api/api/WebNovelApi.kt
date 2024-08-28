package dev.woos.toons_api.api

import dev.woos.toons_api.infra.JsoupCrawlerImpl
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/web-novels")
class WebNovelApi(
    private val jsoupCrawlerImpl: JsoupCrawlerImpl,
){

    @PostMapping
    suspend fun test() {
        jsoupCrawlerImpl.getNaverWebNovelList()
    }
}
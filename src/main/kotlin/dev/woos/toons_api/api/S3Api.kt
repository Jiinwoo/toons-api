package dev.woos.toons_api.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class S3Api {

    @GetMapping("/presigned-url")
    suspend fun getPresignedUrl(): String {
        return "presigned-url"
    }
}
package dev.woos.toons_api.api

import dev.woos.toons_api.api.dto.WebtoonDto
import dev.woos.toons_api.domain.webtoon.Webtoon
import dev.woos.toons_api.domain.webtoon.WebtoonRepository
import dev.woos.toons_api.usecase.WebtoonService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/webtoons")
class WebtoonApi(
    private val webtoonService: WebtoonService,
    private val webtoonRepository: WebtoonRepository,
) {

    @GetMapping
    suspend fun getWebtoons(pageable: Pageable): Page<WebtoonDto> {
        return webtoonService.findAllWithPage(pageable)
    }

    @PostMapping("/naver")
    suspend fun getNaverWebtoons(): String {
        return webtoonService.crawerNAVER()
    }

//    @PostMapping
//    suspend fun createWebtoon(): Long {
//        return webtoonRepository.save(
//            Webtoon(
//                name = "Toons",
//            )
//        ).id
//    }
//
//    @PutMapping("/{id}")
//    suspend fun updateWebtoon(
//        @PathVariable id: Long
//    ): Long {
//        val a = webtoonRepository.findById(id) ?: throw Exception("Not Found")
//        a.name = "Toons22"
//        webtoonRepository.save(
//            a
//        )
//
//
//
//        return 1
//    }

}
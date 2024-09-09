package dev.woos.toons_api.api

import dev.woos.toons_api.usecase.MemberService
import dev.woos.toons_api.utils.TokenUtil
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/api")
class SubscribeApi(
    private val memberService: MemberService,
    private val tokenUtil: TokenUtil,
) {
    @GetMapping("/subscribe")
    suspend fun subscribe(
        @RequestParam token: String,
        @RequestParam email: String,
        model: Model
    ): String {
        val memberId = tokenUtil.getMemberIdIfVerified(token)
        if (memberId != null) {
            memberService.subscribe(memberId, email)
            model.addAttribute("message", "구독이 완료되었습니다.")
            return "subscribe"
        } else {
            return "error"
        }
    }

    @GetMapping("/unsubscribe")
    suspend fun unsubscribe(@RequestParam token: String): String {
        tokenUtil.getMemberIdIfVerified(token)?.let {
            memberService.unsubscribe(it)
            return "unsubscribe"
        } ?: run {
            return "error"
        }
    }
}

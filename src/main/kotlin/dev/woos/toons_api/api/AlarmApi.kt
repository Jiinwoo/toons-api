package dev.woos.toons_api.api

import dev.woos.toons_api.api.dto.AlarmCreateDto
import dev.woos.toons_api.api.dto.AlarmDto
import dev.woos.toons_api.usecase.AlarmService
import dev.woos.toons_api.usecase.MemberService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/alarms")
class AlarmApi(
    private val alarmService: AlarmService
) {

    @GetMapping
    suspend fun getAlarms(
        @AuthenticationPrincipal userDetails: UserDetails
    ): List<AlarmDto> {
        return alarmService.getAlarmList(userDetails.username)
    }

    @PostMapping
    suspend fun createAlarm(
        @AuthenticationPrincipal userDetails: UserDetails,
        @RequestBody alarmCreateDto: AlarmCreateDto
    ): Long {
        return alarmService.createAlarm(userDetails.username, alarmCreateDto.webtoonId)
    }

    @DeleteMapping("/{id}")
    suspend fun deleteAlarm(
        @AuthenticationPrincipal userDetails: UserDetails,
        @PathVariable id: Long
    ) {
        alarmService.deleteAlarm(userDetails.username, id)
    }

}
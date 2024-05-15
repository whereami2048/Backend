package com.bamyanggang.apimodule.domain.experience.presentation

import com.bamyanggang.apimodule.domain.experience.application.dto.CreateStrongPointRequest
import com.bamyanggang.apimodule.domain.experience.application.dto.CreateStrongPointResponse
import com.bamyanggang.apimodule.domain.experience.application.service.StrongPointCreateService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class StrongPointController(
    private val strongPointCreateService: StrongPointCreateService,
) {
    @PostMapping(StrongPointApi.BASE_URL)
    fun createStrongPoint(@RequestBody request: CreateStrongPointRequest): CreateStrongPointResponse {
        return strongPointCreateService.createStrongPoint(request)
    }
}
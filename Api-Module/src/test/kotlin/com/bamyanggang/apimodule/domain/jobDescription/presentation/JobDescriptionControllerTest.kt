package com.bamyanggang.apimodule.domain.jobDescription.presentation

import com.bamyanggang.apimodule.BaseRestDocsTest
import com.bamyanggang.apimodule.domain.jobDescription.application.dto.CreateJobDescription
import com.bamyanggang.apimodule.domain.jobDescription.application.service.JobDescriptionCreateService
import com.bamyanggang.commonmodule.exception.ExceptionHandler
import com.bamyanggang.commonmodule.fixture.generateFixture
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.http.MediaType
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@WebMvcTest(JobDescriptionController::class)
@Import(ExceptionHandler::class)
class JobDescriptionControllerTest : BaseRestDocsTest() {

    @MockBean
    private lateinit var jobDescriptionCreateService: JobDescriptionCreateService

    @Test
    @DisplayName("직무 공고를 등록한다.")
    fun createJobDescription() {
        //given
        val createJobDescriptionRequest: CreateJobDescription.Request = generateFixture {
            it.set("enterpriseName", "기업 이름")
            it.set("title", "직무 공고 제목")
            it.set("content", "직무 공고 내용")
            it.set("link", "직무 공고 링크")
            it.set("startedAt", LocalDateTime.now())
            it.set("endedAt", LocalDateTime.now())
        }
        val request = RestDocumentationRequestBuilders.post(JobDescriptionApi.BASE_URL)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(createJobDescriptionRequest))
        //when
        val result = mockMvc.perform(request)
        //then
        result.andExpect(status().isOk)
            .andDo(
                resultHandler.document(
                    requestFields(
                        fieldWithPath("enterpriseName").description("기업 이름"),
                        fieldWithPath("title").description("직무 공고 제목"),
                        fieldWithPath("content").description("직무 공고 내용"),
                        fieldWithPath("link").description("직무 공고 링크"),
                        fieldWithPath("startedAt").description("직무 공고 시작일"),
                        fieldWithPath("endedAt").description("직무 공고 종료일")
                    )
                )
            )
    }

    @Test
    @DisplayName("직무 공고를 등록시, 빈 값을 보내면 예외를 반환한다")
    fun createJobDescriptionWithEmptyValue() {
        //given
        val createJobDescriptionRequest: CreateJobDescription.Request = generateFixture {
            it.set("enterpriseName", "기업 이름")
            it.set("title", "직무 공고 제목")
            it.set("content", "")
            it.set("link", "직무 공고 링크")
            it.set("startedAt", LocalDateTime.now())
            it.set("endedAt", LocalDateTime.now())
        }
        val request = RestDocumentationRequestBuilders.post(JobDescriptionApi.BASE_URL)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(createJobDescriptionRequest))
        given(jobDescriptionCreateService.createJobDescription(createJobDescriptionRequest)).willThrow(IllegalArgumentException("내용은 필수입니다."))

        //when
        val result = mockMvc.perform(request)

        //then
        result.andExpect(status().isBadRequest)
            .andDo(
                resultHandler.document(
                    responseFields(
                        fieldWithPath("code").description("에러 코드"),
                        fieldWithPath("message").description("에러 메시지")
                    )
                )
            )
    }

    @Test
    @DisplayName("직무 공고를 등록시, 시작일이 종료일보다 늦으면 예외를 반환한다")
    fun createJobDescriptionWithInvalidDate() {
        //given
        val createJobDescriptionRequest: CreateJobDescription.Request = generateFixture {
            it.set("enterpriseName", "기업 이름")
            it.set("title", "직무 공고 제목")
            it.set("content", "직무 공고 내용")
            it.set("link", "직무 공고 링크")
            it.set("startedAt", LocalDateTime.now())
            it.set("endedAt", LocalDateTime.now().minusDays(1))
        }
        val request = RestDocumentationRequestBuilders.post(JobDescriptionApi.BASE_URL)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(createJobDescriptionRequest))
        given(jobDescriptionCreateService.createJobDescription(createJobDescriptionRequest)).willThrow(IllegalArgumentException("시작일은 종료일보다 빨라야 합니다."))

        //when
        val result = mockMvc.perform(request)

        //then
        result.andExpect(status().isBadRequest)
            .andDo(
                resultHandler.document(
                    responseFields(
                        fieldWithPath("code").description("에러 코드"),
                        fieldWithPath("message").description("에러 메시지")
                    )
                )
            )
    }

}

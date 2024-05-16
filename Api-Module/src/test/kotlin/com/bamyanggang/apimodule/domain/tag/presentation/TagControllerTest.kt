package com.bamyanggang.apimodule.domain.tag.presentation

import com.bamyanggang.apimodule.BaseRestDocsTest
import com.bamyanggang.apimodule.domain.tag.application.dto.CreateTag
import com.bamyanggang.commonmodule.exception.ExceptionHandler
import com.bamyanggang.commonmodule.fixture.generateFixture
import com.bamyanggang.domainmodule.domain.tag.exception.TagException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@WebMvcTest(TagController::class)
@Import(ExceptionHandler::class)
class TagControllerTest : BaseRestDocsTest() {

    @MockBean
    private lateinit var tagController: TagController

    @Test
    @DisplayName("상위 태그를 등록한다.")
    fun createParentTagTest() {
        //given
        val createTagRequest: CreateTag.Request = generateFixture()
        val createTagResponse: CreateTag.Response = generateFixture()

        given(tagController.createTag(null, createTagRequest)).willReturn(createTagResponse)

        val request = RestDocumentationRequestBuilders.post(TagApi.BASE_URL)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(createTagRequest))

        //when
        val result = mockMvc.perform(request)

        //then
        result.andExpect(status().isOk)
            .andDo(resultHandler.document(
                requestFields(
                    fieldWithPath("name").description("태그 이름")
                ),
                responseFields(
                    fieldWithPath("id").description("생성된 태그 id")
                )
            )
        )
    }

    @Test
    @DisplayName("하위 태그를 등록한다.")
    fun createChildTagTest() {
        //given
        val createChildTagRequest: CreateTag.Request = generateFixture()
        val createChildTagResponse: CreateTag.Response = generateFixture()

        val parentTagId = generateFixture<UUID>()

        given(tagController.createTag(parentTagId, createChildTagRequest)).willReturn(createChildTagResponse)

        val request = RestDocumentationRequestBuilders.post(TagApi.TAG_PATH_VARIABLE_URL, parentTagId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(createChildTagRequest))

        //when
        val result = mockMvc.perform(request)

        //then
        result.andExpect(status().isOk)
            .andDo(resultHandler.document(
                pathParameters(
                    parameterWithName("tagId").description("상위 태그 id (생략 시 상위 태그 생성 API 호출)")
                ),
                requestFields(
                    fieldWithPath("name").description("태그 이름")
                ),
                responseFields(
                    fieldWithPath("id").description("생성된 태그 id")
                )
            )
        )
    }

    @Test
    @DisplayName("등록하려는 상위 태그 이름이 유저가 이미 등록하여 갖고 있는 경우 저장되지 않고 예외를 반환한다.")
    fun duplicatedParentTagTest() {
        //given
        val createParentTagRequest: CreateTag.Request = generateFixture()

        val request = RestDocumentationRequestBuilders.post(TagApi.BASE_URL)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(createParentTagRequest))

        given(tagController.createTag(null, createParentTagRequest)).willThrow(TagException.DuplicatedTagName())

        //when
        val result = mockMvc.perform(request)

        //then
        result.andExpect(status().isBadRequest)
            .andDo(resultHandler.document(
                responseFields(
                    fieldWithPath("code").description(TagException.DuplicatedTagName().code),
                    fieldWithPath("message").description(TagException.DuplicatedTagName().message)
                )
            )
        )
    }

    @Test
    @DisplayName("등록하려는 하위 태그 이름이 유저가 이미 등록하여 갖고 있는 경우 저장되지 않고 예외를 반환한다.")
    fun duplicatedChildTagTest() {
        //given
        val createChildTagRequest: CreateTag.Request = generateFixture()
        val parentTagId: UUID  = generateFixture()

        val request = RestDocumentationRequestBuilders.post(TagApi.TAG_PATH_VARIABLE_URL, parentTagId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(createChildTagRequest))

        given(tagController.createTag(parentTagId, createChildTagRequest)).willThrow(TagException.DuplicatedTagName())

        //when
        val result = mockMvc.perform(request)

        //then
        result.andExpect(status().isBadRequest)
            .andDo(resultHandler.document(
                pathParameters(
                    parameterWithName("tagId").description("상위 태그 id (생략 시 상위 태그 생성 API 호출)")
                ),
                responseFields(
                    fieldWithPath("code").description(TagException.DuplicatedTagName().code),
                    fieldWithPath("message").description(TagException.DuplicatedTagName().message)
                )
            )
        )
    }

    @Test
    @DisplayName("태그를 삭제한다.")
    fun deleteTagTest() {
        //given
        val deleteTagId = generateFixture<UUID>()

        val request = RestDocumentationRequestBuilders.delete(TagApi.TAG_PATH_VARIABLE_URL, deleteTagId)

        //when
        val result = mockMvc.perform(request)

        //then
        result.andExpect(status().isOk)
            .andDo(resultHandler.document(
                pathParameters(
                    parameterWithName("tagId").description("태그 id(상위, 하위 둘 다 가능)")
                ),
            )
        )
    }
}

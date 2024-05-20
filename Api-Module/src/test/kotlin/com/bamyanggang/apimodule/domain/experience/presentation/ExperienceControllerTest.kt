package com.bamyanggang.apimodule.domain.experience.presentation

import com.bamyanggang.apimodule.BaseRestDocsTest
import com.bamyanggang.apimodule.domain.experience.application.dto.CreateExperience
import com.bamyanggang.apimodule.domain.experience.application.dto.DetailExperience
import com.bamyanggang.apimodule.domain.experience.application.dto.EditExperience
import com.bamyanggang.apimodule.domain.experience.application.dto.ExperienceYear
import com.bamyanggang.apimodule.domain.experience.application.service.ExperienceCreateService
import com.bamyanggang.apimodule.domain.experience.application.service.ExperienceDeleteService
import com.bamyanggang.apimodule.domain.experience.application.service.ExperienceEditService
import com.bamyanggang.apimodule.domain.experience.application.service.ExperienceGetService
import com.bamyanggang.commonmodule.exception.ExceptionHandler
import com.bamyanggang.commonmodule.fixture.generateFixture
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime
import java.util.*

@WebMvcTest(ExperienceController::class)
@Import(ExceptionHandler::class)
class ExperienceControllerTest : BaseRestDocsTest() {

    @MockBean
    private lateinit var experienceCreateService: ExperienceCreateService

    @MockBean
    private lateinit var experienceDeleteService: ExperienceDeleteService

    @MockBean
    private lateinit var experienceEditService: ExperienceEditService

    @MockBean
    private lateinit var experienceGetService: ExperienceGetService

    @Test
    @DisplayName("경험을 등록한다.")
    fun createExperienceTest() {
        //given
        val content1 = CreateExperience.ExperienceContentRequest("질문1", "답변1")
        val content2 = CreateExperience.ExperienceContentRequest("질문2", "답변2")

        val contentRequest = arrayListOf(content1, content2)

        val createExperienceRequest : CreateExperience.Request = generateFixture {
            it.set("title", "제목")
            it.set("contents", contentRequest)
            it.set("strongPointIds", generateFixture<List<UUID>>())
            it.set("parentTagId", generateFixture<UUID>())
            it.set("childTagId", generateFixture<UUID>())
            it.set("startedAt", generateFixture<LocalDateTime>())
            it.set("endedAt", generateFixture<LocalDateTime>())
        }

        val createExperienceResponse : CreateExperience.Response = generateFixture()

        given(experienceCreateService.createExperience(createExperienceRequest)).willReturn(createExperienceResponse)

        val request = RestDocumentationRequestBuilders.post(ExperienceApi.BASE_URL)
            .header("Authorization", "Bearer Access Token")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(createExperienceRequest))

        //when
        val result = mockMvc.perform(request)

        //then
        result.andExpect(status().isOk).andDo(
            resultHandler.document(
                requestHeaders(
                    headerWithName("Authorization").description("엑세스 토큰")
                ),
                requestFields(
                    fieldWithPath("title").description("경험 제목"),
                    fieldWithPath("contents").description("경험 내용"),
                    fieldWithPath("contents[].question").description("경험 내용 질문"),
                    fieldWithPath("contents[].answer").description("경험 내용 답변"),
                    fieldWithPath("strongPointIds").description("관련된 역량 키워드"),
                    fieldWithPath("parentTagId").description("속한 상위 태그"),
                    fieldWithPath("childTagId").description("속한 하위 태그"),
                    fieldWithPath("startedAt").description("경험 시작 날짜"),
                    fieldWithPath("endedAt").description("경험 종료 날짜"),
                ),
                responseFields(
                    fieldWithPath("id").description("경험 id")
                )
            )
        )
    }

    @Test
    @DisplayName("제목 길이가 50이 넘으면 예외가 발생한다.")
    fun createOverTitleLengthTest() {
        //given
        val content1 = CreateExperience.ExperienceContentRequest("질문1", "답변1")
        val content2 = CreateExperience.ExperienceContentRequest("질문2", "답변2")

        val contentRequest = arrayListOf(content1, content2)

        val createExperienceRequest : CreateExperience.Request = generateFixture {
            it.set("title", "제목")
            it.set("contents", contentRequest)
            it.set("strongPointIds", generateFixture<List<UUID>>())
            it.set("parentTagId", generateFixture<UUID>())
            it.set("childTagId", generateFixture<UUID>())
            it.set("startedAt", generateFixture<LocalDateTime>())
            it.set("endedAt", generateFixture<LocalDateTime>())
        }

        given(experienceCreateService.createExperience(createExperienceRequest)).willThrow(IllegalArgumentException("제목의 글자 수는 50자 제한입니다."))

        val request = RestDocumentationRequestBuilders.post(ExperienceApi.BASE_URL)
            .header("Authorization", "Bearer Access Token")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(createExperienceRequest))

        //when
        val result = mockMvc.perform(request)

        //then
        result.andExpect(status().isBadRequest).andDo(
            resultHandler.document(
                requestHeaders(
                    headerWithName("Authorization").description("엑세스 토큰")
                ),
                requestFields(
                    fieldWithPath("title").description("경험 제목"),
                    fieldWithPath("contents").description("경험 내용"),
                    fieldWithPath("contents[].question").description("경험 내용 질문"),
                    fieldWithPath("contents[].answer").description("경험 내용 답변"),
                    fieldWithPath("strongPointIds").description("관련된 역량 키워드"),
                    fieldWithPath("parentTagId").description("속한 상위 태그"),
                    fieldWithPath("childTagId").description("속한 하위 태그"),
                    fieldWithPath("startedAt").description("경험 시작 날짜"),
                    fieldWithPath("endedAt").description("경험 종료 날짜"),
                ),
                responseFields(
                    fieldWithPath("code").description(HttpStatus.BAD_REQUEST),
                    fieldWithPath("message").description("제목의 글자 수는 50자 제한입니다.")
                )
            )
        )
    }

    @Test
    @DisplayName("역량 키워드를 5개 초과하여 등록할 경우 예외를 반환한다.")
    fun createOverStrongPointCountTest() {
        //given
        val content1 = CreateExperience.ExperienceContentRequest("질문1", "답변1")
        val content2 = CreateExperience.ExperienceContentRequest("질문2", "답변2")

        val contentRequest = arrayListOf(content1, content2)

        val createExperienceRequest : CreateExperience.Request = generateFixture {
            it.set("title", "제목")
            it.set("contents", contentRequest)
            it.set("strongPointIds", generateFixture<List<UUID>>())
            it.set("parentTagId", generateFixture<UUID>())
            it.set("childTagId", generateFixture<UUID>())
            it.set("startedAt", generateFixture<LocalDateTime>())
            it.set("endedAt", generateFixture<LocalDateTime>())
        }

        given(experienceCreateService.createExperience(createExperienceRequest)).willThrow(IllegalArgumentException("역량 키워드는 최대 5개까지 붙일 수 있습니다."))

        val request = RestDocumentationRequestBuilders.post(ExperienceApi.BASE_URL)
            .header("Authorization", "Bearer Access Token")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(createExperienceRequest))

        //when
        val result = mockMvc.perform(request)

        //then
        result.andExpect(status().isBadRequest).andDo(
            resultHandler.document(
                requestHeaders(
                    headerWithName("Authorization").description("엑세스 토큰")
                ),
                requestFields(
                    fieldWithPath("title").description("경험 제목"),
                    fieldWithPath("contents").description("경험 내용"),
                    fieldWithPath("contents[].question").description("경험 내용 질문"),
                    fieldWithPath("contents[].answer").description("경험 내용 답변"),
                    fieldWithPath("strongPointIds").description("관련된 역량 키워드"),
                    fieldWithPath("parentTagId").description("속한 상위 태그"),
                    fieldWithPath("childTagId").description("속한 하위 태그"),
                    fieldWithPath("startedAt").description("경험 시작 날짜"),
                    fieldWithPath("endedAt").description("경험 종료 날짜"),
                ),
                responseFields(
                    fieldWithPath("code").description(HttpStatus.BAD_REQUEST),
                    fieldWithPath("message").description("역량 키워드는 최대 5개까지 붙일 수 있습니다.")
                )
            )
        )
    }

    @Test
    @DisplayName("경험을 삭제한다.")
    fun deleteExperienceTest() {
        //given
        val request = RestDocumentationRequestBuilders.delete(ExperienceApi.EXPERIENCE_PATH_VARIABLE_URL, generateFixture<UUID>())
            .header("Authorization", "Bearer Access Token")
            .contentType(MediaType.APPLICATION_JSON_VALUE)

        //when
        val result = mockMvc.perform(request)

        //then
        result.andExpect(status().isOk).andDo(
            resultHandler.document(
                requestHeaders(
                    headerWithName("Authorization").description("엑세스 토큰")
                ),
                pathParameters(
                    parameterWithName("experienceId").description("경험 id")
                )
            )
        )
    }

    @Test
    @DisplayName("경험을 수정한다.")
    fun editExperienceTest() {
        //given
        val content1 = EditExperience.ExperienceContentRequest("질문1", "답변1")
        val content2 = EditExperience.ExperienceContentRequest("질문2", "답변2")

        val contentRequest = arrayListOf(content1, content2)

        val editExperienceRequest : EditExperience.Request = generateFixture {
            it.set("title", "제목")
            it.set("contents", contentRequest)
            it.set("strongPointIds", generateFixture<List<UUID>>())
            it.set("parentTagId", generateFixture<UUID>())
            it.set("childTagId", generateFixture<UUID>())
            it.set("startedAt", generateFixture<LocalDateTime>())
            it.set("endedAt", generateFixture<LocalDateTime>())
        }

        val editedExperienceId : UUID = UUID.randomUUID()
        val editExperienceResponse : EditExperience.Response = generateFixture()

        given(experienceEditService.editExperienceById(editExperienceRequest, editedExperienceId)).willReturn(editExperienceResponse)

        val request = RestDocumentationRequestBuilders.patch(ExperienceApi.EXPERIENCE_PATH_VARIABLE_URL, editedExperienceId)
            .header("Authorization", "Bearer Access Token")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(editExperienceRequest))

        //when
        val result = mockMvc.perform(request)

        //then
        result.andExpect(status().isOk).andDo(
            resultHandler.document(
                requestHeaders(
                    headerWithName("Authorization").description("엑세스 토큰")
                ),
                pathParameters(
                    parameterWithName("experienceId").description("경험 id")
                ),
                requestFields(
                    fieldWithPath("title").description("경험 제목"),
                    fieldWithPath("contents").description("경험 내용"),
                    fieldWithPath("contents[].question").description("경험 내용 질문"),
                    fieldWithPath("contents[].answer").description("경험 내용 답변"),
                    fieldWithPath("strongPointIds").description("관련된 역량 키워드"),
                    fieldWithPath("parentTagId").description("속한 상위 태그"),
                    fieldWithPath("childTagId").description("속한 하위 태그"),
                    fieldWithPath("startedAt").description("경험 시작 날짜"),
                    fieldWithPath("endedAt").description("경험 종료 날짜"),
                ),
                responseFields(
                    fieldWithPath("id").description("경험 id")
                )
            )
        )
    }

    @Test
    @DisplayName("경험을 상세조회한다.")
    fun getExperienceDetailTest() {
        val content1 = DetailExperience.DetailExperienceContent("질문1", "답변1")
        val content2 = DetailExperience.DetailExperienceContent("질문2", "답변2")

        val contentResponse = arrayListOf(content1, content2)

        val experienceId: UUID = UUID.randomUUID()

        val experienceDetailResponse : DetailExperience.Response = generateFixture {
            it.set("id", experienceId)
            it.set("title", "제목")
            it.set("contents", contentResponse)
            it.set("strongPoints", generateFixture<List<DetailExperience.DetailStrongPoint>>())
            it.set("parentTag", generateFixture<DetailExperience.DetailTag>())
            it.set("childTag", generateFixture<DetailExperience.DetailTag>())
            it.set("startedAt", generateFixture<LocalDateTime>())
            it.set("endedAt", generateFixture<LocalDateTime>())
        }

        given(experienceGetService.getExperienceDetailById(experienceId)).willReturn(experienceDetailResponse)

        //given
        val request = RestDocumentationRequestBuilders.get(ExperienceApi.EXPERIENCE_PATH_VARIABLE_URL, experienceId)
              .header("Authorization", "Bearer Access Token")
                  .contentType(MediaType.APPLICATION_JSON_VALUE)

        //when
        val result = mockMvc.perform(request)

        //then
        result.andExpect(status().isOk).andDo(
            resultHandler.document(
                requestHeaders(
                    headerWithName("Authorization").description("엑세스 토큰")
                ),
                pathParameters(
                  parameterWithName("experienceId").description("경험 id")
                ),
                responseFields(
                    fieldWithPath("id").description("경험 id"),
                    fieldWithPath("title").description("경험 제목"),
                    fieldWithPath("contents").description("경험 내용"),
                    fieldWithPath("contents[].question").description("경험 내용 질문"),
                    fieldWithPath("contents[].answer").description("경험 내용 답변"),
                    fieldWithPath("strongPoints").description("관련된 역량 키워드"),
                    fieldWithPath("strongPoints[].id").description("역량 키워드 id"),
                    fieldWithPath("strongPoints[].name").description("역량 키워드 이름"),
                    fieldWithPath("parentTag").description("속한 상위 태그"),
                    fieldWithPath("parentTag.id").description("상위 태그 id"),
                    fieldWithPath("parentTag.name").description("상위 태그 이름"),
                    fieldWithPath("childTag").description("속한 하위 태그"),
                    fieldWithPath("childTag.id").description("하위 태그 id"),
                    fieldWithPath("childTag.name").description("하위 태그 이름"),
                    fieldWithPath("startedAt").description("경험 시작 날짜"),
                    fieldWithPath("endedAt").description("경험 종료 날짜"),
                ),
            )
        )
    }

    @Test
    @DisplayName("경험 목록을 상위 태그 id를 기준으로 조회한다.")
    fun getExperienceYearAndParentTagTest() {
        val content1 = DetailExperience.DetailExperienceContent("질문1", "답변1")
        val content2 = DetailExperience.DetailExperienceContent("질문2", "답변2")
        val strongPoint1 = DetailExperience.DetailStrongPoint(UUID.randomUUID(), "역량 키워드 이름 1")
        val strongPoint2 = DetailExperience.DetailStrongPoint(UUID.randomUUID(), "역량 키워드 이름 2")
        val parentTag = DetailExperience.DetailTag(UUID.randomUUID(), "상위 태그 이름")
        val childTag = DetailExperience.DetailTag(UUID.randomUUID(), "하위 태그 이름")
        val startedAt = LocalDateTime.now()
        val endedAt = LocalDateTime.now().plusDays(1)

        val contentResponse = arrayListOf(content1, content2)
        val strongPointResponse = arrayListOf(strongPoint1, strongPoint2)

        val experienceResponses = arrayListOf(
            DetailExperience.Response(
                id = UUID.randomUUID(),
                title = "경험 제목1 ",
                contents = contentResponse,
                strongPoints = strongPointResponse,
                parentTag = parentTag,
                childTag = childTag,
                startedAt = startedAt,
                endedAt = endedAt
            ),
            DetailExperience.Response(
                id = UUID.randomUUID(),
                title = "경험 제목 2",
                contents = contentResponse,
                strongPoints = strongPointResponse,
                parentTag = parentTag,
                childTag = childTag,
                startedAt = startedAt.minusYears(1),
                endedAt = endedAt
            )
        )

        val year = 2024
        given(experienceGetService.getExperienceByYearAndParentTag(year, parentTag.id)).willReturn(experienceResponses)

        //given
        val request = RestDocumentationRequestBuilders.get(ExperienceApi.BASE_URL)
            .header("Authorization", "Bearer Access Token")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .queryParam("year", year.toString())
            .queryParam("parent-tag", parentTag.id.toString())

        //when
        val result = mockMvc.perform(request)

        //then
        result.andExpect(status().isOk).andDo(
            resultHandler.document(
                requestHeaders(
                    headerWithName("Authorization").description("엑세스 토큰")
                ),
                responseFields(
                    fieldWithPath("[].id").description("경험 id"),
                    fieldWithPath("[].title").description("경험 제목"),
                    fieldWithPath("[].contents").description("경험 내용"),
                    fieldWithPath("[].contents[].question").description("경험 내용 질문"),
                    fieldWithPath("[].contents[].answer").description("경험 내용 답변"),
                    fieldWithPath("[].strongPoints").description("관련된 역량 키워드"),
                    fieldWithPath("[].strongPoints[].id").description("역량 키워드 id"),
                    fieldWithPath("[].strongPoints[].name").description("역량 키워드 이름"),
                    fieldWithPath("[].parentTag").description("속한 상위 태그"),
                    fieldWithPath("[].parentTag.id").description("상위 태그 id"),
                    fieldWithPath("[].parentTag.name").description("상위 태그 이름"),
                    fieldWithPath("[].childTag").description("속한 하위 태그"),
                    fieldWithPath("[].childTag.id").description("하위 태그 id"),
                    fieldWithPath("[].childTag.name").description("하위 태그 이름"),
                    fieldWithPath("[].startedAt").description("경험 시작 날짜"),
                    fieldWithPath("[].endedAt").description("경험 종료 날짜"),
                ),
            )
        )
    }

    @Test
    @DisplayName("경험 목록을 하위 태그 id를 기준으로 조회한다.")
    fun getExperienceYearAndChildTagTest() {
        val content1 = DetailExperience.DetailExperienceContent("질문1", "답변1")
        val content2 = DetailExperience.DetailExperienceContent("질문2", "답변2")
        val strongPoint1 = DetailExperience.DetailStrongPoint(UUID.randomUUID(), "역량 키워드 이름 1")
        val strongPoint2 = DetailExperience.DetailStrongPoint(UUID.randomUUID(), "역량 키워드 이름 2")
        val parentTag = DetailExperience.DetailTag(UUID.randomUUID(), "상위 태그 이름")
        val childTag = DetailExperience.DetailTag(UUID.randomUUID(), "하위 태그 이름")
        val startedAt = LocalDateTime.now()
        val endedAt = LocalDateTime.now().plusDays(1)

        val contentResponse = arrayListOf(content1, content2)
        val strongPointResponse = arrayListOf(strongPoint1, strongPoint2)

        val experienceResponses = arrayListOf(
            DetailExperience.Response(
                id = UUID.randomUUID(),
                title = "경험 제목1 ",
                contents = contentResponse,
                strongPoints = strongPointResponse,
                parentTag = parentTag,
                childTag = childTag,
                startedAt = startedAt,
                endedAt = endedAt
            ),
            DetailExperience.Response(
                id = UUID.randomUUID(),
                title = "경험 제목 2",
                contents = contentResponse,
                strongPoints = strongPointResponse,
                parentTag = parentTag,
                childTag = childTag,
                startedAt = startedAt.minusYears(1),
                endedAt = endedAt
            )
        )

        val year = 2024
        given(experienceGetService.getExperienceByYearAndChildTag(year, childTag.id)).willReturn(experienceResponses)

        //given
        val request = RestDocumentationRequestBuilders.get(ExperienceApi.BASE_URL)
            .header("Authorization", "Bearer Access Token")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .queryParam("year", year.toString())
            .queryParam("parent-tag", parentTag.id.toString())
            .queryParam("child-tag", childTag.id.toString())
        //when
        val result = mockMvc.perform(request)

        //then
        result.andExpect(status().isOk).andDo(
            resultHandler.document(
                requestHeaders(
                    headerWithName("Authorization").description("엑세스 토큰")
                ),
                responseFields(
                    fieldWithPath("[].id").description("경험 id"),
                    fieldWithPath("[].title").description("경험 제목"),
                    fieldWithPath("[].contents").description("경험 내용"),
                    fieldWithPath("[].contents[].question").description("경험 내용 질문"),
                    fieldWithPath("[].contents[].answer").description("경험 내용 답변"),
                    fieldWithPath("[].strongPoints").description("관련된 역량 키워드"),
                    fieldWithPath("[].strongPoints[].id").description("역량 키워드 id"),
                    fieldWithPath("[].strongPoints[].name").description("역량 키워드 이름"),
                    fieldWithPath("[].parentTag").description("속한 상위 태그"),
                    fieldWithPath("[].parentTag.id").description("상위 태그 id"),
                    fieldWithPath("[].parentTag.name").description("상위 태그 이름"),
                    fieldWithPath("[].childTag").description("속한 하위 태그"),
                    fieldWithPath("[].childTag.id").description("하위 태그 id"),
                    fieldWithPath("[].childTag.name").description("하위 태그 이름"),
                    fieldWithPath("[].startedAt").description("경험 시작 날짜"),
                    fieldWithPath("[].endedAt").description("경험 종료 날짜"),
                ),
            )
        )
    }

    @Test
    @DisplayName("유저의 경험 내 존재하는 연도들을 중복 제거한 리스트를 반환한다.")
    fun getExperienceYearsTest() {
        //given
        val userId: UUID = generateFixture()
        val years = arrayListOf(2020,2021,2023, 2024, 2025)
        val yearResponse = ExperienceYear.Response(years)

        given(experienceGetService.getAllYearsByExistExperience()).willReturn(yearResponse)

        val request = RestDocumentationRequestBuilders.get(ExperienceApi.ALL_YEARS)
              .header("Authorization", "Bearer Access Token")
                  .contentType(MediaType.APPLICATION_JSON_VALUE)

        //when
        val result = mockMvc.perform(request)
        
        result.andExpect(status().isOk).andDo(
            resultHandler.document(
                requestHeaders(
                    headerWithName("Authorization").description("엑세스 토큰")
                ),
                responseFields(
                    fieldWithPath("years").description("경험이 존재하는 연도 배열(활동 시작 일시 기준)")
                )
            )
        )
    }
}


package br.all.review.controller

import br.all.infrastructure.review.MongoSystematicStudyRepository
import br.all.review.shared.TestDataFactory
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@Tag("IntegrationTest")
class SystematicStudyControllerTest(
    @Autowired private val repository: MongoSystematicStudyRepository,
    @Autowired private val mockMvc: MockMvc,
) {
    private lateinit var factory: TestDataFactory

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
        factory = TestDataFactory()
    }

    @AfterEach
    fun tearDown() = repository.deleteAll()

    private fun postUrl() = "/api/v1/researcher/${factory.researcherId}/systematic-study"

    private fun getOneUrl(
        researcherId: UUID = factory.researcherId,
        systematicStudyId: UUID = factory.systematicStudyId,
    ) = "/api/v1/researcher/$researcherId/systematic-study/$systematicStudyId"

    @Nested
    @DisplayName("When posting a new Systematic Study")
    inner class WhenPostingANewSystematicStudy {
        @Test
        @Tag("ValidClasses")
        fun `should create a valid systematic study`() {
            val json = factory.createValidPostRequest()
            mockMvc.perform(post(postUrl()).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.researcherId").value(factory.researcherId.toString()))
                .andExpect(jsonPath("$.systematicStudyId").isString)
                .andExpect(jsonPath("$._links").exists())
        }
        
        @Test
        @Tag("InvalidClasses")
        fun `should not create a invalid systematic study`() {
            val json = factory.createInvalidPostRequest()
            mockMvc.perform(post(postUrl()).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest)
        }
    }

    @Nested
    @DisplayName("When getting systematic studies")
    inner class WhenGettingSystematicStudies {
        @Nested
        @Tag("ValidClasses")
        @DisplayName("And successfully finding them")
        inner class AndSuccessfullyFindingThem {
            @Test
            fun `should get a systematic study and return 200 status code`() {
                repository.save(factory.createSystematicStudyDocument())
                mockMvc.perform(get(getOneUrl()).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.content.id").value(factory.systematicStudyId.toString()))
                    .andExpect(jsonPath("$._links").exists())
            }
        }
    }
}
package br.all.protocol.controller

import br.all.infrastructure.protocol.MongoProtocolRepository
import br.all.infrastructure.review.MongoSystematicStudyRepository
import br.all.protocol.shared.TestDataFactory
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
import br.all.review.shared.TestDataFactory as SystematicStudyTestDataFactory

@SpringBootTest
@AutoConfigureMockMvc
@Tag("IntegrationTest")
class ProtocolControllerTest(
    @Autowired private val protocolRepository: MongoProtocolRepository,
    @Autowired private val systematicStudyRepository: MongoSystematicStudyRepository,
    @Autowired private val mockMvc: MockMvc,
) {
    private lateinit var factory: TestDataFactory
    private lateinit var systematicStudyDataFactory: SystematicStudyTestDataFactory

    @BeforeEach
    fun setUp()  {
        protocolRepository.deleteAll()
        systematicStudyRepository.deleteAll()

        factory = TestDataFactory()
        systematicStudyDataFactory = SystematicStudyTestDataFactory()

        val (researcher, systematicStudyId) = factory
        val systematicStudy = systematicStudyDataFactory.createSystematicStudyDocument(
            id = systematicStudyId,
            collaborators = mutableSetOf(researcher),
        )
        systematicStudyRepository.save(systematicStudy)
    }

    @AfterEach
    fun tearDown() {
        protocolRepository.deleteAll()
        systematicStudyRepository.deleteAll()
    }

    @Nested
    @DisplayName("When posting protocols")
    inner class WhenPostingProtocols {
        private fun postUrl(
            researcher: UUID = factory.researcher,
            systematicStudy: UUID = factory.protocol,
        ) = "/researcher/$researcher/systematic-study/$systematicStudy/protocol"

        @Nested
        @Tag("ValidClasses")
        @DisplayName("And being succeed")
        inner class AndBeingSucceed {
            @Test
            fun `should post a new protocol`() {
                val (researcher, systematicStudy) = factory
                val json = factory.validPostRequest()

                mockMvc.perform(post(postUrl()).contentType(MediaType.APPLICATION_JSON).content(json))
                    .andExpect(status().isCreated)
                    .andExpect(jsonPath("$.researcherId").value(researcher.toString()))
                    .andExpect(jsonPath("$.systematicStudyId").value(systematicStudy.toString()))
                    .andExpect(jsonPath("$._links").exists())
            }
        }

        @Nested
        @Tag("InvalidClasses")
        @DisplayName("And failing to create it")
        inner class AndFailingToCreateIt {
            @Test
            fun `should not write a protocol for nonexistent systematic study and return 404`() {
                val json = factory.validPostRequest()

                mockMvc.perform(
                    post(postUrl(systematicStudy = UUID.randomUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                ).andExpect(status().isNotFound)
            }

            @Test
            fun `should not allow non collaborator researcher to write protocols`() {
                val json = factory.validPostRequest()

                mockMvc.perform(
                    post(postUrl(researcher = UUID.randomUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                ).andExpect(status().isForbidden)
            }
        }
    }

    @Nested
    @DisplayName("When getting protocols")
    inner class WhenGettingProtocols {
        private fun getUrl(
            researcher: UUID = factory.researcher,
            systematicStudy: UUID = factory.protocol,
        ) = "/researcher/$researcher/systematic-study/$systematicStudy/protocol"

        @Nested
        @Tag("ValidClasses")
        @DisplayName("And finding them")
        inner class AndFindingThem {
            @Test
            fun `should find the protocol and return it as well as 200 status code`() {
                val document = factory.createProtocolDocument()
                protocolRepository.save(document)

                mockMvc.perform(get(getUrl()).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.researcherId").value(factory.researcher.toString()))
                    .andExpect(jsonPath("$.systematicStudyId").value(factory.protocol.toString()))
                    .andExpect(jsonPath("$.content").exists())
                    .andExpect(jsonPath("$._links").exists())
            }
        }

        @Nested
        @Tag("InvalidClasses")
        @DisplayName("And failing to get any")
        inner class AndFailingToGetAny {
            @Test
            fun `should return 404 when trying to find nonexistent protocols`() {
                mockMvc.perform(get(getUrl()).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound)
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.detail").exists())
            }

            @Test
            fun `should return 404 when trying to find protocols of nonexistent systematic studies`() {
                mockMvc.perform(
                    get(getUrl(systematicStudy = UUID.randomUUID())).contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNotFound)
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.detail").exists())
            }

            @Test
            fun `should not authorize researchers that are not a collaborator to find protocols`() {
                mockMvc.perform(
                    get(getUrl(researcher =  UUID.randomUUID())).contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isForbidden)
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.detail").exists())
            }
        }
    }
}
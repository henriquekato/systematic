package br.all.application.review.create

import br.all.application.researcher.repository.ResearcherRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.review.repository.fromRequestModel
import br.all.application.review.repository.toDto
import br.all.domain.model.review.SystematicStudy
import br.all.domain.services.UuidGeneratorService
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class CreateSystematicStudyServiceTest {
    @MockK
    private lateinit var systematicStudyRepository: SystematicStudyRepository

    @MockK
    private lateinit var uuidGeneratorService: UuidGeneratorService

    @MockK
    private lateinit var researcherRepository: ResearcherRepository

    private lateinit var sut: CreateSystematicStudyService

    @BeforeEach
    fun setUp() {
        sut = CreateSystematicStudyService(systematicStudyRepository, researcherRepository, uuidGeneratorService)
    }

    //TODO: add all missing unit tests

    @Test
    fun `Should create a systematic study`() {
        val researcherId = UUID.randomUUID()
        val requestModel = SystematicStudyRequestModel(
            "Some title",
            "Some description",
            researcherId,
            setOf(researcherId)
        )
        val id = UUID.randomUUID()
        val dto = SystematicStudy.fromRequestModel(id, requestModel).toDto()

        every { uuidGeneratorService.next() } returns id
        every { researcherRepository.exists(researcherId) } returns true
        every { systematicStudyRepository.create(dto) } returns Unit
        every { systematicStudyRepository.findById(id) } returns Optional.of(dto)

        val systematicStudy = sut.create(requestModel)
        assertEquals(dto, systematicStudy)
    }

    @Test
    fun `Should throw IllegalArgumentException for nonexistent researcher`() {
        val nonExistentResearcherId = UUID.randomUUID()
        val requestModel = SystematicStudyRequestModel(
            "Some title",
            "Some description",
            nonExistentResearcherId,
            emptySet()
        )
        val id = UUID.randomUUID()
        val dto = SystematicStudy.fromRequestModel(id, requestModel).toDto()

        every { uuidGeneratorService.next() } returns id
        every { researcherRepository.exists(nonExistentResearcherId) } returns false
        every { systematicStudyRepository.create(dto) } returns Unit
        every { systematicStudyRepository.findById(id) } returns Optional.of(dto)

        assertThrows<IllegalArgumentException> { sut.create(requestModel) }
    }
}
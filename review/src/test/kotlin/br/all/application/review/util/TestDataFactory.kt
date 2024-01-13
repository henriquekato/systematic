package br.all.application.review.util

import br.all.application.review.repository.SystematicStudyDto
import br.all.application.review.repository.fromRequestModel
import br.all.application.review.repository.toDto
import br.all.application.review.update.services.UpdateSystematicStudyService.ResponseModel
import br.all.domain.model.researcher.ResearcherId
import br.all.domain.model.review.SystematicStudy
import io.github.serpro69.kfaker.Faker
import java.util.*
import br.all.application.review.create.CreateSystematicStudyService.RequestModel as CreateRequestModel
import br.all.application.review.create.CreateSystematicStudyService.ResponseModel as CreateResponseModel
import br.all.application.review.find.services.FindAllSystematicStudiesService.ResponseModel as FindAllResponseModel
import br.all.application.review.find.services.FindOneSystematicStudyService.ResponseModel as FindOneResponseModel
import br.all.application.review.update.services.UpdateSystematicStudyService.RequestModel as UpdateRequestModel

class TestDataFactory {
    private val faker = Faker()
    val researcher: UUID = UUID.randomUUID()
    val systematicStudy: UUID = UUID.randomUUID()

    fun generateDto(
        systematicStudyId: UUID = this.systematicStudy,
        title: String = faker.book.title(),
        description: String = faker.lorem.words(),
        ownerId: UUID = this.researcher,
        collaborators: Set<UUID> = emptySet(),
    ) = SystematicStudyDto(
        systematicStudyId,
        title,
        description,
        ownerId,
        mutableSetOf(ownerId).also { it.addAll(collaborators) },
    )

    fun createRequestModel(
        title: String = faker.book.title(),
        description: String = faker.lorem.words(),
        collaborators: Set<UUID> = emptySet()
    ) = CreateRequestModel(title, description, collaborators)

    fun createResponseModel(
        researcherId: ResearcherId,
        systematicStudyId: UUID,
    ) = CreateResponseModel(researcherId.value, systematicStudyId)

    fun createDtoFromCreateRequestModel(
        systematicStudyId: UUID,
        researcherId: ResearcherId,
        request: CreateRequestModel,
    ) = SystematicStudy.fromRequestModel(systematicStudyId, researcherId.value, request).toDto()

    fun findOneResponseModel(
        researcherId: UUID = this.researcher,
        systematicStudyId: UUID = this.systematicStudy,
        dto: SystematicStudyDto = generateDto(),
    ) = FindOneResponseModel(researcherId, systematicStudyId, dto)

    fun findAllResponseModel(
        vararg systematicStudies: SystematicStudyDto,
        researcherId: UUID = this.researcher,
    ) = FindAllResponseModel(researcherId, systematicStudies.toList())

    fun emptyFindAllResponseModel(
        researcherId: UUID = this.researcher,
        owner: UUID? = null,
    ) = FindAllResponseModel(
        researcherId = researcherId,
        ownerId = owner,
        systematicStudies = emptyList(),
    )

    fun findAllByOwnerResponseModel(
        owner: UUID,
        vararg systematicStudies: SystematicStudyDto,
        researcherId: UUID = this.researcher,
    ) = FindAllResponseModel(
        researcherId = researcherId,
        ownerId = owner,
        systematicStudies = systematicStudies.toList(),
    )

    fun updateRequestModel(
        title: String? = null,
        description: String? = null,
    ) = UpdateRequestModel(title, description)

    fun updateResponseModel(
        researcherId: UUID = this.researcher,
        systematicStudyId: UUID = this.systematicStudy,
    ) = ResponseModel(researcherId, systematicStudyId)

    operator fun component1() = researcher
    
    operator fun component2() = systematicStudy
}
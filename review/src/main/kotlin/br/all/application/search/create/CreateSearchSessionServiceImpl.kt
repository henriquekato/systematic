package br.all.application.search

import br.all.application.search.repository.SearchSessionRepository
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.search.create.CreateSearchSessionPresenter
import br.all.application.search.create.CreateSearchSessionService
import br.all.application.search.create.CreateSearchSessionService.RequestModel
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.repository.toDto
import br.all.domain.model.search.SearchSession
import br.all.domain.model.search.SearchSessionID
import br.all.domain.services.UuidGeneratorService
import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.review.SystematicStudyId
import br.all.domain.services.BibtexConverterService
import br.all.infrastructure.study.StudyReviewRepositoryImpl
import java.util.*
import kotlin.NoSuchElementException

class CreateSearchSessionServiceImpl(
    private val searchSessionRepository: SearchSessionRepository,
    private val systematicStudyRepository: SystematicStudyRepository,
    private val uuidGeneratorService: UuidGeneratorService,
    private val bibtexConverterService: BibtexConverterService,
    private val studyReviewRepository: StudyReviewRepository
) : CreateSearchSessionService {
    override fun createSession(presenter: CreateSearchSessionPresenter, request: RequestModel) {
        require(request.searchString.isNotBlank()) { "Search string must not be blank" }

        val systematicStudy = systematicStudyRepository.findById(request.systematicStudy)
            ?: throw NoSuchElementException("Systematic study not found with ID: ${request.systematicStudy}")

        val protocolId = ProtocolId(systematicStudy.id)

        if (searchSessionRepository.getSearchSessionBySource(protocolId, request.source) != null) {
            throw IllegalStateException("Search session already exists for source: ${request.source}")
        }

        val sessionId = SearchSessionID(uuidGeneratorService.next())
        val searchSession = SearchSession(
            sessionId,
            systematicStudyId = SystematicStudyId(UUID.randomUUID()),
            request.searchString,
            request.additionalInfo ?: "",
            source = request.source
        )

        val bibFileContent = String(request.bibFile.bytes)

        val systematicStudyId = SystematicStudyId(systematicStudy.id)

        val studyReviews = bibtexConverterService.convertManyToStudyReview(systematicStudyId, bibFileContent)

        studyReviewRepository.saveOrUpdateBatch(studyReviews.map { it.toDto() })

        searchSessionRepository.create(searchSession)
    }
}

package br.all.study.presenter

import br.all.application.study.update.interfaces.AnswerExtractionQuestionPresenter
import br.all.application.study.update.interfaces.AnswerExtractionQuestionService.ResponseModel
import br.all.shared.error.createErrorResponseFrom
import br.all.study.controller.StudyReviewController
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

class RestfulAnswerExtractionQuestionPresenter : AnswerExtractionQuestionPresenter {
    var responseEntity: ResponseEntity<*>? = null

    override fun prepareSuccessView(response: ResponseModel) {
        val viewModel = ViewModel(response.researcherId, response.systematicStudyId, response.studyReviewId)

        val link = linkTo<StudyReviewController> {
            findStudyReview(response.researcherId, response.systematicStudyId, response.studyReviewId)
        }.withSelfRel()
        viewModel.add(link)

        responseEntity = ResponseEntity.status(HttpStatus.OK).body(viewModel)
    }

    override fun prepareFailView(throwable: Throwable) = run { responseEntity = createErrorResponseFrom(throwable) }

    override fun isDone() = responseEntity != null

    data class ViewModel(
        val researcherId: UUID,
        val systematicStudyId: UUID,
        val studyReviewId: Long,
    ): RepresentationModel<ViewModel>()
}

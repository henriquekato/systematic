package br.all.application.study.update.implementation
//
//import br.all.application.question.repository.QuestionRepository
//import br.all.application.question.repository.fromDto
//import br.all.application.researcher.credentials.ResearcherCredentialsService
//import br.all.application.review.repository.SystematicStudyRepository
//import br.all.application.shared.exceptions.EntityNotFoundException
//import br.all.application.shared.presenter.PreconditionChecker
//import br.all.application.study.repository.StudyReviewRepository
//import br.all.application.study.repository.fromDto
//import br.all.application.study.repository.toDto
//import br.all.application.study.update.interfaces.AnswerExtractionQuestionPresenter
//import br.all.application.study.update.interfaces.AnswerExtractionQuestionService
//import br.all.application.study.update.interfaces.AnswerExtractionQuestionService.*
//import br.all.domain.model.question.*
//import br.all.domain.model.researcher.toResearcherId
//import br.all.domain.model.review.toSystematicStudyId
//import br.all.domain.model.study.Answer
//import br.all.domain.model.study.StudyReview
//
//class AnswerExtractionQuestionImpl(
//    private val studyReviewRepository: StudyReviewRepository,
//    private val questionRepository: QuestionRepository,
//    private val systematicStudyRepository: SystematicStudyRepository,
//    private val credentialsService: ResearcherCredentialsService,
//): AnswerExtractionQuestionService {
//    override fun answer(presenter: AnswerExtractionQuestionPresenter, request: RequestModel<*>) {
//        val (researcher, systematicStudy, studyReviewId, questionId) = request
//        PreconditionChecker(systematicStudyRepository, credentialsService).also {
//           it.prepareIfViolatesPreconditions(presenter, researcher.toResearcherId(), systematicStudy.toSystematicStudyId())
//        }
//        if (presenter.isDone()) return
//
//        val reviewDto = studyReviewRepository.findById(systematicStudy, studyReviewId)
//        if (reviewDto == null) {
//            val message = "Review with id $studyReviewId in systematic study $systematicStudy does not exist!"
//            presenter.prepareFailView(EntityNotFoundException(message))
//            return
//        }
//        val review = StudyReview.fromDto(reviewDto)
//
//        val questionDto = questionRepository.findById(systematicStudy, questionId)
//        if (questionDto == null) {
//            val message = "There is not a question with id $questionId defined in protocol $systematicStudy"
//            presenter.prepareFailView(EntityNotFoundException(message))
//            return
//        }
//        val question = Question.fromDto(questionDto)
//
//        val answer = answerQuestion(questionDto.questionType, request, question)
//        review.answerFormQuestionOf(answer)
//
//        studyReviewRepository.saveOrUpdate(review.toDto())
//        presenter.prepareSuccessView(ResponseModel(researcher, systematicStudy, studyReviewId))
//    }
//
//    private fun answerQuestion(
//        type: String,
//        request: RequestModel<*>,
//        question: Question<*>
//    ): Answer<*> {
//        if (type != request.type) {
//            val message = "Answer for ${request.type} has been sent, but question ${question.id} is actually $type"
//            throw IllegalArgumentException(message)
//        }
//        return when {
//            type == "TEXTUAL" && request.answer is String -> (question as Textual).answer(request.answer)
//            type == "PICK_LIST" && request.answer is String -> (question as PickList).answer(request.answer)
//            type == "NUMBER_SCALE" && request.answer is Int -> (question as NumberScale).answer(request.answer)
//            type == "LABELED_SCALE" && request.answer is LabelDto ->
//                (question as LabeledScale).answer(Label(request.answer.name, request.answer.value))
//            else -> {
//                val message = "Answer type of ${request.answer?.javaClass} is not compatible with question type $type"
//                throw IllegalArgumentException(message)
//            }
//        }
//    }
//}

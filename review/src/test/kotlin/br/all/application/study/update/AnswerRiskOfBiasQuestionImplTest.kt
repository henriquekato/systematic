package br.all.application.study.update

import br.all.application.question.repository.QuestionRepository
import br.all.application.researcher.credentials.ResearcherCredentialsService
import br.all.application.review.repository.SystematicStudyRepository
import br.all.application.study.repository.StudyReviewRepository
import br.all.application.study.update.implementation.AnswerRiskOfBiasQuestionImpl
import br.all.application.study.update.interfaces.AnswerRiskOfBiasQuestionPresenter
import br.all.application.study.util.TestDataFactory
import br.all.application.util.PreconditionCheckerMocking
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@Tag("UnitTest")
@Tag("ServiceTest")
@ExtendWith(MockKExtension::class)
class AnswerRiskOfBiasQuestionImplTest {

    @MockK(relaxed = true) private lateinit var studyReviewRepository: StudyReviewRepository
    @MockK(relaxUnitFun = true) private lateinit var systematicStudyRepository: SystematicStudyRepository
    @MockK(relaxUnitFun = true) private lateinit var questionRepository: QuestionRepository
    @MockK private lateinit var credentialService: ResearcherCredentialsService
    @MockK(relaxed = true) private lateinit var presenter: AnswerRiskOfBiasQuestionPresenter

    private lateinit var sut: AnswerRiskOfBiasQuestionImpl

    private lateinit var factory: TestDataFactory
    private lateinit var preconditionCheckerMocking: PreconditionCheckerMocking

    @BeforeEach
    fun setUp() {
        factory = TestDataFactory()
        preconditionCheckerMocking = PreconditionCheckerMocking(
            presenter,
            credentialService,
            systematicStudyRepository,
            factory.researcherId,
            factory.systematicStudyId
        )
        preconditionCheckerMocking.makeEverythingWork()
        sut = AnswerRiskOfBiasQuestionImpl(
            studyReviewRepository,
            questionRepository,
            systematicStudyRepository,
            credentialService,
        )
    }

    @Nested
    @Tag("ValidClasses")
    @DisplayName("When successfully answering a question")
    inner class WhenSuccessfullyAnsweringAQuestion {
        @Test
        fun `should successfully Answer a text question`() {
            val dto = factory.generateDto()
            val questionId = UUID.randomUUID()
            val questionDto = factory.generateQuestionTextualDto(questionId, factory.systematicStudyId)
            val answerString = "Answer Test"
            val request = factory.answerRequestModel(questionId, "TEXTUAL", answerString)


            every { studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId) } returns dto
            every { questionRepository.findById(request.systematicStudyId, questionId) } returns questionDto

            sut.answerQuestion(presenter, request)

            verify(exactly = 1) {
                studyReviewRepository.saveOrUpdate(any())
                presenter.prepareSuccessView(any())
            }
        }

        @Test
        fun `should successfully Answer a labeled scale question`() {
            val dto = factory.generateDto()
            val questionId = UUID.randomUUID()
            val answer = factory.labelDto("Test Name", 1)
            val questionDto = factory.generateQuestionLabeledScaleDto(questionId, labelDto = answer)
            val request = factory.answerRequestModel(questionId, "LABELED_SCALE", answer)

            every { studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId) } returns dto
            every { questionRepository.findById(request.systematicStudyId, questionId) } returns questionDto

            sut.answerQuestion(presenter, request)

            verify(exactly = 1) {
                studyReviewRepository.saveOrUpdate(any())
                presenter.prepareSuccessView(any())
            }
        }

    }
//
//    @Nested
//    @Tag("InvalidClasses")
//    @DisplayName("When failing to update a study review's selection status")
//    inner class WhenFailingToUpdateAStudyReviewSelection {
//        @Test
//        fun `should not be able to update a non-existent study`() {
//            val request = factory.updateStatusRequestModel("INCLUDED")
//
//            every { studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId) } returns null
//            sut.changeStatus(presenter, request)
//
//            verify {
//                presenter.prepareFailView(any<EntityNotFoundException>())
//            }
//        }
//
//        @Test
//        fun `should not accept duplicated as a new status`() {
//            val dto = factory.generateDto()
//            val request = factory.updateStatusRequestModel("DUPLICATED")
//
//            every { studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId) } returns dto
//            sut.changeStatus(presenter, request)
//
//            verify {
//                presenter.prepareFailView(any<IllegalArgumentException>())
//            }
//        }
//
//        @Test
//        fun `should not accept invalid statuses`() {
//            val dto = factory.generateDto()
//            val request = factory.updateStatusRequestModel("NOTREAL")
//
//            every { studyReviewRepository.findById(request.systematicStudyId, request.studyReviewId) } returns dto
//
//            assertFailsWith<IllegalArgumentException> {
//                sut.changeStatus(presenter, request)
//            }
//        }
//    }

}
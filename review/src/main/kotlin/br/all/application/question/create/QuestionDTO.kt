package br.all.application.question.create

import br.all.domain.model.question.QuestionId
import java.util.*

data class QuestionDTO(
    val systematicStudyId: UUID,
    val questionId: QuestionId,
    val protocolId: UUID,
    val code: String,
    val description: String,
    val questionType: String,
    val scales: Map<String, Int>?,
    val higher: Int?,
    val lower: Int?,
    val options: List<String>?
)

package br.all.application.question.repository
import br.all.application.question.create.CreateQuestionService.QuestionType
import br.all.domain.model.question.*
import br.all.domain.model.review.SystematicStudyId

fun Question<*>.toDto(type: QuestionType) = QuestionDto(
    this.id.value(),
    this.systematicStudyId.value(),
    this.code,
    this.description,
    type.toString(),
    (this as? LabeledScale)?.scales?.mapValues { it.value.value },
    (this as? NumberScale)?.lower,
    (this as? NumberScale)?.higher,
    (this as? PickList)?.options
)

fun Question.Companion.fromDto(dto: QuestionDto): Question<*> {
    val builder = QuestionBuilder.with(
        QuestionId(dto.questionId),
        SystematicStudyId(dto.systematicStudyId),
        dto.code,
        dto.description,
    )

    return when {
        dto.questionType == "PICK_LIST" && dto.options != null -> builder.buildPickList(dto.options)
        dto.questionType == "NUMBER_SCALE" && dto.higher != null && dto.lower != null ->
            builder.buildNumberScale(dto.lower, dto.higher)
        dto.questionType == "LABELED_SCALE" && dto.scales != null -> builder.buildLabeledScale(dto.scales)
        dto.questionType == "TEXTUAL" -> builder.buildTextual()
        else -> throw IllegalArgumentException("Required values for question type ${dto.questionType} are missing!")
    }
}


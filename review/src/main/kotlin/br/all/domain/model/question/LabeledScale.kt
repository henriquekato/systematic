package br.all.domain.model.question

import br.all.domain.model.protocol.ProtocolId
import br.all.domain.model.study.Answer
import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.utils.requireThatExists

class LabeledScale(
    id: QuestionId,
    protocolId: ProtocolId,
    code: String,
    description: String,
    scales: Map<String, Int>
) : Question<Label>(id, protocolId, code, description) {
    private val scales = scales.mapValues { (key, value) -> Label(key, value) }.toMutableMap()

    init {
        val notification = validate()
        require(notification.hasNoErrors()) { notification.message() }
    }

    private fun validate(): Notification {
        val notification = Notification()
        if (scales.isEmpty())
            notification.addError("Can not create a labeled scale without a label to choose.")
        return notification
    }

    override fun answer(value: Label): Answer<Label> {
        require(value in scales.values) { "Invalid answer for question $code, because there is not label $value!" }
        return Answer(id.value(), value)
    }

    fun addScale(name: String, value: Int) = run { scales[name] = Label(name, value) }

    fun removeScale(name: String) {
        requireThatExists(name in scales.keys) { "There is not a scale with name $name in this question!" }
        check(scales.size > 1) { "Unable to remove the last scale from this question!" }
        scales.remove(name)
    }

    override fun toString() =
        "LabeledScale(QuestionId: $id, ProtocolId: $protocolId, Code: $code, " +
                "Description: $description, Scales: $scales)"
}
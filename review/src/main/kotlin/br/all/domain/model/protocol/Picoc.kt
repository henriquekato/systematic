package br.all.domain.model.protocol

import br.all.domain.shared.ddd.Notification
import br.all.domain.shared.ddd.ValueObject
import br.all.domain.shared.utils.Phrase

data class Picoc(
    val population: Phrase,
    val intervention: Phrase,
    val control: Phrase,
    val outcome: Phrase,
    val context: Phrase?,
) : ValueObject() {
    override fun validate() = Notification()
}
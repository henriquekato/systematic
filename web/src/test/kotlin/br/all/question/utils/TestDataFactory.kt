package br.all.question.utils

import br.all.domain.shared.utils.paragraph
import io.github.serpro69.kfaker.Faker
import java.util.*

class TestDataFactory {
    val researcherId: UUID = UUID.randomUUID()
    val systematicStudyId: UUID = UUID.randomUUID()
    private val faker = Faker()

    fun validCreateTextualRequest(researcher: UUID = researcherId, systematicStudyId: UUID = this.systematicStudyId) =
        """
        {
            "researcherId": "$researcher",
            "systematicStudyId": "$systematicStudyId",
            "type": "TEXTUAL",
            "code": "${faker.lorem.words()}",
            "description": "${faker.paragraph(8)}"
        }
        """

    fun validCreatePickListRequest(researcher: UUID = researcherId, systematicStudyId: UUID = this.systematicStudyId) =
        """
        {
            "researcherId": "$researcher",
            "systematicStudyId": "$systematicStudyId",
            "type": "PICK_LIST",
            "code": "${faker.lorem.words()}",
            "description": "${faker.paragraph(8)}",
            "options": [
                "${faker.lorem.words()}",
                "${faker.lorem.words()}",
                "${faker.lorem.words()}"
            ]
        }
        """

    fun validCreateNumberScaleRequest(researcher: UUID = researcherId, systematicStudyId: UUID = this.systematicStudyId) =
        """
        {
            "researcherId": "$researcher",
            "systematicStudyId": "$systematicStudyId",
            "type": "NUMBERED_SCALE",
            "code": "${faker.lorem.words()}",
            "description": "${faker.paragraph(8)}",
            "higher": "${10}",
            "lower": "${1}"
        }
        """
}
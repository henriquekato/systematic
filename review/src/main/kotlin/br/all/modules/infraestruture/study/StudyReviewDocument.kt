package br.all.modules.infraestruture.study

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document
class StudyReviewDocument (
    @Id
    val id: Long,
    val reviewId: UUID,
    val title: String,
    val year: Int,
    val authors: String,
    val venue: String,
    val abstract: String,
    val keywords: Set<String>,
    val references: List<String>,
    val doi: String,
    val searchSources: Set<String>,
    val criteria: Set<String>,
    val formAnswers: Map<UUID, String>,
    val qualityAnswers: Map<UUID, String>,
    val comments: String,
    val readingPriority: String,
    val extractionStatus: String,
    val selectionStatus: String,
)
package com.techglock.health.app.model.toolscalculators

import com.techglock.health.app.model.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SmartPhoneSaveResponceModel(
    @SerializedName("JSONData")
    @Expose
    private val jsonData: String, private val authToken: String
) : BaseRequest(Header(authTicket = authToken)) {

    data class JSONDataRequest(
        @SerializedName("ParticipationID")
        val participationID: Int = 0,
        @SerializedName("Questions")
        val questions: List<Question> = listOf()
    )

    data class Question(
        @SerializedName("Answers")
        val answers: List<Answer> = listOf(),
        @SerializedName("Code")
        val code: String = "",
        @SerializedName("Description")
        val description: String = "",
        @SerializedName("QuestionTypeCode")
        val questionTypeCode: Int = 0,
        @SerializedName("QuizID")
        val quizID: Int = 0,
        @SerializedName("SectionID")
        val sectionID: Int = 0,
        @SerializedName("TemplateID")
        val templateID: Int = 0,
    )

    data class Answer(
        @SerializedName("QuestionCode")
        val questionCode: String = "",
        @SerializedName("Code")
        val code: String = "",
        @SerializedName("Score")
        val score: Int = 0
    )

    data class SmartPhoneSaveResponce(
        @SerializedName("Result")
        val result: Result? = Result()
    )

    data class Result(
        @SerializedName("Score")
        val score: Double? = 0.0,
        @SerializedName("SmartPhoneReport")
        val smartPhoneReport: SmartPhoneReport? = SmartPhoneReport(),
        @SerializedName("SmartPhoneRisk")
        val smartPhoneRisk: SmartPhoneRisk? = SmartPhoneRisk()
    )

    data class SmartPhoneReport(
        @SerializedName("Physical_Effects")
        val physicalEffects: PhysicalEffects? = PhysicalEffects(),
        @SerializedName("Psychological_Effects")
        val psychologicalEffects: PsychologicalEffects? = PsychologicalEffects()
    )

    data class SmartPhoneRisk(
        @SerializedName("RiskTitle")
        val riskTitle: String? = "",
        @SerializedName("RiskLabel")
        val riskLabel: String? = "",
        @SerializedName("RiskDescription")
        val riskDescription: String? = ""
    )

    data class PhysicalEffects(
        @SerializedName("Description")
        val description: String? = "",
        @SerializedName("Section")
        val section: List<Section>? = listOf(),
        @SerializedName("Title")
        val title: String? = ""
    )

    data class PsychologicalEffects(
        @SerializedName("Description")
        val description: String? = "",
        @SerializedName("Section")
        val section: List<Section>? = listOf(),
        @SerializedName("Title")
        val title: String? = ""
    )

    data class Section(
        @SerializedName("Title")
        val title: String? = "",
        @SerializedName("SubSection")
        val subSection: List<SubSection>? = listOf()
    )

    data class SubSection(
        @SerializedName("Title")
        val title: String? = "",
        @SerializedName("Description")
        val description: String? = "",
        @SerializedName("SubDescription")
        val subDescription: List<String>? = listOf()
    )

    /*    data class SectionX(
            @SerializedName("SubSection")
            val subSection: List<SubSectionX>? = listOf(),
            @SerializedName("Title")
            val title: String? = ""
        )

        data class SubSectionX(
            @SerializedName("Description")
            val description: String? = "",
            @SerializedName("SubDescription1")
            val subDescription1: String? = "",
            @SerializedName("SubDescription2")
            val subDescription2: String? = "",
            @SerializedName("SubDescription3")
            val subDescription3: String? = "",
            @SerializedName("Title")
            val title: String? = ""
        )*/

    /*    data class RespQuestion(
            @SerializedName("AnswerExplanation")
            val answerExplanation: String = "",
            @SerializedName("Answers")
            val answers: List<Any> = listOf(),
            @SerializedName("Code")
            val code: String = "",
            @SerializedName("CreatedBy")
            val createdBy: Any? = Any(),
            @SerializedName("CreatedDate")
            val createdDate: Any? = Any(),
            @SerializedName("Description")
            val description: Any? = Any(),
            @SerializedName("DisplayOrder")
            val displayOrder: Int = 0,
            @SerializedName("ID")
            val iD: Int = 0,
            @SerializedName("IsCheck")
            val isCheck: Any? = Any(),
            @SerializedName("IsDisable")
            val isDisable: Any? = Any(),
            @SerializedName("IsEnable")
            val isEnable: Any? = Any(),
            @SerializedName("IsHide")
            val isHide: Any? = Any(),
            @SerializedName("IsShow")
            val isShow: Any? = Any(),
            @SerializedName("IsUncheck")
            val isUncheck: Any? = Any(),
            @SerializedName("ModifiedBy")
            val modifiedBy: Any? = Any(),
            @SerializedName("ModifiedDate")
            val modifiedDate: Any? = Any(),
            @SerializedName("ParticipantID")
            val participantID: Int = 0,
            @SerializedName("QuestionTypeCode")
            val questionTypeCode: Any? = Any(),
            @SerializedName("QuizID")
            val quizID: Int = 0,
            @SerializedName("Score")
            val score: Double = 0.0,
            @SerializedName("SectionID")
            val sectionID: Any? = Any(),
            @SerializedName("TempletID")
            val templetID: Int = 0
        )*/

}
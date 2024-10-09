package com.techglock.health.app.model.hra

data class Question(
    var question: Int = 0,
    var qCode: String = "",
    var questionType: QuestionType = QuestionType.Other,
    var optionList: ArrayList<Option> = arrayListOf(),
    var subQuestion: Int = 0,
    var subQuestionCode: String = "",
    var isLast: Boolean = false,
    var bgImage: Int = 0,
    var category: String = "",
    var tabName: String = "SETGENHEALTH"
)
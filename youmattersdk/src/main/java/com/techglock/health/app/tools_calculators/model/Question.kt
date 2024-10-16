package com.techglock.health.app.tools_calculators.model

class Question {
    var question = ""
    var qCode = ""
    var respQuesCode = ""
    var options = ArrayList<String>()
    var optionCodes =
        ArrayList<String>()
    var subQuestion = ""
    var subQuestionCode = ""
    var yesBtnAction = ""
    var noBtnAction = ""
    var txtFieldValue = ""
    var isLast = false

    fun getqCode(): String {
        return qCode
    }

    fun setqCode(qCode: String) {
        this.qCode = qCode
    }

}
package com.techglock.health.app.model.entity

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(tableName = "HRAQuesTable", primaryKeys = ["AnswerCode"])
data class HRAQuestions(
    @field:SerializedName("QuestionCode")
    var QuestionCode: String = "",
    @field:SerializedName("AnswerCode")
    var AnswerCode: String = "",
    @field:SerializedName("AnsDescription")
    var AnsDescription: String = "",
    @field:SerializedName("Category")
    var Category: String = "",
    @field:SerializedName("TabName")
    var TabName: String = "",
    @field:SerializedName("OthersVal")
    var OthersVal: String = "",
    @field:SerializedName("Code")
    var Code: String = "",
    @field:SerializedName("IsSelected")
    var IsSelected: String = ""
)
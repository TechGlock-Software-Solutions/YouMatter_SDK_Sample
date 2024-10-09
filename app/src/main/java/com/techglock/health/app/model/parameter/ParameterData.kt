package com.techglock.health.app.model.parameter

data class ParameterData(
    val parameterCode: String = "",
    val profileCode: String = "",
    val unit: String = "",
    val value: String = "",
    val minPermissibleValue: String = "",
    val maxPermissibleValue: String = ""
)
package com.techglock.health.app.tools_calculators.model

import android.util.ArrayMap

class DiabetesSummeryModel {
    var totalScore = "0"
    var riskLabel = ""
    var probabilityPercentage = ""
    var goodIn = ""
    var needImprovement = ""
    var nonModifiableRisk = ""
    var detailReport: ArrayMap<String, ArrayList<SubSectionModel>> = ArrayMap()

}
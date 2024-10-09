package com.techglock.health.app.tools_calculators.model

class StressCalculatorSummeryModel {
    var stage = "0"
    var parameterReport: ArrayList<HypertensionResultPojo> =
        ArrayList<HypertensionResultPojo>()
    var depression: StressData = StressData()
    var anxiety: StressData = StressData()
    var stress: StressData = StressData()

}

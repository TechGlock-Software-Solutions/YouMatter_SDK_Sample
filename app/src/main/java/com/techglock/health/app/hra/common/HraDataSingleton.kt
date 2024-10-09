package com.techglock.health.app.hra.common

import com.techglock.health.app.model.hra.Option
import com.techglock.health.app.model.hra.Question

class HraDataSingleton {

    var question = Question()
    val previousAnsList = HashMap<Int, MutableList<Option>>()
    var selectedOptionList: MutableList<Option> = ArrayList()

    var heightValue = ""
    var weightValue = ""
    var bmiValue = ""
    var heightUnit = ""
    var weightUnit = ""

    fun getPrevAnsList(pageNumber: Int): MutableList<Option> {
        //paramList[3].finalValue("previousPageNumber---> $pageNumber")
        return if (previousAnsList.containsKey(pageNumber)) {
            previousAnsList[pageNumber]!!
        } else ArrayList()
    }

    fun clearData() {
        instance = null
    }

    companion object {
        private var instance: HraDataSingleton? = null
        fun getInstance(): HraDataSingleton? {
            if (instance == null) {
                instance = HraDataSingleton()
            }
            return instance
        }
    }

}
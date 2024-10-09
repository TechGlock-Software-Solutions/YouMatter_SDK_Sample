package com.techglock.health.app.fitness_tracker.common

import com.techglock.health.app.model.entity.FitnessEntity.StepGoalHistory
import com.techglock.health.app.model.fitness.GetStepsGoalModel.LatestGoal

class StepsDataSingleton private constructor() {

    var latestGoal = LatestGoal()
    var stepHistoryList: MutableList<StepGoalHistory> = mutableListOf()
    var stepsHistoryBetweenDatesList: MutableList<StepGoalHistory> = mutableListOf()

    fun clearStepsData() {
        instance = null
        stepHistoryList.clear()
        latestGoal = LatestGoal()
        stepsHistoryBetweenDatesList.clear()
    }

    companion object {
        var instance: StepsDataSingleton? = null
            get() {
                if (field == null) {
                    synchronized(StepsDataSingleton::class.java) {
                        if (field == null) {
                            field = StepsDataSingleton()
                        }
                    }
                }
                return field
            }
            private set
    }

}
package com.techglock.health.app.fitness_tracker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.fitness_tracker.common.StepsDataSingleton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FitnessBroadcastReceiver : BroadcastReceiver() {

    private val stepsDataSingleton = StepsDataSingleton.instance!!

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent!!.action
        Utilities.printLogError("action---> $action")
        if (!Utilities.isNullOrEmpty(action)) {
            if (action.equals(Constants.CLEAR_FITNESS_DATA, ignoreCase = true)) {
                stepsDataSingleton.clearStepsData()
                /*                stepsDataSingleton.stepHistoryList.clear()
                                stepsDataSingleton.latestGoal = GetStepsGoalModel.LatestGoal()
                                stepsDataSingleton.selectedDateHistory = FitnessEntity.StepGoalHistory(lastRefreshed = Date())*/
            }
        }
    }
}
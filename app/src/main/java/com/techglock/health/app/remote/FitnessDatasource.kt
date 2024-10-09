package com.techglock.health.app.remote

import com.techglock.health.app.di.DIModule
import com.techglock.health.app.model.fitness.FitnessModel
import com.techglock.health.app.model.fitness.GetStepsGoalModel
import com.techglock.health.app.model.fitness.SetGoalModel
import com.techglock.health.app.model.fitness.StepsHistoryModel
import com.techglock.health.app.model.fitness.StepsSaveListModel
import javax.inject.Inject
import javax.inject.Named

class FitnessDatasource @Inject constructor(@Named(DIModule.ENCRYPTED) private val encryptedService: ApiService) {

    suspend fun fetchLatestGoal(data: GetStepsGoalModel) = encryptedService.fetchLatestGoal(data)

    suspend fun fetchStepsListHistory(data: StepsHistoryModel) =
        encryptedService.fetchStepsListHistory(data)

    suspend fun saveStepsGoal(data: SetGoalModel) = encryptedService.saveStepsGoal(data)

    suspend fun saveStepsList(data: StepsSaveListModel) = encryptedService.saveStepsList(data)

    suspend fun saveStepsData(data: FitnessModel) = encryptedService.saveStepsData(data)

}
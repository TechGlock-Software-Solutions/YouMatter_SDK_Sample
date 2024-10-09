package com.techglock.health.app.fitness_tracker.domain

import androidx.lifecycle.LiveData
import com.techglock.health.app.model.fitness.GetStepsGoalModel
import com.techglock.health.app.model.fitness.SetGoalModel
import com.techglock.health.app.model.fitness.StepsHistoryModel
import com.techglock.health.app.model.fitness.StepsSaveListModel
import com.techglock.health.app.repository.FitnessRepository
import com.techglock.health.app.repository.utils.Resource
import javax.inject.Inject


class FitnessManagementUseCase @Inject constructor(private val repository: FitnessRepository) {

    suspend fun invokeStepsHistory(data: StepsHistoryModel): LiveData<Resource<StepsHistoryModel.Response>> {
        return repository.fetchStepsListHistory(data = data)
    }

    suspend fun invokeStpesHistory(data: StepsHistoryModel): LiveData<Resource<StepsHistoryModel.Response>> {
        return repository.fetchStepsListHistory(data = data)
    }

    suspend fun invokeFetchStepsGoal(data: GetStepsGoalModel): LiveData<Resource<GetStepsGoalModel.Response>> {
        return repository.fetchLatestGoal(data = data)
    }

    suspend fun invokeSaveStepsGoal(data: SetGoalModel): LiveData<Resource<SetGoalModel.Response>> {
        return repository.saveStepsGoal(data = data)
    }

    suspend fun invokeSaveStepsList(data: StepsSaveListModel): LiveData<Resource<StepsSaveListModel.StepsSaveListResponse>> {
        return repository.saveStepsList(data = data)
    }

    suspend fun invokeStepsHistoryBetweenDates(data: StepsHistoryModel): LiveData<Resource<StepsHistoryModel.Response>> {
        return repository.fetchStepsListHistoryBetweenDates(data = data)
    }

}
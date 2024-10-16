package com.techglock.health.app.remote

import com.techglock.health.app.di.DIModule
import com.techglock.health.app.model.waterTracker.GetDailyWaterIntakeModel
import com.techglock.health.app.model.waterTracker.GetWaterIntakeHistoryByDateModel
import com.techglock.health.app.model.waterTracker.GetWaterIntakeSummaryModel
import com.techglock.health.app.model.waterTracker.SaveDailyWaterIntakeModel
import com.techglock.health.app.model.waterTracker.SaveWaterIntakeGoalModel
import javax.inject.Inject
import javax.inject.Named

class WaterTrackerDatasource @Inject constructor(@Named(DIModule.ENCRYPTED) private val encryptedService: ApiService) {

    suspend fun saveWaterIntakeGoalResponse(data: SaveWaterIntakeGoalModel) =
        encryptedService.saveWaterIntakeGoalApi(data)

    suspend fun saveDailyWaterIntakeResponse(data: SaveDailyWaterIntakeModel) =
        encryptedService.saveDailyWaterIntakeApi(data)

    suspend fun getDailyWaterIntakeResponse(data: GetDailyWaterIntakeModel) =
        encryptedService.getDailyWaterIntakeApi(data)

    suspend fun getWaterIntakeHistoryByDateResponse(data: GetWaterIntakeHistoryByDateModel) =
        encryptedService.getWaterIntakeHistoryByDate(data)

    suspend fun getWaterIntakeSummaryResponse(data: GetWaterIntakeSummaryModel) =
        encryptedService.getWaterIntakeSummary(data)
}
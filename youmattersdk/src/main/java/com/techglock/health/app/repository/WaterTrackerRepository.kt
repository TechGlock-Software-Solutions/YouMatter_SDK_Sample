package com.techglock.health.app.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.techglock.health.app.model.BaseResponse
import com.techglock.health.app.model.waterTracker.GetDailyWaterIntakeModel
import com.techglock.health.app.model.waterTracker.GetWaterIntakeHistoryByDateModel
import com.techglock.health.app.model.waterTracker.GetWaterIntakeSummaryModel
import com.techglock.health.app.model.waterTracker.SaveDailyWaterIntakeModel
import com.techglock.health.app.model.waterTracker.SaveWaterIntakeGoalModel
import com.techglock.health.app.remote.WaterTrackerDatasource
import com.techglock.health.app.repository.utils.NetworkBoundResource
import com.techglock.health.app.repository.utils.Resource
import javax.inject.Inject

interface WaterTrackerRepository {

    suspend fun saveWaterIntakeGoal(data: SaveWaterIntakeGoalModel): LiveData<Resource<SaveWaterIntakeGoalModel.SaveWaterIntakeGoalResponse>>

    suspend fun saveDailyWaterIntake(data: SaveDailyWaterIntakeModel): LiveData<Resource<SaveDailyWaterIntakeModel.SaveDailyWaterIntakeResponse>>

    suspend fun getDailyWaterIntake(data: GetDailyWaterIntakeModel): LiveData<Resource<GetDailyWaterIntakeModel.GetDailyWaterIntakeResponse>>

    suspend fun getWaterIntakeHistoryByDate(data: GetWaterIntakeHistoryByDateModel): LiveData<Resource<GetWaterIntakeHistoryByDateModel.GetWaterIntakeHistoryByDateResponse>>

    suspend fun getWaterIntakeSummary(data: GetWaterIntakeSummaryModel): LiveData<Resource<GetWaterIntakeSummaryModel.GetWaterIntakeSummaryResponse>>
}

class WaterTrackerRepositoryImpl @Inject constructor(
    private val datasource: WaterTrackerDatasource,
    val context: Context
) : WaterTrackerRepository {

    override suspend fun saveWaterIntakeGoal(data: SaveWaterIntakeGoalModel): LiveData<Resource<SaveWaterIntakeGoalModel.SaveWaterIntakeGoalResponse>> {

        return object :
            NetworkBoundResource<SaveWaterIntakeGoalModel.SaveWaterIntakeGoalResponse, BaseResponse<SaveWaterIntakeGoalModel.SaveWaterIntakeGoalResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): SaveWaterIntakeGoalModel.SaveWaterIntakeGoalResponse {
                return SaveWaterIntakeGoalModel.SaveWaterIntakeGoalResponse()
            }

            override fun processResponse(response: BaseResponse<SaveWaterIntakeGoalModel.SaveWaterIntakeGoalResponse>): SaveWaterIntakeGoalModel.SaveWaterIntakeGoalResponse {
                return response.jSONData
            }

            override suspend fun createCallAsync(): BaseResponse<SaveWaterIntakeGoalModel.SaveWaterIntakeGoalResponse> {
                return datasource.saveWaterIntakeGoalResponse(data)
            }

            override suspend fun saveCallResults(items: SaveWaterIntakeGoalModel.SaveWaterIntakeGoalResponse) {

            }

            override fun shouldFetch(data: SaveWaterIntakeGoalModel.SaveWaterIntakeGoalResponse?): Boolean {
                return true
            }

        }.build().asLiveData()

    }

    override suspend fun saveDailyWaterIntake(data: SaveDailyWaterIntakeModel): LiveData<Resource<SaveDailyWaterIntakeModel.SaveDailyWaterIntakeResponse>> {

        return object :
            NetworkBoundResource<SaveDailyWaterIntakeModel.SaveDailyWaterIntakeResponse, BaseResponse<SaveDailyWaterIntakeModel.SaveDailyWaterIntakeResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): SaveDailyWaterIntakeModel.SaveDailyWaterIntakeResponse {
                return SaveDailyWaterIntakeModel.SaveDailyWaterIntakeResponse()
            }

            override fun processResponse(response: BaseResponse<SaveDailyWaterIntakeModel.SaveDailyWaterIntakeResponse>): SaveDailyWaterIntakeModel.SaveDailyWaterIntakeResponse {
                return response.jSONData
            }

            override suspend fun createCallAsync(): BaseResponse<SaveDailyWaterIntakeModel.SaveDailyWaterIntakeResponse> {
                return datasource.saveDailyWaterIntakeResponse(data)
            }

            override suspend fun saveCallResults(items: SaveDailyWaterIntakeModel.SaveDailyWaterIntakeResponse) {

            }

            override fun shouldFetch(data: SaveDailyWaterIntakeModel.SaveDailyWaterIntakeResponse?): Boolean {
                return true
            }

        }.build().asLiveData()

    }

    override suspend fun getDailyWaterIntake(data: GetDailyWaterIntakeModel): LiveData<Resource<GetDailyWaterIntakeModel.GetDailyWaterIntakeResponse>> {

        return object :
            NetworkBoundResource<GetDailyWaterIntakeModel.GetDailyWaterIntakeResponse, BaseResponse<GetDailyWaterIntakeModel.GetDailyWaterIntakeResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): GetDailyWaterIntakeModel.GetDailyWaterIntakeResponse {
                return GetDailyWaterIntakeModel.GetDailyWaterIntakeResponse()
            }

            override fun processResponse(response: BaseResponse<GetDailyWaterIntakeModel.GetDailyWaterIntakeResponse>): GetDailyWaterIntakeModel.GetDailyWaterIntakeResponse {
                return response.jSONData
            }

            override suspend fun createCallAsync(): BaseResponse<GetDailyWaterIntakeModel.GetDailyWaterIntakeResponse> {
                return datasource.getDailyWaterIntakeResponse(data)
            }

            override suspend fun saveCallResults(items: GetDailyWaterIntakeModel.GetDailyWaterIntakeResponse) {

            }

            override fun shouldFetch(data: GetDailyWaterIntakeModel.GetDailyWaterIntakeResponse?): Boolean {
                return true
            }

        }.build().asLiveData()

    }

    override suspend fun getWaterIntakeHistoryByDate(data: GetWaterIntakeHistoryByDateModel): LiveData<Resource<GetWaterIntakeHistoryByDateModel.GetWaterIntakeHistoryByDateResponse>> {

        return object :
            NetworkBoundResource<GetWaterIntakeHistoryByDateModel.GetWaterIntakeHistoryByDateResponse, BaseResponse<GetWaterIntakeHistoryByDateModel.GetWaterIntakeHistoryByDateResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override fun processResponse(response: BaseResponse<GetWaterIntakeHistoryByDateModel.GetWaterIntakeHistoryByDateResponse>): GetWaterIntakeHistoryByDateModel.GetWaterIntakeHistoryByDateResponse {
                return response.jSONData
            }

            override suspend fun loadFromDb(): GetWaterIntakeHistoryByDateModel.GetWaterIntakeHistoryByDateResponse {
                return GetWaterIntakeHistoryByDateModel.GetWaterIntakeHistoryByDateResponse()
            }

            override suspend fun createCallAsync(): BaseResponse<GetWaterIntakeHistoryByDateModel.GetWaterIntakeHistoryByDateResponse> {
                return datasource.getWaterIntakeHistoryByDateResponse(data)
            }

            override suspend fun saveCallResults(items: GetWaterIntakeHistoryByDateModel.GetWaterIntakeHistoryByDateResponse) {

            }

            override fun shouldFetch(data: GetWaterIntakeHistoryByDateModel.GetWaterIntakeHistoryByDateResponse?): Boolean {
                return true
            }

        }.build().asLiveData()

    }

    override suspend fun getWaterIntakeSummary(data: GetWaterIntakeSummaryModel): LiveData<Resource<GetWaterIntakeSummaryModel.GetWaterIntakeSummaryResponse>> {

        return object :
            NetworkBoundResource<GetWaterIntakeSummaryModel.GetWaterIntakeSummaryResponse, BaseResponse<GetWaterIntakeSummaryModel.GetWaterIntakeSummaryResponse>>(
                context
            ) {

            override fun shouldStoreInDb(): Boolean = false

            override suspend fun loadFromDb(): GetWaterIntakeSummaryModel.GetWaterIntakeSummaryResponse {
                return GetWaterIntakeSummaryModel.GetWaterIntakeSummaryResponse()
            }

            override fun processResponse(response: BaseResponse<GetWaterIntakeSummaryModel.GetWaterIntakeSummaryResponse>): GetWaterIntakeSummaryModel.GetWaterIntakeSummaryResponse {
                return response.jSONData
            }

            override suspend fun createCallAsync(): BaseResponse<GetWaterIntakeSummaryModel.GetWaterIntakeSummaryResponse> {
                return datasource.getWaterIntakeSummaryResponse(data)
            }

            override suspend fun saveCallResults(items: GetWaterIntakeSummaryModel.GetWaterIntakeSummaryResponse) {

            }

            override fun shouldFetch(data: GetWaterIntakeSummaryModel.GetWaterIntakeSummaryResponse?): Boolean {
                return true
            }

        }.build().asLiveData()

    }

}
package com.techglock.health.app.track_parameter.domain

import androidx.lifecycle.LiveData
import com.techglock.health.app.model.entity.TrackParameterMaster
import com.techglock.health.app.model.parameter.BMIHistoryModel
import com.techglock.health.app.model.parameter.BloodPressureHistoryModel
import com.techglock.health.app.model.parameter.DashboardObservationData
import com.techglock.health.app.model.parameter.LabRecordsListModel
import com.techglock.health.app.model.parameter.ParameterListModel
import com.techglock.health.app.model.parameter.ParameterPreferenceModel
import com.techglock.health.app.model.parameter.ParameterProfile
import com.techglock.health.app.model.parameter.SaveParameterModel
import com.techglock.health.app.model.parameter.WHRHistoryModel
import com.techglock.health.app.repository.ParameterRepository
import com.techglock.health.app.repository.utils.Resource
import javax.inject.Inject

class ParameterManagementUseCase @Inject constructor(private val repository: ParameterRepository) {

    suspend fun invokeParamList(
        isForceRefresh: Boolean = true,
        data: ParameterListModel
    ): LiveData<Resource<ParameterListModel.Response>> {
        return repository.fetchParamList(isForceRefresh, data)
    }

    suspend fun invokeLabRecordsList(
        isForceRefresh: Boolean = true,
        data: LabRecordsListModel,
        personId: String
    ): LiveData<Resource<TrackParameterMaster.HistoryResponse>> {
        return repository.fetchLabRecordsList(data, personId)
    }

    suspend fun invokeGetSelectParamList(
        selectedParameter: String,
        showAllProfile: String = "true"
    ): List<ParameterListModel.SelectedParameter> {
        return repository.getSelectParameterList(selectedParameter, showAllProfile)
    }

    suspend fun invokeGetDBParamList(): List<TrackParameterMaster.Parameter> {
        return repository.getParametersFromDB()
    }

    suspend fun invokeParameterListBaseOnCode(pCode: String): List<TrackParameterMaster.Parameter> {
        return repository.fetchParamListBaseOnCode(pCode)
    }

    suspend fun invokeParameterHisBaseOnCode(
        pCode: String,
        personId: String
    ): List<TrackParameterMaster.History> {
        return repository.fetchParamHisBaseOnCode(pCode, personId)
    }

//    suspend fun invokeGetDBDashboardList():List<TrackParameterMaster.ParamDashboardData>?{
//        return repository.fetchDashboardListFromDB()
//    }

    suspend fun invokeGetDashboardData(): List<DashboardObservationData> {
        return repository.fetchDashboardDataFromDB()
    }

    suspend fun invokeParameterPreference(data: ParameterPreferenceModel): LiveData<Resource<ParameterPreferenceModel.Response>> {
        return repository.fetchParameterPreferences(data)
    }

    suspend fun invokeBMIHistory(
        data: BMIHistoryModel,
        personId: String
    ): LiveData<Resource<BMIHistoryModel.Response>> {
        return repository.fetchBMIHistory(data, personId)
    }

    suspend fun invokeWHRHistory(
        data: WHRHistoryModel,
        personId: String
    ): LiveData<Resource<WHRHistoryModel.Response>> {
        return repository.fetchWHRHistory(data, personId)
    }

    suspend fun invokeBloodPressureHistory(
        data: BloodPressureHistoryModel,
        personId: String
    ): LiveData<Resource<BloodPressureHistoryModel.Response>> {
        return repository.fetchBloodPressureHistory(data, personId)
    }

    suspend fun invokeSaveParameter(data: SaveParameterModel): LiveData<Resource<SaveParameterModel.Response>> {
        return repository.saveLabParameter(data)
    }

    suspend fun invokeLatestParametersHisBaseOnProfileCode(
        pCode: String,
        personId: String
    ): List<TrackParameterMaster.History> {
        return repository.getLatestParameterBasedOnProfileCode(pCode, personId)
    }

    suspend fun invokeLatestRecordHisBaseOnParameterCode(
        paramCode: String,
        personId: String
    ): List<TrackParameterMaster.History> {
        return repository.getLatestRecordBasedOnParamCode(paramCode, personId)
    }

    suspend fun invokeGetParameterObservationList(
        paramCode: String,
        personId: String
    ): List<TrackParameterMaster.TrackParameterRanges> {
        return repository.getParameterObservationList(paramCode, personId)
    }


    suspend fun invokeGetLatestParametersData(
        profileCode: String,
        personId: String
    ): List<ParameterListModel.InputParameterModel> {
        return repository.getLatestParameterData(profileCode, personId)
    }

    suspend fun invokeGetParameterDataBasedOnRecordDate(
        profileCode: String,
        personId: String,
        recordDate: String
    ): List<ParameterListModel.InputParameterModel> {
        return repository.getParameterDataBasedOnRecordDate(profileCode, personId, recordDate)
    }

    suspend fun invokeGetAllProfileCodes(): List<ParameterProfile> {
        return repository.getAllProfileCodes()
    }

    suspend fun invokeGetAllProfilesWithRecentSelectionList(personId: String): List<ParameterProfile> {
        return repository.getAllProfilesWithRecentSelectionList(personId)
    }

    suspend fun invokeListHistoryByProfileCodeAndMonthYear(
        personId: String,
        profileCode: String,
        month: String,
        year: String
    ): List<TrackParameterMaster.History> {
        return repository.listHistoryByProfileCodeAndMonthYear(personId, profileCode, month, year)
    }

    suspend fun invokeListHistoryWithLatestRecord(
        personId: String,
        profileCode: String,
        parameterCode: String
    ): List<TrackParameterMaster.History> {
        return repository.listHistoryWithLatestRecord(personId, profileCode, parameterCode)
    }

}
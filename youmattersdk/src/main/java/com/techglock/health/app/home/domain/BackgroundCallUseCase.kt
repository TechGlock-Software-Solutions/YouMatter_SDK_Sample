package com.techglock.health.app.home.domain

import androidx.lifecycle.LiveData
import com.techglock.health.app.model.entity.AppVersion
import com.techglock.health.app.model.entity.DataSyncMaster
import com.techglock.health.app.model.entity.HRASummary
import com.techglock.health.app.model.entity.TrackParameterMaster
import com.techglock.health.app.model.entity.Users
import com.techglock.health.app.model.fitness.StepsHistoryModel
import com.techglock.health.app.model.fitness.StepsSaveListModel
import com.techglock.health.app.model.home.CheckAppUpdateModel
import com.techglock.health.app.model.home.SaveCloudMessagingIdModel
import com.techglock.health.app.model.hra.HraMedicalProfileSummaryModel
import com.techglock.health.app.model.medication.MedicationListModel
import com.techglock.health.app.model.parameter.BMIHistoryModel
import com.techglock.health.app.model.parameter.BloodPressureHistoryModel
import com.techglock.health.app.model.parameter.LabRecordsListModel
import com.techglock.health.app.model.parameter.ParameterListModel
import com.techglock.health.app.model.parameter.ParameterPreferenceModel
import com.techglock.health.app.model.parameter.VitalsHistoryModel
import com.techglock.health.app.model.parameter.WHRHistoryModel
import com.techglock.health.app.model.shr.ListDocumentTypesModel
import com.techglock.health.app.model.shr.ListRelativesModel
import com.techglock.health.app.repository.FitnessRepository
import com.techglock.health.app.repository.HomeRepository
import com.techglock.health.app.repository.HraRepository
import com.techglock.health.app.repository.MedicationRepository
import com.techglock.health.app.repository.ParameterRepository
import com.techglock.health.app.repository.StoreRecordRepository
import com.techglock.health.app.repository.utils.Resource
import javax.inject.Inject


class BackgroundCallUseCase @Inject constructor(
    private val homeRepository: HomeRepository,
    private val hraRepository: HraRepository,
    private val shrRepository: StoreRecordRepository,
    private val trackParamRepo: ParameterRepository,
    private val medicationRepository: MedicationRepository,
    private val fitnessRepository: FitnessRepository
) {

    suspend fun invokeGetAppVersionDetails(): AppVersion {
        return homeRepository.getAppVersionDetails()
    }

    suspend fun invokeSaveCloudMessagingId(
        isForceRefresh: Boolean,
        data: SaveCloudMessagingIdModel
    ): LiveData<Resource<SaveCloudMessagingIdModel.SaveCloudMessagingIdResponse>> {
        return homeRepository.saveCloudMessagingId(isForceRefresh, data)
    }

    suspend fun invokeCheckAppUpdate(
        isForceRefresh: Boolean,
        data: CheckAppUpdateModel
    ): LiveData<Resource<CheckAppUpdateModel.CheckAppUpdateResponse>> {
        return homeRepository.checkAppUpdate(isForceRefresh, data)
    }

    suspend fun invokeDocumentType(
        isForceRefresh: Boolean,
        data: ListDocumentTypesModel
    ): LiveData<Resource<ListDocumentTypesModel.ListDocumentTypesResponse>> {
        return shrRepository.fetchDocumentType(isForceRefresh, data)
    }

    suspend fun invokeRelativesList(
        isForceRefresh: Boolean,
        data: ListRelativesModel
    ): LiveData<Resource<ListRelativesModel.ListRelativesResponse>> {
        return homeRepository.fetchRelativesList(isForceRefresh, data)
    }

    suspend fun invokeMedicalProfileSummary(
        isForceRefresh: Boolean,
        data: HraMedicalProfileSummaryModel,
        personId: String
    ): LiveData<Resource<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse>> {
        return hraRepository.getMedicalProfileSummary(isForceRefresh, data, personId)
    }

    suspend fun invokeParamList(
        isForceRefresh: Boolean = true,
        data: ParameterListModel
    ): LiveData<Resource<ParameterListModel.Response>> {
        return trackParamRepo.fetchParamList(isForceRefresh, data)
    }

    suspend fun invokeBMIHistory(
        data: BMIHistoryModel,
        personId: String
    ): LiveData<Resource<BMIHistoryModel.Response>> {
        return trackParamRepo.fetchBMIHistory(data, personId)
    }

    suspend fun invokeWHRHistory(
        data: WHRHistoryModel,
        personId: String
    ): LiveData<Resource<WHRHistoryModel.Response>> {
        return trackParamRepo.fetchWHRHistory(data, personId)
    }

    suspend fun invokeBloodPressureHistory(
        data: BloodPressureHistoryModel,
        personId: String
    ): LiveData<Resource<BloodPressureHistoryModel.Response>> {
        return trackParamRepo.fetchBloodPressureHistory(data, personId)
    }

    suspend fun invokeLabRecordsList(
        isForceRefresh: Boolean = true,
        data: LabRecordsListModel,
        personId: String
    ): LiveData<Resource<TrackParameterMaster.HistoryResponse>> {
        return trackParamRepo.fetchLabRecordsList(data, personId)
    }

    suspend fun invokeLabRecordsVitalsList(
        isForceRefresh: Boolean = true,
        data: VitalsHistoryModel,
        personId: String
    ): LiveData<Resource<VitalsHistoryModel.Response>> {
        return trackParamRepo.fetchLabRecordsVitalsList(data, personId)
    }

    suspend fun fetchMedicationList(
        data: MedicationListModel,
        personId: String
    ): LiveData<Resource<MedicationListModel.Response>> {
        return medicationRepository.fetchMedicationList(data = data, personId = personId)
    }

    suspend fun invokeParameterPreference(data: ParameterPreferenceModel): LiveData<Resource<ParameterPreferenceModel.Response>> {
        return trackParamRepo.fetchParameterPreferences(data)
    }

    suspend fun invokeGetSyncMasterData(personId: String): List<DataSyncMaster> {
        return homeRepository.getSyncMasterData(personId)
    }

    suspend fun addDataSyncDetails(data: DataSyncMaster) {
        return homeRepository.saveSyncDetails(data)
    }

    suspend fun invokeStepsHistory(data: StepsHistoryModel): LiveData<Resource<StepsHistoryModel.Response>> {
        return fitnessRepository.fetchStepsListHistory(data = data)
    }

    suspend fun invokeSaveStepsList(data: StepsSaveListModel): LiveData<Resource<StepsSaveListModel.StepsSaveListResponse>> {
        return fitnessRepository.saveStepsList(data = data)
    }

    suspend fun invokeGetHraSummaryDetails(): HRASummary {
        return hraRepository.getHraSummaryDetails()
    }

    suspend fun invokeDeleteHistoryWithOtherPersonId(personId: String) {
        trackParamRepo.deleteHistoryWithOtherPersonId(personId)
    }

    suspend fun getVitalsData(
        profileCode: String,
        profileCodeTwo: String,
        personId: String
    ): List<TrackParameterMaster.History>? {
        return trackParamRepo.getLatestParameterBasedOnProfileCodes(
            profileCode,
            profileCodeTwo,
            personId
        )
    }

    suspend fun invokeGetLoggedInPersonDetails(): Users {
        return homeRepository.getLoggedInPersonDetails()
    }

    suspend fun invokeLogout() {
        trackParamRepo.logoutUser()
        hraRepository.logoutUser()
        medicationRepository.logoutUser()
        shrRepository.logoutUser()
        homeRepository.logoutUser()
        fitnessRepository.logoutUser()
    }
}
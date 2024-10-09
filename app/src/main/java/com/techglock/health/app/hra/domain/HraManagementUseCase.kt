package com.techglock.health.app.hra.domain

import androidx.lifecycle.LiveData
import com.techglock.health.app.model.entity.HRALabDetails
import com.techglock.health.app.model.entity.HRAQuestions
import com.techglock.health.app.model.entity.HRAVitalDetails
import com.techglock.health.app.model.entity.TrackParameterMaster
import com.techglock.health.app.model.entity.UserRelatives
import com.techglock.health.app.model.entity.Users
import com.techglock.health.app.model.hra.BMIExistModel
import com.techglock.health.app.model.hra.BPExistModel
import com.techglock.health.app.model.hra.HraAssessmentSummaryModel
import com.techglock.health.app.model.hra.HraListRecommendedTestsModel
import com.techglock.health.app.model.hra.HraMedicalProfileSummaryModel
import com.techglock.health.app.model.hra.HraStartModel
import com.techglock.health.app.model.hra.LabRecordsModel
import com.techglock.health.app.model.hra.SaveAndSubmitHraModel
import com.techglock.health.app.model.parameter.ParameterListModel
import com.techglock.health.app.repository.HomeRepository
import com.techglock.health.app.repository.HraRepository
import com.techglock.health.app.repository.ParameterRepository
import com.techglock.health.app.repository.StoreRecordRepository
import com.techglock.health.app.repository.utils.Resource
import javax.inject.Inject


class HraManagementUseCase @Inject constructor(
    private val hraRepository: HraRepository,
    private val homeRepository: HomeRepository,
    private val shrRepository: StoreRecordRepository,
    private val trackParamRepository: ParameterRepository,
) {

    suspend fun invokeStartHra(
        isForceRefresh: Boolean,
        data: HraStartModel,
        relativeId: String
    ): LiveData<Resource<HraStartModel.HraStartResponse>> {
        return hraRepository.startHra(isForceRefresh, data, relativeId)
    }

    suspend fun invokeBMIExist(
        isForceRefresh: Boolean,
        data: BMIExistModel
    ): LiveData<Resource<BMIExistModel.BMIExistResponse>> {
        return hraRepository.isBMIExist(isForceRefresh, data)
    }

    suspend fun invokeBPExist(
        isForceRefresh: Boolean,
        data: BPExistModel
    ): LiveData<Resource<BPExistModel.BPExistResponse>> {
        return hraRepository.isBPExist(isForceRefresh, data)
    }

    suspend fun invokeLabRecords(
        isForceRefresh: Boolean,
        data: LabRecordsModel
    ): LiveData<Resource<LabRecordsModel.LabRecordsExistResponse>> {
        return hraRepository.getLabRecords(isForceRefresh, data)
    }

    suspend fun invokeSaveAndSubmitHra(
        isForceRefresh: Boolean,
        data: SaveAndSubmitHraModel
    ): LiveData<Resource<SaveAndSubmitHraModel.SaveAndSubmitHraResponse>> {
        return hraRepository.saveAndSubmitHRA(isForceRefresh, data)
    }

    suspend fun invokeMedicalProfileSummary(
        isForceRefresh: Boolean,
        data: HraMedicalProfileSummaryModel,
        personId: String
    ): LiveData<Resource<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse>> {
        return hraRepository.getMedicalProfileSummary(isForceRefresh, data, personId)
    }

    suspend fun invokeGetAssessmentSummary(
        isForceRefresh: Boolean,
        data: HraAssessmentSummaryModel
    ): LiveData<Resource<HraAssessmentSummaryModel.AssessmentSummaryResponce>> {
        return hraRepository.getAssessmentSummary(isForceRefresh, data)
    }

    suspend fun invokeGetListRecommendedTests(
        isForceRefresh: Boolean,
        data: HraListRecommendedTestsModel
    ): LiveData<Resource<HraListRecommendedTestsModel.ListRecommendedTestsResponce>> {
        return hraRepository.getListRecommendedTests(isForceRefresh, data)
    }

    suspend fun invokeParamList(
        isForceRefresh: Boolean = true,
        data: ParameterListModel
    ): LiveData<Resource<ParameterListModel.Response>> {
        return trackParamRepository.fetchParamList(isForceRefresh, data)
    }

    suspend fun invokeGetLoggedInPersonDetails(): Users {
        return homeRepository.getLoggedInPersonDetails()
    }

    suspend fun invokeSaveQuesResponse(savedOptionList: ArrayList<HRAQuestions>) {
        hraRepository.saveResponse(savedOptionList)
    }

    suspend fun invokeGetSavedResponse(questionCode: String): String {
        return hraRepository.getSavedResponse(questionCode)
    }

    suspend fun invokeSaveVitalDetails(hraVitalDetails: ArrayList<HRAVitalDetails>) {
        hraRepository.saveVitalDetailsToDb(hraVitalDetails)
    }

    suspend fun invokeSaveLabValue(hraLabValues: HRALabDetails) {
        hraRepository.saveHRALabValue(hraLabValues)
    }

    suspend fun invokeClearLabValue(parameterCode: String) {
        hraRepository.clearHRALabValue(parameterCode)
    }

    suspend fun invokeGetHRASavedDetailsWithQuestionCode(questionCode: String): List<HRAQuestions> {
        return hraRepository.getHRASavedDetailsWithQuestionCode(questionCode)
    }

    suspend fun invokeGetHRASavedDetailsWithCode(code: String): List<HRAQuestions> {
        return hraRepository.getHRASavedDetailsWithCode(code)
    }

    suspend fun invokeGetVitalDetails(): List<HRAVitalDetails> {
        return hraRepository.getVitalDetails()
    }

    suspend fun invokeGetLabDetails(): List<HRALabDetails> {
        return hraRepository.getLabDetails()
    }

    suspend fun invokeGetHRASaveDetailsTabName(tabName: String): List<HRAQuestions> {
        return hraRepository.getSavedDetailsTabNameByCategory(tabName)
    }

    suspend fun invokeClearResponse(questionCode: String) {
        hraRepository.clearQuestionResponse(questionCode)
    }

    suspend fun invokeClearHraQuestionsTable() {
        hraRepository.clearHraQuestionsTable()
    }

    suspend fun invokeClearHraDataTables() {
        hraRepository.clearHraDataTables()
    }

    suspend fun invokeClearTablesForSwitchProfile() {
        homeRepository.clearTablesForSwitchProfile()
    }

    suspend fun invokeGetUserRelatives(): List<UserRelatives> {
        return shrRepository.getUserRelatives()
    }

    suspend fun invokeGetParameterDataByProfileCode(profileCode: String): List<TrackParameterMaster.Parameter> {
        return hraRepository.getParameterDataByProfileCode(profileCode)
    }

}
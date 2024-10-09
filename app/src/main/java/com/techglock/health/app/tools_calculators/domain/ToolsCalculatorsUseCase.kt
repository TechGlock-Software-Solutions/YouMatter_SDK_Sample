package com.techglock.health.app.tools_calculators.domain

import androidx.lifecycle.LiveData
import com.techglock.health.app.model.entity.TrackParameterMaster
import com.techglock.health.app.model.hra.HraMedicalProfileSummaryModel
import com.techglock.health.app.model.hra.LabRecordsModel
import com.techglock.health.app.model.toolscalculators.DiabetesSaveResponceModel
import com.techglock.health.app.model.toolscalculators.HeartAgeSaveResponceModel
import com.techglock.health.app.model.toolscalculators.HypertensionSaveResponceModel
import com.techglock.health.app.model.toolscalculators.SmartPhoneSaveResponceModel
import com.techglock.health.app.model.toolscalculators.StartQuizModel
import com.techglock.health.app.model.toolscalculators.StressAndAnxietySaveResponceModel
import com.techglock.health.app.repository.HraRepository
import com.techglock.health.app.repository.ParameterRepository
import com.techglock.health.app.repository.ToolsCalculatorsRepository
import com.techglock.health.app.repository.utils.Resource
import javax.inject.Inject

class ToolsCalculatorsUseCase @Inject constructor(
    private val repository: ToolsCalculatorsRepository,
    private val parmaRepository: ParameterRepository,
    private val hraRepository: HraRepository
) {

    suspend fun invokeStartQuiz(
        isForceRefresh: Boolean,
        data: StartQuizModel
    ): LiveData<Resource<StartQuizModel.StartQuizResponse>> {
        return repository.startQuiz(isForceRefresh, data)
    }

    suspend fun invokeHeartAgeSaveResponce(
        isForceRefresh: Boolean,
        data: HeartAgeSaveResponceModel
    ): LiveData<Resource<HeartAgeSaveResponceModel.HeartAgeSaveResponce>> {
        return repository.heartAgeSaveResponce(isForceRefresh, data)
    }

    suspend fun invokeDiabetesSaveResponce(
        isForceRefresh: Boolean,
        data: DiabetesSaveResponceModel
    ): LiveData<Resource<DiabetesSaveResponceModel.DiabetesSaveResponce>> {
        return repository.diabetesSaveResponce(isForceRefresh, data)
    }

    suspend fun invokeHypertensionSaveResponce(
        isForceRefresh: Boolean,
        data: HypertensionSaveResponceModel
    ): LiveData<Resource<HypertensionSaveResponceModel.HypertensionSaveResponce>> {
        return repository.hypertensionSaveResponce(isForceRefresh, data)
    }

    suspend fun invokeStressAndAnxietySaveResponce(
        isForceRefresh: Boolean,
        data: StressAndAnxietySaveResponceModel
    ): LiveData<Resource<StressAndAnxietySaveResponceModel.StressAndAnxietySaveResponse>> {
        return repository.stressAndAnxietySaveResponce(isForceRefresh, data)
    }

    suspend fun invokeSmartPhoneSaveResponce(
        isForceRefresh: Boolean,
        data: SmartPhoneSaveResponceModel
    ): LiveData<Resource<SmartPhoneSaveResponceModel.SmartPhoneSaveResponce>> {
        return repository.smartPhoneSaveResponce(isForceRefresh, data)
    }

    suspend fun invokeMedicalProfileSummary(
        isForceRefresh: Boolean,
        data: HraMedicalProfileSummaryModel,
        personId: String
    ): LiveData<Resource<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse>> {
        return hraRepository.getMedicalProfileSummary(isForceRefresh, data, personId)
    }

    suspend fun invokeLabRecords(
        isForceRefresh: Boolean,
        data: LabRecordsModel
    ): LiveData<Resource<LabRecordsModel.LabRecordsExistResponse>> {
        return hraRepository.getLabRecords(isForceRefresh, data)
    }

    suspend fun invokeLatestRecordHisBaseOnParameterCode(
        paramCode: String,
        personId: String
    ): List<TrackParameterMaster.History>? {
        return parmaRepository.getLatestRecordBasedOnParamCode(paramCode, personId)
    }

}
package com.techglock.health.app.remote

import com.techglock.health.app.di.DIModule
import com.techglock.health.app.model.toolscalculators.DiabetesSaveResponceModel
import com.techglock.health.app.model.toolscalculators.HeartAgeSaveResponceModel
import com.techglock.health.app.model.toolscalculators.HypertensionSaveResponceModel
import com.techglock.health.app.model.toolscalculators.SmartPhoneSaveResponceModel
import com.techglock.health.app.model.toolscalculators.StartQuizModel
import com.techglock.health.app.model.toolscalculators.StressAndAnxietySaveResponceModel
import javax.inject.Inject
import javax.inject.Named

class ToolsCalculatorsDatasource @Inject constructor(@Named(DIModule.ENCRYPTED) private val encryptedService: ApiService) {

    suspend fun getStartQuizResponse(data: StartQuizModel) =
        encryptedService.toolsStartQuizApi(data)

    suspend fun getHeartAgeSaveResponce(data: HeartAgeSaveResponceModel) =
        encryptedService.toolsHeartAgeSaveResponceApi(data)

    suspend fun getDiabetesSaveResponce(data: DiabetesSaveResponceModel) =
        encryptedService.toolsDiabetesSaveResponceApi(data)

    suspend fun getHypertensionSaveResponce(data: HypertensionSaveResponceModel) =
        encryptedService.toolsHypertensionSaveResponceApi(data)

    suspend fun getStressAndAnxietySaveResponce(data: StressAndAnxietySaveResponceModel) =
        encryptedService.toolsStressAndAnxietySaveResponceApi(data)

    suspend fun getSmartPhoneSaveResponce(data: SmartPhoneSaveResponceModel) =
        encryptedService.toolsSmartPhoneSaveResponceApi(data)


}
package com.techglock.health.app.security.domain

import androidx.lifecycle.LiveData
import com.techglock.health.app.model.entity.Users
import com.techglock.health.app.model.home.CheckAppUpdateModel
import com.techglock.health.app.model.security.DarwinBoxDataModel
import com.techglock.health.app.repository.FitnessRepository
import com.techglock.health.app.repository.HomeRepository
import com.techglock.health.app.repository.HraRepository
import com.techglock.health.app.repository.MedicationRepository
import com.techglock.health.app.repository.ParameterRepository
import com.techglock.health.app.repository.StoreRecordRepository
import com.techglock.health.app.repository.UserRepository
import com.techglock.health.app.repository.utils.Resource
import javax.inject.Inject

class StartupManagementUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val homeRepository: HomeRepository,
    private val hraRepository: HraRepository,
    private val shrRepository: StoreRecordRepository,
    private val trackParamRepo: ParameterRepository,
    private val medicationRepository: MedicationRepository,
    private val fitnessRepository: FitnessRepository
) {

    suspend fun invokeSso(data: String): LiveData<Resource<Users>> {
        return userRepository.fetchSsoResponse(data)
    }

    suspend fun invokeCheckAppUpdate(
        isForceRefresh: Boolean,
        data: CheckAppUpdateModel
    ): LiveData<Resource<CheckAppUpdateModel.CheckAppUpdateResponse>> {
        return homeRepository.checkAppUpdate(isForceRefresh, data)
    }

    suspend fun invokeDarwinBoxDataResponse(
        isForceRefresh: Boolean,
        data: DarwinBoxDataModel
    ): LiveData<Resource<DarwinBoxDataModel.DarwinBoxDataResponse>> {
        return userRepository.darwinBoxDataResponse(isForceRefresh, data)
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
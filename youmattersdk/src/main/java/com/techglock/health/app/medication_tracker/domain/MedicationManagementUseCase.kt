package com.techglock.health.app.medication_tracker.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.techglock.health.app.model.entity.MedicationEntity
import com.techglock.health.app.model.entity.UserRelatives
import com.techglock.health.app.model.medication.AddInTakeModel
import com.techglock.health.app.model.medication.AddMedicineModel
import com.techglock.health.app.model.medication.DeleteMedicineModel
import com.techglock.health.app.model.medication.DrugsModel
import com.techglock.health.app.model.medication.GetMedicineModel
import com.techglock.health.app.model.medication.MedicationListModel
import com.techglock.health.app.model.medication.MedicineInTakeModel
import com.techglock.health.app.model.medication.MedicineListByDayModel
import com.techglock.health.app.model.medication.SetAlertModel
import com.techglock.health.app.model.medication.UpdateMedicineModel
import com.techglock.health.app.repository.HomeRepository
import com.techglock.health.app.repository.MedicationRepository
import com.techglock.health.app.repository.utils.Resource
import javax.inject.Inject

class MedicationManagementUseCase @Inject constructor(
    private val repository: MedicationRepository,
    private val homeRepository: HomeRepository
) {

    suspend fun invokeDrugsList(data: DrugsModel): LiveData<Resource<DrugsModel.DrugsResponse>> {
        return repository.getDrugsListResponse(data = data)
    }

    suspend fun fetchMedicationList(
        data: MedicationListModel,
        personId: String
    ): LiveData<Resource<MedicationListModel.Response>> {
        return repository.fetchMedicationList(data = data, personId = personId)
    }

    suspend fun invokeGetMedicationListByDay(data: MedicineListByDayModel): LiveData<Resource<MedicineListByDayModel.MedicineListByDayResponse>> {
        return repository.getMedicationListByDay(data = data)
    }

    suspend fun invokeSaveMedicine(data: AddMedicineModel): LiveData<Resource<AddMedicineModel.AddMedicineResponse>> {
        return repository.saveMedicine(data = data)
    }

    suspend fun invokeUpdateMedicine(data: UpdateMedicineModel): LiveData<Resource<UpdateMedicineModel.UpdateMedicineResponse>> {
        return repository.updateMedicine(data = data)
    }

    suspend fun invokeDeleteMedicine(
        data: DeleteMedicineModel,
        medicationID: String
    ): LiveData<Resource<DeleteMedicineModel.DeleteMedicineResponse>> {
        return repository.deleteMedicine(data = data, medicationID = medicationID)
    }

    suspend fun invokeSetAlert(data: SetAlertModel): LiveData<Resource<SetAlertModel.SetAlertResponse>> {
        return repository.setAlert(data = data)
    }

    suspend fun invokeGetMedicine(data: GetMedicineModel): LiveData<Resource<GetMedicineModel.GetMedicineResponse>> {
        return repository.getMedicine(data = data)
    }

    suspend fun invokeGetMedicationInTakeByScheduleID(
        data: MedicineInTakeModel,
        scheduleId: Int
    ): LiveData<Resource<MedicineInTakeModel.MedicineDetailsResponse>> {
        return repository.getMedicationInTakeByScheduleID(data = data, scheduleId = scheduleId)
            .map {
                it
            }
    }

    suspend fun invokeAddMedicineIntake(data: AddInTakeModel): LiveData<Resource<AddInTakeModel.AddInTakeResponse>> {
        return repository.addMedicineIntake(data = data)
    }

    suspend fun getOngoingMedicines(): List<MedicationEntity.Medication> {
        return repository.getOngoingMedicines()
    }

    suspend fun getCompletedMedicines(): List<MedicationEntity.Medication> {
        return repository.getCompletedMedicines()
    }

    suspend fun getAllMyMedicines(): List<MedicationEntity.Medication> {
        return repository.getAllMyMedicines()
    }

    suspend fun getPastMedicines(): List<MedicationEntity.Medication> {
        return repository.getPastMedicines()
    }

    suspend fun invokeUpdateNotificationAlert(id: String, isAlert: Boolean) {
        return repository.updateNotificationAlert(id, isAlert)
    }

    suspend fun getMedicineDetailsByMedicationId(medicationId: Int): MedicationEntity.Medication {
        return repository.getMedicineDetailsByMedicationId(medicationId)
    }

    suspend fun invokeGetUserRelativeDetailsByRelativeId(relativeId: String): UserRelatives {
        return homeRepository.getUserRelativeDetailsByRelativeId(relativeId)
    }

}
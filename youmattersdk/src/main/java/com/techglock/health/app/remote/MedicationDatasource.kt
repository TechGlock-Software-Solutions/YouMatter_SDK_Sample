package com.techglock.health.app.remote

import com.techglock.health.app.di.DIModule
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
import javax.inject.Inject
import javax.inject.Named

class MedicationDatasource @Inject constructor(@Named(DIModule.ENCRYPTED) private val encryptedService: ApiService) {

    suspend fun fetchDrugsListAsync(data: DrugsModel) = encryptedService.fetchDrugsList(data)

    suspend fun fetchMedicationList(data: MedicationListModel) =
        encryptedService.fetchMedicationList(data)

    suspend fun fetchMedicationInTakeByScheduleID(data: MedicineInTakeModel) =
        encryptedService.fetchMedicationInTakeByScheduleID(data)

    suspend fun deleteMedicine(data: DeleteMedicineModel) = encryptedService.deleteMedicine(data)

    suspend fun getMedicine(data: GetMedicineModel) = encryptedService.getMedicine(data)

    suspend fun fetchMedicationListByDay(data: MedicineListByDayModel) =
        encryptedService.fetchMedicationListByDay(data)

    suspend fun setAlert(data: SetAlertModel) = encryptedService.setAlert(data)

    suspend fun saveMedicine(data: AddMedicineModel) = encryptedService.saveMedicine(data)

    suspend fun updateMedicine(data: UpdateMedicineModel) = encryptedService.UpdateMedicine(data)

    suspend fun addInTake(data: AddInTakeModel) = encryptedService.addInTake(data)
}
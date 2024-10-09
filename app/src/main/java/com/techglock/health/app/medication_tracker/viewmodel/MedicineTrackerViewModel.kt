package com.techglock.health.app.medication_tracker.viewmodel

//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.RequestBody.Companion.toRequestBody
//import com.caressa.allizhealth.app.model.tempconst.Configuration
import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Configuration
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.NavigationConstants
import com.techglock.health.app.common.constants.PreferenceConstants
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.EncryptionUtility
import com.techglock.health.app.common.utils.Event
import com.techglock.health.app.common.utils.LocaleHelper
import com.techglock.health.app.common.utils.PreferenceUtils
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.medication_tracker.MedNotificationApiService
import com.techglock.health.app.medication_tracker.common.MedicationSingleton
import com.techglock.health.app.medication_tracker.common.MedicationTrackerHelper
import com.techglock.health.app.medication_tracker.domain.MedicationManagementUseCase
import com.techglock.health.app.medication_tracker.model.ReminderNotification
import com.techglock.health.app.medication_tracker.model.TimeModel
import com.techglock.health.app.medication_tracker.ui.AddMedicineFragment
import com.techglock.health.app.medication_tracker.ui.MedicineDashboardFragment
import com.techglock.health.app.medication_tracker.ui.MyMedicationsFragment
import com.techglock.health.app.medication_tracker.ui.ScheduleDetailsFragment
import com.techglock.health.app.medication_tracker.ui.ScheduleDetailsFragmentDirections
import com.techglock.health.app.model.entity.MedicationEntity
import com.techglock.health.app.model.entity.UserRelatives
import com.techglock.health.app.model.medication.AddInTakeModel
import com.techglock.health.app.model.medication.AddMedicineModel
import com.techglock.health.app.model.medication.DeleteMedicineModel
import com.techglock.health.app.model.medication.DrugsModel
import com.techglock.health.app.model.medication.GetMedicineModel
import com.techglock.health.app.model.medication.MedicationListModel
import com.techglock.health.app.model.medication.MedicationModel
import com.techglock.health.app.model.medication.MedicineInTakeModel
import com.techglock.health.app.model.medication.MedicineListByDayModel
import com.techglock.health.app.model.medication.SetAlertModel
import com.techglock.health.app.model.medication.UpdateMedicineModel
import com.techglock.health.app.repository.utils.Resource
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class MedicineTrackerViewModel @Inject constructor(
    application: Application,
    private val preferenceUtils: PreferenceUtils,
    private val useCase: MedicationManagementUseCase,
    val medicationTrackerHelper: MedicationTrackerHelper,
    private val context: Context?
) : BaseViewModel(application) {

    private val localResource =
        LocaleHelper.getLocalizedResources(context!!, Locale(LocaleHelper.getLanguage(context!!)))!!
    private var medDashboardFragment: MedicineDashboardFragment? = null

    val personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
    private val authToken = preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
    private val notificationManager =
        context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val medicinesList = MutableLiveData<List<MedicationEntity.Medication>>()
    val medicineDetails = MutableLiveData<MedicationEntity.Medication>()

    private var drugsSource: LiveData<Resource<DrugsModel.DrugsResponse>> = MutableLiveData()
    private val _drugsList = MediatorLiveData<DrugsModel.DrugsResponse>()
    val drugsList: LiveData<DrugsModel.DrugsResponse> get() = _drugsList

    private var medicineListDataSource: LiveData<Resource<MedicationListModel.Response>> =
        MutableLiveData()
    private val _medicineList = MediatorLiveData<MedicationListModel.Response>()
    val medicineList: LiveData<MedicationListModel.Response> get() = _medicineList

    private var medicineListByDaySource: LiveData<Resource<MedicineListByDayModel.MedicineListByDayResponse>> =
        MutableLiveData()
    private val _medicineListByDay =
        MediatorLiveData<MedicineListByDayModel.MedicineListByDayResponse>()
    val medicineListByDay: LiveData<MedicineListByDayModel.MedicineListByDayResponse> get() = _medicineListByDay

    private var saveMedicineSource: LiveData<Resource<AddMedicineModel.AddMedicineResponse>> =
        MutableLiveData()
    private val _saveMedicine = MediatorLiveData<AddMedicineModel.AddMedicineResponse>()
    val saveMedicine: LiveData<AddMedicineModel.AddMedicineResponse> get() = _saveMedicine

    private var updateMedicineSource: LiveData<Resource<UpdateMedicineModel.UpdateMedicineResponse>> =
        MutableLiveData()
    private val _updateMedicine = MediatorLiveData<UpdateMedicineModel.UpdateMedicineResponse>()
    val updateMedicine: LiveData<UpdateMedicineModel.UpdateMedicineResponse> get() = _updateMedicine

    private var deleteMedicineSource: LiveData<Resource<DeleteMedicineModel.DeleteMedicineResponse>> =
        MutableLiveData()
    private val _deleteMedicine = MediatorLiveData<DeleteMedicineModel.DeleteMedicineResponse>()
    val deleteMedicine: LiveData<DeleteMedicineModel.DeleteMedicineResponse> get() = _deleteMedicine

    private var setAlertSource: LiveData<Resource<SetAlertModel.SetAlertResponse>> =
        MutableLiveData()
    private val _setAlert = MediatorLiveData<SetAlertModel.SetAlertResponse>()
    val setAlert: LiveData<SetAlertModel.SetAlertResponse> get() = _setAlert

    /*  private var getMedicineSource: LiveData<Resource<GetMedicineModel.GetMedicineResponse>> = MutableLiveData()
        private val _getMedicine = MediatorLiveData<GetMedicineModel.GetMedicineResponse>()
        val getMedicine: LiveData<GetMedicineModel.GetMedicineResponse> get() = _getMedicine*/

    private var listMedicationInTakeSource: LiveData<Resource<MedicineInTakeModel.MedicineDetailsResponse>> =
        MutableLiveData()
    private val _listMedicationInTake =
        MediatorLiveData<MedicineInTakeModel.MedicineDetailsResponse>()
    val listMedicationInTake: LiveData<MedicineInTakeModel.MedicineDetailsResponse> get() = _listMedicationInTake

    private var addMedicineIntakeSource: LiveData<Resource<AddInTakeModel.AddInTakeResponse>> =
        MutableLiveData()
    private val _addMedicineIntake = MediatorLiveData<AddInTakeModel.AddInTakeResponse>()
    val addMedicineIntake: LiveData<AddInTakeModel.AddInTakeResponse> get() = _addMedicineIntake

    //fun fetchDrugsList(str: String) = viewModelScope.launch(Dispatchers.Main) {
    fun fetchDrugsList(str: String, fragment: AddMedicineFragment) =
        fragment.lifecycleScope.launch(Dispatchers.Main) {
            try {
                val requestData = DrugsModel(
                    Gson().toJson(
                        DrugsModel.JSONDataRequest(
                            name = str,
                            OrAndFlag = 1,
                            message = "Getting List.."
                        ), DrugsModel.JSONDataRequest::class.java
                    ),
                    authToken = preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
                )

                _drugsList.removeSource(drugsSource)
                withContext(Dispatchers.IO) {
                    drugsSource = useCase.invokeDrugsList(requestData)
                }
                _drugsList.addSource(drugsSource) {
                    it.data?.let {
                        _drugsList.value = it
                    }

                    if (it.status == Resource.Status.SUCCESS) {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                    }

                    if (it.status == Resource.Status.ERROR) {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        if (it.errorNumber.equals("1100014", true)) {
                            _sessionError.value = Event(true)
                        } else {
                            toastMessage(it.errorMessage)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    //fun fetchMedicationList() = viewModelScope.launch(Dispatchers.Main) {
    fun fetchMedicationList(fragment: MyMedicationsFragment) =
        fragment.lifecycleScope.launch(Dispatchers.Main) {
            try {
                val requestData = MedicationListModel(
                    Gson().toJson(
                        MedicationListModel.JSONDataRequest(
                            personId = preferenceUtils.getPreference(
                                PreferenceConstants.PERSONID,
                                "0"
                            ),
                            toDate = DateHelper.currentDateAsStringyyyyMMdd
                        ), MedicationListModel.JSONDataRequest::class.java
                    ), authToken = preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
                )

                //_progressBar.value = Event("Loading...")
                _medicineList.removeSource(medicineListDataSource)
                withContext(Dispatchers.IO) {
                    medicineListDataSource = useCase.fetchMedicationList(
                        requestData,
                        preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
                    )
                }
                _medicineList.addSource(medicineListDataSource) {
                    it.data?.let {
                        _medicineList.value = it
                    }

                    if (it.status == Resource.Status.SUCCESS) {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        try {
                            Utilities.printLogError("Now Update")
                            fragment.binding.spinnerMedication.setSelection(fragment.selectedTab)
                            //fragment.updateData(fragment.selectedTab)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    if (it.status == Resource.Status.ERROR) {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        if (it.errorNumber.equals("1100014", true)) {
                            _sessionError.value = Event(true)
                        } else {
                            toastMessage(it.errorMessage)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    fun callMedicineListByDayApi(medicationDate: String, fragment: MedicineDashboardFragment) =
        fragment.lifecycleScope.launch(Dispatchers.Main) {
            try {
                medDashboardFragment = fragment
                val requestData = MedicineListByDayModel(
                    Gson().toJson(
                        MedicineListByDayModel.JSONDataRequest(
                            personID = preferenceUtils.getPreference(
                                PreferenceConstants.PERSONID,
                                "0"
                            ),
                            medicationDate = medicationDate
                        ), MedicineListByDayModel.JSONDataRequest::class.java
                    ), authToken = preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
                )

                //_progressBar.value = Event("Getting Medicines...")
                _medicineListByDay.removeSource(medicineListByDaySource)
                withContext(Dispatchers.IO) {
                    medicineListByDaySource = useCase.invokeGetMedicationListByDay(requestData)
                }
                _medicineListByDay.addSource(medicineListByDaySource) {
                    it?.data?.let {
                        _medicineListByDay.value = it
                    }

                    if (it.status == Resource.Status.SUCCESS) {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        try {
                            val medicineListByDay = it.data!!.medications.toMutableList()
                            Utilities.printLogError("medicineListByDay---> ${medicineListByDay.size}")
                            it?.data.let {
                                if (it.medications.isNotEmpty()) {
                                    medicineListByDay.sortByDescending { med -> med.notification!!.setAlert }
                                    MedicationSingleton.getInstance()!!
                                        .setMedicineListByDay(medicineListByDay)
                                }
                            }
                            fragment.stopShimmer()
                            fragment.updateMedicinesList()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    if (it.status == Resource.Status.ERROR) {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        if (it.errorNumber.equals("1100014", true)) {
                            _sessionError.value = Event(true)
                        } else {
                            toastMessage(it.errorMessage)
                        }
                        fragment.stopShimmer()
                        fragment.updateMedicinesList()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    fun callAddOrUpdateMedicineApi(
        medicine: MedicationModel.Medication,
        scheduleList: List<TimeModel>,
        removedScheduleList: List<TimeModel>,
        dosage: Double, tab: Int, fragment: ScheduleDetailsFragment
    ) {
        medicine.personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
        var timeModel: TimeModel
        val medicationScheduleList: MutableList<MedicationModel.MedicationSchedule> =
            mutableListOf()
        for (i in scheduleList.indices) {
            val medSchedule = MedicationModel.MedicationSchedule()
            timeModel = scheduleList[i]
            medSchedule.medicationID = medicine.medicationID.toString()
            medSchedule.scheduleId = timeModel.scheduleId
            medSchedule.scheduleTime = timeModel.time
            medSchedule.dosage = dosage.toString()
            medicationScheduleList.add(medSchedule)
        }
        medicine.medicationScheduleList = medicationScheduleList
        //***********************************
        if (removedScheduleList.isNotEmpty()) {
            //deleteRemovedSchedules(medicineId, removedScheduleList)
        }
        //***********************************
        if (medicine.medicationID == 0) {
            Utilities.printLogError("from--->Add")
            callSaveMedicineApi(medicine, fragment)
        } else {
            Utilities.printLogError("from--->Update")
            callUpdateMedicineApi(medicine, tab, fragment)
        }
    }

    //private fun callSaveMedicineApi(medicine: MedicationModel.Medication, fragment: ScheduleDetailsFragment) = viewModelScope.launch(Dispatchers.Main) {
    private fun callSaveMedicineApi(
        medicine: MedicationModel.Medication,
        fragment: ScheduleDetailsFragment
    ) = fragment.lifecycleScope.launch(Dispatchers.Main) {
        try {
            val requestData = AddMedicineModel(
                Gson().toJson(
                    AddMedicineModel.JSONDataRequest(
                        medication = medicine
                    ), AddMedicineModel.JSONDataRequest::class.java
                ), authToken = preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
            )

            _progressBar.value = Event("Adding Medicine...")
            _saveMedicine.removeSource(saveMedicineSource)
            withContext(Dispatchers.IO) {
                saveMedicineSource = useCase.invokeSaveMedicine(requestData)
            }
            _saveMedicine.addSource(saveMedicineSource) {
                it.data?.let {
                    _saveMedicine.value = it
                }

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data?.medication?.medicationId != null && it.data.medication.medicationId != 0) {
                        toastMessage(localResource.getString(R.string.MEDICINE_ADDED))
                        val data = HashMap<String, Any>()
                        data[CleverTapConstants.DRUG_ID] = medicine.drug.drugId
                        data[CleverTapConstants.MEDICATION_ID] = it.data.medication.medicationId
                        data[CleverTapConstants.MEDICINE_NAME] = medicine.drug.name
                        CleverTapHelper.pushEventWithProperties(
                            context!!,
                            CleverTapConstants.ADD_MEDICATION,
                            data
                        )
                        navigate(
                            ScheduleDetailsFragmentDirections.actionScheduleMedicineFragmentToMedicineHome(
                                Constants.DASHBOARD,
                                DateHelper.currentDateAsStringyyyyMMdd,
                                tab = 0
                            )
                        )
//                        FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.MEDICINE_UPLOAD_EVENT)
                    }
                }

                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.errorNumber.equals("1100014", true)) {
                        _sessionError.value = Event(true)
                    } else {
                        toastMessage(it.errorMessage)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //private fun callUpdateMedicineApi(medicine: MedicationModel.Medication,tab:Int) = viewModelScope.launch(Dispatchers.Main) {
    private fun callUpdateMedicineApi(
        medicine: MedicationModel.Medication, tab: Int,
        fragment: ScheduleDetailsFragment
    ) = fragment.lifecycleScope.launch(Dispatchers.Main) {
        try {
            val requestData = UpdateMedicineModel(
                Gson().toJson(
                    UpdateMedicineModel.JSONDataRequest(
                        medication = medicine
                    ), UpdateMedicineModel.JSONDataRequest::class.java
                ), authToken = preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
            )

            _progressBar.value = Event("Updating Medicine...")
            _updateMedicine.removeSource(updateMedicineSource)
            withContext(Dispatchers.IO) {
                updateMedicineSource = useCase.invokeUpdateMedicine(requestData)
            }
            _updateMedicine.addSource(updateMedicineSource) {
                it.data?.let { _updateMedicine.value = it }

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data?.medication?.medicationId != null && it.data.medication.medicationId != 0) {
                        val data = HashMap<String, Any>()
                        data[CleverTapConstants.DRUG_ID] = medicine.drug.drugId
                        data[CleverTapConstants.MEDICATION_ID] = it.data.medication.medicationId
                        data[CleverTapConstants.MEDICINE_NAME] = medicine.drug.name
                        CleverTapHelper.pushEventWithProperties(
                            context!!,
                            CleverTapConstants.UPDATE_MEDICATION,
                            data
                        )
                        toastMessage(localResource.getString(R.string.MEDICINE_UPDATED))
                        navigate(
                            ScheduleDetailsFragmentDirections.actionScheduleMedicineFragmentToMedicineHome(
                                Constants.MEDICATION,
                                medicine.prescribedDate,
                                tab = tab
                            )
                        )
                    }
                }

                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.errorNumber.equals("1100014", true)) {
                        _sessionError.value = Event(true)
                    } else {
                        toastMessage(it.errorMessage)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //fun callDeleteMedicineApi(spinnerPos: Int,medicine:MedicationEntity.Medication,fragment: MyMedicationsFragment) = viewModelScope.launch(Dispatchers.Main) {
    fun callDeleteMedicineApi(
        spinnerPos: Int, medicine: MedicationEntity.Medication,
        fragment: MyMedicationsFragment
    ) = fragment.lifecycleScope.launch(Dispatchers.Main) {
        try {
            val requestData = DeleteMedicineModel(
                Gson().toJson(
                    DeleteMedicineModel.JSONDataRequest(
                        medicationID = medicine.medicationId.toString()
                    ), DeleteMedicineModel.JSONDataRequest::class.java
                ), authToken = preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
            )

            _progressBar.value = Event(Constants.LOADER_DELETE)
            _deleteMedicine.removeSource(deleteMedicineSource)
            withContext(Dispatchers.IO) {
                deleteMedicineSource =
                    useCase.invokeDeleteMedicine(requestData, medicine.medicationId.toString())
            }
            _deleteMedicine.addSource(deleteMedicineSource) {
                it.data?.let {
                    _deleteMedicine.value = it
                }

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data != null && it.data.isProcessed) {
                        toastMessage(localResource.getString(R.string.MEDICINE_DELETED))
                        val data = HashMap<String, Any>()
                        data[CleverTapConstants.DRUG_ID] = medicine.drugID
                        data[CleverTapConstants.MEDICATION_ID] = medicine.medicationId
                        data[CleverTapConstants.MEDICINE_NAME] =
                            medicine.drug.name + " - " + medicine.drug.strength
                        CleverTapHelper.pushEventWithProperties(
                            context!!,
                            CleverTapConstants.DELETE_MEDICATION,
                            data
                        )
                    }
                    fragment.updateData(spinnerPos)
                }

                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.errorNumber.equals("1100014", true)) {
                        _sessionError.value = Event(true)
                    } else {
                        toastMessage(it.errorMessage)
                    }
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun callSetAlertApi(
        medicine: MedicineListByDayModel.Medication,
        fragment: MedicineDashboardFragment
    ) = fragment.lifecycleScope.launch(Dispatchers.Main) {

        val medicationID = medicine.medicationId.toString()
        val setAlert = medicine.notification!!.setAlert
        val requestData = SetAlertModel(
            Gson().toJson(
                SetAlertModel.JSONDataRequest(
                    medicationID = medicationID,
                    setAlert = setAlert!!
                ), SetAlertModel.JSONDataRequest::class.java
            ), authToken = preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
        )

        _progressBar.value = Event("Updating Notification Alert...")
        _setAlert.removeSource(setAlertSource)
        withContext(Dispatchers.IO) {
            setAlertSource = useCase.invokeSetAlert(requestData)
        }
        _setAlert.addSource(setAlertSource) {
            it.data?.let {
                _setAlert.value = it
            }

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                val msgTxt: String = if (setAlert) {
                    localResource.getString(R.string.ALERT_ENABLED)
                } else {
                    localResource.getString(R.string.ALERT_DISABLED)
                }
                if (it.data != null && it.data.isProcessed) {
                    val list = MedicationSingleton.getInstance()!!.geMedicineListByDay()
                    for (medicineDetails in list) {
                        if (medicineDetails.medicationId.toString()
                                .equals(medicationID, ignoreCase = true)
                        ) {
                            medicineDetails.notification!!.setAlert = setAlert
                            break
                        }
                    }
                    fragment.updateMedicinesList()
                    updateNotificationAlert(medicationID, setAlert)
                    toastMessage(msgTxt)
                }
            }

            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.errorNumber.equals("1100014", true)) {
                    _sessionError.value = Event(true)
                } else {
                    toastMessage(it.errorMessage)
                }
            }
        }
    }

    fun getMedicationInTakeByScheduleID(
        notificationData: Intent,
        fragment: MedicineDashboardFragment
    ) = fragment.lifecycleScope.launch(Dispatchers.Main) {

        try {
            val scheduleID = notificationData.getStringExtra(Constants.SERVER_SCHEDULE_ID)!!
            val date = notificationData.getStringExtra(Constants.DATE)!!
            val requestData = MedicineInTakeModel(
                Gson().toJson(
                    MedicineInTakeModel.JSONDataRequest(
                        scheduleID = scheduleID.toInt(),
                        recordDate = date
                    ), MedicineInTakeModel.JSONDataRequest::class.java
                ), authToken = preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
            )

            _progressBar.value = Event("Loading...")
            _listMedicationInTake.removeSource(listMedicationInTakeSource)
            withContext(Dispatchers.IO) {
                listMedicationInTakeSource =
                    useCase.invokeGetMedicationInTakeByScheduleID(requestData, scheduleID.toInt())
            }
            _listMedicationInTake.addSource(listMedicationInTakeSource) {
                it.data?.let {
                    _listMedicationInTake.value = it
                }

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data?.medicationInTakes != null) {
                        val medicationInTakes = it.data.medicationInTakes
                        var status = ""
                        var medicationInTakeID = 0
                        if (medicationInTakes.isNotEmpty()) {
                            status = medicationInTakes[0].status
                            medicationInTakeID = medicationInTakes[0].medicineInTakeId
                        }
                        fragment.defaultNotificationDialog!!.updateMedicineDetails(
                            status,
                            medicationInTakeID
                        )
                    }
                }

                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.errorNumber.equals("1100014", true)) {
                        _sessionError.value = Event(true)
                    } else {
                        toastMessage(it.errorMessage)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun callAddMedicineIntakeApi(
        medicationInTakeList: List<AddInTakeModel.MedicationInTake>,
        fragment: MedicineDashboardFragment
    ) = fragment.lifecycleScope.launch(Dispatchers.Main) {
/*        fun callAddMedicineIntakeApi(medicationInTakeList: List<AddInTakeModel.MedicationInTake>) =
            viewModelScope.launch(Dispatchers.Main) {*/

        val requestData = AddInTakeModel(
            Gson().toJson(
                AddInTakeModel.JSONDataRequest(
                    medicationInTake = medicationInTakeList
                ), AddInTakeModel.JSONDataRequest::class.java
            ), authToken = preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
        )

        _progressBar.value = Event("Adding Medicine Intake...")
        _addMedicineIntake.removeSource(addMedicineIntakeSource)
        withContext(Dispatchers.IO) {
            addMedicineIntakeSource = useCase.invokeAddMedicineIntake(requestData)
        }
        _addMedicineIntake.addSource(addMedicineIntakeSource) {
            it.data?.let {
                _addMedicineIntake.value = it
            }
            //updateBroadcastReceiver()

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)

                if (!it.data?.medicationInTake.isNullOrEmpty()) {
                    val list = it.data!!.medicationInTake
                    var status = ""
                    val medicineDetailsList =
                        MedicationSingleton.getInstance()!!.geMedicineListByDay()
                    if (medicineDetailsList.size > 0) {
                        for (inTakeObject in list) {
                            for (medicineDetails in medicineDetailsList) {
                                if (inTakeObject.medicationID == medicineDetails.medicationId) {
                                    for (scheduleDetails in medicineDetails.medicationScheduleList) {
                                        if (inTakeObject.scheduleID == scheduleDetails.scheduleId) {
                                            scheduleDetails.status = inTakeObject.status
                                            status = inTakeObject.status
                                            scheduleDetails.medicationInTakeID =
                                                inTakeObject.medicationInTakeID
                                            Utilities.printLogError("Intake Updated.....")
                                        }
                                    }
                                }
                            }
                        }
                    }
                    fragment.updateMedicinesList()
                    updateAndCancelNotification(status)
                }
            }

            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.errorNumber.equals("1100014", true)) {
                    _sessionError.value = Event(true)
                } else {
                    toastMessage(it.errorMessage)
                }
            }
        }
    }

    private fun updateAndCancelNotification(takeStatus: String) {
        try {
            val notificationData = MedicationSingleton.getInstance()!!.getNotificationIntent()
            val notificationID = notificationData.getIntExtra(Constants.NOTIFICATION_ID, -1)
            val medName = notificationData.getStringExtra(Constants.MEDICINE_NAME)
            Utilities.printLog("notificationData=>$notificationData")
            if (notificationID != -1 && !Utilities.isNullOrEmpty(medName)) {
                var text = "You have $takeStatus : $medName"
                when (takeStatus) {
                    "Taken" -> {
                        text = "${localResource.getString(R.string.YOU_HAVE_TAKEN)} : $medName"
                    }

                    "Skipped" -> {
                        text = "${localResource.getString(R.string.YOU_HAVE_SKIPPED)} : $medName"
                    }
                }

                //Cancel notification
                cancelNotification(notificationManager, notificationID)
                val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                //Update the Notification to show that the Status has been Updated.
                val repliedNotification =
                    NotificationCompat.Builder(context!!, "fcm_medication_channel")
                        .setSmallIcon(R.drawable.notification_logo)
                        //.setColor(ContextCompat.getColor(context!!, R.color.colorPrimary))
                        .setSound(alarmSound)
                        .setTicker(context!!.resources.getString(R.string.app_name))
                        .setContentTitle(text)
                notificationManager.notify(notificationID, repliedNotification.build())
                /*                Handler(Looper.getMainLooper()).postDelayed({
                                    cancelNotification(notificationManager, notificationID)
                                }, 2000)*/
                /*                Timer().schedule(2000){
                                    cancelNotification(notificationManager, notificationID)
                                }*/
                //cancelNotification(notificationManager, notificationID)
                //MedicationSingleton.getInstance()!!.setNotificationIntent(Intent())
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*    fun checkRelativeExistAndShowNotification(context!!: Context, data: ReminderNotification) =
            viewModelScope.launch {
                val relativeId = data.personID
                var relativeDetails: UserRelatives
                withContext(Dispatchers.IO) {
                    relativeDetails = useCase.invokeGetUserRelativeDetailsByRelativeId(relativeId)
                    if (relativeDetails != null && relativeId == relativeDetails.relativeID) {
                        medicationTrackerHelper.displayMedicineReminderNotification(context!!, data, relativeDetails)
                    } else {
                        Utilities.printLogError("Relative Details not Exist for PersonID--->$relativeId")
                    }
                }
            }*/

    fun checkPersonExistAndShowNotification(context: Context, data: ReminderNotification) =
        viewModelScope.launch {
            val personID = data.personID
            val loggedInPersonID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
            val loggedInPersonName =
                preferenceUtils.getPreference(PreferenceConstants.FIRSTNAME, "")
            if (personID == loggedInPersonID) {
                medicationTrackerHelper.displayMedicineReminderNotification(
                    context,
                    data,
                    loggedInPersonName
                )
            } else {
                Utilities.printLogError("Person Details not Exist for PersonID--->$personID")
            }
        }

    fun checkRelativeExistAndLaunchApp(intent: Intent) = viewModelScope.launch {
        val relativeId = intent.getStringExtra(Constants.PERSON_ID)!!
        var relativeDetails: UserRelatives
        withContext(Dispatchers.IO) {
            relativeDetails = useCase.invokeGetUserRelativeDetailsByRelativeId(relativeId)
            if (relativeDetails != null && relativeId == relativeDetails.relativeID) {

                //Switch Profile Details
                preferenceUtils.storePreference(
                    PreferenceConstants.PERSONID,
                    relativeDetails.relativeID
                )
                preferenceUtils.storePreference(
                    PreferenceConstants.RELATIONSHIPCODE,
                    relativeDetails.relationshipCode
                )
                preferenceUtils.storePreference(
                    PreferenceConstants.EMAIL,
                    relativeDetails.emailAddress
                )
                preferenceUtils.storePreference(
                    PreferenceConstants.PHONE,
                    relativeDetails.contactNo
                )
                preferenceUtils.storePreference(
                    PreferenceConstants.FIRSTNAME,
                    relativeDetails.firstName
                )
                preferenceUtils.storePreference(PreferenceConstants.GENDER, relativeDetails.gender)

                val launchIntent = Intent()
                launchIntent.component =
                    ComponentName(NavigationConstants.APPID, NavigationConstants.HOME)
                launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                launchIntent.putExtra(Constants.FROM, Constants.NOTIFICATION_ACTION)
                launchIntent.putExtra(Constants.DATE, intent.getStringExtra(Constants.DATE))
                context!!.startActivity(launchIntent)
            } else {
                Utilities.toastMessageLong(
                    context!!,
                    localResource.getString(R.string.ALREADY_DELETED_MEMBER_RELATED_TO_NOTIFICATION)
                )
                Utilities.printLogError("Relative Details not Exist for PersonID--->$relativeId")
            }
        }
    }

    fun routeToScheduleDetails(view: View, bundle: Bundle) = viewModelScope.launch {
        var isAdded = false
        withContext(Dispatchers.IO) {
            val medName = bundle.get(Constants.MEDICINE_NAME).toString().split(" ")[0]
            val medList = useCase.getOngoingMedicines()
            val distinctMedList = ArrayList<String>()
            for (i in medList.indices) {
                if (!distinctMedList.contains(medList[i].drug.name)) {
                    distinctMedList.add(medList[i].drug.name!!)
                }
            }
            for (j in distinctMedList.indices) {
                if (distinctMedList[j].equals(medName, ignoreCase = true)) {
                    isAdded = true
                    break
                }
            }
        }
        if (isAdded) {
            Utilities.toastMessageShort(
                context!!,
                localResource.getString(R.string.YOU_ARE_ALREADY_TAKING) + " " + bundle.get(
                    Constants.MEDICINE_NAME
                )
            )
        } else {
            view.findNavController()
                .navigate(R.id.action_addMedicineFragment_to_scheduleMedicineFragment, bundle)
        }
    }

    fun getMedicineDetailsByMedicationId(medicationId: Int) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val medicine = useCase.getMedicineDetailsByMedicationId(medicationId)
            Utilities.printData("Medicine", medicine)
            val start =
                medicine.PrescribedDate!!.split("T".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            medicine.PrescribedDate = start[0]
            if (!Utilities.isNullOrEmpty(medicine.EndDate)) {
                val end = medicine.EndDate!!.split("T".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                medicine.EndDate = end[0]
            }
            medicineDetails.postValue(medicine)
        }
    }

    fun isUserLoggedIn(): Boolean {
        return preferenceUtils.getBooleanPreference(PreferenceConstants.IS_LOGIN, false)
    }

    private fun updateNotificationAlert(medicationID: String, setAlert: Boolean) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                useCase.invokeUpdateNotificationAlert(medicationID, setAlert)
            }
        }

    fun getOngoingMedicines(fragment: MyMedicationsFragment) {
        fragment.lifecycleScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                val list = useCase.getOngoingMedicines()
                Utilities.printData("OngoingMedicinesDBList", list)
                medicinesList.postValue(list)
            }
        }
    }

    fun getCompletedMedicines(fragment: MyMedicationsFragment) {
        fragment.lifecycleScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                val list = useCase.getCompletedMedicines()
                Utilities.printData("CompletedMedicinesDBList", list)
                medicinesList.postValue(list)
            }
        }
    }

    fun getAllMyMedicines(fragment: MyMedicationsFragment) {
        fragment.lifecycleScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                val list = useCase.getAllMyMedicines()
                Utilities.printData("AllMyMedicinesDBList", list)
                medicinesList.postValue(list)
            }
        }
    }

    fun getPastMedicines(fragment: MyMedicationsFragment) {
        fragment.lifecycleScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                val list = useCase.getPastMedicines()
                Utilities.printData("DBList", list)
                medicinesList.postValue(list)
            }
        }
    }

    /*    fun getOngoingMedicines() = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val list = useCase.getOngoingMedicines()
                Utilities.printData("DBList",list)
                medicinesList.postValue(list)
            }
        }

        fun getCompletedMedicines() = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val list = useCase.getCompletedMedicines()
                Utilities.printData("DBList",list)
                medicinesList.postValue(useCase.getCompletedMedicines())
            }
        }

        fun getAllMyMedicines() = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val list = useCase.getAllMyMedicines()
                Utilities.printData("DBList",list)
                medicinesList.postValue(useCase.getAllMyMedicines())
            }
        }

        fun getPastMedicines() = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val list = useCase.getPastMedicines()
                Utilities.printData("DBList",list)
                medicinesList.postValue(useCase.getPastMedicines())
            }
        }*/

    private fun cancelNotification(manager: NotificationManager, notificationId: Int) {
        manager.cancel(notificationId)
    }

    private fun closeNotificationDrawer(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            context!!.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
        }
    }

    /////////////////////////////

    fun getMedDetailsByMedicationIdApi(medicationID: String) {
        val jsonObjectJSONData = JSONObject()
        jsonObjectJSONData.put("MedicationID", medicationID)
        jsonObjectJSONData.put("Message", "Get medicine....")
        getResponseFromServer(jsonObjectJSONData, "1")
    }

    private fun getMedDetailsByMedicationIdApiResp(response: String) {
        if (!Utilities.isNullOrEmpty(response)) {
            val gson = GsonBuilder().setPrettyPrinting().create()
            val jsonData: GetMedicineModel.GetMedicineResponse = gson.fromJson(
                response,
                object : TypeToken<GetMedicineModel.GetMedicineResponse?>() {}.type
            )
            val medication = jsonData.medication
            Utilities.printLogError("MedicationID--->${medication.medicationId}")
            try {
                val notificationData = MedicationSingleton.getInstance()!!.getNotificationIntent()
                val notificationID = notificationData.getIntExtra(Constants.NOTIFICATION_ID, -1)
                val medName = notificationData.getStringExtra(Constants.MEDICINE_NAME)
                val scheduleTime =
                    notificationData.getStringExtra(Constants.SCHEDULE_TIME)?.substring(0, 5)
                val time = notificationData.getStringExtra(Constants.TIME)

                if (!Utilities.isNullOrEmptyOrZero(medication.medicationId.toString())) {
                    var isExist = false
                    for (i in medication.scheduleList) {
                        Utilities.printLogError(
                            "scheduleTimeResp--->${
                                i.scheduleTime!!.substring(
                                    0,
                                    5
                                )
                            }"
                        )
                        Utilities.printLogError("scheduleTimeNoti--->$scheduleTime")
                        if (i.scheduleTime!!.substring(0, 5) == scheduleTime) {
                            isExist = true
                            break
                        }
                    }

                    if (isExist) {
                        getMedInTakeFromNotificationByScheduleIDApi()
                    } else {
                        closeNotificationDrawer(context!!)
                        cancelNotification(notificationManager, notificationID)
                        Utilities.toastMessageShort(
                            context!!,
                            localResource.getString(R.string.YOU_HAVE_REMOVED_OR_RESCHEDULED_TIME) + " $time" + " " + localResource.getString(
                                R.string.FOR
                            ) + " " + medName
                        )
                    }

                } else {
                    closeNotificationDrawer(context!!)
                    cancelNotification(notificationManager, notificationID)
                    Utilities.toastMessageShort(
                        context!!,
                        localResource.getString(R.string.YOU_HAVE_ALREADY_DELETED) + " $medName"
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            Utilities.printLogError("Error while fetching Response from server")
        }
    }

    private fun getMedInTakeFromNotificationByScheduleIDApi() {
        val notificationData = MedicationSingleton.getInstance()!!.getNotificationIntent()
        val scheduleID = notificationData.getStringExtra(Constants.SERVER_SCHEDULE_ID)!!
        val recordDate = notificationData.getStringExtra(Constants.DATE)!!
        val jsonObjectJSONData = JSONObject()
        jsonObjectJSONData.put("ScheduleID", scheduleID)
        jsonObjectJSONData.put("RecordDate", recordDate)
        getResponseFromServer(jsonObjectJSONData, "2")
    }

    private fun getMedInTakeFromNotificationByScheduleIDResp(response: String) {
        if (!Utilities.isNullOrEmpty(response)) {
            val gson = GsonBuilder().setPrettyPrinting().create()
            val jsonData: MedicineInTakeModel.MedicineDetailsResponse = gson.fromJson(
                response,
                object : TypeToken<MedicineInTakeModel.MedicineDetailsResponse?>() {}.type
            )
            val medicationInTakes = jsonData.medicationInTakes
            val medicationInTakeID = if (medicationInTakes.isNotEmpty()) {
                medicationInTakes[0].medicineInTakeId
            } else {
                0
            }
            Utilities.printLogError("MedicationInTakeID--->$medicationInTakeID")
            addMedIntakeApi(medicationInTakeID)
        } else {
            Utilities.printLogError("Error while fetching Response from server")
        }
    }

    private fun addMedIntakeApi(medicationInTakeID: Int) {
        try {
            val notificationData = MedicationSingleton.getInstance()!!.getNotificationIntent()
            val medicationInTakes = JSONArray()
            val intake = JSONObject()
            intake.put(
                "MedicationID",
                notificationData.getStringExtra(Constants.MEDICATION_ID)!!.toInt()
            )
            intake.put("ID", medicationInTakeID)
            intake.put(
                "ScheduleID",
                notificationData.getStringExtra(Constants.SERVER_SCHEDULE_ID)!!.toInt()
            )
            intake.put("Status", notificationData.getStringExtra(Constants.NOTIFICATION_ACTION)!!)
            intake.put("FeelStatus", "")
            intake.put("MedDate", notificationData.getStringExtra(Constants.DATE)!!)
            intake.put("MedTime", notificationData.getStringExtra(Constants.SCHEDULE_TIME)!!)
            intake.put("Dosage", notificationData.getStringExtra(Constants.DOSAGE)!!)
            intake.put("Description", "")
            intake.put("CreatedDate", notificationData.getStringExtra(Constants.DATE)!!)
            medicationInTakes.put(intake)

            val jsonObjectJSONData = JSONObject()
            jsonObjectJSONData.put("MedicationInTake", medicationInTakes)
            getResponseFromServer(jsonObjectJSONData, "3")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun addMedIntakeResp(response: String) {
        if (!Utilities.isNullOrEmpty(response)) {
            val jsonObjectResponse = JSONObject(response)
            val intakeArray = jsonObjectResponse.get("MedicationInTake")
            Utilities.printLogError("MedicationInTake--->$response")
            if (!Utilities.isNullOrEmpty(intakeArray.toString())) {
                val gson = GsonBuilder().setPrettyPrinting().create()
                val jsonData: AddInTakeModel.AddInTakeResponse = gson.fromJson(
                    response,
                    object : TypeToken<AddInTakeModel.AddInTakeResponse?>() {}.type
                )
                val medicationInTake = jsonData.medicationInTake
                Utilities.printLogError("MedicationInTake--->$medicationInTake")

                if (medicationInTake.isNotEmpty()) {
                    val medicineDetailsList =
                        MedicationSingleton.getInstance()!!.geMedicineListByDay()
                    if (medicineDetailsList.size > 0) {
                        for (inTakeObject in medicationInTake) {
                            for (medicineDetails in medicineDetailsList) {
                                if (inTakeObject.medicationID == medicineDetails.medicationId) {
                                    for (scheduleDetails in medicineDetails.medicationScheduleList) {
                                        if (inTakeObject.scheduleID == scheduleDetails.scheduleId) {
                                            scheduleDetails.status = inTakeObject.status
                                            scheduleDetails.medicationInTakeID =
                                                inTakeObject.medicationInTakeID
                                            Utilities.printLogError("Intake Updated.....")
                                        }
                                    }
                                }
                            }
                        }
                    }
                    updateAndCancelNotification(medicationInTake[0].status)
                    //meddashboardFragment!!.updateMedicatinesList()
                }
            }
        } else {
            Utilities.printLogError("Error while fetching Response from server")
        }
    }

    private fun getResponseFromServer(jsonRequest: JSONObject, from: String): String {
        var jsonData = JSONObject()
        try {
            val jsonObject = JSONObject()

            val jsonObjectHeader = JSONObject()
            jsonObjectHeader.put("RequestID", Configuration.RequestID)
            jsonObjectHeader.put("DateTime", DateHelper.currentDateAsStringddMMMyyyy)
            jsonObjectHeader.put("ApplicationCode", Configuration.ApplicationCode)
            jsonObjectHeader.put(
                "AuthTicket",
                preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
            )
            jsonObjectHeader.put("PartnerCode", Constants.PartnerCode)
            jsonObjectHeader.put("EntityType", Configuration.EntityType)
            jsonObjectHeader.put("HandShake", Configuration.Handshake)
            jsonObjectHeader.put("UTMSource", Configuration.UTMSource)
            jsonObjectHeader.put("UTMMedium", Configuration.UTMMedium)
            jsonObjectHeader.put("LanguageCode", Configuration.LanguageCode)
            jsonObjectHeader.put("EntityID", Configuration.EntityID)

            jsonObject.put("Header", jsonObjectHeader)
            jsonObject.put("JSONData", jsonRequest.toString())

            val logging = HttpLoggingInterceptor {
                Utilities.printLog("HttpLogging--> $it")
            }
            logging.level = HttpLoggingInterceptor.Level.BODY
            // Create Retrofit
            val retrofit = Retrofit.Builder()
                .client(
                    OkHttpClient.Builder()
                        .retryOnConnectionFailure(true)
                        .protocols(Arrays.asList(Protocol.HTTP_1_1))
                        .addInterceptor(logging)
                        .connectTimeout(3, TimeUnit.MINUTES)
                        .writeTimeout(3, TimeUnit.MINUTES)
                        .readTimeout(3, TimeUnit.MINUTES)
                        .build()
                )
                .baseUrl(Constants.strAPIUrl)
                .addConverterFactory(GsonConverterFactory.create())
                //.addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
                //.addConverterFactory(ScalarsConverterFactory.create())
                //.addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()

            // Create Service
            val service = retrofit.create(MedNotificationApiService::class.java)
            //Utilities.printLogError("Request--->$jsonObject")
            val body = getEncryptedRequestBody(jsonObject.toString())

            CoroutineScope(Dispatchers.Main).launch {
                var response: Response<ResponseBody>? = null
                when (from) {
                    "1" -> response = service.getMedicineDetailsApi(body)
                    "2" -> response = service.getMedicationInTakeApi(body)
                    "3" -> response = service.addIntakeApi(body)
                }
                //val response = service.getMedicineDetailsApi(body)
                withContext(Dispatchers.IO) {
                    if (response!!.isSuccessful) {
                        val resp = getResponse(response.body()!!.string())
                        Utilities.printLogError("from--->$from")
                        Utilities.printLogError("Resp--->$resp")
                        val jsonObjectResponse = JSONObject(resp)
                        if (!jsonObjectResponse.isNull("JSONData")) {
                            jsonData = JSONObject(jsonObjectResponse.optString("JSONData"))

                            when (from) {
                                "1" -> {
                                    getMedDetailsByMedicationIdApiResp(
                                        jsonObjectResponse.optString(
                                            "JSONData"
                                        )
                                    )
                                }

                                "2" -> {
                                    getMedInTakeFromNotificationByScheduleIDResp(
                                        jsonObjectResponse.optString(
                                            "JSONData"
                                        )
                                    )
                                }

                                "3" -> {
                                    addMedIntakeResp(jsonObjectResponse.optString("JSONData"))
                                }
                            }
                        }
                    } else {
                        Utilities.printLogError("RETROFIT_ERROR--->${response.code()}")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jsonData.toString()
    }

    private fun getEncryptedRequestBody(request: String): RequestBody {
        val encryptedReq = EncryptionUtility.encrypt(request)
        return RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), encryptedReq)
        //return encryptedReq.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }

    private fun getResponse(response: String): String {
        val decrypted = EncryptionUtility.decrypt(response)
        val decryptedResponse: String = decrypted
            .replace("\\r\\n", "")
            .replace("\\\"", "\"")
            .replace("\\\\\"", "\"")
            .replace("\"{", "{")
            .replace("\"[", "[")
            .replace("}\"", "}")
            .replace("]\"", "]")
        return decryptedResponse
    }

    /*    fun getMedicineDetailsByMedicationIdApi(medicationID: String, from: String) = viewModelScope.launch(Dispatchers.Main) {

            val requestData = GetMedicineModel(Gson().toJson(GetMedicineModel.JSONDataRequest(
                medicationID = medicationID), GetMedicineModel.JSONDataRequest::class.java), authToken)

            //_progressBar.value = Event("Getting Medicine Details...")
            _getMedicine.removeSource(getMedicineSource)
            withContext(Dispatchers.Main) {
                getMedicineSource = useCase.invokeGetMedicine(requestData)
            }
            _getMedicine.addSource(getMedicineSource) {
                _getMedicine.value = it.data
                //updateBroadcastReceiver()

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    val medication = it.data!!.medication
                    val medicationId = it.data!!.medication.MedicationID
                    Utilities.printLog("Medication--->${it.data!!.medication}")
                    if (from.equals(Constants.NOTIFICATION, ignoreCase = true)) {
                        try {
                            val notificationData = MedicationSingleton.getInstance()!!.getNotificationIntent()
                            val notificationID = notificationData.getIntExtra(Constants.NOTIFICATION_ID, -1)
                            val medName = notificationData.getStringExtra(Constants.MEDICINE_NAME)
                            val scheduleTime = notificationData.getStringExtra(Constants.SCHEDULE_TIME)?.substring(0, 5)
                            val time = notificationData.getStringExtra(Constants.TIME)

                            if (!Utilities.isNullOrEmptyOrZero(medicationId.toString())) {
                                var isExist = false
                                for (i in medication.scheduleList) {
                                    if (i.scheduleTime!!.substring(0, 5) == scheduleTime) {
                                        isExist = true
                                        break
                                    }
                                }

                                if (isExist) {
                                    getMedicationInTakeFromNotificationByScheduleID(notificationData)
                                } else {
                                    closeNotificationDrawer(context!!)
                                    cancelNotification(notificationManager, notificationID)
                                    Utilities.toastMessageShort(context!!, "You have Removed or Rescheduled Time $time for $medName")
                                }

                            } else {
                                closeNotificationDrawer(context!!)
                                cancelNotification(notificationManager, notificationID)
                                Utilities.toastMessageShort(context!!, "You have Already Deleted $medName")
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.errorNumber.equals("1100014", true)) {
                        _sessionError.value = Event(true)
                    } else {
                        toastMessage(it.errorMessage)
                    }
                }
            }
        }

        private fun getMedicationInTakeFromNotificationByScheduleID(notificationData: Intent) = viewModelScope.launch(Dispatchers.Main) {

            val scheduleID = notificationData.getStringExtra(Constants.SERVER_SCHEDULE_ID)!!
            val date = notificationData.getStringExtra(Constants.DATE)!!
            val requestData = MedicineInTakeModel(Gson().toJson(MedicineInTakeModel.JSONDataRequest(
                scheduleID = scheduleID.toInt(),
                recordDate = date), MedicineInTakeModel.JSONDataRequest::class.java), authToken)

            _progressBar.value = Event("Loading...")
            _listMedicationInTake.removeSource(listMedicationInTakeSource)
            withContext(Dispatchers.IO) {
                listMedicationInTakeSource = useCase.invokeGetMedicationInTakeByScheduleID(requestData, scheduleID.toInt())
            }
            _listMedicationInTake.addSource(listMedicationInTakeSource) {
                _listMedicationInTake.value = it.data
                //updateBroadcastReceiver()

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    val medicationInTakes = it.data!!.medicationInTakes
                    val medicationInTakeID: Int
                    medicationInTakeID = if (medicationInTakes.isNotEmpty()) {
                        medicationInTakes[0].medicineInTakeId
                    } else {
                        0
                    }
                    val medicationInTakeList: MutableList<AddInTakeModel.MedicationInTake> = mutableListOf()
                    val intake = AddInTakeModel.MedicationInTake()
                    intake.medicationID = notificationData.getStringExtra(Constants.MEDICATION_ID)!!.toString().toInt()
                    intake.medicationInTakeID = medicationInTakeID
                    intake.scheduleID = notificationData.getStringExtra(Constants.SERVER_SCHEDULE_ID)!!.toString().toInt()
                    intake.status = notificationData.getStringExtra(Constants.NOTIFICATION_ACTION)!!.toString()
                    intake.medDate = notificationData.getStringExtra(Constants.DATE)!!
                    intake.medTime = notificationData.getStringExtra(Constants.SCHEDULE_TIME)!!
                    intake.dosage = notificationData.getStringExtra(Constants.DOSAGE)!!
                    intake.createdDate = notificationData.getStringExtra(Constants.DATE)!!
                    medicationInTakeList.add(intake)
                    //callAddMedicineIntakeApi(medicationInTakeList)
                }

                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.errorNumber.equals("1100014", true)) {
                        _sessionError.value = Event(true)
                    } else {
                        toastMessage(it.errorMessage)
                    }
                }
            }
        }*/


}
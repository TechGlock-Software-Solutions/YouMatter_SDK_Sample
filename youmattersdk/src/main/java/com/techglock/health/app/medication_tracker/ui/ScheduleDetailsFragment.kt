package com.techglock.health.app.medication_tracker.ui

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.*
import com.techglock.health.app.databinding.FragmentScheduleDetailsBinding
import com.techglock.health.app.medication_tracker.adapter.MealTimeAdapter
import com.techglock.health.app.medication_tracker.adapter.MedFreuencyAdapter
import com.techglock.health.app.medication_tracker.adapter.MedScheduleTimeAdapter
import com.techglock.health.app.medication_tracker.model.FrequencyModel
import com.techglock.health.app.medication_tracker.model.InstructionModel
import com.techglock.health.app.medication_tracker.model.TimeModel
import com.techglock.health.app.medication_tracker.view.CounterView
import com.techglock.health.app.medication_tracker.viewmodel.MedicineTrackerViewModel
import com.techglock.health.app.model.entity.MedicationEntity
import com.techglock.health.app.model.medication.MedicationModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ScheduleDetailsFragment : BaseFragment(), CounterView.OnCounterSubmitListener,
    MedFreuencyAdapter.OnMedFrequencyListener, MealTimeAdapter.OnInstructionClickListener,
    TimePickerDialog.OnTimeSetListener, DefaultNotificationDialog.OnDialogValueListener {

    private lateinit var binding: FragmentScheduleDetailsBinding

    private var medFrequencyAdapter: MedFreuencyAdapter? = null
    private var medScheduleTimeAdapter: MedScheduleTimeAdapter? = null
    private var mealTimeAdapter: MealTimeAdapter? = null

    var medicine = MedicationEntity.Medication()
    var tab = 0
    private var from = ""
    private var medicationId = 0
    private var drugId = 0
    private var medicineName = ""
    private var drugTypeCode = ""
    private var drugType = ""
    private var medFreqPos = 0
    private var frequency = ""
    private var serverStartDate: String = DateHelper.currentDateAsStringyyyyMMdd
    private var serverEndDate = ""
    private var medDoseCount = 1.0
    private var intakeInstruction = ""
    private var hour = 0
    private var minute = 0
    private var scheduleTimePosition = 0
    private val calendar = Calendar.getInstance()
    private val df1 = SimpleDateFormat(DateHelper.SERVER_DATE_YYYYMMDD, Locale.ENGLISH)

    private var scheduleList: MutableList<TimeModel> = mutableListOf()
    private var removedScheduleList: MutableList<TimeModel> = mutableListOf()

    private val viewModel: MedicineTrackerViewModel by lazy {
        ViewModelProvider(this)[MedicineTrackerViewModel::class.java]
    }

    private val instructionList: ArrayList<InstructionModel> = ArrayList()

    override fun getViewModel(): BaseViewModel = viewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val medi = it.getString("medicine", "")
            from = it.getString(Constants.FROM, Constants.ADD)
            medicationId = it.getInt(Constants.MEDICATION_ID, 0)
            drugId = it.getInt(Constants.Drug_ID, 0)
            medicineName = it.getString(Constants.MEDICINE_NAME, "")!!
            drugTypeCode = it.getString(Constants.DRUG_TYPE_CODE, "")!!
            tab = it.getInt(Constants.TAB, 0)
            medicine = Gson().fromJson(medi, MedicationEntity.Medication::class.java)
            Utilities.printData("Data", medicine)
            Utilities.printLogError("from,tab,MedicationID,DrugID,MedicineName,DrugTypeCode--->$from,$tab,$medicationId,$drugId,$medicineName,$drugTypeCode")
        }

        // callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                performBackClick()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScheduleDetailsBinding.inflate(inflater, container, false)
        try {
            initialise()
            setData()
            setClickable()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    private fun initialise() {
        instructionList.clear()
        instructionList.addAll(viewModel.medicationTrackerHelper.getMedInstructionList())

        binding.counterMedDose.setAsNonDecimal(false)
        binding.counterMedDose.setValue("1")
        binding.counterMedDose.setUnit(resources.getString(R.string.DOSE))
        binding.counterMedDose.setInputRange(0.5, 6.0)
        binding.counterMedDose.setValueToAddSubctract(0.5)
        binding.counterMedDose.setOnCounterSubmitListener(this)

        binding.edtStartDate.setText(DateHelper.getDateTimeAs_ddMMMyyyyNew(serverStartDate))

        medFrequencyAdapter = MedFreuencyAdapter(
            requireContext(),
            viewModel.medicationTrackerHelper.getFrequencyList(),
            this
        )
        binding.rvMedFrequency.layoutManager = GridLayoutManager(context, 2)
        binding.rvMedFrequency.adapter = medFrequencyAdapter

        medScheduleTimeAdapter = MedScheduleTimeAdapter(requireContext(), this)
        binding.rvMedTime.layoutManager = GridLayoutManager(context, 3)
        binding.rvMedTime.adapter = medScheduleTimeAdapter

        mealTimeAdapter = MealTimeAdapter(requireContext(), instructionList, this)
        binding.rvMealTime.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvMealTime.adapter = mealTimeAdapter
    }

    @SuppressLint("SetTextI18n")
    fun setData() {
        val localResource = LocaleHelper.getLocalizedResources(
            requireContext(),
            Locale(LocaleHelper.getLanguage(requireContext()))
        )!!
        if (Utilities.isNullOrEmptyOrZero(medicationId.toString())) {
            // Medicine Details
            drugType = "  (" + localResource.getString(
                viewModel.medicationTrackerHelper.getMedTypeByCode(drugTypeCode)
            ) + ")"
            Utilities.printLog("drugTypeCode--->$drugTypeCode")
            medScheduleTimeAdapter!!.addMedTime(0, TimeModel(0, "", "", 0, 0))
        } else {
            Utilities.printLogError("Existing--->$medicationId")
            val medicineDetails = medicine
            drugTypeCode = if (Utilities.isNullOrEmpty(medicineDetails.DrugTypeCode)) {
                "TAB"
            } else {
                medicineDetails.DrugTypeCode!!
            }
            drugType = "  (" + localResource.getString(
                viewModel.medicationTrackerHelper.getMedTypeByCode(drugTypeCode)
            ) + ")"
            medicineName = medicineDetails.drug.name!!
            if (!Utilities.isNullOrEmpty(medicineDetails.drug.strength)) {
                medicineName = medicineName + " - " + medicineDetails.drug.strength
            }

            // Medication Period
            if (medicineDetails.medicationPeriod.equals(Constants.FOR_X_DAYS, ignoreCase = true)) {
                binding.layoutDates.visibility = View.VISIBLE
                binding.edtDuration.setText(medicineDetails.durationInDays)
                medFrequencyAdapter!!.updateSelectedPos(1)
            } else {
                binding.layoutDates.visibility = View.GONE
                medFrequencyAdapter!!.updateSelectedPos(0)
            }

            // Star Date and End Date
            serverStartDate = medicineDetails.PrescribedDate!!
            binding.edtStartDate.setText(DateHelper.getDateTimeAs_ddMMMyyyyNew(serverStartDate))
            if (!Utilities.isNullOrEmpty(medicineDetails.EndDate)) {
                serverEndDate = medicineDetails.EndDate!!
                binding.edtEndDate.setText(DateHelper.getDateTimeAs_ddMMMyyyyNew(serverEndDate))
            }

            // Dosage
            if (!medicineDetails.scheduleList.isNullOrEmpty()) {
                binding.counterMedDose.setValue(medicineDetails.scheduleList[0].dosage)
            } else {
                binding.counterMedDose.setValue("1.0")
            }

            // Schedule Time List
            if (!medicineDetails.scheduleList.isNullOrEmpty()) {
                medScheduleTimeAdapter!!.addMedTime(0, TimeModel(0, "", "", 0, 0))
                for (i in medicineDetails.scheduleList.indices) {
                    val medSchedule = medicineDetails.scheduleList[i]
                    val scheduleId = medSchedule.scheduleID
                    val scheduleTime = medSchedule.scheduleTime
                    val split = scheduleTime!!.split(":").toTypedArray()
                    val hours = split[0].toInt()
                    val minutes = split[1].toInt()
                    Utilities.printLog((i + 1).toString() + "---" + scheduleTime + " => " + +hours + " : " + minutes)
                    medScheduleTimeAdapter!!.addMedTime(
                        i + 1,
                        TimeModel(
                            scheduleId,
                            DateHelper.getTimeIn12HrFormatAmOrPm(scheduleTime),
                            scheduleTime,
                            hours,
                            minutes
                        )
                    )
                }
            } else {
                Utilities.toastMessageLong(
                    requireContext(),
                    resources.getString(R.string.ERROR_SCHEDULE_TIME_NOT_FOUND_PLEASE_SELECT_TO_UPDATE_MEDICINE_DETAILS)
                )
                medScheduleTimeAdapter!!.addMedTime(0, TimeModel(0, "", "", 0, 0))
            }

            // Intake Instructions
            for (i in instructionList.indices) {
                if (instructionList[i].code.equals(medicineDetails.comments, ignoreCase = true)) {
                    mealTimeAdapter!!.updateSelectedPos(i)
                    break
                }
            }

            // Notes
            if (!Utilities.isNullOrEmpty(medicineDetails.notes)) {
                binding.edtNotes.setText(medicineDetails.notes)
            }

            // Alert Notification
            binding.swAlert.isChecked = medicineDetails.notification!!.setAlert!!
        }
    }

    private fun setClickable() {

        binding.edtDuration.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                try {
                    if (!Utilities.isNullOrEmptyOrZero(editable.toString())) {
                        val endDate: String = DateHelper.getDateBeforeOrAfterGivenDays(
                            serverStartDate,
                            editable.toString().toInt()
                        )
                        serverEndDate = endDate
                        binding.edtEndDate.setText(
                            DateHelper.getDateTimeAs_ddMMMyyyyNew(
                                serverEndDate
                            )
                        )
                    } else {
                        binding.edtEndDate.setText("")
                        serverEndDate = ""
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

        binding.edtStartDate.setOnClickListener {
            showStartDatePicker()
        }

        binding.edtEndDate.setOnClickListener {
            showEndDatePicker()
        }

        binding.edtStartDate.setOnClickListener {
            showStartDatePicker()
        }

        binding.edtEndDate.setOnClickListener {
            showEndDatePicker()
        }

        /*        binding.swAlert.setOnClickListener {
                    val isChecked = binding.swAlert.isChecked
                    val td: String = if (isChecked) {
                        " " + resources.getString(R.string.ENABLE) + " "
                    } else {
                        " " + resources.getString(R.string.DISABLE) + " "
                    }
                    val rightBtnTxt: String = if (isChecked) {
                        " " + resources.getString(R.string.ENABLE)
                    } else {
                        " " + resources.getString(R.string.DISABLE)
                    }
                    val hasErrorBtn: Boolean = !isChecked
                    showDialog(
                        listener = this,
                        title = td + resources.getString(R.string.NOTIFICATION_ALERT),
                        message = resources.getString(R.string.DO_YOU_WANT_TO) + td
                                + resources.getString(R.string.NOTIFICATION_ALERT) + " "
                                + resources.getString(R.string.FOR) + " " + medicineName,
                        leftText = resources.getString(R.string.CANCEL),
                        rightText = rightBtnTxt,
                        showLeftBtn = true,
                        hasErrorBtn = hasErrorBtn
                    )
                }*/

        binding.swAlert.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.txtChecked.text = resources.getString(R.string.YES)
            } else {
                binding.txtChecked.text = resources.getString(R.string.NO)
            }
        }

        binding.btnSchedule.setOnClickListener {
            if (NetworkUtility.isOnline(context)) {
                validateAndSchedule()
            } else {
                Utilities.toastMessageShort(
                    context,
                    resources.getString(R.string.NO_INTERNET_CONNECTION)
                )
            }
        }

        viewModel.saveMedicine.observe(viewLifecycleOwner, {})
        viewModel.updateMedicine.observe(viewLifecycleOwner, {})
    }

    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
        if (isButtonLeft) {
            binding.swAlert.isChecked = !binding.swAlert.isChecked
        }
    }

    private fun showStartDatePicker() {
        try {
            val cal = Calendar.getInstance()
            if (!Utilities.isNullOrEmpty(serverStartDate)) {
                cal.time = df1.parse(serverStartDate)!!
            }
            DialogHelper().showDatePickerDialog(resources.getString(R.string.START_DATE),
                requireContext(),
                cal,
                Calendar.getInstance(),
                null,
                object : DialogHelper.DateListener {
                    override fun onDateSet(
                        date: String,
                        year: String,
                        month: String,
                        dayOfMonth: String
                    ) {
                        val selectedDate = DateHelper.convertDateSourceToDestination(
                            date,
                            DateHelper.DISPLAY_DATE_DDMMMYYYY,
                            DateHelper.SERVER_DATE_YYYYMMDD
                        )
                        Utilities.printLogError("SelectedStartDate--->$selectedDate")
                        if (!Utilities.isNullOrEmpty(selectedDate)) {
                            if (!Utilities.isNullOrEmpty(serverEndDate)) {
                                val startDate = DateHelper.getDate(
                                    selectedDate,
                                    DateHelper.SERVER_DATE_YYYYMMDD
                                )
                                val endDate = DateHelper.getDate(
                                    serverEndDate,
                                    DateHelper.SERVER_DATE_YYYYMMDD
                                )
                                if (startDate == endDate || startDate.before(endDate)) {
                                    serverStartDate = selectedDate
                                    val date = DateHelper.getDateTimeAs_ddMMMyyyyNew(selectedDate)
                                    binding.edtStartDate.setText(date)
                                } else {
                                    Utilities.toastMessageShort(
                                        context,
                                        resources.getString(R.string.ERROR_START_DATE_MUST_BE_LESS_THAN_OR_EQUAL_TO_END_DATE)
                                    )
                                }
                            } else {
                                serverStartDate = selectedDate
                                binding.edtStartDate.setText(
                                    DateHelper.getDateTimeAs_ddMMMyyyyNew(
                                        selectedDate
                                    )
                                )
                            }

                            if (medFreqPos == 1 && !Utilities.isNullOrEmptyOrZero(binding.edtDuration.text.toString())) {
                                val endDate = DateHelper.getDateBeforeOrAfterGivenDays(
                                    serverStartDate,
                                    binding.edtDuration.text.toString().toInt()
                                )
                                serverEndDate = endDate
                                binding.edtEndDate.setText(
                                    DateHelper.getDateTimeAs_ddMMMyyyyNew(
                                        endDate
                                    )
                                )
                            }
                        }
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showEndDatePicker() {
        try {
            val cal = Calendar.getInstance()
            if (!Utilities.isNullOrEmpty(serverEndDate)) {
                cal.time = df1.parse(serverEndDate)!!
            }
            DialogHelper().showDatePickerDialog(resources.getString(R.string.END_DATE),
                requireContext(),
                cal,
                Calendar.getInstance(),
                null,
                object : DialogHelper.DateListener {
                    @SuppressLint("SetTextI18n")
                    override fun onDateSet(
                        date: String,
                        year: String,
                        month: String,
                        dayOfMonth: String
                    ) {
                        val selectedDate = DateHelper.convertDateSourceToDestination(
                            date,
                            DateHelper.DISPLAY_DATE_DDMMMYYYY,
                            DateHelper.SERVER_DATE_YYYYMMDD
                        )
                        Utilities.printLogError("SelectedEndDate--->$selectedDate")
                        if (!Utilities.isNullOrEmpty(selectedDate)) {
                            val startDate =
                                DateHelper.getDate(serverStartDate, DateHelper.SERVER_DATE_YYYYMMDD)
                            val endDate =
                                DateHelper.getDate(selectedDate, DateHelper.SERVER_DATE_YYYYMMDD)
                            if (endDate == startDate || endDate.after(startDate)) {
                                serverEndDate = selectedDate
                                binding.edtDuration.setText(
                                    (DateHelper.getDateDifference(
                                        serverStartDate,
                                        selectedDate
                                    ) + 1).toString()
                                )
                            } else {
                                Utilities.toastMessageShort(
                                    requireContext(),
                                    resources.getString(R.string.ERROR_END_DATE_MUST_BE_GREATER_THAN_OR_EQUAL_TO_START_DATE)
                                )
                            }
                        }
                    }

                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun validateAndSchedule() {
        try {
            if (isValidTimeList()) {
                if (medFreqPos == 1 && Utilities.isNullOrEmptyOrZero(binding.edtDuration.text.toString())) {
                    Utilities.toastMessageShort(
                        context,
                        resources.getString(R.string.ERROR_PLEASE_ENTER_VALID_NUMBER_OF_DAYS)
                    )
                } else if (Utilities.isNullOrEmpty(intakeInstruction)) {
                    Utilities.toastMessageShort(
                        context,
                        resources.getString(R.string.ERROR_PLEASE_SELECT_INTAKE_INSTRUCTION)
                    )
                } else {
                    val medicine = MedicationModel.Medication()
                    medicine.drug.drugId = drugId
                    medicine.drug.name = medicineName
                    medicine.drug.drugTypeCode = drugTypeCode
                    medicine.medicationID = medicationId
                    medicine.drugID = drugId
                    medicine.drugTypeCode = drugTypeCode
                    medicine.prescribedDate = serverStartDate
                    medicine.endDate = serverEndDate
                    medicine.medicationPeriod = frequency
                    if (!Utilities.isNullOrEmptyOrZero(binding.edtDuration.text.toString())) {
                        medicine.durationInDays = binding.edtDuration.text.toString()
                    }
                    medicine.comments = intakeInstruction
                    medicine.notes = binding.edtNotes.text.toString()
                    medicine.notification.setAlert =
                        if (binding.swAlert.isChecked) Constants.TRUE.lowercase(Locale.ENGLISH)
                        else Constants.FALSE.lowercase(Locale.ENGLISH)

                    viewModel.callAddOrUpdateMedicineApi(
                        medicine,
                        getScheduleTimeList(),
                        removedScheduleList,
                        medDoseCount,
                        tab,
                        this
                    )
                }
            } else {
                Utilities.toastMessageShort(
                    context,
                    resources.getString(R.string.ERROR_PLEASE_SELECT_VALID_SCHEDULE_TIME)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onTimeSet(view: TimePicker?, hour: Int, minitue: Int) {
        Utilities.printLog("hour,minute=>$hour : $minitue")
        val time: String = DateHelper.getFormattedTime("" + hour, "" + minitue)
        medScheduleTimeAdapter!!.updateTime(
            scheduleTimePosition,
            TimeModel(0, DateHelper.getTimeIn12HrFormatAmOrPm(time), time, hour, minitue)
        )
    }

    override fun onCounterSubmit() {
        try {
            medDoseCount = binding.counterMedDose.value
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onMedFrequencySelection(medFreq: FrequencyModel, position: Int) {
        Utilities.printLog("Position,MedicationPeriod=>$position,$medFreq")
        Utilities.printData("FrequencyModel", medFreq)
        if (position == 0) {
            medFreqPos = 0
            binding.layoutDates.visibility = View.GONE
            frequency = medFreq.code
            binding.edtDuration.setText("")
            serverEndDate = ""
        } else {
            medFreqPos = 1
            binding.layoutDates.visibility = View.VISIBLE
            frequency = medFreq.code
        }
    }

    override fun onInstructionSelection(position: Int, instruction: InstructionModel) {
        intakeInstruction = instruction.code
        Utilities.printLog("Instruction=>$intakeInstruction")
    }

    fun showTimePicker(position: Int, h: Int, m: Int) {
        Utilities.printLog("$position => $h : $m")
        try {
            scheduleTimePosition = position
            hour = calendar.get(Calendar.HOUR_OF_DAY)
            minute = calendar.get(Calendar.MINUTE)
            val tpd = TimePickerDialog(context, R.style.TimePickerTheme, this, h, m, false)
            //tpd.getButton(1).setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            if (h == 0 && m == 0) {
                tpd.updateTime(hour, minute)
            }
            tpd.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun removeAndAddToRemovedScheduleList(position: Int, time: TimeModel, fromAdapter: Boolean) {
        if (time.scheduleId != 0) {
            Utilities.printLog("RemovedTime,ScheduleID=>" + time.displayTime + "," + time.scheduleId)
            removedScheduleList.add(time)
        }
        medScheduleTimeAdapter!!.removeMedTime(position)
        if (fromAdapter) {
            //binding.counterMedTime.setValue(java.lang.String.valueOf(medScheduleTimeAdapter!!.medScheduleTimeList.size))
        }
    }

    private fun isValidTimeList(): Boolean {
        var isValid = false
        scheduleList.clear()
        var timeModel: TimeModel
        for (i in 0 until medScheduleTimeAdapter!!.medScheduleTimeList.size) {
            if (i != 0) {
                timeModel = medScheduleTimeAdapter!!.medScheduleTimeList[i]
                isValid = if (!Utilities.isNullOrEmpty(timeModel.time) && !containsTimeModel(
                        scheduleList,
                        timeModel.time
                    )
                ) {
                    scheduleList.add(timeModel)
                    true
                } else {
                    false
                }
            }
        }
        return isValid
    }

    private fun getDisplayTime(time: String): String {
        return DateHelper.getTimeIn12HrFormatAmOrPm(time)
    }

    private fun containsTimeModel(list: List<TimeModel>, time: String): Boolean {
        for (`object` in list) {
            if (`object`.time.equals(time, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    private fun getScheduleTimeList(): List<TimeModel> {
        return scheduleList
    }

    private fun setDayMonthYearToCalender(server_date_YYYYMMDD: String, cal: Calendar): Calendar {
        val dob =
            server_date_YYYYMMDD.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val years = Integer.parseInt(dob[0])
        val months = Integer.parseInt(dob[1])
        val days = Integer.parseInt(dob[2])

        //val cal = Calendar.getInstance()
        cal[Calendar.YEAR] = years
        cal[Calendar.MONTH] = months - 1
        cal[Calendar.DAY_OF_MONTH] = days
        return cal
    }

    fun performBackClick() {
        Utilities.printLogError("from----->$$from")
        val bundle = Bundle()
        when (from) {
            Constants.ADD -> {
                bundle.putInt(Constants.MEDICATION_ID, medicationId)
                bundle.putInt(Constants.Drug_ID, drugId)
                bundle.putString(Constants.MEDICINE_NAME, medicineName)
                bundle.putString(Constants.DRUG_TYPE_CODE, drugTypeCode)
                findNavController().navigate(
                    R.id.action_scheduleMedicineFragment_to_addMedicineFragment,
                    bundle
                )
            }

            Constants.MEDICATION -> {
                bundle.putString(Constants.FROM, Constants.MEDICATION)
                bundle.putInt(Constants.TAB, tab)
                findNavController().navigate(
                    R.id.action_scheduleMedicineFragment_to_medicineHome,
                    bundle
                )
            }

            /*            from.equals("Dashboard", ignoreCase = true) -> {
                            bundle.putString(Constants.DATE, selectedDate)
                            findNavController().navigate(R.id.action_scheduleMedicineFragment_to_medicineDashboardFragment, bundle)
                        }*/
            else -> {
                bundle.putString(Constants.FROM, from)
                findNavController().navigate(
                    R.id.action_scheduleMedicineFragment_to_myMedicationsFragment,
                    bundle
                )
            }
        }
    }

}
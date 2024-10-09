package com.techglock.health.app.medication_tracker.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.recyclerview.widget.LinearLayoutManager
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.DialogUpdateStatusBinding
import com.techglock.health.app.medication_tracker.adapter.MedTimeStatusAdapter
import com.techglock.health.app.medication_tracker.viewmodel.MedicineTrackerViewModel
import com.techglock.health.app.model.medication.AddInTakeModel
import com.techglock.health.app.model.medication.MedicineListByDayModel


class DialogUpdateMedicineStatus(
    private val mcontext: Context?,
    medicineDetails: MedicineDetails,
    listener: OnUpdateClickListener,
    viewModel: MedicineTrackerViewModel,
    fragment: MedicineDashboardFragment
) : Dialog(mcontext!!) {

    private lateinit var binding: DialogUpdateStatusBinding

    private val medicationTrackerHelper = viewModel.medicationTrackerHelper
    private var medicineDetails = MedicineDetails()
    private var onUpdateClickListener: OnUpdateClickListener
    private var viewModel: MedicineTrackerViewModel
    private var fragment: MedicineDashboardFragment
    private var medTimeStatusAdapter: MedTimeStatusAdapter? = null
    private var animation: LayoutAnimationController? = null

    init {
        this.medicineDetails = medicineDetails
        this.onUpdateClickListener = listener
        this.viewModel = viewModel
        this.fragment = fragment
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //setContentView(R.layout.dialog_update_status)
        binding = DialogUpdateStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    @SuppressLint("SetTextI18n")
    override fun show() {
        super.show()
        try {
            binding.rvMedTimeStatus
            animation = AnimationUtils.loadLayoutAnimation(
                context,
                R.anim.layout_animation_slide_from_bottom
            )

            Utilities.printData("Medicine Notification Details", medicineDetails, true)
            if (!Utilities.isNullOrEmpty(medicineDetails.drupTypeCode)) {
                binding.imgMedType.setImageResource(
                    medicationTrackerHelper.getMedTypeImageByCode(
                        medicineDetails.drupTypeCode
                    )
                )
            }
            binding.txtMedicineName.text = medicineDetails.medName
            binding.txtDose.text =
                medicineDetails.medDose + " ${mcontext!!.resources.getString(R.string.DOSE)}"
            binding.txtMedTime.text =
                medicationTrackerHelper.getMedInstructionByCode(medicineDetails.medInstruction)
            /*            if (medicineDetails.setAlert.equals(Constants.TRUE, ignoreCase = true)) {
                            img_alert.setImageResource(R.drawable.img_alert_on)
                        } else {
                            img_alert.setImageResource(R.drawable.img_alert_off)
                        }*/

            val list = medicineDetails.medTimeStatusList.sortedBy { med -> med.scheduleTime }
                .toMutableList()

            medTimeStatusAdapter = MedTimeStatusAdapter(context, list, medicineDetails.selectedDate)
            binding.rvMedTimeStatus.layoutManager = LinearLayoutManager(context)
            binding.rvMedTimeStatus.layoutAnimation = animation
            binding.rvMedTimeStatus.adapter = medTimeStatusAdapter

            binding.imgClose.setOnClickListener {
                onUpdateClickListener.onUpdateClick()
            }

            binding.btnUpdate.setOnClickListener {
                val medScheduleList: MutableList<MedicineListByDayModel.MedicationSchedule> =
                    mutableListOf()
                var futureTimeCount = 0
                for (data in medTimeStatusAdapter!!.medTimeStatusList) {
                    if (!Utilities.isNullOrEmpty(data.status) && !data.status.equals(
                            "N",
                            ignoreCase = true
                        )
                    ) {
                        medScheduleList.add(data)
                    }
                    if (data.isFuture) {
                        futureTimeCount++
                    }
                }
                Utilities.printLogError("medScheduleList--->${medScheduleList.size}")
                when {
                    medScheduleList.size > 0 -> {
                        dismiss()
                        onUpdateClickListener.onUpdateClick()

                        val medicationInTakeList: MutableList<AddInTakeModel.MedicationInTake> =
                            mutableListOf()
                        for (item in medScheduleList) {
                            val intake = AddInTakeModel.MedicationInTake()
                            intake.medicationID = item.medicationID
                            if (!Utilities.isNullOrEmpty(item.medicationInTakeID.toString())) {
                                intake.medicationInTakeID = item.medicationInTakeID
                            } else {
                                intake.medicationInTakeID = 0
                            }
                            intake.scheduleID = item.scheduleId
                            intake.status = item.status
                            intake.medDate = medicineDetails.selectedDate
                            intake.medTime = item.scheduleTime
                            intake.dosage = item.dosage.toString()
                            intake.createdDate = medicineDetails.selectedDate
                            medicationInTakeList.add(intake)
                        }
                        viewModel.callAddMedicineIntakeApi(medicationInTakeList, fragment)
                    }

                    medTimeStatusAdapter!!.medTimeStatusList.size == futureTimeCount -> {
                        Utilities.toastMessageShort(
                            context,
                            context.resources.getString(R.string.ERROR_STATUS_FOR_FUTURE_SCHEDULE_TIMES_CAN_NOT_BE_UPDATED)
                        )
                    }

                    else -> {
                        Utilities.toastMessageShort(
                            context,
                            context.resources.getString(R.string.ERROR_SELECT_MEDICINE_STATUS)
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    class MedicineDetails {
        var selectedDate = ""
        var drupTypeCode = ""
        var medName = ""
        var medDose = ""
        var medInstruction = ""
        var setAlert = "true"
        var medTimeStatusList: MutableList<MedicineListByDayModel.MedicationSchedule> =
            mutableListOf()
    }

    fun updateMedicineDetails(status: String, medicationInTakeID: Int) {
        Utilities.printLog("UpdateIntake=>$status , $medicationInTakeID")
        medTimeStatusAdapter!!.updateTimeStatusList(status, medicationInTakeID)
    }

    interface OnUpdateClickListener {
        fun onUpdateClick()
    }

}
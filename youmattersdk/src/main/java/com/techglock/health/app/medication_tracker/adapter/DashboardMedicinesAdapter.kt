package com.techglock.health.app.medication_tracker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ItemMedDashboardBinding
import com.techglock.health.app.medication_tracker.ui.DialogUpdateMedicineStatus
import com.techglock.health.app.medication_tracker.ui.MedicineDashboardFragment
import com.techglock.health.app.medication_tracker.viewmodel.MedicineTrackerViewModel
import com.techglock.health.app.model.medication.MedicineListByDayModel.Medication

class DashboardMedicinesAdapter(
    val viewModel: MedicineTrackerViewModel,
    val context: Context,
    val fragment: MedicineDashboardFragment,
    private var selectedDate: String
) : RecyclerView.Adapter<DashboardMedicinesAdapter.DashboardMedicinesViewHolder>() {

    private val medicationTrackerHelper = viewModel.medicationTrackerHelper
    private var medicinesList: MutableList<Medication> = mutableListOf()
    private var currentDate: String = DateHelper.currentDateAsStringyyyyMMdd
    private var medTimeListAdapter: MedTimeListAdapter? = null

    override fun getItemCount(): Int = medicinesList.size

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): DashboardMedicinesViewHolder = DashboardMedicinesViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_med_dashboard, parent, false)
    )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DashboardMedicinesViewHolder, position: Int) {
        try {
            val medicine = medicinesList[position]

            if (!Utilities.isNullOrEmpty(medicine.drugTypeCode)) {
                holder.imgMedType.setImageResource(
                    medicationTrackerHelper.getMedTypeImageByCode(
                        medicine.drugTypeCode
                    )
                )
            }

            if (!Utilities.isNullOrEmpty(medicine.drug.strength)) {
                holder.txtMedicineName.text = medicine.drug.name + " - " + medicine.drug.strength
            } else {
                holder.txtMedicineName.text = medicine.drug.name
            }

            if (medicine.medicationScheduleList.isNotEmpty()) {
                holder.txtDose.text = medicine.medicationScheduleList[0].dosage.toString() + " ${
                    context.resources.getString(R.string.DOSE)
                }"
            }

            holder.txtMedInstruction.text =
                medicationTrackerHelper.getMedInstructionByCode(medicine.comments)

            var medDateDuration: String =
                DateHelper.getDateTimeAs_ddMMMyyyyNew(medicine.prescribedDate)
            medDateDuration = if (!Utilities.isNullOrEmpty(medicine.endDate.toString())) {
                context.resources.getString(R.string.FROM) + " " + medDateDuration + " " + context.resources.getString(
                    R.string.TO
                ) + " " + DateHelper.getDateTimeAs_ddMMMyyyyNew(medicine.endDate.toString())

            } else {
                if (DateHelper.getDateDifference(medicine.prescribedDate, currentDate) < 0) {
                    context.resources.getString(R.string.STARTING_ON) + "  $medDateDuration"
                } else {
                    context.resources.getString(R.string.STARTED) + "  $medDateDuration"
                }
            }

            val dateDiff: Long =
                DateHelper.getDateDifference(medicine.endDate.toString(), currentDate)
            if (!Utilities.isNullOrEmpty(medicine.endDate.toString()) && dateDiff > 0) {
                holder.imgAlert.visibility = View.GONE
                holder.imgCompleted.visibility = View.VISIBLE
            } else {
                holder.imgAlert.visibility = View.VISIBLE
                holder.imgCompleted.visibility = View.GONE
            }

            if (currentDate.equals(
                    medicine.prescribedDate, ignoreCase = true
                ) && currentDate.equals(medicine.endDate.toString(), ignoreCase = true)
            ) {
                holder.txtMedDateDuration.text =
                    context.resources.getString(R.string.FOR_TODAY_ONLY)
            } else if (medicine.prescribedDate.equals(
                    medicine.endDate.toString(), ignoreCase = true
                )
            ) {
                holder.txtMedDateDuration.text =
                    context.resources.getString(R.string.FOR) + "  " + DateHelper.getDateTimeAs_ddMMMyyyyNew(
                        medicine.prescribedDate
                    ) + "  " + context.resources.getString(R.string.ONLY)
            } else {
                holder.txtMedDateDuration.text = medDateDuration
            }

            if (medicine.notification!!.setAlert!!) {
                holder.imgAlert.setImageResource(R.drawable.img_alert_on)
                holder.imgAlert.tag = R.drawable.img_alert_on
            } else {
                holder.imgAlert.setImageResource(R.drawable.img_alert_off)
                holder.imgAlert.tag = R.drawable.img_alert_off
            }

            val list = medicine.medicationScheduleList.sortedBy { med -> med.scheduleTime }

            medTimeListAdapter = MedTimeListAdapter(context, list)
            holder.rvMedScheduleTime.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            holder.rvMedScheduleTime.adapter = medTimeListAdapter

            holder.imgAlert.setOnClickListener {
                if (getDrawableId(holder.imgAlert) == R.drawable.img_alert_on) {
                    medicine.notification!!.setAlert = false
                }
                if (getDrawableId(holder.imgAlert) == R.drawable.img_alert_off) {
                    medicine.notification!!.setAlert = true
                }
                fragment.showUpdateMedicineAlertDialog(medicine)
            }

            holder.imgCompleted.setOnClickListener {
                fragment.showRescheduleDialog(medicine)
            }

            holder.imgEditMed.setOnClickListener {
                if (medicine.medicationScheduleList.isNotEmpty()) {
                    val dialogData = DialogUpdateMedicineStatus.MedicineDetails()
                    dialogData.selectedDate = selectedDate
                    if (!Utilities.isNullOrEmpty(medicine.drugTypeCode)) {
                        dialogData.drupTypeCode = medicine.drugTypeCode
                    }
                    dialogData.medName = holder.txtMedicineName.text.toString()
                    dialogData.medDose = medicine.medicationScheduleList[0].dosage.toString()
                    dialogData.medInstruction = medicine.comments
                    if (medicine.notification!!.setAlert!!) {
                        dialogData.setAlert = "true"
                    } else {
                        dialogData.setAlert = "false"
                    }
                    dialogData.medTimeStatusList.clear()
                    dialogData.medTimeStatusList = medicine.medicationScheduleList.toMutableList()
                    fragment.showUpdateStatusDialog(dialogData)
                } else {
                    fragment.showUpdateScheduleListDialog(medicine)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateMedicines(list: MutableList<Medication>, selectedDate: String) {
        this.selectedDate = selectedDate
        medicinesList.clear()
        medicinesList.addAll(list)
        notifyDataSetChanged()
    }

    private fun getDrawableId(iv: ImageView): Int {
        return iv.tag as Int
    }

    inner class DashboardMedicinesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemMedDashboardBinding.bind(view)
        val imgMedType = binding.imgMedType
        val imgEditMed = binding.imgEditMed
        val imgAlert = binding.imgAlert
        val imgCompleted = binding.imgCompleted

        //val txtMedType = binding.txtMedType!!
        val txtMedicineName = binding.txtMedicineName
        val txtDose = binding.txtDose
        val txtMedInstruction = binding.txtMedTime
        val txtMedDateDuration = binding.txtStartDate
        val rvMedScheduleTime = binding.rvMedScheduleTime
    }
}
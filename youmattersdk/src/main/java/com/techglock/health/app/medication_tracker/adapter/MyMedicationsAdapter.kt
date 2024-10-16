package com.techglock.health.app.medication_tracker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ItemMyMedicationBinding
import com.techglock.health.app.medication_tracker.ui.MyMedicationsFragment
import com.techglock.health.app.medication_tracker.ui.OptionsBottomSheet
import com.techglock.health.app.medication_tracker.viewmodel.MedicineTrackerViewModel
import com.techglock.health.app.model.entity.MedicationEntity

class MyMedicationsAdapter(
    val viewModel: MedicineTrackerViewModel, val context: Context,
    val fragment: MyMedicationsFragment
) : RecyclerView.Adapter<MyMedicationsAdapter.MyMedicationsViewHolder>() {

    private val medicationTrackerHelper = viewModel.medicationTrackerHelper
    private var spinnerPosition = 0
    private var medicinesList: MutableList<MedicationEntity.Medication> = mutableListOf()
    private var currentDate: String = DateHelper.currentDateAsStringyyyyMMdd

    override fun getItemCount(): Int = medicinesList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyMedicationsViewHolder =
        MyMedicationsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_my_medication, parent, false)
        )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: MyMedicationsAdapter.MyMedicationsViewHolder,
        position: Int
    ) {
        try {
            val medicine = medicinesList[position]

            if (!Utilities.isNullOrEmpty(medicine.DrugTypeCode)) {
                holder.imgMedType.setImageResource(
                    medicationTrackerHelper.getMedTypeImageByCode(
                        medicine.DrugTypeCode!!
                    )
                )
            }

            val medName = if (!Utilities.isNullOrEmpty(medicine.drug.strength)) {
                medicine.drug.name + " - " + medicine.drug.strength
            } else {
                medicine.drug.name
            }

            holder.txtMedicineName.text = medName
            if (medicine.scheduleList.isNotEmpty()) {
                holder.txtDose.text = medicine.scheduleList[0].dosage + " Dose"
            }
            holder.txtMedTime.text =
                medicationTrackerHelper.getMedInstructionByCode(medicine.comments!!)

            var medDateDuration: String =
                DateHelper.getDateTimeAs_ddMMMyyyyNew(medicine.PrescribedDate)
            if (!Utilities.isNullOrEmpty(medicine.EndDate)) {
                medDateDuration =
                    context.resources.getString(R.string.FROM) + " " + medDateDuration +
                            " " + context.resources.getString(R.string.TO) + " " + DateHelper.getDateTimeAs_ddMMMyyyyNew(
                        medicine.EndDate
                    )
            } else {
                if (DateHelper.getDateDifference(medicine.PrescribedDate!!, currentDate) < 0) {
                    medDateDuration =
                        context.resources.getString(R.string.STARTING_ON) + "  $medDateDuration"
                } else {
                    medDateDuration =
                        context.resources.getString(R.string.STARTED) + "  $medDateDuration"
                }
            }

            if (currentDate.equals(medicine.PrescribedDate, ignoreCase = true)
                && currentDate.equals(medicine.EndDate, ignoreCase = true)
            ) {
                holder.txtStartDate.text = context.resources.getString(R.string.FOR_TODAY_ONLY)
            } else if (medicine.PrescribedDate.equals(medicine.EndDate, ignoreCase = true)) {
                holder.txtStartDate.text =
                    context.resources.getString(R.string.FOR) + "  " + DateHelper.getDateTimeAs_ddMMMyyyyNew(
                        medicine.PrescribedDate
                    ) + "  " + context.resources.getString(R.string.ONLY)
            } else {
                holder.txtStartDate.text = medDateDuration
            }

            /*            holder.itemView.setOnClickListener {
                            val bottomSheet = OptionsBottomSheet( fragment,spinnerPosition,medicine)
                            bottomSheet.show(fragment.childFragmentManager, OptionsBottomSheet.TAG)
                        }*/

            if (position == 0 || position % 2 == 0) {
                holder.layoutDosage.background.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        R.color.primary_purple_light
                    ), PorterDuff.Mode.SRC_ATOP
                )
                holder.txtDose.setTextColor(context.resources.getColor(R.color.colorPrimary))
                holder.txtMedTime.setTextColor(context.resources.getColor(R.color.colorPrimary))
                holder.view.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
            } else {
                holder.layoutDosage.background.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        R.color.secondary_yellow_light
                    ), PorterDuff.Mode.SRC_ATOP
                )
                holder.txtDose.setTextColor(context.resources.getColor(R.color.secondary_yellow))
                holder.txtMedTime.setTextColor(context.resources.getColor(R.color.secondary_yellow))
                holder.view.setBackgroundColor(context.resources.getColor(R.color.secondary_yellow))
            }

            holder.layoutMyMedication.setOnClickListener {
                val bottomSheet = OptionsBottomSheet(fragment, spinnerPosition, medicine)
                bottomSheet.show(fragment.childFragmentManager, OptionsBottomSheet.TAG)
            }

            /*            if (!Utilities.isNullOrEmpty(medicine.EndDate)) {
                            val dateDiff = DateHelper.getDateDifference(medicine.EndDate!!, currentDate)
                            if (dateDiff > 0) {
                                holder.imgEditMed.visibility = View.GONE
                                holder.imgCompleted.visibility = View.VISIBLE
                            } else {
                                holder.imgEditMed.visibility = View.VISIBLE
                                holder.imgCompleted.visibility = View.GONE
                            }
                        } else {
                            holder.imgEditMed.visibility = View.VISIBLE
                            holder.imgCompleted.visibility = View.GONE
                        }*/

            /*
                        holder.imgCompleted.setOnClickListener {
                            fragment.showRescheduleDialog(medicine)
                        }*/

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateMedicines(list: MutableList<MedicationEntity.Medication>, position: Int) {
        this.spinnerPosition = position
        //Utilities.printLog("Position=>$position")
        medicinesList.clear()
        medicinesList.addAll(list)
        notifyDataSetChanged()
    }

    /*    private fun getFrom(position: Int): String {
            val from: String = when (position) {
                0 -> {
                    "History"
                }
                1 -> {
                    "Reschedule"
                }
                else -> {
                    "All"
                }
            }
            Utilities.printLog("from=>$from")
            return from
        }*/

    inner class MyMedicationsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemMyMedicationBinding.bind(view)
        val layoutMyMedication = binding.layoutMyMedication
        val imgMedType = binding.imgMedType
        val txtMedicineName = binding.txtMedicineName

        val layoutDosage = binding.layoutDosage
        val txtDose = binding.txtDose
        val txtMedTime = binding.txtMedTime
        val view = binding.view3

        val txtStartDate = binding.txtStartDate
        val imgMore = binding.imgMore
    }

}
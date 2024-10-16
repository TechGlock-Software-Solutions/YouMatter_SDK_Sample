package com.techglock.health.app.medication_tracker.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ItemMedScheduleTimeBinding
import com.techglock.health.app.model.medication.MedicineListByDayModel.MedicationSchedule

class MedTimeListAdapter(val context: Context, private var medTimeList: List<MedicationSchedule>) :
    RecyclerView.Adapter<MedTimeListAdapter.MedTimeListViewHolder>() {

    override fun getItemCount(): Int = medTimeList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedTimeListViewHolder =
        MedTimeListViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_med_schedule_time, parent, false)
        )

    override fun onBindViewHolder(holder: MedTimeListViewHolder, position: Int) {
        val medTime = medTimeList[position]
        //holder.txtMedTime.text = DateHelper.getTimeIn12HrFormatAmPm(medTime.scheduleTime)
        holder.txtMedTime.text = DateHelper.getTimeIn12HrFormatAmOrPm(medTime.scheduleTime)
        if (!Utilities.isNullOrEmpty(medTime.status)) {
            when {
                medTime.status.equals(Constants.TAKEN, ignoreCase = true) -> {
                    holder.imgStatus.setImageResource(R.drawable.img_circle_check)
                    ImageViewCompat.setImageTintList(
                        holder.imgStatus,
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.state_success
                            )
                        )
                    )
                    //holder.layoutMedTime.background = ContextCompat.getDrawable(context,R.drawable.btn_oval_success)
                }

                medTime.status.equals(Constants.SKIPPED, ignoreCase = true) -> {
                    holder.imgStatus.setImageResource(R.drawable.img_circle_skip)
                    ImageViewCompat.setImageTintList(
                        holder.imgStatus,
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.state_error))
                    )
                    //holder.layoutMedTime.background = ContextCompat.getDrawable(context,R.drawable.btn_oval_error)
                }

                else -> {
                    holder.imgStatus.visibility = View.GONE
                    //holder.layoutMedTime.background = ContextCompat.getDrawable(context,R.drawable.btn_oval_disabled)
                }
            }
        } else {
            holder.imgStatus.visibility = View.GONE
        }
    }

    inner class MedTimeListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemMedScheduleTimeBinding.bind(view)
        val layoutMedTime = binding.layoutRecentMed
        val txtMedTime = binding.txtMedTime
        val imgStatus = binding.imgStatus
    }

}
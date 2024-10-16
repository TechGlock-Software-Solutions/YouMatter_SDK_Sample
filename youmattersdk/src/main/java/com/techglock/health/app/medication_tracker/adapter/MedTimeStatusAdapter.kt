package com.techglock.health.app.medication_tracker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ItemTimeStatusBinding
import com.techglock.health.app.medication_tracker.model.MedStatusModel
import com.techglock.health.app.model.medication.MedicineListByDayModel.MedicationSchedule

class MedTimeStatusAdapter(
    val context: Context,
    var medTimeStatusList: MutableList<MedicationSchedule>,
    private var selectedDate: String
) :
    RecyclerView.Adapter<MedTimeStatusAdapter.MedTimeStatusViewHolder>(),
    MedStatusAdapter.OnStatusChangeListener, MedStatusAdapter.OnFutureListener {

    private var medStatusAdapter: MedStatusAdapter? = null

    override fun getItemCount(): Int = medTimeStatusList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedTimeStatusViewHolder =
        MedTimeStatusViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_time_status, parent, false)
        )

    override fun onBindViewHolder(holder: MedTimeStatusViewHolder, position: Int) {
        try {
            val medicine = medTimeStatusList[position]

            //holder.txtMedTime.text = DateHelper.getTimeIn12HrFormatAmPm(medicine.scheduleTime)
            holder.txtMedTime.text = DateHelper.getTimeIn12HrFormatAmOrPm(medicine.scheduleTime)
            var selectedPos = -1
            if (!Utilities.isNullOrEmpty(medicine.status)) {
                if (medicine.status.equals(Constants.TAKEN, ignoreCase = true)) {
                    selectedPos = 0
                } else if (medicine.status.equals(Constants.SKIPPED, ignoreCase = true)) {
                    selectedPos = 1
                }
                medStatusAdapter = MedStatusAdapter(
                    context,
                    position,
                    getStatusList(),
                    selectedPos,
                    medicine.scheduleTime,
                    selectedDate,
                    this,
                    this
                )
            } else {
                medStatusAdapter = MedStatusAdapter(
                    context,
                    position,
                    getStatusList(),
                    selectedPos,
                    medicine.scheduleTime,
                    selectedDate,
                    this,
                    this
                )
            }
            holder.rvMedTimeStatus.layoutManager = GridLayoutManager(context, 2)
            holder.rvMedTimeStatus.itemAnimator = null
            holder.rvMedTimeStatus.adapter = medStatusAdapter

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getStatusList(): ArrayList<MedStatusModel> {
        val statusList: ArrayList<MedStatusModel> = ArrayList()
        statusList.add(
            MedStatusModel(
                context.resources.getString(R.string.TAKEN),
                Constants.TAKEN,
                R.drawable.img_circle_check,
                R.drawable.btn_oval_success,
                R.color.state_success
            )
        )
        statusList.add(
            MedStatusModel(
                context.resources.getString(R.string.SKIPPED),
                Constants.SKIPPED,
                R.drawable.img_circle_skip,
                R.drawable.btn_oval_error,
                R.color.state_error
            )
        )
        return statusList
    }

    fun updateTimeStatusList(status: String, medicationInTakeID: Int) {
        medTimeStatusList[0].status = status
        medTimeStatusList[0].medicationInTakeID = medicationInTakeID
        if (!Utilities.isNullOrEmpty(status)) {
            if (status.equals(Constants.TAKEN, ignoreCase = true)) {
                medStatusAdapter!!.updateSelectedPosStatus(0)
            } else if (status.equals(Constants.SKIPPED, ignoreCase = true)) {
                medStatusAdapter!!.updateSelectedPosStatus(1)
            }
        }
        this.notifyItemChanged(0)
    }

    override fun onStatusChange(position: Int, status: String) {
        medTimeStatusList[position].status = status
    }

    override fun onFutureChange(position: Int, isFuture: Boolean) {
        medTimeStatusList[position].isFuture = isFuture
    }

    inner class MedTimeStatusViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemTimeStatusBinding.bind(view)

        val txtMedTime = binding.txtMedTime
        val rvMedTimeStatus = binding.rvMedTimeStatus
    }

}
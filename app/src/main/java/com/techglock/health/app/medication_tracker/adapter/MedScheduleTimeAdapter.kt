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
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ItemMedTimeBinding
import com.techglock.health.app.medication_tracker.model.TimeModel
import com.techglock.health.app.medication_tracker.ui.ScheduleDetailsFragment

class MedScheduleTimeAdapter(val context: Context, var fragment: ScheduleDetailsFragment) :
    RecyclerView.Adapter<MedScheduleTimeAdapter.MedScheduleTimeViewHolder>() {

    var medScheduleTimeList: MutableList<TimeModel> = mutableListOf()

    override fun getItemCount(): Int = medScheduleTimeList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedScheduleTimeViewHolder =
        MedScheduleTimeViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_med_time, parent, false)
        )

    override fun onBindViewHolder(holder: MedScheduleTimeViewHolder, position: Int) {
        val time = medScheduleTimeList[position]

        if (position == 0) {
            //holder.imgClock.setImageResource(R.drawable.add)
            holder.imgClock.visibility = View.VISIBLE
            holder.layoutMedTime.background.setColorFilter(
                ContextCompat.getColor(
                    context,
                    R.color.primary_purple_light
                ), PorterDuff.Mode.SRC_ATOP
            )
            holder.txtMedScheduleTime.setTextColor(context.resources.getColor(R.color.colorPrimary))
        } else {
            //holder.imgClock.setImageResource(R.drawable.img_clock2)
            holder.imgClock.visibility = View.GONE
            holder.layoutMedTime.background.setColorFilter(
                ContextCompat.getColor(
                    context,
                    R.color.colorPrimary
                ), PorterDuff.Mode.SRC_ATOP
            )
            holder.txtMedScheduleTime.setTextColor(context.resources.getColor(R.color.white))
        }

        if (!Utilities.isNullOrEmpty(time.displayTime)) {
            if (position != 0) {
                holder.txtMedScheduleTime.text = time.displayTime
            }
        } else {
            if (position != 0) {
                holder.txtMedScheduleTime.text = "--:--"
            } else {
                holder.txtMedScheduleTime.text = context.resources.getString(R.string.ADD)
            }
        }

        if (itemCount > 1) {
            if (position != 0) {
                holder.imgRemove.visibility = View.VISIBLE
            } else {
                holder.imgRemove.visibility = View.GONE
            }
        } else {
            holder.imgRemove.visibility = View.GONE
        }

        holder.layoutMedTime.setOnClickListener {
            if (position == 0) {
                if (medScheduleTimeList.size < 6) {
                    addMedTime(medScheduleTimeList.size, TimeModel(0, "", "", 0, 0))
                }
            } else {
                fragment.showTimePicker(position, time.hour, time.minute)
            }
        }

        holder.imgRemove.setOnClickListener {
            if (medScheduleTimeList.size > 1) {
                fragment.removeAndAddToRemovedScheduleList(position, time, true)
            }
        }
    }

    fun addMedTime(position: Int, timeModel: TimeModel?) {
        Utilities.printLog("Added_position=>$position")
        medScheduleTimeList.add(position, timeModel!!)
        notifyItemInserted(position)
        /*        if (position == 1) {
                    this.notifyItemChanged(0)
                }*/
        //this.notifyDataSetChanged();
    }

    fun updateTime(position: Int, timeModel: TimeModel) {
        Utilities.printLog("Update_position=>$position")
        medScheduleTimeList[position].displayTime = timeModel.displayTime
        medScheduleTimeList[position].time = timeModel.time
        medScheduleTimeList[position].hour = timeModel.hour
        medScheduleTimeList[position].minute = timeModel.minute
        this.notifyItemChanged(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeMedTime(position: Int) {
        Utilities.printLog("Removed_position=>$position")
        medScheduleTimeList.removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    class MedScheduleTimeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemMedTimeBinding.bind(view)
        val layoutMedTime = binding.layoutMedTime
        val txtMedScheduleTime = binding.txtMedScheduleTime
        val imgClock = binding.imgClock
        val imgRemove = binding.imgRemove
    }
}
package com.techglock.health.app.medication_tracker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ItemMedicineCalenderBinding
import com.techglock.health.app.medication_tracker.model.MedCalender
import com.techglock.health.app.medication_tracker.ui.MedicineDashboardFragment
import com.techglock.health.app.medication_tracker.viewmodel.MedicineTrackerViewModel

class MedCalenderAdapter(
    val viewModel: MedicineTrackerViewModel, val context: Context,
    pos: Int, val fragment: MedicineDashboardFragment
) : RecyclerView.Adapter<MedCalenderAdapter.MedCalenderViewHolder>() {

    private var calenderDateList: MutableList<MedCalender> = mutableListOf()
    private var listener: OnDateClickListener = fragment
    private var selectedPos = pos
    //private var todayPos = selectedPos

    override fun getItemCount(): Int = calenderDateList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedCalenderViewHolder =
        MedCalenderViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_medicine_calender, parent, false)
        )


    @SuppressLint("SetTextI18n", "RecyclerView")
    override fun onBindViewHolder(holder: MedCalenderViewHolder, position: Int) {
        try {
            val date = calenderDateList[position]
            if (date.IsToday) {
                holder.txtDate.text = context.resources.getString(R.string.TODAY)
            } else {
                holder.txtDate.text = date.Month + " " + date.DayOfMonth
            }
            holder.txtDayName.text = Utilities.getDayOfWeekConverted(date.DayOfWeek, context)
            holder.txtDay.text = date.DayOfMonth
            if (selectedPos == position) {
                //holder.layoutSelectedDay.background.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP)
                holder.layoutSelectedDay.background =
                    ContextCompat.getDrawable(context, R.drawable.btn_fill_selected)
                if (date.IsToday) {
                    holder.txtDate.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorPrimary
                        )
                    )
                } else {
                    holder.txtDate.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorPrimary
                        )
                    )
                }
                holder.txtDayName.setTextColor(ContextCompat.getColor(context, R.color.white))
                holder.txtDay.setTextColor(ContextCompat.getColor(context, R.color.white))

            } else {
                //holder.layoutSelectedDay.background.setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_ATOP)
                holder.layoutSelectedDay.background =
                    ContextCompat.getDrawable(context, R.drawable.btn_border_disabled)
                if (date.IsToday) {
                    holder.txtDate.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorPrimary
                        )
                    )
                } else {
                    holder.txtDate.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.transparent
                        )
                    )
                }
                holder.txtDayName.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorPrimary
                    )
                )
                holder.txtDay.setTextColor(ContextCompat.getColor(context, R.color.textViewColor))

            }

            holder.itemView.setOnClickListener {
                listener.onDateSelection(date, position)
                notifyItemChanged(selectedPos)
                selectedPos = position
                notifyItemChanged(selectedPos)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: MutableList<MedCalender>) {
        calenderDateList.clear()
        calenderDateList.addAll(list)
        notifyDataSetChanged()
    }

    interface OnDateClickListener {
        fun onDateSelection(date: MedCalender, newDayPosition: Int)
    }

    inner class MedCalenderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemMedicineCalenderBinding.bind(view)
        val txtDate = binding.txtDate
        val txtDayName = binding.txtDayName
        val txtDay = binding.txtDay
        val layoutSelectedDay = binding.layoutSelectedDay
    }

}
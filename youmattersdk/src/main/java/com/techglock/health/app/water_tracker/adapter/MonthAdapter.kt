package com.techglock.health.app.water_tracker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ItemMonthBinding
import com.techglock.health.app.model.fitness.MonthModel
import com.techglock.health.app.water_tracker.viewmodel.WaterTrackerViewModel

class MonthAdapter(
    val viewModel: WaterTrackerViewModel,
    val context: Context,
    val listener: OnMonthListener
) : RecyclerView.Adapter<MonthAdapter.MedicineTypeViewHolder>() {

    //private val appColorHelper = AppColorHelper.instance!!
    private var selectedPos = -1
    //private var selectedPos = 0

    private val currentMonth = DateHelper.currentMonthAsStringMMM
    private val joiningMonthOfYear =
        DateHelper.getMonthOfYearAsStringMM(viewModel.joiningDate, "yyyy-MM-dd")

    private val currentMonthOfYear = DateHelper.currentMonthOfYearAsStringMM.toInt()
    private val currentYear = DateHelper.currentYearAsStringyyyy.toInt()
    private var joiningYear = viewModel.joiningDate.split("-")[0].toInt()

    private var year = currentYear
    private var month = currentMonth
    private var isClickable = true
    private var monthList: MutableList<MonthModel> = mutableListOf()

    override fun getItemCount(): Int = monthList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineTypeViewHolder =
        MedicineTypeViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_month, parent, false)
        )

    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(holder: MedicineTypeViewHolder, position: Int) {
        val monthData = monthList[position]

        holder.txtMonth.text = Utilities.getMonthConverted(monthData.month, context)

        if (selectedPos == -1) {
            holder.layoutContainer.background =
                ContextCompat.getDrawable(context, R.drawable.border_button_selected_blue)
            holder.txtMonth.setTextColor(ContextCompat.getColor(context, R.color.primary_blue))
        } else {
            if (selectedPos == position) {
                listener.onMonthSelection(position, monthData)
                holder.layoutContainer.background =
                    ContextCompat.getDrawable(context, R.drawable.btn_fill_selected_blue)
                holder.txtMonth.setTextColor(ContextCompat.getColor(context, R.color.white))
            } else {
                holder.layoutContainer.background =
                    ContextCompat.getDrawable(context, R.drawable.border_button_selected_blue)
                holder.txtMonth.setTextColor(ContextCompat.getColor(context, R.color.primary_blue))
            }
        }

        Utilities.printLogError("monthOfYear--->${monthData.monthOfYear.toInt()}")
        if (currentYear == joiningYear) {
            if (monthData.monthOfYear.toInt() > currentMonthOfYear) {
                isClickable = false
                holder.layoutContainer.background =
                    ContextCompat.getDrawable(context, R.drawable.btn_fill_disabled)
                holder.layoutContainer.background.colorFilter =
                    BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                        ContextCompat.getColor(
                            context,
                            R.color.vivant_disabled
                        ), BlendModeCompat.SRC_ATOP
                    )
                holder.txtMonth.setTextColor(ContextCompat.getColor(context, R.color.tab_text))
            } else if (monthData.monthOfYear.toInt() < joiningMonthOfYear.toInt()) {
                isClickable = false
                holder.layoutContainer.background =
                    ContextCompat.getDrawable(context, R.drawable.btn_fill_disabled)
                holder.layoutContainer.background.colorFilter =
                    BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                        ContextCompat.getColor(
                            context,
                            R.color.vivant_disabled
                        ), BlendModeCompat.SRC_ATOP
                    )
                holder.txtMonth.setTextColor(ContextCompat.getColor(context, R.color.tab_text))
            } else {
                isClickable = true
            }
        } else if (year == currentYear) {
            if (monthData.monthOfYear.toInt() > currentMonthOfYear) {
                isClickable = false
                holder.layoutContainer.background =
                    ContextCompat.getDrawable(context, R.drawable.btn_fill_disabled)
                holder.layoutContainer.background.colorFilter =
                    BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                        ContextCompat.getColor(
                            context,
                            R.color.vivant_disabled
                        ), BlendModeCompat.SRC_ATOP
                    )
                holder.txtMonth.setTextColor(ContextCompat.getColor(context, R.color.tab_text))
            } else {
                isClickable = true
            }
        } else if (year == joiningYear) {
            if (monthData.monthOfYear.toInt() < joiningMonthOfYear.toInt()) {
                isClickable = false
                holder.layoutContainer.background =
                    ContextCompat.getDrawable(context, R.drawable.btn_fill_disabled)
                holder.layoutContainer.background.colorFilter =
                    BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                        ContextCompat.getColor(
                            context,
                            R.color.vivant_disabled
                        ), BlendModeCompat.SRC_ATOP
                    )
                holder.txtMonth.setTextColor(ContextCompat.getColor(context, R.color.tab_text))
            } else {
                isClickable = true
            }
        } else {
            isClickable = true
        }

        if (isClickable) {
            holder.itemView.setOnClickListener {
                notifyItemChanged(selectedPos)
                selectedPos = position
                //notifyItemChanged(selectedPos)
                notifyDataSetChanged()
            }
        }

    }

    fun updateList(list: MutableList<MonthModel>, mon: String, yr: Int) {
        year = yr
        month = mon
        Utilities.printLogError("year--->$year")
        Utilities.printLogError("currentYear--->$currentYear")
        Utilities.printLogError("currentMonthOfYear--->$currentMonthOfYear")
        this.monthList.clear()
        this.monthList.addAll(list)
        if (!Utilities.isNullOrEmpty(mon)) {
            for (j in 0 until monthList.size) {
                if (monthList[j].month == mon) {
                    this.selectedPos = j
                    break
                }
            }
        } else {
            this.selectedPos = -1
        }
        this.notifyDataSetChanged()
    }

    interface OnMonthListener {
        fun onMonthSelection(position: Int, mon: MonthModel)
    }

    inner class MedicineTypeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemMonthBinding.bind(view)
        val txtMonth = binding.txtMonth
        val layoutContainer = binding.layoutContainer
    }

}
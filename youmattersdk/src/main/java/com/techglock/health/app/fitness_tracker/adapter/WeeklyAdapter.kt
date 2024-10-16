package com.techglock.health.app.fitness_tracker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.databinding.ItemWeekMonthBinding
import com.techglock.health.app.model.fitness.WeekModel

class WeeklyAdapter(
    val context: Context,
    val joiningDate: String,
    mListener: OnWeeklyItemClickListener
) : RecyclerView.Adapter<WeeklyAdapter.WeeklyViewHolder>() {

    private var selectedPos = 1
    private val appColorHelper = AppColorHelper.instance!!
    private var listener: OnWeeklyItemClickListener = mListener
    private var weeklyList: MutableList<WeekModel> = mutableListOf()
    private val currentDate = DateHelper.currentDateAsStringyyyyMMdd

    override fun getItemCount(): Int = weeklyList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeklyViewHolder =
        WeeklyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_week_month, parent, false)
        )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: WeeklyViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val item = weeklyList[position]

        if (position == 0) {
            holder.txtWeekDate.visibility = View.GONE
            holder.layoutJoiningDate.visibility = View.VISIBLE
            holder.txtJoiningDate.text = joiningDate
        } else {
            holder.txtWeekDate.visibility = View.VISIBLE
            holder.layoutJoiningDate.visibility = View.GONE

            if (item.endDate == currentDate) {
                holder.txtWeekDate.text = context.resources.getString(R.string.THIS_WEEK)
            } else {
                holder.txtWeekDate.text = "${item.left} - ${item.right}"
            }

            if (selectedPos == position) {
                listener.onWeeklyItemSelection(position, item)
                holder.txtWeekDate.background = ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.btn_fill_selected,
                    null
                )
                holder.txtWeekDate.setTextColor(ContextCompat.getColor(context, R.color.white))
            } else {
                holder.txtWeekDate.background = ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.btn_border_deselected,
                    null
                )
                holder.txtWeekDate.setTextColor(appColorHelper.textColor)
            }

            holder.txtWeekDate.setOnClickListener {
                //listener.onWeeklyItemSelection(position,item)
                if (selectedPos != position) {
                    notifyItemChanged(selectedPos)
                    selectedPos = position
                    notifyItemChanged(selectedPos)
                }
            }

        }

    }

    fun updateList(list: MutableList<WeekModel>, isWeeklySynopsis: Boolean) {
        this.weeklyList.clear()
        this.weeklyList.addAll(list)
        this.selectedPos = list.size - 1
        if (isWeeklySynopsis) {
            this.selectedPos = list.size - 2
        }
        this.notifyDataSetChanged()
    }

    class WeeklyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemWeekMonthBinding.bind(view)
        val txtWeekDate = binding.txtDate
        val layoutJoiningDate = binding.layoutJoiningDate
        val txtJoiningDate = binding.txtJoiningDate
    }

    interface OnWeeklyItemClickListener {
        fun onWeeklyItemSelection(position: Int, week: WeekModel)
    }

}
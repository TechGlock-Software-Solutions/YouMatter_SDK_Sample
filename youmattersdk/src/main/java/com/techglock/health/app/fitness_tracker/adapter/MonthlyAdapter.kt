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
import com.techglock.health.app.databinding.ItemWeekMonthBinding
import com.techglock.health.app.model.fitness.MonthModel

class MonthlyAdapter(
    val context: Context,
    val joiningDate: String,
    mListener: OnMonthlyItemClickListener
) : RecyclerView.Adapter<MonthlyAdapter.MonthlyViewHolder>() {

    private var selectedPos = 1
    private val appColorHelper = AppColorHelper.instance!!
    private var listener: OnMonthlyItemClickListener = mListener
    private var monthlyList: MutableList<MonthModel> = mutableListOf()

    override fun getItemCount(): Int = monthlyList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthlyViewHolder =
        MonthlyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_week_month, parent, false)
        )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: MonthlyViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val item = monthlyList[position]

        if (position == 0) {
            holder.txtWeekDate.visibility = View.GONE
            holder.layoutJoiningDate.visibility = View.VISIBLE
            holder.txtJoiningDate.text = joiningDate
        } else {
            holder.txtWeekDate.visibility = View.VISIBLE
            holder.layoutJoiningDate.visibility = View.GONE

            holder.txtWeekDate.text = "${item.month} ${item.year}"

            if (selectedPos == position) {
                listener.onMonthlyItemSelection(position, item)
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
                //listener.onMonthlyItemSelection(position,item)
                if (selectedPos != position) {
                    notifyItemChanged(selectedPos)
                    selectedPos = position
                    notifyItemChanged(selectedPos)
                }
            }

        }

    }

    fun updateList(list: MutableList<MonthModel>) {
        this.monthlyList.clear()
        this.monthlyList.addAll(list)
        this.selectedPos = list.size - 1
        this.notifyDataSetChanged()
    }

    class MonthlyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemWeekMonthBinding.bind(view)
        val txtWeekDate = binding.txtDate
        val layoutJoiningDate = binding.layoutJoiningDate
        val txtJoiningDate = binding.txtJoiningDate
    }

    interface OnMonthlyItemClickListener {
        fun onMonthlyItemSelection(position: Int, month: MonthModel)
    }

}
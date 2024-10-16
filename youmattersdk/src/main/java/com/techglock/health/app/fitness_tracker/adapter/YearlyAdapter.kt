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
import com.techglock.health.app.model.fitness.YearModel

class YearlyAdapter(
    val context: Context,
    private val joiningDate: String,
    mListener: OnYearItemClickListener
) : RecyclerView.Adapter<YearlyAdapter.YearlyViewHolder>() {

    private var selectedPos = 1
    private val appColorHelper = AppColorHelper.instance!!
    private var listener: OnYearItemClickListener = mListener
    private var monthlyList: MutableList<YearModel> = mutableListOf()

    override fun getItemCount(): Int = monthlyList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YearlyViewHolder =
        YearlyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_week_month, parent, false)
        )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: YearlyViewHolder,
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

            holder.txtWeekDate.text = item.year

            if (selectedPos == position) {
                listener.onYearlyItemSelection(position, item)
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

    fun updateList(list: MutableList<YearModel>) {
        this.monthlyList.clear()
        this.monthlyList.addAll(list)
        this.selectedPos = list.size - 1
        this.notifyDataSetChanged()
    }

    class YearlyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemWeekMonthBinding.bind(view)
        val txtWeekDate = binding.txtDate
        val layoutJoiningDate = binding.layoutJoiningDate
        val txtJoiningDate = binding.txtJoiningDate
    }

    interface OnYearItemClickListener {
        fun onYearlyItemSelection(position: Int, yearModel: YearModel)
    }

}
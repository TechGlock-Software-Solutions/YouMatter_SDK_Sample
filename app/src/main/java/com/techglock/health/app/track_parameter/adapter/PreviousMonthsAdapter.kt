package com.techglock.health.app.track_parameter.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ItemHistoryMonthBinding
import com.techglock.health.app.model.parameter.MonthYear

class PreviousMonthsAdapter(
    private val yearMonthList: ArrayList<MonthYear>,
    val listener: OnMonthClickListener,
    val mContext: Context
) : RecyclerView.Adapter<PreviousMonthsAdapter.PreviousMonthsViewHolder>() {

    private var selectedPos = 0
    private val appColorHelper = AppColorHelper.instance!!

    override fun getItemCount(): Int = yearMonthList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreviousMonthsViewHolder =
        PreviousMonthsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_history_month, parent, false)
        )

    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(holder: PreviousMonthsViewHolder, position: Int) {
        val yearMonth = yearMonthList[position]

        //holder.txtMonth.text = yearMonth.month.substring(0, 3)
        holder.txtMonth.text = Utilities.getMonthConverted(yearMonth.month, mContext)

        if (selectedPos == position) {
            holder.txtMonth.setTextColor(appColorHelper.primaryColor())
            holder.viewSelectedMonth.visibility = View.VISIBLE
        } else {
            holder.txtMonth.setTextColor(ContextCompat.getColor(mContext, R.color.textViewColor))
            holder.viewSelectedMonth.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            listener.onMonthSelection(yearMonth, position)
            notifyItemChanged(selectedPos)
            selectedPos = position
            notifyItemChanged(selectedPos)
        }
    }

    interface OnMonthClickListener {
        fun onMonthSelection(yearMonth: MonthYear, newMonthPosition: Int)
    }

    inner class PreviousMonthsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemHistoryMonthBinding.bind(view)
        val viewSelectedMonth = binding.viewSelectedMonth
        val txtMonth = binding.txtMonth
    }
}
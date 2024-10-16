package com.techglock.health.app.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.databinding.ItemNimeyaCalculatorBinding
import com.techglock.health.app.home.common.DataHandler


class NimeyaCalculatorsAdapter(
    private val mContext: Context,
    private val listener: OnNimeyaCalculatorListener
) : RecyclerView.Adapter<NimeyaCalculatorsAdapter.NimeyaCalculatorsViewHolder>() {

    private var calculatorsList: MutableList<DataHandler.FinancialCalculatorModel> = mutableListOf()

    override fun getItemCount(): Int = calculatorsList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NimeyaCalculatorsViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_nimeya_calculator, parent, false)
        return NimeyaCalculatorsViewHolder(v)
    }

    override fun onBindViewHolder(holder: NimeyaCalculatorsViewHolder, position: Int) {
        val feature = calculatorsList[position]
        holder.txtTrackerName.text = feature.calculatorTitle
        holder.imgTracker.setImageResource(feature.calculatorImgId)
        holder.txtTrackerDesc.text = feature.calculatorDesc

        holder.layoutTracker.setOnClickListener {
            listener.onNimeyaCalculatorClick(feature)
        }
    }

    fun updateList(list: List<DataHandler.FinancialCalculatorModel>) {
        this.calculatorsList.clear()
        this.calculatorsList.addAll(list)
        this.notifyDataSetChanged()
    }

    interface OnNimeyaCalculatorListener {
        fun onNimeyaCalculatorClick(item: DataHandler.FinancialCalculatorModel)
    }

    inner class NimeyaCalculatorsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemNimeyaCalculatorBinding.bind(view)
        var layoutTracker = binding.layoutTracker

        //var speedView = binding.speedView
        var txtTrackerName = binding.txtTrackerName
        var imgTracker = binding.imgTracker
        var txtTrackerDesc = binding.txtTrackerDesc
    }

}
package com.techglock.health.app.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.databinding.ItemFinancialCalculatorBinding
import com.techglock.health.app.home.common.DataHandler

class FinancialCalculatorsAdapter(
    private val mContext: Context,
    private val listener: OnFinancialCalculatorListener
) : RecyclerView.Adapter<FinancialCalculatorsAdapter.FinancialCalculatorsViewHolder>() {

    private var calculatorsList: MutableList<DataHandler.FinancialCalculatorModel> = mutableListOf()

    override fun getItemCount(): Int = calculatorsList.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FinancialCalculatorsAdapter.FinancialCalculatorsViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_financial_calculator, parent, false)
        return FinancialCalculatorsViewHolder(v)
    }

    override fun onBindViewHolder(
        holder: FinancialCalculatorsAdapter.FinancialCalculatorsViewHolder,
        position: Int
    ) {
        val feature = calculatorsList[position]
        holder.imgFinancialCalculator.setImageResource(feature.calculatorImgId)
        holder.txtFinancialCalculator.text = feature.calculatorTitle
        holder.txtFinancialCalculator.setTextColor(ContextCompat.getColor(mContext, feature.color))

        holder.layoutFinancialCalculators.setOnClickListener {
            listener.onFinancialCalculatorClick(feature)
        }
    }

    fun updateList(list: List<DataHandler.FinancialCalculatorModel>) {
        this.calculatorsList.clear()
        this.calculatorsList.addAll(list)
        this.notifyDataSetChanged()
    }

    interface OnFinancialCalculatorListener {
        fun onFinancialCalculatorClick(calculatorModel: DataHandler.FinancialCalculatorModel)
    }

    inner class FinancialCalculatorsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemFinancialCalculatorBinding.bind(view)
        var layoutFinancialCalculators = binding.layoutFinancialCalculators
        var imgFinancialCalculator = binding.imgFinancialCalculator
        var txtFinancialCalculator = binding.txtFinancialCalculator
    }

}
package com.techglock.health.app.tools_calculators.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.databinding.HeartRiskDataItemBinding
import com.techglock.health.app.tools_calculators.model.HeartAgeReport

class HeartRiskReportAdapter(val paramList: MutableList<HeartAgeReport>, val context: Context) :
    RecyclerView.Adapter<HeartRiskReportAdapter.HeartRiskReportViewHolder>() {

    override fun getItemCount(): Int = paramList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeartRiskReportViewHolder =
        HeartRiskReportViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.heart_risk_data_item, parent, false)
        )

    override fun onBindViewHolder(
        holder: HeartRiskReportAdapter.HeartRiskReportViewHolder,
        position: Int
    ) {
        val parameter = paramList[position]
        holder.binding.txtTitle.text = parameter.title
        holder.binding.txtDescription.text = parameter.description
    }

    inner class HeartRiskReportViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = HeartRiskDataItemBinding.bind(view)
    }

}
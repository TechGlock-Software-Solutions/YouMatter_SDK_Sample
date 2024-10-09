package com.techglock.health.app.tools_calculators.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.databinding.ItemToolsStressListitemBinding
import com.techglock.health.app.tools_calculators.model.HypertensionResultPojo

class StressDetailReportAdapter(
    private val context: Context,
    private val suggestionsList: List<HypertensionResultPojo>
) : RecyclerView.Adapter<StressDetailReportAdapter.StressDetailReportViewHolder>() {

    override fun getItemCount(): Int = suggestionsList.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StressDetailReportViewHolder =
        StressDetailReportViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_tools_stress_listitem, parent, false)
        )

    override fun onBindViewHolder(holder: StressDetailReportViewHolder, position: Int) {
        holder.txtTitle.text = suggestionsList[position].title.replace("-", "")
        holder.txtDesc.text = suggestionsList[position].description
    }

    inner class StressDetailReportViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemToolsStressListitemBinding.bind(view)
        var txtTitle = binding.txtTitle
        var txtDesc = binding.txtDescription
    }

}
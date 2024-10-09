package com.techglock.health.app.tools_calculators.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.databinding.ParamReportItemViewBinding
import com.techglock.health.app.tools_calculators.model.HeartAgeReport

class ParamDataReportAdapter(val paramList: MutableList<HeartAgeReport>, val context: Context) :
    RecyclerView.Adapter<ParamDataReportAdapter.ParamDataReportViewHolder>() {

    override fun getItemCount(): Int = paramList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParamDataReportViewHolder =
        ParamDataReportViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.param_report_item_view, parent, false)
        )

    override fun onBindViewHolder(holder: ParamDataReportViewHolder, position: Int) {
        val parameter = paramList[position]

        holder.binding.txtTitle.text = parameter.title
        holder.binding.txtDescription.text = parameter.description

        if (parameter.title.lowercase().contains("smoke")) {
            holder.mImg.setImageResource(R.drawable.img_smoking)
            //ViewCompat.setBackgroundTintList(holder.mImg, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.vivant_dusty_orange)))
        } else if (parameter.title.lowercase().contains("blood pressure")) {
            holder.mImg.setImageResource(R.drawable.img_bp_new)
            //ViewCompat.setBackgroundTintList(holder.mImg, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.vivant_watermelon)))
        } else {
            //ViewCompat.setBackgroundTintList(holder.mImg, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimary)))
        }
    }

    inner class ParamDataReportViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ParamReportItemViewBinding.bind(view)
        var mImg = binding.img
    }

}
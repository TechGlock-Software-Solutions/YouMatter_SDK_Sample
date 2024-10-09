package com.techglock.health.app.hra.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.databinding.ItemLabtestBinding
import com.techglock.health.app.hra.ui.HraSummaryActivity
import com.techglock.health.app.hra.viewmodel.HraSummaryViewModel
import com.techglock.health.app.model.hra.HraLabTest

class HraLabTestsAdapter(
    private val labTestsList: List<HraLabTest>,
    val viewModel: HraSummaryViewModel,
    val context: Context,
    val activity: HraSummaryActivity
) : RecyclerView.Adapter<HraLabTestsAdapter.HraLabTestsHolder>() {

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HraLabTestsHolder =
        HraLabTestsHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_labtest, parent, false)
        )

    override fun getItemCount(): Int = labTestsList.size

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: HraLabTestsHolder, position: Int) {
        val labTest = labTestsList[position]
        holder.labTestName.text = labTest.LabTestName
        if (!labTest.Frequency.equals("As suggested by your doctor", ignoreCase = true)) {
            holder.labTestFrequency.visibility = View.VISIBLE
            holder.labTestFrequency.text = labTest.Frequency
        } else {
            holder.labTestFrequency.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            activity.showLabTestDetailsDialog(labTestsList[position])
        }
    }

    inner class HraLabTestsHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemLabtestBinding.bind(view)
        val labTestName = binding.txtLabTest
        val labTestFrequency = binding.txtFrequency
    }
}
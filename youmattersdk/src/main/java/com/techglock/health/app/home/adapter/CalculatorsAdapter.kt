package com.techglock.health.app.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.databinding.ItemCalculatorsBinding
import com.techglock.health.app.home.common.CalculatorModel

class CalculatorsAdapter(private val listener: OnCalculatorClickListener) :
    RecyclerView.Adapter<CalculatorsAdapter.CalculatorsViewHolder>() {

    private val trackersList: MutableList<CalculatorModel> = mutableListOf()

    override fun getItemCount(): Int = trackersList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalculatorsViewHolder =
        CalculatorsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_calculators, parent, false)
        )

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: CalculatorsViewHolder, position: Int) {
        val tracker = trackersList[position]
        holder.bindTo(tracker)

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateTrackersList(items: List<CalculatorModel>) {
        trackersList.clear()
        trackersList.addAll(items)
        notifyDataSetChanged()
    }

    inner class CalculatorsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemCalculatorsBinding.bind(view)

        val layoutTracker = binding.layoutTracker

        fun bindTo(trackerItem: CalculatorModel) {
            binding.imgTracker.setImageResource(trackerItem.imageId)
            binding.txtTrackerName.text = trackerItem.name
            binding.txtTrackerDesc.text = trackerItem.description
        }

        init {
            layoutTracker.setOnClickListener {
                listener.onCalculatorSelection(trackersList[adapterPosition], it)
            }
        }
    }

    interface OnCalculatorClickListener {
        fun onCalculatorSelection(trackerDashboardModel: CalculatorModel, view: View)
    }

}
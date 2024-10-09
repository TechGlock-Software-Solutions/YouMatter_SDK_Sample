package com.techglock.health.app.tools_calculators.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.databinding.ItemToolsDashboardBinding
import com.techglock.health.app.tools_calculators.model.TrackerDashboardModel

class TrackersDashboardAdapter(
    private val context: Context,
    private val listener: OnCalculatorClickListener
) : RecyclerView.Adapter<TrackersDashboardAdapter.TrackersDashboardViewHolder>() {

    private val trackersList: MutableList<TrackerDashboardModel> = mutableListOf()

    override fun getItemCount(): Int = trackersList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackersDashboardViewHolder =
        TrackersDashboardViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_tools_dashboard, parent, false)
        )

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: TrackersDashboardViewHolder, position: Int) {
        val tracker = trackersList[position]

        holder.imgTracker.setImageResource(tracker.imageId)
        holder.txtTrackerName.text = tracker.name
        holder.txtTrackerDesc.text = tracker.description
        //holder.view.setBackgroundColor(ContextCompat.getColor(context,tracker.color))
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateTrackersList(items: List<TrackerDashboardModel>) {
        trackersList.clear()
        trackersList.addAll(items)
        notifyDataSetChanged()
    }

    inner class TrackersDashboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemToolsDashboardBinding.bind(view)

        val layoutTracker = binding.layoutTracker
        val view = binding.view
        val imgTracker = binding.imgTracker
        val txtTrackerName = binding.txtTrackerName
        val txtTrackerDesc = binding.txtTrackerDesc

        init {
            view.setOnClickListener {
                listener.onCalculatorSelection(trackersList[adapterPosition], it)
            }
        }
    }

    interface OnCalculatorClickListener {
        fun onCalculatorSelection(trackerDashboardModel: TrackerDashboardModel, view: View)
    }

}
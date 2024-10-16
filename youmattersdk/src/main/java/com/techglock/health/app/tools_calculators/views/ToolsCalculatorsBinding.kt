package com.techglock.health.app.tools_calculators.views

import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.tools_calculators.adapter.TrackersDashboardAdapter
import com.techglock.health.app.tools_calculators.model.TrackerDashboardModel

object ToolsCalculatorsBinding {

    @BindingAdapter("android:loadImage")
    @JvmStatic
    fun AppCompatImageView.setImageView(resource: Int) {
        //Picasso.get().load(resource).into(this)
        setImageResource(resource)
    }

    @BindingAdapter("app:trackersList")
    @JvmStatic
    fun RecyclerView.setTrackersList(list: MutableList<TrackerDashboardModel>?) {
        with(this.adapter as TrackersDashboardAdapter) {
            layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 2)
            list?.let { updateTrackersList(it) }
        }
    }

}
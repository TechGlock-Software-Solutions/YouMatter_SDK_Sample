package com.techglock.health.app.track_parameter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ItemTableTrackParamBinding
import com.techglock.health.app.model.entity.TrackParameterMaster
import com.techglock.health.app.track_parameter.util.TrackParameterHelper

class RevParamDetailsTableAdapter(val context: Context) :
    RecyclerView.Adapter<RevParamDetailsTableAdapter.ParamViewHolder>() {

    private val dataList: MutableList<TrackParameterMaster.History> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ParamViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_table_track_param, parent, false)
    )

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: ParamViewHolder, position: Int) =
        holder.bindTo(dataList[position])

    fun updateData(items: List<TrackParameterMaster.History>) {
        dataList.clear()
        dataList.addAll(items.reversed())
        Utilities.printLog("Inside updateData " + dataList.size)
        this.notifyDataSetChanged()
    }

    inner class ParamViewHolder(parent: View) : RecyclerView.ViewHolder(parent) {
        private val binding = ItemTableTrackParamBinding.bind(parent)

        fun bindTo(item: TrackParameterMaster.History) {
//            binding.txtDate.text = DateHelper.convertDateToStr(DateHelper.convertStringToDate(item.recordDate), DateHelper.DISPLAY_DATE_DDMMMYYYY)
            binding.txtDate.text = DateHelper.convertDateSourceToDestination(
                item.recordDate,
                DateHelper.DISPLAY_DATE_DDMMMYYYY,
                DateHelper.DATEFORMAT_DDMMMYYYY_NEW
            )
            // This condition is required cause we are customizing Blood pressure list
            if (item.parameterCode.equals("BP", true)) {
                binding.txtValue.text = item.textValue
            } else {
                binding.txtValue.text = item.value.toString()
            }
            if (item.observation.isNullOrEmpty()) {
                binding.txtObs.text = " -- "
            } else {
                binding.txtObs.text =
                    TrackParameterHelper.getLocalizeObservation(item.observation, context)
            }

        }

    }

}
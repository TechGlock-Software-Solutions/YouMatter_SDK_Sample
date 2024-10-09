package com.techglock.health.app.track_parameter.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ItemExpandableParameterBinding
import com.techglock.health.app.model.entity.TrackParameterMaster
import com.techglock.health.app.track_parameter.ui.DetailHistoryFragment
import com.techglock.health.app.track_parameter.util.TrackParameterHelper
import java.util.*

class ExpandedParametersAdapter(
    private val mContext: Context, private val fragment: DetailHistoryFragment,
    private var parametersList: MutableList<TrackParameterMaster.History>
) : RecyclerView.Adapter<ExpandedParametersAdapter.ExpandedParametersViewHolder>() {

    //private val appColorHelper = AppColorHelper.instance!!

    override fun getItemCount(): Int = parametersList.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ExpandedParametersViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expandable_parameter, parent, false)
        return ExpandedParametersViewHolder(v)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ExpandedParametersViewHolder, position: Int) {
        try {
            val parameter = parametersList[position]
            var color: Int = ContextCompat.getColor(mContext, R.color.textViewColor)
            if (!Utilities.isNullOrEmpty(parameter.observation)) {
                color = ContextCompat.getColor(
                    mContext,
                    TrackParameterHelper.getObservationColor(
                        Objects.requireNonNull(parameter.observation!!),
                        parameter.profileCode!!
                    )
                )
            }
            holder.bindTo(parameter, color)

            if (!Utilities.isNullOrEmpty(parameter.recordDate)) {
                holder.txtParamDate.text = DateHelper.convertDateSourceToDestination(
                    parameter.recordDate,
                    DateHelper.DISPLAY_DATE_DDMMMYYYY,
                    DateHelper.DATEFORMAT_DDMMMYYYY_NEW
                )
            }

            if (parameter.profileCode.equals("URINE", ignoreCase = true)) {
                if (!Utilities.isNullOrEmpty(parameter.textValue)) {
                    holder.txtParamValue.text = parameter.textValue
                }
            } else {
                if (!Utilities.isNullOrEmpty(parameter.value.toString())) {
                    holder.txtParamValue.text = parameter.value.toString()
                }
            }

            if (!Utilities.isNullOrEmpty(parameter.observation)) {
                if (!parameter.observation.equals("NA", ignoreCase = true)) {
                    holder.txtParamObs.text = parameter.observation
                }
            }

            holder.itemView.setOnClickListener {
                fragment.showDetailsDialog(parameter, color)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class ExpandedParametersViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemExpandableParameterBinding.bind(view)

        //var layoutParent = binding.layoutParent
        //var txtParamTitle = binding.txtParamTitle
        var txtParamObs = binding.txtParamObs
        var txtParamValue = binding.txtParamValue

        //var txtParamUnit = binding.txtParamUnit
        var txtParamDate = binding.txtParamDate
        var view = binding.viewExpand
        //var viewExpand = binding.viewLast

        fun bindTo(history: TrackParameterMaster.History, color: Int) {
//            android:NotNullEmptyText="@{history.unit.toString()}"
            binding.txtParamTitle.text = history.description ?: ""
            binding.txtParamUnit.text = history.unit ?: ""
            binding.txtParamValue.setTextColor(color)
            binding.txtParamObs.setTextColor(color)

        }
    }
}
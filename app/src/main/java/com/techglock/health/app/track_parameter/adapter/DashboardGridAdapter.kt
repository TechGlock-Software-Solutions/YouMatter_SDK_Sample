package com.techglock.health.app.track_parameter.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ItemTrackparamDashboardGridBinding
import com.techglock.health.app.model.parameter.DashboardParamGridModel
import com.techglock.health.app.track_parameter.viewmodel.DashboardViewModel
import java.util.*

class DashboardGridAdapter(
    private val mContext: Context, private val listener: ParameterSelectionListener,
    private val viewModel: DashboardViewModel
) : RecyclerView.Adapter<DashboardGridAdapter.DashboardTrackParamGridViewHolder>() {

    private var parametersList: ArrayList<DashboardParamGridModel> = arrayListOf()
    //private var appColorHelper = AppColorHelper.instance!!

    override fun getItemCount(): Int = parametersList.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DashboardTrackParamGridViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trackparam_dashboard_grid, parent, false)
        return DashboardTrackParamGridViewHolder(v)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: DashboardTrackParamGridViewHolder, position: Int) {
        val parameter: DashboardParamGridModel = parametersList[position]

        holder.imgParam.setImageResource(parameter.imgId)
        holder.txtParamName.text = parameter.parameterName
        holder.txtParamValue.text = parameter.parameterValue

        holder.txtParamValue.setTextColor(ContextCompat.getColor(mContext, parameter.colorId))
        //holder.layoutParameter.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white))
        //ImageViewCompat.setImageTintList(holder.imgParam, ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.dark_gold)))

        holder.layoutParameter.setOnClickListener {
            listener.onSelection(parameter)
        }

    }

    fun updateData(list: List<DashboardParamGridModel>) {
        Utilities.printData("DashboardList", list.toMutableList())
        this.parametersList.clear()
        this.parametersList.addAll(list)
        this.notifyDataSetChanged()
        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.hideProgressBar()
        }, 1000)
    }

    interface ParameterSelectionListener {
        fun onSelection(paramGridModel: DashboardParamGridModel)
    }

    inner class DashboardTrackParamGridViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemTrackparamDashboardGridBinding.bind(view)
        var layoutParameter = binding.layoutParameter
        var imgParam = binding.imgParam
        var txtParamName = binding.txtParamName
        var txtParamValue = binding.txtParamValue
    }

}
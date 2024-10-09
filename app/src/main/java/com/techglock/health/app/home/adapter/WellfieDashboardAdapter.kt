package com.techglock.health.app.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.databinding.ItemWellfieDashboardBinding
import com.techglock.health.app.home.common.DataHandler
import com.techglock.health.app.home.common.DataHandler.WellfieResultModel

class WellfieDashboardAdapter(val mContext: Context) :
    RecyclerView.Adapter<WellfieDashboardAdapter.WellfieDashboardViewHolder>() {

    private var parametersList: ArrayList<WellfieResultModel> = arrayListOf()

    override fun getItemCount(): Int = parametersList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WellfieDashboardViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wellfie_dashboard, parent, false)
        return WellfieDashboardViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WellfieDashboardViewHolder, position: Int) {
        try {
            val parameter: WellfieResultModel = parametersList[position]
            holder.imgParam.setImageResource(
                DataHandler(mContext).getWellfieDashboardParameterImageByCode(
                    parameter.paramCode
                )
            )
            //holder.txtParamName.text = parameter.paramName
            holder.txtParamName.text =
                DataHandler(mContext).getWellfieParametersConverted(parameter.paramCode)

            when (parameter.paramCode) {
                "HEART_RATE" -> {
                    holder.txtParamValue.text = "${parameter.paramValue} BPM"
                }

                "BREATHING_RATE" -> {
                    holder.txtParamValue.text = "${parameter.paramValue} RPM"
                }

                "BLOOD_OXYGEN" -> {
                    holder.txtParamValue.text = "${parameter.paramValue} %"
                }

                else -> {
                    holder.txtParamValue.text = parameter.paramValue
                }
            }
            if (parameter.paramCode == "STRESS_INDEX") {
                holder.txtParamValue.text = parameter.paramObs
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateData(list: List<WellfieResultModel>) {
        val abc = list.filter { it.paramCode != "BLOOD_OXYGEN" && it.paramCode != "STRESS_INDEX" }
        for (i in abc) {
            when (i.paramCode) {
                "HEART_RATE" -> i.paramId = 1
                "BREATHING_RATE" -> i.paramId = 2
                "BMI" -> i.paramId = 3
                "BP" -> i.paramId = 4
            }
        }
        this.parametersList.clear()
        this.parametersList.addAll(abc.sortedBy { it.paramId })
        //Utilities.printData("WellfieResultList",parametersList.toMutableList())
        this.notifyDataSetChanged()
    }

    inner class WellfieDashboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemWellfieDashboardBinding.bind(view)
        var layoutParameter = binding.layoutWellfieParameter
        var imgParam = binding.imgParameter
        var txtParamName = binding.txtParameter
        var txtParamValue = binding.txtParameterValue
    }

}
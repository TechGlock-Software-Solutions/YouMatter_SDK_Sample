package com.techglock.health.app.track_parameter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.CalculateParameters
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ItemParamResultBinding
import com.techglock.health.app.model.entity.TrackParameterMaster
import com.techglock.health.app.track_parameter.util.TrackParameterHelper

class RevSavedParamAdapter(val context: Context) :
    RecyclerView.Adapter<RevSavedParamAdapter.SavedParamViewHolder>() {

    private val dataList: MutableList<TrackParameterMaster.History> = mutableListOf()
    var selectedPosition: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SavedParamViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_param_result, parent, false)
    )

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: SavedParamViewHolder, position: Int) =
        holder.bindTo(dataList[position])

    fun updateData(items: List<TrackParameterMaster.History>) {

        dataList.clear()
        if (items.size > 0 && items[0].profileCode.equals("BLOODPRESSURE", true)) {
            dataList.addAll(updateBloodPressureObservation(items))
        } else if (items.size > 0 && items[0].profileCode.equals("WHR", true)) {
            dataList.addAll(updateWHRSequence(items))
        } else {
            dataList.addAll(items)
        }
        Utilities.printLog("Inside updateData " + dataList.size)
        //Utilities.printLog("Data " + dataList)

        this.notifyDataSetChanged()
    }

    fun updateSelection(pos: Int) {
        selectedPosition = pos
        this.notifyDataSetChanged()
    }

    private fun updateWHRSequence(dataList: List<TrackParameterMaster.History>): Collection<TrackParameterMaster.History> {
        val list: MutableList<TrackParameterMaster.History> = mutableListOf()
        var whr: TrackParameterMaster.History? = null
        var hip: TrackParameterMaster.History? = null
        var waist: TrackParameterMaster.History? = null

        try {


            if (dataList.isNotEmpty()) {
                for (item in dataList) {
                    if (item.parameterCode.equals("WHR", true)) {
                        whr = item
                    }
                    if (item.parameterCode.equals("HIP", true)) {
                        hip = item
                    }
                    if (item.parameterCode.equals("WAIST", true)) {
                        waist = item
                    }
                }
                list.add(whr!!)
                list.add(waist!!)
                list.add(hip!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun updateBloodPressureObservation(bpList: List<TrackParameterMaster.History>): List<TrackParameterMaster.History> {
        var systolic = 0
        var diastolic = 0
        val list: MutableList<TrackParameterMaster.History> = mutableListOf()
        lateinit var sys: TrackParameterMaster.History
        lateinit var dia: TrackParameterMaster.History
        lateinit var pulse: TrackParameterMaster.History
        for (item in bpList) {
            when (item.parameterCode) {
                "BP_SYS" -> {
                    sys = item
                    systolic = item.value!!.toInt()
                }

                "BP_DIA" -> {
                    dia = item
                    diastolic = item.value!!.toInt()
                }

                else -> {
                    pulse = item
                }
            }
        }
        if (bpList.isNotEmpty() && bpList.size > 1) {
            list.add(sys)
            list.add(dia)
            if (bpList.size > 2)
                list.add(pulse)
        }
        val observation = CalculateParameters.getBPObservation(systolic, diastolic, context)
        for (item in list) {
            if (item.parameterCode != "BP_PULSE") {
                item.observation = observation
                item.textValue = "$systolic/$diastolic"
            }
        }
        return list
    }

    inner class SavedParamViewHolder(parent: View) : RecyclerView.ViewHolder(parent) {
        private val binding = ItemParamResultBinding.bind(parent)

        fun bindTo(item: TrackParameterMaster.History) {
            var color: Int = R.color.almost_black

            if (!item.description.isNullOrEmpty()) {
                binding.txtParamTitle.text = item.description
            } else {
                binding.txtParamTitle.text = "- -"
            }
            binding.txtParamValue.text = item.value.toString()
            if (!item.unit.isNullOrEmpty()) {
                binding.txtParamUnit.text = item.unit
            } else {
                binding.txtParamUnit.text = ""
            }
            try {
                Utilities.printLog("ColorCode=> " + item.parameterCode + " :: " + item.observation)
                Utilities.printLog(
                    "ColorCode=> " + TrackParameterHelper.getObservationColor(
                        item.observation!!,
                        item.profileCode!!
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (!item.observation.isNullOrEmpty()) {
                color =
                    TrackParameterHelper.getObservationColor(item.observation!!, item.profileCode!!)
                binding.txtParamValue.setTextColor(
                    binding.txtParamValue.context.resources.getColor(
                        color
                    )
                )
                binding.txtParamUnit.setTextColor(
                    binding.txtParamValue.context.resources.getColor(
                        color
                    )
                )
            } else {
                binding.txtParamValue.setTextColor(
                    binding.txtParamValue.context.resources.getColor(
                        color
                    )
                )
                binding.txtParamUnit.setTextColor(
                    binding.txtParamValue.context.resources.getColor(
                        color
                    )
                )
            }

            if (!item.profileCode.equals("BMI", true) && !item.profileCode.equals(
                    "BLOODPRESSURE",
                    true
                ) && !item.profileCode.equals("WHR", true)
            ) {

                if (adapterPosition == selectedPosition) {
                    binding.layoutParam.background =
                        ContextCompat.getDrawable(context, R.drawable.btn_fill_purple_light)
                } else {
                    binding.layoutParam.background =
                        ContextCompat.getDrawable(context, R.drawable.btn_orange_light)
                }
            } else {
                binding.layoutParam.background =
                    ContextCompat.getDrawable(context, R.drawable.btn_orange_light)
            }

            if (!item.profileCode.isNullOrEmpty()) {
                binding.imgParam.setImageResource(
                    TrackParameterHelper.getParameterImageByProfileCode(
                        item.profileCode!!,
                        item.parameterCode
                    )
                )
            }

            binding.layoutParam.setOnClickListener {

                onItemClickListener?.let { click ->

                    if (!item.profileCode.equals("BMI", true)
                        && !item.profileCode.equals("BLOODPRESSURE", true)
                        && !item.profileCode.equals("WHR", true)
                    ) {
                        selectedPosition = adapterPosition
                        click(item)
                    }

                }

            }

        }

    }

    //add these
    private var onItemClickListener: ((TrackParameterMaster.History) -> Unit)? = null

    fun setOnItemClickListener(listener: (TrackParameterMaster.History) -> Unit) {
        onItemClickListener = listener
    }
}
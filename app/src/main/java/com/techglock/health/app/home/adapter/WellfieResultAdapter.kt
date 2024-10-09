package com.techglock.health.app.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ItemWellfieResultBinding
import com.techglock.health.app.home.common.DataHandler
import com.techglock.health.app.home.common.DataHandler.WellfieResultModel

class WellfieResultAdapter(
    val mContext: Context,
    val from: String,
    private val listener: OnWellfieParameterClickListener
) : RecyclerView.Adapter<WellfieResultAdapter.WellfieResultViewHolder>() {

    //private val dataHandler = DataHandler(mContext)
    private var parametersList: ArrayList<WellfieResultModel> = arrayListOf()

    override fun getItemCount(): Int = parametersList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WellfieResultViewHolder {
        val v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_wellfie_result, parent, false)
        return WellfieResultViewHolder(v)
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n", "Range")
    override fun onBindViewHolder(holder: WellfieResultViewHolder, position: Int) {
        try {
            val parameter: WellfieResultModel = parametersList[position]

            holder.imgParam.setImageResource(Utilities.getWellfieParameterImageByCode(parameter.paramCode))
            //holder.txtParamName.text = parameter.paramName
            holder.txtParamName.text =
                DataHandler(mContext).getWellfieParametersConverted(parameter.paramCode)

            if (!Utilities.isNullOrEmpty(parameter.paramColor)) {
                holder.txtParamValue.setTextColor(Color.parseColor(parameter.paramColor))
                holder.txtParamObs.setTextColor(Color.parseColor(parameter.paramColor))
            }
            /*            if ( parameter.paramCode == "BP" ) {
                            holder.txtParamValue.setTextColor(Color.parseColor(Utilities.getWellfieBpColorByObservation(parameter.paramObs!!)))
                            holder.txtParamObs.setTextColor(Color.parseColor(Utilities.getWellfieBpColorByObservation(parameter.paramObs!!)))
                        }*/

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

            if (!Utilities.isNullOrEmpty(parameter.paramObs)) {
                holder.txtParamObs.text = parameter.paramObs
                holder.txtParamObs.visibility = View.VISIBLE
            } else {
                holder.txtParamObs.visibility = View.GONE
            }

            if (parameter.paramCode == "STRESS_INDEX") {
                holder.txtParamName.text =
                    DataHandler(mContext).getWellfieParametersConverted(parameter.paramCode)
                holder.txtParamValue.text =
                    Utilities.convertStringToPascalCase(parameter.paramObs!!)
                holder.txtParamObs.text = ""
            }

            /*            if ( parameter.paramCode == "STRESS_INDEX" ) {
                            holder.txtParamName.text = "Stress"
                            holder.txtParamValue.visibility = View.GONE
                            holder.txtParamObs.visibility = View.VISIBLE
                        }*/

            if (Utilities.isNullOrEmpty(from) && !Utilities.isNullOrEmpty(parameter.paramToolTip)) {
                holder.layoutParameter.setOnClickListener {
                    //Utilities.printLogError("Tooltips--->${parameter.paramToolTip}")
                    listener.onWellfieParameterClick(parameter)
                }
                holder.imgInfo.visibility = View.VISIBLE
            } else {
                holder.imgInfo.visibility = View.GONE
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateData(list: List<WellfieResultModel>) {
        for (i in list) {
            when (i.paramCode) {
                "BP" -> i.paramId = 1
                "STRESS_INDEX" -> i.paramId = 1
                "HEART_RATE" -> i.paramId = 3
                "BREATHING_RATE" -> i.paramId = 4
                "BLOOD_OXYGEN" -> i.paramId = 5
                "BMI" -> i.paramId = 6
            }
        }
        this.parametersList.clear()
        this.parametersList.addAll(list.sortedBy { it.paramId })
        //Utilities.printData("WellfieResultList", parametersList.toMutableList())
        this.notifyDataSetChanged()
    }

    interface OnWellfieParameterClickListener {
        fun onWellfieParameterClick(wellfieResultModel: WellfieResultModel)
    }

    inner class WellfieResultViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemWellfieResultBinding.bind(view)
        var layoutParameter = binding.layoutParameter
        var imgInfo = binding.imgInfo
        var imgParam = binding.imgParam
        var txtParamName = binding.txtParamName
        var txtParamValue = binding.txtParamValue
        var txtParamObs = binding.txtParamObs
    }

}

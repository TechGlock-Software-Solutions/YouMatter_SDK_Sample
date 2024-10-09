package com.techglock.health.app.medication_tracker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.databinding.ItemMedFrequencyBinding
import com.techglock.health.app.medication_tracker.model.FrequencyModel


class MedFreuencyAdapter(
    val context: Context, private var medFreuencyList: ArrayList<FrequencyModel>,
    val listener: OnMedFrequencyListener
) :
    RecyclerView.Adapter<MedFreuencyAdapter.MedFreuencyViewHolder>() {

    private var selectedPos = 0
    private val appColorHelper = AppColorHelper.instance!!

    override fun getItemCount(): Int = medFreuencyList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedFreuencyViewHolder =
        MedFreuencyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_med_frequency, parent, false)
        )

    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(holder: MedFreuencyAdapter.MedFreuencyViewHolder, position: Int) {
        val medFrequency = medFreuencyList[position]

        holder.txtFrequency.text = medFrequency.title

        if (selectedPos == position) {
            listener.onMedFrequencySelection(medFrequency, position)
            holder.layoutFreq.background =
                ContextCompat.getDrawable(context, R.drawable.btn_fill_pressed)
            //holder.layoutFreq.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(ContextCompat.getColor(context,R.color.dai_ichi_dark_gold), BlendModeCompat.SRC_ATOP)
            holder.txtFrequency.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            holder.layoutFreq.background =
                ContextCompat.getDrawable(context, R.drawable.btn_fill_disabled)
            holder.layoutFreq.background.colorFilter =
                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    ContextCompat.getColor(
                        context,
                        R.color.tab_bg
                    ), BlendModeCompat.SRC_ATOP
                )
            holder.txtFrequency.setTextColor(context.resources.getColor(R.color.tab_text))
        }

        holder.itemView.setOnClickListener {
            listener.onMedFrequencySelection(medFrequency, position)
            notifyItemChanged(selectedPos)
            selectedPos = position
            notifyItemChanged(selectedPos)
        }

    }

    fun updateSelectedPos(selectedPos: Int) {
        this.selectedPos = selectedPos
        notifyDataSetChanged()
    }

    interface OnMedFrequencyListener {
        fun onMedFrequencySelection(medFreq: FrequencyModel, position: Int)
    }

    inner class MedFreuencyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemMedFrequencyBinding.bind(view)
        val txtFrequency = binding.txtFrequency
        val layoutFreq = binding.layoutFreq
    }

}
package com.techglock.health.app.home.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.databinding.ItemRiskoMeterOptionBinding
import com.techglock.health.app.model.nimeya.GetRiskoMeterModel

class RiskoMeterOptionAdapter(
    val quesCode: String,
    val context: Context,
    private val listener: OnRiskoMeterAnswerListener
) : RecyclerView.Adapter<RiskoMeterOptionAdapter.RiskoMeterOptionViewHolder>() {

    val riskoMeterOptionList: MutableList<GetRiskoMeterModel.Answer> = mutableListOf()

    override fun getItemCount(): Int = riskoMeterOptionList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiskoMeterOptionViewHolder =
        RiskoMeterOptionViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_risko_meter_option, parent, false)
        )

    override fun onBindViewHolder(holder: RiskoMeterOptionViewHolder, position: Int) {
        val riskoMeterOption = riskoMeterOptionList[position]
        holder.txtOption.tag = riskoMeterOption.id
        holder.txtOption.text = riskoMeterOption.answer
        if (riskoMeterOption.isSelected) {
            holder.imgCheck.setImageResource(R.drawable.ic_radio_button_checked)
            ImageViewCompat.setImageTintList(
                holder.imgCheck,
                ColorStateList.valueOf(context.resources.getColor(R.color.colorPrimary))
            )
        } else {
            holder.imgCheck.setImageResource(R.drawable.ic_radio_button_unchecked)
            ImageViewCompat.setImageTintList(
                holder.imgCheck,
                ColorStateList.valueOf(context.resources.getColor(R.color.vivant_greyish))
            )
        }

        holder.layoutOption.setOnClickListener {
            listener.onRiskoMeterAnswerClick(position, quesCode, riskoMeterOption)
        }
    }

    fun updateList(items: List<GetRiskoMeterModel.Answer>) {
        riskoMeterOptionList.clear()
        riskoMeterOptionList.addAll(items)
        notifyDataSetChanged()
    }

    fun refresh() {
        notifyDataSetChanged()
    }

    interface OnRiskoMeterAnswerListener {
        fun onRiskoMeterAnswerClick(
            position: Int,
            quesCode: String,
            answer: GetRiskoMeterModel.Answer
        )
    }

    inner class RiskoMeterOptionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemRiskoMeterOptionBinding.bind(view)
        val layoutOption = binding.layoutOption
        val imgCheck = binding.imgCheck
        val txtOption = binding.txtOption
    }

}
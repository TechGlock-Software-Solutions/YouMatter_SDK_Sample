package com.techglock.health.app.medication_tracker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.databinding.ItemMealTimeBinding
import com.techglock.health.app.medication_tracker.model.InstructionModel


class MealTimeAdapter(
    val context: Context, private var mealTimeList: List<InstructionModel>,
    val listener: OnInstructionClickListener
) : RecyclerView.Adapter<MealTimeAdapter.MealTimeViewHolder>() {

    private var selectedPos = -1
    private val appColorHelper = AppColorHelper.instance!!

    override fun getItemCount(): Int = mealTimeList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealTimeViewHolder =
        MealTimeViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_meal_time, parent, false)
        )

    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(holder: MealTimeViewHolder, position: Int) {
        val instruction = mealTimeList[position]
        holder.bindTo(instruction)

        /*        if (selectedPos == position) {
                    listener.onInstructionSelection(position, instruction)
                    holder.imgSelect.setImageResource(R.drawable.ic_radio_active)
                } else {
                    holder.imgSelect.setImageResource(R.drawable.ic_radio_inactive)
                }*/

        if (selectedPos == -1) {
            holder.layoutMeal.background =
                ContextCompat.getDrawable(context, R.drawable.bg_transparant)
        } else {
            if (selectedPos == position) {
                listener.onInstructionSelection(position, instruction)
                holder.layoutMeal.background =
                    ContextCompat.getDrawable(context, R.drawable.border_button_selected)
                holder.imgSelect.visibility = View.VISIBLE
                //holder.imgSelect.setImageResource(R.drawable.ic_radio_active)
            } else {
                holder.layoutMeal.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_transparant)
                holder.imgSelect.visibility = View.INVISIBLE
                //holder.imgSelect.setImageResource(R.drawable.ic_radio_inactive)
            }
        }

        holder.itemView.setOnClickListener {
            listener.onInstructionSelection(position, instruction)
            notifyItemChanged(selectedPos)
            selectedPos = position
            //notifyItemChanged(selectedPos)
            notifyDataSetChanged()
        }

    }

    fun updateSelectedPos(selectedPos: Int) {
        this.selectedPos = selectedPos
        notifyDataSetChanged()
    }

    interface OnInstructionClickListener {
        fun onInstructionSelection(position: Int, instruction: InstructionModel)
    }

    inner class MealTimeViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemMealTimeBinding.bind(view)

        //val txtMeal = binding.txtMeal
        //val imgMeal = binding.imgMeal
        val imgSelect = binding.imgSelect
        val layoutMeal = binding.layoutMeal

        fun bindTo(instruction: InstructionModel) {
            binding.imgMeal.setImageResource(instruction.imageId ?: 0)
            binding.txtMeal.text = instruction.title ?: ""
        }
    }

}
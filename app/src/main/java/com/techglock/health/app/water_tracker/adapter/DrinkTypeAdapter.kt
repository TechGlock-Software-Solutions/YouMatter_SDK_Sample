package com.techglock.health.app.water_tracker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ItemDrinkTypeBinding
import com.techglock.health.app.water_tracker.model.DrinkTypeModel

class DrinkTypeAdapter(
    val context: Context, private var drinkTypeList: List<DrinkTypeModel>,
    val listener: OnDrinkTypeListener
) : RecyclerView.Adapter<DrinkTypeAdapter.MedicineTypeViewHolder>() {

    //private var selectedPos = -1
    private var selectedPos = 0

    override fun getItemCount(): Int = drinkTypeList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineTypeViewHolder =
        MedicineTypeViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_drink_type, parent, false)
        )

    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(holder: MedicineTypeViewHolder, position: Int) {
        val drinkType = drinkTypeList[position]
        holder.imgDrinkType.setImageResource(drinkType.drinkTypeImageId)
        holder.txtDrinkType.text = drinkType.drinkTypeTitle
        Utilities.printLogError("selectedPos----->$selectedPos")

        if (selectedPos == -1) {
            holder.layoutContainer.background =
                ContextCompat.getDrawable(context, R.drawable.bg_transparant)
        } else {
            if (selectedPos == position) {
                listener.onDrinkTypeSelection(position, drinkType)
                holder.layoutContainer.background =
                    ContextCompat.getDrawable(context, R.drawable.border_button_selected_blue)
                holder.imgSelect.visibility = View.VISIBLE
                //holder.imgSelect.setImageResource(R.drawable.ic_radio_active)
            } else {
                holder.layoutContainer.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_transparant)
                holder.imgSelect.visibility = View.INVISIBLE
                //holder.imgSelect.setImageResource(R.drawable.ic_radio_inactive)
            }
        }

        holder.itemView.setOnClickListener {
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

    interface OnDrinkTypeListener {
        fun onDrinkTypeSelection(position: Int, drinkType: DrinkTypeModel)
    }

    inner class MedicineTypeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemDrinkTypeBinding.bind(view)
        val imgSelect = binding.imgSelect
        val imgDrinkType = binding.imgDrinkType
        val txtDrinkType = binding.txtDrinkType
        val layoutContainer = binding.layoutContainer
    }

}
package com.techglock.health.app.water_tracker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.databinding.ItemDrinkQuantityBinding
import com.techglock.health.app.water_tracker.common.WaterTrackerHelper
import com.techglock.health.app.water_tracker.model.DrinkQuantityModel

class DrinkQuantityAdapter(
    private val waterTrackerHelper: WaterTrackerHelper,
    val context: Context,
    val listener: OnDrinkQuantityListener
) : RecyclerView.Adapter<DrinkQuantityAdapter.MedicineTypeViewHolder>() {

    private var selectedPos = 0
    private var drinkQuantityList: MutableList<DrinkQuantityModel> = mutableListOf()

    override fun getItemCount(): Int = drinkQuantityList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineTypeViewHolder =
        MedicineTypeViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_drink_quantity, parent, false)
        )

    @SuppressLint("RecyclerView", "SetTextI18n")
    override fun onBindViewHolder(holder: MedicineTypeViewHolder, position: Int) {
        val medType = drinkQuantityList[position]
        holder.binding.txtQuantity.text = "${medType.quantity} ${medType.unit}"

        if (selectedPos == position) {
            listener.onDrinkQuantitySelection(position, medType)
            holder.layoutContainer.background =
                ContextCompat.getDrawable(context, R.drawable.btn_fill_selected_blue)
            holder.txtQuantity.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            holder.layoutContainer.background =
                ContextCompat.getDrawable(context, R.drawable.border_button_selected_blue)
            holder.txtQuantity.setTextColor(ContextCompat.getColor(context, R.color.primary_blue))
        }

        if (medType.isCustom) {
            holder.imgRemove.visibility = View.VISIBLE
        } else {
            holder.imgRemove.visibility = View.INVISIBLE
        }

        holder.itemView.setOnClickListener {
            notifyItemChanged(selectedPos)
            selectedPos = position
            //notifyItemChanged(selectedPos)
            notifyDataSetChanged()
        }

        holder.imgRemove.setOnClickListener {
            removeCustomDrinkQuantity()
        }

    }

    fun updateList(list: MutableList<DrinkQuantityModel>) {
        this.drinkQuantityList.clear()
        this.drinkQuantityList.addAll(list)
        this.notifyDataSetChanged()
    }

    fun addCustomDrinkQuantity(drinkQuantity: String) {
        this.drinkQuantityList.clear()
        drinkQuantityList.add(
            DrinkQuantityModel(
                quantity = drinkQuantity,
                isCustom = true
            )
        )
        this.drinkQuantityList.addAll(waterTrackerHelper.getDrinkQuantityList())
        this.selectedPos = 0
        this.notifyDataSetChanged()
    }

    private fun removeCustomDrinkQuantity() {
        drinkQuantityList.removeAt(0)
        notifyItemRemoved(0)
        this.notifyDataSetChanged()
    }

    fun updateSelectedPos(selectedPos: Int) {
        this.selectedPos = selectedPos
        this.notifyDataSetChanged()
    }

    interface OnDrinkQuantityListener {
        fun onDrinkQuantitySelection(position: Int, quantity: DrinkQuantityModel)
    }

    inner class MedicineTypeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemDrinkQuantityBinding.bind(view)
        val imgRemove = binding.imgRemove
        val layoutContainer = binding.layoutContainer
        val txtQuantity = binding.txtQuantity
    }

}

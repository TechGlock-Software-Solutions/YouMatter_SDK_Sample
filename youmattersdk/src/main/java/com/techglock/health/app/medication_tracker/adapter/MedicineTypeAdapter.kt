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
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ItemMedicineTypeBinding
import com.techglock.health.app.medication_tracker.model.MedTypeModel

class MedicineTypeAdapter(
    val context: Context, private var medTypeList: List<MedTypeModel>,
    val listener: OnMedTypeListener
) : RecyclerView.Adapter<MedicineTypeAdapter.MedicineTypeViewHolder>() {

    private var selectedPos = -1
    private val appColorHelper = AppColorHelper.instance!!

    override fun getItemCount(): Int = medTypeList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineTypeViewHolder =
        MedicineTypeViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_medicine_type, parent, false)
        )

    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(holder: MedicineTypeViewHolder, position: Int) {
        val medType = medTypeList[position]
        Utilities.printLogError("selectedPos----->$selectedPos")
        holder.imgMedType.setImageResource(medType.medTypeImageId)
        holder.txtMedType.text = medType.medTypeTitle

        if (selectedPos == -1) {
            holder.layoutContainer.background =
                ContextCompat.getDrawable(context, R.drawable.bg_transparant)
        } else {
            if (selectedPos == position) {
                listener.onMedTypeSelection(position, medType)
                holder.layoutContainer.background =
                    ContextCompat.getDrawable(context, R.drawable.border_button_selected)
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

    interface OnMedTypeListener {
        fun onMedTypeSelection(position: Int, medType: MedTypeModel)
    }

    inner class MedicineTypeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemMedicineTypeBinding.bind(view)
        val imgSelect = binding.imgSelect
        val imgMedType = binding.imgMedType
        val txtMedType = binding.txtMedType
        val layoutContainer = binding.layoutContainer
    }

}
package com.techglock.health.app.security.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.databinding.ItemGenderSignUpBinding
import com.techglock.health.app.security.model.GenderModel

class GenderSelectionAdapter(
    val context: Context,
    val listener: OnGenderListener
) : RecyclerView.Adapter<GenderSelectionAdapter.GenderSelectionViewHolder>() {

    private var selectedPos = -1
    private var dataList: MutableList<GenderModel> = mutableListOf()

    override fun getItemCount(): Int = dataList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenderSelectionViewHolder =
        GenderSelectionViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_gender_sign_up, parent, false)
        )

    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(holder: GenderSelectionViewHolder, position: Int) {
        val item = dataList[position]
        holder.imgGender.setImageResource(item.image)
        holder.txtGender.text = item.title

        if (selectedPos == -1) {
            holder.layoutGender.background =
                ContextCompat.getDrawable(context, R.drawable.border_button_white)
            holder.txtGender.setTextColor(ContextCompat.getColor(context, R.color.almost_black))
            holder.imgGender.setColorFilter(ContextCompat.getColor(context, R.color.almost_black))
        } else {
            if (selectedPos == position) {
                listener.onGenderSelection(item)
                holder.layoutGender.background =
                    ContextCompat.getDrawable(context, R.drawable.btn_fill_selected)
                holder.txtGender.setTextColor(ContextCompat.getColor(context, R.color.white))
                holder.imgGender.setColorFilter(ContextCompat.getColor(context, R.color.white))
            } else {
                holder.layoutGender.background =
                    ContextCompat.getDrawable(context, R.drawable.border_button_white)
                holder.txtGender.setTextColor(ContextCompat.getColor(context, R.color.almost_black))
                holder.imgGender.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        R.color.almost_black
                    )
                )
            }
        }

        holder.layoutGender.setOnClickListener {
            notifyItemChanged(selectedPos)
            selectedPos = position
            notifyDataSetChanged()
        }
    }

    fun updateList(items: MutableList<GenderModel>) {
        dataList.clear()
        dataList.addAll(items)
        for (i in dataList.indices) {
            if (dataList[i].isChecked) {
                selectedPos = i
            }
        }
        this.notifyDataSetChanged()
    }

    interface OnGenderListener {
        fun onGenderSelection(item: GenderModel)
    }

    inner class GenderSelectionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemGenderSignUpBinding.bind(view)
        val layoutGender = binding.layoutGender
        val imgGender = binding.imgGender
        val txtGender = binding.txtGender
    }

}
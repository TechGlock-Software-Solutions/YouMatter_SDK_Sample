package com.techglock.health.app.security.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.databinding.ItemEmployerBinding
import com.techglock.health.app.security.model.EmployerModel

class EmployerSelectionAdapter(
    val context: Context,
    val listener: OnEmployerListener
) : RecyclerView.Adapter<EmployerSelectionAdapter.EmployerSelectionViewHolder>() {

    private var selectedPos = -1
    private var dataList: MutableList<EmployerModel> = mutableListOf()

    override fun getItemCount(): Int = dataList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployerSelectionViewHolder =
        EmployerSelectionViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_employer, parent, false)
        )

    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(holder: EmployerSelectionViewHolder, position: Int) {
        val item = dataList[position]
        holder.txtEmployer.text = item.employertitle

        if (selectedPos == -1) {
            holder.layoutEmployer.background =
                ContextCompat.getDrawable(context, R.drawable.border_button_white)
            holder.imgSelect.setImageResource(R.drawable.ic_radio_inactive)
            holder.imgSelect.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary))
            holder.txtEmployer.setTextColor(ContextCompat.getColor(context, R.color.textViewColor))
        } else {
            if (selectedPos == position) {
                listener.onEmployerSelection(item)
                holder.layoutEmployer.background =
                    ContextCompat.getDrawable(context, R.drawable.btn_fill_selected)
                holder.imgSelect.setImageResource(R.drawable.ic_radio_active)
                holder.imgSelect.setColorFilter(ContextCompat.getColor(context, R.color.white))
                holder.txtEmployer.setTextColor(ContextCompat.getColor(context, R.color.white))
            } else {
                holder.layoutEmployer.background =
                    ContextCompat.getDrawable(context, R.drawable.border_button_white)
                holder.imgSelect.setImageResource(R.drawable.ic_radio_inactive)
                holder.imgSelect.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        R.color.colorPrimary
                    )
                )
                holder.txtEmployer.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.textViewColor
                    )
                )
            }
        }

        holder.layoutEmployer.setOnClickListener {
            notifyItemChanged(selectedPos)
            selectedPos = position
            notifyDataSetChanged()
        }
    }

    fun updateList(items: MutableList<EmployerModel>) {
        dataList.clear()
        dataList.addAll(items)
        this.notifyDataSetChanged()
    }

    interface OnEmployerListener {
        fun onEmployerSelection(item: EmployerModel)
    }

    inner class EmployerSelectionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemEmployerBinding.bind(view)
        val layoutEmployer = binding.layoutEmployer
        val imgSelect = binding.imgSelect
        val txtEmployer = binding.txtEmployer
    }

}
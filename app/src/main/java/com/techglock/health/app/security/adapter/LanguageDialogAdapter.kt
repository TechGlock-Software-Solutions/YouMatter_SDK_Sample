package com.techglock.health.app.security.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.databinding.ItemLanguageDialogBinding
import com.techglock.health.app.model.home.LanguageModel

class LanguageDialogAdapter(
    val context: Context,
    val listener: OnLanguageItemClickListener
) : RecyclerView.Adapter<LanguageDialogAdapter.ItemLanguageDialogViewHolder>() {

    private var selectedPos = -1
    private var dataList: MutableList<LanguageModel> = mutableListOf()

    override fun getItemCount(): Int = dataList.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemLanguageDialogViewHolder = ItemLanguageDialogViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_language_dialog, parent, false)
    )

    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(
        holder: LanguageDialogAdapter.ItemLanguageDialogViewHolder,
        position: Int
    ) {
        val item = dataList[position]
        holder.imgLanguage.setImageResource(item.languageImage)
        holder.txtLanguage.text = item.language
        holder.imgLanguage.setColorFilter(ContextCompat.getColor(context, item.languageColor))

        if (selectedPos == position || item.selectionStatus) {
            holder.imgSelect.visibility = View.VISIBLE
            holder.layoutLanguage.background = ContextCompat.getDrawable(
                holder.layoutLanguage.context,
                R.drawable.btn_border_selected_primary
            )
            /*            holder.layoutLanguage.background = ContextCompat.getDrawable(holder.layoutLanguage.context,R.drawable.btn_fill_selected)
                        holder.imgSelect.setImageResource(R.drawable.ic_radio_active)
                        holder.imgSelect.setColorFilter(ContextCompat.getColor(context,R.color.white))
                        holder.txtLanguage.setTextColor(ContextCompat.getColor(context,R.color.white))*/
        } else {
            holder.imgSelect.visibility = View.INVISIBLE
            holder.layoutLanguage.background =
                ContextCompat.getDrawable(holder.layoutLanguage.context, R.drawable.bg_transparant)
            /*            holder.layoutLanguage.background = ContextCompat.getDrawable(holder.layoutLanguage.context,R.drawable.border_button_white)
                        holder.imgSelect.setImageResource(R.drawable.ic_radio_inactive)
                        holder.imgSelect.setColorFilter(ContextCompat.getColor(context,R.color.colorPrimary))
                        holder.txtLanguage.setTextColor(ContextCompat.getColor(context,R.color.textViewColor))*/
        }

        holder.itemView.setOnClickListener {
            filterListForSelection()
            listener.onLanguageItemSelection(position, dataList[position])
            notifyItemChanged(selectedPos)
            selectedPos = position
            notifyDataSetChanged()
        }
    }

    private fun filterListForSelection() {
        for (item in dataList) {
            if (item.selectionStatus) {
                item.selectionStatus = false
            }
        }
    }

    fun updateList(items: List<LanguageModel>) {
        dataList.clear()
        dataList.addAll(items)
        notifyDataSetChanged()
    }

    interface OnLanguageItemClickListener {
        fun onLanguageItemSelection(position: Int, data: LanguageModel)
    }

    inner class ItemLanguageDialogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemLanguageDialogBinding.bind(view)
        val layoutLanguage = binding.layoutLanguage
        val imgSelect = binding.imgSelect
        val imgLanguage = binding.imgLanguage
        val txtLanguage = binding.txtLanguage
    }

}
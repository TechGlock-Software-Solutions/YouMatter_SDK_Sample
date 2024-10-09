package com.techglock.health.app.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.databinding.ItemLanguageNewBinding
import com.techglock.health.app.model.home.LanguageModel

class LanguageAdapter(
    val context: Context,
    val listener: OnItemClickListener
) : RecyclerView.Adapter<LanguageAdapter.ItemViewsHolder>() {

    private var selectedPos = -1
    private var dataList: MutableList<LanguageModel> = mutableListOf()

    override fun getItemCount(): Int = dataList.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LanguageAdapter.ItemViewsHolder = ItemViewsHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_language_new, parent, false)
    )

    @SuppressLint("RecyclerView", "SuspiciousIndentation", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ItemViewsHolder, position: Int) {
        val language = dataList[position]

        holder.imgLanguage.setImageResource(language.languageImage)
        holder.txtLanguage.text = language.language
        holder.imgLanguage.setColorFilter(ContextCompat.getColor(context, language.languageColor))

//        if ( selectedPos == -1 ) {
//            holder.layoutRelative.background = ContextCompat.getDrawable(holder.layoutRelative.context,R.drawable.border_button_white)
//        } else {
        if (selectedPos == position || language.selectionStatus) {
            holder.imgSelect.visibility = View.VISIBLE
            holder.layoutLanguage.background = ContextCompat.getDrawable(
                holder.layoutLanguage.context,
                R.drawable.btn_border_selected_primary
            )
            //holder.layoutLanguage.background = ContextCompat.getDrawable(holder.layoutLanguage.context,R.drawable.btn_fill_selected)
            //holder.imgSelect.setImageResource(R.drawable.ic_radio_active)
            //holder.imgSelect.setColorFilter(ContextCompat.getColor(context,R.color.white))
            //holder.imgLanguage.setColorFilter(ContextCompat.getColor(context,R.color.white))
            //holder.txtLanguage.setTextColor(ContextCompat.getColor(context,R.color.white))
        } else {
            holder.imgSelect.visibility = View.INVISIBLE
            holder.layoutLanguage.background =
                ContextCompat.getDrawable(holder.layoutLanguage.context, R.drawable.bg_transparant)
            //holder.layoutLanguage.background = ContextCompat.getDrawable(holder.layoutLanguage.context,R.drawable.border_button_white)
            //holder.imgSelect.setImageResource(R.drawable.ic_radio_inactive)
            //holder.imgSelect.setColorFilter(ContextCompat.getColor(context,R.color.colorPrimary))
            //holder.imgLanguage.setColorFilter(ContextCompat.getColor(context,language.languageColor))
            //holder.txtLanguage.setTextColor(ContextCompat.getColor(context,R.color.textViewColor))
        }
//        }

        holder.itemView.setOnClickListener {
            listener.onItemSelection(position, language)
            filterListForSelection()
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

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(items: MutableList<LanguageModel>) {
        dataList.clear()
        dataList.addAll(items)
        this.notifyDataSetChanged()
    }

    inner class ItemViewsHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemLanguageNewBinding.bind(view)
        val layoutLanguage = binding.layoutLanguage
        val imgSelect = binding.imgSelect
        val imgLanguage = binding.imgLanguage
        val txtLanguage = binding.txtLanguage
    }

    interface OnItemClickListener {
        fun onItemSelection(position: Int, data: LanguageModel)
    }

}
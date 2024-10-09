package com.techglock.health.app.security.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseAdapter
import com.techglock.health.app.common.base.BaseViewHolder
import com.techglock.health.app.databinding.ItemLanguageDialogBinding
import com.techglock.health.app.model.home.LanguageModel

class AdapterLanguageDialog(val dataList: ArrayList<LanguageModel>) : BaseAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding =
            DataBindingUtil.inflate<ItemLanguageDialogBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_language_dialog, parent, false
            )
        return BaseViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val binding = holder.binding as ItemLanguageDialogBinding
        val data = dataList[position]

        binding.imgLanguage.setImageResource(data.languageImage)
        binding.txtLanguage.text = data.language
        binding.imgLanguage.setColorFilter(
            ContextCompat.getColor(
                holder.itemView.context,
                data.languageColor
            )
        )

        if (data.selectionStatus) {
            binding.imgSelect.visibility = View.VISIBLE
            binding.layoutLanguage.background = ContextCompat.getDrawable(
                holder.itemView.context,
                R.drawable.btn_border_selected_primary
            )
        } else {
            binding.imgSelect.visibility = View.INVISIBLE
            binding.layoutLanguage.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_transparant)
        }

        binding.root.setOnClickListener {
            onItemClick(position)
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}
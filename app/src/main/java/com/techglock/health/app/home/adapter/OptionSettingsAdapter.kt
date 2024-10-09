package com.techglock.health.app.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.databinding.ItemOptionBinding
import com.techglock.health.app.home.common.DataHandler
import com.techglock.health.app.home.viewmodel.SettingsViewModel

class OptionSettingsAdapter(
    val viewModel: SettingsViewModel,
    val context: Context,
    val mSettingsOptionListener: SettingsOptionListener
) : RecyclerView.Adapter<OptionSettingsAdapter.OptionSettingsViewHolder>() {

    private val settingsOptionsList: MutableList<DataHandler.Option> = mutableListOf()
    private val appColorHelper = AppColorHelper.instance!!

    override fun getItemCount(): Int = settingsOptionsList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionSettingsViewHolder =
        OptionSettingsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_option, parent, false)
        )

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: OptionSettingsViewHolder, position: Int) {
        val settingsOption = settingsOptionsList[position]
        holder.bindTo(viewModel, settingsOption)

        holder.imgOption.setImageResource(settingsOption.imageId)
        holder.txtOption.text = settingsOption.title

        /*        if((position+1) == settingsOptionsList.size){
                    holder.view.visibility = View.INVISIBLE
                }*/
        /*        holder.itemView.setOnClickListener {
                    mSettingsOptionListener.OnSettingsOptionListener(position , settingsOption)
                }*/
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateDashboardOptionsList(items: List<DataHandler.Option>) {
        settingsOptionsList.clear()
        settingsOptionsList.addAll(items)
        notifyDataSetChanged()
    }

    inner class OptionSettingsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemOptionBinding.bind(view)
        val imgOption = binding.imgOption
        val txtOption = binding.txtOption
        val layout_option = binding.layoutOptionSetting

        fun bindTo(viewModel: SettingsViewModel, option: DataHandler.Option) {
            if (viewModel.isSelfUser()) {
                layout_option.isClickable = true
                layout_option.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_transparant)
                layout_option.setOnClickListener {
                    mSettingsOptionListener.onSettingsOptionListener(
                        adapterPosition,
                        settingsOptionsList[adapterPosition]
                    )
                }
            } else {
                layout_option.isClickable = false
                layout_option.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_disabled)
            }
        }

        /*        init {
                    view.setOnClickListener {
                        mSettingsOptionListener.onSettingsOptionListener(adapterPosition, settingsOptionsList[adapterPosition])
                    }
                }*/
    }

    interface SettingsOptionListener {
        fun onSettingsOptionListener(position: Int, option: DataHandler.Option)
    }

}
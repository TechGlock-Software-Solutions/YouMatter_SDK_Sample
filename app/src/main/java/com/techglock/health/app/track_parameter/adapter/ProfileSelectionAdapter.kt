package com.techglock.health.app.track_parameter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ItemProfileSelectionBinding
import com.techglock.health.app.model.parameter.ParameterListModel
import com.techglock.health.app.track_parameter.util.TrackParameterHelper

class ProfileSelectionAdapter(
    val mContext: Context,
    val listener: ProfileSelectionListener,
    private val showUrineProfile: Boolean
) : RecyclerView.Adapter<ProfileSelectionAdapter.ProfileSelectionViewHolder>() {

    private val dataList: MutableList<ParameterListModel.SelectedParameter> = mutableListOf()
    var selectedPosition: Int = 0

    private val appColorHelper = AppColorHelper.instance!!

    override fun getItemCount(): Int = dataList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProfileSelectionViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_profile_selection, parent, false)
    )

    override fun onBindViewHolder(holder: ProfileSelectionViewHolder, position: Int) {
        val item = dataList[position]
        holder.binding.txtSelectedProfile.text =
            holder.binding.txtSelectedProfile.context.getString(
                TrackParameterHelper.getProfileNameByProfileCode(item.profileCode)
            )
        holder.binding.imgSelectedProfile.setImageDrawable(
            ContextCompat.getDrawable(
                mContext,
                TrackParameterHelper.getProfileImageByProfileCode(
                    item.profileCode,
                    item.profileCode
                )
            )
        )


        if (item.profileCode == "BLOODPRESSURE") {
            holder.binding.txtSelectedProfile.text =
                mContext.resources.getText(R.string.BLOOD_PRESSURE2)
        }
        try {
            if (selectedPosition == position) {
                selectedPosition = position
                holder.binding.layoutContainer.background =
                    ContextCompat.getDrawable(mContext, R.drawable.btn_fill_blue)
                holder.binding.imgSelectedProfile.setColorFilter(
                    ContextCompat.getColor(
                        mContext,
                        R.color.white
                    ), android.graphics.PorterDuff.Mode.SRC_ATOP
                )
                holder.binding.txtSelectedProfile.setTextColor(
                    ContextCompat.getColor(
                        mContext,
                        R.color.white
                    )
                )
            } else {
                holder.binding.layoutContainer.background =
                    ContextCompat.getDrawable(mContext, R.drawable.bg_transparant)
                holder.binding.imgSelectedProfile.clearColorFilter()
                holder.binding.txtSelectedProfile.setTextColor(appColorHelper.textColor)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        holder.binding.layoutContainer.setOnClickListener {
            selectedPosition = position
            notifyItemChanged(selectedPosition)
            //notifyItemChanged(selectedPos)
            notifyDataSetChanged()
            listener.onProfileSelection(position, item)
        }

    }

    fun updateData(items: List<ParameterListModel.SelectedParameter>) {
        var filteredList = items
        if (!showUrineProfile) {
            filteredList = items.filter { it.profileCode != "URINE" }
        }
        dataList.clear()
        dataList.addAll(filteredList)
        Utilities.printLog("Inside updateData " + dataList.size)
        this.notifyDataSetChanged()
    }

    inner class ProfileSelectionViewHolder(parent: View) : RecyclerView.ViewHolder(parent) {

        val binding = ItemProfileSelectionBinding.bind(parent)

    }

    interface ProfileSelectionListener {
        fun onProfileSelection(position: Int, profile: ParameterListModel.SelectedParameter)
    }

}
package com.techglock.health.app.track_parameter.adapter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ItemSelectParametersBinding
import com.techglock.health.app.model.parameter.ParameterProfile
import com.techglock.health.app.track_parameter.util.TrackParameterHelper
import com.techglock.health.app.track_parameter.viewmodel.DashboardViewModel

class SelectParameterAdapter(
    private val mContext: Context,
    private val viewModel: DashboardViewModel
) : RecyclerView.Adapter<SelectParameterAdapter.SelectParameterViewHolder>() {

    val dataList: MutableList<ParameterProfile> = mutableListOf()
    private val appColorHelper = AppColorHelper.instance!!
    private val helper = TrackParameterHelper

    override fun getItemCount(): Int = dataList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SelectParameterViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_select_parameters, parent, false)
    )

    override fun onBindViewHolder(holder: SelectParameterViewHolder, position: Int) {
        val profile: ParameterProfile = dataList[position]

        holder.binding.imgProfile.setImageResource(
            helper.getProfileImageByProfileCode(
                profile.profileCode,
                profile.profileCode
            )
        )
        //holder.binding.txtProfile.text = profile.profileName
        holder.binding.txtProfile.text =
            mContext.getString(TrackParameterHelper.getProfileByProfileCode(profile.profileCode))

        if (profile.isSelection) {
            //holder.binding.layoutProfile.setBackgroundColor(appColorHelper.secondaryColor())
            holder.binding.layoutProfile.background =
                ContextCompat.getDrawable(mContext, R.drawable.btn_fill_blue)
            holder.binding.imgProfile.setColorFilter(
                ContextCompat.getColor(
                    mContext,
                    R.color.white
                ), android.graphics.PorterDuff.Mode.SRC_ATOP
            )
            holder.binding.txtProfile.setTextColor(ContextCompat.getColor(mContext, R.color.white))

        } else {
            //holder.binding.layoutProfile.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white))
            holder.binding.layoutProfile.background =
                ContextCompat.getDrawable(mContext, R.drawable.bg_transparant)
            holder.binding.imgProfile.clearColorFilter()
            holder.binding.txtProfile.setTextColor(appColorHelper.textColor)
        }

        holder.binding.layoutProfile.setOnClickListener {
            if (profile.isSelection) {
                updateSelection(position, false)
            } else {
                updateSelection(position, true)
            }
            viewModel.updateSelection(position)
        }

    }

    private fun updateSelection(position: Int, isSelected: Boolean) {
        dataList[position].isSelection = isSelected
        this.notifyItemChanged(position)
    }

    fun updateData(list: List<ParameterProfile>) {
        val filteredList = list.filter { it.profileCode != "URINE" }
        Utilities.printData("AllProfilesList", filteredList)
        this.dataList.clear()
        this.dataList.addAll(filteredList)
        this.notifyDataSetChanged()
        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.hideProgressBar()
        }, 1000)
    }

    fun getSelectedParameterList(): ArrayList<ParameterProfile> {
        val list: ArrayList<ParameterProfile> = arrayListOf()
        for (item in dataList) {
            if (item.isSelection) {
                list.add(item)
            }
        }
        return list
    }

    class SelectParameterViewHolder(parent: View) : RecyclerView.ViewHolder(parent) {

        val binding = ItemSelectParametersBinding.bind(parent)
    }
}
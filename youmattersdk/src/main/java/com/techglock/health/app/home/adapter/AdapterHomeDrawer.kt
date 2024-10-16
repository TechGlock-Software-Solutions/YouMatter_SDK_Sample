package com.techglock.health.app.home.adapter


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseAdapter
import com.techglock.health.app.common.base.BaseViewHolder
import com.techglock.health.app.databinding.AdapterDrawerLayoutBinding
import com.techglock.health.app.model.DrawerData


class AdapterHomeDrawer(
    private val drawerList: ArrayList<DrawerData>
) : BaseAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding =
            DataBindingUtil.inflate<AdapterDrawerLayoutBinding>(
                LayoutInflater.from(parent.context),
                R.layout.adapter_drawer_layout, parent, false
            )
        return BaseViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val binding = holder.binding as AdapterDrawerLayoutBinding
        val data = drawerList[position]

        binding.drawerItemTV.text = data.name
        binding.drawerItemTV.setCompoundDrawablesRelativeWithIntrinsicBounds(data.icon, 0, 0, 0)
        binding.root.setOnClickListener {
            onItemClick(position, data.type)
        }
    }

    override fun getItemCount(): Int {
        return drawerList.size
    }
}

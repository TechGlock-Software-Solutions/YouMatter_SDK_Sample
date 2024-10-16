package com.techglock.health.app.home.adapter

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.techglock.health.app.home.ui.sudLifePolicy.SlidingSudBannerFragment
import com.techglock.health.app.model.sudLifePolicy.PolicyProductsModel

class SlidingSudBannerAdapter(
    activity: FragmentActivity,
    private val itemsCount: Int,
    private val campaignDetailsList: MutableList<PolicyProductsModel.PolicyProducts>
) : FragmentStateAdapter(activity) {

    override fun getItemCount() = itemsCount

    override fun createFragment(position: Int) =
        SlidingSudBannerFragment(campaignDetailsList, position)

}
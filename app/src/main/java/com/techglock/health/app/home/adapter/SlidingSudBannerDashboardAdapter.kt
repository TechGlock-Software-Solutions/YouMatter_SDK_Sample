package com.techglock.health.app.home.adapter

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.techglock.health.app.home.common.OnPolicyBannerListener
import com.techglock.health.app.home.ui.SlidingSudBannerDashboardFragment
import com.techglock.health.app.model.sudLifePolicy.PolicyProductsModel


class SlidingSudBannerDashboardAdapter(
    activity: FragmentActivity,
    private val itemsCount: Int,
    private val campaignDetailsList: MutableList<PolicyProductsModel.PolicyProducts>,
    private val listener: OnPolicyBannerListener
) : FragmentStateAdapter(activity) {

    override fun getItemCount() = itemsCount

    override fun createFragment(position: Int) =
        SlidingSudBannerDashboardFragment(listener, campaignDetailsList, position)

}
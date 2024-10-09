package com.techglock.health.app.home.adapter

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.techglock.health.app.home.ui.aktivo.SlidingAktivoChallengesFragment

class SlidingAktivoChallengesAdapter(
    private val context: Context,
    activity: FragmentActivity,
    private val itemsCount: Int/*,
    private val challengesList: MutableList<Challenge>*/
) : FragmentStateAdapter(activity) {

    override fun getItemCount() = itemsCount

    override fun createFragment(position: Int) =
        SlidingAktivoChallengesFragment(context, /*challengesList,*/ position)

}
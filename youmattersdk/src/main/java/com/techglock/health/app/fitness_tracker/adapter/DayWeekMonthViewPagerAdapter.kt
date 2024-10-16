package com.techglock.health.app.fitness_tracker.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.techglock.health.app.R
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.fitness_tracker.ui.MonthFragment
import com.techglock.health.app.fitness_tracker.ui.TodayFragment
import com.techglock.health.app.fitness_tracker.ui.WeekFragment
import com.techglock.health.app.fitness_tracker.ui.YearFragment


class DayWeekMonthViewPagerAdapter(
    fm: FragmentManager,
    val context: Context
) : FragmentStatePagerAdapter(fm) {

    private var totalFragments = 4

    override fun getCount(): Int = totalFragments

    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null

        when (position) {
            0 -> {
                fragment = TodayFragment()
                CleverTapHelper.pushEvent(context, CleverTapConstants.TODAY_SCREEN)
            }

            1 -> {
                fragment = WeekFragment()
                CleverTapHelper.pushEvent(context, CleverTapConstants.WEEKLY_SCREEN)
            }

            2 -> {
                fragment = MonthFragment()
                CleverTapHelper.pushEvent(context, CleverTapConstants.MONTHLY_SCREEN)
            }

            3 -> {
                fragment = YearFragment()
                CleverTapHelper.pushEvent(context, CleverTapConstants.YEARLY_SCREEN)
            }
        }
        return fragment!!
    }

    override fun getPageTitle(position: Int): CharSequence {
        var title = ""
        when (position) {
            0 -> title = context.resources.getString(R.string.TODAY)
            1 -> title = context.resources.getString(R.string.WEEKLY)
            2 -> title = context.resources.getString(R.string.MONTHLY)
            3 -> title = context.resources.getString(R.string.YEARLY)
        }
        return title
    }
}

/*
class DayWeekMonthAdapter (fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3


    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)

        val fragment = DayFragment()
        fragment.arguments = Bundle().apply {
            //putInt(ARG_OBJECT, position + 1)
        }
        return fragment
    }
}*/

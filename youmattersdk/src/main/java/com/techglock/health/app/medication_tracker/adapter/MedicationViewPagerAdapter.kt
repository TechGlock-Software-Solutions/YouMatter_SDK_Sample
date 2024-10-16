package com.techglock.health.app.medication_tracker.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.techglock.health.app.medication_tracker.ui.MedicineDashboardFragment
import com.techglock.health.app.medication_tracker.ui.MedicineHomeFragment
import com.techglock.health.app.medication_tracker.ui.MyMedicationsFragment

//class MedicationViewPagerAdapter(fm: FragmentManager, val context: Context, val homeFragment: MedicineHomeFragment) : FragmentStatePagerAdapter(fm) {
class MedicationViewPagerAdapter(
    fm: FragmentActivity,
    val context: Context,
    val homeFragment: MedicineHomeFragment
) : FragmentStateAdapter(fm) {
    private var totalFragments = 2

    override fun getItemCount(): Int = totalFragments

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MedicineDashboardFragment(homeFragment)
            1 -> MyMedicationsFragment(homeFragment)
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }

    /*    override fun getCount(): Int = totalFragments

        override fun getItem(position: Int): Fragment {
            var fragment: Fragment? = null

            when (position) {
                0 -> fragment = MedicineDashboardFragment(homeFragment)
                1 -> fragment = MyMedicationsFragment(homeFragment)
            }
            return fragment!!
        }

        override fun getPageTitle(position: Int): CharSequence {
            var title = ""
            val localResource =
                LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
            when (position) {
                0 -> title = localResource.getString(R.string.SCHEDULE)
                1 -> title = localResource.getString(R.string.MY_MEDICATIONS)
            }
            return title
        }*/

}
package com.techglock.health.app.medication_tracker.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.NavigationConstants
import com.techglock.health.app.common.extension.openAnotherActivity
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentMedicationHomeBinding
import com.techglock.health.app.medication_tracker.MedicationHomeActivity
import com.techglock.health.app.medication_tracker.adapter.MedicationViewPagerAdapter
import com.techglock.health.app.medication_tracker.viewmodel.MedicineTrackerViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class MedicineHomeFragment : BaseFragment() {

    val viewModel: MedicineTrackerViewModel by lazy {
        ViewModelProvider(this)[MedicineTrackerViewModel::class.java]
    }
    private lateinit var binding: FragmentMedicationHomeBinding

    var from = ""
    var tab = 0
    var selectedDate = ""
    private var medicationViewPagerAdapter: MedicationViewPagerAdapter? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (requireActivity().intent.hasExtra(Constants.FROM)) {
            from = requireActivity().intent.getStringExtra(Constants.FROM)!!
        }
        if (requireActivity().intent.hasExtra(Constants.DATE)) {
            selectedDate = requireActivity().intent.getStringExtra(Constants.DATE)!!
        }
        Utilities.printLogError("from,selectedDate(onCreate)--->$from,$selectedDate")
        /*        when (from) {
                    Constants.TRACK_PARAMETER -> {
                        val bundle = Bundle()
                        bundle.putString(Constants.FROM, Constants.TRACK_PARAMETER)
                        findNavController().navigate(R.id.action_medicineHome_to_addMedicineFragment, bundle)
                    }
                }*/
        // callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Utilities.printLogError("from--->$from")
                when (from) {
                    Constants.NOTIFICATION_ACTION -> {
                        (activity as MedicationHomeActivity).routeToHomeScreen()
                    }

                    Constants.TRACK_PARAMETER -> {
                        requireActivity().finish()
                    }

                    else -> {
                        requireActivity().finish()
                    }
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMedicationHomeBinding.inflate(inflater, container, false)
        if (from.equals("", ignoreCase = true)) {
            arguments?.let {
                from = it.getString(Constants.FROM, "")!!
                selectedDate = it.getString(Constants.DATE, "")!!
                tab = it.getInt(Constants.TAB, 0)
                Utilities.printLogError("from,selectedDate,tab--->$from,$selectedDate,$tab")
            }
        }
        initialise()
        setClickable()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    fun initialise() {
        /*        medicationViewPagerAdapter = MedicationViewPagerAdapter(requireFragmentManager(), requireContext(), this)
                binding.viewPager.adapter = medicationViewPagerAdapter
                binding.viewPager.offscreenPageLimit = 1
                binding.viewPager.setScrollDuration(500)
                binding.viewPager.setPageScrollEnabled(false)
                binding.tabLayout.setupWithViewPager(binding.viewPager)*/
        medicationViewPagerAdapter =
            MedicationViewPagerAdapter(requireActivity(), requireContext(), this)
        binding.viewPager.adapter = medicationViewPagerAdapter
        binding.viewPager.isUserInputEnabled = false

        TabLayoutMediator(
            binding.tabLayout,
            binding.viewPager
        ) { tab: TabLayout.Tab, position: Int ->
            binding.viewPager.setCurrentItem(position, true)
            Utilities.printLogError("position--->${position}")
            when (position) {
                0 -> {
                    tab.setText(resources.getString(R.string.SCHEDULE))
                    CleverTapHelper.pushEvent(
                        requireContext(),
                        CleverTapConstants.MEDICATION_DASHBOARD_SCREEN
                    )
                }

                1 -> {
                    tab.setText(resources.getString(R.string.MY_MEDICATIONS))
                    CleverTapHelper.pushEvent(
                        requireContext(),
                        CleverTapConstants.MEDICATION_HISTORY_SCREEN
                    )
                }
            }
        }.attach()
        when (from) {
            Constants.MEDICATION -> {
                binding.viewPager.setCurrentItem(1, true)
            }

            else -> {
                binding.viewPager.setCurrentItem(0, true)
            }
        }
    }

    fun navigateFromDashboardToAddScreen(bundle: Bundle) {
        findNavController().navigate(R.id.action_medicineHome_to_addMedicineFragment, bundle)
    }

    fun navigateFromMyMedicationsToAddScreen(bundle: Bundle) {
        findNavController().navigate(R.id.action_medicineHome_to_addMedicineFragment, bundle)
    }

    fun navigateToScheduleScreen(bundle: Bundle) {
        findNavController().navigate(R.id.action_medicineHome_to_scheduleMedicineFragment, bundle)
    }

    private fun setClickable() {
        /*        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab) {
                        Utilities.printLogError("position--->${tab.position}")
                        binding.viewPager.currentItem = tab.position
                        when (tab.position) {
                            0 -> CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.MEDICATION_DASHBOARD_SCREEN)
                            1 -> CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.MEDICATION_HISTORY_SCREEN)
                        }
                    }
                    override fun onTabUnselected(tab: TabLayout.Tab?) {}
                    override fun onTabReselected(tab: TabLayout.Tab?) {}
                })*/
    }

    private fun navigateToHomeScreen() {
        openAnotherActivity(destination = NavigationConstants.HOME, clearTop = true)
    }

}
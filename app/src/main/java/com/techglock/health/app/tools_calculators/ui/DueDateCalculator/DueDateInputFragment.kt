package com.techglock.health.app.tools_calculators.ui.DueDateCalculator

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
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentDueDateInputBinding
import com.techglock.health.app.model.toolscalculators.UserInfoModel
import com.techglock.health.app.tools_calculators.viewmodel.ToolsCalculatorsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class DueDateInputFragment : BaseFragment() {

    private val viewModel: ToolsCalculatorsViewModel by lazy {
        ViewModelProvider(this)[ToolsCalculatorsViewModel::class.java]
    }
    private lateinit var binding: FragmentDueDateInputBinding

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}

        // callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding.btnCancel.performClick()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDueDateInputBinding.inflate(inflater, container, false)
        initialise()
//        FirebaseHelper.logScreenEvent(FirebaseConstants.DUE_DATE_CALCULATOR_SCREEN)
        return binding.root
    }

    private fun initialise() {
        CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.DUE_DATE_CALCULATOR_SCREEN)
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -10)
        binding.datePicker.maxDate = Calendar.getInstance().timeInMillis - 1000
        binding.datePicker.minDate = calendar.timeInMillis

        binding.btnCalculate.setOnClickListener {
            val lmpDate: String = binding.datePicker.year.toString() +
                    "-" + (binding.datePicker.month + 1) + "-" + binding.datePicker.dayOfMonth
            val bundle = Bundle()
            bundle.putString("lmpDate", lmpDate)
            findNavController().navigate(
                R.id.action_dueDateInputFragment_to_dueDateCalculatorReportFragment,
                bundle
            )
        }

        binding.btnCancel.setOnClickListener {
//            findNavController().navigate(R.id.action_dueDateInputFragment_to_toolsCalculatorsDashboardFragment)
            if (requireActivity().intent.hasExtra(Constants.TO)) {
                val extra = requireActivity().intent.getStringExtra(Constants.TO)
                Utilities.printLog("CalculatorTO=> $extra")
                if (extra.equals("DASH", true)) {
                    findNavController().navigateUp()
                } else {
                    UserInfoModel.getInstance()!!.isDataLoaded = false
                    requireActivity().finish()
                }
            }
//            findNavController().navigateUp()
        }
    }

}

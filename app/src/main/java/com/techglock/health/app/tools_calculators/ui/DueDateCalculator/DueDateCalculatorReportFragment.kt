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
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentDueDateCalculatorReportBinding
import com.techglock.health.app.model.toolscalculators.UserInfoModel
import com.techglock.health.app.tools_calculators.model.CalculatorDataSingleton
import com.techglock.health.app.tools_calculators.viewmodel.ToolsCalculatorsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class DueDateCalculatorReportFragment : BaseFragment() {

    private val viewModel: ToolsCalculatorsViewModel by lazy {
        ViewModelProvider(this)[ToolsCalculatorsViewModel::class.java]
    }
    private lateinit var binding: FragmentDueDateCalculatorReportBinding
    private var calculatorDataSingleton: CalculatorDataSingleton? = null

    private var lmpDate = ""

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                calculatorDataSingleton!!.clearData()
                if (requireActivity().intent.hasExtra(Constants.TO)) {
                    val extra = requireActivity().intent.getStringExtra(Constants.TO)
                    Utilities.printLog("CalculatorTO=> $extra")
                    if (extra.equals("DASH", true)) {
                        findNavController().navigate(R.id.action_dueDateCalculatorReportFragment_to_toolsCalculatorsDashboardFragment)
                    } else {
                        UserInfoModel.getInstance()!!.isDataLoaded = false
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
    ): View {
        binding = FragmentDueDateCalculatorReportBinding.inflate(inflater, container, false)
        lmpDate = requireArguments().getString("lmpDate")!!
        Utilities.printLog("lmpDate--->$lmpDate")
        calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {
        CleverTapHelper.pushEvent(
            requireContext(),
            CleverTapConstants.DUE_DATE_CALCULATOR_SUMMARY_SCREEN
        )
        val strLmpDate: String = DateHelper.convertDateToStr(
            DateHelper.convertStringDateToDate(lmpDate, "yyyy-MM-dd"),
            "dd MMMM yyyy"
        )
        binding.txtLmpDate.text = strLmpDate
        calculateBabyDueDate()
    }

    private fun calculateBabyDueDate() {
        val cal = Calendar.getInstance()
        val oldDate = DateHelper.convertStringDateToDate(lmpDate, "yyyy-MM-dd")
        cal.time = oldDate

        //Number of Days to add
        cal.add(Calendar.HOUR, 280 * 24)
        //Date after adding the days to the given date
        val newDate = DateHelper.convertDateToStr(cal.time, "dd MMMM yyyy")
        Utilities.printLog("Date after Addition--->$newDate")
        binding.txtDdDate.text = newDate
    }

    private fun setClickable() {

        binding.btnCancel.setOnClickListener {
            findNavController().navigate(R.id.action_dueDateCalculatorReportFragment_to_toolsCalculatorsDashboardFragment)
        }

        binding.btnRecalculate.setOnClickListener {
            findNavController().navigate(R.id.action_dueDateCalculatorReportFragment_to_dueDateInputFragment)
        }

    }

}

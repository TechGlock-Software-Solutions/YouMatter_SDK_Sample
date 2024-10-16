package com.techglock.health.app.tools_calculators.ui

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
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentToolsCalculatorsDashboardBinding
import com.techglock.health.app.model.toolscalculators.UserInfoModel
import com.techglock.health.app.tools_calculators.ToolsCalculatorsHomeActivity
import com.techglock.health.app.tools_calculators.adapter.TrackersDashboardAdapter
import com.techglock.health.app.tools_calculators.model.CalculatorDataSingleton
import com.techglock.health.app.tools_calculators.model.TrackerDashboardModel
import com.techglock.health.app.tools_calculators.viewmodel.ToolsCalculatorsViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ToolsCalculatorsDashboardFragment : BaseFragment(),
    TrackersDashboardAdapter.OnCalculatorClickListener,
    DialogNotificationTools.OnRestartHRA {

    private lateinit var binding: FragmentToolsCalculatorsDashboardBinding
    private val viewModel: ToolsCalculatorsViewModel by lazy {
        ViewModelProvider(this)[ToolsCalculatorsViewModel::class.java]
    }
    private var calculatorDataSingleton: CalculatorDataSingleton? = null

    private var screen = ""
    private val userInfoModel = UserInfoModel.getInstance()!!
    private var trackersDashboardAdapter: TrackersDashboardAdapter? = null
    private var dialogDisclaimer: DialogDisclaimer? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            arguments?.let {
                screen = it.getString(Constants.SCREEN, "")!!
                Utilities.printLogError("screen--->$screen")
                if (!Utilities.isNullOrEmpty(screen)) {
                    showNotificationPopup(screen)
                }
            }

            // callback to Handle back button event
            val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    calculatorDataSingleton!!.clearData()
                    if (requireActivity().intent.hasExtra(Constants.TO)) {
                        val extra = requireActivity().intent.getStringExtra(Constants.TO)
                        Utilities.printLog("CalculatorTO=> $extra")
                        if (extra.equals("DASH", true)) {
                            UserInfoModel.getInstance()!!.isDataLoaded = false
                            requireActivity().finish()
                        }
                    }

                    if (!Utilities.isNullOrEmpty(screen)) {
                        UserInfoModel.getInstance()!!.isDataLoaded = false
                        (activity as ToolsCalculatorsHomeActivity).finish()
                    }
                }
            }
            requireActivity().onBackPressedDispatcher.addCallback(this, callback)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentToolsCalculatorsDashboardBinding.inflate(inflater, container, false)

        try {
            calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!
            initialise()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    private fun initialise() {
        binding.rvTrackerDashboard.layoutManager =
            androidx.recyclerview.widget.GridLayoutManager(context, 2)
        trackersDashboardAdapter = TrackersDashboardAdapter(requireContext(), this)
        binding.rvTrackerDashboard.adapter = trackersDashboardAdapter

        if (!userInfoModel.isDataLoaded) {
            viewModel.getMedicalProfileSummary(true)
        }

        binding.txtDisclaimer.setOnClickListener {
            showDisclaimerDialog()
        }

        viewModel.medicalProfileSummary.observe(viewLifecycleOwner) {}
        viewModel.labRecords.observe(viewLifecycleOwner) {}
        viewModel.startQuiz.observe(viewLifecycleOwner) {}

        viewModel.getAllTrackersList()
        viewModel.getParameterDetails()

        viewModel.trackersList.observe(viewLifecycleOwner) {
            it?.let {
                if (trackersDashboardAdapter != null) {
                    trackersDashboardAdapter!!.updateTrackersList(it)
                }
            }

        }
    }

    override fun onCalculatorSelection(trackerDashboardModel: TrackerDashboardModel, view: View) {
        calculatorDataSingleton!!.clearData()
        startQuiz(trackerDashboardModel.code)
    }

    private fun startQuiz(code: String) {
        when (code) {

            Constants.CODE_HEART_AGE_CALCULATOR -> {
                viewModel.callStartQuizApi(true, Constants.QUIZ_CODE_HEART_AGE)
                //SessionInfoSingleton.getInstance().setQuizId("5")
            }

            Constants.CODE_DIABETES_CALCULATOR -> {
                viewModel.callStartQuizApi(true, Constants.QUIZ_CODE_DIABETES)
                //SessionInfoSingleton.getInstance().setQuizId("8")
            }

            Constants.CODE_HYPERTENSION_CALCULATOR -> {
                viewModel.callStartQuizApi(true, Constants.QUIZ_CODE_HYPERTENSION)
                //SessionInfoSingleton.getInstance().setQuizId("9")
            }

            Constants.CODE_STRESS_ANXIETY_CALCULATOR -> {
                viewModel.callStartQuizApi(true, Constants.QUIZ_CODE_STRESS_ANXIETY)
                //SessionInfoSingleton.getInstance().setQuizId("7")
            }

            Constants.CODE_SMART_PHONE_ADDICTION_CALCULATOR -> {
                viewModel.callStartQuizApi(true, Constants.QUIZ_CODE_SMART_PHONE)
                //SessionInfoSingleton.getInstance().setQuizId("4")
            }

            Constants.CODE_DUE_DATE_CALCULATOR -> {
                findNavController().navigate(R.id.action_toolsCalculatorsDashboardFragment_to_dueDateInputFragment)
            }
            //else -> Utilities.toastMessageShort(context, "Under Development")

        }
    }

    private fun showNotificationPopup(screen: String) {
        val dialogUpdateApp = DialogNotificationTools(requireContext(), screen, this)
        dialogUpdateApp.show()
    }

    override fun onRestartHRAClick(screen: String, tracker: TrackerDashboardModel) {
        Utilities.printLogError("screen--->$screen")
        startQuiz(tracker.code)
    }

    private fun showDisclaimerDialog() {
        dialogDisclaimer = DialogDisclaimer(requireContext())
        dialogDisclaimer!!.show()
    }

}

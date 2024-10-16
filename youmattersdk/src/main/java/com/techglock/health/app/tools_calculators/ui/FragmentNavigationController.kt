package com.techglock.health.app.tools_calculators.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentNavigationControllerBinding
import com.techglock.health.app.model.toolscalculators.UserInfoModel
import com.techglock.health.app.tools_calculators.adapter.TrackersDashboardAdapter
import com.techglock.health.app.tools_calculators.model.CalculatorDataSingleton
import com.techglock.health.app.tools_calculators.model.TrackerDashboardModel
import com.techglock.health.app.tools_calculators.viewmodel.ToolsCalculatorsViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FragmentNavigationController : BaseFragment(),
    TrackersDashboardAdapter.OnCalculatorClickListener {

    private lateinit var binding: FragmentNavigationControllerBinding
    private val viewModel: ToolsCalculatorsViewModel by lazy {
        ViewModelProvider(this)[ToolsCalculatorsViewModel::class.java]
    }
    private var calculatorDataSingleton: CalculatorDataSingleton? = null

    //private var from = ""
    private var to = ""
    private var screen = ""

    private val userInfoModel = UserInfoModel.getInstance()!!

    override fun getViewModel(): BaseViewModel = viewModel

    /*    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            try {
                arguments?.let {
                    screen = it.getString(Constants.SCREEN, "")!!
                    Utilities.printLogError("screen--->$screen")
                }
            } catch ( e : Exception ) {
                e.printStackTrace()
            }
        }*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNavigationControllerBinding.inflate(inflater, container, false)
        try {
            calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!
            initialise()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    private fun initialise() {

//        val anim = AnimationUtils.loadAnimation(context, com.caressa.allizhealth.app.common.R.anim.rotate_forward)
//        binding.imgLoader.animation = anim
//        binding.imgLoader.startAnimation(anim)

        if (requireActivity().intent.hasExtra(Constants.TO)) {
            val extra = requireActivity().intent.getStringExtra(Constants.TO)
            Utilities.printLog("CalculatorTO=> $extra")
            to = if (extra.equals("DASH", true)) {
                extra!!
            } else {
                extra!!
            }
        }

        if (requireActivity().intent.hasExtra(Constants.SCREEN)) {
            screen = requireActivity().intent.getStringExtra(Constants.SCREEN)!!
            Utilities.printLogError("screen--->$screen")
        }

        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.anim_pulse)
        binding.imgLoader.startAnimation(animation)

        viewModel.medicalProfileSummary.observe(viewLifecycleOwner) {}
        viewModel.apiStatusLiveData.observe(viewLifecycleOwner) {
            if (to.equals("DASH", true)) {
                viewModel.navigate(
                    FragmentNavigationControllerDirections.actionToolsCalculatorsControllerToDashboardFragment(
                        ""
                    )
                )
            } else if (!Utilities.isNullOrEmpty(screen)) {
                viewModel.navigate(
                    FragmentNavigationControllerDirections.actionToolsCalculatorsControllerToDashboardFragment(
                        screen
                    )
                )
            } else {
                startQuiz(to)
            }
        }
//        viewModel.labRecords.observe(viewLifecycleOwner, {
//
//        })
        viewModel.startQuiz.observe(viewLifecycleOwner) {}

        if (!userInfoModel.isDataLoaded) {
            viewModel.getMedicalProfileSummary(false)
        }

        viewModel.getAllTrackersList()
    }

    override fun onCalculatorSelection(trackerDashboardModel: TrackerDashboardModel, view: View) {
        calculatorDataSingleton!!.clearData()
        startQuiz(trackerDashboardModel.code)
    }

    private fun startQuiz(code: String) {
        when (code) {

            Constants.CODE_HEART_AGE_CALCULATOR-> {
                viewModel.callStartQuizApi(false, Constants.QUIZ_CODE_HEART_AGE)
                //SessionInfoSingleton.getInstance().setQuizId("5")
            }

            Constants.CODE_DIABETES_CALCULATOR -> {
                viewModel.callStartQuizApi(false, Constants.QUIZ_CODE_DIABETES)
                //SessionInfoSingleton.getInstance().setQuizId("8")
            }

            Constants.CODE_HYPERTENSION_CALCULATOR -> {
                viewModel.callStartQuizApi(false, Constants.QUIZ_CODE_HYPERTENSION)
                //SessionInfoSingleton.getInstance().setQuizId("9")
            }

            Constants.CODE_STRESS_ANXIETY_CALCULATOR -> {
                viewModel.callStartQuizApi(false, Constants.QUIZ_CODE_STRESS_ANXIETY)
                //SessionInfoSingleton.getInstance().setQuizId("7")
            }

            Constants.CODE_SMART_PHONE_ADDICTION_CALCULATOR -> {
                viewModel.callStartQuizApi(false, Constants.QUIZ_CODE_SMART_PHONE)
                //SessionInfoSingleton.getInstance().setQuizId("4")
            }

            Constants.CODE_DUE_DATE_CALCULATOR -> {
                findNavController().navigate(R.id.action_toolsCalculatorsDashboardFragment_to_dueDateInputFragment)
            }
            //else -> Utilities.toastMessageShort(context, "Under Development")

        }
    }

}
package com.techglock.health.app.tools_calculators.ui.DiabetesCalculator

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
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
import com.techglock.health.app.databinding.FragmentDiabetesSummaryBinding
import com.techglock.health.app.tools_calculators.common.DataHandler
import com.techglock.health.app.tools_calculators.model.CalculatorDataSingleton
import com.techglock.health.app.tools_calculators.viewmodel.ToolsCalculatorsViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DiabetesSummaryFragment : BaseFragment() {

    private lateinit var binding: FragmentDiabetesSummaryBinding
    private val viewModel: ToolsCalculatorsViewModel by lazy {
        ViewModelProvider(this)[ToolsCalculatorsViewModel::class.java]
    }
    private var calculatorDataSingleton: CalculatorDataSingleton? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_diabetesSummaryFragment_to_diabetesCalculatorFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDiabetesSummaryBinding.inflate(inflater, container, false)
        calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!
        initialise()
        setClickable()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun initialise() {
        CleverTapHelper.pushEvent(
            requireContext(),
            CleverTapConstants.DIABETES_CALCULATOR_SUMMARY_SCREEN
        )
        binding.indicatorDiabetesRisk.isClickable = false
        binding.indicatorDiabetesRisk.setOnTouchListener { _: View?, _: MotionEvent? -> true }

        binding.layoutNotNomophobic.setViewColor(R.color.risk_normal)
        binding.layoutMildNomophobic.setViewColor(R.color.risk_mild)
        binding.layoutModerateNomophobic.setViewColor(R.color.risk_moderate_new)
        binding.layoutSevereNomophobic.setViewColor(R.color.risk_severe)

        binding.layoutNotNomophobic.setViewBackgroundColor(R.color.bg_risk_normal)
        binding.layoutMildNomophobic.setViewBackgroundColor(R.color.bg_risk_mild)
        binding.layoutModerateNomophobic.setViewBackgroundColor(R.color.bg_risk_moderate)
        binding.layoutSevereNomophobic.setViewBackgroundColor(R.color.bg_risk_extremly_severe)

        binding.layoutNotNomophobic.setRangeValue(resources.getString(R.string.DIABETES_RANGE_LOW_RISK))
        binding.layoutMildNomophobic.setRangeValue(resources.getString(R.string.DIABETES_RANGE_MILD_RISK))
        binding.layoutModerateNomophobic.setRangeValue(resources.getString(R.string.DIABETES_RANGE_MODERATE_RISK))
        binding.layoutSevereNomophobic.setRangeValue(resources.getString(R.string.DIABETES_RANGE_HIGH_RISK))

        binding.layoutNotNomophobic.setRangeTitle(resources.getString(R.string.LOW_RISK))
        binding.layoutMildNomophobic.setRangeTitle(resources.getString(R.string.MILD_RISK))
        binding.layoutModerateNomophobic.setRangeTitle(resources.getString(R.string.MODERATE_RISK))
        binding.layoutSevereNomophobic.setRangeTitle(resources.getString(R.string.HIGH_RISK))

        binding.txtDibProbability.text =
            (resources.getString(R.string.BASED_ON_YOUR_SCORE_YOU_HAVE) + " "
                    + calculatorDataSingleton!!.diabetesSummeryModel.probabilityPercentage
                    + " " + resources.getString(R.string.PROBABILITY_OF_GETTING_DIABETES))
        binding.indicatorDiabetesRisk.setOnTouchListener { _: View?, _: MotionEvent? -> true }

        setDiabetesRiskDetails(
            calculatorDataSingleton!!.diabetesSummeryModel.totalScore.toDouble(),
            calculatorDataSingleton!!.diabetesSummeryModel.riskLabel
        )
    }

    @SuppressLint("SetTextI18n")
    private fun setDiabetesRiskDetails(riskPercentage: Double, riskType: String) {
        val dataHandler = DataHandler(requireContext())
        val heartColour: Int
        when (riskPercentage) {
            in 0.0..5.0 -> {
                heartColour = R.color.risk_normal
            }

            in 6.0..11.0 -> {
                heartColour = R.color.risk_mild
            }

            in 12.0..19.0 -> {
                heartColour = R.color.risk_moderate_new
            }

            in 20.0..47.0 -> {
                heartColour = R.color.risk_severe
            }

            else -> {
                heartColour = R.color.vivant_marigold
            }
        }
        //binding.indicatorDiabetesRisk.progressColor = ContextCompat.getColor(requireContext(), heartColour)
        binding.indicatorDiabetesRisk.setBarColor(resources.getColor(heartColour))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //binding.indicatorDiabetesRisk.mThumb!!.setTint(ContextCompat.getColor(requireContext(), heartColour))
        }
        //binding.layoutHeartRisk.backgroundTintList = ContextCompat.getColorStateList(requireContext(),heartColour)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //binding.layoutHeartRisk.background.colorFilter = BlendModeColorFilter(ContextCompat.getColor(requireContext(), heartColour), BlendMode.SRC_ATOP)
        } else {
            //binding.layoutHeartRisk.background.setColorFilter(ContextCompat.getColor(requireContext(), heartColour), PorterDuff.Mode.SRC_ATOP)
        }
        //binding.indicatorDiabetesRisk.progress = riskPercentage.toInt()
        //binding.indicatorDiabetesRisk.setProgressWithAnimation(riskPercentage)
        binding.indicatorDiabetesRisk.setValueAnimated(
            riskPercentage.toFloat(),
            Constants.ANIMATION_DURATION.toLong()
        )
        binding.txtScore.text = riskPercentage.toInt().toString()
        binding.txtObservation.text = dataHandler.getRiskConverted(riskType, requireContext())
    }

    private fun setClickable() {

        binding.btnViewReportDiabetes.setOnClickListener {
            findNavController().navigate(R.id.action_diabetesSummaryFragment_to_diabetesReportFragment)
        }

        binding.btnRestartDiabetes.setOnClickListener {
            findNavController().navigate(R.id.action_diabetesSummaryFragment_to_diabetesCalculatorFragment)
        }

    }

}
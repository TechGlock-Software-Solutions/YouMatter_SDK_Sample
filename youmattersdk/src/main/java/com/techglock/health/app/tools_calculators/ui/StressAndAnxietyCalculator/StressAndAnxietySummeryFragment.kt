package com.techglock.health.app.tools_calculators.ui.StressAndAnxietyCalculator

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.databinding.FragmentStressAndAnxietySummeryBinding
import com.techglock.health.app.model.toolscalculators.UserInfoModel
import com.techglock.health.app.tools_calculators.adapter.ScaleAssetsAdapter
import com.techglock.health.app.tools_calculators.adapter.StressDetailReportAdapter
import com.techglock.health.app.tools_calculators.common.DataHandler
import com.techglock.health.app.tools_calculators.model.CalculatorDataSingleton
import com.techglock.health.app.tools_calculators.model.StressData
import com.techglock.health.app.tools_calculators.viewmodel.ToolsCalculatorsViewModel
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StressAndAnxietySummeryFragment : BaseFragment() {

    private val viewModel: ToolsCalculatorsViewModel by lazy {
        ViewModelProvider(this)[ToolsCalculatorsViewModel::class.java]
    }
    private lateinit var binding: FragmentStressAndAnxietySummeryBinding
    private var calculatorDataSingleton: CalculatorDataSingleton? = null


    private var scaleAssetsAdapter: ScaleAssetsAdapter? = null
    private var stressDetailReportAdapter: StressDetailReportAdapter? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}

        // callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                calculatorDataSingleton!!.clearData()
                if (requireActivity().intent.hasExtra(Constants.TO)) {
                    val extra = requireActivity().intent.getStringExtra(Constants.TO)
                    //Utilities.printLog("CalculatorTO=> "+extra)
                    if (extra.equals("DASH", true)) {
                        findNavController().navigate(R.id.action_stressAndAnxietySummeryFragment_to_toolsCalculatorsDashboardFragment)
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
        binding = FragmentStressAndAnxietySummeryBinding.inflate(inflater, container, false)
        calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {
        CleverTapHelper.pushEvent(
            requireContext(),
            CleverTapConstants.STRESS_AND_ANXIETY_CALCULATOR_SUMMARY_SCREEN
        )
        //binding.layoutNormal.setViewBackgroundColor(R.color.vivant_soft_pink)

        binding.layoutNormal.setViewColor(R.color.risk_normal)
        binding.layoutMild.setViewColor(R.color.risk_mild)
        binding.layoutModerate.setViewColor(R.color.risk_moderate_new)
        binding.layoutSevere.setViewColor(R.color.risk_severe)
        binding.layoutExtremelySevere.setViewColor(R.color.risk_extremly_severe)

        binding.layoutNormal.setViewBackgroundColor(R.color.bg_risk_normal)
        binding.layoutMild.setViewBackgroundColor(R.color.bg_risk_mild)
        binding.layoutModerate.setViewBackgroundColor(R.color.bg_risk_moderate)
        binding.layoutSevere.setViewBackgroundColor(R.color.bg_risk_severe)
        binding.layoutExtremelySevere.setViewBackgroundColor(R.color.bg_risk_extremly_severe)

        scaleAssetsAdapter =
            ScaleAssetsAdapter(viewModel.dataHandler.scaleAssetsList(), requireContext())
        binding.rvScaleAssets.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvScaleAssets.adapter = scaleAssetsAdapter

        val detailReportList =
            CalculatorDataSingleton.getInstance()!!.stressSummeryData.parameterReport
        stressDetailReportAdapter = StressDetailReportAdapter(requireContext(), detailReportList)
        binding.observationRecycler.adapter = stressDetailReportAdapter
        if (detailReportList.isNotEmpty()) {
            binding.layoutList.visibility = View.VISIBLE
        } else {
            binding.layoutList.visibility = View.GONE
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setClickable() {

        binding.indicatorScore.isClickable = false
        binding.indicatorScore.setOnTouchListener { _: View?, _: MotionEvent? -> true }

        binding.layoutTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab) {
                updateData(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.btnRestartAssessment.setOnClickListener {
            findNavController().navigate(R.id.action_stressAndAnxietySummeryFragment_to_stressAndAnxietyInputFragment)
        }

        updateData(0)

    }

    private fun updateData(position: Int) {
        try {
            var stressData = StressData()
            when (position) {
                0 -> {
                    binding.lblRiskScore.text = resources.getString(R.string.DEPRESSION_RISK_SCORE)
                    stressData =
                        CalculatorDataSingleton.getInstance()!!.stressSummeryData.depression
                }

                1 -> {
                    binding.lblRiskScore.text = resources.getString(R.string.ANXIETY_RISK_SCORE)
                    stressData = CalculatorDataSingleton.getInstance()!!.stressSummeryData.anxiety
                }

                2 -> {
                    binding.lblRiskScore.text = resources.getString(R.string.STRESS_RISK_SCORE)
                    stressData = CalculatorDataSingleton.getInstance()!!.stressSummeryData.stress
                }
            }

            binding.layoutNormal.setRangeValue(stressData.normal)
            binding.layoutMild.setRangeValue(stressData.mild)
            binding.layoutModerate.setRangeValue(stressData.moderate)
            binding.layoutSevere.setRangeValue(stressData.severe)
            binding.layoutExtremelySevere.setRangeValue(stressData.extremeSevere)

            binding.layoutNormal.setRangeTitle(resources.getString(R.string.RISK_NORMAL))
            binding.layoutMild.setRangeTitle(resources.getString(R.string.RISK_MILD))
            binding.layoutModerate.setRangeTitle(resources.getString(R.string.RISK_MODERATE))
            binding.layoutSevere.setRangeTitle(resources.getString(R.string.RISK_SEVERE))
            binding.layoutExtremelySevere.setRangeTitle(resources.getString(R.string.RISK_EXTREMELY_SEVERE))
            setRiskDetails(stressData.score.toInt(), stressData.riskLabel, stressData)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    @Throws(java.lang.Exception::class)
    private fun setRiskDetails(riskPercentage: Int, riskType: String, stressData: StressData) {
        val dataHandler = DataHandler(requireContext())
        val heartColour: Int
        if (riskPercentage >= stressData.normal.split("-")[0].toDouble()
            && riskPercentage <= stressData.normal.split("-")[1].toDouble()
        ) {
            heartColour = R.color.risk_normal
        } else if (riskPercentage >= stressData.mild.split("-")[0].toDouble()
            && riskPercentage <= stressData.mild.split("-")[1].toDouble()
        ) {
            heartColour = R.color.risk_mild
        } else if (riskPercentage >= stressData.moderate.split("-")[0].toDouble()
            && riskPercentage <= stressData.moderate.split("-")[1].toDouble()
        ) {
            heartColour = R.color.risk_moderate_new
        } else if (riskPercentage >= stressData.severe.split("-")[0].toDouble()
            && riskPercentage <= stressData.severe.split("-")[1].toDouble()
        ) {
            heartColour = R.color.risk_severe
        } else if (riskPercentage >= stressData.extremeSevere.split("-")[0].toDouble()
            && riskPercentage <= stressData.extremeSevere.split("-")[1].toDouble()
        ) {
            heartColour = R.color.risk_extremly_severe
        } else {
            heartColour = R.color.vivant_marigold
        }

        val progress = (riskPercentage * 100) / 21

        //binding.indicatorScore.progressColor = ContextCompat.getColor(requireContext(), heartColour)
        binding.indicatorScore.setBarColor(resources.getColor(heartColour))
        //binding.indicatorScore.setProgressWithAnimation(riskPercentage.toDouble())
        binding.indicatorScore.setValueAnimated(
            progress.toFloat(),
            Constants.ANIMATION_DURATION.toLong()
        )
        binding.txtScore.text = "$riskPercentage"
        binding.txtObservation.text = dataHandler.getRiskConverted(riskType, requireContext())
    }

    /*    @Throws(java.lang.Exception::class)
        private fun setRiskDetails(riskPercentage: Int, riskType: String, stressData: StressData) {
            val heartColour: Int
            if (riskPercentage >= stressData.normal.split("-")[0].toDouble()
                && riskPercentage <= stressData.normal.split("-")[1].toDouble()) {
                heartColour = R.color.vivant_nasty_green
            } else if (riskPercentage >= stressData.mild.split("-")[0].toDouble()
                && riskPercentage <= stressData.mild.split("-")[1].toDouble()) {
                heartColour = R.color.vivant_marigold
            } else if (riskPercentage >= stressData.moderate.split("-")[0].toDouble()
                && riskPercentage <= stressData.moderate.split("-")[1].toDouble()) {
                heartColour = R.color.vivant_orange_yellow
            } else if (riskPercentage >= stressData.severe.split("-")[0].toDouble()
                && riskPercentage <= stressData.severe.split("-")[1].toDouble()) {
                heartColour = R.color.vivant_watermelon
            } else if (riskPercentage >= stressData.extremeSevere.split("-")[0].toDouble()
                && riskPercentage <= stressData.extremeSevere.split("-")[1].toDouble()) {
                heartColour = R.color.vivant_red
            } else {
                heartColour = R.color.vivant_marigold
            }

            val progress = (riskPercentage * 100) / 21

            //binding.indicatorScore.progressColor = ContextCompat.getColor(requireContext(), heartColour)
            binding.indicatorScore.setBarColor(resources.getColor(heartColour))
            //binding.indicatorScore.setProgressWithAnimation(riskPercentage.toDouble())
            binding.indicatorScore.setValueAnimated(progress.toFloat(),Constants.ANIMATION_DURATION.toLong())
            binding.txtScore.text = "" + riskPercentage
            binding.txtObservation.text = riskType + " ${resources.getString(R.string.RISK)}"
        }*/

}

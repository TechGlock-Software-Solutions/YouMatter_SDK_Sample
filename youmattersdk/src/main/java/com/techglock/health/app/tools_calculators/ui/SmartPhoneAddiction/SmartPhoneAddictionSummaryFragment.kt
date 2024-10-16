package com.techglock.health.app.tools_calculators.ui.SmartPhoneAddiction

import android.annotation.SuppressLint
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
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentSmartPhoneAddictionSummaryBinding
import com.techglock.health.app.model.toolscalculators.UserInfoModel
import com.techglock.health.app.tools_calculators.adapter.SmartphoneAddictionAdapter
import com.techglock.health.app.tools_calculators.common.DataHandler
import com.techglock.health.app.tools_calculators.model.CalculatorDataSingleton
import com.techglock.health.app.tools_calculators.viewmodel.ToolsCalculatorsViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SmartPhoneAddictionSummaryFragment : BaseFragment() {

    private val viewModel: ToolsCalculatorsViewModel by lazy {
        ViewModelProvider(this)[ToolsCalculatorsViewModel::class.java]
    }
    private lateinit var binding: FragmentSmartPhoneAddictionSummaryBinding
    private var calculatorDataSingleton: CalculatorDataSingleton? = null

    //private val dataHandler: DataHandler = get()

    //var score = "0"
    private var smartphoneAddictionAdapter1: SmartphoneAddictionAdapter? = null
    private var smartphoneAddictionAdapter2: SmartphoneAddictionAdapter? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            //score = it.getString("Score", "")!!
            //Utilities.printLogError("Score---> $score")
        }

        // callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                calculatorDataSingleton!!.clearData()
                if (requireActivity().intent.hasExtra(Constants.TO)) {
                    val extra = requireActivity().intent.getStringExtra(Constants.TO)
                    Utilities.printLog("CalculatorTO=> $extra")
                    if (extra.equals("DASH", true)) {
                        findNavController().navigate(R.id.action_smartPhoneAddictionSummaryFragment_to_toolsCalculatorsDashboardFragment)
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
        binding = FragmentSmartPhoneAddictionSummaryBinding.inflate(inflater, container, false)
        calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {
        try {
            val dataHandler = DataHandler(requireContext())
            CleverTapHelper.pushEvent(
                requireContext(),
                CleverTapConstants.SMART_PHONE_ADDICTION_CALCULATOR_SUMMARY_SCREEN
            )
            binding.layoutNotNomophobic.setViewColor(R.color.risk_normal)
            binding.layoutMildNomophobic.setViewColor(R.color.risk_mild)
            binding.layoutModerateNomophobic.setViewColor(R.color.risk_moderate_new)
            binding.layoutSevereNomophobic.setViewColor(R.color.risk_severe)

            binding.layoutNotNomophobic.setViewBackgroundColor(R.color.bg_risk_normal)
            binding.layoutMildNomophobic.setViewBackgroundColor(R.color.bg_risk_mild)
            binding.layoutModerateNomophobic.setViewBackgroundColor(R.color.bg_risk_moderate)
            binding.layoutSevereNomophobic.setViewBackgroundColor(R.color.bg_risk_extremly_severe)

            binding.layoutNotNomophobic.setRangeValue("0 - 6")
            binding.layoutMildNomophobic.setRangeValue("7 - 23")
            binding.layoutModerateNomophobic.setRangeValue("24 - 39")
            binding.layoutSevereNomophobic.setRangeValue("40 - 55")

            binding.layoutNotNomophobic.setRangeTitle(resources.getString(R.string.NOT_AT_ALL_NOMOPHOBIC))
            binding.layoutMildNomophobic.setRangeTitle(resources.getString(R.string.MILD_NOMOPHOBIC))
            binding.layoutModerateNomophobic.setRangeTitle(resources.getString(R.string.MODERATE_NOMOPHOBIC))
            binding.layoutSevereNomophobic.setRangeTitle(resources.getString(R.string.SEVERE_NOMOPHOBIC))

            val data = calculatorDataSingleton!!.smartPhoneSummeryData
            Utilities.printData("SmartPhoneData", data, true)

            binding.txtScore.text = data.score!!.toInt().toString()

            val score: Double = data.score!!
            val observation = dataHandler.getSmartPhoneRiskTitleConverted(
                data.smartPhoneRisk.riskTitle!!,
                requireContext()
            )
            val detailObservation = dataHandler.getSmartPhoneRiskLabelConverted(
                data.smartPhoneRisk.riskLabel!!,
                requireContext()
            )
            val detailObservationContent = data.smartPhoneRisk.riskDescription!!.replace("\\'", "'")
            val progress = (score * 100) / 55
            Utilities.printLog("Progress=> ${progress.toInt()}")
            //binding.indicatorScore.setProgressWithAnimation(progress)
            binding.indicatorScore.setValueAnimated(
                progress.toFloat(),
                Constants.ANIMATION_DURATION.toLong()
            )
            val color = when {
                score < 7 -> R.color.risk_normal
                score in 7.0..23.0 -> R.color.risk_mild
                score in 24.0..39.0 -> R.color.risk_moderate_new
                else -> R.color.risk_severe
            }
            binding.txtObservation.text = observation
            binding.txtSmartphoneResult.text = detailObservation
            //binding.txtSmartphoneResult.setTextColor(ContextCompat.getColor(requireContext(), color))
            binding.txtSmartphoneResultMsg.text = detailObservationContent
            //binding.indicatorScore.progressColor = ContextCompat.getColor(requireContext(), color)
            binding.indicatorScore.setBarColor(resources.getColor(color))

            binding.lblPhysicalEffectsOfAddiction.text =
                data.smartPhoneReport.physicalEffects!!.title
            binding.lblPhysicalEffectsOfAddictionDesc.text =
                data.smartPhoneReport.physicalEffects!!.description
            binding.lblPhyschologicalEffectsOfAddiction.text =
                data.smartPhoneReport.psychologicalEffects!!.title

            val physicalEffects = data.smartPhoneReport.physicalEffects!!.section!!.take(3)
            smartphoneAddictionAdapter1 =
                SmartphoneAddictionAdapter(physicalEffects, requireContext())
            binding.rvPhysicalEffects.adapter = smartphoneAddictionAdapter1

            val psychologicalEffects = data.smartPhoneReport.psychologicalEffects!!.section!!
            smartphoneAddictionAdapter2 =
                SmartphoneAddictionAdapter(psychologicalEffects, requireContext())
            binding.rvPhyschologicalEffects.adapter = smartphoneAddictionAdapter2

            /*            smartphoneAddictionAdapter1 = SmartphoneAddictionAdapter(dataHandler.getSACList1(),requireContext())
                        binding.rvPhysicalEffects.adapter = smartphoneAddictionAdapter1

                        smartphoneAddictionAdapter2 = SmartphoneAddictionAdapter(dataHandler.getSACList2(),requireContext())
                        binding.rvPhyschologicalEffects.adapter = smartphoneAddictionAdapter2*/

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setClickable() {

        binding.indicatorScore.setOnTouchListener { _: View?, _: MotionEvent? -> true }

        binding.btnRestart.setOnClickListener {
            findNavController().navigate(R.id.action_smartPhoneAddictionSummaryFragment_to_smartPhoneInputFragment)
        }

    }

}

package com.techglock.health.app.tools_calculators.ui.DiabetesCalculator

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.ArrayMap
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
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentDiabetesReportBinding
import com.techglock.health.app.model.toolscalculators.UserInfoModel
import com.techglock.health.app.tools_calculators.adapter.DiabetesReportExpandableAdapter
import com.techglock.health.app.tools_calculators.adapter.ScaleAssetsAdapter
import com.techglock.health.app.tools_calculators.common.DataHandler
import com.techglock.health.app.tools_calculators.model.CalculatorDataSingleton
import com.techglock.health.app.tools_calculators.model.ExpandModel
import com.techglock.health.app.tools_calculators.model.ScaleAsset
import com.techglock.health.app.tools_calculators.model.SubSectionModel
import com.techglock.health.app.tools_calculators.viewmodel.ToolsCalculatorsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class DiabetesReportFragment : BaseFragment() {

    private lateinit var binding: FragmentDiabetesReportBinding
    private val viewModel: ToolsCalculatorsViewModel by lazy {
        ViewModelProvider(this)[ToolsCalculatorsViewModel::class.java]
    }
    private var calculatorDataSingleton: CalculatorDataSingleton? = null

    private var scaleAssetsAdapter: ScaleAssetsAdapter? = null

    private var detailReportList: ArrayMap<String, ArrayList<SubSectionModel>> = ArrayMap()

    //private var diabetesSummarySuggestionsAdapter1: SummarySuggestionsAdapter? = null
    //private var diabetesSummarySuggestionsAdapter2: SummarySuggestionsAdapter? = null
    //private var diabetesSummarySuggestionsAdapter3: SummarySuggestionsAdapter? = null
    private var diabetesReportExpandableAdapter: DiabetesReportExpandableAdapter? = null

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
                        findNavController().navigate(R.id.action_diabetesReportFragment_to_toolsCalculatorsDashboardFragment)
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
        binding = FragmentDiabetesReportBinding.inflate(inflater, container, false)
        calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!
        initialise()
        setClickable()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initialise() {

        binding.indicatorScore.isClickable = false
        binding.indicatorScore.setOnTouchListener { _: View?, _: MotionEvent? -> true }

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

        val suggestionList: ArrayList<ScaleAsset> = ArrayList()

        val doingGoodList: List<String>
        if (calculatorDataSingleton!!.diabetesSummeryModel.goodIn.isNotEmpty()) {
            val arrDoingGood: Array<String> =
                calculatorDataSingleton!!.diabetesSummeryModel.goodIn.split(",").toTypedArray()
            doingGoodList = listOf(*arrDoingGood)
            suggestionList.add(
                ScaleAsset(
                    R.drawable.img_doing_great,
                    resources.getString(R.string.YOU_ARE_DOING_GOOD_IN),
                    doingGoodList,
                    "DEPRESSION"
                )
            )
        }

        val needImpList: List<String>
        if (calculatorDataSingleton!!.diabetesSummeryModel.needImprovement.isNotEmpty()) {
            val arrNeedImp: Array<String> =
                calculatorDataSingleton!!.diabetesSummeryModel.needImprovement.split(",")
                    .toTypedArray()
            needImpList = listOf(*arrNeedImp)
            suggestionList.add(
                ScaleAsset(
                    R.drawable.img_need_to_improve,
                    resources.getString(R.string.YOU_NEED_TO_IMPROVE_ON),
                    needImpList,
                    "ANXIETY"
                )
            )
        }

        val nonModRiskList: List<String>
        if (calculatorDataSingleton!!.diabetesSummeryModel.nonModifiableRisk.isNotEmpty()) {
            val arrNonModRisk: Array<String> =
                calculatorDataSingleton!!.diabetesSummeryModel.nonModifiableRisk.split(",")
                    .toTypedArray()
            nonModRiskList = listOf(*arrNonModRisk)
            suggestionList.add(
                ScaleAsset(
                    R.drawable.img_risk,
                    resources.getString(R.string.YOU_HAVE_FOLLOWING_ON_MODIFIABLE_RISK),
                    nonModRiskList,
                    "STRESS"
                )
            )
        }

        scaleAssetsAdapter = ScaleAssetsAdapter(suggestionList, requireContext())
        binding.rvSuggestions.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvSuggestions.adapter = scaleAssetsAdapter

        detailReportList = calculatorDataSingleton!!.diabetesSummeryModel.detailReport
        val arrayList: MutableList<ExpandModel> = ArrayList()
        for (i in 0..9) {
            arrayList.add(ExpandModel())
        }
        //recyclerView is passes to achieve expand/collapse functionality correctly.
        diabetesReportExpandableAdapter = DiabetesReportExpandableAdapter(
            binding.observationRecycler,
            detailReportList,
            arrayList,
            requireContext()
        )
        binding.observationRecycler.layoutManager = LinearLayoutManager(context)
        binding.observationRecycler.setExpanded(true)
        binding.observationRecycler.adapter = diabetesReportExpandableAdapter

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
        //binding.indicatorDiabetesRiskReport.progressColor = ContextCompat.getColor(requireContext(), heartColour)
        binding.indicatorScore.setBarColor(resources.getColor(heartColour))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //binding.indicatorDiabetesRiskReport.mThumb!!.setTint(ContextCompat.getColor(requireContext(), heartColour))
        }
        //binding.layoutHeartRisk.backgroundTintList = ContextCompat.getColorStateList(requireContext(),heartColour)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //binding.layoutHeartRisk.background.colorFilter = BlendModeColorFilter(ContextCompat.getColor(requireContext(), heartColour), BlendMode.SRC_ATOP)
        } else {
            //binding.layoutHeartRisk.background.setColorFilter(ContextCompat.getColor(requireContext(), heartColour), PorterDuff.Mode.SRC_ATOP)
        }
        //binding.indicatorDiabetesRiskReport.progress = riskPercentage.toInt()
        //binding.indicatorDiabetesRiskReport.setProgressWithAnimation(riskPercentage)
        binding.indicatorScore.setValueAnimated(
            riskPercentage.toFloat(),
            Constants.ANIMATION_DURATION.toLong()
        )
        binding.txtDiabetesRiskPercentReport.text = riskPercentage.toInt().toString()
        binding.txtDiabetesRiskTypeReport.text =
            dataHandler.getRiskConverted(riskType, requireContext())
    }

    private fun setClickable() {

        binding.btnRestartDiabetesReport.setOnClickListener {
            findNavController().navigate(R.id.action_diabetesReportFragment_to_diabetesCalculatorFragment)
        }

    }

}
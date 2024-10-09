package com.techglock.health.app.tools_calculators.ui.HypertensionCalculator

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentHypertensionSummeryBinding
import com.techglock.health.app.tools_calculators.model.CalculatorDataSingleton
import com.techglock.health.app.tools_calculators.viewmodel.ToolsCalculatorsViewModel
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class HypertensionSummeryFragment : BaseFragment() {

    private lateinit var binding: FragmentHypertensionSummeryBinding
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
                findNavController().navigate(R.id.action_hypertensionSummeryFragment_to_hypertensionInputFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHypertensionSummeryBinding.inflate(inflater, container, false)
        calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {
        CleverTapHelper.pushEvent(
            requireContext(),
            CleverTapConstants.HYPERTENSION_CALCULATOR_SUMMARY_SCREEN
        )
        if (calculatorDataSingleton!!.hypertensionSummery.hypertensionRisk.containsKey("RISK2")) {
            binding.btnRecalculate.text = resources.getString(R.string.RESTART)
        }
        initialiseChart()
    }

    private fun initialiseChart() {
        setChartData()

        // Initialise Heart risk chart Data
        val rightAxisRisk: YAxis = binding.barChartHeartRisk.axisRight
        rightAxisRisk.setDrawGridLines(false)
        rightAxisRisk.setDrawLabels(false)

        val leftAxisRisk: YAxis = binding.barChartHeartRisk.axisLeft
        leftAxisRisk.textColor = ContextCompat.getColor(requireContext(), R.color.textViewColor)
        leftAxisRisk.setLabelCount(5, true)
        leftAxisRisk.setDrawTopYLabelEntry(true)
        leftAxisRisk.setDrawZeroLine(true)
        leftAxisRisk.granularity = 1f
        leftAxisRisk.axisMinimum = 0f

        binding.barChartHeartRisk.setFitBars(true) // make the x-axis fit exactly all bars

        //barChart.getLegend().setEnabled(false);
        //barChart.getLegend().setEnabled(false);
        binding.barChartHeartRisk.setTouchEnabled(false)
        binding.barChartHeartRisk.isDoubleTapToZoomEnabled = false
        val descriptionRisk = Description()
        descriptionRisk.text = ""
        binding.barChartHeartRisk.description = descriptionRisk
        binding.barChartHeartRisk.invalidate() // refresh

    }

    private fun setChartData() {
        val xAxis: XAxis = binding.barChartHeartRisk.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 10f
        xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.textViewColor)
        xAxis.setDrawAxisLine(true)
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)
        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = 3f
        xAxis.setCenterAxisLabels(true)

        xAxis.valueFormatter =
            IAxisValueFormatter { value, _ ->
                Utilities.printLog("Position ==> $value")
                if (value == 0f) return@IAxisValueFormatter resources.getString(R.string.YEAR_1) else if (value == 1f) return@IAxisValueFormatter "${
                    resources.getString(
                        R.string.YEAR_2
                    )
                }  " else if (value == 2f) return@IAxisValueFormatter "${resources.getString(R.string.YEAR_4)}  "
                ""
            }

        //ArrayList<HeartAgeSummeryModel> summeryModelArrayList = CalculatorDataSingleton.getInstance().getHeartAgeSummeryList();
        val hypertensionRiskList: ArrayMap<String, ArrayList<String>> =
            calculatorDataSingleton!!.hypertensionSummery.hypertensionRisk
        val entries1: MutableList<BarEntry> = ArrayList()
        val entries2: MutableList<BarEntry> = ArrayList()
        val entries3: MutableList<BarEntry> = ArrayList()

        for (i in 0..2) {
            entries1.add(BarEntry(i.toFloat(), hypertensionRiskList["ORISK"]!![i].toFloat()))
            //entries1.add(new BarEntry(i.toFloat(), 12f));
            entries2.add(BarEntry(i.toFloat(), hypertensionRiskList["RISK1"]!![i].toFloat()))
            if (hypertensionRiskList.containsKey("RISK2")) {
                entries3.add(BarEntry(i.toFloat(), hypertensionRiskList["RISK2"]!![i].toFloat()))
            }
        }

        val setOptRisk = BarDataSet(entries1, resources.getString(R.string.OPTIMUM_RISK))
        setOptRisk.color = ContextCompat.getColor(requireContext(), R.color.dark_gold)
        setOptRisk.valueTextColor = ContextCompat.getColor(requireContext(), R.color.textViewColor)

        val setRisk1 = BarDataSet(entries2, resources.getString(R.string.RISK_1))
        setRisk1.color = ContextCompat.getColor(requireContext(), R.color.colorAccent)
        setRisk1.valueTextColor = ContextCompat.getColor(requireContext(), R.color.textViewColor)

        var groupSpace = 0.20f
        var barSpace = 0.03f // x2 dataset
        var barWidth = 0.35f

        var data = BarData(setOptRisk, setRisk1)
        if (hypertensionRiskList.containsKey("RISK2")) {
            val setRisk2 = BarDataSet(entries3, resources.getString(R.string.RISK_2))
            setRisk2.color = ContextCompat.getColor(requireContext(), R.color.dia_ichi_red)
            setRisk2.valueTextColor =
                ContextCompat.getColor(requireContext(), R.color.textViewColor)
            data = BarData(setOptRisk, setRisk1, setRisk2)
            groupSpace = 0.13f
            barSpace = 0.02f
            barWidth = 0.26f
        }

        data.setValueTextSize(12f)
        data.barWidth = barWidth // set the width of each bar

        binding.barChartHeartRisk.data = data
        binding.barChartHeartRisk.groupBars(
            0f,
            groupSpace,
            barSpace
        ) // perform the "explicit" grouping

        binding.barChartHeartRisk.setDrawValueAboveBar(true)
        binding.barChartHeartRisk.invalidate() // refresh
    }

    @SuppressLint("SetTextI18n")
    private fun setClickable() {

        if (calculatorDataSingleton!!.heartAgeSummeryList.size >= 1) {
            binding.btnRecalculate.text = resources.getString(R.string.RESTART_ASSESSMENT)
        }

        binding.btnRecalculate.setOnClickListener {
            if (binding.btnRecalculate.text.toString()
                    .equals(Constants.RESTART, ignoreCase = true)
            ) {
                findNavController().navigate(R.id.action_hypertensionSummeryFragment_to_hypertensionInputFragment)
            } else {
                findNavController().navigate(R.id.action_hypertensionSummeryFragment_to_hypertensionRecalculateFragment)
            }
        }

        binding.btnViewReportGraph.setOnClickListener {
            findNavController().navigate(R.id.action_hypertensionSummeryFragment_to_hypertensionReportFragment)
        }

    }

}
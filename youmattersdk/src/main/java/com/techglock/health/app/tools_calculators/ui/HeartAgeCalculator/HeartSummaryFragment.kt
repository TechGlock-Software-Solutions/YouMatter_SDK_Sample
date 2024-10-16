package com.techglock.health.app.tools_calculators.ui.HeartAgeCalculator

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
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
import com.techglock.health.app.databinding.FragmentHeartSummaryBinding
import com.techglock.health.app.model.toolscalculators.UserInfoModel
import com.techglock.health.app.tools_calculators.model.CalculatorDataSingleton
import com.techglock.health.app.tools_calculators.model.HeartAgeSummeryModel
import com.techglock.health.app.tools_calculators.viewmodel.ToolsCalculatorsViewModel
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HeartSummaryFragment : BaseFragment() {

    private val viewModel: ToolsCalculatorsViewModel by lazy {
        ViewModelProvider(this)[ToolsCalculatorsViewModel::class.java]
    }
    private lateinit var binding: FragmentHeartSummaryBinding

    private var yourAge = 0
    private var heartAge = 0
    private var riskPercent = 0
    private var calculatorDataSingleton: CalculatorDataSingleton? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { }

        // callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.btnRecalculate.visibility != View.VISIBLE) {
                    calculatorDataSingleton!!.clearData()
                    if (requireActivity().intent.hasExtra(Constants.TO)) {
                        val extra = requireActivity().intent.getStringExtra(Constants.TO)
                        Utilities.printLog("CalculatorTO=> $extra")
                        if (extra.equals("DASH", true)) {
                            findNavController().navigate(R.id.action_hypertensionSummeryFragment_to_toolsCalculatorsDashboardFragment)
                        } else {
                            UserInfoModel.getInstance()!!.isDataLoaded = false
                            requireActivity().finish()
                        }
                    }
                } else {
                    findNavController().navigateUp()
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
        binding = FragmentHeartSummaryBinding.inflate(inflater, container, false)
        calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!
        initialise()
        initialiseChart()
        setClickable()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initialise() {
        CleverTapHelper.pushEvent(
            requireContext(),
            CleverTapConstants.HEART_AGE_CALCULATOR_SUMMARY_SCREEN
        )
        binding.layoutHeartAgeView.visibility = View.VISIBLE
        binding.layoutHeartRiskView.visibility = View.GONE
        binding.indicatorYourAge.setOnTouchListener { _: View?, _: MotionEvent? -> true }
        binding.indicatorHeartAge.setOnTouchListener { _: View?, _: MotionEvent? -> true }
        binding.indicatorHeartRisk.setOnTouchListener { _: View?, _: MotionEvent? -> true }

        binding.layoutYourAge.setRangeTitle(resources.getString(R.string.YOUR_AGE))
        binding.layoutHeartAge.setRangeTitle(resources.getString(R.string.HEART_AGE))

        binding.layoutYourAge.setViewColor(R.color.colorPrimary)
        binding.layoutYourAge.setViewBackgroundColor(R.color.primary_purple_light)

        try {
            yourAge = calculatorDataSingleton!!.personAge.toInt()
            heartAge = calculatorDataSingleton!!.heartAge.toInt()
            val riskPer: Double = calculatorDataSingleton!!.riskScorePercentage.toDouble()
            riskPercent = riskPer.toInt()
            seHeartAgeIndicatorDetails(heartAge, yourAge)
            setYourIndicatorDetails(yourAge, R.color.colorPrimary, heartAge)
            seHeartRiskIndicatorDetails(riskPer, calculatorDataSingleton!!.riskLabel)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setClickable() {

        binding.layoutHeartTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 0) {
                    binding.layoutHeartAgeView.visibility = View.VISIBLE
                    binding.layoutHeartRiskView.visibility = View.GONE
                    binding.barChartHeartAge.visibility = View.VISIBLE
                    binding.barChartHeartRisk.visibility = View.INVISIBLE
                } else if (tab.position == 1) {
                    binding.layoutHeartRiskView.visibility = View.VISIBLE
                    binding.layoutHeartAgeView.visibility = View.GONE
                    binding.barChartHeartAge.visibility = View.INVISIBLE
                    binding.barChartHeartRisk.visibility = View.VISIBLE
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        binding.btnRecalculate.setOnClickListener {
            findNavController().navigate(R.id.action_heartSummaryFragment_to_heartAgeRecalculateFragment)
        }

        binding.btnViewReport.setOnClickListener {
            findNavController().navigate(R.id.action_heartSummaryFragment_to_heartReportFragment)
        }

    }

    private fun seHeartAgeIndicatorDetails(heartAge: Int, yourAge: Int) {
        if (heartAge < yourAge) {
            //binding.indicatorHeartAge.progressColor = ContextCompat.getColor(requireContext(), R.color.risk_normal)
            binding.indicatorHeartAge.setBarColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.risk_normal
                )
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //binding.view2.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.vivant_green_blue_two)
                binding.layoutHeartAge.setViewColor(R.color.risk_normal)
                binding.layoutHeartAge.setViewBackgroundColor(R.color.bg_risk_normal)
            }
        } else {
            //binding.indicatorHeartAge.progressColor = ContextCompat.getColor(requireContext(), R.color.risk_extremly_severe)
            binding.indicatorHeartAge.setBarColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.risk_severe
                )
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //binding.view2.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.vivant_watermelon)
                binding.layoutHeartAge.setViewColor(R.color.risk_severe)
                binding.layoutHeartAge.setViewBackgroundColor(R.color.bg_risk_severe)
            }
        }
//        binding.indicatorHeartAge.progress = heartAge
        //binding.indicatorHeartAge.setProgressWithAnimation(heartAge.toDouble())
        binding.indicatorHeartAge.setValueAnimated(
            heartAge.toFloat(),
            Constants.ANIMATION_DURATION.toLong()
        )
        //binding.txtHeartAge.text = "$heartAge "+requireContext().getString(R.string.YEARS)
        binding.layoutHeartAge.setRangeValue("$heartAge " + requireContext().getString(R.string.YRS))
        binding.txtAgeHeart.text = heartAge.toString()
    }

    private fun setYourIndicatorDetails(yourAge: Int, heartColor: Int, heartAge: Int) {
        if (heartAge > 100) {
            //binding.indicatorYourAge.max = heartAge
            binding.indicatorYourAge.maxValue = heartAge.toFloat()
        }
        //binding.indicatorYourAge.progressColor = ContextCompat.getColor(requireContext(), heartColor)
        binding.indicatorYourAge.setBarColor(ContextCompat.getColor(requireContext(), heartColor))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //binding.indicatorYourAge.mThumb!!.setTint(ContextCompat.getColor(requireContext(), heartColor))
        }
        //binding.indicatorYourAge.progress = yourAge
        //binding.indicatorYourAge.setProgressWithAnimation(yourAge.toDouble())
        binding.indicatorYourAge.setValueAnimated(
            yourAge.toFloat(),
            Constants.ANIMATION_DURATION.toLong()
        )
        //binding.txtYourAge.text = "$yourAge "+requireContext().getString(R.string.YEARS)
        binding.layoutYourAge.setRangeValue("$yourAge " + requireContext().getString(R.string.YRS))
    }

    @SuppressLint("SetTextI18n")
    private fun seHeartRiskIndicatorDetails(riskPercentage: Double, riskType: String) {

        val heartColour: Int = when {
            riskPercentage <= 10 -> {
                R.color.risk_normal
            }
//            riskPercentage in 1.0..10.0 -> {
//                R.color.vivant_marigold
//            }
            riskPercentage in 11.0..20.0 -> {
                R.color.vivant_orange_yellow
            }

            else -> {
                R.color.risk_severe
            }
        }

        //binding.indicatorHeartRisk.progressColor = ContextCompat.getColor(requireContext(), heartColour)
        binding.indicatorHeartRisk.setBarColor(resources.getColor(heartColour))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //binding.indicatorHeartRisk.mThumb!!.setTint(ContextCompat.getColor(requireContext(), heartColour))
            //binding.layoutHeartRiskDetail.backgroundTintList = AppCompatResources.getColorStateList(requireContext(), heartColour)
        }
        try {
            val finalRiskPercent = riskPercent
            //binding.indicatorHeartRisk.progress = riskPercent
            //binding.indicatorHeartRisk.setProgressWithAnimation(riskPercent.toDouble())
            binding.indicatorHeartRisk.setValueAnimated(
                riskPercentage.toFloat(),
                Constants.ANIMATION_DURATION.toLong()
            )
            binding.txtRiskPercent.text = "$finalRiskPercent%"
            binding.txtRisk.text = riskType
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        if (calculatorDataSingleton!!.heartAgeSummeryList.size >= 3) {
            binding.btnRecalculate.visibility = View.INVISIBLE
        }
        if (calculatorDataSingleton!!.heartAgeSummeryList.size <= 1) {
            binding.layoutHeart.visibility = View.VISIBLE
            binding.layoutHeartAgeChart.visibility = View.GONE
        } else
            if (calculatorDataSingleton!!.heartAgeSummeryList.size > 1) {
                binding.layoutHeart.visibility = View.INVISIBLE
                binding.layoutHeartAgeChart.visibility = View.VISIBLE
            }
    }

    private fun initialiseChart() {
        setHeartAgeChartData()
        setHeartRiskChartData()

        val rightAxis: YAxis = binding.barChartHeartAge.axisRight
        rightAxis.setDrawGridLines(false)
        rightAxis.setDrawLabels(false)
        val leftAxis: YAxis = binding.barChartHeartAge.axisLeft
        leftAxis.textColor = ContextCompat.getColor(requireContext(), R.color.textViewColor)
        leftAxis.setLabelCount(5, true)
        leftAxis.setDrawTopYLabelEntry(true)
        leftAxis.setDrawZeroLine(true)
        leftAxis.granularity = 1f
        leftAxis.axisMinimum = 0f
        binding.barChartHeartAge.setFitBars(true) // make the x-axis fit exactly all bars
        binding.barChartHeartAge.legend.isEnabled = false
        val description = Description()
        description.text = ""
        binding.barChartHeartAge.setTouchEnabled(false)
        binding.barChartHeartAge.isDoubleTapToZoomEnabled = false
        binding.barChartHeartAge.description = description
        binding.barChartHeartAge.invalidate() // refresh

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
        binding.barChartHeartRisk.legend.isEnabled = false
        binding.barChartHeartRisk.setTouchEnabled(false)
        binding.barChartHeartRisk.isDoubleTapToZoomEnabled = false
        val descriptionRisk = Description()
        descriptionRisk.text = ""
        binding.barChartHeartRisk.description = descriptionRisk
        binding.barChartHeartRisk.invalidate() // refresh
    }

    private fun setHeartAgeChartData() {
        val entries: ArrayList<BarEntry> = ArrayList()
        val personAge: Float = calculatorDataSingleton!!.personAge.toFloat()
        entries.add(BarEntry(0f, personAge))
        val summeryModelArrayList: ArrayList<HeartAgeSummeryModel> =
            calculatorDataSingleton!!.heartAgeSummeryList
        val colorArray = IntArray(summeryModelArrayList.size + 1)
        colorArray[0] = ContextCompat.getColor(requireContext(), R.color.colorAccent)
        for (i in 1..summeryModelArrayList.size) {
            val heartAge: Float = summeryModelArrayList[i - 1].heartAge.toFloat()
            entries.add(BarEntry(i.toFloat(), heartAge))
            if (personAge <= heartAge) {
                colorArray[i] = ContextCompat.getColor(requireContext(), R.color.risk_severe)
            } else {
                colorArray[i] = ContextCompat.getColor(requireContext(), R.color.risk_normal)
            }
        }
        val set = BarDataSet(entries, "BarDataSet")
        set.colors = colorArray.toMutableList()
        set.valueTextSize = 10f
        //set.setColors(colorArray, this)
        set.valueTextColor = ContextCompat.getColor(requireContext(), R.color.textViewColor)
        val data = BarData(set)
        //data.setBarWidth(0.9f); // set custom bar width
        binding.barChartHeartAge.data = data

        // the labels that should be drawn on the XAxis
        val xAxis: XAxis = binding.barChartHeartAge.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 10f
        xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.textViewColor)
        xAxis.setDrawAxisLine(true)
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter =
            IAxisValueFormatter { value, _ ->
                var name = resources.getString(R.string.YOUR_AGE)
                if (value > 0) {
                    name = "${resources.getString(R.string.HEART_AGE)} " + value.toInt()
                }
                name
            }
        binding.barChartHeartAge.invalidate() // refresh
    }

    private fun setHeartRiskChartData() {
        val entries: MutableList<BarEntry> = ArrayList()
        val summeryModelArrayList: ArrayList<HeartAgeSummeryModel> =
            calculatorDataSingleton!!.heartAgeSummeryList
        val colorArray = IntArray(summeryModelArrayList.size)
        for (i in summeryModelArrayList.indices) {
            val heartRisk: Float = summeryModelArrayList[i].heartRisk.toFloat()
            val heartColour: Int = when {
                heartRisk <= 10 -> {
                    R.color.risk_normal
                }

                heartRisk in 11.0..20.0 -> {
                    R.color.vivant_orange_yellow
                }

                else -> {
                    R.color.risk_severe
                }
            }
//            if (heartRisk < 1) {
//                colorArray[i] = ContextCompat.getColor(requireContext(), R.color.colorAccent)
//            } else if (heartRisk in 1.0..10.0) {
//                colorArray[i] = ContextCompat.getColor(requireContext(), R.color.dia_ichi_grey)
//            } else if (heartRisk > 10 && heartRisk <= 20) {
//                colorArray[i] = ContextCompat.getColor(requireContext(), R.color.dia_ichi_grey_dark)
//            } else {
//                colorArray[i] = ContextCompat.getColor(requireContext(), R.color.textViewColor)
//            }
            colorArray[i] = ContextCompat.getColor(requireContext(), heartColour)
            entries.add(BarEntry(i.toFloat(), heartRisk))
        }
        val set = BarDataSet(entries, "BarDataSet")
        set.colors = colorArray.toMutableList()
        set.valueTextSize = 10f
        //set.setColors(colorArray, this)
        set.valueTextColor = ContextCompat.getColor(requireContext(), R.color.textViewColor)
        val data = BarData(set)
        //        data.setBarWidth(0.9f); // set custom bar width
        binding.barChartHeartRisk.data = data
        val xAxis: XAxis = binding.barChartHeartRisk.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 10f
        xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.textViewColor)
        xAxis.setDrawAxisLine(true)
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = IAxisValueFormatter { value, _ ->
            "${resources.getString(R.string.HEART_RISK)} " + (value.toInt() + 1)
        }
        binding.barChartHeartRisk.invalidate() // refresh
    }

}

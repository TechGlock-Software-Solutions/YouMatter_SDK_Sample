package com.techglock.health.app.fitness_tracker.ui

import android.annotation.SuppressLint
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.fitness.FitnessDataManager
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentYearBinding
import com.techglock.health.app.fitness_tracker.adapter.YearlyAdapter
import com.techglock.health.app.fitness_tracker.adapter.YearlyMonthDataAdapter
import com.techglock.health.app.fitness_tracker.util.FitnessHelper
import com.techglock.health.app.fitness_tracker.viewmodel.ActivityTrackerViewModel
import com.techglock.health.app.model.entity.FitnessEntity
import com.techglock.health.app.model.fitness.MonthModel
import com.techglock.health.app.model.fitness.YearModel
import com.techglock.health.app.model.fitness.YearlyMonthDataModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class YearFragment : BaseFragment(), YearlyAdapter.OnYearItemClickListener {

    private val viewModel: ActivityTrackerViewModel by lazy {
        ViewModelProvider(this)[ActivityTrackerViewModel::class.java]
    }
    private lateinit var binding: FragmentYearBinding
    private val appColorHelper = AppColorHelper.instance!!

    @Inject
    lateinit var fitnessHelper: FitnessHelper

    private var fitnessDataActivity: FitnessDataActivity? = null
    private var fitnessDataManager: FitnessDataManager? = null
    private var yearlyAdapter: YearlyAdapter? = null
    private var yearlyMonthDataAdapter: YearlyMonthDataAdapter? = null

    private var year = ""
    private var startDate = ""
    private var endDate = ""
    private var type = ""
    private var monthlyList: MutableList<MonthModel> = mutableListOf()
    private var yearlyDataList: MutableList<YearlyMonthDataModel> = mutableListOf()
    private var animation: LayoutAnimationController? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentYearBinding.inflate(inflater, container, false)
        if (userVisibleHint) {
            try {
                initialise()
                setClickable()
                registerObservers()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return binding.root
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && isResumed) {
            try {
                initialise()
                setClickable()
                registerObservers()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun initialise() {
        //Utilities.printLogError("Inside Month Fragment")
        animation =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_slide_from_bottom)
        fitnessDataManager = FitnessDataManager(context)
        fitnessDataActivity = (activity as FitnessDataActivity)
        binding.txtJoiningDate.text = DateHelper.convertDateTimeValue(
            viewModel.joiningDate,
            DateHelper.SERVER_DATE_YYYYMMDD,
            DateHelper.DATEFORMAT_DDMMMYYYY_NEW
        )
        val yearlyList = fitnessDataActivity!!.yearlyList

        binding.graphYearly.visibility = View.INVISIBLE
        binding.graphYearly.clear()

        yearlyAdapter = YearlyAdapter(
            requireContext(),
            DateHelper.convertDateTimeValue(
                viewModel.joiningDate,
                DateHelper.SERVER_DATE_YYYYMMDD,
                DateHelper.DATEFORMAT_DDMMMYYYY_NEW
            )!!,
            this
        )
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvWeekMonth.layoutManager = layoutManager
        binding.rvWeekMonth.layoutManager!!.scrollToPosition(yearlyList.size - 1)
        binding.rvWeekMonth.adapter = yearlyAdapter
        yearlyAdapter!!.updateList(yearlyList)

        yearlyMonthDataAdapter =
            YearlyMonthDataAdapter(requireContext(), Constants.STEPS_COUNT, fitnessHelper)
        binding.rvDailyData.layoutAnimation = animation
        binding.rvDailyData.adapter = yearlyMonthDataAdapter

        setStepsView()
        binding.txtData.text = resources.getString(R.string.STEPS)
        binding.graphUnit.text = resources.getString(R.string.STEPS)
    }

    private fun setClickable() {

        binding.btnTotalSteps.setOnClickListener {
            setStepsView()
            if (binding.txtData.text != resources.getString(R.string.STEPS)) {
                //viewModel.showProgressBar()
                setGraphData()
                binding.rvDailyData.layoutAnimation = animation
                yearlyMonthDataAdapter!!.updateType(Constants.STEPS_COUNT)
                binding.rvDailyData.scheduleLayoutAnimation()
            }
            binding.txtData.text = resources.getString(R.string.STEPS)
            binding.graphUnit.text = resources.getString(R.string.STEPS)
        }

        binding.btnTotalDistance.setOnClickListener {
            setDistanceView()
            if (binding.txtData.text != resources.getString(R.string.DISTANCE)) {
                //viewModel.showProgressBar()
                setGraphData()
                binding.rvDailyData.layoutAnimation = animation
                yearlyMonthDataAdapter!!.updateType(Constants.DISTANCE)
                binding.rvDailyData.scheduleLayoutAnimation()
            }
            binding.txtData.text = resources.getString(R.string.DISTANCE)
            binding.graphUnit.text = resources.getString(R.string.KM)
        }

        binding.btnTotalCalories.setOnClickListener {
            setCaloriesView()
            if (binding.txtData.text != resources.getString(R.string.CALORIES)) {
                //viewModel.showProgressBar()
                setGraphData()
                binding.rvDailyData.layoutAnimation = animation
                yearlyMonthDataAdapter!!.updateType(Constants.CALORIES)
                binding.rvDailyData.scheduleLayoutAnimation()
            }
            binding.txtData.text = resources.getString(R.string.CALORIES)
            binding.graphUnit.text = resources.getString(R.string.KCAL)
        }

    }

    private fun registerObservers() {

        viewModel.stepsHistoryBetweenList.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                //Utilities.printData("YearlyApiHistory",data.stepGoalHistory,true)
                showFitnessData(data.stepGoalHistory)
            }
        }

    }

    override fun onYearlyItemSelection(position: Int, yearModel: YearModel) {
        try {
            year = yearModel.year
            startDate = yearModel.startDate
            endDate = yearModel.endDate
            //Utilities.printLogError("Year,StartDate,EndDate--->$year , $startDate , $endDate")
            if (Utilities.isNullOrEmpty(type)) {
                type = Constants.STEPS_COUNT
                binding.txtData.text = resources.getString(R.string.STEPS)
                binding.graphUnit.text = resources.getString(R.string.STEPS)
            }
            monthlyList = fitnessHelper.getAllMonthsOfYear(year.toInt())
            viewModel.getStepsHistoryBetweenDates(startDate, endDate)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showFitnessData(yearlyHistoryFromApi: List<FitnessEntity.StepGoalHistory>) {
        try {
            yearlyDataList.clear()
            var totalSteps = 0.0
            var totalDistance = 0.0
            var totalCalories = 0.0
            var yearlyMonthData: YearlyMonthDataModel

            if (year == DateHelper.currentYearAsStringyyyy && !yearlyHistoryFromApi.isNullOrEmpty() && yearlyHistoryFromApi.any { it.recordDate == fitnessDataActivity!!.currentDate }) {
                val todayData =
                    yearlyHistoryFromApi.find { it.recordDate == fitnessDataActivity!!.currentDate }!!
                if (todayData != null) {
                    todayData.stepID = fitnessDataActivity!!.todayFitnessData.stepID
                    todayData.goalID = fitnessDataActivity!!.todayFitnessData.goalID
                    todayData.recordDate = fitnessDataActivity!!.currentDate
                    todayData.stepsCount = fitnessDataActivity!!.todayFitnessData.stepsCount
                    todayData.totalGoal = fitnessDataActivity!!.todayFitnessData.totalGoal
                    todayData.distance = fitnessDataActivity!!.todayFitnessData.distance
                    todayData.calories = fitnessDataActivity!!.todayFitnessData.calories
                    todayData.goalPercentile = fitnessDataActivity!!.todayFitnessData.goalPercentile
                    todayData.activeTime = fitnessDataActivity!!.todayFitnessData.activeTime
                }
            }

            if (!yearlyHistoryFromApi.isNullOrEmpty()) {
                for (month in monthlyList) {
                    totalSteps = 0.0
                    totalDistance = 0.0
                    totalCalories = 0.0
                    for (item in yearlyHistoryFromApi) {
                        if (item.recordDate.contains(month.monthOfYear)) {
                            totalSteps += item.stepsCount.toDouble()
                            totalDistance += item.distance
                            totalCalories += item.calories.toDouble()
                        }
                    }
                    yearlyMonthData = YearlyMonthDataModel()
                    yearlyMonthData.totalSteps = totalSteps.toInt()
                    yearlyMonthData.totalDistance = totalDistance
                    yearlyMonthData.totalCalories = totalCalories.toInt()
                    yearlyMonthData.monthYear = month.month
                    yearlyMonthData.year = month.year
                    yearlyDataList.add(yearlyMonthData)
                }
            }

            Utilities.printData("yearlyDataList", yearlyDataList)
            calculateTotalData(yearlyHistoryFromApi.size)
            setGraphData()

            if (yearlyDataList.isNotEmpty()) {
                binding.layoutData.visibility = View.VISIBLE
                binding.layoutNoData.visibility = View.GONE
                binding.rvDailyData.layoutAnimation = animation
                yearlyMonthDataAdapter!!.updateList(yearlyDataList)
                binding.rvDailyData.scheduleLayoutAnimation()
            } else {
                binding.layoutData.visibility = View.GONE
                binding.layoutNoData.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun calculateTotalData(totalDays: Int) {
        var totalSteps = 0
        var totalDistance = 0.0
        var totalCalories = 0

        for (item in yearlyDataList) {
            if (item.totalSteps != 0) {
                totalSteps += item.totalSteps
                totalDistance += item.totalDistance
                totalCalories += item.totalCalories
            }
        }

        //Utilities.printLogError("totalDays----->$totalDays")

        if (totalSteps != 0) {
            totalSteps /= totalDays
        }
        if (totalDistance != 0.0) {
            totalDistance /= totalDays
        }
        if (totalCalories != 0) {
            totalCalories /= totalDays
        }

        val steps = totalSteps
        val dist: Double = Utilities.roundOffPrecision(totalDistance, 2)
        val calories = totalCalories

        binding.btnTotalSteps.text =
            Utilities.formatNumberWithComma(steps) + "\n${resources.getString(R.string.AVG_STEPS)}"
        binding.btnTotalDistance.text = "${
            Utilities.formatNumberWithComma(
                fitnessHelper.convertMtrToKmsValueNew(dist.toString()).toDouble()
            )
        } km\n${resources.getString(R.string.AVG_DISTANCE)}"
        binding.btnTotalCalories.text =
            Utilities.formatNumberWithComma(calories) + " Kcal\n${resources.getString(R.string.AVG_CALORIES)}"
    }

    /*    @SuppressLint("SetTextI18n")
        private fun calculateTotalData() {
            var totalSteps = 0
            var totalDistance = 0.0
            var totalCalories = 0
            var dataSize = 0

            for (item in yearlyDataList) {
                if ( item.totalSteps != 0 ) {
                    totalSteps += item.totalSteps
                    totalDistance += item.totalDistance
                    totalCalories += item.totalCalories
                    dataSize++
                }
            }

            if ( totalSteps != 0 ) {
                totalSteps /= dataSize
            }
            if ( totalDistance != 0.0 ) {
                totalDistance /= dataSize
            }
            if ( totalCalories != 0 ) {
                totalCalories /= dataSize
            }

            val steps = totalSteps
            val dist: Double = Utilities.roundOffPrecision(totalDistance, 2)
            val calories = totalCalories

            binding.btnTotalSteps.text = Utilities.formatNumberWithComma(steps) + "\nAvg Steps"
            binding.btnTotalDistance.text = "${Utilities.formatNumberWithComma(fitnessHelper.convertMtrToKmsValueNew(dist.toString()).toDouble())} km\nAvg Distance"
            binding.btnTotalCalories.text = Utilities.formatNumberWithComma(calories) + " Kcal\nAvg Calories"
        }*/

    private fun setGraphData() {
        try {
            binding.graphYearly.clear()
            binding.graphYearly.invalidate()
            if (yearlyDataList.isNotEmpty()) {
                binding.graphYearly.visibility = View.VISIBLE
                binding.graphUnit.visibility = View.VISIBLE
                val yAxis: YAxis = binding.graphYearly.axisLeft
                yAxis.setDrawTopYLabelEntry(true)
                yAxis.setDrawZeroLine(false)
                yAxis.granularity = 1f
                yAxis.axisMinimum = 0f
                val xAxis: XAxis = binding.graphYearly.xAxis
                xAxis.labelCount = 12
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawAxisLine(true)
                xAxis.textSize = 9f
                xAxis.granularity = 1f
                xAxis.setDrawGridLines(false)
                xAxis.axisMinimum = 0f
                //xAxis.setAxisMaximum(5);
                xAxis.valueFormatter = object : IAxisValueFormatter {
                    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                        for (i in 1..yearlyDataList.size) {
                            if (value == i.toFloat()) {
                                //return yearlyDataList[i - 1].monthYear
                                return yearlyDataList[i - 1].monthYear.substring(0, 1)
                            }
                        }
                        return ""
                    }
                }
                val entries: MutableList<BarEntry> = ArrayList()
                entries.add(BarEntry(0.0f, 0.0f))
                when (type) {
                    Constants.STEPS_COUNT -> {
                        var i = 0
                        while (i < yearlyDataList.size) {
                            if (Utilities.isNullOrEmptyOrZero(yearlyDataList[i].totalSteps.toString())) {
                                entries.add(BarEntry((i + 1).toFloat(), 0.0f))
                            } else {
                                entries.add(
                                    BarEntry(
                                        (i + 1).toFloat(),
                                        yearlyDataList[i].totalSteps.toFloat()
                                    )
                                )
                            }
                            i++
                        }
                    }

                    Constants.DISTANCE -> {
                        var i = 0
                        while (i < yearlyDataList.size) {
                            if (Utilities.isNullOrEmptyOrZero(yearlyDataList[i].totalDistance.toString())) {
                                entries.add(BarEntry((i + 1).toFloat(), 0.0f))
                            } else {
                                entries.add(
                                    BarEntry(
                                        (i + 1).toFloat(),
                                        fitnessHelper.convertMtrToKmsValueNew(yearlyDataList[i].totalDistance.toString())
                                            .toFloat()
                                    )
                                )
                            }
                            i++
                        }
                    }

                    Constants.CALORIES -> {
                        var i = 0
                        while (i < yearlyDataList.size) {
                            if (Utilities.isNullOrEmptyOrZero(yearlyDataList[i].totalCalories.toString())) {
                                entries.add(BarEntry((i + 1).toFloat(), 0.0f))
                            } else {
                                entries.add(
                                    BarEntry(
                                        (i + 1).toFloat(),
                                        yearlyDataList[i].totalCalories.toFloat()
                                    )
                                )
                            }
                            i++
                        }
                    }
                }
                val barDataSet = BarDataSet(entries, "")
                barDataSet.setDrawValues(false)
                barDataSet.setColors(
                    ContextCompat.getColor(
                        requireContext(),
                        fitnessHelper.getColorBasedOnType(type)
                    )
                )
                barDataSet.valueTextColor =
                    ContextCompat.getColor(requireContext(), R.color.textViewColor)
                barDataSet.valueTextSize = 9f
                barDataSet.color = ContextCompat.getColor(
                    requireContext(),
                    fitnessHelper.getColorBasedOnType(type)
                )
                val dataSets = ArrayList<IBarDataSet>()
                dataSets.add(barDataSet)
                val data = BarData(dataSets)
                //data.barWidth = 0.5f
                binding.graphYearly.data = data
                binding.graphYearly.description.isEnabled = false
                binding.graphYearly.legend.isEnabled = false
                binding.graphYearly.axisRight.isEnabled = false
                binding.graphYearly.isDoubleTapToZoomEnabled = false
                binding.graphYearly.setTouchEnabled(false)
                binding.graphYearly.isHighlightPerTapEnabled = false
                binding.graphYearly.isDragEnabled = false
                binding.graphYearly.setPinchZoom(false)
                binding.graphYearly.isHighlightPerTapEnabled = false
                binding.graphYearly.setDrawValueAboveBar(false)
                binding.graphYearly.setVisibleXRangeMaximum(13f)
                binding.graphYearly.invalidate()
                //binding.graphYearly.animateY(1200);
                binding.graphYearly.animateXY(1000, 1000)
            } else {
                binding.graphUnit.visibility = View.GONE
                binding.graphYearly.invalidate()
                //binding.graphYearly.clearValues();
                binding.graphYearly.notifyDataSetChanged()
                binding.graphYearly.setNoDataText(resources.getString(R.string.NO_HISTORY_AVAILABLE))
                binding.graphYearly.setNoDataTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.textViewColorBlack
                    )
                )
                val paint: Paint = binding.graphYearly.getPaint(BarChart.PAINT_INFO)
                paint.textSize = resources.getDimension(com.intuit.sdp.R.dimen._15sdp)
                paint.typeface = Typeface.DEFAULT_BOLD
            }
            /*            Handler(Looper.getMainLooper()).postDelayed({
                viewModel.hideProgressBar()
            }, 1000)*/
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun setStepsView() {
        binding.btnTotalSteps.background =
            ResourcesCompat.getDrawable(resources, R.drawable.btn_fill_selected, null)
        binding.btnTotalSteps.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.btnTotalDistance.background =
            ResourcesCompat.getDrawable(resources, R.drawable.btn_border_deselected, null)
        binding.btnTotalDistance.setTextColor(appColorHelper.textColor)
        binding.btnTotalCalories.background =
            ResourcesCompat.getDrawable(resources, R.drawable.btn_border_deselected, null)
        binding.btnTotalCalories.setTextColor(appColorHelper.textColor)
        type = Constants.STEPS_COUNT
        binding.btnTotalSteps.background.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                ContextCompat.getColor(
                    requireContext(),
                    fitnessHelper.getColorBasedOnType(type)
                ), BlendModeCompat.SRC_ATOP
            )
    }

    private fun setDistanceView() {
        binding.btnTotalSteps.background =
            ResourcesCompat.getDrawable(resources, R.drawable.btn_border_deselected, null)
        binding.btnTotalSteps.setTextColor(appColorHelper.textColor)
        binding.btnTotalDistance.background =
            ResourcesCompat.getDrawable(resources, R.drawable.btn_fill_selected, null)
        binding.btnTotalDistance.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        binding.btnTotalCalories.background =
            ResourcesCompat.getDrawable(resources, R.drawable.btn_border_deselected, null)
        binding.btnTotalCalories.setTextColor(appColorHelper.textColor)
        type = Constants.DISTANCE
        binding.btnTotalDistance.background.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                ContextCompat.getColor(
                    requireContext(),
                    fitnessHelper.getColorBasedOnType(type)
                ), BlendModeCompat.SRC_ATOP
            )
    }

    private fun setCaloriesView() {
        binding.btnTotalSteps.background =
            ResourcesCompat.getDrawable(resources, R.drawable.btn_border_deselected, null)
        binding.btnTotalSteps.setTextColor(appColorHelper.textColor)
        binding.btnTotalDistance.background =
            ResourcesCompat.getDrawable(resources, R.drawable.btn_border_deselected, null)
        binding.btnTotalDistance.setTextColor(appColorHelper.textColor)
        binding.btnTotalCalories.background =
            ResourcesCompat.getDrawable(resources, R.drawable.btn_fill_selected, null)
        binding.btnTotalCalories.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            )
        )
        type = Constants.CALORIES
        binding.btnTotalCalories.background.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                ContextCompat.getColor(
                    requireContext(),
                    fitnessHelper.getColorBasedOnType(type)
                ), BlendModeCompat.SRC_ATOP
            )
    }

}
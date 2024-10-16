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
import com.techglock.health.app.databinding.FragmentWeekBinding
import com.techglock.health.app.fitness_tracker.adapter.DailyDataAdapter
import com.techglock.health.app.fitness_tracker.adapter.WeeklyAdapter
import com.techglock.health.app.fitness_tracker.util.FitnessHelper
import com.techglock.health.app.fitness_tracker.viewmodel.ActivityTrackerViewModel
import com.techglock.health.app.model.entity.FitnessEntity.StepGoalHistory
import com.techglock.health.app.model.fitness.WeekModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class WeekFragment : BaseFragment(), WeeklyAdapter.OnWeeklyItemClickListener {

    private val viewModel: ActivityTrackerViewModel by lazy {
        ViewModelProvider(this)[ActivityTrackerViewModel::class.java]
    }
    private lateinit var binding: FragmentWeekBinding
    private val appColorHelper = AppColorHelper.instance!!

    @Inject
    lateinit var fitnessHelper: FitnessHelper

    private var fitnessDataActivity: FitnessDataActivity? = null
    private var weeklyAdapter: WeeklyAdapter? = null
    private var dailyDataAdapter: DailyDataAdapter? = null
    private var fitnessDataManager: FitnessDataManager? = null

    private var startDate = ""
    private var endDate = ""
    private var type = ""
    private var weeklyDataList: MutableList<StepGoalHistory> = mutableListOf()
    private var animation: LayoutAnimationController? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeekBinding.inflate(inflater, container, false)
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
        //Utilities.printLogError("Inside Week Fragment")
        animation =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_slide_from_bottom)
        fitnessDataManager = FitnessDataManager(context)
        fitnessDataActivity = (activity as FitnessDataActivity)
        binding.txtJoiningDate.text = DateHelper.convertDateTimeValue(
            viewModel.joiningDate,
            DateHelper.SERVER_DATE_YYYYMMDD,
            DateHelper.DATEFORMAT_DDMMMYYYY_NEW
        )!!
        val weeklyList = fitnessDataActivity!!.weeklyList

        binding.graphWeekly.visibility = View.INVISIBLE
        binding.graphWeekly.clear()

        weeklyAdapter = WeeklyAdapter(
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
        binding.rvWeekMonth.layoutManager!!.scrollToPosition(weeklyList.size - 1)
        binding.rvWeekMonth.adapter = weeklyAdapter

        if ((activity as FitnessDataActivity).screen.equals(
                "STEPS_WEEKLY_SYNOPSIS",
                ignoreCase = true
            )
        ) {
            weeklyAdapter!!.updateList(weeklyList, true)
        } else {
            weeklyAdapter!!.updateList(weeklyList, false)
        }

        dailyDataAdapter = DailyDataAdapter(requireContext(), Constants.STEPS_COUNT, fitnessHelper)
        binding.rvDailyData.layoutAnimation = animation
        binding.rvDailyData.adapter = dailyDataAdapter

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
                dailyDataAdapter!!.updateType(Constants.STEPS_COUNT)
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
                dailyDataAdapter!!.updateType(Constants.DISTANCE)
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
                dailyDataAdapter!!.updateType(Constants.CALORIES)
                binding.rvDailyData.scheduleLayoutAnimation()
            }
            binding.txtData.text = resources.getString(R.string.CALORIES)
            binding.graphUnit.text = resources.getString(R.string.KCAL)
        }

    }

    private fun registerObservers() {

        viewModel.stepsHistoryBetweenList.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                //Utilities.printData("WeeklyApiHistory",data.stepGoalHistory,true)
                showFitnessData(data.stepGoalHistory)
            }
        }

    }

    override fun onWeeklyItemSelection(position: Int, week: WeekModel) {
        startDate = week.startDate
        endDate = week.endDate
        //Utilities.printLogError("StartDate,EndDate---> $startDate,$endDate")
        if (Utilities.isNullOrEmpty(type)) {
            type = Constants.STEPS_COUNT
            binding.txtData.text = resources.getString(R.string.STEPS)
            binding.graphUnit.text = resources.getString(R.string.STEPS)
        }
        viewModel.getStepsHistoryBetweenDates(startDate, endDate)
    }

    @SuppressLint("SetTextI18n")
    private fun calculateTotalData() {
        var totalSteps = 0
        var totalDistance = 0.0
        var totalCalories = 0
        var dataSize = 0

        for (item in weeklyDataList) {
            if (item.stepsCount != 0) {
                totalSteps += item.stepsCount
                totalDistance += item.distance
                totalCalories += item.calories
                dataSize++
            }
        }

        if (totalSteps != 0) {
            totalSteps /= dataSize
        }
        if (totalDistance != 0.0) {
            totalDistance /= dataSize
        }
        if (totalCalories != 0) {
            totalCalories /= dataSize
        }

        val steps = totalSteps
        val dist = Utilities.roundOffPrecision(totalDistance, 2)
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

    private fun showFitnessData(weeklyHistoryFromApi: List<StepGoalHistory>) {
        try {
            val weeklyFitnessDataList: ArrayList<StepGoalHistory> = ArrayList<StepGoalHistory>()
            val sd: DateFormat =
                SimpleDateFormat(DateHelper.SERVER_DATE_YYYYMMDD, Locale.getDefault())
            val fromDate = sd.parse(startDate)
            val toDate = sd.parse(endDate)

            var isCurrentWeek = false
            //var fitnessListSize: Int = DateHelper.getDateDifferenceInDays(startDate, endDate, DateHelper.SERVER_DATE_YYYYMMDD) + 1
            val fitnessListSize = 7

            var dataMap: StepGoalHistory
            if (weeklyHistoryFromApi.isNotEmpty() && weeklyHistoryFromApi.any { it.stepsCount != 0 }) {
                for (i in 0 until fitnessListSize) {
                    val recordDate = Calendar.getInstance()
                    recordDate.time = fromDate!!
                    recordDate.add(Calendar.DATE, i)
                    //String recordDateStr = sd.format(recordDate.getTime());
                    val recordDateStr: String = fitnessHelper.getFormattedValue(
                        DateHelper.SERVER_DATE_YYYYMMDD,
                        recordDate.time
                    )
                    //Utilities.printLogError("recordDateStr---> $recordDateStr")

                    if (recordDateStr.equals(
                            fitnessDataActivity!!.currentDate,
                            ignoreCase = true
                        )
                    ) {
                        isCurrentWeek = true
                    }

                    dataMap = StepGoalHistory(lastRefreshed = Date())
                    dataMap.stepID = 0
                    dataMap.goalID = 0
                    dataMap.recordDate = recordDateStr
                    dataMap.stepsCount = 0
                    dataMap.totalGoal = 0
                    dataMap.distance = 0.0
                    dataMap.calories = 0
                    dataMap.goalPercentile = 0.0
                    dataMap.activeTime = "0"

                    for (history in weeklyHistoryFromApi) {
                        if (!Utilities.isNullOrEmpty(history.recordDate)) {
                            if (recordDateStr.equals(
                                    history.recordDate.split("T")[0],
                                    ignoreCase = true
                                )
                            ) {
                                //dataMap = history;
                                dataMap.stepID = history.stepID
                                dataMap.goalID = history.goalID
                                dataMap.recordDate = recordDateStr
                                dataMap.stepsCount = history.stepsCount
                                dataMap.totalGoal = history.totalGoal
                                dataMap.distance = history.distance
                                dataMap.calories = history.calories
                                dataMap.goalPercentile = history.goalPercentile
                                dataMap.activeTime = history.activeTime
                            }
                            if (recordDateStr == fitnessDataActivity!!.currentDate) {
                                dataMap.stepID = fitnessDataActivity!!.todayFitnessData.stepID
                                dataMap.goalID = fitnessDataActivity!!.todayFitnessData.goalID
                                dataMap.recordDate = recordDateStr
                                dataMap.stepsCount =
                                    fitnessDataActivity!!.todayFitnessData.stepsCount
                                dataMap.totalGoal = fitnessDataActivity!!.todayFitnessData.totalGoal
                                dataMap.distance = fitnessDataActivity!!.todayFitnessData.distance
                                dataMap.calories = fitnessDataActivity!!.todayFitnessData.calories
                                dataMap.goalPercentile =
                                    fitnessDataActivity!!.todayFitnessData.goalPercentile
                                dataMap.activeTime =
                                    fitnessDataActivity!!.todayFitnessData.activeTime
                            }
                        }
                    }
                    weeklyFitnessDataList.add(dataMap)
                }
            }

            val filteredList = ArrayList<StepGoalHistory>()
            var d: Date
            if (!isCurrentWeek) {
                for (history in weeklyFitnessDataList) {
                    d = DateHelper.stringDateToDate(
                        DateHelper.SERVER_DATE_YYYYMMDD,
                        history.recordDate
                    )
                    if (d >= fromDate && d <= toDate) {
                        filteredList.add(history)
                    }
                }
            } else {
                filteredList.addAll(weeklyFitnessDataList)
            }

            weeklyDataList.clear()
            weeklyDataList.addAll(filteredList)
            Utilities.printData("weeklyDataList", weeklyDataList)
            calculateTotalData()
            setGraphData()
            if (weeklyDataList.isNotEmpty()) {
                binding.layoutData.visibility = View.VISIBLE
                binding.layoutNoData.visibility = View.GONE
                /*                val filteredList: ArrayList<StepGoalHistory> = ArrayList<StepGoalHistory>()
                                for (history in weeklyDataList) {
                                    if (DateHelper.stringDateToDate(DateHelper.SERVER_DATE_YYYYMMDD, history.recordDate).before(Date())) {
                                        filteredList.add(history)
                                    }
                                }*/
                binding.rvDailyData.layoutAnimation = animation
                dailyDataAdapter!!.updateList(weeklyDataList)
                binding.rvDailyData.scheduleLayoutAnimation()
            } else {
                binding.layoutData.visibility = View.GONE
                binding.layoutNoData.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setGraphData() {
        try {
            binding.graphWeekly.clear()
            binding.graphWeekly.invalidate()
            if (weeklyDataList.isNotEmpty()) {
                binding.graphWeekly.visibility = View.VISIBLE
                binding.graphUnit.visibility = View.VISIBLE

                val yAxis: YAxis = binding.graphWeekly.axisLeft
                yAxis.setDrawTopYLabelEntry(true)
                yAxis.setDrawZeroLine(false)
                yAxis.granularity = 1f
                yAxis.axisMinimum = 0f
                val xAxis: XAxis = binding.graphWeekly.xAxis
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawAxisLine(true)
                xAxis.granularity = 1f
                xAxis.setDrawGridLines(false)
                xAxis.textSize = 9f
                xAxis.axisMinimum = 0f
                //xAxis.setAxisMaximum(5);
                xAxis.valueFormatter = object : IAxisValueFormatter {
                    override fun getFormattedValue(value: Float, axis: AxisBase?): String? {
                        for (i in 1..weeklyDataList.size) {
                            if (value == i.toFloat()) {
                                return DateHelper.formatDateValue(
                                    "EEE",
                                    weeklyDataList[i - 1].recordDate
                                )
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
                        while (i < weeklyDataList.size) {
                            if (Utilities.isNullOrEmptyOrZero(weeklyDataList[i].stepsCount.toString())) {
                                entries.add(BarEntry((i + 1).toFloat(), 0.0f))
                            } else {
                                entries.add(
                                    BarEntry(
                                        (i + 1).toFloat(),
                                        weeklyDataList[i].stepsCount.toFloat()
                                    )
                                )
                            }
                            i++
                        }
                    }

                    Constants.DISTANCE -> {
                        var i = 0
                        while (i < weeklyDataList.size) {
                            if (Utilities.isNullOrEmptyOrZero(weeklyDataList[i].distance.toString())) {
                                entries.add(BarEntry((i + 1).toFloat(), 0.0f))
                            } else {
                                entries.add(
                                    BarEntry(
                                        (i + 1).toFloat(),
                                        fitnessHelper.convertMtrToKmsValueNew(weeklyDataList[i].distance.toString())
                                            .toFloat()
                                    )
                                )
                            }
                            i++
                        }
                    }

                    Constants.CALORIES -> {
                        var i = 0
                        while (i < weeklyDataList.size) {
                            if (Utilities.isNullOrEmptyOrZero(weeklyDataList[i].calories.toString())) {
                                entries.add(BarEntry((i + 1).toFloat(), 0.0f))
                            } else {
                                entries.add(
                                    BarEntry(
                                        (i + 1).toFloat(),
                                        weeklyDataList[i].calories.toFloat()
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
                barDataSet.valueTextSize = 10f
                barDataSet.color = ContextCompat.getColor(
                    requireContext(),
                    fitnessHelper.getColorBasedOnType(type)
                )
                val dataSets = ArrayList<IBarDataSet>()
                dataSets.add(barDataSet)
                val data = BarData(dataSets)
                data.barWidth = 0.5f
                binding.graphWeekly.data = data
                binding.graphWeekly.description.isEnabled = false
                binding.graphWeekly.legend.isEnabled = false
                binding.graphWeekly.axisRight.isEnabled = false
                binding.graphWeekly.isDoubleTapToZoomEnabled = false
                binding.graphWeekly.setTouchEnabled(false)
                binding.graphWeekly.isHighlightPerTapEnabled = false
                binding.graphWeekly.setTouchEnabled(false)
                binding.graphWeekly.isDragEnabled = false
                binding.graphWeekly.setPinchZoom(false)
                binding.graphWeekly.isHighlightPerTapEnabled = false
                binding.graphWeekly.setDrawValueAboveBar(false)
                //binding.graphWeekly.setVisibleXRangeMaximum(7f);
                binding.graphWeekly.invalidate()
                //binding.graphWeekly.animateY(1200);
                binding.graphWeekly.animateXY(1000, 1000)
            } else {
                binding.graphUnit.visibility = View.GONE
                binding.graphWeekly.invalidate()
                //binding.graphWeekly.clearValues();
                binding.graphWeekly.notifyDataSetChanged()
                binding.graphWeekly.setNoDataText(resources.getString(R.string.NO_HISTORY_AVAILABLE))
                binding.graphWeekly.setNoDataTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.textViewColorBlack
                    )
                )
                val paint: Paint = binding.graphWeekly.getPaint(BarChart.PAINT_INFO)
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

    /*    private fun setGraphData1() {
            try {
                val weeklyDataList = weeklyDataList
                if (!weeklyDataList.isNullOrEmpty()) {
                    binding.graphWeekly.visibility = View.VISIBLE
                    binding.graphUnit.visibility = View.VISIBLE

                    binding.graphWeekly.description.text = ""
                    binding.graphWeekly.legend.isEnabled = false
                    binding.graphWeekly.axisRight.isEnabled = false
                    binding.graphWeekly.isDoubleTapToZoomEnabled = false
                    binding.graphWeekly.setTouchEnabled(false)
                    binding.graphWeekly.isHighlightPerTapEnabled = false
                    val yAxis: YAxis = binding.graphWeekly.axisLeft
                    yAxis.setDrawTopYLabelEntry(true)
                    yAxis.setDrawZeroLine(true)
                    yAxis.granularity = 1f
                    yAxis.axisMinimum = 0f
                    val xAxis: XAxis = binding.graphWeekly.xAxis
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.setDrawAxisLine(true)
                    xAxis.granularity = 1f
                    xAxis.setDrawGridLines(false)
                    xAxis.axisMinimum = 0f
                    //xAxis.setAxisMaximum(5);
                    xAxis.valueFormatter = object : IAxisValueFormatter {
                        override fun getFormattedValue(value: Float, axis: AxisBase?): String? {
                            for (i in 1..weeklyDataList.size) {
                                if (value == i.toFloat()) {
                                    return DateHelper.formatDateValue("EEE", weeklyDataList[i - 1].recordDate)
                                }
                            }
                            return ""
                        }
                    }
                    val entries: MutableList<Entry> = ArrayList()
                    entries.add(Entry(0f, 0f))

                    when (type) {
                        Constants.STEPS_COUNT -> {
                            for (i in weeklyDataList.indices) {
                                if (Utilities.isNullOrEmptyOrZero(weeklyDataList[i].stepsCount.toString())) {
                                    entries.add(Entry((i + 1).toFloat(), 0f))
                                } else {
                                    entries.add(Entry((i + 1).toFloat(), weeklyDataList[i].stepsCount.toFloat()))
                                }
                            }
                        }
                        Constants.DISTANCE -> {
                            for (i in weeklyDataList.indices) {
                                if (Utilities.isNullOrEmptyOrZero(weeklyDataList[i].distance.toString())) {
                                    entries.add(Entry((i + 1).toFloat(), 0f))
                                } else {
                                    entries.add(Entry((i + 1).toFloat(), weeklyDataList[i].distance.toFloat()))
                                }
                            }
                        }
                        Constants.CALORIES -> {
                            for (i in weeklyDataList.indices) {
                                if (Utilities.isNullOrEmptyOrZero(weeklyDataList[i].calories.toString())) {
                                    entries.add(Entry((i + 1).toFloat(), 0f))
                                } else {
                                    entries.add(Entry((i + 1).toFloat(), weeklyDataList[i].calories.toFloat()))
                                }
                            }
                        }
                    }
                    val lineDataSet = LineDataSet(entries, "")
                    //lineDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                    lineDataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
                    lineDataSet.setDrawFilled(true)
                    lineDataSet.setDrawValues(true)
                    lineDataSet.setColors(appColorHelper.primaryColor())
                    lineDataSet.valueTextColor = ContextCompat.getColor(requireContext(), R.color.textViewColor)
                    lineDataSet.valueTextSize = 12f
                    //lineDataSet.setFillColor(ContextCompat.getColor(getContext(), R.color.vivant_heather));
                    lineDataSet.color = appColorHelper.primaryColor()
                    lineDataSet.fillAlpha = 255
                    lineDataSet.lineWidth = 1.5f
                    lineDataSet.circleRadius = 5f
                    lineDataSet.circleHoleRadius = 2f
                    //lineDataSet.setCircleColorHole(ContextCompat.getColor(requireContext(),R.color.colorPrimary))
                    lineDataSet.setCircleColor(appColorHelper.primaryColor())
                    lineDataSet.setDrawCircles(true)
                    lineDataSet.setDrawCircleHole(true)
                    lineDataSet.fillDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.graph_fill)
                    //lineDataSet.fillColor = appColorHelper.primaryColor()
                    val lineData = LineData(lineDataSet)
                    binding.graphWeekly.data = lineData
                    binding.graphWeekly.setTouchEnabled(true)
                    binding.graphWeekly.isDragEnabled = true
                    binding.graphWeekly.setPinchZoom(false)
                    binding.graphWeekly.isHighlightPerTapEnabled = true
                    binding.graphWeekly.invalidate()
                    //binding.graphWeekly.animateY(1200)
                    binding.graphWeekly.animateXY(1500, 1500)
                } else {
                    binding.graphUnit.visibility = View.GONE
                    binding.graphWeekly.invalidate()
                    //binding.graphWeekly.clearValues();
                    binding.graphWeekly.notifyDataSetChanged()
                    binding.graphWeekly.setNoDataText(resources.getString(R.string.NO_HISTORY_AVAILABLE))
                    binding.graphWeekly.setNoDataTextColor(ContextCompat.getColor(requireContext(), R.color.textViewColorBlack))
                    val paint: Paint = binding.graphWeekly.getPaint(BarChart.PAINT_INFO)
                    paint.textSize = resources.getDimension(R.dimen._15sdp)
                    paint.typeface = Typeface.DEFAULT_BOLD
                }
    *//*            Handler(Looper.getMainLooper()).postDelayed({
                viewModel.hideProgressBar()
            }, 1000)*//*
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/

    /*    private fun getFitnessData(startDate: Date, endDate: Date): MutableList<StepGoalHistory> {
            val fitnessDataList: MutableList<StepGoalHistory> = mutableListOf()
            try {
                val newEndDate = endDate
                var fitnessListSize = DateHelper.getDateDifferenceInDays(startDate, newEndDate) + 1
                if (fitnessListSize < 7) {
                    fitnessListSize = 7
                    newEndDate.date = startDate.date + 6
                }
                //Utilities.printLogError("StartDate---> $startDate")
                //Utilities.printLogError("EndDate-----> $newEndDate")
                fitnessDataManager!!.readHistoryData(startDate, newEndDate).addOnCompleteListener {
                    val fitnessDataJsonArray: JSONArray = fitnessDataManager!!.fitnessDataArray
                    if (fitnessDataJsonArray != null) {
                        //Utilities.printLogError("Google Fit data for $fitnessListSize days---> $fitnessDataJsonArray")
                        if (fitnessDataJsonArray.length() > 0) {
                            for (i in fitnessListSize - 1 downTo 0) {
                                try {
                                    val recordDate = Calendar.getInstance()
                                    recordDate.time = newEndDate
                                    val sd = SimpleDateFormat(DateHelper.SERVER_DATE_YYYYMMDD, Locale.getDefault())
                                    val sd2 = SimpleDateFormat(DateHelper.DATEFORMAT_DDMMMYYYY_NEW, Locale.getDefault())
                                    val sd3 = SimpleDateFormat(DateHelper.DISPLAY_DATE_DDMMMYYYY, Locale.getDefault())

                                    recordDate.add(Calendar.DATE, -i)
                                    val recordDateStr = sd.format(recordDate.time)
                                    val recordDateStr2ndFormat = sd2.format(recordDate.time)
                                    val recordDateStr3rdFormat = sd3.format(recordDate.time)

                                    val dataMap = StepGoalHistory(lastRefreshed = Date())
                                    dataMap.stepID = 1
                                    dataMap.goalID = 1
                                    dataMap.recordDate = recordDateStr
                                    dataMap.stepsCount = 0
                                    dataMap.totalGoal = Constants.DEFAULT_STEP_GOAL
                                    dataMap.distance = 0.0
                                    dataMap.calories = 0
                                    dataMap.goalPercentile = 0.0
                                    dataMap.activeTime = "0"

                                    for (j in 0 until fitnessDataJsonArray.length()) {
                                        if (!Utilities.isNullOrEmpty(fitnessDataJsonArray.getJSONObject(j).getString(Constants.RECORD_DATE))) {
                                            if (recordDateStr.equals(fitnessDataJsonArray.getJSONObject(j).getString(Constants.RECORD_DATE).split("T".toRegex()).toTypedArray()[0], ignoreCase = true)
                                                || recordDateStr2ndFormat.equals(fitnessDataJsonArray.getJSONObject(j).getString(Constants.RECORD_DATE), ignoreCase = true)
                                                || recordDateStr3rdFormat.equals(fitnessDataJsonArray.getJSONObject(j).getString(Constants.RECORD_DATE), ignoreCase = true)) {

                                                dataMap.stepsCount = fitnessDataJsonArray.getJSONObject(j).getString(Constants.STEPS_COUNT).toInt()
                                                dataMap.calories = fitnessDataJsonArray.getJSONObject(j).getString(Constants.CALORIES).toInt()
                                                dataMap.distance = CalculateParameters.convertMtrToKmsValue(fitnessDataJsonArray.getJSONObject(j).getString(Constants.DISTANCE)).toDouble()
    *//*                                            dataMap.distance = (fitnessDataJsonArray.getJSONObject(j).getString(
                                                Constants.DISTANCE).toDouble())*//*
                                            dataMap.activeTime = fitnessDataJsonArray.getJSONObject(j).getString(Constants.ACTIVE_TIME)
                                        }
                                    }
                                }
                                fitnessDataList.add(dataMap)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    weeklyDataList.clear()
                    weeklyDataList.addAll(fitnessDataList)
                    //weeklyDataList.sortBy { it.recordDate }
                    Utilities.printData("weeklyDataList", weeklyDataList)
                    calculateTotalData()
                    setGraphData()

                    val filteredList = fitnessDataList.filter { DateHelper.stringDateToDate(DateHelper.SERVER_DATE_YYYYMMDD, it.recordDate) < Date() }.toMutableList()
                    binding.rvDailyData.layoutAnimation = animation
                    dailyDataAdapter!!.updateList(filteredList)
                    binding.rvDailyData.scheduleLayoutAnimation()
                }
            }
            fitnessDataList.reverse()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return fitnessDataList
    }*/

}
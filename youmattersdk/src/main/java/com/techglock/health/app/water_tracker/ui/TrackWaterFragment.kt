package com.techglock.health.app.water_tracker.ui


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.DefaultNotificationDialog
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.common.utils.showDialog
import com.techglock.health.app.databinding.FragmentTrackWaterBinding
import com.techglock.health.app.model.fitness.MonthModel
import com.techglock.health.app.model.waterTracker.GetWaterIntakeHistoryByDateModel
import com.techglock.health.app.model.waterTracker.GetWaterIntakeSummaryModel
import com.techglock.health.app.water_tracker.adapter.CalenderTrackIntakeAdapter
import com.techglock.health.app.water_tracker.adapter.WeekDayAdapter
import com.techglock.health.app.water_tracker.model.CalenderTrackModel
import com.techglock.health.app.water_tracker.viewmodel.WaterTrackerViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.math.roundToInt

@AndroidEntryPoint
class TrackWaterFragment : BaseFragment(), CalenderTrackIntakeAdapter.OnTrackDateListener,
    MonthYearSelectionBottomSheet.OnMonthYearClickListener,
    DefaultNotificationDialog.OnDialogValueListener {

    private val viewModel: WaterTrackerViewModel by lazy {
        ViewModelProvider(this)[WaterTrackerViewModel::class.java]
    }
    private lateinit var binding: FragmentTrackWaterBinding

    private var calenderTrackIntakeAdapter: CalenderTrackIntakeAdapter? = null
    private var weekDayAdapter: WeekDayAdapter? = null
    private var monthList: MutableList<MonthModel> = mutableListOf()
    private val weekDayList = mutableListOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    private var trackDaysList: MutableList<CalenderTrackModel> = mutableListOf()

    private var selectedDate = DateHelper.currentDateAsStringyyyyMMdd
    private var fromDate = ""
    private var toDate = ""

    private var isCurrentMonth = true
    private var selectedMonthIndex = 0

    private var achievedPercent = 0
    private var goal = 0
    private var achieved = 0

    private var animation: LayoutAnimationController? = null
    private val currentMonthYear =
        DateHelper.currentMonthAsStringMMM + " " + DateHelper.currentYearAsStringyyyy
    private var currentDate = DateHelper.currentDateAsStringyyyyMMdd

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            // callback to Handle back button event
            val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_trackWaterFragment_to_waterTrackerDashboardFragment)
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
        binding = FragmentTrackWaterBinding.inflate(inflater, container, false)
        try {
            initialise()
            setClickable()
            registerObserver()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun initialise() {
        CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.TRACK_WATER_INTAKE_SCREEN)
        animation = AnimationUtils.loadLayoutAnimation(
            requireContext(),
            R.anim.layout_animation_slide_from_bottom_grid
        )
        monthList = viewModel.waterTrackerHelper.getAllMonthsListFromJoiningToCurrentDate(
            viewModel.joiningDate,
            DateHelper.currentDateAsStringyyyyMMdd,
            DateHelper.SERVER_DATE_YYYYMMDD
        )

        if (monthList.isNotEmpty()) {
            selectedMonthIndex = monthList.size - 1
        }
        //binding.txtMonth.text = monthList[selectedMonthIndex].displayValue
        binding.txtMonth.text = "${
            Utilities.getMonthConverted(
                monthList[selectedMonthIndex].month,
                requireContext()
            )
        } ${monthList[selectedMonthIndex].year}"
        binding.txtMonth.tag = monthList[selectedMonthIndex].displayValue

        binding.imgNext.visibility = View.INVISIBLE
        if (monthList.size == 1) {
            binding.imgBack.visibility = View.INVISIBLE
        }

        weekDayAdapter = WeekDayAdapter(requireContext(), weekDayList)
        binding.rvWeekDays.layoutManager = GridLayoutManager(requireContext(), 7)
        binding.rvWeekDays.adapter = weekDayAdapter

        calenderTrackIntakeAdapter = CalenderTrackIntakeAdapter(viewModel, requireContext(), this)
        binding.rvCalender.layoutManager = GridLayoutManager(requireContext(), 7)
        binding.rvCalender.setExpanded(true)
        binding.rvCalender.layoutAnimation = animation
        binding.rvCalender.adapter = calenderTrackIntakeAdapter

        trackDaysList.clear()
        trackDaysList.addAll(
            viewModel.waterTrackerHelper.getDatesInMonth(
                monthList[selectedMonthIndex].year.toInt(),
                monthList[selectedMonthIndex].monthOfYear.toInt()
            )
        )
        calenderTrackIntakeAdapter!!.updateList(trackDaysList, isCurrentMonth)

        callWaterIntakeHistoryByDate()
    }

    @SuppressLint("SetTextI18n")
    private fun setClickable() {

        binding.imgBack.setOnClickListener {
            selectedMonthIndex -= 1
            if (selectedMonthIndex >= 0 && selectedMonthIndex < monthList.size) {
                val displayValue = monthList[selectedMonthIndex].displayValue
                binding.txtMonth.text = "${
                    Utilities.getMonthConverted(
                        monthList[selectedMonthIndex].month,
                        requireContext()
                    )
                } ${monthList[selectedMonthIndex].year}"
                binding.txtMonth.tag = displayValue

                isCurrentMonth = displayValue == currentMonthYear

                trackDaysList.clear()
                trackDaysList.addAll(
                    viewModel.waterTrackerHelper.getDatesInMonth(
                        monthList[selectedMonthIndex].year.toInt(),
                        monthList[selectedMonthIndex].monthOfYear.toInt()
                    )
                )
                binding.rvCalender.layoutAnimation = animation
                calenderTrackIntakeAdapter!!.updateList(trackDaysList, isCurrentMonth)
                binding.rvCalender.scheduleLayoutAnimation()

                callWaterIntakeHistoryByDate()

                if (displayValue == monthList[0].displayValue) {
                    binding.imgBack.visibility = View.INVISIBLE
                    binding.imgNext.visibility = View.VISIBLE
                } else {
                    binding.imgBack.visibility = View.VISIBLE
                    binding.imgNext.visibility = View.VISIBLE
                }
            } else {
                selectedMonthIndex += 1
            }
            //Utilities.printLogError("selectedMonthIndex--->$selectedMonthIndex")
        }

        binding.imgNext.setOnClickListener {
            selectedMonthIndex += 1
            if (selectedMonthIndex < monthList.size) {
                val displayValue = monthList[selectedMonthIndex].displayValue
                binding.txtMonth.text = "${
                    Utilities.getMonthConverted(
                        monthList[selectedMonthIndex].month,
                        requireContext()
                    )
                } ${monthList[selectedMonthIndex].year}"
                binding.txtMonth.tag = displayValue

                isCurrentMonth = displayValue == currentMonthYear

                trackDaysList.clear()
                trackDaysList.addAll(
                    viewModel.waterTrackerHelper.getDatesInMonth(
                        monthList[selectedMonthIndex].year.toInt(),
                        monthList[selectedMonthIndex].monthOfYear.toInt()
                    )
                )

                binding.rvCalender.layoutAnimation = animation
                calenderTrackIntakeAdapter!!.updateList(trackDaysList, isCurrentMonth)
                binding.rvCalender.scheduleLayoutAnimation()

                callWaterIntakeHistoryByDate()

                if (displayValue == monthList.last().displayValue) {
                    binding.imgNext.visibility = View.INVISIBLE
                    binding.imgBack.visibility = View.VISIBLE
                } else {
                    binding.imgNext.visibility = View.VISIBLE
                    binding.imgBack.visibility = View.VISIBLE
                }
            } else {
                selectedMonthIndex -= 1
            }
            //Utilities.printLogError("selectedMonthIndex--->$selectedMonthIndex")
        }

        binding.imgEdit.setOnClickListener {
            showMonthYearBottomSheet()
        }

        binding.txtMonth.setOnClickListener {
            showMonthYearBottomSheet()
        }

        binding.imgInfo.setOnClickListener {
            showDialog(
                listener = this,
                title = "",
                message = "<a><B><font color='#3A393B'>${resources.getString(R.string.WTD)} :</font></B><br/> ${
                    resources.getString(
                        R.string.WEEK_START_DAY
                    )
                }<br/>  ${resources.getString(R.string.WTD_DESC)}<br/><br/>  <B><font color='#3A393B'>${
                    resources.getString(
                        R.string.ACHIEVEMENT_THIS_WEEK
                    )
                } :</font></B><br/> ${resources.getString(R.string.ACHIEVEMENT_THIS_WEEK_DESC)}<br/><br/>  <B><font color='#3A393B'>${
                    resources.getString(
                        R.string.WITD
                    )
                } :</font></B><br/> ${resources.getString(R.string.WITD_DESC)}<br/><br/>  <B><font color='#3A393B'>${
                    resources.getString(
                        R.string.MAX_STREAK
                    )
                } :</font></B><br/> ${resources.getString(R.string.MAX_STREAK_DESC)}<br/><br/>  <B><font color='#3A393B'>${
                    resources.getString(
                        R.string.CURRENT_STREAK
                    )
                } :</font></B><br/> ${resources.getString(R.string.CURRENT_STREAK_DESC)}</a>",
                showLeftBtn = false
            )
        }

    }

    private fun registerObserver() {

        viewModel.getWaterIntakeHistoryByDate.observe(viewLifecycleOwner) {
            if (it != null) {
                if (!it.result.result.isNullOrEmpty()) {
                    val result = it.result.result
                    //Utilities.printData("History",result,true)
                    loadData(result)
                }
            }
        }

        viewModel.getWaterIntakeSummary.observe(viewLifecycleOwner) {
            if (it != null) {
                //Utilities.printData("Summary",it.result,true)
                loadSummary(it.result)
            }
        }

        viewModel.getDailyWaterIntake.observe(viewLifecycleOwner) {
            if (it != null) {
                val dataMap = CalenderTrackModel()
                dataMap.trackServerDate = selectedDate
                dataMap.trackDisplayDate = DateHelper.convertDateSourceToDestination(
                    selectedDate,
                    DateHelper.SERVER_DATE_YYYYMMDD,
                    DateHelper.DATEFORMAT_DDMMMYYYY_NEW
                )
                if (!it.result.result.isNullOrEmpty()) {
                    val result = it.result.result[0]
                    Utilities.printData("result", result, true)
                    //var target = Constants.DEFAULT_WATER_GOAL.toDouble()
                    var target = 0.0
                    var intake = 0.0
                    //dataMap.trackDate = selectedDate
                    //dataMap.trackWeekDay = trackDaysList[i].trackWeekDay
                    //dataMap.trackMonthYear = trackDaysList[i].trackMonthYear
                    if (!Utilities.isNullOrEmpty(result.waterGoal)) {
                        target = result.waterGoal!!.toDouble()
                    }
                    if (!Utilities.isNullOrEmpty(result.totalWaterIntake)) {
                        intake = result.totalWaterIntake!!.toDouble()
                    }
                    dataMap.trackGoal = target
                    dataMap.trackAchieved = intake
                    dataMap.trackGoalPercentage = ((intake * 100) / target)
                    dataMap.isGoalAchieved = dataMap.trackAchieved >= dataMap.trackGoal
                }
                loadDataForDate(dataMap)
            }
        }

    }

    private fun loadData(waterDataHistoryFromApi: List<GetWaterIntakeHistoryByDateModel.ResultData>) {
        try {
            val waterDataHistory = ArrayList<CalenderTrackModel>()
            //var target = Constants.DEFAULT_WATER_GOAL.toDouble()
            var target = 0.0
            var intake = 0.0
            var dataMap: CalenderTrackModel
            //Utilities.printData("SelectedList",trackDaysList,false)
            for (i in 0 until trackDaysList.size) {
                dataMap = CalenderTrackModel()
                dataMap.trackDate = trackDaysList[i].trackDate
                dataMap.trackWeekDay = trackDaysList[i].trackWeekDay
                dataMap.trackMonthYear = trackDaysList[i].trackMonthYear
                dataMap.trackDisplayDate = trackDaysList[i].trackDisplayDate
                dataMap.trackServerDate = trackDaysList[i].trackServerDate

                for (j in waterDataHistoryFromApi.indices) {
                    if (trackDaysList[i].trackServerDate.equals(
                            waterDataHistoryFromApi[j].recordDate!!.split(
                                "T"
                            )[0], ignoreCase = true
                        )
                    ) {
                        if (!Utilities.isNullOrEmpty(waterDataHistoryFromApi[j].waterGoal)) {
                            target = waterDataHistoryFromApi[j].waterGoal!!.toDouble()
                        }
                        if (!Utilities.isNullOrEmpty(waterDataHistoryFromApi[j].totalWaterIntake)) {
                            intake = waterDataHistoryFromApi[j].totalWaterIntake!!.toDouble()
                        }
                        dataMap.trackGoal = target
                        dataMap.trackAchieved = intake
                        dataMap.trackGoalPercentage = ((intake * 100) / target)
                        if (dataMap.trackGoal > 0) {
                            dataMap.isGoalAchieved = dataMap.trackAchieved >= dataMap.trackGoal
                        }
                    }
                }
                waterDataHistory.add(dataMap)
            }

            /*        val filteredList: MutableList<CalenderTrackModel> = mutableListOf()
                    for (history in waterDataHistory) {
                        if ( DateHelper.stringDateToDate(DateHelper.SERVER_DATE_YYYYMMDD,history.trackServerDate).before(Date())
                            || DateHelper.stringDateToDate(DateHelper.SERVER_DATE_YYYYMMDD,history.trackServerDate) == Date()) {
                            filteredList.add(history)
                        }
                    }*/

            trackDaysList.clear()
            trackDaysList.addAll(waterDataHistory)
            //Utilities.printLogError("waterDataHistory--->${trackDaysList}")
            //Utilities.printData("waterDataHistory",trackDaysList,false)
            //binding.rvCalender.layoutAnimation = animation
            calenderTrackIntakeAdapter!!.updateList(trackDaysList, isCurrentMonth)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadSummary(waterIntakeSummary: GetWaterIntakeSummaryModel.Result) {
        try {
            if (!waterIntakeSummary.weeklyWaterIntake.result.isNullOrEmpty()) {
                if (!Utilities.isNullOrEmptyOrZero(waterIntakeSummary.weeklyWaterIntake.result[0].weekTillDate.toString())) {
                    binding.txtWeekTillDate.text =
                        "${waterIntakeSummary.weeklyWaterIntake.result[0].weekTillDate}"
                } else {
                    binding.txtWeekTillDate.text = "0 %"
                }
                if (!Utilities.isNullOrEmptyOrZero(waterIntakeSummary.weeklyWaterIntake.result[0].achievementThisWeek.toString())) {
                    binding.txtAchievementThisWeek.text =
                        "${waterIntakeSummary.weeklyWaterIntake.result[0].achievementThisWeek}"
                } else {
                    binding.txtAchievementThisWeek.text = "0 %"
                }
            } else {
                binding.txtWeekTillDate.text = "0 %"
                binding.txtAchievementThisWeek.text = "0 %"
            }

            if (!waterIntakeSummary.lifeTimeWaterIntake.result.isNullOrEmpty()) {
                if (!Utilities.isNullOrEmptyOrZero(waterIntakeSummary.lifeTimeWaterIntake.result[0].lifeTimeWaterIntake.toString())) {
                    val lifetimeIntake =
                        waterIntakeSummary.lifeTimeWaterIntake.result[0].lifeTimeWaterIntake
                    if (lifetimeIntake > 999.5) {
                        val intake = (lifetimeIntake * 0.001).roundToInt()
                        if (intake == 1) {
                            binding.txtLifetime.text =
                                "$intake ${resources.getString(R.string.LITRE)}"
                        } else {
                            binding.txtLifetime.text =
                                "$intake ${resources.getString(R.string.LITRES)}"
                        }
                    } else {
                        binding.txtLifetime.text =
                            "${lifetimeIntake.roundToInt()} ${resources.getString(R.string.ML)}"
                    }
                } else {
                    binding.txtLifetime.text = "0"
                }
            } else {
                binding.txtLifetime.text = "0"
            }

            if (!waterIntakeSummary.waterStreak.result.isNullOrEmpty()) {
                if (!Utilities.isNullOrEmptyOrZero(waterIntakeSummary.waterStreak.result[0].maximumStreak.toString())) {
                    binding.txtStreak.text =
                        "${waterIntakeSummary.waterStreak.result[0].maximumStreak}"
                } else {
                    binding.txtStreak.text = "0"
                }
                if (!Utilities.isNullOrEmptyOrZero(waterIntakeSummary.waterStreak.result[0].currentStreak.toString())) {
                    binding.txtCurrentStreak.text =
                        "${waterIntakeSummary.waterStreak.result[0].currentStreak}"
                } else {
                    binding.txtCurrentStreak.text = "0"
                }
            } else {
                binding.txtStreak.text = "0"
                binding.txtCurrentStreak.text = "0"
            }

            /*            if ( isCurrentMonth ) {
                            binding.lblCurrentStreak.visibility = View.VISIBLE
                            binding.txtCurrentStreak.visibility = View.VISIBLE
                            binding.imgStreakSmall.visibility = View.VISIBLE
                        } else {
                            binding.lblCurrentStreak.visibility = View.INVISIBLE
                            binding.txtCurrentStreak.visibility = View.INVISIBLE
                            binding.imgStreakSmall.visibility = View.INVISIBLE
                        }*/

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onTrackDateSelection(position: Int, trackDate: CalenderTrackModel) {
        selectedDate = trackDate.trackServerDate
        viewModel.callGetDailyWaterIntakeApi(trackDate.trackServerDate)
    }

    /*    override fun onTrackDateSelection(position: Int, trackDate: CalenderTrackModel) {
            binding.txtDate.text = trackDate.trackDisplayDate
            binding.txtGoal.text = "${resources.getString(R.string.TARGET)} - ${trackDate.trackGoal.toInt()} ${resources.getString(R.string.ML)}"
            binding.txtAchieved.text = "${resources.getString(R.string.ACHIEVED)} - ${trackDate.trackAchieved} ${resources.getString(R.string.ML)}"
            //achievedPercent = ( (trackDate.trackAchieved * 100) / trackDate.trackGoal )
            achievedPercent = trackDate.trackGoalPercentage
            if( trackDate.trackGoalPercentage >= 100 ) {
                achievedPercent = 100.0
            }
            binding.waterViewSelectedDate.setImageResource(Utilities.getWaterDropImageByValue((achievedPercent)))
            binding.txtPercentage.text = "$achievedPercent %"
            if( trackDate.trackGoalPercentage <= 0 ) {
                binding.txtPercentage.text = "${achievedPercent.toInt()} %"
            }
            if( trackDate.trackGoalPercentage >= 100 ) {
                binding.txtPercentage.text = "${achievedPercent.toInt()} %"
            }
            //binding.waterViewSelectedDate.setWaveProgress( achievedPercent.toLong() )
            //binding.waterViewSelectedDate.setProgressWithAnimation( achievedPercent.toDouble(),1000 )
        }*/

    @SuppressLint("SetTextI18n")
    override fun onMonthYearClick(month: MonthModel) {
        try {
            Utilities.printData("month", month, true)
            if (binding.txtMonth.tag.toString() != month.displayValue) {
                binding.txtMonth.text =
                    "${Utilities.getMonthConverted(month.month, requireContext())} ${month.year}"
                isCurrentMonth = month.displayValue == currentMonthYear
                trackDaysList.clear()
                trackDaysList.addAll(
                    viewModel.waterTrackerHelper.getDatesInMonth(
                        month.year.toInt(),
                        month.monthOfYear.toInt()
                    )
                )

                binding.rvCalender.layoutAnimation = animation
                calenderTrackIntakeAdapter!!.updateList(trackDaysList, isCurrentMonth)
                binding.rvCalender.scheduleLayoutAnimation()

                callWaterIntakeHistoryByDate()

                if (month.displayValue == monthList[0].displayValue) {
                    binding.imgBack.visibility = View.INVISIBLE
                    binding.imgNext.visibility = View.VISIBLE
                } else if (month.displayValue == monthList.last().displayValue) {
                    binding.imgNext.visibility = View.INVISIBLE
                    binding.imgBack.visibility = View.VISIBLE
                } else {
                    binding.imgBack.visibility = View.VISIBLE
                    binding.imgNext.visibility = View.VISIBLE
                }

                for (i in 0 until monthList.size) {
                    if (monthList[i].displayValue.equals(month.displayValue, ignoreCase = true)) {
                        selectedMonthIndex = i
                        break
                    }
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun showMonthYearBottomSheet() {
        val m = binding.txtMonth.tag.toString().split(" ")[0]
        val y = binding.txtMonth.tag.toString().split(" ")[1].toInt()
        val bottomSheet = MonthYearSelectionBottomSheet(m, y, viewModel, this)
        bottomSheet.show(requireFragmentManager(), MonthYearSelectionBottomSheet.TAG)
    }

    private fun callWaterIntakeHistoryByDate() {
        fromDate = trackDaysList[0].trackServerDate
        toDate = trackDaysList.last().trackServerDate
        if (isCurrentMonth) {
            toDate = currentDate
        }
        Utilities.printLogError("fromDate--->$fromDate")
        Utilities.printLogError("toDate--->$toDate")
        viewModel.callGetWaterIntakeHistoryByDateApi(fromDate, toDate)
        viewModel.callGetWaterIntakeSummaryApi(fromDate, currentDate)
    }

    @SuppressLint("SetTextI18n")
    private fun loadDataForDate(trackDate: CalenderTrackModel) {
        goal = if (!Utilities.isNullOrEmpty(trackDate.trackGoal.toString())) {
            trackDate.trackGoal.toInt()
        } else {
            0
        }

        achieved = if (!Utilities.isNullOrEmpty(trackDate.trackAchieved.toString())) {
            trackDate.trackAchieved.roundToInt()
        } else {
            0
        }

        binding.txtDate.text = trackDate.trackDisplayDate
        binding.txtGoal.text =
            "${resources.getString(R.string.TARGET)} - $goal ${resources.getString(R.string.ML)}"
        binding.txtAchieved.text =
            "${resources.getString(R.string.ACHIEVED)} - $achieved ${resources.getString(R.string.ML)}"

        //achievedPercent = trackDate.trackGoalPercentage
        achievedPercent = if (goal != 0) {
            //( (achieved * 100) / goal )
            ((trackDate.trackAchieved * 100) / trackDate.trackGoal).roundToInt()
        } else {
            0
        }
        if (achievedPercent > 99.99) {
            achievedPercent = 100
        }
        binding.waterViewSelectedDate.setImageResource(Utilities.getWaterDropImageByValue((achievedPercent.toDouble())))
        binding.txtPercentage.text = "$achievedPercent %"
    }

    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {}

}
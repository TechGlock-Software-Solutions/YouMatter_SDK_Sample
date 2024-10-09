package com.techglock.health.app.fitness_tracker.ui

import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseActivity
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.NavigationConstants
import com.techglock.health.app.common.extension.openAnotherActivity
import com.techglock.health.app.common.fitness.FitRequestCode
import com.techglock.health.app.common.fitness.FitnessDataManager
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.DefaultNotificationDialog
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.common.utils.showDialog
import com.techglock.health.app.databinding.ActivityFitnessDataBinding
import com.techglock.health.app.fitness_tracker.adapter.DayWeekMonthViewPagerAdapter
import com.techglock.health.app.fitness_tracker.viewmodel.ActivityTrackerViewModel
import com.techglock.health.app.model.entity.FitnessEntity
import com.techglock.health.app.model.fitness.GetStepsGoalModel
import com.techglock.health.app.model.fitness.MonthModel
import com.techglock.health.app.model.fitness.WeekModel
import com.techglock.health.app.model.fitness.YearModel
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.Date

@AndroidEntryPoint
class FitnessDataActivity : BaseActivity(), DefaultNotificationDialog.OnDialogValueListener {

    val viewModel: ActivityTrackerViewModel by lazy {
        ViewModelProvider(this)[ActivityTrackerViewModel::class.java]
    }

    private lateinit var binding: ActivityFitnessDataBinding
    private val appColorHelper = AppColorHelper.instance!!

    /*@Inject
    lateinit var fitnessHelper: FitnessHelper

    @Inject
    lateinit var stepCountHelper: StepCountHelper*/
    private var fitnessDataManager: FitnessDataManager? = null

    private var latestGoalListener: OnLatestGoalListener? = null
    private var onGoogleAccountNameListener: OnGoogleAccountNameListener? = null

    private var from = ""
    var screen = ""
    private var notificationTitle = ""
    private var notificationMsg = ""

    private var tabSelected = 0
    var showAnim = false
    var isForceSync = false
    var syncFrom = ""
    val currentDate = DateHelper.currentDateAsStringyyyyMMdd
    var weeklyList: MutableList<WeekModel> = mutableListOf()
    var monthlyList: MutableList<MonthModel> = mutableListOf()
    var yearlyList: MutableList<YearModel> = mutableListOf()
    var todayFitnessData = FitnessEntity.StepGoalHistory(lastRefreshed = Date())
    private var dayWeekMonthAdapter: DayWeekMonthViewPagerAdapter? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivityFitnessDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        try {
            if (intent.hasExtra(Constants.FROM)) {
                from = intent.getStringExtra(Constants.FROM)!!
            }
            if (intent.hasExtra(Constants.SCREEN)) {
                screen = intent.getStringExtra(Constants.SCREEN)!!
                notificationTitle = intent.getStringExtra(Constants.NOTIFICATION_TITLE)!!
                notificationMsg = intent.getStringExtra(Constants.NOTIFICATION_MESSAGE)!!
            }
            Utilities.printData("Intent", intent, true)
            //Utilities.printLogError("from--->$from")
            //Utilities.printLogError("screen--->$screen")
            setupToolbar()
            initialise()
            registerObservers()
            syncAccountDetails()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initialise() {
        fitnessDataManager = FitnessDataManager(this)
        if (fitnessDataManager!!.oAuthPermissionsApproved()) {
            //Utilities.printLogError("oAuthPermissionsApproved---> true")
            //syncAccountDetails()
            getTodayFitnessData()
        } else {
            //Utilities.printLogError("oAuthPermissionsApproved---> false")
            fitnessDataManager!!.fitSignIn(FitRequestCode.READ_DATA)
        }

        dayWeekMonthAdapter = DayWeekMonthViewPagerAdapter(supportFragmentManager, this)
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.viewPager.adapter = dayWeekMonthAdapter
        binding.viewPager.offscreenPageLimit = 2
        binding.viewPager.setScrollDuration(500)
        binding.viewPager.setPageScrollEnabled(false)

        setWeeklyMonthlyYearlyList()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab) {
                tabSelected = tab.position
                Utilities.printLogError("tabSelected--->$tabSelected")
                Handler(Looper.getMainLooper()).postDelayed({
                    screen = ""
                }, 3000)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        showBlast()
        routeFromWeeklySynopsis()
    }

    private fun setWeeklyMonthlyYearlyList() {
        DateHelper
        val week = WeekModel()
        weeklyList.add(week)
        val list1 = viewModel.fitnessHelper.getAllWeeklyGroupListFromJoiningToCurrentDate(
            viewModel.joiningDate,
            DateHelper.currentDateAsStringyyyyMMdd,
            DateHelper.SERVER_DATE_YYYYMMDD
        )
        weeklyList.addAll(list1)

        val month = MonthModel()
        monthlyList.add(month)
        val list2 = viewModel.fitnessHelper.getAllMonthsListFromJoiningToCurrentDate(
            viewModel.joiningDate,
            DateHelper.currentDateAsStringyyyyMMdd,
            DateHelper.SERVER_DATE_YYYYMMDD
        )
        monthlyList.addAll(list2)

        val year = YearModel()
        yearlyList.add(year)
        val list3 = viewModel.fitnessHelper.getAllYearsListFromJoiningToCurrentDate(
            viewModel.joiningDate,
            DateHelper.currentDateAsStringyyyyMMdd,
            DateHelper.SERVER_DATE_YYYYMMDD
        )
        yearlyList.addAll(list3)

    }

    private fun registerObservers() {
        viewModel.stepsHistoryList.observe(this) { data ->
            if (data != null) {
                //syncAccountDetails()
            }
        }
        viewModel.getLatestStepsGoal.observe(this) { }
        viewModel.saveStepsList.observe(this) { data ->
            if (data != null) {
                //Utilities.printLogError("StepsDetails--->${data.stepsDetails}")
                updateFromForceSync()
            }
        }
    }

    private fun syncAccountDetails() {
        val selectedEmail = fitnessDataManager!!.getGoogleAccountName(this)
        Utilities.printLogError("Previous_Email--->$syncFrom")
        Utilities.printLogError("Selected_Email--->$selectedEmail")
        syncFrom = selectedEmail
        if (onGoogleAccountNameListener != null) {
            onGoogleAccountNameListener!!.onGoogleAccountNameReceived(syncFrom)
        }
    }

    private fun switchAccountDetails() {
        CleverTapHelper.pushEvent(this, CleverTapConstants.SWITCH_FITNESS_ACCOUNT)
        val selectedEmail = fitnessDataManager!!.getGoogleAccountName(this)
        //Utilities.printLogError("Previous_Email--->$syncFrom")
        //Utilities.printLogError("Selected_Email--->$selectedEmail")
        if (onGoogleAccountNameListener != null) {
            if (!Utilities.isNullOrEmpty(selectedEmail)) {
                if (syncFrom != selectedEmail) {
                    isForceSync = true
                    viewModel.setIsFitnessDataNotSync(true)
                    getTodayFitnessData()
                } else {
                    Utilities.toastMessageShort(
                        this,
                        resources.getString(R.string.MSG_SAME_GOOGLE_ACCOUNT)
                    )
                }
            }
            syncFrom = selectedEmail
            onGoogleAccountNameListener!!.onGoogleAccountNameReceived(syncFrom)
        }
    }

    fun updateFromForceSync() {
        if (isForceSync) {
            isForceSync = false
            //updateTodayData("saveStepsList")
            Utilities.printLogError("tabSelected--->$tabSelected")
            if (tabSelected != 0) {
                binding.tabLayout.getTabAt(0)!!.select()
            }
        }
    }

    private fun getTodayFitnessData() {
        try {
            viewModel.showProgressBar()
            val today = Calendar.getInstance().time
            fitnessDataManager!!.readHistoryData(today, today).addOnCompleteListener {
                if (fitnessDataManager!!.fitnessDataArray.length() > 0) {
                    val todayData = fitnessDataManager!!.fitnessDataArray.getJSONObject(0)
                    Utilities.printData("TodayFitnessData", todayData)
                    todayFitnessData.recordDate = todayData.getString(Constants.RECORD_DATE)
                    todayFitnessData.stepsCount = todayData.getString(Constants.STEPS_COUNT).toInt()
                    todayFitnessData.calories = todayData.getString(Constants.CALORIES).toInt()
                    todayFitnessData.distance =
                        todayData.get(Constants.DISTANCE).toString().toDouble()
                    todayFitnessData.activeTime = todayData.getString(Constants.ACTIVE_TIME)
                    viewModel.hideProgressBar()
                } else {
                    todayFitnessData = FitnessEntity.StepGoalHistory(lastRefreshed = Date())
                    //Utilities.printLogError("Fitness Data not Available")
                    viewModel.hideProgressBar()
                }
                viewModel.getLatestStepGoal(this, latestGoalListener!!, viewModel.stepCountHelper)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    interface OnLatestGoalListener {
        fun onLatestGoalReceived(latestGoal: GetStepsGoalModel.LatestGoal)
    }

    fun setLatestGoalListener(listener: OnLatestGoalListener) {
        this.latestGoalListener = listener
    }

    interface OnGoogleAccountNameListener {
        fun onGoogleAccountNameReceived(name: String)
    }

    fun setGoogleAccountNameListener(listener: OnGoogleAccountNameListener) {
        this.onGoogleAccountNameListener = listener
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        val drawable = menu.getItem(0).icon
        if (drawable != null) {
            drawable.mutate()
            drawable.setColorFilter(
                resources.getColor(R.color.textViewColor),
                PorterDuff.Mode.SRC_ATOP
            )
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_force_sync) {
            screen = ""
            showDialog(
                listener = this,
                title = this.resources.getString(R.string.SWITCH_ACCOUNT),
                message = this.resources.getString(R.string.FORCE_SYNC_CONFORMATION),
                leftText = this.resources.getString(R.string.NO),
                rightText = this.resources.getString(R.string.YES)
            )
        }
        return true
    }

    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
        if (isButtonRight) {
            if (screen.equals("", ignoreCase = true)) {
                //isForceSync = true
                if (fitnessDataManager!!.oAuthPermissionsApproved()) {
                    fitnessDataManager!!.signOutGoogleAccount()
                    fitnessDataManager!!.fitSignIn(FitRequestCode.READ_DATA)
                } else {
                    fitnessDataManager!!.fitSignIn(FitRequestCode.READ_DATA)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            //Utilities.printLogError("requestCode-> $requestCode")
            //Utilities.printLogError("resultCode-> $resultCode")
            //Utilities.printLogError("data-> $data")

            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == FitRequestCode.READ_DATA.value) {
                    //Utilities.printLogError("onActivityResult READ_DATA")
                    switchAccountDetails()
                }
            } else {
                FitnessDataManager(this).oAuthErrorMsg(requestCode, resultCode)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            try {
                //Utilities.printLogError("requestCode-> $requestCode")
                Utilities.printLogError("resultCode-> $resultCode")
                Utilities.printLogError("data-> $data")

                if (resultCode == Activity.RESULT_OK) {
                    if (requestCode == FitRequestCode.READ_DATA.value) {
                        Utilities.printLogError("onActivityResult READ_DATA")
                        if (googleAccountListener != null) {
                            googleAccountListener!!.onGoogleAccountSelection(Constants.SUCCESS)
                        }
                        //stepCountHelper.synchronize(this)
                    }
                } else {
                    FitnessDataManager(this).oAuthErrorMsg(requestCode, resultCode)
                    if (googleAccountListener != null) {
                        googleAccountListener!!.onGoogleAccountSelection(Constants.FAILURE)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }*/

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarFitLayout.toolBarFitness)
        binding.toolbarFitLayout.toolbarTitle.text =
            resources.getString(R.string.TITLE_ACTIVITY_TRACKER)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        binding.toolbarFitLayout.toolBarFitness.navigationIcon?.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                appColorHelper.textColor, BlendModeCompat.SRC_ATOP
            )

        binding.toolbarFitLayout.toolBarFitness.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    override fun onBackPressed() {
        //super.onBackPressed()
        when (from) {
            Constants.TRACK_PARAMETER -> finish()
            else -> routeToHomeScreen()
        }
    }

    private fun showBlast() {
        Utilities.printLogError("fromBlast--->$screen")
        if (screen.equals("STEPS_DAILY_TARGET", ignoreCase = true)) {
            binding.imgCustomAnim.setAnimation(R.raw.blast_2)
            Handler(Looper.getMainLooper()).postDelayed({
                showDialog(
                    listener = this,
                    title = notificationTitle,
                    message = notificationMsg,
                    showDismiss = false,
                    showLeftBtn = false,
                    rightText = this.resources.getString(R.string.OK)
                )
            }, 2000)
        }
    }

    private fun routeFromWeeklySynopsis() {
        Utilities.printLogError("fromWeeklySynopsis--->$screen")
        if (screen.equals("STEPS_WEEKLY_SYNOPSIS", ignoreCase = true)) {
            binding.tabLayout.getTabAt(1)!!.select()
        }
    }

    private fun routeToHomeScreen() {
        openAnotherActivity(destination = NavigationConstants.HOME, clearTop = true)
    }

}
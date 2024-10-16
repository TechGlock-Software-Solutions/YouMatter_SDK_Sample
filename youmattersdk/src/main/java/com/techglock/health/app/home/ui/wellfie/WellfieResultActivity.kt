package com.techglock.health.app.home.ui.wellfie

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseActivity
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.FirebaseConstants
import com.techglock.health.app.common.constants.NavigationConstants
import com.techglock.health.app.common.extension.openAnotherActivity
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.DefaultNotificationDialog
import com.techglock.health.app.common.utils.FirebaseHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.common.utils.showDialog
import com.techglock.health.app.databinding.ActivityWellfieResultBinding
import com.techglock.health.app.home.adapter.WellfieResultAdapter
import com.techglock.health.app.home.common.DataHandler
import com.techglock.health.app.home.common.WellfieSingleton
import com.techglock.health.app.home.viewmodel.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray

@AndroidEntryPoint
class WellfieResultActivity : BaseActivity(), WellfieResultAdapter.OnWellfieParameterClickListener,
    DefaultNotificationDialog.OnDialogValueListener {

    private val viewModel: DashboardViewModel by lazy {
        ViewModelProvider(this)[DashboardViewModel::class.java]
    }
    private lateinit var binding: ActivityWellfieResultBinding
    private val appColorHelper = AppColorHelper.instance!!
    private val wellfieSingleton = WellfieSingleton.getInstance()!!

    private var wellfieResultAdapter: WellfieResultAdapter? = null
    private var from = ""
    private var msg = ""
    private var title = ""
    private var tip = JSONArray()

    override fun getViewModel(): BaseViewModel = viewModel

    @SuppressLint("SetTextI18n")
    override fun onCreateEvent(savedInstanceState: Bundle?) {
        //binding = DataBindingUtil.setContentView(this, R.layout.activity_wellfie_result)
        binding = ActivityWellfieResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //binding.lifecycleOwner = this
        try {
            CleverTapHelper.pushEvent(this, CleverTapConstants.SCAN_VITALS_RESULT)
            setupToolbar()
            if (intent.hasExtra(Constants.FROM)) {
                from = intent.getStringExtra(Constants.FROM)!!
            }
            Utilities.printLogError("from--->$from")

            if (from.equals(Constants.DASHBOARD, ignoreCase = true)) {
                binding.toolBarView.toolbarTitle.text = resources.getString(R.string.PAST_VITALS)
                binding.txtLastUpdatedDateTime.visibility = View.VISIBLE
                binding.btnRescanVitals.visibility = View.VISIBLE
                //binding.txtLastUpdatedDateTime.text = "Last Updated : ${DateHelper.currentDateAsStringddMMMyyyyNew} - ${DateHelper.currentTimeAs_am_pm()}"
                binding.txtLastUpdatedDateTime.text =
                    "${resources.getString(R.string.LAST_UPDATED)} : ${
                        DateHelper.convertDateSourceToDestination(
                            wellfieSingleton.dateTime,
                            DateHelper.SERVER_DATE_YYYYMMDD,
                            DateHelper.DATEFORMAT_DDMMMYYYY_NEW
                        )
                    }"
            } else {
                binding.toolBarView.toolbarTitle.text = resources.getString(R.string.CURRENT_VITALS)
                binding.txtLastUpdatedDateTime.visibility = View.GONE
                binding.btnRescanVitals.visibility = View.GONE
            }
            //setupToolbar()
            initialise()
            setClickable()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        FirebaseHelper.logScreenEvent(FirebaseConstants.WELLFIE_RESULT_SCREEN)
    }

    @SuppressLint("SetTextI18n")
    private fun initialise() {
        wellfieResultAdapter = WellfieResultAdapter(this, from, this)
        binding.rvWellfieResult.layoutManager = GridLayoutManager(this, 2)
        binding.rvWellfieResult.adapter = wellfieResultAdapter
        wellfieResultAdapter!!.updateData(wellfieSingleton.getWellfieResultList())

        //val strDisclaimer = resources.getString(R.string.WELLFIE_DISCLAIMER1) + "<a><B><font color='#0000000'>" + resources.getString(R.string.WELLFIE_DISCLAIMER2) + "</font></B></a>"
        val strLearnMore =
            "<u><a><B><font color='#00cfff'>" + resources.getString(R.string.LEARN_MORE) + "</font></B></a></u>"
        binding.txtDisclaimer.text = Html.fromHtml(
            resources.getString(R.string.WELLFIE_DISCLAIMER_LABEL) + " " + strLearnMore
        )
    }

    private fun setClickable() {

        binding.btnRescanVitals.setOnClickListener {
            openAnotherActivity(destination = NavigationConstants.BMI_VITALS_SCREEN)
        }

        binding.txtDisclaimer.setOnClickListener {
            showDialog(
                listener = this,
                title = resources.getString(R.string.DISCLAIMER),
                message = "<a><font color='#3A393B'>" + resources.getString(R.string.WELLFIE_DISCLAIMER1) + "</font></a>" + "<a><B><font color='#000000'>" + resources.getString(
                    R.string.WELLFIE_DISCLAIMER2
                ) + "</font></B></a>",
                showLeftBtn = false
            )
        }

    }

    /*    private fun showDisclaimer() {
            showDialog(
                listener = this,
                title = resources.getString(R.string.DISCLAIMER),
                message = resources.getString(R.string.WELLFIE_DISCLAIMER1) + "<a><B><font color='#0000000'>" + resources.getString(R.string.WELLFIE_DISCLAIMER2) + "</font></B></a>",
                showLeftBtn = false)
        }*/

    override fun onWellfieParameterClick(wellfieResultModel: DataHandler.WellfieResultModel) {
        if (!Utilities.isNullOrEmpty(wellfieResultModel.paramToolTip)) {
            tip = JSONArray(wellfieResultModel.paramToolTip)
            msg = ""
            for (i in 0 until tip.length()) {
                msg += "${tip[i]} <br/>"
            }
            title = if (wellfieResultModel.paramCode == "STRESS_INDEX") {
                "Stress"
            } else {
                wellfieResultModel.paramName
            }
            showDialog(
                listener = this,
                title = title,
                message = msg,
                showLeftBtn = false
            )
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolBarView.toolbarCommon)
        //binding.toolBarView.toolbarTitle.text = "Your Current Vitals"
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        binding.toolBarView.toolbarCommon.navigationIcon?.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                appColorHelper.textColor, BlendModeCompat.SRC_ATOP
            )

        binding.toolBarView.toolbarCommon.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        showHraInsightDialog()
    }

    private fun showHraInsightDialog() {
        resources.getString(R.string.WELLFIE_HRA_MSG1)
        val dialogData = DefaultNotificationDialog.DialogData()
        dialogData.title = resources.getString(R.string.WELLFIE_HRA_TITLE)
        dialogData.message =
            "<a>" + "${resources.getString(R.string.WELLFIE_HRA_MSG1)}.<br/>${resources.getString(R.string.WELLFIE_HRA_MSG2)}" + "<a>"
        dialogData.btnLeftName = resources.getString(R.string.NO_THANKS)
        dialogData.btnRightName = resources.getString(R.string.PROCEED)
        dialogData.showDismiss = false
        val defaultNotificationDialog = DefaultNotificationDialog(
            this,
            object : DefaultNotificationDialog.OnDialogValueListener {
                override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
                    if (isButtonLeft) {
                        performBeforeBack()
                    }
                    if (isButtonRight) {
                        navigateToHRA()
                    }
                }
            },
            dialogData
        )
        defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        defaultNotificationDialog.show()
    }

    fun navigateToHRA() {
        openAnotherActivity(destination = NavigationConstants.HRA_INFO)
    }

    fun performBeforeBack() {
        wellfieSingleton.clearData()
        routeToHomeScreen()
    }

    private fun routeToHomeScreen() {
        openAnotherActivity(destination = NavigationConstants.HOME, clearTop = true)
    }

    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {}

}
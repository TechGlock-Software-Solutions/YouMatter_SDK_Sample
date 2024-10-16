package com.techglock.health.app.hra.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseActivity
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.*
import com.techglock.health.app.common.utils.PermissionUtil.AppPermissionListener
import com.techglock.health.app.databinding.ActivityHraSummaryBinding
import com.techglock.health.app.home.ui.HomeMainActivity
import com.techglock.health.app.hra.HraHomeActivity
import com.techglock.health.app.hra.adapter.HraLabTestsAdapter
import com.techglock.health.app.hra.viewmodel.HraSummaryViewModel
import com.techglock.health.app.model.hra.HraAssessmentSummaryModel
import com.techglock.health.app.model.hra.HraLabTest
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class HraSummaryActivity : BaseActivity(), DefaultNotificationDialog.OnDialogValueListener {

    private lateinit var binding: ActivityHraSummaryBinding
    private val viewModel: HraSummaryViewModel by lazy {
        ViewModelProvider(this)[HraSummaryViewModel::class.java]
    }
    private val appColorHelper = AppColorHelper.instance!!
    private val permissionUtil = PermissionUtil

    private var context: Context? = null
    private var activity: Activity? = null
    private var personId = ""
    private var hraCutOff = 0
    private var wellnessscore = 0
    private var currentHRAHistoryID = 0
    private var wellnessScoreProgress = 0
    private var color: Int = appColorHelper.primaryColor()
    private var observation = ""
    private var hraLabTestsAdapter: HraLabTestsAdapter? = null
    private var labTestsDoctorSuggestedAdapter: HraLabTestsAdapter? = null
    private val permissionListener = object : AppPermissionListener {
        override fun isPermissionGranted(isGranted: Boolean) {
            Utilities.printLogError("$isGranted")
            if (isGranted) {
                viewModel.callDownloadReport()
            }
        }
    }

    override fun getViewModel(): BaseViewModel = viewModel

    private val onBackPressedCallBack = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (viewModel.personId != personId) {
                viewModel.clearHraDataTables()
            } else {
                viewModel.clearPreviousQuesDataAndTable()
            }
            startActivity(Intent(this@HraSummaryActivity, HomeMainActivity::class.java))
        }
    }

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivityHraSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallBack)
        context = this
        activity = this
        try {
            val i = intent
            if (i.hasExtra(Constants.PERSON_ID)) {
                personId = i.getStringExtra(Constants.PERSON_ID)!!
            }
            setUpToolbar()
            initLayout()
            registerObservers()
            setClickable()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initLayout() {
        try {
            viewModel.getMedicalProfileSummary(true, personId)
            viewModel.getAssessmentSummary(true, personId)
            viewModel.getListRecommendedTests(true, personId)
            setWellnessScore()
            setHraRiskSummary()
            setLabTestsList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun registerObservers() {
        viewModel.medicalProfileSummary.observe(this) { }
        viewModel.assessmentSummary.observe(this) { }
        viewModel.listRecommendedTests.observe(this) { }
    }

    private fun setClickable() {

        binding.btnRestartHra.setOnClickListener {
            viewModel.clearPreviousQuesDataAndTable()
//            openAnotherActivity(destination = NavigationConstants.HRA_HOME, clearTop = true)
            startActivity(Intent(this, HraHomeActivity::class.java))
        }

        binding.btnDownloadReport.setOnClickListener {
            val permissionResult = permissionUtil.checkStoragePermission(permissionListener, this)
            if (permissionResult) {
                viewModel.callDownloadReport()
            }
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setWellnessScore() {

        binding.layoutHighRisk.setViewColor(R.color.risk_high)
        binding.layoutModerateRisk.setViewColor(R.color.risk_moderate)
        binding.layoutHealthy.setViewColor(R.color.risk_healthy)
        binding.layoutOptimum.setViewColor(R.color.risk_optimum)

        binding.layoutHighRisk.setViewBackgroundColor(R.color.bg_risk_extremly_severe)
        binding.layoutModerateRisk.setViewBackgroundColor(R.color.bg_risk_moderate)
        binding.layoutHealthy.setViewBackgroundColor(R.color.bg_risk_mild)
        binding.layoutOptimum.setViewBackgroundColor(R.color.bg_risk_normal)

        binding.layoutHighRisk.setRangeValue("0 - 15")
        binding.layoutModerateRisk.setRangeValue("16 - 45")
        binding.layoutHealthy.setRangeValue("46 - 85")
        binding.layoutOptimum.setRangeValue("86 - 100")

        binding.layoutHighRisk.setRangeTitle(resources.getString(R.string.HIGH_RISK))
        binding.layoutModerateRisk.setRangeTitle(resources.getString(R.string.MODERATE_RISK))
        binding.layoutHealthy.setRangeTitle(resources.getString(R.string.HEALTHY))
        binding.layoutOptimum.setRangeTitle(resources.getString(R.string.OPTIMUM))

        binding.indicatorScore.isClickable = false
        binding.indicatorScore.setOnTouchListener { _: View?, _: MotionEvent? -> true }

        viewModel.hraSummaryDetails.observe(this) {
            if (it != null) {
                val wellnessSummary = it
                //Utilities.printLog("HraSummaryDetails :- " + wellnessSummary)

                if (!Utilities.isNullOrEmptyOrZero(wellnessSummary.currentHRAHistoryID.toString())) {
                    currentHRAHistoryID = wellnessSummary.currentHRAHistoryID
                }
                if (!Utilities.isNullOrEmptyOrZero(wellnessSummary.hraCutOff)) {
                    hraCutOff = wellnessSummary.hraCutOff.toInt()
                }

                wellnessscore = wellnessSummary.scorePercentile.toInt()

                wellnessScoreProgress = wellnessscore
                if (wellnessScoreProgress <= 0) {
                    wellnessScoreProgress = 0
                }

                Utilities.printLog("WellnessScore,HrACutOff,CurrentHRAHistoryID--->$wellnessscore,$hraCutOff,$currentHRAHistoryID")

                when {
                    wellnessscore in 0..15 -> {
                        observation = resources.getString(R.string.HIGH_RISK)
                        color = R.color.risk_high
                    }

                    wellnessscore in 16..45 -> {
                        observation = resources.getString(R.string.MODERATE_RISK)
                        color = R.color.risk_moderate
                    }

                    wellnessscore in 46..85 -> {
                        observation = resources.getString(R.string.HEALTHY)
                        color = R.color.risk_healthy
                    }

                    wellnessscore > 85 -> {
                        observation = resources.getString(R.string.OPTIMUM)
                        color = R.color.risk_optimum
                    }
                }

                setColors(color)
                //binding.indicatorScore.progress = wellnessscore
                //binding.indicatorScore.setProgressWithAnimation(wellnessscore.toDouble())
                binding.indicatorScore.setValueAnimated(
                    wellnessscore.toFloat(),
                    Constants.ANIMATION_DURATION.toLong()
                )
                binding.txtScore.text = wellnessscore.toString()
                binding.txtObservation.text = observation

                when {
                    currentHRAHistoryID == 0 -> {
                        binding.layoutScoreSummary.visibility = View.GONE
                        binding.txtUnableToCalculateScore.visibility = View.VISIBLE
                        binding.txtUnableToCalculateScore.text =
                            resources.getString(R.string.WELLNESS_DESC_CURRENT_HRA_HISTORY_ID)
                    }

                    hraCutOff == 0 -> {
                        binding.layoutScoreSummary.visibility = View.GONE
                        binding.txtUnableToCalculateScore.visibility = View.VISIBLE
                        binding.txtUnableToCalculateScore.text =
                            resources.getString(R.string.WELLNESS_DESC_HRA_CUTOFF)
                    }

                    wellnessscore <= 0 -> {
                        binding.layoutScoreSummary.visibility = View.GONE
                        binding.txtUnableToCalculateScore.visibility = View.VISIBLE
                        binding.txtUnableToCalculateScore.text =
                            resources.getString(R.string.WELLNESS_DESC_SCORE_ZERO_NEGATIVE)
                    }

                    else -> {
                        binding.layoutScoreSummary.visibility = View.VISIBLE
                        binding.txtUnableToCalculateScore.visibility = View.GONE
                    }
                }

            }
        }
    }

    private fun setHraRiskSummary() {

        val atRiskList: ArrayList<HraAssessmentSummaryModel.AssessmentDetails> = ArrayList()
        val needImprovementsList: ArrayList<HraAssessmentSummaryModel.AssessmentDetails> =
            ArrayList()
        val moderateList: ArrayList<HraAssessmentSummaryModel.AssessmentDetails> = ArrayList()
        val highList: ArrayList<HraAssessmentSummaryModel.AssessmentDetails> = ArrayList()
        binding.layoutRiskSummery.visibility = View.GONE
        viewModel.hraAssessmentSummaryDetails.observe(this) {
            if (it != null) {
                for (i in it) {
                    if (i.riskLevel.equals("At Risk", ignoreCase = true)
                        || i.riskLevel.equals("Low", ignoreCase = true)
                    ) {
                        if (!atRiskList.contains(i)) {
                            atRiskList.add(i)
                        }
                    }
                    if (i.riskLevel.equals("Need improvements", ignoreCase = true)) {
                        if (!atRiskList.contains(i)) {
                            needImprovementsList.add(i)
                        }
                    }
                    if (i.riskLevel.equals("Moderate", ignoreCase = true)) {
                        if (!atRiskList.contains(i)) {
                            moderateList.add(i)
                        }
                    }
                    if (i.riskLevel.equals("High", ignoreCase = true)) {
                        if (!atRiskList.contains(i)) {
                            highList.add(i)
                        }
                    }
                }
                //Utilities.printLog("AtRiskList---->" + atRiskList.toString())
                //Utilities.printLog("NeedImprovementsList---->" + needImprovementsList.toString())
                //Utilities.printLog("ModerateList---->" + moderateList.toString())
                //Utilities.printLog("HighList---->" + highList.toString())
                // ************************ At Risk data ************************
                val sbLow = StringBuilder()
                var separatorLow = ""
                for (assessmentIndex in atRiskList.indices) {
                    val strRiskLevel = atRiskList[assessmentIndex].riskCategory
                    sbLow.append(separatorLow + strRiskLevel)
                    separatorLow = ", "
                }
                val strLow = sbLow.toString()
                Utilities.printLogError("AtRisk---->$strLow")
                binding.cardAtRisk.visibility = View.VISIBLE
                if (!Utilities.isNullOrEmpty(strLow)) {
                    binding.txtAtRiskDesc.text = strLow
                } else {
                    binding.txtAtRiskDesc.text = "-"
                    binding.cardAtRisk.visibility = View.GONE
                }
                // ************************ At Risk data ************************

                //************************ Moderate Risk data ************************
                val sbNeedImprovements = StringBuilder()
                var separatorNeedImprovements = ""
                for (assessmentIndex in needImprovementsList.indices) {
                    val strRiskLevel = needImprovementsList[assessmentIndex].riskCategory
                    sbNeedImprovements.append(separatorNeedImprovements + strRiskLevel)
                    separatorNeedImprovements = ", "
                }

                val sbModerate = StringBuilder()
                var separatorModerate = ""
                for (assessmentIndex in moderateList.indices) {
                    val strRiskLevel = moderateList[assessmentIndex].riskCategory
                    sbModerate.append(separatorModerate + strRiskLevel)
                    separatorModerate = ", "
                }

                var strNeedImprovements = sbNeedImprovements.toString()
                val strModerate = sbModerate.toString()
                if (!Utilities.isNullOrEmpty(strNeedImprovements) && !Utilities.isNullOrEmpty(
                        strModerate
                    )
                ) {
                    strNeedImprovements += ", "
                }
                Utilities.printLogError("Moderate---->${(strNeedImprovements + strModerate)}")
                binding.txtModerateDesc.text = (strNeedImprovements + strModerate)
                binding.cardModerateRisk.visibility = View.VISIBLE
                if (Utilities.isNullOrEmpty(strNeedImprovements) && Utilities.isNullOrEmpty(
                        strModerate
                    )
                ) {
                    binding.txtModerateDesc.text = "-"
                    binding.cardModerateRisk.visibility = View.GONE
                }
                //************************ Moderate Risk data ************************

                //************************ High Risk data ************************
                val sbHigh = StringBuilder()
                var separatorHigh = ""
                for (assessmentIndex in highList.indices) {
                    val strRiskLevel = highList[assessmentIndex].riskCategory
                    sbHigh.append(separatorHigh + strRiskLevel)
                    separatorHigh = ", "
                }
                val strHigh = sbHigh.toString()
                Utilities.printLogError("High---->${strHigh}")
                binding.cardHighRisk.visibility = View.VISIBLE
                if (!Utilities.isNullOrEmpty(strHigh)) {
                    binding.txtHighDesc.text = strHigh
                } else {
                    binding.txtHighDesc.text = "-"
                    binding.cardHighRisk.visibility = View.GONE
                }
                binding.layoutRiskSummery.visibility = View.VISIBLE
                //************************ High Risk data ************************
                if (Utilities.isNullOrEmpty(strLow) && Utilities.isNullOrEmpty(strModerate) &&
                    Utilities.isNullOrEmpty(strNeedImprovements) && Utilities.isNullOrEmpty(strHigh)
                ) {
                    binding.layoutRisks.visibility = View.GONE
                    binding.txtCongratsMsg.visibility = View.VISIBLE
                } else {
                    binding.layoutRisks.visibility = View.VISIBLE
                    binding.txtCongratsMsg.visibility = View.GONE
                }
            }
        }
    }

    private fun setLabTestsList() {
        binding.layoutLabTests.visibility = View.GONE

        viewModel.hraRecommendedTests.observe(this) {
            val labTestsDetailsList = it
            Utilities.printLog("TotalTests--->${labTestsDetailsList.size}")

            val labTestsCommon = labTestsDetailsList.filter { labTest ->
                !labTest.Frequency.equals("As suggested by your doctor", ignoreCase = true)
            }
            val labTestsDoctorSuggested = labTestsDetailsList.filter { labTest ->
                labTest.Frequency.equals("As suggested by your doctor", ignoreCase = true)
            }

            if (labTestsDetailsList.isNotEmpty()) {
                binding.layoutLabTests.visibility = View.VISIBLE

                if (labTestsCommon.isNotEmpty()) {
                    binding.cardCommonLabtests.visibility = View.VISIBLE
                    hraLabTestsAdapter = HraLabTestsAdapter(labTestsCommon, viewModel, this, this)
                    binding.rvLabTests.adapter = hraLabTestsAdapter
                    binding.rvLabTests.setExpanded(true)
                } else {
                    binding.cardCommonLabtests.visibility = View.GONE
                }

                if (labTestsDoctorSuggested.isNotEmpty()) {
                    binding.cardDoctorSuggestedLabtests.visibility = View.VISIBLE
                    labTestsDoctorSuggestedAdapter =
                        HraLabTestsAdapter(labTestsDoctorSuggested, viewModel, this, this)
                    binding.rvLabTestDoctorSuggested.adapter = labTestsDoctorSuggestedAdapter
                    binding.rvLabTestDoctorSuggested.setExpanded(true)
                } else {
                    binding.cardDoctorSuggestedLabtests.visibility = View.GONE
                }

            } else {
                binding.layoutLabTests.visibility = View.GONE
            }

        }
    }

    private fun setColors(color: Int) {
        //binding.indicatorScore.progressColor = ContextCompat.getColor(this, color)
        binding.indicatorScore.setBarColor(resources.getColor(color))
    }

    fun showLabTestDetailsDialog(labTest: HraLabTest) {
        showDialog(
            listener = this,
            title = labTest.LabTestName,
            message = labTest.Reasons,
            showLeftBtn = false
        )
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolBarView.toolbarSummary)
        binding.toolBarView.toolbarTitleSummary.text = resources.getString(R.string.HRA_SUMMARY)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        binding.toolBarView.toolbarSummary.navigationIcon?.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                appColorHelper.textColor,
                BlendModeCompat.SRC_ATOP
            )

        binding.toolBarView.toolbarSummary.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }


    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {}

    /*    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            Utilities.printLogError("requestCode-> $requestCode")
            Utilities.printLogError("resultCode-> $resultCode")
            Utilities.printLogError("data-> $data")
            try {
                if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQ_CODE_SAF) {
                    if (data != null) {
                        //this is the uri user has provided us for folder Access
                        val treeUri: Uri = data.data!!
                        permissionUtil.releasePermissions(treeUri,this,storageAccessListener)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }*/

    /*    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val per = Environment.isExternalStorageManager()
                    Utilities.printLogError("requestCode---> $requestCode")
                    Utilities.printLogError("permissionGranted---> $per")
                    when(requestCode) {
                        Constants.REQ_CODE_STORAGE -> {
                            if (per) {
                                permissionListener.isPermissionGranted(true)
                            } else {
                                Utilities.toastMessageShort(this,resources.getString(R.string.ERROR_STORAGE_PERMISSION))
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }*/

}
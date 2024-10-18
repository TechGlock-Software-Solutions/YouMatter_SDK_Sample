package com.techglock.health.app.home.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.techglock.health.app.R
import com.techglock.health.app.blogs.BlogsActivity
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.NavigationConstants
import com.techglock.health.app.common.constants.PreferenceConstants
import com.techglock.health.app.common.extension.openAnotherActivity
import com.techglock.health.app.common.fitness.FitnessDataManager
import com.techglock.health.app.common.taptargetview.TapTarget
import com.techglock.health.app.common.taptargetview.TapTargetSequence
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.PermissionUtil
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentHomeScreenBinding
import com.techglock.health.app.home.adapter.BlogDashboardAdapter
import com.techglock.health.app.home.adapter.FinancialCalculatorsAdapter
import com.techglock.health.app.home.adapter.LiveSessionAdapter
import com.techglock.health.app.home.adapter.NimeyaCalculatorsAdapter
import com.techglock.health.app.home.adapter.SlidingSudBannerDashboardAdapter
import com.techglock.health.app.home.adapter.SmitFitAdapter
import com.techglock.health.app.home.adapter.WellfieDashboardAdapter
import com.techglock.health.app.home.common.DataHandler
import com.techglock.health.app.home.common.DataHandler.*
import com.techglock.health.app.home.common.HRAObservationModel
import com.techglock.health.app.home.common.NimeyaSingleton
import com.techglock.health.app.home.common.OnPolicyBannerListener
import com.techglock.health.app.home.common.WellfieSingleton
import com.techglock.health.app.home.common.extensions.showBannerDialog
import com.techglock.health.app.home.di.ScoreListener
import com.techglock.health.app.home.ui.WebViews.FeedUpdateActivity
import com.techglock.health.app.home.ui.WebViews.LeadershipExperincesActivity
import com.techglock.health.app.home.ui.WebViews.SaltWebViewActivity
import com.techglock.health.app.home.ui.aktivo.AktivoPermissionsActivity
import com.techglock.health.app.home.ui.nimeya.NimeyaActivity
import com.techglock.health.app.home.ui.nimeya.NimeyaWebViewActivity
import com.techglock.health.app.home.viewmodel.BackgroundCallViewModel
import com.techglock.health.app.home.viewmodel.DashboardViewModel
import com.techglock.health.app.home.viewmodel.NimeyaViewModel
import com.techglock.health.app.hra.ui.HraInfoActivity
import com.techglock.health.app.hra.ui.HraSummaryActivity
import com.techglock.health.app.medication_tracker.MedicationHomeActivity
import com.techglock.health.app.model.blogs.BlogItem
import com.techglock.health.app.model.entity.HRASummary
import com.techglock.health.app.model.entity.TrackParameterMaster
import com.techglock.health.app.model.home.LiveSessionModel
import com.techglock.health.app.model.home.WellfieGetVitalsModel
import com.techglock.health.app.model.sudLifePolicy.PolicyProductsModel.PolicyProducts
import com.techglock.health.app.model.toolscalculators.UserInfoModel
import com.techglock.health.app.records_tracker.HealthRecordsActivity
import com.techglock.health.app.repository.utils.Resource
import com.techglock.health.app.tools_calculators.ToolsCalculatorsHomeActivity
import com.techglock.health.app.track_parameter.ParameterHomeActivity
import com.techglock.health.app.water_tracker.WaterTrackerActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.smitfit.media.lib.ui.feature.main.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
//import nvest.com.nvestlibrary.NvestSud
import java.util.Timer
import java.util.TimerTask

@AndroidEntryPoint
class HomeScreenFragment : BaseFragment(), ScoreListener, SmitFitAdapter.OnSmitFitFeatureListener,
    LiveSessionAdapter.OnLiveSessionListener,
    FinancialCalculatorsAdapter.OnFinancialCalculatorListener,
    BlogDashboardAdapter.OnBlogClickListener, HomeMainActivity.OnHelpClickListener,
    HomeMainActivity.OnAktivoListener, OnPolicyBannerListener,
    NimeyaCalculatorsAdapter.OnNimeyaCalculatorListener {

    private lateinit var binding: FragmentHomeScreenBinding
    private val viewModel: DashboardViewModel by lazy {
        ViewModelProvider(this)[DashboardViewModel::class.java]
    }
    private val backGroundCallViewModel: BackgroundCallViewModel by lazy {
        ViewModelProvider(this)[BackgroundCallViewModel::class.java]
    }

    private val nimeyaViewModel: NimeyaViewModel by lazy {
        ViewModelProvider(this)[NimeyaViewModel::class.java]
    }

    private var bannerDialog: Dialog? = null
    var policyProduct = PolicyProducts()
    private var nimeyaSingleton = NimeyaSingleton.getInstance()!!

    private var fitnessDataManager: FitnessDataManager? = null
    private lateinit var navigation: BottomNavigationView
    private lateinit var fm: FragmentManager
    private val permissionUtil = PermissionUtil
    private val wellfieSingleton = WellfieSingleton.getInstance()!!
    private val permissionListener = object : PermissionUtil.AppPermissionListener {
        override fun isPermissionGranted(isGranted: Boolean) {
            Utilities.printLogError("$isGranted")
            if (isGranted) {
                startActivity(Intent(requireContext(), HealthRecordsActivity::class.java))
            }
        }
    }

    private var blogAdapter: BlogDashboardAdapter? = null
    private var wellfieDashboardAdapter: WellfieDashboardAdapter? = null
    private var bpSystolic = ""
    private var bpDiastolic = ""
    private var bpStatus = ""
    private var bpColor = ""
    private var color = ""
    private var observation = ""
    private var bmi = ""
    var tapCount = 0
    private var smitFitAdapter: SmitFitAdapter? = null
    private var liveSessionAdapter: LiveSessionAdapter? = null
    private var financialCalculatorsAdapter: FinancialCalculatorsAdapter? = null
    private var nimeyaCalculatorsAdapter: NimeyaCalculatorsAdapter? = null

    private var eventTitle = ""
    private var eventType = ""
    private var eventDesc = ""
    private var eventDestination = ""
    private var eventFeatureCode = ""
    private var currentPage = 0
    private var slidingImageDots: Array<ImageView?> = arrayOf()
    private var slidingDotsCount = 0

    private var aktivoScore = 0
    private var aktivoMindScore = 0
    private var aktivoBadge = ""

    //private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
    //private var currentTime = dateFormat.parse(dateFormat.format(Date()))
    //private var slotTime = Date()
    //private var differenceInMinutes = 0

    //private val todayDate = DateHelper.currentDateAsStringyyyyMMdd
    //private val todayDate = "2023-12-16"
    //private val todayDate = "2023-10-16"

    /*  private var currentPage1 = 0
      private var slidingImageDotsChallenges: Array<ImageView?> = arrayOf()
      private var slidingDotsCountChallenges = 0*/

    //    private var aktivoManager: AktivoManager? = null
//    private var compositeDisposable: CompositeDisposable? = null
    private var animation: Animation? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utilities.printLogError("Inside=> onCreate")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as HomeMainActivity).setOnAktivoListener(this)
        (activity as HomeMainActivity).setOnHelpClickListener(this)
        (activity as HomeMainActivity).setToolbarInfo(0, showAppLogo = true, title = "", showBg = true)
        Utilities.printLog("Inside=> onViewCreated")
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeScreenBinding.inflate(inflater, container, false)
        try {
            initialise()
            setClickable()
            setObserver()
            /*if ( Utilities.preferenceUtils.getBooleanPreference(PreferenceConstants.IS_CAMPAIGN_DETAILS_AVAILABLE) ) {
                val campaignFeatureName = Utilities.preferenceUtils.getPreference(Constants.CAMPAIGN_FEATURE_NAME)
                Utilities.printLogError("CampaignFeatureName--->$campaignFeatureName")
                if ( !campaignFeatureName.equals(Constants.FEATURE_CODE_LIVE_SESSIONS,ignoreCase = true) ) {
                    Utilities.clearCampaignFeatureDetails()
                }
                openInternalFeature(campaignFeatureName)
            }*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Utilities.printLogError("Inside=> onCreateView")
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun initialise() {
        CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.DASHBOARD_SCREEN)
        animation = AnimationUtils.loadAnimation(requireContext(), R.anim.anim_pulse_once)
//        aktivoManager = AktivoManager.getInstance(requireContext())
//        compositeDisposable = CompositeDisposable()
        (activity as HomeMainActivity).registerListener(this)

        fitnessDataManager = FitnessDataManager(requireContext())
        binding.txtUserName.text = "${resources.getString(R.string.HI)} " + viewModel.preferenceUtil.getPreference(PreferenceConstants.FIRSTNAME, "")

        binding.layoutScanVitalsPrevious.visibility = View.GONE
        binding.layoutScanVitals.visibility = View.VISIBLE
        startWellfieShimmer()
        checkMptResult()

        wellfieDashboardAdapter = WellfieDashboardAdapter(requireContext())
        binding.rvScanVitals.setExpanded(true)
        binding.rvScanVitals.adapter = wellfieDashboardAdapter

        smitFitAdapter = SmitFitAdapter(requireContext(), this)
        binding.rvSmitFit.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvSmitFit.adapter = smitFitAdapter
        smitFitAdapter!!.updateList(DataHandler(requireContext()).getSmitFitFeatures())

        liveSessionAdapter = LiveSessionAdapter(requireContext(), this)
        binding.rvLiveSessions.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvLiveSessions.adapter = liveSessionAdapter

        nimeyaCalculatorsAdapter = NimeyaCalculatorsAdapter(requireContext(), this)
        binding.rvNimeyaCalculators.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvNimeyaCalculators.adapter = nimeyaCalculatorsAdapter
        nimeyaCalculatorsAdapter!!.updateList(DataHandler(requireContext()).getNimeyaCalculators())

        financialCalculatorsAdapter = FinancialCalculatorsAdapter(requireContext(), this)
        binding.rvFinancialCalculators.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvFinancialCalculators.adapter = financialCalculatorsAdapter
        financialCalculatorsAdapter!!.updateList(DataHandler(requireContext()).getFinancialCalculators())

        binding.viewSteps.setParamTitle(resources.getString(R.string.STEPS))
        binding.viewSleep.setParamTitle(resources.getString(R.string.SLEEP))
        binding.viewExercise.setParamTitle(resources.getString(R.string.EXERCISE))
        binding.viewSedentary.setParamTitle(resources.getString(R.string.SEDENTARY))
        binding.viewLightActivity.setParamTitle(resources.getString(R.string.LIGHT_ACTIVITY))
        binding.layoutBadgeProgress.visibility = View.GONE
        binding.layoutChallenges.visibility = View.GONE
//        startAktivoParameterShimmer()
//        startAktivoScoreShimmer()
//        startMindScoreShimmer()
//        getAktivoScore()

        //setUpSlidingViewPager(DataHandler(requireContext()).getSudBannersDashboardList1())
        //setUpBannerData(DataHandler(requireContext()).getSudBannersDashboardList1())
        startDashboardProductsShimmer()
        viewModel.callPolicyProductsApi(this)

        startLiveSessionsShimmer()
        //startBlogsShimmer()
        //requestGoogleFit

        //viewModel.fetchStepsGoal()
        viewModel.callEventsBannerApi()
        viewModel.callWellfieGetVitalsApi(this)
        viewModel.getMedicalProfileSummary()
        viewModel.callSmitFitEventsApi()
        checkNotificationPermission()
        //viewModel.callGetBlogsFromServerApi(5, 0, this)

        if (Utilities.getUserPreference(PreferenceConstants.ORG_NAME) == Constants.SUD_ORG_NAME) {
            binding.cardLeadershipExperinces.visibility = View.VISIBLE
        } else {
            binding.cardLeadershipExperinces.visibility = View.GONE
        }

        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.rvBlogs.layoutManager = layoutManager
        blogAdapter = BlogDashboardAdapter(this, viewModel, this)
        binding.rvBlogs.adapter = blogAdapter
    }

    private fun checkMptResult() {
        try {
            if (!Utilities.isNullOrEmpty(Utilities.getUserPreference(PreferenceConstants.MPT_RESULT_PAGE_URL))) {
                //binding.imgCohort.setImgUrl(Utilities.getUserPreference(PreferenceConstants.MPT_COHORT_ICON_URL))
                Utilities.loadImageUrl(
                    Utilities.getUserPreference(PreferenceConstants.MPT_COHORT_ICON_URL),
                    binding.imgCohort
                )
                binding.txtCohortTitle.text =
                    Utilities.getUserPreference(PreferenceConstants.MPT_COHORT_TITLE)
                binding.layoutSaltMptGiven.visibility = View.VISIBLE
                binding.layoutSaltMptNotGiven.visibility = View.GONE
            } else {
                binding.layoutSaltMptGiven.visibility = View.GONE
                binding.layoutSaltMptNotGiven.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setClickable() {

        binding.layoutBadgeProgress.setOnClickListener {
            CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.AKTIVO_BADGES)
            navigateToAktivo(Constants.AKTIVO_BADGES_CODE)
        }

        binding.cardAktivoLabs.setOnClickListener {
            CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.AKTIVO_DASHBOARD)
            navigateToAktivo(Constants.AKTIVO_DASHBOARD_CODE)
        }

        binding.txtAktivoViewDetails.setOnClickListener {
            binding.cardAktivoLabs.performClick()
        }

        binding.cardAktivoScore.setOnClickListener {
            CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.AKTIVO_SCORE)
            navigateToAktivo(Constants.AKTIVO_SCORE_CODE)
        }

        binding.cardMindScore.setOnClickListener {
            CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.MIND_SCORE)
            navigateToAktivo(Constants.AKTIVO_MIND_SCORE_CODE)
        }

        binding.layoutFeedUpdate.setOnClickListener {
            Utilities.printLogError("eventType--->$eventType")
            Utilities.printLogError("eventDestination--->$eventDestination")
            Utilities.printLogError("eventFeatureCode--->$eventFeatureCode")
            if (!Utilities.isNullOrEmpty(eventType)) {
                when (eventType) {
                    "INTERNAL_FEATURE" -> {
                        if (!Utilities.isNullOrEmpty(eventFeatureCode)) {
                            openInternalFeature(eventFeatureCode)
                        }
                    }

                    "INTERNAL_URL" -> {
                        if (!Utilities.isNullOrEmpty(eventDestination)) {
                            val intent = Intent(requireContext(), FeedUpdateActivity::class.java)
                            intent.putExtra(Constants.TITLE, eventTitle)
                            intent.putExtra(Constants.WEB_URL, eventDestination)
                            startActivity(intent)
                        }
                    }

                    "EXTERNAL_URL" -> {
                        if (!Utilities.isNullOrEmpty(eventDestination)) {
                            Utilities.redirectToChrome(eventDestination, requireContext())
                        }
                    }
                }
            }
        }

        binding.btnScan.setOnClickListener {
            val data = HashMap<String, Any>()
            data[CleverTapConstants.FROM] = CleverTapConstants.SCAN
            CleverTapHelper.pushEventWithProperties(
                requireContext(), CleverTapConstants.SCAN_YOUR_VITALS, data
            )
//            navigateToWellfie(NavigationConstants.BMI_VITALS_SCREEN, false)
        }

        binding.btnRescan.setOnClickListener {
            val data = HashMap<String, Any>()
            data[CleverTapConstants.FROM] = CleverTapConstants.RESCAN
            CleverTapHelper.pushEventWithProperties(
                requireContext(), CleverTapConstants.SCAN_YOUR_VITALS, data
            )
//            navigateToWellfie(NavigationConstants.BMI_VITALS_SCREEN, false)
            //navigateToWellfie(NavigationConstants.WELLFIE_RESULT_SCREEN,true)
        }

        /*binding.cardConsultTherapist.setOnClickListener {
            val permissionResult: Boolean = permissionUtil.checkAmahaWebviewPermissions(object : PermissionUtil.AppPermissionListener {
                override fun isPermissionGranted(isGranted: Boolean) {
                    Utilities.printLogError("$isGranted")
                    if (isGranted) {
                        navigateToAmahaWebView("TELE_URL",resources.getString(R.string.CONSULT_WITH_THERAPIST))
                    }
                }
            }, requireContext())
            if (permissionResult) {
                navigateToAmahaWebView("TELE_URL",resources.getString(R.string.CONSULT_WITH_THERAPIST))
            }
        }*/

        /*        binding.layoutHraStart.setOnClickListener {
                    //viewModel.navigateToHraInfo()
                    viewModel.callAddFeatureAccessLogApi(Constants.HRA,"HRA","VivantCore","")
                    openAnotherActivity(destination = NavigationConstants.HRA_INFO)
                }*/

        binding.cardLeadershipExperinces.setOnClickListener {
            val data = HashMap<String, Any>()
            data[CleverTapConstants.EMPLOYEE_ID] = Utilities.getEmployeeID()
            CleverTapHelper.pushEventWithProperties(
                requireContext(), CleverTapConstants.LEADERSHIP_EXPERIENCES, data
            )
            val intent = Intent(requireContext(), LeadershipExperincesActivity::class.java)
            intent.putExtra(Constants.WEB_URL, Constants.LEADERSHIP_EXPERINCES_URL)
            startActivity(intent)
        }

        binding.cardHra.setOnClickListener {
            //viewModel.getHraSummaryDetails()
            goToHRA()
        }

        binding.hraProgressBar.setOnClickListener {
            binding.cardHra.performClick()
        }

        binding.txtHraScore.setOnClickListener {
            binding.cardHra.performClick()
        }

        binding.btnStartMptTest.setOnClickListener {
            launchSaltMpt(CleverTapConstants.START)
        }

        binding.btnMptViewResult.setOnClickListener {
            launchSaltMpt(CleverTapConstants.VIEW_RESULT)
        }

        binding.btnSaltVideos.setOnClickListener {
            CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.SALT_VIDEOS)
            val intent = Intent(requireContext(), FeedUpdateActivity::class.java)
            intent.putExtra(Constants.TITLE, "Expert Advice")
            intent.putExtra(Constants.WEB_URL, Constants.SALT_VIDEOS_URL)
            startActivity(intent)
        }

        binding.imgMptShare.setOnClickListener {
            CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.SALT_MPT_SHARE)
            viewModel.shareAppInviteMpt(Utilities.getUserPreference(PreferenceConstants.MPT_COHORT_TITLE))
        }

        binding.btnGetRewarded.setOnClickListener {
            CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.INVITE_APP)
            viewModel.shareAppInvite()
        }

        binding.imgBlogsArrow.setOnClickListener {
            CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.HEALTH_LIBRARY)
            viewModel.callAddFeatureAccessLogApi(
                Constants.HEALTH_LIBRARY, "Health Library", "VivantCore", ""
            )
            startActivity(Intent(requireContext(), BlogsActivity::class.java))
        }

        binding.hraProgressBar.isClickable = false
        binding.hraProgressBar.setOnTouchListener { _, _ -> true }

        binding.btnViewDetailsNimeya.setOnClickListener {
            launchNimeyaArticles()
        }

        binding.layoutNimeya.setOnClickListener {
            binding.btnViewDetailsNimeya.performClick()
        }

    }

    private fun launchSaltMpt(from: String) {
        val data = HashMap<String, Any>()
        data[CleverTapConstants.FROM] = from
        CleverTapHelper.pushEventWithProperties(requireContext(), CleverTapConstants.SALT_MPT, data)
        startActivity(Intent(requireContext(),SaltWebViewActivity::class.java))
    }

    private fun launchNimeyaArticles() {
        CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.NIMEYA_FINTALK_FINSTAT)
        startActivity(Intent(requireContext(), NimeyaWebViewActivity::class.java))
    }

    private fun goToHRA() {
        CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.HEALTH_RISK_ASSESSMENT)
        viewModel.callAddFeatureAccessLogApi(Constants.HRA, "HRA", "VivantCore", "")
        //viewModel.goToHRA( this@PhysicalWellbeingActivity )
        try {
//            val dob = user.dateOfBirth
//            if (dob.isNullOrEmpty()){
//                toastMessage("HRA is allowed for 18+ members only")
//            }else {
//                if(DateHelper.isDateAbove18Years(dob)) {
            if (viewModel.hraDetails.value != null) {
                val hraSummary = viewModel.hraDetails.value
                val currentHRAHistoryID = hraSummary?.currentHRAHistoryID.toString()
                val wellnessScore = hraSummary?.scorePercentile.toString()
                val hraCutOff = hraSummary?.hraCutOff
                if (!Utilities.isNullOrEmpty(currentHRAHistoryID) && currentHRAHistoryID != "0") {
                    if (hraCutOff.equals("0")) {
                        navigateToHraStart()
                    } else if (!Utilities.isNullOrEmpty(wellnessScore)) {
                        navigateToHraSummary()
                    } else {
                        navigateToHraStart()
                    }
                } else {
                    navigateToHraStart()
                }
            } else {
                navigateToHraStart()
            }
//                }else{
//                    toastMessage("HRA is allowed for 18+ members only")
//                }
//            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openInternalFeature(featureCode: String) {
        val homeActivity = (activity as HomeMainActivity)
        when (featureCode) {
            Constants.FEATURE_CODE_AKTIVO_DASHBOARD, Constants.FEATURE_CODE_AKTIVO_BADGES,
            Constants.FEATURE_CODE_AKTIVO_SCORE,Constants.FEATURE_CODE_AKTIVO_MIND_SCORE,
            Constants.FEATURE_CODE_AKTIVO_CHALLENGES -> {
                navigateToAktivoFromCampaign(featureCode)
            }

            Constants.FEATURE_CODE_HRA -> {
                goToHRA()
            }
            Constants.FEATURE_CODE_MPT -> {
                launchSaltMpt(CleverTapConstants.START)
                startActivity(Intent(requireContext(), SaltWebViewActivity::class.java))
            }
            Constants.FEATURE_CODE_NIMEYA -> {
                launchNimeyaArticles()
                startActivity(Intent(requireContext(), NimeyaWebViewActivity::class.java))
            }

            Constants.FEATURE_CODE_SMITFIT -> {
                scrollToView(binding.cardSaltMpt)
                highlightFeature(binding.rvSmitFit,resources.getString(R.string.TUTORIAL_SMIT_FIT),resources.getString(R.string.TUTORIAL_SMIT_FIT_DESC),90)
            }
            Constants.FEATURE_CODE_LIVE_SESSIONS -> {
                //scrollToView(binding.layoutInvestometers)
                //highlightFeature(binding.lblLiveSessions,resources.getString(R.string.TUTORIAL_LIVE_SESSIONS),resources.getString(R.string.TUTORIAL_LIVE_SESSIONS_DESC),70)
            }
            Constants.FEATURE_CODE_INVESTOMETER -> {
                scrollToView(binding.rvFinancialCalculators)
                highlightFeature(binding.lblInvestometers,resources.getString(R.string.TUTORIAL_INVESTOMETERS),resources.getString(R.string.TUTORIAL_INVESTOMETERS_DESC),55)
            }
            Constants.FEATURE_CODE_NVEST -> {
                scrollToView(binding.layoutWellfie)
                highlightFeature(binding.rvFinancialCalculators,resources.getString(R.string.TUTORIAL_NVEST_CALCULATORS),resources.getString(R.string.TUTORIAL_NVEST_CALCULATORS_DESC),105)
            }

            Constants.FEATURE_CODE_MEDITATION -> {
                launchSmitFit(Constants.MEDITATION)
            }
            Constants.FEATURE_CODE_YOGA -> {
                launchSmitFit(Constants.YOGA)
            }
            Constants.FEATURE_CODE_EXERCISE -> {
                launchSmitFit(Constants.EXERCISE)
            }

            Constants.FEATURE_CODE_RISKO_METER -> {
                launchInvestometer(Constants.NIMEYA_RISKO_METER)
            }
            Constants.FEATURE_CODE_PROTECTO_METER -> {
                launchInvestometer(Constants.NIMEYA_PROTECTO_METER)
            }
            Constants.FEATURE_CODE_RETIRO_METER -> {
                launchInvestometer(Constants.NIMEYA_RETIRO_METER)
            }

            Constants.FEATURE_CODE_NVEST_EDUCATION -> {
                launchNvest(Constants.EDUCATION)
            }
            Constants.FEATURE_CODE_NVEST_MARRIAGE -> {
                launchNvest(Constants.MARRIAGE)
            }
            Constants.FEATURE_CODE_NVEST_WEALTH -> {
                launchNvest(Constants.WEALTH)
            }
            Constants.FEATURE_CODE_NVEST_HOME -> {
                launchNvest(Constants.HOUSE)
            }
            Constants.FEATURE_CODE_NVEST_RETIREMENT -> {
                launchNvest(Constants.RETIREMENT)
            }
            Constants.FEATURE_CODE_NVEST_HUMAN_VALUE -> {
                launchNvest(Constants.HUMAN_VALUE)
            }

            Constants.FEATURE_CODE_HYDRATION_TRACKER -> {
                CleverTapHelper.pushEvent(requireContext(),CleverTapConstants.HYDRATION_TRACKER)
                startActivity(Intent(requireContext(), WaterTrackerActivity::class.java))
            }
            Constants.FEATURE_CODE_TRACK_VITAL_PARAMETERS -> {
                CleverTapHelper.pushEvent(requireContext(),CleverTapConstants.TRACK_VITAL_PARAMETERS)
                startActivity(Intent(requireContext(),ParameterHomeActivity::class.java))
            }
            Constants.FEATURE_CODE_MEDICATION_TRACKER -> {
                CleverTapHelper.pushEvent(requireContext(),CleverTapConstants.MEDITATION_TRACKER)
                startActivity(Intent(requireContext(),MedicationHomeActivity::class.java))
            }
            Constants.FEATURE_CODE_MY_HEALTH_RECORDS -> {
                val permissionResult = permissionUtil.checkStoragePermission(permissionListener, requireContext())
                if (permissionResult) {
                    CleverTapHelper.pushEvent(requireContext(),CleverTapConstants.HEALTH_RECORDS)
                    startActivity(Intent(requireContext(), HealthRecordsActivity::class.java))
                }
            }
            Constants.FEATURE_CODE_TOOLS_CALCULATORS -> {
                CleverTapHelper.pushEvent(requireContext(),CleverTapConstants.TOOLS_CALCULATORS)
                val intent = Intent(requireContext(), ToolsCalculatorsHomeActivity::class.java)
                intent.putExtra(Constants.FROM, Constants.HOME)
                intent.putExtra(Constants.TO, "DASH")
                startActivity(intent)
            }
            Constants.FEATURE_CODE_YOUR_POLICY -> {
                Utilities.preferenceUtils.storePreference(Constants.POLICY_VIEW,Constants.FEATURE_CODE_YOUR_POLICY)
                homeActivity.binding.bottomNavigation.selectedItemId = R.id.page_5
            }
            Constants.FEATURE_CODE_YOUR_POLICY_DOWNLOADS -> {
                Utilities.preferenceUtils.storePreference(Constants.POLICY_VIEW,Constants.FEATURE_CODE_YOUR_POLICY_DOWNLOADS)
                homeActivity.binding.bottomNavigation.selectedItemId = R.id.page_5
            }

            Constants.FEATURE_CODE_CALCULATOR_HEART_AGE -> {
                launchToolsCalculator(Constants.CODE_HEART_AGE_CALCULATOR)
            }
            Constants.FEATURE_CODE_CALCULATOR_DIABETES -> {
                launchToolsCalculator(Constants.CODE_DIABETES_CALCULATOR)
            }
            Constants.FEATURE_CODE_CALCULATOR_HYPERTENSION -> {
                launchToolsCalculator(Constants.CODE_HYPERTENSION_CALCULATOR)
            }
            Constants.FEATURE_CODE_CALCULATOR_STRESS_ANXIETY -> {
                launchToolsCalculator(Constants.CODE_STRESS_ANXIETY_CALCULATOR)
            }
            Constants.FEATURE_CODE_CALCULATOR_SMART_PHONE_ADDICTION -> {
                launchToolsCalculator(Constants.CODE_SMART_PHONE_ADDICTION_CALCULATOR)
            }
            Constants.FEATURE_CODE_CALCULATOR_DUE_DATE -> {
                launchToolsCalculator(Constants.CODE_DUE_DATE_CALCULATOR)
            }

            Constants.FEATURE_CODE_REFER_FRIEND -> {
                CleverTapHelper.pushEvent(requireContext(),CleverTapConstants.INVITE_APP)
                viewModel.shareAppInvite()
            }
            Constants.FEATURE_CODE_HOW_IT_WORKS -> {
                selectToPlayTutorial()
            }
            Constants.FEATURE_CODE_SETTINGS -> {
                CleverTapHelper.pushEvent(requireContext(),CleverTapConstants.SETTINGS)
                openAnotherActivity(destination = NavigationConstants.SETTINGS)
            }
            Constants.FEATURE_CODE_LANGUAGE -> {
                openAnotherActivity(destination = NavigationConstants.LANGUAGE_SCREEN) {
                    putString(Constants.FROM, "")
                }
            }
            Constants.FEATURE_CODE_PRIVACY_POLICY -> {
                openAnotherActivity(destination = NavigationConstants.TERMS_CONDITION) {
                    putString(Constants.FROM, Constants.PRIVACY_POLICY)
                }
            }
            Constants.FEATURE_CODE_TERMS_CONDITIONS -> {
                openAnotherActivity(destination = NavigationConstants.TERMS_CONDITION) {
                    putString(Constants.FROM, Constants.TERMS_CONDITIONS)
                }
            }
            /*"AKTIVO_FITNESS_TRACKER" -> {
            (activity as HomeMainActivity).replaceFragment(AktivoScreenFragment(), frameId = R.id.main_container)
            }*/
        }
    }

    private fun openInternalFeature1(eventTitle: String) {
        when (eventTitle) {
            "INVESTOMETER" -> {
                scrollToView(binding.cardNimeya)
                highlightInvestometer()
            }

            "RISKO_METER" -> {
                viewModel.showProgress()
                nimeyaViewModel.callGetRiskoMeterHistoryApi(this)
            }

            "PROTECTO_METER" -> {
                viewModel.showProgress()
                nimeyaViewModel.callGetProtectoMeterHistoryApi(this)
            }

            "RETIRO_METER" -> {
                viewModel.showProgress()
                nimeyaViewModel.callGetRetiroMeterHistoryApi(this)
            }
        }
    }

    private fun highlightInvestometer() {
        val tapTarget = TapTargetSequence(requireActivity())
        tapTarget.targets(
            TapTarget.forView(
                binding.lblInvestometers,
                resources.getString(R.string.TUTORIAL_INVESTOMETERS),
                resources.getString(R.string.TUTORIAL_INVESTOMETERS_DESC)
            ).targetRadius(55).setConfiguration(requireContext())
        )?.listener(object : TapTargetSequence.Listener {
            override fun onSequenceFinish() {
                tapTarget.cancel()
            }

            override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) {}
            override fun onSequenceCanceled(lastTarget: TapTarget?) {}
        })?.start()
    }

    private fun highlightFeature(view:View,title:String,desc:String,radius:Int) {
        val tapTarget = TapTargetSequence(requireActivity())
        tapTarget.targets(TapTarget.forView(view,title,desc)
            .targetRadius(radius).setConfiguration(requireContext())
        )?.listener(object : TapTargetSequence.Listener {
            override fun onSequenceFinish() {
                tapTarget.cancel()
            }
            override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) { }
            override fun onSequenceCanceled(lastTarget: TapTarget?) { }
        })?.start()
    }

    private fun launchToolsCalculator(code:String) {
        UserInfoModel.getInstance()!!.isDataLoaded = false
        val intent = Intent(requireContext(), ToolsCalculatorsHomeActivity::class.java)
        intent.putExtra(Constants.FROM, Constants.HOME)
        intent.putExtra(Constants.TO, code)
        startActivity(intent)
    }

    private fun navigateToHraStart() {
        startActivity(Intent(requireContext(), HraInfoActivity::class.java))
    }

    private fun navigateToHraSummary() {
        val data = HashMap<String, Any>()
        data[CleverTapConstants.FROM] = CleverTapConstants.DASHBOARD
        CleverTapHelper.pushEventWithProperties(
            requireContext(), CleverTapConstants.HRA_SUMMARY_SCREEN, data
        )
        startActivity(Intent(requireContext(), HraSummaryActivity::class.java))
    }

    private fun navigateToAktivoFromCampaign(feature: String) {
        when(feature) {
            Constants.FEATURE_CODE_AKTIVO_DASHBOARD -> navigateToAktivo(Constants.AKTIVO_DASHBOARD_CODE)
            Constants.FEATURE_CODE_AKTIVO_BADGES -> navigateToAktivo(Constants.AKTIVO_BADGES_CODE)
            Constants.FEATURE_CODE_AKTIVO_SCORE -> navigateToAktivo(Constants.AKTIVO_SCORE_CODE)
            Constants.FEATURE_CODE_AKTIVO_MIND_SCORE -> navigateToAktivo(Constants.AKTIVO_MIND_SCORE_CODE)
            Constants.FEATURE_CODE_AKTIVO_CHALLENGES -> navigateToAktivo(Constants.AKTIVO_CHALLENGES_CODE)
        }
    }

    private fun navigateToAktivo(screenCode: String) {
        when(screenCode) {
            Constants.AKTIVO_DASHBOARD_CODE -> CleverTapHelper.pushEvent(requireContext(),CleverTapConstants.AKTIVO_DASHBOARD)
            Constants.AKTIVO_CHALLENGES_CODE -> CleverTapHelper.pushEvent(requireContext(),CleverTapConstants.AKTIVO_CHALLENGES)
            Constants.AKTIVO_BADGES_CODE -> {
                val aktivoData = HashMap<String, Any>()
                aktivoData[CleverTapConstants.BADGE] = aktivoBadge
                CleverTapHelper.pushEventWithProperties(requireContext(),CleverTapConstants.AKTIVO_BADGES,aktivoData)
            }
            Constants.AKTIVO_SCORE_CODE -> {
                val aktivoData = HashMap<String, Any>()
                aktivoData[CleverTapConstants.SCORE] = aktivoScore
                CleverTapHelper.pushEventWithProperties(requireContext(),CleverTapConstants.AKTIVO_SCORE,aktivoData)
            }
            Constants.AKTIVO_MIND_SCORE_CODE -> {
                val aktivoData = HashMap<String, Any>()
                aktivoData[CleverTapConstants.SCORE] = aktivoMindScore
                CleverTapHelper.pushEventWithProperties(requireContext(),CleverTapConstants.MIND_SCORE,aktivoData)
            }
        }
        val intent = Intent(requireContext(), AktivoPermissionsActivity::class.java)
        intent.putExtra(Constants.CODE, screenCode)
        startActivity(intent)
    }

    @SuppressLint("SetTextI18n")
    private fun setObserver() {

        viewModel.medicalProfileSummary.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {
                if (it.data!!.MedicalProfileSummary != null) {
                    viewModel.setupHRAWidgetData(it.data.MedicalProfileSummary)
                    viewModel.setHraSummaryDetails(it.data.MedicalProfileSummary!!)
                    if (!Utilities.isNullOrEmptyOrZero(it.data.MedicalProfileSummary!!.bmi.toString())) {
                        val hraSummary = it.data.MedicalProfileSummary!!
                        bmi = hraSummary.bmi.toString()
                        val bmiVitalsList: MutableList<TrackParameterMaster.History> = ArrayList()
                        bmiVitalsList.add(
                            TrackParameterMaster.History(
                                parameterCode = "HEIGHT",
                                value = hraSummary.height,
                                profileCode = "BMI",
                                recordDate = ""
                            )
                        )
                        bmiVitalsList.add(
                            TrackParameterMaster.History(
                                parameterCode = "WEIGHT",
                                value = hraSummary.weight,
                                profileCode = "BMI",
                                recordDate = ""
                            )
                        )
                        bmiVitalsList.add(
                            TrackParameterMaster.History(
                                parameterCode = "BMI",
                                value = hraSummary.bmi,
                                profileCode = "BMI",
                                recordDate = ""
                            )
                        )
                        wellfieSingleton.setBmiVitalsList(bmiVitalsList)
                    }
                }
            }
        }

        viewModel.hraObservationLiveData.observe(viewLifecycleOwner) {
            HRAData.data = it
            setupHRAWidgetUI(it)
        }

        viewModel.wellfieGetVitals.observe(viewLifecycleOwner) { getData ->
            if (getData.status == Resource.Status.SUCCESS) {

            }
        }

        viewModel.liveSessions.observe(viewLifecycleOwner) {
            if (it != null) {
                liveSessionAdapter!!.updateList(it)
                stopLiveSessionsShimmer()
                if (it.isNotEmpty()) {
                    binding.layoutLiveSessions.visibility = View.VISIBLE
                    /*val campaignFeatureName = Utilities.preferenceUtils.getPreference(Constants.CAMPAIGN_FEATURE_NAME)
                    if( !Utilities.isNullOrEmpty(campaignFeatureName)
                        && campaignFeatureName.equals(Constants.FEATURE_CODE_LIVE_SESSIONS,ignoreCase = true) ) {
                        Utilities.clearCampaignFeatureDetails()
                        redirectToLiveSession(it.toMutableList())
                    }*/
                } else {
                    binding.layoutLiveSessions.visibility = View.GONE
                    Utilities.clearCampaignFeatureDetails()
                }
            }
        }

        viewModel.healthBlogList.observe(viewLifecycleOwner) {
            if (it != null) {
                blogAdapter!!.updateData(it)
            }
        }

        /*        viewModel.getStepsGoal.observe(viewLifecycleOwner) {
                    if (it.status == Resource.Status.SUCCESS) {
                        if ( it.data!!.latestGoal != null && it.data!!.latestGoal.goal != null ) {
                            stepGoal = it.data!!.latestGoal.goal
                            if( stepGoal == 0 ) {
                                stepGoal = Constants.DEFAULT_STEP_GOAL
                            }
                            binding.txtStepGoal.text = " / $stepGoal"
                            binding.txtCaloriesGoal.text = " / ${CalculateParameters.getCaloriesFromSteps(stepGoal)} ${resources.getString(R.string.KCAL)}"
                            proceedWithFitnessData()
                        }
                    }
                }*/


        viewModel.aktivoCreateUser.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {
                if (!Utilities.isNullOrEmpty(it.data!!.resultData.member.id)) {
                    viewModel.storeUserPreference(
                        PreferenceConstants.AKTIVO_MEMBER_ID, it.data.resultData.member.id!!
                    )
                    viewModel.callAktivoGetUserTokenApi(it.data.resultData.member.id)
                }
            }
            if (it.status == Resource.Status.ERROR) {
                stopAllAktivoShimmers()
            }
        }
        viewModel.aktivoGetUserToken.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {
                if (!Utilities.isNullOrEmpty(it.data!!.accessToken) && !Utilities.isNullOrEmpty(it.data.refreshToken)) {
                    viewModel.storeUserPreference(
                        PreferenceConstants.AKTIVO_ACCESS_TOKEN, it.data.accessToken!!
                    )
                    viewModel.storeUserPreference(
                        PreferenceConstants.AKTIVO_REFRESH_TOKEN, it.data.refreshToken!!
                    )
//                    authenticateUserUsingToken()
                }
            }
            if (it.status == Resource.Status.ERROR) {
                stopAllAktivoShimmers()
            }
        }

        viewModel.blogList.observe(viewLifecycleOwner) { }
        viewModel.addFeatureAccessLog.observe(viewLifecycleOwner) {}
        viewModel.eventsBanner.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {
                if (it.data!!.eventsBannerDetailList.isNotEmpty()) {
                    val eventsBannerDetail = it.data.eventsBannerDetailList[0]
                    eventTitle = eventsBannerDetail.title
                    eventFeatureCode = eventsBannerDetail.featureCode
                    eventType = eventsBannerDetail.redirectType
                    //eventFeatureCode = "YOUR_POLICY"
                    //eventType ="INTERNAL_FEATURE"
                    eventDesc = eventsBannerDetail.description
                    eventDestination = eventsBannerDetail.redirectURL
                    binding.layoutFeedUpdate.visibility = View.VISIBLE
                    binding.txtFeedUpdate.text = eventDesc
                } else {
                    binding.layoutFeedUpdate.visibility = View.GONE
                }
            }
        }
        nimeyaViewModel.riskoMeterHistory.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {
                viewModel.hideProgress()
            }
            if (it.status == Resource.Status.ERROR) {
                viewModel.hideProgress()
            }
        }
        nimeyaViewModel.protectoMeterHistory.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {
                viewModel.hideProgress()
            }
            if (it.status == Resource.Status.ERROR) {
                viewModel.hideProgress()
            }
        }
        nimeyaViewModel.retiroMeterHistory.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {
                viewModel.hideProgress()
            }
            if (it.status == Resource.Status.ERROR) {
                viewModel.hideProgress()
            }
        }
        viewModel.policyProducts.observe(viewLifecycleOwner) { }
    }

/*    private fun redirectToLiveSession(list:MutableList<LiveSessionModel>) {
        try {
            //currentTime = dateFormat.parse("07:05:00")!!
            //val currentDate = "2024-07-13"
            val currentDate = DateHelper.currentDateAsStringyyyyMMdd
            Utilities.printData("LiveSessions",list,true)
            differenceInMinutes = 0
            var liveSessionAvailable = false
            var liveSession = LiveSessionModel()
            for ( i in list ) {
                slotTime = dateFormat.parse(i.time)!!
                differenceInMinutes = ((currentTime!!.time - slotTime.time) / (60 * 1000)).toInt()
                Utilities.printLogError("differenceInMinutes--->$differenceInMinutes")
                if ( currentDate == i.date.split("T")[0]
                    && differenceInMinutes >= -5 ) {
                    Utilities.printData("MatchedLiveSessions",i,true)
                    liveSessionAvailable = true
                    liveSession = i
                    break
                }
            }
            if ( liveSessionAvailable && !Utilities.isNullOrEmpty(liveSession.link) ) {
                onLiveSessionClick(liveSession)
            } else {
                scrollToView(binding.layoutInvestometers)
                highlightFeature(binding.lblLiveSessions,resources.getString(R.string.TUTORIAL_LIVE_SESSIONS),resources.getString(R.string.TUTORIAL_LIVE_SESSIONS_DESC),70)
            }
        } catch ( e:Exception ) {
            e.printStackTrace()
        }
    }*/

    fun loadWellfieData(vitalsList:List<WellfieGetVitalsModel.Report>) {
        try {
            if (!vitalsList.isNullOrEmpty()) {
                val abc = vitalsList.filter { it.paramCode != null }
                wellfieSingleton.dateTime = abc[0].recordDateTime!!.split("T").toTypedArray()[0]
                var parametersList: MutableList<WellfieResultModel> = mutableListOf()
                for (i in abc) {
                    if (!Utilities.isNullOrEmpty(i.paramCode!!)) {
                        when (i.paramCode) {
                            "BP_SYS" -> {
                                bpSystolic = "${i.value}"
                                bpStatus = "${i.observation}"
                                bpColor = "${i.colorCode}"
                            }

                            "BP_DIA" -> {
                                bpDiastolic = "${i.value}"
                                bpStatus = "${i.observation}"
                            }

                            else -> {
                                if (i.observation != null) {
                                    observation = i.observation.toString()
                                }
                                if (i.colorCode != null) {
                                    color = i.colorCode.toString()
                                }
                                if (!parametersList.any {
                                        it.paramCode.equals(i.paramCode, ignoreCase = true)
                                    }) {
                                    parametersList.add(
                                        WellfieResultModel(
                                            Utilities.getWellfieParameterIdByCode(i.paramCode),
                                            i.paramCode,
                                            i.name!!,
                                            i.value!!,
                                            observation,
                                            color))
                                }
                                //parametersList.add(WellfieResultModel(Utilities.getWellfieParameterIdByCode(i.paramCode!!),i.paramCode!!,i.name!!,i.value!!,observation))
                            }
                        }
                    }
                }
                parametersList.add(
                    WellfieResultModel(
                        1,
                        "BP",
                        "Blood Pressure",
                        "$bpSystolic / $bpDiastolic",
                        bpStatus,
                        bpColor))
                parametersList = parametersList.reversed().toMutableList()

                if (!parametersList.any { it.paramCode.equals("BMI", ignoreCase = true) }) {
                    if (!Utilities.isNullOrEmptyOrZero(bmi)) {
                        parametersList.add(
                            WellfieResultModel(
                                6,
                                "BMI",
                                "BMI",
                                bmi,
                                ""))
                    }
                }

                wellfieSingleton.setWellfieResultList(parametersList)
                //Utilities.printData("WellfieResultList",parametersList.toMutableList())
                wellfieDashboardAdapter!!.updateData(parametersList)
                binding.txtLastUpdated.text = DateHelper.convertDateSourceToDestination(wellfieSingleton.dateTime, DateHelper.SERVER_DATE_YYYYMMDD, DateHelper.DATEFORMAT_DDMMMYYYY_NEW)
                binding.txtOxygenLevel.text = parametersList.find { it.paramCode == "BLOOD_OXYGEN" }!!.paramValue + " %"
                binding.txtStressLevel.text = Utilities.convertStringToPascalCase(parametersList.find { it.paramCode == "STRESS_INDEX" }!!.paramObs!!)
                binding.layoutScanVitalsPrevious.visibility = View.VISIBLE
                binding.layoutScanVitals.visibility = View.GONE
                WellfieSingleton.getInstance()!!.logCleverTapScanVitalsInfo(requireContext(),abc.toMutableList(),wellfieSingleton.dateTime)
                //checkToPlayTutorial()
            } else {
                //wellfieSingleton.clearData()
                wellfieSingleton.setWellfieResultList(mutableListOf())
                binding.layoutScanVitalsPrevious.visibility = View.GONE
                binding.layoutScanVitals.visibility = View.VISIBLE
                //checkToPlayTutorial()
            }
            stopWellfieShimmer()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupHRAWidgetUI(model: HRAObservationModel) {
        if (model.obaservation.contains(resources.getString(R.string.TAKE), true)) {
            binding.layoutHraGiven.visibility = View.GONE
            binding.layoutHraStart.visibility = View.VISIBLE
            //playTutorialHraNotGiven()
        } else {
            binding.layoutHraGiven.visibility = View.VISIBLE
            binding.layoutHraStart.visibility = View.GONE
            //binding.txtHraObservation.text = model.obaservation
            binding.txtHraScore.text = "${model.hraScore} %"
            binding.hraProgressBar.setValueAnimated(
                model.hraScore.toFloat(), Constants.ANIMATION_DURATION.toLong()
            )
            //binding.hraProgressBar.setBackgroundColor(resources.getColor(model.color))
            binding.hraProgressBar.setBarColor(
                ContextCompat.getColor(
                    requireContext(), model.color
                )
            )
            //playTutorialHraGiven()
        }
    }

    /*private fun navigateToWellfie(destination: String, isData: Boolean) {
        val permissionResult: Boolean = permissionUtil.checkCameraPermissionWellfie(object :
            PermissionUtil.AppPermissionListener {
            override fun isPermissionGranted(isGranted: Boolean) {
                Utilities.printLogError("$isGranted")
                if (isGranted) {
                    viewModel.callAddFeatureAccessLogApi("WELLFIE", "Wellfie", "Wellfie", "")
                    //viewModel.callWellfieGetVitalsApi()
                    openAnotherActivity(destination = destination) {
                        putString(Constants.FROM, Constants.DASHBOARD)
                        putBoolean(Constants.IS_DATA, isData)
                    }
                }
            }
        }, requireContext())
        if (permissionResult) {
            viewModel.callAddFeatureAccessLogApi("WELLFIE", "Wellfie", "Wellfie", "")
            //viewModel.callWellfieGetVitalsApi()
            openAnotherActivity(destination = destination) {
                putString(Constants.FROM, Constants.DASHBOARD)
                putBoolean(Constants.IS_DATA, isData)
            }
        }
    }*/

    private fun playTutorial() {
        scrollToTop()
        val tapTarget = TapTargetSequence(requireActivity())
        val tutorialList = getTutorialList()
        val list: MutableList<TapTarget> = mutableListOf()
        for (i in tutorialList) {
            list.add(TapTarget.forView(i.view, i.title, i.desc).targetRadius(i.size)
                    .setConfiguration(requireContext()))
        }
        tapTarget.targets(list)?.listener(object : TapTargetSequence.Listener {
            // This listener will tell us when interesting(tm) events happen in regards
            // to the sequence
            override fun onSequenceFinish() {
                //requestGoogleFit()
                tapTarget.cancel()
                scrollToTop()
                tapCount = 0
            }

            override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) {
                tapCount += 1
                Utilities.printLogError("tapCount--->$tapCount")
                when (tapCount) {
                    1 -> scrollToView(binding.layoutInvestometers)
                    4 -> scrollToView(binding.layoutFinancialCalculators)
                }

                /* if (binding.layoutChallenges.visibility == View.VISIBLE) {
                     when (tapCount) {
                         3 -> scrollToView(binding.layoutLiveSessions)
                         6 -> scrollToView(binding.rvFinancialCalculators)
                         10 -> scrollToView(binding.layoutWellfie)
                     }
                 } else {
                     when (tapCount) {
                         2 -> scrollToView(binding.layoutLiveSessions)
                         5 -> scrollToView(binding.rvFinancialCalculators)
                         9 -> scrollToView(binding.layoutWellfie)
                     }
                 }*/
            }

            override fun onSequenceCanceled(lastTarget: TapTarget?) {
                Utilities.printLogError("Sequence Cancelled")
                tapCount = 0
            }
        })?.start()
    }

    private fun playTutorialWithLeadershipExperinces() {
        scrollToTop()
        val tapTarget = TapTargetSequence(requireActivity())
        val tutorialList = getTutorialWithLeadershipExperincesList()
        val list: MutableList<TapTarget> = mutableListOf()
        for (i in tutorialList) {
            list.add(TapTarget.forView(i.view, i.title, i.desc).targetRadius(i.size)
                .setConfiguration(requireContext()))
        }
        tapTarget.targets(list)?.listener(object : TapTargetSequence.Listener {
            // This listener will tell us when interesting(tm) events happen in regards
            // to the sequence
            override fun onSequenceFinish() {
                //requestGoogleFit()
                tapTarget.cancel()
                scrollToTop()
                tapCount = 0
            }

            override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) {
                tapCount += 1
                Utilities.printLogError("tapCount--->$tapCount")
                when (tapCount) {
                    3 -> scrollToView(binding.layoutFinancialCalculators)
                    //5 -> scrollToView(binding.layoutFinancialCalculators)
                }

                /*if (binding.layoutChallenges.visibility == View.VISIBLE) {
                    when (tapCount) {
                        3 -> scrollToView(binding.layoutLiveSessions)
                        7 -> scrollToView(binding.rvFinancialCalculators)
                        11 -> scrollToView(binding.layoutWellfie)
                    }
                } else {
                    when (tapCount) {
                        2 -> scrollToView(binding.layoutLiveSessions)
                        6 -> scrollToView(binding.rvFinancialCalculators)
                        10 -> scrollToView(binding.layoutWellfie)
                    }
                }*/
            }

            override fun onSequenceCanceled(lastTarget: TapTarget?) {
                Utilities.printLogError("Sequence Cancelled")
                tapCount = 0
            }
        })?.start()
    }

    fun setUpSlidingPolicyBannerViewPager(campaignList: MutableList<PolicyProducts>) {
        try {
            //val campaignDetailsList = campaignList.filter { it.campaignID == "1" }.toMutableList()
            //campaignList.sortBy { it.displayOrder }
            //Utilities.printData("PolicyProductsDashboard",campaignList,true)
            slidingDotsCount = campaignList.size
            slidingImageDots = arrayOfNulls(slidingDotsCount)
            val landingImagesAdapter = SlidingSudBannerDashboardAdapter(requireActivity(), slidingDotsCount, campaignList, this)

            binding.slidingViewPager.apply {
                adapter = landingImagesAdapter
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        for (i in 0 until slidingDotsCount) {
                            slidingImageDots[i]?.setImageDrawable(ContextCompat.getDrawable(binding.slidingViewPager.context, R.drawable.dot_non_active))
                        }
                        slidingImageDots[position]?.setImageDrawable(ContextCompat.getDrawable(binding.slidingViewPager.context, R.drawable.dot_active))
                    }
                })
            }

            if (slidingDotsCount > 1) {
                binding.sliderDots.visibility = View.VISIBLE
                for (i in 0 until slidingDotsCount) {
                    slidingImageDots[i] = ImageView(binding.slidingViewPager.context)
                    slidingImageDots[i]?.setImageDrawable(ContextCompat.getDrawable(binding.slidingViewPager.context, R.drawable.dot_non_active))
                    val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    params.setMargins(8, 0, 8, 0)
                    binding.sliderDots.addView(slidingImageDots[i], params)
                }

                slidingImageDots[0]?.setImageDrawable(ContextCompat.getDrawable(binding.slidingViewPager.context, R.drawable.dot_active))

                val handler = Handler(Looper.getMainLooper())
                val update = Runnable {
                    if (currentPage == slidingDotsCount) {
                        currentPage = 0
                    }
                    binding.slidingViewPager.setCurrentItem(currentPage++, true)
                }
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        handler.post(update)
                    }
                }, 3000, 3000)
            } else {
                binding.sliderDots.visibility = View.INVISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    fun setUpBannerData(campaignList: MutableList<PolicyProducts>) {
        Utilities.printData("PolicyProductsDashboard", campaignList)
        if (!Utilities.preferenceUtils.getBooleanPreference(Constants.BANNER_AD, false)) {
            if (campaignList.any { it.popUpEnabled }) {
                bannerDialog = showBannerDialog(campaignList) {
                    when (it) {
                        Constants.CLOSE_DIALOG -> {
                            Utilities.preferenceUtils.storeBooleanPreference(
                                Constants.BANNER_AD, true
                            )
                            updateBannerUI()
                        }
                    }
                }
            } else {
                updateBannerUI()
            }
        } else {
            updateBannerUI()
        }
    }

    private fun updateBannerUI() {
        //binding.progressBar.animateProgress(100, 5000)
        //binding.progressBar.setProgress(100, true)
        checkToPlayTutorial()
    }

    /*private fun getAktivoScore() {
        if (Utilities.isNullOrEmpty(viewModel.getUserPreference(PreferenceConstants.AKTIVO_MEMBER_ID)) && Utilities.isNullOrEmpty(
                viewModel.getUserPreference(PreferenceConstants.AKTIVO_ACCESS_TOKEN)
            ) && Utilities.isNullOrEmpty(viewModel.getUserPreference(PreferenceConstants.AKTIVO_REFRESH_TOKEN))
        ) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                // Get new FCM registration token
                val token = task.result!!
                Utilities.printLogError("\nDeviceToken--->$token")
                viewModel.callAktivoCreateUserApi(token)
            }
        } else {
            authenticateUserUsingToken()
        }
    }

      private fun authenticateUserUsingToken() {
         val userId = viewModel.getUserPreference(PreferenceConstants.AKTIVO_MEMBER_ID)
         val token = viewModel.getUserPreference(PreferenceConstants.AKTIVO_ACCESS_TOKEN)
         val refreshToken = viewModel.getUserPreference(PreferenceConstants.AKTIVO_REFRESH_TOKEN)
         aktivoManager!!.setClientId(Constants.strAktivoClientId)
         aktivoManager!!.setUserTokens(token, refreshToken)
         authenticateUser(userId)
     }

     private fun authenticateUser(userId: String) {
         try {
             aktivoManager!!.authenticateUser(User(userId), requireActivity())
                 .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                 .subscribe(object : CompletableObserver {
                     override fun onSubscribe(d: Disposable) {}
                     override fun onComplete() {
                         Utilities.printLogError("User Authenticated")
                         checkFitnessPermissionsAndProceed()
                     }

                     override fun onError(e: Throwable) {
                         stopAllAktivoShimmers()
                         e.printStackTrace()
                     }
                 })
         } catch (e: Exception) {
             e.printStackTrace()
         }
     }

     private fun checkFitnessPermissionsAndProceed() {
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
             if (fitnessDataManager!!.aktivoAuthPermissionsApproved() && permissionUtil.isActivityRecognitionPermission(
                     binding.txtUserName.context
                 )
             ) {
                 syncFitnessData()
             } else {
                 showAktivoPermissionsDialog()
             }
         } else {
             if (fitnessDataManager!!.aktivoAuthPermissionsApproved()) {
                 syncFitnessData()
             } else {
                 showAktivoPermissionsDialog()
             }
         }
     }

     private fun showAktivoPermissionsDialog() {
        /* if (this.isVisible) {
             val dialogData = DefaultNotificationDialog.DialogData()
             dialogData.title = resources.getString(R.string.PERMISSIONS_REQUIRED)
             //dialogData.imgResource = R.drawable.app_logo_white
             dialogData.message =
                 "<a>" + "${resources.getString(R.string.NEED_HEART_RATE_PERMISSION)} <br/><br/> ${
                     resources.getString(R.string.NEED_SLEEP_PERMISSION)
                 } <br/><br/> ${resources.getString(R.string.NEED_PHYSICAL_PERMISSION)}" + "</a>"
             dialogData.showLeftButton = false
             dialogData.btnRightName = resources.getString(R.string.ALLOW_PERMISSIONS)
             dialogData.showDismiss = false
             val defaultNotificationDialog =
                 DefaultNotificationDialog(
                     (activity as HomeMainActivity),
                     object : DefaultNotificationDialog.OnDialogValueListener {
                         override fun onDialogClickListener(
                             isButtonLeft: Boolean,
                             isButtonRight: Boolean
                         ) {
                             if (isButtonRight) {
                                 checkAllGoogleFitPermission()
                             }
                         }
                     },
                     dialogData
                 )
             defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
             defaultNotificationDialog.show()
         }*/
     }

     private fun syncFitnessData() {
         try {
             //viewModel.showProgress()
             compositeDisposable!!.add(
                 aktivoManager!!.syncFitnessData().subscribeOn(Schedulers.io())
                     .observeOn(AndroidSchedulers.mainThread())
                     .subscribeWith(object : DisposableCompletableObserver() {
                         override fun onComplete() {
                             //viewModel.hideProgress()
                             Utilities.printLogError("Data Synced")
                             getLatestAktivoScore()
                             getMindScore()
                             getAktivoParameters()
                             getBadgeByDate()
                             getOngoingChallenges()
                             //Utilities.toastMessageShort(requireContext(), "Data Synced")
                         }

                         override fun onError(e: Throwable) {
                             //viewModel.hideProgress()
                             stopAllAktivoShimmers()
                             e.printStackTrace()
                             //Utilities.toastMessageShort(requireContext(), "Data Sync error: " + e.message)
                         }
                     })
             )
         } catch (e: Exception) {
             e.printStackTrace()
         }
     }

    private fun getLatestAktivoScore() {
        try {
            var score: String = "0"
            Utilities.printLogError("TodaysDate--->$todayDate")
            val date = DateHelper.getDateBeforeOrAfterGivenDaysNew(todayDate, -1)
            Utilities.printLogError("Getting Aktivo Score for Date--->$date")
            aktivoManager!!.query(ScoreQuery(date, date)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { localDateScoreStatsMap: Map<LocalDate, ScoreStats> -> localDateScoreStatsMap }
                .subscribe(object : SingleObserver<Map<LocalDate, ScoreStats>> {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onSuccess(localDateScoreStatsMap: Map<LocalDate, ScoreStats>) {
                        //logBulkData()
                        val keySet = localDateScoreStatsMap.keys
                        for (localDate in keySet) {
                            Utilities.printLogError("***********************************")
                            score = localDateScoreStatsMap[localDate]!!.score.toString()
                            Utilities.printLogError("Date : $localDate , Aktivo Score: $score")
                            Utilities.printLogError("***********************************")
                            if (!Utilities.isNullOrEmptyOrZero(score)) {
                                binding.txtAktivoScore.text = score
                                aktivoScore = score.toInt()
                            }
                        }
                        val aktivoData = HashMap<String, Any>()
                        aktivoData[CleverTapConstants.AKTIVO_SCORE2] = aktivoScore
                        CleverTapHelper.pushEventWithProperties(requireContext(),CleverTapConstants.AKTIVO_HEALTH_DATA_INFO,aktivoData)
                        stopAktivoScoreShimmer()
                    }
                    override fun onError(e: Throwable) {
                        //logBulkData()
                        Utilities.printLogError("Error in getScoreStats: " + e.message)
                        stopAktivoScoreShimmer()
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getMindScore() {
        try {
            val resultData: ResultData<MindScore> = aktivoManager!!.getLatestMindScore(viewModel.getUserPreference(PreferenceConstants.AKTIVO_MEMBER_ID))
            when (resultData) {
                is ResultData.Success -> {
                    val mindScore = resultData.data.score
                    Utilities.printLogError("MindScore : $mindScore")
                    if (mindScore >= 0) {
                        binding.txtMindScore.text = mindScore.toString()
                        aktivoMindScore = mindScore
                    }/*else {
                            binding.txtMindScore.text = resources.getString(R.string.CHECK_NOW)
                     }*/
                    val aktivoData = HashMap<String, Any>()
                    aktivoData[CleverTapConstants.AKTIVO_MIND_SCORE] = aktivoMindScore
                    CleverTapHelper.pushEventWithProperties(requireContext(),CleverTapConstants.AKTIVO_HEALTH_DATA_INFO,aktivoData)
                    stopMindScoreShimmer()
                }

                is ResultData.Error -> {
                    Utilities.printLogError("Error in getMindScore: " + resultData.errorData.message)
                    stopMindScoreShimmer()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getAktivoParameters() {
        val queryList = ArrayList<Query>()
        queryList.add(ScoreQuery(todayDate, todayDate))
        queryList.add(StepsQuery(todayDate, todayDate))
        queryList.add(SleepQuery(todayDate, todayDate))
        aktivoManager!!.query(queryList).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).map { maps -> maps }
            .subscribe(object : SingleObserver<List<Map<LocalDate, Stats>>> {
                override fun onSubscribe(d: Disposable) {}
                override fun onSuccess(mapList: List<Map<LocalDate, Stats>>) {
                    Utilities.printData("MapList", mapList, true)
                    Utilities.printLogError("MapSize--->" + mapList.size)
                    var steps = 0
                    var sleepInMinitues = 0
                    var exerciseInMinitues = 0
                    var sedentaryInMinitues = 0
                    var lightActivityInMinitues = 0
                    for (i in mapList.indices) {
                        val keySet = mapList[i].keys
                        if (queryList[i] is ScoreQuery) {
                            for (`object` in keySet) {
                                //Utilities.printLogError("Date: " + `object` + " value: " + (mapList[i][`object`] as ScoreStats?)!!.score)
                                val exercise = (mapList[i][`object`] as ScoreStats?)!!.scoreMvpa.value
                                val lightActivity = (mapList[i][`object`] as ScoreStats?)!!.scoreLipa.value
                                val sedentary = (mapList[i][`object`] as ScoreStats?)!!.scoreSb.value
                                Utilities.printLogError("Date: $`object` Exercise: $exercise")
                                Utilities.printLogError("Date: $`object` LightActivity: $lightActivity")
                                Utilities.printLogError("Date: $`object` Sedentary: $sedentary")
                                exerciseInMinitues = DateHelper.convertSecToMin(exercise)
                                sedentaryInMinitues = DateHelper.convertSecToMin(sedentary)
                                lightActivityInMinitues = DateHelper.convertSecToMin(lightActivity)
                                binding.viewExercise.setParamValue(DateHelper.getHourMinFromSecondsAktivo(exercise))
                                binding.viewLightActivity.setParamValue(DateHelper.getHourMinFromSecondsAktivo(lightActivity))
                                binding.viewSedentary.setParamValue(DateHelper.getHourMinFromSecondsAktivo(sedentary))
                            }
                        } else if (queryList[i] is StepsQuery) {
                            for (`object` in keySet) {
                                steps = (mapList[i][`object`] as StepStats?)!!.value
                                Utilities.printLogError("Date: $`object` Steps: $steps")
                            }
                            binding.viewSteps.setParamValue(steps.toString())
                        } else if (queryList[i] is SleepQuery) {
                            for (`object` in keySet) {
                                val sleep = (mapList[i][`object`] as SleepStats?)!!.value
                                Utilities.printLogError("Date: $`object` Sleep: $sleep")
                                sleepInMinitues = DateHelper.convertSecToMin(sleep)
                                binding.viewSleep.setParamValue(DateHelper.getHourMinFromSecondsAktivo(sleep))
                            }
                        }
                    }
                    stopAktivoParameterShimmer()
                    val aktivoData = HashMap<String, Any>()
                    aktivoData[CleverTapConstants.AKTIVO_STEPS] = steps
                    aktivoData[CleverTapConstants.AKTIVO_SLEEP] = sleepInMinitues
                    aktivoData[CleverTapConstants.AKTIVO_EXERCISE] = exerciseInMinitues
                    aktivoData[CleverTapConstants.AKTIVO_SEDENTARY] = sedentaryInMinitues
                    aktivoData[CleverTapConstants.AKTIVO_LIGHT_ACTIVITY] = lightActivityInMinitues
                    CleverTapHelper.pushEventWithProperties(requireContext(),CleverTapConstants.AKTIVO_HEALTH_DATA_INFO,aktivoData)
                }
                override fun onError(e: Throwable) {
                    stopAktivoParameterShimmer()
                    Utilities.printLogError("Aktivo stats error: " + e.localizedMessage + "---" + e.message)
                }
            })
    }

  private fun getBadgeByDate() {
      try {
          val date = DateHelper.getDateBeforeOrAfterGivenDaysNew(todayDate, -1)
          val badgeByDateQuery = BadgeByDateQuery(date)

          aktivoManager!!.queryBadgeByDate(badgeByDateQuery).subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(object : SingleObserver<DailyBadge> {
                  override fun onSubscribe(d: Disposable) {}
                  override fun onSuccess(dailyBadge: DailyBadge) {
                      if (dailyBadge.badgeType != null) {
                          Utilities.printData("DailyBadge", dailyBadge, true)
                          binding.layoutBadgeProgress.visibility = View.VISIBLE
                          if (this@HomeScreenFragment.isVisible) {
                              setBadgeView(dailyBadge.badgeType.badgeTypeEnum.toString())
                          }

                      }
                  }

                  override fun onError(e: Throwable) {
                      //val message = "BadgesByDate, error: " + e.message
                      binding.layoutBadgeProgress.visibility = View.GONE
                  }
              })

      } catch (e: Exception) {
          e.printStackTrace()
      }
  }

  fun setBadgeView(badge: String) {

      when (badge.uppercase()) {
          "CONTENDER" -> {
              binding.progressBadge.progress = 0
              binding.txtBadgeContender.setTextColor(
                  ContextCompat.getColor(
                      (activity as HomeMainActivity),
                      R.color.vivantActive
                  )
              )
              binding.txtBadgeChallenger.setTextColor(
                  ContextCompat.getColor(
                      (activity as HomeMainActivity),
                      R.color.mid_gray
                  )
              )
              binding.txtBadgeAchiever.setTextColor(
                  ContextCompat.getColor(
                      (activity as HomeMainActivity),
                      R.color.mid_gray
                  )
              )
              animateBadgeView(binding.imgBadgeContender)
          }

          "CHALLENGER" -> {
              Utilities.setProgressWithAnimation(
                  binding.progressBadge,
                  50,
                  Constants.PROGRESS_DURATION
              )
              Handler(Looper.getMainLooper()).postDelayed({
                  binding.txtBadgeContender.setTextColor(
                      ContextCompat.getColor(
                          (activity as HomeMainActivity),
                          R.color.mid_gray
                      )
                  )
                  binding.txtBadgeChallenger.setTextColor(
                      ContextCompat.getColor(
                          (activity as HomeMainActivity),
                          R.color.vivantActive
                      )
                  )
                  binding.txtBadgeAchiever.setTextColor(
                      ContextCompat.getColor(
                          (activity as HomeMainActivity),
                          R.color.mid_gray
                      )
                  )
                  animateBadgeView(binding.imgBadgeChallenger)
              }, (Constants.PROGRESS_DURATION).toLong())
          }

          "ACHIEVER" -> {
              Utilities.setProgressWithAnimation(
                  binding.progressBadge,
                  100,
                  Constants.PROGRESS_DURATION
              )
              Handler(Looper.getMainLooper()).postDelayed({
                  binding.txtBadgeContender.setTextColor(
                      ContextCompat.getColor(
                          (activity as HomeMainActivity),
                          R.color.mid_gray
                      )
                  )
                  binding.txtBadgeChallenger.setTextColor(
                      ContextCompat.getColor(
                          (activity as HomeMainActivity),
                          R.color.mid_gray
                      )
                  )
                  binding.txtBadgeAchiever.setTextColor(
                      ContextCompat.getColor(
                          (activity as HomeMainActivity),
                          R.color.vivantActive
                      )
                  )
                  animateBadgeView(binding.imgBadgeAchiever)
              }, (Constants.PROGRESS_DURATION).toLong())
          }
      }
  }

  private fun getOngoingChallenges() {
      try {
          Utilities.printLogError("Fetching ongoing challenges")
          aktivoManager!!.getOngoingChallenges(
              ChallengeListQuery(
                  viewModel.getUserPreference(PreferenceConstants.AKTIVO_MEMBER_ID),
                  false
              )
          ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
              .subscribe(object : SingleObserver<List<Challenge>> {
                  override fun onSubscribe(d: Disposable) {}
                  override fun onSuccess(challenges: List<Challenge>) {
                      Utilities.printLogError("Ongoing_Challenges_Count--->${challenges.size}")
                      Utilities.printData("Ongoing_Challenges", challenges, true)
                      if (challenges.isNotEmpty()) {
                          binding.layoutChallenges.visibility = View.VISIBLE
                          setUpSlidingChallengesViewPager(challenges.toMutableList())
                      } else {
                          binding.layoutChallenges.visibility = View.GONE
                      }
                      checkToPlayTutorial()
                  }

                  override fun onError(e: Throwable) {
                      Utilities.printLogError("getOngoingChallenges error:$e")
                      binding.layoutChallenges.visibility = View.GONE
                      checkToPlayTutorial()
                  }
              })
      } catch (e: Exception) {
          e.printStackTrace()
      }
  }

  private fun checkAllGoogleFitPermission() {
      try {
          aktivoManager!!.isGoogleFitPermissionGranted.subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(object : SingleObserver<Boolean> {
                  override fun onSubscribe(d: Disposable) {}

                  @RequiresApi(Build.VERSION_CODES.Q)
                  override fun onSuccess(aBoolean: Boolean) {
                      if (aBoolean) {
                          Utilities.printLogError("All Google fit permissions granted")
                          checkPhysicalActivityPermission()
                      } else {
                          requestAllGoogleFitPermission()
                      }
                  }

                  override fun onError(e: Throwable) {}
              })
      } catch (e: Exception) {
          e.printStackTrace()
      }
  }

  private fun requestAllGoogleFitPermission() {
      try {
          Utilities.printLogError("Requesting All Google fit permissions")
          aktivoManager!!.requestGoogleFitPermissions(
              requireActivity(), Constants.REQ_CODE_AKTIVO_GOOGLE_FIT_PERMISSIONS
          ).subscribeOn(
              Schedulers.io()
          ).observeOn(AndroidSchedulers.mainThread()).subscribe(object : CompletableObserver {
              override fun onSubscribe(d: Disposable) {}
              override fun onError(e: Throwable) {}
              override fun onComplete() {
                  Utilities.printLogError("Permission requested")
              }
          })
      } catch (e: Exception) {
          e.printStackTrace()
      }
  }

  @RequiresApi(Build.VERSION_CODES.Q)
  private fun checkPhysicalActivityPermission() {
      try {
          Utilities.printLogError("Checking Physical Activity permissions")
          aktivoManager!!.isActivityRecognitionPermissionGranted(requireActivity())
              .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
              .subscribe(object : SingleObserver<Boolean> {
                  override fun onSubscribe(d: Disposable) {}
                  override fun onSuccess(aBoolean: Boolean) {
                      if (aBoolean) {
                          Utilities.printLogError("Physical Activity permissions granted")
                          syncFitnessData()
                      } else {
                          requestPhysicalActivityPermission()
                      }
                  }

                  override fun onError(e: Throwable) {}
              })
      } catch (e: Exception) {
          e.printStackTrace()
      }
  }

  @RequiresApi(api = Build.VERSION_CODES.Q)
  private fun requestPhysicalActivityPermission() {
      try {
          Utilities.printLogError("Requesting Physical Activity permissions")
          aktivoManager!!.requestActivityRecognitionPermission(
              requireActivity(), Constants.REQ_PHYSICAL_ACTIVITY_PERMISSIONS
          ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
              .subscribe(object : CompletableObserver {
                  override fun onSubscribe(d: Disposable) {}
                  override fun onError(e: Throwable) {}
                  override fun onComplete() {
                      Utilities.printLogError("Permission requested")
                  }
              })
      } catch (e: Exception) {
          e.printStackTrace()
      }
  }

  private fun setUpSlidingChallengesViewPager(challengesList: MutableList<Challenge>) {
      try {
          slidingDotsCountChallenges = challengesList.size
          slidingImageDotsChallenges = arrayOfNulls(slidingDotsCountChallenges)
          val landingImagesAdapter = SlidingAktivoChallengesAdapter(
              requireContext(),
              requireActivity(),
              slidingDotsCountChallenges,
              challengesList
          )

          binding.slidingViewPagerChallenges.apply {
              adapter = landingImagesAdapter
              registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                  override fun onPageSelected(position: Int) {
                      for (i in 0 until slidingDotsCountChallenges) {
                          slidingImageDotsChallenges[i]?.setImageDrawable(
                              ContextCompat.getDrawable(
                                  binding.slidingViewPagerChallenges.context,
                                  R.drawable.dot_non_active
                              )
                          )
                      }
                      slidingImageDotsChallenges[position]?.setImageDrawable(
                          ContextCompat.getDrawable(
                              binding.slidingViewPagerChallenges.context,
                              R.drawable.dot_active
                          )
                      )
                  }
              })
          }

          if (slidingDotsCountChallenges > 1) {
              for (i in 0 until slidingDotsCountChallenges) {
                  slidingImageDotsChallenges[i] =
                      ImageView(binding.slidingViewPagerChallenges.context)
                  slidingImageDotsChallenges[i]?.setImageDrawable(
                      ContextCompat.getDrawable(
                          binding.slidingViewPagerChallenges.context,
                          R.drawable.dot_non_active
                      )
                  )
                  val params = LinearLayout.LayoutParams(
                      LinearLayout.LayoutParams.WRAP_CONTENT,
                      LinearLayout.LayoutParams.WRAP_CONTENT
                  )
                  params.setMargins(8, 0, 8, 0)
                  binding.sliderDotsChallenges.addView(slidingImageDotsChallenges[i], params)
              }
              slidingImageDotsChallenges[0]?.setImageDrawable(
                  ContextCompat.getDrawable(
                      binding.slidingViewPagerChallenges.context,
                      R.drawable.dot_active
                  )
              )

              val handler = Handler(Looper.getMainLooper())
              val update = Runnable {
                  if (currentPage1 == slidingDotsCountChallenges) {
                      currentPage1 = 0
                  }
                  binding.slidingViewPagerChallenges.setCurrentItem(currentPage1++, true)
              }
              Timer().schedule(object : TimerTask() {
                  override fun run() {
                      handler.post(update)
                  }
              }, 3000, 3000)
          }
      } catch (e: Exception) {
          e.printStackTrace()
      }
  }*/

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onAktivoSelection(from: String) {
        Utilities.printLogError("from---> $from")
        when (from) {
            Constants.PHYSICAL_ACTIVITY_PERMISSION -> {
//            checkPhysicalActivityPermission()
            }

            Constants.SYNC_FITNESS_DATA -> {
//            syncFitnessData()
            }

            Constants.DENIED -> {
                stopAllAktivoShimmers()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionUtil.checkNotificationPermission(object :
                PermissionUtil.AppPermissionListener {
                override fun isPermissionGranted(isGranted: Boolean) {
                    Utilities.printLogError("$isGranted")
                    if (isGranted) {
                        Utilities.toastMessageShort(
                            requireContext(),
                            resources.getString(R.string.MSG_NOTIFICATION_PERMISSION)
                        )
                    }
                }
            }, requireContext())
            //if (permissionResult) { }
        }
    }

    override fun onSmitFitFeatureClick(smitFitModel: SmitFitModel) {
        launchSmitFit(smitFitModel.featureCode)
    }

    private fun launchSmitFit(featureCode:String) {
        try {
            val data = HashMap<String, Any>()
            data[CleverTapConstants.FROM] = CleverTapConstants.DASHBOARD
            when (featureCode) {
                Constants.MEDITATION -> {
                    CleverTapHelper.pushEventWithProperties(requireContext(), CleverTapConstants.MEDITATION, data)
                    startActivity(MainActivity.getMeditationIntent(requireContext()))
                }
                Constants.YOGA -> {
                    CleverTapHelper.pushEventWithProperties(requireContext(), CleverTapConstants.YOGA, data)
                    startActivity(MainActivity.getYogaIntent(requireContext()))
                }
                Constants.EXERCISE -> {
                    CleverTapHelper.pushEventWithProperties(requireContext(), CleverTapConstants.EXERCISE, data)
                    startActivity(MainActivity.getExerciseIntent(requireContext()))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onLiveSessionClick(liveSessionModel: LiveSessionModel) {
        val data = HashMap<String, Any>()
        data[CleverTapConstants.FROM] = CleverTapConstants.DASHBOARD
        CleverTapHelper.pushEventWithProperties(
            requireContext(), CleverTapConstants.JOIN_LIVE_SESSION, data
        )
        Utilities.redirectToChrome(liveSessionModel.link, requireContext())
    }

    override fun onFinancialCalculatorClick(calculatorModel: FinancialCalculatorModel) {
        launchNvest(calculatorModel.calculatorCode)
    }

    private fun launchNvest(calculatorCode:String) {
        when (calculatorCode) {
            Constants.EDUCATION -> CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.EDUCATION)
            Constants.MARRIAGE -> CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.MARRIAGE)
            Constants.WEALTH -> CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.WEALTH)
            Constants.HOUSE -> CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.HOUSE)
            Constants.RETIREMENT -> CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.RETIREMENT)
            Constants.HUMAN_VALUE -> CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.HUMAN_VALUE)
        }
        //NvestSud.startNvestActivity(requireContext(),calculatorCode)
    }

    override fun onNimeyaCalculatorClick(item: FinancialCalculatorModel) {
        launchInvestometer(item.calculatorCode)
    }

    private fun launchInvestometer(calculatorCode:String) {
        nimeyaSingleton.clearData()
        Utilities.printLogError("NimeyaCalculatorCode--->$calculatorCode")
        when (calculatorCode) {
            Constants.NIMEYA_RISKO_METER -> {
                viewModel.showProgress()
                nimeyaViewModel.callGetRiskoMeterHistoryApi(this)
            }
            Constants.NIMEYA_PROTECTO_METER -> {
                viewModel.showProgress()
                nimeyaViewModel.callGetProtectoMeterHistoryApi(this)
            }
            Constants.NIMEYA_RETIRO_METER -> {
                viewModel.showProgress()
                nimeyaViewModel.callGetRetiroMeterHistoryApi(this)
            }
        }
    }

    override fun onBlogSelection(blog: BlogItem, view: View) {
        val data = HashMap<String, Any>()
        data[CleverTapConstants.FROM] = CleverTapConstants.DASHBOARD
        data[CleverTapConstants.CATEGORY_ID] = blog.categoryId!!
        data[CleverTapConstants.BLOG_ID] = blog.id!!
        CleverTapHelper.pushEventWithProperties(
            requireContext(), CleverTapConstants.BLOG_DETAILS_SCREEN, data
        )
        viewModel.callAddFeatureAccessLogApi(
            Constants.HEALTH_LIBRARY, "Health Library", "VivantCore", ""
        )


        val intent = Intent(requireContext(), BlogDetailActivity::class.java)
        intent.putExtra(Constants.TITLE, blog.title)
        intent.putExtra(Constants.DESCRIPTION, blog.description)
        intent.putExtra(Constants.BODY, blog.body)
        intent.putExtra(Constants.BLOG_ID, blog.id)
        intent.putExtra(Constants.CATEGORY_ID, blog.categoryId!!)
        intent.putExtra(Constants.LINK, blog.link)
        startActivity(intent)
    }

    private fun getWellfieView(): View {
        return if (binding.layoutScanVitals.visibility == View.VISIBLE) {
            binding.btnScan
        } else {
            binding.btnRescan
        }
    }

    private fun scrollToView(view: View) {
        view.parent.requestChildFocus(view, view)
    }

    private fun scrollToTop() {
        scrollToView(binding.txtUserName)/*        binding.scrollView.post {
                binding.scrollView.scrollTo(0, binding.scrollView.top)
            }*/
    }

    /*private fun scrollToBottom() {
        binding.scrollView.post {
            //binding.scrollView.fullScroll(View.FOCUS_DOWN)
            binding.scrollView.scrollTo(0, binding.scrollView.bottom)
        }
    }
*//*private fun navigateToFitnessTracker() {
        CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.PHYSICAL_ACTIVITY_TRACKER)
        viewModel.callAddFeatureAccessLogApi(
            Constants.FITNESS_TRACKER, "Fitness Tracker", "VivantCore", ""
        )
        startActivity(Intent(requireContext(), FitnessDataActivity::class.java))
    }*/

    /* private fun navigateToAmahaWebView(moduleCode: String, title: String) {
         CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.CONSULT_THERAPIST)
         val intent = Intent(requireContext(), AmahaWebViewActivity::class.java)
         intent.putExtra(Constants.MODULE_CODE, moduleCode)
         intent.putExtra(Constants.TITLE, title)
         startActivity(intent)
     }*/

    /* private fun animateBadgeView(badge: ImageView) {
         badge.startAnimation(animation)
     }*/

    private fun checkToPlayTutorial() {
        if (viewModel.isFirstTimeHomeVisit()) {
            //selectToPlayTutorial()
            viewModel.setFirstTimeHomeVisitFlag(false)
        }
    }

    private fun selectToPlayTutorial() {
        if ((activity as HomeMainActivity).isDrawerOpen()) {
            (activity as HomeMainActivity).closeDrawer()
        }
        if (Utilities.getUserPreference(PreferenceConstants.ORG_NAME) == Constants.SUD_ORG_NAME) {
            playTutorialWithLeadershipExperinces()
        } else {
            playTutorial()
        }
    }

    private fun startDashboardProductsShimmer() {
        binding.layoutDashboardBannerShimmer.startShimmer()
        binding.layoutDashboardBannerShimmer.visibility = View.VISIBLE
        binding.layoutDashboardBanner.visibility = View.GONE
    }

    fun stopDashboardProductsShimmer() {
        binding.layoutDashboardBannerShimmer.stopShimmer()
        binding.layoutDashboardBannerShimmer.visibility = View.GONE
        binding.layoutDashboardBanner.visibility = View.VISIBLE
    }

    fun hideBannerView() {
        binding.layoutBanner.visibility = View.GONE
    }

    private fun startWellfieShimmer() {
        binding.layoutScanVitalsShimmer.startShimmer()
        binding.layoutScanVitalsShimmer.visibility = View.VISIBLE
        binding.cardScanVitals.visibility = View.GONE
    }

    private fun stopWellfieShimmer() {
        binding.layoutScanVitalsShimmer.stopShimmer()
        binding.layoutScanVitalsShimmer.visibility = View.GONE
        binding.cardScanVitals.visibility = View.VISIBLE
    }

    /* private fun startBlogsShimmer() {
         binding.layoutShimmer.startShimmer()
         binding.layoutShimmer.visibility = View.VISIBLE
     }*/

    fun stopBlogsShimmer() {
        binding.layoutShimmer.stopShimmer()
        binding.layoutShimmer.visibility = View.GONE
    }

    private fun startLiveSessionsShimmer() {
        binding.layoutLiveSessionsShimmer.startShimmer()
        binding.layoutLiveSessionsShimmer.visibility = View.GONE
    }

    private fun stopLiveSessionsShimmer() {
        binding.layoutLiveSessionsShimmer.stopShimmer()
        binding.layoutLiveSessionsShimmer.visibility = View.GONE
    }

    /*private fun startAktivoParameterShimmer() {
        binding.layoutAktivoShimmer.startShimmer()
        binding.layoutAktivoShimmer.visibility = View.VISIBLE
        binding.layoutRvAktivo.visibility = View.GONE
    }
*/
    private fun stopAktivoParameterShimmer() {
        binding.layoutAktivoShimmer.stopShimmer()
        binding.layoutAktivoShimmer.visibility = View.GONE
        binding.layoutRvAktivo.visibility = View.VISIBLE
    }

    /*
        private fun startAktivoScoreShimmer() {
            binding.layoutAktivoScoreShimmer.startShimmer()
            binding.layoutAktivoScoreShimmer.visibility = View.VISIBLE
            binding.layoutAktivoScore.visibility = View.GONE
        }
    */

    private fun stopAktivoScoreShimmer() {
        binding.layoutAktivoScoreShimmer.stopShimmer()
        binding.layoutAktivoScoreShimmer.visibility = View.GONE
        binding.layoutAktivoScore.visibility = View.VISIBLE
    }

    /* private fun startMindScoreShimmer() {
         binding.layoutMindScoreShimmer.startShimmer()
         binding.layoutMindScoreShimmer.visibility = View.VISIBLE
         binding.layoutMindScore.visibility = View.GONE
     }*/

    private fun stopMindScoreShimmer() {
        binding.layoutMindScoreShimmer.stopShimmer()
        binding.layoutMindScoreShimmer.visibility = View.GONE
        binding.layoutMindScore.visibility = View.VISIBLE
    }

    private fun stopAllAktivoShimmers() {
        stopAktivoScoreShimmer()
        stopMindScoreShimmer()
        stopAktivoParameterShimmer()
    }

    override fun onHelpClick() {
        selectToPlayTutorial()
    }

    private fun getTutorialWithLeadershipExperincesList(): MutableList<TutorialModel> {
        val list: MutableList<TutorialModel> = mutableListOf()
        /*list.add(TutorialModel(55,resources.getString(R.string.TUTORIAL_AKTIVO_SCORE),resources.getString(R.string.TUTORIAL_AKTIVO_SCORE_DESC),binding.cardAktivoScore))
        list.add(TutorialModel(55,resources.getString(R.string.TUTORIAL_MIND_SCORE),resources.getString(R.string.TUTORIAL_MIND_SCORE_DESC),binding.cardMindScore))
        if (binding.layoutChallenges.visibility == View.VISIBLE) {
            list.add(TutorialModel(120, resources.getString(R.string.CHALLENGES), "", binding.layoutChallenges))
        }
        list.add(TutorialModel(50,resources.getString(R.string.LEADERSHIP_EXPERIENCES),"",binding.cardLeadershipExperinces))*/
        list.add(TutorialModel(50, resources.getString(R.string.TUTORIAL_HRA), resources.getString(R.string.TUTORIAL_HRA_DESC), binding.cardHra))
        list.add(TutorialModel(90,resources.getString(R.string.TUTORIAL_SMIT_FIT),resources.getString(R.string.TUTORIAL_SMIT_FIT_DESC),binding.rvSmitFit))
        if (binding.layoutLiveSessions.visibility == View.VISIBLE) {
            list.add(TutorialModel(70,resources.getString(R.string.TUTORIAL_LIVE_SESSIONS),resources.getString(R.string.TUTORIAL_LIVE_SESSIONS_DESC),binding.lblLiveSessions))
        }
        list.add(TutorialModel(50, resources.getString(R.string.TUTORIAL_SALT_MPT), resources.getString(R.string.TUTORIAL_SALT_MPT_DESC), binding.cardSaltMpt))
        list.add(TutorialModel(50, resources.getString(R.string.SALT_VIDEOS_TITLE), resources.getString(R.string.SALT_VIDEOS_DESC), binding.cardSaltVideos))
        list.add(TutorialModel(55, resources.getString(R.string.TUTORIAL_INVESTOMETERS), resources.getString(R.string.TUTORIAL_INVESTOMETERS_DESC), binding.lblInvestometers))
        list.add(TutorialModel(50, resources.getString(R.string.NIMEYA_TITLE), resources.getString(R.string.NIMEYA_DESC), binding.btnViewDetailsNimeya))
        list.add(TutorialModel(105, resources.getString(R.string.TUTORIAL_NVEST_CALCULATORS), resources.getString(R.string.TUTORIAL_NVEST_CALCULATORS_DESC), binding.rvFinancialCalculators))
        /*list.add(TutorialModel(65,resources.getString(R.string.TUTORIAL_WELLFIE),resources.getString(R.string.TUTORIAL_WELLFIE_DESC),getWellfieView()))*/
        return list
    }

    private fun getTutorialList(): MutableList<TutorialModel> {
        val list: MutableList<TutorialModel> = mutableListOf()
        /*list.add(TutorialModel(55,resources.getString(R.string.TUTORIAL_AKTIVO_SCORE),resources.getString(R.string.TUTORIAL_AKTIVO_SCORE_DESC),binding.cardAktivoScore))
        list.add(TutorialModel(55,resources.getString(R.string.TUTORIAL_MIND_SCORE),resources.getString(R.string.TUTORIAL_MIND_SCORE_DESC),binding.cardMindScore))
        if (binding.layoutChallenges.visibility == View.VISIBLE) {
            list.add(TutorialModel(120, resources.getString(R.string.CHALLENGES), "", binding.layoutChallenges))
        }*/
        list.add(TutorialModel(50, resources.getString(R.string.TUTORIAL_HRA), resources.getString(R.string.TUTORIAL_HRA_DESC), binding.cardHra))
        list.add(TutorialModel(90,resources.getString(R.string.TUTORIAL_SMIT_FIT),resources.getString(R.string.TUTORIAL_SMIT_FIT_DESC),binding.rvSmitFit))
        if (binding.layoutLiveSessions.visibility == View.VISIBLE) {
            list.add(TutorialModel(70,resources.getString(R.string.TUTORIAL_LIVE_SESSIONS),resources.getString(R.string.TUTORIAL_LIVE_SESSIONS_DESC),binding.lblLiveSessions))
        }
        list.add(TutorialModel(50, resources.getString(R.string.TUTORIAL_SALT_MPT), resources.getString(R.string.TUTORIAL_SALT_MPT_DESC), binding.cardSaltMpt))
        list.add(TutorialModel(50, resources.getString(R.string.SALT_VIDEOS_TITLE), resources.getString(R.string.SALT_VIDEOS_DESC), binding.cardSaltVideos))
        list.add(TutorialModel(55, resources.getString(R.string.TUTORIAL_INVESTOMETERS), resources.getString(R.string.TUTORIAL_INVESTOMETERS_DESC), binding.lblInvestometers))
        list.add(TutorialModel(50, resources.getString(R.string.NIMEYA_TITLE), resources.getString(R.string.NIMEYA_DESC), binding.btnViewDetailsNimeya))
        list.add(TutorialModel(105, resources.getString(R.string.TUTORIAL_NVEST_CALCULATORS), resources.getString(R.string.TUTORIAL_NVEST_CALCULATORS_DESC), binding.rvFinancialCalculators))
        /* list.add(TutorialModel(65,resources.getString(R.string.TUTORIAL_WELLFIE),resources.getString(R.string.TUTORIAL_WELLFIE_DESC),getWellfieView()))*/
        return list
    }

    fun setBottomNavigationView(navView: BottomNavigationView, cfm: FragmentManager) {
        navigation = navView
        fm = cfm
    }

    override fun onScore(hraSummary: HRASummary?) {

    }

    override fun onVitalDataUpdateListener(history: List<TrackParameterMaster.History>) {

    }

    fun gotoNimeyaActivity(destination: String) {
        val intent = Intent(requireContext(), NimeyaActivity::class.java)
        intent.putExtra(Constants.TO, destination)
        startActivity(intent)


    }

    override fun onPolicyBannerClick(item: Int) {
        Utilities.printLogError("BannerItem--->$item")
        if (bannerDialog != null && bannerDialog!!.isShowing) {
            Utilities.preferenceUtils.storeBooleanPreference(Constants.BANNER_AD, true)
            bannerDialog!!.dismiss()
        }
    }

}
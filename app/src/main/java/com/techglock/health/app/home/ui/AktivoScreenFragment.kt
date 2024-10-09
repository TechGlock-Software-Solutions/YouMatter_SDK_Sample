package com.techglock.health.app.home.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.techglock.health.app.R
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
import com.techglock.health.app.common.utils.DefaultNotificationDialog
import com.techglock.health.app.common.utils.PermissionUtil
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentAktivoScreenBinding
import com.techglock.health.app.home.di.ScoreListener
import com.techglock.health.app.home.viewmodel.DashboardViewModel
import com.techglock.health.app.model.entity.HRASummary
import com.techglock.health.app.model.entity.TrackParameterMaster
import com.techglock.health.app.repository.utils.Resource
import com.google.firebase.messaging.FirebaseMessaging
import io.reactivex.disposables.CompositeDisposable


class AktivoScreenFragment : BaseFragment(), ScoreListener,
    HomeMainActivity.OnHelpClickListener {

    private lateinit var binding: FragmentAktivoScreenBinding
    private val viewModel: DashboardViewModel by lazy {
        ViewModelProvider(this)[DashboardViewModel::class.java]
    }
    private var fitnessDataManager: FitnessDataManager? = null
    private val permissionUtil = PermissionUtil
    private var tapCount = 0
    private var currentPage1 = 0
    private var slidingImageDotsChallenges: Array<ImageView?> = arrayOf()
    private var slidingDotsCountChallenges = 0
    private val todayDate = DateHelper.currentDateAsStringyyyyMMdd

    //    private var aktivoManager: AktivoManager? = null
    private var compositeDisposable: CompositeDisposable? = null
    private var animation: Animation? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utilities.printLogError("Inside=> onCreate")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        (activity as HomeMainActivity).setOnAktivoListener(this)
        (activity as HomeMainActivity).setOnHelpClickListener(this)
        (activity as HomeMainActivity).setToolbarInfo(
            0,
            showAppLogo = false,
            title = getString(R.string.FITNESS_TRACKER),
            showBg = false,
            showBottomNavigation = false
        )
        Utilities.printLog("Inside=> onViewCreated")
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAktivoScreenBinding.inflate(inflater, container, false)
        try {
            initialise()
            setClickable()
            setObserver()
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
        compositeDisposable = CompositeDisposable()
        (activity as HomeMainActivity).registerListener(this)

        fitnessDataManager = FitnessDataManager(requireContext())
        binding.txtUserName.text =
            "${resources.getString(R.string.HI)} " + viewModel.preferenceUtil.getPreference(
                PreferenceConstants.FIRSTNAME, ""
            )

        binding.viewSteps.setParamTitle(resources.getString(R.string.STEPS))
        binding.viewSleep.setParamTitle(resources.getString(R.string.SLEEP))
        binding.viewExercise.setParamTitle(resources.getString(R.string.EXERCISE))
        binding.viewSedentary.setParamTitle(resources.getString(R.string.SEDENTARY))
        binding.viewLightActivity.setParamTitle(resources.getString(R.string.LIGHT_ACTIVITY))

        binding.layoutBadgeProgress.visibility = View.GONE
        binding.layoutChallenges.visibility = View.GONE
        startAktivoParameterShimmer()
        startAktivoScoreShimmer()
        startMindScoreShimmer()

        checkNotificationPermission()

    }


    @SuppressLint("ClickableViewAccessibility")
    private fun setClickable() {

        binding.layoutFeedUpdate.setOnClickListener {
            openAnotherActivity(destination = NavigationConstants.FEED_UPDATE_SCREEN)
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

        binding.layoutBadgeProgress.setOnClickListener {
            CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.AKTIVO_BADGES)
            navigateToAktivo(Constants.AKTIVO_BADGES_CODE)
        }

    }


    private fun navigateToAktivo(screenCode: String) {
        openAnotherActivity(destination = NavigationConstants.AKTIVO_PERMISSION_SCREEN) {
            putString(Constants.CODE, screenCode)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setObserver() {
        viewModel.aktivoCreateUser.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {
                if (!Utilities.isNullOrEmpty(it.data!!.resultData.member.id)) {
                    viewModel.storeUserPreference(
                        PreferenceConstants.AKTIVO_MEMBER_ID, it.data!!.resultData.member.id!!
                    )
                    viewModel.callAktivoGetUserTokenApi(it.data!!.resultData.member.id!!)
                }
            }
            if (it.status == Resource.Status.ERROR) {
                stopAllAktivoShimmers()
            }
        }
        viewModel.aktivoGetUserToken.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {
                if (!Utilities.isNullOrEmpty(it.data!!.accessToken) && !Utilities.isNullOrEmpty(it.data!!.refreshToken)) {
                    viewModel.storeUserPreference(
                        PreferenceConstants.AKTIVO_ACCESS_TOKEN, it.data!!.accessToken!!
                    )
                    viewModel.storeUserPreference(
                        PreferenceConstants.AKTIVO_REFRESH_TOKEN, it.data!!.refreshToken!!
                    )
                    authenticateUserUsingToken()
                }
            }
            if (it.status == Resource.Status.ERROR) {
                stopAllAktivoShimmers()
            }
        }

    }


    private fun startAktivoScoreShimmer() {
        binding.layoutAktivoScoreShimmer.startShimmer()
        binding.layoutAktivoScoreShimmer.visibility = View.VISIBLE
        binding.layoutAktivoScore.visibility = View.GONE
    }

    private fun stopAktivoScoreShimmer() {
        binding.layoutAktivoScoreShimmer.stopShimmer()
        binding.layoutAktivoScoreShimmer.visibility = View.GONE
        binding.layoutAktivoScore.visibility = View.VISIBLE
    }

    private fun startMindScoreShimmer() {
        binding.layoutMindScoreShimmer.startShimmer()
        binding.layoutMindScoreShimmer.visibility = View.VISIBLE
        binding.layoutMindScore.visibility = View.GONE
    }

    private fun stopMindScoreShimmer() {
        binding.layoutMindScoreShimmer.stopShimmer()
        binding.layoutMindScoreShimmer.visibility = View.GONE
        binding.layoutMindScore.visibility = View.VISIBLE
    }

    private fun startAktivoParameterShimmer() {
        binding.layoutAktivoShimmer.startShimmer()
        binding.layoutAktivoShimmer.visibility = View.VISIBLE
        binding.layoutRvAktivo.visibility = View.GONE
    }

    private fun stopAktivoParameterShimmer() {
        binding.layoutAktivoShimmer.stopShimmer()
        binding.layoutAktivoShimmer.visibility = View.GONE
        binding.layoutRvAktivo.visibility = View.VISIBLE
    }

    private fun stopAllAktivoShimmers() {
        stopAktivoScoreShimmer()
        stopMindScoreShimmer()
        stopAktivoParameterShimmer()
        checkToPlayTutorial()

    }


    private fun checkToPlayTutorial() {
        if (viewModel.isFirstTimeHomeVisit()) {
            selectToPlayTutorial()
            viewModel.setFirstTimeHomeVisitFlag(false)
        }
    }


    private fun selectToPlayTutorial() {
        if ((activity as HomeMainActivity).isDrawerOpen()) {
            (activity as HomeMainActivity).closeDrawer()
        }
        if (binding.layoutChallenges.visibility == View.VISIBLE) {
            playTutorialWithChallenges()
        } else {
            playTutorial()
        }

    }

    private fun playTutorial() {
        val tapTarget = TapTargetSequence(requireActivity())
        tapTarget.targets(
            TapTarget.forView(
                binding.cardAktivoScore,
                resources.getString(R.string.TUTORIAL_AKTIVO_SCORE),
                resources.getString(R.string.TUTORIAL_AKTIVO_SCORE_DESC)
            ).targetRadius(55).setConfiguration(requireContext()),

            TapTarget.forView(
                binding.cardMindScore,
                resources.getString(R.string.TUTORIAL_MIND_SCORE),
                resources.getString(R.string.TUTORIAL_MIND_SCORE_DESC)
            ).targetRadius(55).setConfiguration(requireContext()),
        ).listener(object : TapTargetSequence.Listener {
            // This listener will tell us when interesting(tm) events happen in regards
            // to the sequence
            override fun onSequenceFinish() {
                //requestGoogleFit()
                tapTarget.cancel()
                tapCount = 0
            }

            override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) {
                tapCount += 1
                Utilities.printLogError("tapCount--->$tapCount")

            }

            override fun onSequenceCanceled(lastTarget: TapTarget?) {

            }

        }).start()

    }

    private fun playTutorialWithChallenges() {
        val tapTarget = TapTargetSequence(requireActivity())
        tapTarget.targets(
            TapTarget.forView(
                binding.cardAktivoScore,
                resources.getString(R.string.TUTORIAL_AKTIVO_SCORE),
                resources.getString(R.string.TUTORIAL_AKTIVO_SCORE_DESC)
            ).targetRadius(55).setConfiguration(requireContext()),

            TapTarget.forView(
                binding.cardMindScore,
                resources.getString(R.string.TUTORIAL_MIND_SCORE),
                resources.getString(R.string.TUTORIAL_MIND_SCORE_DESC)
            ).targetRadius(55).setConfiguration(requireContext()),

            TapTarget.forView(
                binding.cardChallenges,
                resources.getString(R.string.TUTORIAL_CHALLENGE),
                resources.getString(R.string.TUTORIAL_CHALLENGE_DESC)
            ).targetRadius(90).setConfiguration(requireContext()),

            ).listener(object : TapTargetSequence.Listener {

            override fun onSequenceFinish() {
                tapTarget.cancel()
                tapCount = 0
            }

            override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) {
                tapCount += 1
                Utilities.printLogError("tapCount--->$tapCount")

            }

            override fun onSequenceCanceled(lastTarget: TapTarget?) {

            }

        }).start()

    }

    override fun onScore(hraSummary: HRASummary?) {

    }

    override fun onVitalDataUpdateListener(history: List<TrackParameterMaster.History>) {

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionResult = permissionUtil.checkNotificationPermission(object :
                PermissionUtil.AppPermissionListener {
                override fun isPermissionGranted(isGranted: Boolean) {
                    Utilities.printLogError("$isGranted")
                    if (isGranted) {
                        Utilities.toastMessageShort(
                            requireContext(),
                            resources.getString(R.string.MSG_NOTIFICATION_PERMISSION)
                        )
                        getAktivoScore()
                    }
                }
            }, requireContext())
            if (permissionResult) {
                getAktivoScore()
            }
        } else {
            getAktivoScore()
        }
    }

    private fun getAktivoScore() {
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
//        aktivoManager!!.setClientId(Constants.strAktivoClientId)
//        aktivoManager!!.setUserTokens(token, refreshToken)
//        authenticateUser(userId)
    }

    /*private fun authenticateUser(userId: String) {
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
    }*/

    private fun checkFitnessPermissionsAndProceed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (fitnessDataManager!!.aktivoAuthPermissionsApproved() && permissionUtil.isActivityRecognitionPermission(
                    this.requireContext()
                )
            ) {
//                syncFitnessData()
            } else {
                showAktivoPermissionsDialog()
            }
        } else {
            if (fitnessDataManager!!.aktivoAuthPermissionsApproved()) {
//                syncFitnessData()
            } else {
                showAktivoPermissionsDialog()
            }
        }
    }


    /*private fun syncFitnessData() {
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
            var score = ""
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
                            }
                        }
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
            val resultData: ResultData<MindScore> =
                aktivoManager!!.getLatestMindScore(viewModel.getUserPreference(PreferenceConstants.AKTIVO_MEMBER_ID))
            when (resultData) {
                is ResultData.Success -> {
                    val mindScore = resultData.data.score
                    Utilities.printLogError("MindScore : $mindScore")
                    if (mindScore >= 0) {
                        binding.txtMindScore.text = mindScore.toString()
                    }*//*                    else {
                                            binding.txtMindScore.text = resources.getString(R.string.CHECK_NOW)
                                        }*//*
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
                    for (i in mapList.indices) {
                        val keySet = mapList[i].keys
                        if (queryList[i] is ScoreQuery) {
                            for (`object` in keySet) {
                                //Utilities.printLogError("Date: " + `object` + " value: " + (mapList[i][`object`] as ScoreStats?)!!.score)
                                val exercise =
                                    (mapList[i][`object`] as ScoreStats?)!!.scoreMvpa.value
                                val lightActivity =
                                    (mapList[i][`object`] as ScoreStats?)!!.scoreLipa.value
                                val sedentary =
                                    (mapList[i][`object`] as ScoreStats?)!!.scoreSb.value
                                Utilities.printLogError("Date: $`object` Exercise: $exercise")
                                Utilities.printLogError("Date: $`object` LightActivity: $lightActivity")
                                Utilities.printLogError("Date: $`object` Sedentary: $sedentary")
                                binding.viewExercise.setParamValue(
                                    DateHelper.getHourMinFromSecondsAktivo(
                                        exercise
                                    )
                                )
                                binding.viewLightActivity.setParamValue(
                                    DateHelper.getHourMinFromSecondsAktivo(
                                        lightActivity
                                    )
                                )
                                binding.viewSedentary.setParamValue(
                                    DateHelper.getHourMinFromSecondsAktivo(
                                        sedentary
                                    )
                                )
                            }
                        } else if (queryList[i] is StepsQuery) {
                            for (`object` in keySet) {
                                val steps = (mapList[i][`object`] as StepStats?)!!.value.toString()
                                Utilities.printLogError("Date: $`object` Steps: $steps")
                                binding.viewSteps.setParamValue(steps)
                            }
                        } else if (queryList[i] is SleepQuery) {
                            for (`object` in keySet) {
                                val sleep = (mapList[i][`object`] as SleepStats?)!!.value
                                Utilities.printLogError("Date: $`object` Sleep: $sleep")
                                binding.viewSleep.setParamValue(
                                    DateHelper.getHourMinFromSecondsAktivo(
                                        sleep
                                    )
                                )
                            }
                        }
                    }
                    stopAktivoParameterShimmer()
                }

                override fun onError(e: Throwable) {
                    stopAktivoParameterShimmer()
                    Utilities.printLogError("Aktivo stats error: " + e.localizedMessage + "---" + e.message)
                }
            })
    }


    private fun getOngoingChallenges() {
        try {
            Utilities.printLogError("Fetching ongoing challenges")
            aktivoManager!!.getOngoingChallenges(
                ChallengeListQuery(
                    viewModel.getUserPreference(
                        PreferenceConstants.AKTIVO_MEMBER_ID
                    ), false
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
                            if (this@AktivoScreenFragment.isVisible) {
                                setBadgeView(dailyBadge.badgeType.badgeTypeEnum.toString())
                            }

                        }
                    }

                    override fun onError(e: Throwable) {
                        val message = "BadgesByDate, error: " + e.message
                        binding.layoutBadgeProgress.visibility = View.GONE
                    }
                })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/

    fun setBadgeView(badge: String) {

        when (badge.uppercase()) {
            "CONTENDER" -> {
                binding.progressBadge.progress = 0
                binding.txtBadgeContender.setTextColor(
                    ContextCompat.getColor(
                        (activity as HomeMainActivity), R.color.vivantActive
                    )
                )
                binding.txtBadgeChallenger.setTextColor(
                    ContextCompat.getColor(
                        (activity as HomeMainActivity), R.color.mid_gray
                    )
                )
                binding.txtBadgeAchiever.setTextColor(
                    ContextCompat.getColor(
                        (activity as HomeMainActivity), R.color.mid_gray
                    )
                )
                animateBadgeView(binding.imgBadgeContender)
            }

            "CHALLENGER" -> {
                Utilities.setProgressWithAnimation(
                    binding.progressBadge, 50, Constants.PROGRESS_DURATION
                )
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.txtBadgeContender.setTextColor(
                        ContextCompat.getColor(
                            (activity as HomeMainActivity), R.color.mid_gray
                        )
                    )
                    binding.txtBadgeChallenger.setTextColor(
                        ContextCompat.getColor(
                            (activity as HomeMainActivity), R.color.vivantActive
                        )
                    )
                    binding.txtBadgeAchiever.setTextColor(
                        ContextCompat.getColor(
                            (activity as HomeMainActivity), R.color.mid_gray
                        )
                    )
                    animateBadgeView(binding.imgBadgeChallenger)
                }, (Constants.PROGRESS_DURATION).toLong())
            }

            "ACHIEVER" -> {
                Utilities.setProgressWithAnimation(
                    binding.progressBadge, 100, Constants.PROGRESS_DURATION
                )
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.txtBadgeContender.setTextColor(
                        ContextCompat.getColor(
                            (activity as HomeMainActivity), R.color.mid_gray
                        )
                    )
                    binding.txtBadgeChallenger.setTextColor(
                        ContextCompat.getColor(
                            (activity as HomeMainActivity), R.color.mid_gray
                        )
                    )
                    binding.txtBadgeAchiever.setTextColor(
                        ContextCompat.getColor(
                            (activity as HomeMainActivity), R.color.vivantActive
                        )
                    )
                    animateBadgeView(binding.imgBadgeAchiever)
                }, (Constants.PROGRESS_DURATION).toLong())
            }
        }
    }

    private fun animateBadgeView(badge: ImageView) {
        badge.startAnimation(animation)
        //badge.clearAnimation()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (compositeDisposable != null) {
            compositeDisposable!!.dispose()
        }

    }


    private fun showAktivoPermissionsDialog() {
        if (this.isVisible) {
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
            val defaultNotificationDialog = DefaultNotificationDialog(
                (activity as HomeMainActivity),
                object : DefaultNotificationDialog.OnDialogValueListener {
                    override fun onDialogClickListener(
                        isButtonLeft: Boolean, isButtonRight: Boolean
                    ) {
                        if (isButtonRight) {
//                            checkAllGoogleFitPermission()
                        }
                    }
                },
                dialogData
            )
            defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            defaultNotificationDialog.show()
        }
    }

    /*private fun checkAllGoogleFitPermission() {
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

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onAktivoSelection(from: String) {
        Utilities.printLogError("from---> $from")
        when (from) {
            Constants.PHYSICAL_ACTIVITY_PERMISSION -> {
                checkPhysicalActivityPermission()
            }

            Constants.SYNC_FITNESS_DATA -> {
                syncFitnessData()
            }

            Constants.DENIED -> {
                stopAllAktivoShimmers()
            }
        }
    }

    private fun setUpSlidingChallengesViewPager(challengesList: MutableList<Challenge>) {
        try {
            slidingDotsCountChallenges = challengesList.size
            slidingImageDotsChallenges = arrayOfNulls(slidingDotsCountChallenges)
            val landingImagesAdapter = SlidingAktivoChallengesAdapter(
                requireContext(), requireActivity(), slidingDotsCountChallenges, challengesList
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
                                binding.slidingViewPagerChallenges.context, R.drawable.dot_active
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
                            binding.slidingViewPagerChallenges.context, R.drawable.dot_non_active
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
                        binding.slidingViewPagerChallenges.context, R.drawable.dot_active
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
    }
*/

    override fun onHelpClick() {
        selectToPlayTutorial()
    }

    override fun onResume() {
        super.onResume()
        binding.progressBadge.progress = 0
    }

}
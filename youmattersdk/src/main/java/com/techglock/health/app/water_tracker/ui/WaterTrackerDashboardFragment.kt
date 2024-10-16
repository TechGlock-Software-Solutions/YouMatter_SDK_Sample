package com.techglock.health.app.water_tracker.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.taptargetview.TapTarget
import com.techglock.health.app.common.taptargetview.TapTargetSequence
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.DefaultNotificationDialog
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.common.view.WaveView
import com.techglock.health.app.databinding.FragmentWaterTrackerDashboardBinding
import com.techglock.health.app.model.waterTracker.GetDailyWaterIntakeModel
import com.techglock.health.app.water_tracker.WaterTrackerActivity
import com.techglock.health.app.water_tracker.viewmodel.WaterTrackerViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class WaterTrackerDashboardFragment : BaseFragment(),
    AddWaterIntakeBottomSheet.OnIntakeClickListener,
    DefaultNotificationDialog.OnDialogValueListener {

    private val viewModel: WaterTrackerViewModel by lazy {
        ViewModelProvider(this)[WaterTrackerViewModel::class.java]
    }
    private lateinit var binding: FragmentWaterTrackerDashboardBinding

    private var todayIntakePercentage = 0
    private var waterGoal = 0
    private var screen = ""

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            // Callback to Handle back button event
            val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    //(activity as WaterTrackerActivity).routeToHomeScreen()
                    //requireActivity().finish()
                    when (screen) {
                        "WATER_REMINDER", "WATER_REMINDER_21_POSITIVE", "WATER_REMINDER_21_NEGATIVE" -> {
                            (activity as WaterTrackerActivity).routeToHomeScreen()
                        }

                        else -> {
                            requireActivity().finish()
                        }
                    }
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
        binding = FragmentWaterTrackerDashboardBinding.inflate(inflater, container, false)
        try {
            if (requireActivity().intent.hasExtra(Constants.SCREEN)) {
                screen = requireActivity().intent.getStringExtra(Constants.SCREEN)!!
            }
            Utilities.printLogError("screen----->$screen")
            initialise()
            setClickable()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.isFirstWaterTrackerVisit()) {
            //tutorialWaterTrackerOld()
            viewModel.setFirstWaterTrackerVisitFlag(false)
        }
    }

    private fun initialise() {
        CleverTapHelper.pushEvent(
            requireContext(),
            CleverTapConstants.HYDRATION_TRACKER_DASHBOARD_SCREEN
        )
        viewModel.callGetDailyWaterIntakeApi(DateHelper.currentUTCDatetimeInMillisecAsString)
        viewModel.getDailyWaterIntake.observe(viewLifecycleOwner) {
            if (it != null) {
                if (!it.result.result.isNullOrEmpty()) {
                    val result = it.result.result[0]
                    Utilities.printData("result", result, true)
                    loadData(result)
                }
            }
        }

    }

    private fun setClickable() {

        binding.water.setProgressListener(object : WaveView.WaveProgressListener {
            @SuppressLint("SetTextI18n")
            override fun onProgress(isDone: Boolean, progress: Long, max: Long) {
                if (todayIntakePercentage > 99.99) {
                    binding.intake.text = "100 %"
                } else {
                    binding.intake.text = "$todayIntakePercentage %"
                }
            }
        })

        binding.imgAddTodayIntake.setOnClickListener {
            if (!Utilities.isNullOrEmptyOrZero(waterGoal.toString())) {
                openBottomSheet()
            } else {
                Utilities.toastMessageShort(
                    requireContext(),
                    resources.getString(R.string.PLEASE_SET_WATER_INTAKE_TARGET)
                )
            }
        }

        binding.cardAddIntake.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(Constants.GOAL, waterGoal)
            findNavController().navigate(
                R.id.action_waterTrackerDashboardFragment_to_calculateWaterIntakeFragment,
                bundle
            )
        }

        binding.cardTrackIntake.setOnClickListener {
            findNavController().navigate(R.id.action_waterTrackerDashboardFragment_to_trackWaterFragment)
        }

    }

    @SuppressLint("SetTextI18n")
    private fun loadData(data: GetDailyWaterIntakeModel.ResultData) {
        try {
            if (!viewModel.isFirstWaterTrackerVisit()) {
                if (!Utilities.isNullOrEmpty(data.totalWaterIntake)) {
                    binding.txtTodayWaterIntake.text =
                        "${data.totalWaterIntake!!.toDouble().roundToInt()}"
                    if (!Utilities.isNullOrEmpty(data.waterGoal)) {
                        todayIntakePercentage =
                            ((data.totalWaterIntake!!.toDouble() * 100) / data.waterGoal!!.toDouble()).roundToInt()
                        if (todayIntakePercentage > 99.99) {
                            binding.water.setProgressWithAnimation(99.99, 1500)
                        } else {
                            binding.water.setProgressWithAnimation(
                                todayIntakePercentage.toDouble(),
                                1500
                            )
                        }
                    }
                } else {
                    binding.txtTodayWaterIntake.text = "0"
                    todayIntakePercentage = 0
                    binding.water.setProgressWithAnimation(0.0, 1500)
                }
                if (!Utilities.isNullOrEmpty(data.waterGoal)) {
                    waterGoal = data.waterGoal!!.toDouble().toInt()
                    binding.txtTodayWaterGoal.text =
                        " / $waterGoal ${resources.getString(R.string.ML)}"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onIntakeClick(drinkCode: String, quantity: String) {
        viewModel.callGetDailyWaterIntakeApi(DateHelper.currentUTCDatetimeInMillisecAsString)
    }

    private fun tutorialWaterTrackerOld() {

        TapTargetSequence(requireActivity()).targets(
            TapTarget.forView(
                binding.imgCalculate,
                resources.getString(R.string.TITLE_CALCULATE_INTAKE),
                resources.getString(R.string.DESC_CALCULATE_INTAKE)
            )
                .outerCircleColor(R.color.primary_blue) // Specify a color for the outer circle
                .outerCircleAlpha(0.95f) // Specify the alpha amount for the outer circle
                .targetCircleColor(R.color.white) // Specify a color for the target circle
                .titleTextSize(22) // Specify the size (in sp) of the title text
                .descriptionTextSize(17) // Specify the size (in sp) of the description text
                .textColor(R.color.white) // Specify a color for both the title and description text
                .dimColor(R.color.black) // If set, will dim behind the view with 30% opacity of the given color
                .drawShadow(false) // Whether to draw a drop shadow or not
                .cancelable(true) // Whether tapping outside the outer circle dismisses the view
                .transparentTarget(true) // Specify whether the target is transparent (displays the content underneath)
                .targetRadius(40),  // Specify the target radius (in dp),

            TapTarget.forView(
                binding.imgAddTodayIntake,
                resources.getString(R.string.TITLE_LOG_TODAY_INTAKE),
                resources.getString(R.string.DESC_LOG_TODAY_INTAKE)
            )
                .outerCircleColor(R.color.primary_blue)
                .outerCircleAlpha(0.95f)
                .targetCircleColor(R.color.white)
                .titleTextSize(22)
                .descriptionTextSize(17)
                .textColor(R.color.white)
                .dimColor(R.color.black)
                .drawShadow(false)
                .cancelable(true)
                .transparentTarget(true)
                .targetRadius(30),

            TapTarget.forView(
                binding.imgTrack,
                resources.getString(R.string.TITLE_TRACK_INTAKE),
                resources.getString(R.string.DESC_TRACK_INTAKE)
            )
                .outerCircleColor(R.color.primary_blue)
                .outerCircleAlpha(0.95f)
                .targetCircleColor(R.color.white)
                .titleTextSize(22)
                .descriptionTextSize(17)
                .textColor(R.color.white)
                .dimColor(R.color.black)
                .drawShadow(false)
                .cancelable(true)
                .transparentTarget(true)
                .targetRadius(40)

        ).listener(object : TapTargetSequence.Listener {

            override fun onSequenceFinish() {
                viewModel.callGetDailyWaterIntakeApi(DateHelper.currentUTCDatetimeInMillisecAsString)
            }

            override fun onSequenceStep(lastTarget: TapTarget, targetClicked: Boolean) {
                // Perform action for the current target
            }

            override fun onSequenceCanceled(lastTarget: TapTarget) {}
        }).start()

    }

    private fun openBottomSheet() {
        val bottomSheet =
            AddWaterIntakeBottomSheet(DateHelper.currentDateAsStringyyyyMMdd, this, viewModel)
        bottomSheet.show(requireFragmentManager(), AddWaterIntakeBottomSheet.TAG)
    }

    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {}

}
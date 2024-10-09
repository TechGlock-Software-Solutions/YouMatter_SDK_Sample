package com.techglock.health.app.water_tracker.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
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
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.HeightWeightDialog
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentCalculateWaterIntakeBinding
import com.techglock.health.app.water_tracker.viewmodel.WaterTrackerViewModel
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class CalculateWaterIntakeFragment : BaseFragment(), HeightWeightDialog.OnDialogValueListener {

    private val viewModel: WaterTrackerViewModel by lazy {
        ViewModelProvider(this)[WaterTrackerViewModel::class.java]
    }
    private lateinit var binding: FragmentCalculateWaterIntakeBinding


    private var goal = 0
    var dailyIntake = ""
    private var weight: Double = 0.0
    private val minDuration = 10
    private val maxDuration = 120
    private val minTarget = 1000
    private val maxTarget = 10000

    private var selectedtab = 0
    private var isExcercise = false
    private var isNotification = true

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            // callback to Handle back button event
            val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_calculateWaterIntakeFragment_to_waterTrackerDashboardFragment)
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
        binding = FragmentCalculateWaterIntakeBinding.inflate(inflater, container, false)
        try {
            requireArguments().let {
                goal = it.getInt(Constants.GOAL, 0)
                Utilities.printLogError("goal----->$goal")
            }
            initialise()
            registerObserver()
            setClickable()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun initialise() {
        CleverTapHelper.pushEvent(requireContext(),CleverTapConstants.WATER_INTAKE_TARGET_SCREEN)
        viewModel.getMedicalProfileSummary()
        binding.layoutExcerciseDuration.visibility = View.GONE
        binding.txtWaterIntakeTarget.text = ""
        if (!Utilities.isNullOrEmptyOrZero(goal.toString())) {
            binding.txtCurrentTarget.text = "$goal ${resources.getString(R.string.ML)}"
        } else {
            binding.txtCurrentTarget.text = "--  ${resources.getString(R.string.ML)}"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun registerObserver() {

        viewModel.saveWaterIntakeGoal.observe(viewLifecycleOwner) {
            if (it != null) {
                val result = it.result
                Utilities.printData("result", result, true)
                val data = HashMap<String, Any>()
                data[CleverTapConstants.TARGET] = result.waterGoal!!
                when (result.type) {
                    "DEFAULT" -> {
                        CleverTapHelper.pushEventWithProperties(
                            requireContext(),
                            CleverTapConstants.DEFAULT_WATER_TARGET,
                            data
                        )
                        dailyIntake =
                            "${resources.getString(R.string.YOUR_DAILY_WATER_INTAKE)} <br/> ${
                                resources.getString(R.string.SHOULD_BE)
                            } : <a><B><font color='#00ceff'>${result.waterGoal} ${
                                resources.getString(
                                    R.string.ML
                                )
                            }</font></B></a>"
                    }

                    "CUSTOM" -> {
                        CleverTapHelper.pushEventWithProperties(
                            requireContext(),
                            CleverTapConstants.CUSTOM_WATER_TARGET,
                            data
                        )
                        dailyIntake =
                            "${resources.getString(R.string.YOU_HAVE_SET_DAILY_WATER_INTAKE)} <br/> ${
                                resources.getString(R.string.TARGET_TO)
                            } : <a><B><font color='#00ceff'>${result.waterGoal} ${
                                resources.getString(
                                    R.string.ML
                                )
                            }</font></B></a>"
                    }
                }
                binding.txtWaterIntakeTarget.text = Html.fromHtml(dailyIntake)
                binding.txtCurrentTarget.text =
                    "${result.waterGoal} ${resources.getString(R.string.ML)}"
            }
        }

        viewModel.medicalProfileSummary.observe(viewLifecycleOwner) {
            if (it != null) {
                Utilities.printLogError("MedicalProfileSummary--->${it.MedicalProfileSummary}")
                if (it.MedicalProfileSummary != null) {
                    if (!Utilities.isNullOrEmptyOrZero(it.MedicalProfileSummary!!.weight.toString())) {
                        weight = it.MedicalProfileSummary!!.weight
                        binding.txtWeight.text = "$weight  ${resources.getString(R.string.KG)}"
                    }
                }
            }
        }

    }

    private fun setClickable() {

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab) {
                selectedtab = tab.position
                Utilities.printLogError("position--->$selectedtab")
                when (selectedtab) {
                    0 -> {
                        binding.layoutDefaultTarget.visibility = View.VISIBLE
                        binding.layoutCustomTarget.visibility = View.GONE
                        binding.txtWaterIntakeTarget.text = ""
                        binding.btnCalculateIntake.text = resources.getString(R.string.CALCULATE)
                    }

                    1 -> {
                        binding.layoutDefaultTarget.visibility = View.GONE
                        binding.layoutCustomTarget.visibility = View.VISIBLE
                        binding.txtWaterIntakeTarget.text = ""
                        binding.btnCalculateIntake.text =
                            resources.getString(R.string.SET_CUSTOM_TARGET)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.swExcercise.setOnCheckedChangeListener { _, isChecked ->
            isExcercise = isChecked
            if (isChecked) {
                binding.txtCheckedExcercise.text = resources.getString(R.string.YES)
                binding.layoutExcerciseDuration.visibility = View.VISIBLE
            } else {
                binding.txtCheckedExcercise.text = resources.getString(R.string.NO)
                binding.layoutExcerciseDuration.visibility = View.GONE
            }
        }

        binding.swNotification.setOnCheckedChangeListener { _, isChecked ->
            isNotification = isChecked
            if (isChecked) {
                binding.txtCheckedNotification.text = resources.getString(R.string.YES)
            } else {
                binding.txtCheckedNotification.text = resources.getString(R.string.NO)
            }
        }

        binding.layoutWeight.setOnClickListener {
            viewModel.waterTrackerHelper.showWeightDialog(
                weight,
                this@CalculateWaterIntakeFragment,
                requireContext(),
                viewModel.getPreference("WEIGHT")
            )
        }

        binding.btnCalculateIntake.setOnClickListener {
            when (selectedtab) {
                0 -> {
                    validateDefaultTarget()
                }

                1 -> {
                    validateCustomTarget()
                }
            }
        }

    }

    private fun validateDefaultTarget() {
        val duration = binding.edtDuration.text.toString()
        if (Utilities.isNullOrEmptyOrZero(weight.toString())) {
            Utilities.toastMessageShort(
                requireContext(),
                resources.getString(R.string.PLEASE_SELECT_WEIGHT)
            )
        } else if (isExcercise) {
            if (Utilities.isNullOrEmptyOrZero(duration)) {
                Utilities.toastMessageShort(
                    requireContext(),
                    resources.getString(R.string.PLEASE_ENTER_EXERCISE_DURATION)
                )
            } else if (duration.toInt() < minDuration || duration.toInt() > maxDuration) {
                Utilities.toastMessageShort(
                    requireContext(),
                    "${resources.getString(R.string.PLEASE_ENTER_VALID_DURATION_BETWEEN)} $minDuration - $maxDuration ${
                        resources.getString(R.string.MIN)
                    }"
                )
            } else {
                viewModel.callSaveWaterIntakeGoalApi(
                    weight.toString(),
                    isExcercise.toString(),
                    duration,
                    isNotification.toString(),
                    DateHelper.currentUTCDatetimeInMillisecAsString,
                    "",
                    Constants.DEFAULT,
                    binding.txtWaterIntakeTarget
                )
            }
        } else {
            viewModel.callSaveWaterIntakeGoalApi(
                weight.toString(),
                isExcercise.toString(),
                "",
                isNotification.toString(),
                DateHelper.currentUTCDatetimeInMillisecAsString,
                "",
                Constants.DEFAULT,
                binding.txtWaterIntakeTarget
            )
        }
    }

    private fun validateCustomTarget() {
        val customIntake = binding.edtTarget.text.toString()
        if (Utilities.isNullOrEmptyOrZero(customIntake)) {
            Utilities.toastMessageShort(
                requireContext(),
                resources.getString(R.string.PLEASE_ENTER_WATER_INTAKE_QUANTITY)
            )
        } else if (customIntake.toInt() < minTarget || customIntake.toInt() > maxTarget) {
            Utilities.toastMessageShort(
                requireContext(),
                "${resources.getString(R.string.PLEASE_ENTER_VALID_WATER_INTAKE_QUANTITY_BETWEEN)} $minTarget - $maxTarget ${
                    resources.getString(R.string.ML)
                }"
            )
        } else {
            viewModel.callSaveWaterIntakeGoalApi(
                "",
                Constants.FALSE,
                "",
                isNotification.toString(),
                DateHelper.currentUTCDatetimeInMillisecAsString,
                customIntake,
                Constants.CUSTOM,
                binding.txtWaterIntakeTarget
            )
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onDialogValueListener(
        dialogType: String,
        height: String,
        weight: String,
        unit: String,
        visibleValue: String
    ) {
        viewModel.updateUserPreference(unit)
        this.weight = weight.toDouble()
        binding.txtWeight.text = "$visibleValue  ${unit.lowercase(Locale.ROOT)}"
        Utilities.printLog("Weight::visibleValue----> $weight , $visibleValue")

    }

}
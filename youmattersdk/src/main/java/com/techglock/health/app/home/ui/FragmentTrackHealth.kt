package com.techglock.health.app.home.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.extension.replaceFragment
import com.techglock.health.app.common.fitness.FitRequestCode
import com.techglock.health.app.common.fitness.FitnessDataManager
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.PermissionUtil
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentTrackHealthHomeBinding
import com.techglock.health.app.fitness_tracker.ui.FitnessDataActivity
import com.techglock.health.app.home.adapter.CalculatorsAdapter
import com.techglock.health.app.home.common.CalculatorModel
import com.techglock.health.app.home.common.DataHandler
import com.techglock.health.app.home.viewmodel.DashboardViewModel
import com.techglock.health.app.medication_tracker.MedicationHomeActivity
import com.techglock.health.app.model.toolscalculators.UserInfoModel
import com.techglock.health.app.model.waterTracker.GetDailyWaterIntakeModel
import com.techglock.health.app.records_tracker.HealthRecordsActivity
import com.techglock.health.app.repository.utils.Resource
import com.techglock.health.app.tools_calculators.ToolsCalculatorsHomeActivity
import com.techglock.health.app.track_parameter.ParameterHomeActivity
import com.techglock.health.app.water_tracker.WaterTrackerActivity
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.util.Date
import kotlin.math.roundToInt

@AndroidEntryPoint
class FragmentTrackHealth : BaseFragment(), CalculatorsAdapter.OnCalculatorClickListener {

    private lateinit var binding: FragmentTrackHealthHomeBinding
    private val viewModel: DashboardViewModel by lazy {
        ViewModelProvider(this)[DashboardViewModel::class.java]
    }

    private var stepGoal = Constants.DEFAULT_STEP_GOAL
    private var todayIntakePercentage = 0
    private var calculatorsAdapter: CalculatorsAdapter? = null
    private val permissionUtil = PermissionUtil
    private var fitnessDataManager: FitnessDataManager? = null
    private val permissionListener = object : PermissionUtil.AppPermissionListener {
        override fun isPermissionGranted(isGranted: Boolean) {
            Utilities.printLogError("$isGranted")
            if (isGranted) {
                viewModel.callAddFeatureAccessLogApi(
                    Constants.STORE_HEALTH_RECORDS,
                    "Store Health Records",
                    "VivantCore",
                    ""
                )
                startActivity(Intent(requireContext(), HealthRecordsActivity::class.java))

            }
        }
    }

    override fun getViewModel(): BaseViewModel = viewModel

    /*    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            (activity as HomeMainActivity).setDataReceivedListener(this)
        }*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrackHealthHomeBinding.inflate(inflater, container, false)
        initialise()
        setClickable()
        setObserver()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as HomeMainActivity).setToolbarInfo(
            1,
            showAppLogo = true,

            title = resources.getString(R.string.TRACK)
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.callGetDailyWaterIntakeApi(DateHelper.currentUTCDatetimeInMillisecAsString)
    }

    private fun initialise() {
        fitnessDataManager = FitnessDataManager(requireContext())
        binding.rvToolsCalculators.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        calculatorsAdapter = CalculatorsAdapter(this)
        binding.rvToolsCalculators.adapter = calculatorsAdapter

        //viewModel.getAllTrackersList()
        calculatorsAdapter!!.updateTrackersList(DataHandler(requireContext()).getTrackersList())
        //requestGoogleFit()
        //viewModel.fetchStepsGoal()
    }

    private fun setClickable() {
        binding.fitnessCV.setOnClickListener {
            (activity as HomeMainActivity).replaceFragment(
                AktivoScreenFragment(),
                frameId = R.id.main_container
            )
        }

        binding.cardWaterTracker.setOnClickListener {
            CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.HYDRATION_TRACKER)
            viewModel.callAddFeatureAccessLogApi(
                Constants.HYDRATION_TRACKER,
                "Hydration Tracker",
                "VivantCore",
                ""
            )
            startActivity(Intent(requireContext(), WaterTrackerActivity::class.java))

        }

        binding.cardTrackParam1.setOnClickListener {
            CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.TRACK_VITAL_PARAMETERS)
            viewModel.callAddFeatureAccessLogApi(
                Constants.TRACK_PARAMETERS,
                "Track Parameters",
                "VivantCore",
                ""
            )
//            viewModel.navigate(FragmentHomeMainDirections.actionDashboardFragmentToTrackParamActivity())
            startActivity(Intent(requireContext(), ParameterHomeActivity::class.java))
        }

        binding.cardMedication1.setOnClickListener {
            CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.MEDITATION_TRACKER)
            viewModel.callAddFeatureAccessLogApi(
                Constants.MEDICATION_TRACKER,
                "Medication Tracker",
                "VivantCore",
                ""
            )
            //viewModel.navigate(FragmentHomeMainDirections.actionDashboardFragmentToMedicationActivity())
            startActivity(Intent(requireContext(), MedicationHomeActivity::class.java))
        }

        binding.cardHealthRecords.setOnClickListener {
            val permissionResult =
                permissionUtil.checkStoragePermission(permissionListener, requireContext())
            if (permissionResult) {
                CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.HEALTH_RECORDS)
                viewModel.callAddFeatureAccessLogApi(
                    Constants.STORE_HEALTH_RECORDS,
                    "Store Health Records",
                    "VivantCore",
                    ""
                )
                startActivity(Intent(requireContext(), HealthRecordsActivity::class.java))
            }
        }

        binding.imgCalculatorsArrow.setOnClickListener {
            CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.TOOLS_CALCULATORS)
            viewModel.callAddFeatureAccessLogApi(
                Constants.TOOLS_AND_CALCULATORS,
                "Tools and Calculators",
                "VivantCore",
                ""
            )
            val intent = Intent(requireContext(), ToolsCalculatorsHomeActivity::class.java)
            intent.putExtra(Constants.FROM, Constants.HOME)
            intent.putExtra(Constants.TO, "DASH")
            startActivity(intent)

        }

    }

    private fun setObserver() {

        viewModel.getDailyWaterIntake.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {
                if (it.data!!.result.result.isNotEmpty()) {
                    val result = it.data.result.result[0]
                    Utilities.printData("result", result, true)
                    loadData(result)
                }
            }
        }
        viewModel.trackersList.observe(viewLifecycleOwner) {
            it?.let { calculatorsAdapter!!.updateTrackersList(it) }
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

        viewModel.addFeatureAccessLog.observe(viewLifecycleOwner) {}
    }

    @SuppressLint("SetTextI18n")
    private fun loadData(data: GetDailyWaterIntakeModel.ResultData) {
        try {
            if (!Utilities.isNullOrEmpty(data.totalWaterIntake)) {
                binding.txtWaterConsumed.text = "${data.totalWaterIntake!!.toDouble().roundToInt()}"
                if (!Utilities.isNullOrEmpty(data.waterGoal)) {
                    todayIntakePercentage =
                        ((data.totalWaterIntake.toDouble() * 100) / data.waterGoal!!.toDouble()).roundToInt()
                    binding.imgWaterTracker.setImageResource(
                        Utilities.getWaterDropImageByValue(
                            todayIntakePercentage.toDouble()
                        )
                    )

                    if (todayIntakePercentage > 99.99) {
                        binding.txtIntakePercent.text = "100 %"
                    } else {
                        binding.txtIntakePercent.text = "$todayIntakePercentage %"
                    }
                }
            } else {
                binding.txtWaterConsumed.text = "0"
                todayIntakePercentage = 0
                binding.imgWaterTracker.setImageResource(Utilities.getWaterDropImageByValue(0.0))
            }

            if (!Utilities.isNullOrEmpty(data.waterGoal)) {
                binding.txtWaterGoal.text = " / ${data.waterGoal!!.toDouble().toInt()} ${
                    resources.getString(
                        R.string.ML
                    )
                }"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCalculatorSelection(trackerDashboardModel: CalculatorModel, view: View) {
        viewModel.callAddFeatureAccessLogApi(
            Constants.TOOLS_AND_CALCULATORS,
            "Tools and Calculators",
            "VivantCore",
            ""
        )
        UserInfoModel.getInstance()!!.isDataLoaded = false
        val intent = Intent(requireContext(), ToolsCalculatorsHomeActivity::class.java)
        intent.putExtra(Constants.FROM, Constants.HOME)
        intent.putExtra(Constants.TO, trackerDashboardModel.code)
        startActivity(intent)
    }

    private fun proceedWithFitnessData() {
        try {
            if (fitnessDataManager!!.oAuthPermissionsApproved()) {
                viewModel.showProgressBar()
                fitnessDataManager!!.readHistoryData(Date(), Date()).addOnCompleteListener {
                    if (fitnessDataManager!!.fitnessDataArray.length() > 0) {
                        val todayData = fitnessDataManager!!.fitnessDataArray.getJSONObject(0)
                        todayData.put(Constants.TOTAL_GOAL, stepGoal.toString())
                        setupFitnessWidget(todayData)
                        viewModel.hideProgressBar()
                    } else {
                        Utilities.printLogError("Fitness Data not Available")
                        viewModel.hideProgressBar()
                    }
                }
            }
        } catch (e: Exception) {
            viewModel.hideProgressBar()
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupFitnessWidget(todayData: JSONObject) {
        try {
            var todayStepCount = 0
            var todayStepGoal = 0
            if (!todayData.isNull(Constants.STEPS_COUNT)) {
                todayStepCount = Integer.parseInt(todayData.optString(Constants.STEPS_COUNT))
                todayStepGoal = Integer.parseInt(todayData.optString(Constants.TOTAL_GOAL))
            }
            //binding.txtStepCount.text = todayStepCount.toString()
            //binding.txtStepGoal.text = " / $todayStepGoal"
            //binding.txtCaloriesCount.text = CalculateParameters.getCaloriesFromSteps(todayStepCount).toString()
            //binding.txtCaloriesGoal.text = " / ${CalculateParameters.getCaloriesFromSteps(todayStepGoal)} ${resources.getString(R.string.KCAL)}"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun requestGoogleFit() {
        if (fitnessDataManager!!.oAuthPermissionsApproved()) {
            Utilities.printLogError("oAuthPermissionsApproved---> true")
            proceedWithFitnessData()
            //scrollToTop()
        } else {
            Utilities.printLogError("oAuthPermissionsApproved---> false")
            fitnessDataManager!!.fitSignIn(FitRequestCode.READ_DATA)
        }
    }

    private fun navigateToFitnessTracker() {
        CleverTapHelper.pushEvent(requireContext(), CleverTapConstants.PHYSICAL_ACTIVITY_TRACKER)
        viewModel.callAddFeatureAccessLogApi(
            Constants.FITNESS_TRACKER,
            "Fitness Tracker",
            "VivantCore",
            ""
        )
        startActivity(Intent(requireContext(), FitnessDataActivity::class.java))
    }

    /*    override fun onGoogleAccountSelection(from: String) {
            Utilities.printLogError("from---> $from")
            when (from) {
                Constants.SUCCESS -> {
                    viewModel.fetchStepsGoal()
                }
                Constants.FAILURE -> {
                }
            }
        }*/

}
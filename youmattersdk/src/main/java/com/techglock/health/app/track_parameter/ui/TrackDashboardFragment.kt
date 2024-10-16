package com.techglock.health.app.track_parameter.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.NavigationConstants
import com.techglock.health.app.common.extension.openAnotherActivity
import com.techglock.health.app.common.fitness.FitRequestCode
import com.techglock.health.app.common.fitness.FitnessDataManager
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentTackDashboardBinding
import com.techglock.health.app.model.parameter.DashboardParamGridModel
import com.techglock.health.app.model.parameter.FitnessData
import com.techglock.health.app.track_parameter.ParameterHomeActivity
import com.techglock.health.app.track_parameter.adapter.DashboardGridAdapter
import com.techglock.health.app.track_parameter.viewmodel.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class TrackDashboardFragment : BaseFragment(), DashboardGridAdapter.ParameterSelectionListener,
    ParameterHomeActivity.OnGoogleAccountSelectListener {

    private val viewModel: DashboardViewModel by lazy {
        ViewModelProvider(this)[DashboardViewModel::class.java]
    }
    private lateinit var binding: FragmentTackDashboardBinding

    private var screen = ""
    private var dashboardGridAdapter: DashboardGridAdapter? = null
    private var fitnessDataManager: FitnessDataManager? = null
    private val data = FitnessData()

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            arguments?.let {
                screen = it.getString(Constants.SCREEN, "")!!
                Utilities.printLogError("screen--->$screen")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as ParameterHomeActivity).setDataReceivedListener(this)
    }

    override fun onGoogleAccountSelection(from: String) {
        Utilities.printLogError("from---> $from")
        when (from) {
            Constants.SUCCESS -> proceedWithFitnessData()
            Constants.FAILURE -> {
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTackDashboardBinding.inflate(inflater, container, false)

        try {
            initialise()
            setClickable()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    private fun initialise() {
        CleverTapHelper.pushEvent(
            requireContext(),
            CleverTapConstants.TRACK_PARAMETER_DASHBOARD_SCREEN
        )
        fitnessDataManager = FitnessDataManager(requireContext())
        binding.rvDashboardParametersGrid.layoutManager =
            androidx.recyclerview.widget.GridLayoutManager(context, 3)
        viewModel.listHistoryWithLatestRecord(data)
        dashboardGridAdapter = DashboardGridAdapter(requireContext(), this, viewModel)
        binding.rvDashboardParametersGrid.adapter = dashboardGridAdapter

        if (fitnessDataManager!!.oAuthPermissionsApproved()) {
            Utilities.printLogError("oAuthPermissionsApproved---> true")
            proceedWithFitnessData()
        } else {
            Utilities.printLogError("oAuthPermissionsApproved---> false")
            fitnessDataManager!!.fitSignIn(FitRequestCode.READ_DATA)
        }


        viewModel.listHistoryWithLatestRecord.observe(viewLifecycleOwner) {
            it?.let {
                dashboardGridAdapter!!.updateData(it)
            }
        }
    }

    private fun proceedWithFitnessData() {
        try {
            viewModel.showProgressBar()
            fitnessDataManager!!.readHistoryData(Date(), Date()).addOnCompleteListener {
                if (fitnessDataManager!!.fitnessDataArray.length() > 0) {
                    val todayData = fitnessDataManager!!.fitnessDataArray.getJSONObject(0)
                    Utilities.printData("TodayFitnessData", todayData)
                    data.recordDate = todayData.getString(Constants.RECORD_DATE)
                    data.stepsCount = todayData.getString(Constants.STEPS_COUNT)
                    data.calories = todayData.getString(Constants.CALORIES)
                    data.distance = todayData.get(Constants.DISTANCE).toString().toDouble()
                    data.activeTime = todayData.getString(Constants.ACTIVE_TIME).toString().toInt()
                    viewModel.listHistoryWithLatestRecord(data)
                    viewModel.hideProgressBar()
                } else {
                    Utilities.printLogError("Fitness Data not Available")
                    viewModel.hideProgressBar()
                }
            }
        } catch (e: Exception) {
            viewModel.hideProgressBar()
            e.printStackTrace()
        }
    }

    private fun setClickable() {

    }

    override fun onSelection(paramGridModel: DashboardParamGridModel) {
        Utilities.printLogError("paramCode---> ${paramGridModel.paramCode}")
        when (paramGridModel.paramCode) {
            "STEPS", "CAL" -> routeToActivityTracker()
            "BMI", "WEIGHT" -> routeToUpdateParameter("BMI", "BMI")
            "WAIST", "WHR" -> routeToUpdateParameter("WHR", "WHR")
            "BP", "PULSE" -> routeToUpdateParameter(
                "BLOODPRESSURE",
                resources.getString(R.string.BLOOD_PRESSURE)
            )

            "SUGAR" -> routeToUpdateParameter("DIABETIC", resources.getString(R.string.DIABETIC))
            "CHOL" -> routeToUpdateParameter("LIPID", resources.getString(R.string.LIPID))
            "HEMOGLOBIN" -> routeToUpdateParameter(
                "HEMOGRAM",
                resources.getString(R.string.HEMOGRAM)
            )

            "TSH" -> routeToUpdateParameter("THYROID", resources.getString(R.string.THYROID))
            "TOTAL_BILIRUBIN" -> routeToUpdateParameter(
                "LIVER",
                resources.getString(R.string.LIVER)
            )

            "SERUMCREATININE" -> routeToUpdateParameter(
                "KIDNEY",
                resources.getString(R.string.KIDNEY)
            )
//            "ADD" -> routeToUpdateParameter("BMI")
        }
    }

    private fun routeToUpdateParameter(profileCode: String, profileName: String) {
        viewModel.navigateParam(
            TrackDashboardFragmentDirections.actionDashboardFragmentToCurrentFragment(
                profileCode,
                profileName
            )
        )
    }

    private fun routeToActivityTracker() {
        openAnotherActivity(destination = NavigationConstants.FITNESS_HOME) {
            putString(Constants.FROM, Constants.TRACK_PARAMETER)
        }
    }

}
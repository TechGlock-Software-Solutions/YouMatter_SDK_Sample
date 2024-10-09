package com.techglock.health.app.home.ui.wellfie

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseActivity
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.NavigationConstants
import com.techglock.health.app.common.extension.openAnotherActivity
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.common.utils.HeightWeightDialog
import com.techglock.health.app.common.utils.ParameterDataModel
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ActivityBmiVitalsBinding
import com.techglock.health.app.home.common.WellfieSingleton
import com.techglock.health.app.home.viewmodel.DashboardViewModel
import com.techglock.health.app.repository.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class BmiVitalsActivity : BaseActivity(), HeightWeightDialog.OnDialogValueListener {

    private val viewModel: DashboardViewModel by lazy {
        ViewModelProvider(this)[DashboardViewModel::class.java]
    }
    private lateinit var binding: ActivityBmiVitalsBinding
    private val appColorHelper = AppColorHelper.instance!!
    private val wellfieSingleton = WellfieSingleton.getInstance()!!

    private var capturedHeight: Double = 0.0
    private var capturedWeight: Double = 0.0
    private var isChanged = false

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        //binding = DataBindingUtil.setContentView(this, R.layout.activity_bmi_vitals)
        binding = ActivityBmiVitalsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //binding.lifecycleOwner = this
        try {
            Utilities.printData("BmiVitalsList", wellfieSingleton.getBmiVitalsList(), true)
            for (i in wellfieSingleton.getBmiVitalsList()) {
                when (i.parameterCode) {
                    "HEIGHT" -> capturedHeight = i.value!!
                    "WEIGHT" -> capturedWeight = i.value!!
                }
            }
            Utilities.printLog("height--->$capturedHeight")
            Utilities.printLog("weight--->$capturedWeight")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        setupToolbar()
        initialise()
        setObserver()
    }

    private fun initialise() {

        binding.layHeight.setTitle(resources.getString(R.string.HEIGHT))
        binding.layWeight.setTitle(resources.getString(R.string.WEIGHT))
        binding.layHeight.setUnit(resources.getString(R.string.CM))
        binding.layWeight.setUnit(resources.getString(R.string.KG))

        if (!Utilities.isNullOrEmptyOrZero(capturedHeight.toString())) {
            binding.layHeight.setValue(capturedHeight.toInt().toString())
        }
        if (!Utilities.isNullOrEmptyOrZero(capturedWeight.toString())) {
            binding.layWeight.setValue(capturedWeight.toInt().toString())
        }

        binding.layHeight.setOnClickListener {
            showHeightDialog(capturedHeight.toInt(), "cm")
        }

        binding.layHeight.editText!!.setOnClickListener {
            showHeightDialog(capturedHeight.toInt(), "cm")
        }

        binding.layHeight.editText!!.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showHeightDialog(capturedHeight.toInt(), "cm")
            }
        }

        binding.layWeight.setOnClickListener {
            showWeightDialog(capturedWeight)
        }

        binding.layWeight.editText!!.setOnClickListener {
            showWeightDialog(capturedWeight)
        }

        binding.layWeight.editText!!.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showWeightDialog(capturedWeight)
            }
        }

        binding.btnNext.setOnClickListener {
            if (Utilities.isNullOrEmptyOrZero(capturedHeight.toString())) {
                Utilities.toastMessageShort(
                    this, resources.getString(R.string.PLEASE_SELECT_HEIGHT)
                )
            } else if (Utilities.isNullOrEmptyOrZero(capturedWeight.toString())) {
                Utilities.toastMessageShort(
                    this, resources.getString(R.string.PLEASE_SELECT_WEIGHT)
                )
            } else {
                viewModel.callWellfieGetSSOUrlApi()
            }
        }

    }

    private fun setObserver() {

        viewModel.wellfieGetSSOUrl.observe(this) {
            if (it.status == Resource.Status.SUCCESS) {
                if (!Utilities.isNullOrEmpty(it.data!!.sso.clientID) && !Utilities.isNullOrEmpty(it.data.sso.processDataAPIUrl) && !Utilities.isNullOrEmpty(
                        it.data.sso.token
                    ) && !Utilities.isNullOrEmpty(it.data.sso.wellfie_Socket_Url) && !Utilities.isNullOrEmpty(
                        it.data.sso.wellfie_Socket_Token
                    )
                ) {
                    if (isChanged) {
                        viewModel.saveParameter(
                            this,
                            capturedHeight.toString(),
                            capturedWeight.toString(),
                            it.data.sso.clientID!!,
                            it.data.sso.processDataAPIUrl!!,
                            it.data.sso.token!!,
                            it.data.sso.wellfie_Socket_Url!!,
                            it.data.sso.wellfie_Socket_Token!!
                        )
                    } else {
                        routeToWellfie(
                            it.data.sso.clientID!!,
                            it.data.sso.processDataAPIUrl!!,
                            it.data.sso.token!!,
                            it.data.sso.wellfie_Socket_Url!!,
                            it.data.sso.wellfie_Socket_Token!!
                        )
                    }
                }
            }
        }

        viewModel.saveParam.observe(this) {
            if (it.status == Resource.Status.SUCCESS) {
                if (it.data!!.records.isNotEmpty()) {
                    //Utilities.toastMessageShort(this,"Vitals Updated")
                    //routeToWellfie()
                }
            }
        }

    }

    private fun showHeightDialog(height: Int, savedUnit: String) {
        val data = ParameterDataModel()
        data.title = resources.getString(R.string.HEIGHT)
        data.value = " - - "
        data.finalValue = height.toString()
        if (savedUnit.equals("cm", true)) {
            data.unit = resources.getString(R.string.CM)
        } else {
            data.unit = resources.getString(R.string.FEET_INCH)
        }
        data.code = "HEIGHT"
        val heightWeightDialog = HeightWeightDialog(this, this, "Height", data)
        heightWeightDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        heightWeightDialog.show()
    }

    private fun showWeightDialog(weight: Double, savedUnit: String = "kg") {
        val data = ParameterDataModel()
        data.title = resources.getString(R.string.WEIGHT)
        data.value = " - - "
        if (Utilities.isNullOrEmptyOrZero(weight.toString())) {
            data.finalValue = "50.0"
        } else {
            data.finalValue = weight.toString()
        }

        if (!savedUnit.equals("kg", true)) {
            data.unit = resources.getString(R.string.LBS)
        } else {
            data.unit = resources.getString(R.string.KG)
        }
        data.code = "WEIGHT"
        val heightWeightDialog = HeightWeightDialog(this, this, "Weight", data)
        heightWeightDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        heightWeightDialog.show()
    }

    override fun onDialogValueListener(
        dialogType: String, height: String, weight: String, unit: String, visibleValue: String
    ) {
        if (dialogType.equals("Height", ignoreCase = true)) {
            if (capturedHeight != height.toDouble()) {
                isChanged = true
            }
            this.capturedHeight = height.toDouble()
            binding.layHeight.setValue(visibleValue)
            binding.layHeight.setUnit(unit.lowercase(Locale.ROOT))
            Utilities.printLog("Height::visibleValue----> $height , $visibleValue")
        } else {
            if (capturedWeight != weight.toDouble()) {
                isChanged = true
            }
            this.capturedWeight = weight.toDouble()
            binding.layWeight.setValue(visibleValue)
            binding.layWeight.setUnit(unit.lowercase(Locale.ROOT))
            Utilities.printLog("Weight::visibleValue----> $weight , $visibleValue")
        }
        Utilities.printLogError("isChanged---->$isChanged")
    }

    private fun routeToWellfie(
        clientID: String,
        processDataAPIUrl: String,
        processDataAPIToken: String,
        socketUrl: String,
        socketToken: String
    ) {
        openAnotherActivity(destination = NavigationConstants.WELLFIE_SCREEN) {
            putString(Constants.HEIGHT, capturedHeight.toString())
            putString(Constants.WEIGHT, capturedWeight.toString())
            putString(Constants.CLIENT_ID, clientID)
            putString(Constants.PROCESS_DATA_API_URL, processDataAPIUrl)
            putString(Constants.PROCESS_DATA_API_TOKEN, processDataAPIToken)
            putString(Constants.SOCKET_URL, socketUrl)
            putString(Constants.SOCKET_TOKEN, socketToken)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolBarView.toolbarCommon)
        binding.toolBarView.toolbarTitle.text = ""
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

}
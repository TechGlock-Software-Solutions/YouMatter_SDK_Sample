package com.techglock.health.app.home.ui.wellfie

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Html
import android.view.View
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseActivity
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.FirebaseConstants
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.common.utils.DefaultNotificationDialog
import com.techglock.health.app.common.utils.FirebaseHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.common.utils.showDialog
import com.techglock.health.app.databinding.ActivityWellfieBinding
import com.techglock.health.app.home.common.DataHandler.WellfieResultModel
import com.techglock.health.app.home.common.WellfieSingleton
import com.techglock.health.app.home.viewmodel.DashboardViewModel
import com.techglock.health.app.model.home.WellfieSaveVitalsModel.*
import com.techglock.health.app.repository.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class WellfieActivity : BaseActivity(), DefaultNotificationDialog.OnDialogValueListener {

    private val viewModel: DashboardViewModel by lazy {
        ViewModelProvider(this)[DashboardViewModel::class.java]
    }
    private lateinit var binding: ActivityWellfieBinding
    private val appColorHelper = AppColorHelper.instance!!
    private val wellfieSingleton = WellfieSingleton.getInstance()!!

    private val permissionManager: PermissionManager by lazy { PermissionManager() }

    //    private val socketManager = RppgSocketManager()
//    private lateinit var cameraManager: RppgCameraManager
    private var isRecording = false

    private lateinit var obj: JSONObject
    private var counter = 60
    private var countDownTimer: CountDownTimer? = null
    private var i = 0

    private var socketUrl = ""
    private var socketToken = ""
    private var clientID = ""
    private var processDataAPIUrl = ""
    private var processDataAPIToken = ""

    private var bpSystolic = ""
    private var bpDiastolic = ""
    private var bpm = ""
    private var rr = ""
    private var oxygen = ""
    private var stressStatus = ""
    private var bloodPressureStatus = ""
    private var stress = ""
    private var snr = ""
    private var ibi = ""
    private var rmssd = ""
    private var sdnn = ""
    private var height = ""
    private var weight = ""
    //private var bmi = ""

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        //binding = DataBindingUtil.setContentView(this, R.layout.activity_wellfie)
        binding = ActivityWellfieBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //binding.lifecycleOwner = this
        onBackPressedDispatcher.addCallback(this, onBackPressedCallBack)
        try {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

            if (intent.hasExtra(Constants.SOCKET_URL)) {
                socketUrl = intent.getStringExtra(Constants.SOCKET_URL)!!
            }
            if (intent.hasExtra(Constants.SOCKET_TOKEN)) {
                socketToken = intent.getStringExtra(Constants.SOCKET_TOKEN)!!
                //socketToken = "8122748a-802f-45d7-938b-3965a2a07d78"
            }
            if (intent.hasExtra(Constants.CLIENT_ID)) {
                clientID = intent.getStringExtra(Constants.CLIENT_ID)!!
            }
            if (intent.hasExtra(Constants.PROCESS_DATA_API_URL)) {
                processDataAPIUrl = intent.getStringExtra(Constants.PROCESS_DATA_API_URL)!!
            }
            if (intent.hasExtra(Constants.PROCESS_DATA_API_TOKEN)) {
                processDataAPIToken = intent.getStringExtra(Constants.PROCESS_DATA_API_TOKEN)!!
            }
            if (intent.hasExtra(Constants.HEIGHT)) {
                height = intent.getStringExtra(Constants.HEIGHT)!!
            }
            if (intent.hasExtra(Constants.WEIGHT)) {
                weight = intent.getStringExtra(Constants.WEIGHT)!!
            }
            //bmi = CalculateParameters.roundOffPrecision(CalculateParameters.getBMI(height.toDouble(), weight.toDouble()), 1).toString()
            Utilities.printLogError("socketUrl--->$socketUrl")
            Utilities.printLogError("socketToken--->$socketToken")
            Utilities.printLogError("clientID--->$clientID")
            Utilities.printLogError("processDataAPIUrl--->$processDataAPIUrl")
            Utilities.printLogError("processDataAPIToken--->$processDataAPIToken")
            Utilities.printLogError("height--->$height")
            Utilities.printLogError("weight--->$weight")
            setupToolbar()
            initialise()
            setClickable()
            setObserver()
            FirebaseHelper.logScreenEvent(FirebaseConstants.WELLFIE_VITAL_SCAN_SCREEN)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initialise() {
        val strLearnMore =
            "<u><a><B><font color='#00cfff'>" + resources.getString(R.string.LEARN_MORE) + "</font></B></a></u>"
        binding.txtDisclaimer.text = Html.fromHtml(
            resources.getString(R.string.WELLFIE_DISCLAIMER_LABEL) + " " + strLearnMore
        )

        val age = viewModel.age.split(" ").toTypedArray()[0]
        val gender = Utilities.getDisplayGender(viewModel.gender).lowercase()
        val height = height.toDouble().toInt().toString()
        val weight = weight.toDouble().toInt().toString()
        //val finalSocketUrl = "$socketUrl?authToken=$socketToken&age=$age&sex=$gender&height=$height&weight=$weight"
        val finalSocketUrl =
            "$socketUrl?age=$age&sex=$gender&height=$height&weight=$weight&$socketToken"
        Utilities.printLogError("FinalSocketUrl--->$finalSocketUrl")

        //socket
        /*socketManager.init(this@WellfieActivity, finalSocketUrl, object : SocketCallback {

            override fun onEvent(message: String) {
                // socket results here
                Utilities.printLogError("onEventData--->$message")
                sendEvent(message)
            }

            override fun onError(error: String) {
                Utilities.printLogError("onError--->$error")
                // socket errors here
            }

            override fun onAuthError() {
                // auth token expired
                Utilities.printLogError("************onAuthError************")
            }

        })*/

        //binding.control.visibility = View.GONE
        //binding.control.text = resources.getString(R.string.STOP)
        binding.timerTv.visibility = View.VISIBLE
        binding.lblSec.visibility = View.VISIBLE
        permissionManager.checkPermission(this@WellfieActivity) {
            initCameraManager()
        }

    }

    private fun setClickable() {

        binding.control.apply {
            setOnClickListener {
                isRecording = !isRecording
                if (isRecording) {
                    startRecording()
                    //binding.control.visibility = View.GONE
                    //binding.control.text = resources.getString(R.string.STOP)
                } else {
                    stopRecording()
                    //binding.control.text = resources.getString(R.string.START)
                    binding.control.visibility = View.VISIBLE
                }
            }
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

    private fun setObserver() {

        /*        viewModel.wellfieGetSSOUrl.observe(this) {
                    if (it.status == Resource.Status.SUCCESS) {
                        if ( !Utilities.isNullOrEmpty(it.sso.clientID)
                            && !Utilities.isNullOrEmpty(it.sso.processDataAPIUrl)
                            && !Utilities.isNullOrEmpty(it.sso.token) ) {
                            callPostProcessorApi(clientID,processDataAPIUrl,processDataAPIToken)
                        }
                    }
                }*/

        viewModel.wellfieSaveVitals.observe(this) {
            if (it.status == Resource.Status.SUCCESS) {
                if (it.data!!.isSave!!.equals(Constants.TRUE, ignoreCase = true)) {

                    val intent = Intent(this, WellfieResultActivity::class.java)
                    intent.putExtra(Constants.FROM, "")
                    startActivity(intent)
                }
            }
        }

        viewModel.postProcessResponse.observe(this) {
            if (it != null && it != "") {
                val jsonData = JSONObject(it)
                if (jsonData.has("data")) {
                    val data = jsonData.getJSONObject("data")

                    if (data.length() != 0) {
                        val objBPM = data.getJSONObject("BPM")
                        val objRR = data.getJSONObject("RR")
                        val objOxygen = data.getJSONObject("Oxygen")
                        val objStressStatus = data.getJSONObject("StressStatus")
                        //val objBloodPressure =  data.getJSONObject("BloodPressure")
                        val objBloodPressureStatus = data.getJSONObject("BloodPressureStatus")
                        val objSystolic = data.getJSONObject("systolic")
                        val objDiastolic = data.getJSONObject("diastolic")
                        val objStressIndex = data.getJSONObject("stressIndex")
                        val objBmiValue = jsonData.getJSONObject("data").getJSONObject("bmi_value")

                        //val bpColor = Utilities.getWellfieBpColorByObservation(objBloodPressureStatus.getString("value"))
                        val bpColor = objBloodPressureStatus.getString("HexColor")

                        //List to store in backend
                        val wellfieParameters: ArrayList<WellfieParameter> = arrayListOf()
                        wellfieParameters.add(
                            WellfieParameter(
                                "BP_SYS",
                                "Systolic",
                                objSystolic.getString("value"),
                                "",
                                objBloodPressureStatus.getString("indication"),
                                bpColor
                            )
                        )
                        wellfieParameters.add(
                            WellfieParameter(
                                "BP_DIA",
                                "Diastolic",
                                objDiastolic.getString("value"),
                                "",
                                objBloodPressureStatus.getString("indication"),
                                bpColor
                            )
                        )
                        wellfieParameters.add(
                            WellfieParameter(
                                "STRESS_INDEX",
                                "Stress Index",
                                objStressIndex.getString("value"),
                                "",
                                objStressStatus.getString("value"),
                                objStressIndex.getString("HexColor")
                            )
                        )
                        wellfieParameters.add(
                            WellfieParameter(
                                "HEART_RATE",
                                "Heart Rate",
                                objBPM.getString("value"),
                                "BPM",
                                "",
                                objBPM.getString("HexColor")
                            )
                        )
                        wellfieParameters.add(
                            WellfieParameter(
                                "BREATHING_RATE",
                                "Breathing Rate",
                                objRR.getString("value"),
                                "RPM",
                                "",
                                objRR.getString("HexColor")
                            )
                        )
                        wellfieParameters.add(
                            WellfieParameter(
                                "BLOOD_OXYGEN",
                                "Blood Oxygen",
                                objOxygen.getString("value"),
                                "%",
                                "",
                                objOxygen.getString("HexColor")
                            )
                        )
                        wellfieParameters.add(
                            WellfieParameter(
                                "BMI",
                                "BMI",
                                objBmiValue.getString("value"),
                                "",
                                "",
                                objBmiValue.getString("HexColor")
                            )
                        )

                        //List to Display on UI
                        val parametersList: MutableList<WellfieResultModel> = mutableListOf()
                        parametersList.add(
                            WellfieResultModel(
                                1,
                                "BP",
                                "Blood Pressure",
                                "${objSystolic.getString("value")} / ${objDiastolic.getString("value")}",
                                objBloodPressureStatus.getString("indication"),
                                bpColor,
                                objBloodPressureStatus.getString("Tooltips")
                            )
                        )
                        parametersList.add(
                            WellfieResultModel(
                                2,
                                "STRESS_INDEX",
                                "Stress Index",
                                stress,
                                objStressStatus.getString("value"),
                                objStressIndex.getString("HexColor"),
                                objStressStatus.getJSONArray("Tooltips").toString()
                            )
                        )
                        parametersList.add(
                            WellfieResultModel(
                                3,
                                "HEART_RATE",
                                "Heart Rate",
                                objBPM.getString("value"),
                                "",
                                objBPM.getString("HexColor"),
                                objBPM.getJSONArray("Tooltips").toString()
                            )
                        )
                        parametersList.add(
                            WellfieResultModel(
                                4,
                                "BREATHING_RATE",
                                "Breathing Rate",
                                objRR.getString("value"),
                                "",
                                objRR.getString("HexColor"),
                                objRR.getJSONArray("Tooltips").toString()
                            )
                        )
                        parametersList.add(
                            WellfieResultModel(
                                5,
                                "BLOOD_OXYGEN",
                                "Blood Oxygen",
                                objOxygen.getString("value"),
                                "",
                                objOxygen.getString("HexColor"),
                                objOxygen.getJSONArray("Tooltips").toString()
                            )
                        )
                        parametersList.add(
                            WellfieResultModel(
                                6,
                                "BMI",
                                "BMI",
                                objBmiValue.getString("value"),
                                "",
                                objBmiValue.getString("HexColor")
                            )
                        )

                        //Utilities.printData("WellfieResultList",parametersList)
                        wellfieSingleton.setWellfieResultList(parametersList)

                        viewModel.callWellfieSaveVitalsApi(wellfieParameters, height, weight)
                    } else {
                        Utilities.toastMessageShort(this, jsonData.getString("message"))
                    }
                } else {
                    if (jsonData.has("detail")) {
                        Utilities.toastMessageLong(this, jsonData.getString("detail"))
                    }
                }
            }
        }

    }

    private fun initCameraManager() {
        /*try {
            //binding.control.text = resources.getString(R.string.STOP)
            cameraManager = RppgCameraManager(
                lifecycleOwner = this,
                camera = binding.camera,
                cameraConfig = CameraConfig(isDebug = true)
            )

            val manager = RppgCoreManager().apply {
                init(30, RppgCore.CalculationMode.BGR.mode)
            }
            Utilities.printLogError("RppgCommon---version--->${manager.getVersion()}")

            // camera
            cameraManager.setListener { data ->
                handleCameraManagerData(data, manager)
            }
            //startRecording()
        } catch (e: Exception) {
            e.printStackTrace()
        }*/
    }

    /*private fun handleCameraManagerData(data: FaceData, manager: RppgCoreManager) {
        with(data) {
            val result = manager.track(width, height, byteArray, timestamp, floatArray)
            Utilities.printLogError("Rppg-----timestamp:--->$timestamp result: $result")
            if (result == null) {
                setApiMsg("", "")
            }
            // send a result to server via socket
            socketManager.update(result, timestamp)
        }
    }*/

    private fun startRecording() {
        binding.control.text = resources.getString(R.string.STOP)
        bpSystolic = "0"
        bpDiastolic = "0"
        stress = "0"
        bpm = "0"
        rr = "0"
        oxygen = "0"
        stressStatus = ""
        bloodPressureStatus = ""
        snr = "0"
        ibi = "0"
        rmssd = "0"
        sdnn = "0"
        binding.indicatorProgress.setValue(i.toFloat())

        countDownTimer = object : CountDownTimer(60000, 1000) {

            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                if (isRecording) {
                    i++
                    binding.indicatorProgress.setValue((i * 100 / (60000 / 1000)).toFloat())
                    binding.timerTv.text = "$counter"
                    counter--
                }
            }

            override fun onFinish() {
                binding.control.text = resources.getString(R.string.START)
                //binding.timerTv.visibility = View.GONE
                //binding.lblSec.text = resources.getString(R.string.FINISH)
                i++
                binding.indicatorProgress.setValue(100f)
//                cameraManager.stopRecording()
//                socketManager.stopSocket()
                //gatherVitalsData()
                //viewModel.callWellfieGetSSOUrlApi()
                callPostProcessorApi(clientID, processDataAPIUrl, processDataAPIToken)
            }
        }.start()
//        socketManager.startSocket(socketToken)
//        cameraManager.startRecording()
    }

    private fun stopRecording() {
        countDownTimer!!.cancel()
        binding.control.text = resources.getString(R.string.START)
        i = 0
        counter = 60
        binding.timerTv.text = "$counter"
        binding.indicatorProgress.setValue(0f)
//        cameraManager.stopRecording()
//        socketManager.stopSocket()
        setApiMsg("", "")
    }

    private fun sendEvent(message: String) {
        try {
            obj = JSONObject(message)
            when (obj.getString("messageType")) {

                "MEASUREMENT_STATUS" -> {
                    setApiMsg(
                        obj.getJSONObject("data").getString("statusCode"),
                        obj.getJSONObject("data").getString("statusMessage")
                    )
                }

                "MOVING_WARNING" -> {
                    setApiMsg("MOVING_WARNING", "")
                }
                /*               "UNSTABLE_CONDITIONS_WARNING" -> {
                                   setApiMsg( "UNSTABLE_CONDITIONS_WARNING","" )
                               }*/
                "BLOOD_PRESSURE" -> {
                    bpSystolic = obj.getJSONObject("data").getString("systolic")
                    bpDiastolic = obj.getJSONObject("data").getString("diastolic")
                }

                "SIGNAL_QUALITY" -> {
                    snr = obj.getJSONObject("data").getString("snr")
                }

                "STRESS_INDEX" -> {
                    stress = obj.getJSONObject("data").getString("stress")
                }

                "MEASUREMENT_MEAN_DATA" -> {
                    bpm = obj.getJSONObject("data").getString("bpm")
                    rr = obj.getJSONObject("data").getString("rr")
                    oxygen = obj.getJSONObject("data").getString("oxygen")
                    stressStatus = obj.getJSONObject("data").getString("stressStatus")
                    bloodPressureStatus = obj.getJSONObject("data").getString("bloodPressureStatus")
                }

                "HRV_METRICS" -> {
                    ibi = obj.getJSONObject("data").getString("ibi")
                    rmssd = obj.getJSONObject("data").getString("rmssd")
                    sdnn = obj.getJSONObject("data").getString("sdnn")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            initCameraManager()
        }*/

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Utilities.printLogError("requestCode--->$requestCode")
        Utilities.printLogError("grantResults--->${grantResults}")
        when (requestCode) {
            Constants.CAMERA_SELECT_CODE -> {
                if (permissionManager.hasPermissionsGranted(this, permissions)) {
                    initCameraManager()
                } else {
                    Utilities.toastMessageShort(
                        this,
                        resources.getString(R.string.ERROR_CAMERA_PERMISSION)
                    )
                }
            }
        }
    }

    private fun setApiMsg(code: String, msg: String) {
        runOnUiThread {
            // Stuff that updates the UI
            binding.txtError.text = viewModel.getWellfieStatusMsg(code, msg)
            /*            when (code) {
                            "SUCCESS" -> {
                                binding.txtError.setTextColor( ContextCompat.getColor(this,R.color.state_success) )
                            }
                            "CALIBRATING" -> {
                                binding.txtError.setTextColor( ContextCompat.getColor(this,R.color.vivant_marigold) )
                            }
                            else -> {
                                binding.txtError.setTextColor( ContextCompat.getColor(this,R.color.vivantRed) )
                            }
                        }*/
        }
    }

    private fun callPostProcessorApi(clientId: String, url: String, auth: String) {
        try {
            val jsonObject = JSONObject()
            jsonObject.put("client_id", clientId)
            jsonObject.put("uuid", viewModel.personId)
            jsonObject.put("BPM", bpm)
            jsonObject.put("RR", rr)
            jsonObject.put("Oxygen", oxygen)
            jsonObject.put("systolic", bpSystolic)
            jsonObject.put("diastolic", bpDiastolic)
            jsonObject.put("BloodPressureStatus", bloodPressureStatus)
            if (stress.toDouble() < 0) {
                jsonObject.put("stressIndex", 0)
            } else {
                jsonObject.put("stressIndex", Utilities.roundOffPrecision(stress.toDouble(), 2))
            }
            jsonObject.put("StressStatus", stressStatus)
            jsonObject.put("ibi", Utilities.roundOffPrecision(ibi.toDouble(), 2).toString())
            jsonObject.put("rmssd", Utilities.roundOffPrecision(rmssd.toDouble(), 2).toString())
            jsonObject.put("sdnn", Utilities.roundOffPrecision(sdnn.toDouble(), 2).toString())
            jsonObject.put("SNR", Utilities.roundOffPrecision(snr.toDouble(), 2).toString())
            jsonObject.put("gender", Utilities.getDisplayGender(viewModel.gender))
            jsonObject.put("age", viewModel.age.split(" ").toTypedArray()[0])
            jsonObject.put("height", height.toDouble().toInt().toString())
            jsonObject.put("weight", weight.toDouble().toInt().toString())
            jsonObject.put(
                "additional_data",
                "Platform: ANDROID | Device: ${Build.BRAND + "-" + Build.MODEL}"
            )
            jsonObject.put("platform", "ANDROID")

            viewModel.callWellfiePostProcessorApi(url, auth, jsonObject)
        } catch (e: Exception) {
            e.printStackTrace()
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
            onBackPressedDispatcher.onBackPressed()
        }
    }


    private val onBackPressedCallBack = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            wellfieSingleton.clearData()
            if (isRecording) {
                stopRecording()
            }
            finish()
        }

    }

    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {}

    /*    @SuppressLint("SetTextI18n")
        private fun gatherVitalsData() {
            countDownTimer!!.cancel()
            i = 0
            counter = 60
            val parametersList: MutableList<WellfieResultModel> = mutableListOf()
            parametersList.add(WellfieResultModel(1,"BP","Blood Pressure","$bpSystolic / $bpDiastolic",bloodPressureStatus ))
            parametersList.add(WellfieResultModel( 2,"STRESS_INDEX","Stress Index",stress,stressStatus ))
            parametersList.add(WellfieResultModel(3, "HEART_RATE","Heart Rate", bpm))
            parametersList.add(WellfieResultModel( 4,"BREATHING_RATE","Breathing Rate", rr))
            parametersList.add(WellfieResultModel( 5,"BLOOD_OXYGEN","Blood Oxygen", oxygen))
            parametersList.add(WellfieResultModel( 6,"BMI","BMI",bmi,""))

            Utilities.printData("WellfieResultList",parametersList)
            wellfieSingleton.setWellfieResultList(parametersList)
            viewModel.callWellfieGetSSOUrlApi()

            openAnotherActivity(destination = NavigationConstants.WELLFIE_RESULT_SCREEN) {
             putString(Constants.FROM,"")
            }
        }

        private fun gatherAndSaveVitalsData() {
            val wellfieParameters : ArrayList<WellfieParameter> = arrayListOf()
            wellfieParameters.add(WellfieParameter( "BP_SYS","Systolic",bpSystolic,"",bloodPressureStatus,"#d3d3d3" ))
            wellfieParameters.add(WellfieParameter( "BP_DIA","Diastolic",bpDiastolic,"",bloodPressureStatus,"#d3d3d3"))
            wellfieParameters.add(WellfieParameter( "STRESS_INDEX","Stress Index",stress,"",stressStatus,"#fcb4c1" ))
            wellfieParameters.add(WellfieParameter( "HEART_RATE","Heart Rate",bpm,"BPM","","#f1c72f" ))
            wellfieParameters.add(WellfieParameter( "BREATHING_RATE","Breathing Rate",rr,"RPM","","#00cfff" ))
            wellfieParameters.add(WellfieParameter( "BLOOD_OXYGEN","Blood Oxygen",oxygen,"%","","#00c8a0" ))

            viewModel.callWellfieSaveVitalsApi(wellfieParameters,height,weight)
        }*/

}
package com.techglock.health.app.home.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.PreferenceConstants
import com.techglock.health.app.common.utils.CleverTapHelper
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.Event
import com.techglock.health.app.common.utils.FileUtils
import com.techglock.health.app.common.utils.LocaleHelper
import com.techglock.health.app.common.utils.PreferenceUtils
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.common.utils.showDialog
import com.techglock.health.app.home.common.SudPolicyApiService
import com.techglock.health.app.home.domain.SudLifePolicyManagementUseCase
import com.techglock.health.app.home.ui.sudLifePolicy.SudLifePolicyDashboardFragment
import com.techglock.health.app.model.security.GenerateOtpModel
import com.techglock.health.app.model.sudLifePolicy.PolicyProductsModel
import com.techglock.health.app.model.sudLifePolicy.SudFundDetailsModel
import com.techglock.health.app.model.sudLifePolicy.SudGroupCOIModel
import com.techglock.health.app.model.sudLifePolicy.SudKYPModel
import com.techglock.health.app.model.sudLifePolicy.SudKypPdfModel
import com.techglock.health.app.model.sudLifePolicy.SudKypTemplateModel
import com.techglock.health.app.model.sudLifePolicy.SudPMJJBYCoiBaseModel
import com.techglock.health.app.model.sudLifePolicy.SudPayPremiumModel
import com.techglock.health.app.model.sudLifePolicy.SudPolicyByMobileNumberModel
import com.techglock.health.app.model.sudLifePolicy.SudPolicyDetailsByPolicyNumberModel
import com.techglock.health.app.model.sudLifePolicy.SudReceiptDetailsModel
import com.techglock.health.app.model.sudLifePolicy.SudRenewalPremiumModel
import com.techglock.health.app.model.sudLifePolicy.SudRenewalPremiumReceiptModel
import com.techglock.health.app.repository.utils.Resource
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class SudLifePolicyViewModel @Inject constructor(
    application: Application,
    private val sudLifePolicyManagementUseCase: SudLifePolicyManagementUseCase,
    private val preferenceUtils: PreferenceUtils,
    val context: Context?
) : BaseViewModel(application) {

    private val localResource =
        LocaleHelper.getLocalizedResources(context!!, Locale(LocaleHelper.getLanguage(context)))!!
    var email = preferenceUtils.getPreference(PreferenceConstants.EMAIL, "")
    var phone = preferenceUtils.getPreference(PreferenceConstants.PHONE, "")
    var policyMobileNumber =
        preferenceUtils.getPreference(PreferenceConstants.POLICY_MOBILE_NUMBER, "")
    var dob = preferenceUtils.getPreference(PreferenceConstants.DOB, "")
    private val fileUtils = FileUtils

    private var policyProductsSource: LiveData<Resource<PolicyProductsModel.PolicyProductsResponse>> =
        MutableLiveData()
    private val _policyProducts =
        MediatorLiveData<Resource<PolicyProductsModel.PolicyProductsResponse>>()
    val policyProducts: LiveData<Resource<PolicyProductsModel.PolicyProductsResponse>> get() = _policyProducts

    private var sudPolicyByMobileNumberSource: LiveData<Resource<SudPolicyByMobileNumberModel.SudPolicyByMobileNumberResponse>> =
        MutableLiveData()
    private val _sudPolicyByMobileNumber =
        MediatorLiveData<Resource<SudPolicyByMobileNumberModel.SudPolicyByMobileNumberResponse>>()
    val sudPolicyByMobileNumber: LiveData<Resource<SudPolicyByMobileNumberModel.SudPolicyByMobileNumberResponse>> get() = _sudPolicyByMobileNumber

    private var policyDetailsByPolicyNumberSource: LiveData<Resource<SudPolicyDetailsByPolicyNumberModel.SudPolicyByMobileNumberResponse>> =
        MutableLiveData()
    private val _policyDetailsByPolicyNumber =
        MediatorLiveData<Resource<SudPolicyDetailsByPolicyNumberModel.SudPolicyByMobileNumberResponse>>()
    val policyDetailsByPolicyNumber: LiveData<Resource<SudPolicyDetailsByPolicyNumberModel.SudPolicyByMobileNumberResponse>> get() = _policyDetailsByPolicyNumber

    private var sudFundDetailsSource: LiveData<Resource<SudFundDetailsModel.SudFundDetailsResponse>> =
        MutableLiveData()
    private val _sudFundDetails =
        MediatorLiveData<Resource<SudFundDetailsModel.SudFundDetailsResponse>>()
    val sudFundDetails: LiveData<Resource<SudFundDetailsModel.SudFundDetailsResponse>> get() = _sudFundDetails

    private var sudReceiptDetailsSource: LiveData<Resource<SudReceiptDetailsModel.SudReceiptDetailsResponse>> =
        MutableLiveData()
    private val _sudReceiptDetails =
        MediatorLiveData<Resource<SudReceiptDetailsModel.SudReceiptDetailsResponse>>()
    val sudReceiptDetails: LiveData<Resource<SudReceiptDetailsModel.SudReceiptDetailsResponse>> get() = _sudReceiptDetails

    private var sudPMJJBYCoiBaseSource: LiveData<Resource<SudPMJJBYCoiBaseModel.SudPMJJBYCoiBaseResponse>> =
        MutableLiveData()
    private val _sudPMJJBYCoiBase =
        MediatorLiveData<Resource<SudPMJJBYCoiBaseModel.SudPMJJBYCoiBaseResponse>>()
    val sudPMJJBYCoiBase: LiveData<Resource<SudPMJJBYCoiBaseModel.SudPMJJBYCoiBaseResponse>> get() = _sudPMJJBYCoiBase

    private var sudGetKypTemplateSource: LiveData<Resource<SudKypTemplateModel.SudKypTemplateResponse>> =
        MutableLiveData()
    private val _sudGetKypTemplate =
        MediatorLiveData<Resource<SudKypTemplateModel.SudKypTemplateResponse>>()
    val sudGetKypTemplate: LiveData<Resource<SudKypTemplateModel.SudKypTemplateResponse>> get() = _sudGetKypTemplate

    private var sudKypPdfSource: LiveData<Resource<SudKypPdfModel.SudKypPdfResponse>> =
        MutableLiveData()
    private val _sudKypPdf = MediatorLiveData<Resource<SudKypPdfModel.SudKypPdfResponse>>()
    val sudKypPdf: LiveData<Resource<SudKypPdfModel.SudKypPdfResponse>> get() = _sudKypPdf

    private var sudGroupCoiApiSource: LiveData<Resource<SudGroupCOIModel.SudGroupCOIResponse>> =
        MutableLiveData()
    private val _sudGroupCoiApi = MediatorLiveData<Resource<SudGroupCOIModel.SudGroupCOIResponse>>()
    val sudGroupCoiApi: LiveData<Resource<SudGroupCOIModel.SudGroupCOIResponse>> get() = _sudGroupCoiApi

    private var sudGetPayPremiumSource: LiveData<Resource<SudPayPremiumModel.SudPayPremiumResponse>> =
        MutableLiveData()
    private val _sudGetPayPremium =
        MediatorLiveData<Resource<SudPayPremiumModel.SudPayPremiumResponse>>()
    val sudGetPayPremium: LiveData<Resource<SudPayPremiumModel.SudPayPremiumResponse>> get() = _sudGetPayPremium

    private var otpGenerateSource: LiveData<Resource<GenerateOtpModel.GenerateOTPResponse>> =
        MutableLiveData()
    private val _otpGenerateData =
        MediatorLiveData<Resource<GenerateOtpModel.GenerateOTPResponse>>()
    val otpGenerateData: LiveData<Resource<GenerateOtpModel.GenerateOTPResponse>> get() = _otpGenerateData

    private var otpValidateSource: LiveData<Resource<GenerateOtpModel.GenerateOTPResponse>> =
        MutableLiveData()
    private val _otpValidateData =
        MediatorLiveData<Resource<GenerateOtpModel.GenerateOTPResponse>>()
    val otpValidateData: LiveData<Resource<GenerateOtpModel.GenerateOTPResponse>> get() = _otpValidateData

    private var sudKYPSource: LiveData<Resource<SudKYPModel.SudKYPResponse>> = MutableLiveData()
    private val _sudKYP = MediatorLiveData<Resource<SudKYPModel.SudKYPResponse>>()
    val sudKYP: LiveData<Resource<SudKYPModel.SudKYPResponse>> get() = _sudKYP

    private var sudRenewalPremiumSource: LiveData<Resource<SudRenewalPremiumModel.SudRenewalPremiumResponse>> =
        MutableLiveData()
    private val _sudRenewalPremium =
        MediatorLiveData<Resource<SudRenewalPremiumModel.SudRenewalPremiumResponse>>()
    val sudRenewalPremium: LiveData<Resource<SudRenewalPremiumModel.SudRenewalPremiumResponse>> get() = _sudRenewalPremium

    private var sudRenewalPremiumReceiptSource: LiveData<Resource<SudRenewalPremiumReceiptModel.SudRenewalPremiumReceiptResponse>> =
        MutableLiveData()
    private val _sudRenewalPremiumReceipt =
        MediatorLiveData<Resource<SudRenewalPremiumReceiptModel.SudRenewalPremiumReceiptResponse>>()
    val sudRenewalPremiumReceipt: LiveData<Resource<SudRenewalPremiumReceiptModel.SudRenewalPremiumReceiptResponse>> get() = _sudRenewalPremiumReceipt

    fun callPolicyProductsApi(fragment: SudLifePolicyDashboardFragment) =
        viewModelScope.launch(Dispatchers.Main) {

            val requestData = PolicyProductsModel(
                Gson().toJson(
                    PolicyProductsModel.JSONDataRequest(
                        screen = "POLICY_SECTION",
                        personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
                            .toInt(),
                        accountId = preferenceUtils.getPreference(
                            PreferenceConstants.ACCOUNTID,
                            "0"
                        ).toInt(),
                        orgEmpID = preferenceUtils.getPreference(
                            PreferenceConstants.ORG_EMPLOYEE_ID,
                            ""
                        ),
                        orgName = preferenceUtils.getPreference(PreferenceConstants.ORG_NAME, ""),
                        userType = Utilities.getEmployeeType()
                    ), PolicyProductsModel.JSONDataRequest::class.java
                ), preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
            )

            //_progressBar.value = Event("")
            _policyProducts.removeSource(policyProductsSource)
            withContext(Dispatchers.IO) {
                policyProductsSource =
                    sudLifePolicyManagementUseCase.invokePolicyProducts(data = requestData)
            }
            _policyProducts.addSource(policyProductsSource) {
                _policyProducts.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    //_progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data != null) {
                        Utilities.printData("PolicyProducts", it.data)
                        if (it.data.policyProducts.isNotEmpty()) {
                            val list = it.data.policyProducts.toMutableList()
                            list.sortBy { it.productID }
                            fragment.policyDataSingleton!!.productsList = list
                            fragment.setUpSlidingViewPager(list)
                        }
                        fragment.stopProductsShimmer()
                    }
                }
                if (it.status == Resource.Status.ERROR) {
                    //_progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.errorNumber.equals("1100014", true)) {
                        _sessionError.value = Event(true)
                    } else {
                        //toastMessage(it.errorMessage)
                    }
                    fragment.stopProductsShimmer()
                }
            }

        }

    fun callSudPolicyByMobileNumberApi(fragment: SudLifePolicyDashboardFragment) =
        viewModelScope.launch(Dispatchers.Main) {

            val obj = JsonObject()
            obj.addProperty("api_code", Constants.SUD_MOBILE_NUMBER_DETAILS)
            //obj.addProperty("policy_number","")
            obj.addProperty("mobile_number", preferenceUtils.getPreference(PreferenceConstants.POLICY_MOBILE_NUMBER, ""))
            val requestData = SudPolicyByMobileNumberModel(obj, preferenceUtils.getPreference(PreferenceConstants.TOKEN, ""))

            //_progressBar.value = Event("")
            _sudPolicyByMobileNumber.removeSource(sudPolicyByMobileNumberSource)
            withContext(Dispatchers.IO) {
                sudPolicyByMobileNumberSource = sudLifePolicyManagementUseCase.invokeSudPolicyByMobileNumber(data = requestData)
            }
            _sudPolicyByMobileNumber.addSource(sudPolicyByMobileNumberSource) {
                _sudPolicyByMobileNumber.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data != null) {
                        //Utilities.printLog("Response--->" + it.data!!)
                        var policyAvailable = false
                        val data = HashMap<String, Any>()
                        when (it.data.result.status) {
                            "1" -> {
                                fragment.policyListSizeFinal = it.data.result.records.size
                                if (!it.data.result.records.isNullOrEmpty()) {
                                    policyAvailable = true
                                    for (item in it.data.result.records) {
                                        //callPolicyDetailsByPolicyNumberApi(item.policyNumber!!,fragment)
                                        callSudKypApi(item.policyNumber!!, fragment)
                                    }
                                } else {
                                    fragment.showNoDataView()
                                }
                            }

                            "0" -> {
                                fragment.showNoDataView()
                            }
                        }
                        if ( policyAvailable ) {
                            data[CleverTapConstants.DATA_AVAILABLE] = CleverTapConstants.YES
                        } else {
                            data[CleverTapConstants.DATA_AVAILABLE] = CleverTapConstants.NO
                        }
                        CleverTapHelper.pushEventWithProperties(context,CleverTapConstants.POLICY_DASHBAORD_SCREEN,data)
                    }
                }
                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    toastMessage(it.errorMessage)
                    fragment.showNoDataView()
                }
            }

        }

    fun callPolicyDetailsByPolicyNumberApi(policy_Number: String) =
        viewModelScope.launch(Dispatchers.Main) {

            val obj = JsonObject()
            obj.addProperty("api_code", Constants.SUD_POLICY_DETAILS)
            obj.addProperty("policy_number", policy_Number)
            //obj.addProperty("mobile_number","")
            val requestData = SudPolicyDetailsByPolicyNumberModel(
                obj,
                preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
            )

            //_progressBar.value = Event("")
            _policyDetailsByPolicyNumber.removeSource(policyDetailsByPolicyNumberSource)
            withContext(Dispatchers.IO) {
                policyDetailsByPolicyNumberSource =
                    sudLifePolicyManagementUseCase.invokeSudPolicyDetailsByPolicyNumber(data = requestData)
            }
            _policyDetailsByPolicyNumber.addSource(policyDetailsByPolicyNumberSource) {
                _policyDetailsByPolicyNumber.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    //_progressBar.value = Event(Event.HIDE_PROGRESS)
                    Utilities.printLog("Response--->" + it.data!!)
                }
                if (it.status == Resource.Status.ERROR) {
                    //_progressBar.value = Event(Event.HIDE_PROGRESS)
                    toastMessage(it.errorMessage)
                }
            }

        }

    fun callSudGetFundDetailApi(policy_Number: String) = viewModelScope.launch(Dispatchers.Main) {

        val obj = JsonObject()
        obj.addProperty("api_code", Constants.SUD_FUND_DETAILS)
        obj.addProperty("policy_number", policy_Number)
        //obj.addProperty("mobile_number","")
        val requestData =
            SudFundDetailsModel(obj, preferenceUtils.getPreference(PreferenceConstants.TOKEN, ""))

        _progressBar.value = Event("")
        _sudFundDetails.removeSource(sudFundDetailsSource)
        withContext(Dispatchers.IO) {
            sudFundDetailsSource =
                sudLifePolicyManagementUseCase.invokeSudGetFundDetail(data = requestData)
        }
        _sudFundDetails.addSource(sudFundDetailsSource) {
            _sudFundDetails.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null) {
                    Utilities.printLog("Response--->" + it.data)
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }

    }

    fun callSudReceiptDetails(policy_Number: String) = viewModelScope.launch(Dispatchers.Main) {

        val obj = JsonObject()
        obj.addProperty("api_code", Constants.SUD_INSTANT_ISSUANCE_WA)
        obj.addProperty("policy_number", policy_Number)
        //obj.addProperty("mobile_number","")
        val requestData = SudReceiptDetailsModel(
            obj,
            preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
        )

        _progressBar.value = Event("")
        _sudReceiptDetails.removeSource(sudReceiptDetailsSource)
        withContext(Dispatchers.IO) {
            sudReceiptDetailsSource =
                sudLifePolicyManagementUseCase.invokeSudGetReceiptDetails(data = requestData)
        }
        _sudReceiptDetails.addSource(sudReceiptDetailsSource) {
            _sudReceiptDetails.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }

    }

    fun callSudGetKypTemplateApi(policy_Number: String) = viewModelScope.launch(Dispatchers.Main) {

        val obj = JsonObject()
        obj.addProperty("api_code", Constants.SUD_KYP_TEMPLATE)
        obj.addProperty("policy_number", policy_Number)
        obj.addProperty(
            "mobile_number",
            preferenceUtils.getPreference(PreferenceConstants.POLICY_MOBILE_NUMBER, "")
        )
        val requestData =
            SudKypTemplateModel(obj, preferenceUtils.getPreference(PreferenceConstants.TOKEN, ""))

        //_progressBar.value = Event("")
        _sudGetKypTemplate.removeSource(sudGetKypTemplateSource)
        withContext(Dispatchers.IO) {
            sudGetKypTemplateSource =
                sudLifePolicyManagementUseCase.invokeSudGetKypTemplate(data = requestData)
        }
        _sudGetKypTemplate.addSource(sudGetKypTemplateSource) {
            _sudGetKypTemplate.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null) {
                    Utilities.printLog("Response--->" + it.data)
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                //toastMessage(it.errorMessage)
            }
        }

    }

    fun callSudKypPdfApi(policy_Number: String) = viewModelScope.launch(Dispatchers.Main) {

        val obj = JsonObject()
        obj.addProperty("api_code", Constants.SUD_KYP_PDF)
        obj.addProperty("policy_number", policy_Number)
        val requestData =
            SudKypPdfModel(obj, preferenceUtils.getPreference(PreferenceConstants.TOKEN, ""))

        _progressBar.value = Event(Constants.LOADER_DOWNLOAD)
        _sudKypPdf.removeSource(sudKypPdfSource)
        withContext(Dispatchers.IO) {
            sudKypPdfSource = sudLifePolicyManagementUseCase.invokeSudKypPdf(data = requestData)
        }
        _sudKypPdf.addSource(sudKypPdfSource) {
            _sudKypPdf.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null) {
                    try {
                        val data = HashMap<String, Any>()
                        data[CleverTapConstants.POLICY_NUMBER] = policy_Number
                        CleverTapHelper.pushEventWithProperties(
                            context,
                            CleverTapConstants.POLICY_KNOW_YOUR_POLICY,
                            data
                        )
                        val byteArray = it.data.result.pdf_Base64
                        if (!Utilities.isNullOrEmpty(byteArray)) {
                            val decodedString = Base64.decode(byteArray, Base64.DEFAULT)
                            if (decodedString != null) {
                                val fileName =
                                    fileUtils.generateUniqueFileName("KYP-$policy_Number", ".pdf")
                                val pm = fileUtils.saveByteArrayToExternalStorage(
                                    context!!,
                                    decodedString,
                                    fileName
                                )
                                Utilities.openDownloadedDocumentFile(pm!!, context)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                //toastMessage(it.errorMessage)
            }
        }

    }

    fun callSudGetPayPremiumApi(policy_Number: String) = viewModelScope.launch(Dispatchers.Main) {

        val obj = JsonObject()
        obj.addProperty("api_code", Constants.SUD_SHORT_URL_API)
        obj.addProperty("policy_number", policy_Number)
        obj.addProperty(
            "dob", DateHelper.convertDateTimeValue(
                preferenceUtils.getPreference(PreferenceConstants.DOB, ""),
                DateHelper.SERVER_DATE_YYYYMMDD,
                DateHelper.DISPLAY_DATE_DDMMYYYY
            )
        )
        val requestData =
            SudPayPremiumModel(obj, preferenceUtils.getPreference(PreferenceConstants.TOKEN, ""))

        _progressBar.value = Event("")
        _sudGetPayPremium.removeSource(sudGetPayPremiumSource)
        withContext(Dispatchers.IO) {
            sudGetPayPremiumSource =
                sudLifePolicyManagementUseCase.invokeSudGetPayPremium(data = requestData)
        }
        _sudGetPayPremium.addSource(sudGetPayPremiumSource) {
            _sudGetPayPremium.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null) {
                    Utilities.printLog("Response--->" + it.data)
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }

    }

    fun callSudGetPMJJBYCoiBaseApi(fragment: SudLifePolicyDashboardFragment) =
        viewModelScope.launch(Dispatchers.Main) {
            val mobNumber =
                preferenceUtils.getPreference(PreferenceConstants.POLICY_MOBILE_NUMBER, "")
            val userDob = preferenceUtils.getPreference(PreferenceConstants.DOB, "")
            //val mobNumber = "9604710871"
            //val userDob = "1976-05-25"
            val obj = JsonObject()
            obj.addProperty("api_code", Constants.SUD_PMJJBY_COI_BASE)
            obj.addProperty("mobile_number", mobNumber)
            obj.addProperty("dob", userDob)
            obj.addProperty("channelCode", "umatter0101")
            val requestData = SudPMJJBYCoiBaseModel(
                obj,
                preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
            )

            _progressBar.value = Event(Constants.LOADER_DOWNLOAD)
            _sudPMJJBYCoiBase.removeSource(sudPMJJBYCoiBaseSource)
            withContext(Dispatchers.IO) {
                sudPMJJBYCoiBaseSource =
                    sudLifePolicyManagementUseCase.invokeSudGetPMJJBYCoiBase(data = requestData)
            }
            _sudPMJJBYCoiBase.addSource(sudPMJJBYCoiBaseSource) {
                _sudPMJJBYCoiBase.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data != null) {
                        try {
                            val data = HashMap<String, Any>()
                            data[CleverTapConstants.COMBINATION] = "$mobNumber|$userDob"
                            val byteArray = it.data.result.pdf_Base64
                            if (!Utilities.isNullOrEmpty(byteArray)) {
                                val decodedString = Base64.decode(byteArray, Base64.DEFAULT)
                                if (decodedString != null) {
                                    val fileName = fileUtils.generateUniqueFileName(
                                        "PMJJBY-$mobNumber",
                                        ".pdf"
                                    )
                                    val pm = fileUtils.saveByteArrayToExternalStorage(
                                        context!!,
                                        decodedString,
                                        fileName
                                    )
                                    Utilities.openDownloadedDocumentFile(pm!!, context)
                                    data[CleverTapConstants.STATUS] = Constants.SUCCESS
                                }
                            } else {
                                //Utilities.toastMessageLong(context,context.resources.getString(R.string.PMJJBY_ERROR))
                                fragment.showDialog(
                                    listener = fragment,
                                    message = localResource.getString(R.string.PMJJBY_ERROR),
                                    rightText = localResource.getString(R.string.OK),
                                    showLeftBtn = false
                                )
                                data[CleverTapConstants.STATUS] = Constants.FAILURE
                            }
                            CleverTapHelper.pushEventWithProperties(
                                context,
                                CleverTapConstants.POLICY_PMJJBY_POLICY_DOWNLOAD,
                                data
                            )/*                        if ( !Utilities.isNullOrEmpty(it.data!!.result.status)
                                                    && it.data!!.result.status == "2" ) {
                                                    Utilities.toastMessageShort(context,"There is No PMMJJBY Policy found on your registered mobile number")
                                                }*/
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    toastMessage(it.errorMessage)
                }
            }

        }

    fun callSudGroupCoiApi(fragment: SudLifePolicyDashboardFragment) =
        viewModelScope.launch(Dispatchers.Main) {

            val mobNumber =
                preferenceUtils.getPreference(PreferenceConstants.POLICY_MOBILE_NUMBER, "")
            val userDob = preferenceUtils.getPreference(PreferenceConstants.DOB, "")
            //val mobNumber = "8080376028"
            //val userDob = "1991-12-11"
            //Test Users
            //8802691941    1976-02-25,
            //8080376028    1991-12-11
            //9558744809    1971-06-01

            val requestData = SudGroupCOIModel(
                Gson().toJson(
                    SudGroupCOIModel.JSONDataRequest(
                        mobileNo = mobNumber, dob = userDob
                    ), SudGroupCOIModel.JSONDataRequest::class.java
                ), preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
            )

            _progressBar.value = Event(Constants.LOADER_DOWNLOAD)
            _sudGroupCoiApi.removeSource(sudGroupCoiApiSource)
            withContext(Dispatchers.IO) {
                sudGroupCoiApiSource =
                    sudLifePolicyManagementUseCase.invokeSudGroupCoi(data = requestData)
            }
            _sudGroupCoiApi.addSource(sudGroupCoiApiSource) {
                _sudGroupCoiApi.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data != null) {
                        try {
                            val data = HashMap<String, Any>()
                            data[CleverTapConstants.COMBINATION] = "$mobNumber|$userDob"
                            val byteArray = it.data.groupCOIResponse.pdfBASE64
                            if (!Utilities.isNullOrEmpty(byteArray)) {
                                val decodedString = Base64.decode(byteArray, Base64.DEFAULT)
                                if (decodedString != null) {
                                    val fileName = fileUtils.generateUniqueFileName(
                                        "GROUP_COI-$mobNumber",
                                        ".pdf"
                                    )
                                    val pm = fileUtils.saveByteArrayToExternalStorage(
                                        context!!,
                                        decodedString,
                                        fileName
                                    )
                                    Utilities.openDownloadedDocumentFile(pm!!, context)
                                    data[CleverTapConstants.STATUS] = Constants.SUCCESS
                                }
                            } else {
                                //Utilities.toastMessageLong(context,context.resources.getString(R.string.PMJJBY_ERROR))
                                fragment.showDialog(
                                    listener = fragment,
                                    message = localResource.getString(R.string.GROUP_COI_ERROR),
                                    rightText = localResource.getString(R.string.OK),
                                    showLeftBtn = false
                                )
                                data[CleverTapConstants.STATUS] = Constants.FAILURE
                            }
                            CleverTapHelper.pushEventWithProperties(
                                context,
                                CleverTapConstants.POLICY_GROUP_COI_POLICY_DOWNLOAD,
                                data
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    toastMessage(it.errorMessage)
                }
            }

        }

    fun callGenerateVerificationCode(phone: String) = viewModelScope.launch(Dispatchers.Main) {

        val requestData = GenerateOtpModel(
            Gson().toJson(
                GenerateOtpModel.JSONDataRequest(
                    GenerateOtpModel.UPN(
                        loginName = email, emailAddress = email, primaryPhone = phone
                    ), message = "Generating OTP"
                ), GenerateOtpModel.JSONDataRequest::class.java
            )
        )

        _progressBar.value = Event("Generating OTP")
        _otpGenerateData.removeSource(otpGenerateSource)
        withContext(Dispatchers.IO) {
            otpGenerateSource = sudLifePolicyManagementUseCase.invokeGenerateOTP(true, requestData)
        }
        _otpGenerateData.addSource(otpGenerateSource) {
            _otpGenerateData.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)/*                if (it.data!!.status.equals(Constants.SUCCESS, ignoreCase = true)) {
                
                                } else {
                                    toastMessage(it.errorMessage)
                                }*/
            }

            if (it.status == Resource.Status.ERROR) {
                //_progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }
    }

    fun callValidateVerificationCode(otpReceived: String) =
        viewModelScope.launch(Dispatchers.Main) {

            val requestData = GenerateOtpModel(
                Gson().toJson(
                    GenerateOtpModel.JSONDataRequest(
                        upn = GenerateOtpModel.UPN(emailAddress = email),
                        otp = otpReceived,
                        message = "Verifing Code..."
                    ), GenerateOtpModel.JSONDataRequest::class.java
                )
            )

            _progressBar.value = Event("Verifing Code...")
            _otpValidateData.removeSource(otpValidateSource)
            withContext(Dispatchers.IO) {
                otpValidateSource =
                    sudLifePolicyManagementUseCase.invokeValidateOTP(true, requestData)
            }
            _otpValidateData.addSource(otpValidateSource) {
                _otpValidateData.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                }

                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    toastMessage(it.errorMessage)
                }
            }
        }

    private fun callSudKypApi(policy_Number: String, fragment: SudLifePolicyDashboardFragment) {
        try {
            val obj = JsonObject()
            obj.addProperty("api_code", Constants.SUD_KYP)
            obj.addProperty("policy_number", policy_Number)
            //obj.addProperty("mobile_number","")
            val requestData =
                SudKYPModel(obj, preferenceUtils.getPreference(PreferenceConstants.TOKEN, ""))

            //_progressBar.value = Event("")
            val downloadService = provideRetrofit().create(SudPolicyApiService::class.java)
            val call = downloadService.getSudKyp(requestData)
            call.enqueue(object : Callback<SudKYPModel.SudKYPResponse> {
                override fun onResponse(
                    call: Call<SudKYPModel.SudKYPResponse>,
                    response: Response<SudKYPModel.SudKYPResponse>
                ) {
                    if (response.body() != null) {
                        val result = response.body()!!
                        if (response.isSuccessful) {
                            //Utilities.printData("PolicyResp",result,true)
                            if (result.result.status == "1" && !result.result.kYPList.isNullOrEmpty()) {
                                fragment.addPolicyInList(result.result.kYPList[0])
                            }
                            if (result.result.status == "2") {
                                fragment.policyListSize++
                                Utilities.printLogError("policyListSizeFinal--->${fragment.policyListSizeFinal}")
                                Utilities.printLogError("policyListSize--->${fragment.policyListSize}")
                                Utilities.printLogError("No data found for Policy--->$policy_Number")
                                if (fragment.policyListSizeFinal == fragment.policyListSize) {
                                    fragment.notifyList(fragment.sudPolicyList)
                                    fragment.policyDataSingleton!!.policyList =
                                        fragment.sudPolicyList
                                }
                            }
                        } else {
                            Utilities.printLogError("Server Contact failed")
                        }
                    } else {
                        Utilities.printLogError("response.body is null")
                    }
                    //_progressBar.value = Event(Event.HIDE_PROGRESS)
                }

                override fun onFailure(call: Call<SudKYPModel.SudKYPResponse>, t: Throwable) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    Utilities.printLog("Api failed" + t.printStackTrace())
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun callSudKYPApi(policy_Number: String) = viewModelScope.launch(Dispatchers.Main) {

        val obj = JsonObject()
        obj.addProperty("api_code", Constants.SUD_KYP)
        obj.addProperty("policy_number", policy_Number)
        //obj.addProperty("mobile_number","")
        val requestData =
            SudKYPModel(obj, preferenceUtils.getPreference(PreferenceConstants.TOKEN, ""))

        _progressBar.value = Event("")
        _sudKYP.removeSource(sudKYPSource)
        withContext(Dispatchers.IO) {
            sudKYPSource = sudLifePolicyManagementUseCase.invokeSudKYP(data = requestData)
        }
        _sudKYP.addSource(sudKYPSource) {
            _sudKYP.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null) {
                    Utilities.printLog("Response--->" + it.data)
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }

    }

    fun callSudRenewalPremium(policy_Number: String) = viewModelScope.launch(Dispatchers.Main) {

        val obj = JsonObject()
        obj.addProperty("api_code", Constants.SUD_RENEWAL_PREMIUM)
        obj.addProperty("policy_number", policy_Number)
        //obj.addProperty("mobile_number","")
        val requestData = SudRenewalPremiumModel(
            obj,
            preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
        )

        _progressBar.value = Event("")
        _sudRenewalPremium.removeSource(sudRenewalPremiumSource)
        withContext(Dispatchers.IO) {
            sudRenewalPremiumSource =
                sudLifePolicyManagementUseCase.invokeSudGetRenewalPremium(data = requestData)
        }
        _sudRenewalPremium.addSource(sudRenewalPremiumSource) {
            _sudRenewalPremium.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                toastMessage(it.errorMessage)
            }
        }

    }

    fun callSudRenewalPremiumReceipt(policy_Number: String) =
        viewModelScope.launch(Dispatchers.Main) {

            val obj = JsonObject()
            obj.addProperty("api_code", Constants.SUD_PPS_RPR_PDF)
            obj.addProperty("policy_number", policy_Number)
            //obj.addProperty("mobile_number","")
            val requestData = SudRenewalPremiumReceiptModel(
                obj,
                preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
            )

            _progressBar.value = Event("")
            _sudRenewalPremiumReceipt.removeSource(sudRenewalPremiumReceiptSource)
            withContext(Dispatchers.IO) {
                sudRenewalPremiumReceiptSource =
                    sudLifePolicyManagementUseCase.invokeSudGetRenewalPremiumReceipt(data = requestData)
            }
            _sudRenewalPremiumReceipt.addSource(sudRenewalPremiumReceiptSource) {
                _sudRenewalPremiumReceipt.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                }
                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    toastMessage(it.errorMessage)
                }
            }

        }

    /*    fun updateOtpAuthentication(isOtpAuthenticated: Boolean) = viewModelScope.launch {
            withContext(Dispatchers.IO) {
                sudLifePolicyManagementUseCase.invokeUpdateOtpAuthentication(isOtpAuthenticated)
            }
        }*/

    private fun provideRetrofit(): Retrofit {
        val logging = HttpLoggingInterceptor { message ->
            Utilities.printLog("HttpLogging--> $message")
        }
        logging.level = HttpLoggingInterceptor.Level.BODY
        return Retrofit.Builder().client(
            OkHttpClient.Builder().retryOnConnectionFailure(true)
                .protocols(listOf(Protocol.HTTP_1_1)).addInterceptor(logging)
                .connectTimeout(3, TimeUnit.MINUTES).writeTimeout(3, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES).build()
        ).baseUrl(Constants.strAPIUrl).addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun callPolicyDetailsByPolicyNumberApi3(url: String): String {
        var strResponse = ""
        try {
            val request: Request = Request.Builder().addHeader("APPName", "PolicyDetails")
                .addHeader("APIKey", "c06fc4189a5645e4a4fd480e8b1556e7")
                .addHeader("Content-Type", "application/json").url(url).build()
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    strResponse = requestCallToServer(request)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return strResponse
    }

    private fun requestCallToServer(request: Request): String {
        var strResponse = ""
        try {
            val logging = HttpLoggingInterceptor { message: String? ->
                Utilities.printLog("HttpLogging--> $message")
            }
            logging.level = HttpLoggingInterceptor.Level.BODY
            val okHttpClient =
                OkHttpClient.Builder().protocols(Collections.singletonList(Protocol.HTTP_1_1))
                    .addInterceptor(logging).connectTimeout(3, TimeUnit.MINUTES)
                    .writeTimeout(3, TimeUnit.MINUTES).readTimeout(3, TimeUnit.MINUTES).build()
            val response = okHttpClient.newCall(request).execute()
            strResponse = response.body!!.string()
            Utilities.printLogError("OkHttp strResponse-----> $strResponse")
            return strResponse
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return strResponse
    }

    fun showProgress() {
        _progressBar.value = Event("")
    }

    fun hideProgress() {
        _progressBar.value = Event(Event.HIDE_PROGRESS)
    }

    fun setIsOtpAuthenticatedStatus(flag: Boolean) {
        preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_OTP_AUTHENTICATED, flag)
    }

    fun setSudPolicyMobileNumber(policyMobileNumber: String) {
        preferenceUtils.storePreference(
            PreferenceConstants.POLICY_MOBILE_NUMBER,
            policyMobileNumber
        )
    }


    fun setFirstTimePolicyVisitFlag(flag: Boolean) {
        preferenceUtils.storeBooleanPreference(PreferenceConstants.IS_FIRST_POLICY_VISIT, flag)
    }

    fun isFirstTimePolicyVisit(): Boolean {
        return preferenceUtils.getBooleanPreference(PreferenceConstants.IS_FIRST_POLICY_VISIT, true)
    }

}
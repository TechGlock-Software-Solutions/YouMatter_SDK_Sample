package com.techglock.health.app.hra.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
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
import com.techglock.health.app.common.utils.Event
import com.techglock.health.app.common.utils.LocaleHelper
import com.techglock.health.app.common.utils.PreferenceUtils
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.hra.DownloadReportApiService
import com.techglock.health.app.hra.common.HraDataSingleton
import com.techglock.health.app.hra.common.HraHelper
import com.techglock.health.app.hra.domain.HraManagementUseCase
import com.techglock.health.app.model.entity.HRASummary
import com.techglock.health.app.model.hra.HraAssessmentSummaryModel
import com.techglock.health.app.model.hra.HraAssessmentSummaryModel.AssessmentDetails
import com.techglock.health.app.model.hra.HraLabTest
import com.techglock.health.app.model.hra.HraListRecommendedTestsModel
import com.techglock.health.app.model.hra.HraMedicalProfileSummaryModel
import com.techglock.health.app.repository.utils.Resource
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.ResponseBody
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
class HraSummaryViewModel @Inject constructor(
    application: Application,
    private val hraManagementUseCase: HraManagementUseCase,
    private val preferenceUtils: PreferenceUtils,
    val context: Context?
) : BaseViewModel(application) {

    private val localResource =
        LocaleHelper.getLocalizedResources(context!!, Locale(LocaleHelper.getLanguage(context)))!!
    var authToken = preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
    var personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")

    private val hraDataSingleton = HraDataSingleton.getInstance()!!

    var hraSummaryDetails = MutableLiveData<HRASummary?>()
    var hraAssessmentSummaryDetails = MutableLiveData<List<AssessmentDetails>>()
    var hraRecommendedTests = MutableLiveData<List<HraLabTest>>()

    private var medicalProfileSummarySource: LiveData<Resource<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse>> =
        MutableLiveData()
    private val _medicalProfileSummary =
        MediatorLiveData<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse?>()
    val medicalProfileSummary: LiveData<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse?> get() = _medicalProfileSummary

    private var assessmentSummarySource: LiveData<Resource<HraAssessmentSummaryModel.AssessmentSummaryResponce>> =
        MutableLiveData()
    private val _assessmentSummary =
        MediatorLiveData<HraAssessmentSummaryModel.AssessmentSummaryResponce?>()
    val assessmentSummary: LiveData<HraAssessmentSummaryModel.AssessmentSummaryResponce?> get() = _assessmentSummary

    private var listRecommendedTestsSource: LiveData<Resource<HraListRecommendedTestsModel.ListRecommendedTestsResponce>> =
        MutableLiveData()
    private val _listRecommendedTests =
        MediatorLiveData<HraListRecommendedTestsModel.ListRecommendedTestsResponce?>()
    val listRecommendedTests: LiveData<HraListRecommendedTestsModel.ListRecommendedTestsResponce?> get() = _listRecommendedTests

    fun getMedicalProfileSummary(fr: Boolean, relativeId: String) =
        viewModelScope.launch(Dispatchers.Main) {

            var hraPersonId = relativeId
            if (Utilities.isNullOrEmptyOrZero(hraPersonId)) {
                hraPersonId = personId
            }

            val requestData = HraMedicalProfileSummaryModel(
                Gson().toJson(
                    HraMedicalProfileSummaryModel.JSONDataRequest(
                        PersonID = hraPersonId
                    ), HraMedicalProfileSummaryModel.JSONDataRequest::class.java
                ), authToken
            )

            _progressBar.value = Event("")
            _medicalProfileSummary.removeSource(medicalProfileSummarySource)
            withContext(Dispatchers.IO) {
                medicalProfileSummarySource = hraManagementUseCase.invokeMedicalProfileSummary(
                    isForceRefresh = fr,
                    data = requestData,
                    personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
                )
            }
            _medicalProfileSummary.addSource(medicalProfileSummarySource) {
                try {
                    _medicalProfileSummary.value = it.data
                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            //_progressBar.value = Event(Event.HIDE_PROGRESS)
                            if (it.data != null) {
                                Utilities.printLog("MedicalProfileSummary :- ${it.data}")
                                hraSummaryDetails.postValue(it.data.MedicalProfileSummary)
                            }
                        }

                        Resource.Status.ERROR -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            if (it.errorNumber.equals("1100014", true)) {
                                _sessionError.value = Event(true)
                            } else {
                                toastMessage(it.errorMessage)
                            }
                        }

                        else -> {}
                    }

                } catch (e: Exception) {
                    Utilities.printException(e)
                }
            }
        }

    fun getAssessmentSummary(forceRefresh: Boolean, relativeId: String) =
        viewModelScope.launch(Dispatchers.Main) {

            var hraPersonId = relativeId
            if (Utilities.isNullOrEmptyOrZero(hraPersonId)) {
                hraPersonId = personId
            }

            val requestData = HraAssessmentSummaryModel(
                Gson().toJson(
                    HraAssessmentSummaryModel.JSONDataRequest(
                        PersonID = hraPersonId
                    ), HraAssessmentSummaryModel.JSONDataRequest::class.java
                ), authToken
            )

            _progressBar.value = Event("")
            _assessmentSummary.removeSource(assessmentSummarySource)
            withContext(Dispatchers.IO) {
                assessmentSummarySource = hraManagementUseCase.invokeGetAssessmentSummary(
                    isForceRefresh = forceRefresh,
                    data = requestData
                )
            }
            _assessmentSummary.addSource(assessmentSummarySource) {
                try {
                    _assessmentSummary.value = it.data
                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            if (it != null) {
                                val assessmentSummaryData = it.data!!.hraSummaryReport
                                var assessmentSummaryList: MutableList<AssessmentDetails> =
                                    mutableListOf()
                                Utilities.printLog("AssessmentSummary :- $it")
                                if (assessmentSummaryData.suggestedAssessments.isNotEmpty()) {
                                    assessmentSummaryList.addAll(assessmentSummaryData.suggestedAssessments)
                                }
                                if (assessmentSummaryData.otherAssessments.isNotEmpty()) {
                                    assessmentSummaryList.addAll(assessmentSummaryData.otherAssessments)
                                }
                                assessmentSummaryList = assessmentSummaryList.filter { addDetail ->
                                    addDetail.riskCategory != "Fitness" && addDetail.riskCategory != "Nutrition"
                                }.toMutableList()
                                hraAssessmentSummaryDetails.postValue(assessmentSummaryList)
                            }
                        }

                        Resource.Status.ERROR -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            if (it.errorNumber.equals("1100014", true)) {
                                _sessionError.value = Event(true)
                            } else {
                                toastMessage(it.errorMessage)
                            }
                        }

                        else -> {}
                    }

                } catch (e: Exception) {
                    Utilities.printException(e)
                }
            }
        }

    fun getListRecommendedTests(forceRefresh: Boolean, relativeId: String) =
        viewModelScope.launch(Dispatchers.Main) {

            var hraPersonId = relativeId
            if (Utilities.isNullOrEmptyOrZero(hraPersonId)) {
                hraPersonId = personId
            }

            val requestData = HraListRecommendedTestsModel(
                Gson().toJson(
                    HraListRecommendedTestsModel.JSONDataRequest(
                        PersonID = hraPersonId
                    ), HraListRecommendedTestsModel.JSONDataRequest::class.java
                ), authToken
            )

            _progressBar.value = Event("")
            _listRecommendedTests.removeSource(listRecommendedTestsSource)
            withContext(Dispatchers.IO) {
                listRecommendedTestsSource = hraManagementUseCase.invokeGetListRecommendedTests(
                    isForceRefresh = forceRefresh,
                    data = requestData
                )
            }
            _listRecommendedTests.addSource(listRecommendedTestsSource) {
                try {
                    _listRecommendedTests.value = it.data
                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            if (it.data != null) {
                                val listRecommendedTestsData = it.data.labTests
                                if (listRecommendedTestsData.isNotEmpty()) {
                                    saveRecommendedTestsList(listRecommendedTestsData)
                                }
                            }
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                        }

                        Resource.Status.ERROR -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            if (it.errorNumber.equals("1100014", true)) {
                                _sessionError.value = Event(true)
                            } else {
                                toastMessage(it.errorMessage)
                            }
                        }

                        else -> {}
                    }
                } catch (e: Exception) {
                    Utilities.printException(e)
                }
            }
        }

    fun callDownloadReport() {

        _progressBar.value = Event(Constants.LOADER_DOWNLOAD)
        //_progressBarType.value = Event(Constants.LOADER_DOWNLOAD)

        val baseURL = Constants.strAPIUrl
        val proxyURL = Constants.strProxyUrl
        val hraUrl = Constants.HRAurl
        val partnerCode = Constants.PartnerCode
        val personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
        val accountId = preferenceUtils.getPreference(PreferenceConstants.ACCOUNTID, "0")
        val hash = preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
        // Hash will be AUTH_TICKET we are getting in Login response.

        val finalHRADownloadURL =
            "$baseURL$proxyURL$hraUrl&P=$personId&PCD=$partnerCode&UID=$accountId&E=&Skey=$hash"
        Utilities.printLog("FinalHRADownloadURL--> $finalHRADownloadURL")

        val hraDownloadURL =
            "$proxyURL$hraUrl&P=$personId&PCD=$partnerCode&UID=$accountId&E=&Skey=$hash"
        val logging = HttpLoggingInterceptor { message ->
            Utilities.printLog("HttpLogging--> $message")
        }
        logging.level = HttpLoggingInterceptor.Level.BODY
        val retrofit = Retrofit.Builder()
            .client(
                OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .protocols(listOf(Protocol.HTTP_1_1))
                    .addInterceptor(logging)
                    .connectTimeout(3, TimeUnit.MINUTES)
                    .writeTimeout(3, TimeUnit.MINUTES)
                    .readTimeout(3, TimeUnit.MINUTES)
                    .build()
            )
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val downloadService = retrofit.create(DownloadReportApiService::class.java)

        val call = downloadService.downloadHraReport(hraDownloadURL)
        call.enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)

                if (response.body() != null) {
                    val result = response.body()!!
                    if (response.isSuccessful) {
                        CleverTapHelper.pushEvent(context, CleverTapConstants.DOWNLOAD_HRA_REPORT)
                        Utilities.printLog("Server contacted and has file--> ")
                        val writtenToDisk = HraHelper.saveHRAReportAppDirectory(result, context!!)
                        Utilities.printLog("File download was---->$writtenToDisk")
                    } else {
                        Utilities.printLog("Server Contact failed")
                    }
                } else {
                    Utilities.toastMessageShort(
                        context,
                        localResource.getString(R.string.ERROR_UNABLE_TO_DOWNLOAD)
                    )
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                Utilities.printLog("Download Report call failed" + t.printStackTrace())
            }
        })
    }

    private fun saveRecommendedTestsList(list: List<HraListRecommendedTestsModel.LabTest>) {

        val hraLabTestList: ArrayList<HraLabTest> = ArrayList()

        for (labTestsIndex in list.indices) {
            for (testsIndex in list[labTestsIndex].tests.indices) {
                if (list[labTestsIndex].tests.isNotEmpty()) {
                    val labTestName = list[labTestsIndex].tests[testsIndex].labTestName
                    val frequency = list[labTestsIndex].tests[testsIndex].frequency

                    var separator1 = ""
                    val sbReasonCodes = StringBuilder()
                    if (list[labTestsIndex].tests[testsIndex].reasonCodes.isNotEmpty()) {
                        for (element in list[labTestsIndex].tests[testsIndex].reasonCodes) {
                            sbReasonCodes.append(separator1 + element)
                            separator1 = ","
                        }
                    }

                    var separator2 = ""
                    val sbReasons = StringBuilder()
                    if (list[labTestsIndex].tests[testsIndex].reasons.isNotEmpty()) {
                        for (element in list[labTestsIndex].tests[testsIndex].reasons) {
                            sbReasons.append(separator2 + element)
                            separator2 = ","
                        }
                    }

                    val hraLabTest = HraLabTest(
                        LabTestName = labTestName,
                        ReasonCodes = sbReasonCodes.toString(),
                        Reasons = sbReasons.toString(),
                        Frequency = frequency
                    )
                    Utilities.printLog("HraLabTest :- $hraLabTest")
                    hraLabTestList.add(hraLabTest)
                }
            }
        }
        hraRecommendedTests.postValue(hraLabTestList)
    }

    fun clearPreviousQuesDataAndTable() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            hraManagementUseCase.invokeClearHraQuestionsTable()
            hraDataSingleton.previousAnsList.clear()
            hraDataSingleton.clearData()
        }
    }

    fun clearHraDataTables() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            hraManagementUseCase.invokeClearHraDataTables()
            hraDataSingleton.previousAnsList.clear()
            hraDataSingleton.clearData()
        }
    }

}
package com.techglock.health.app.tools_calculators.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.ArrayMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.PreferenceConstants
import com.techglock.health.app.common.utils.Event
import com.techglock.health.app.common.utils.PreferenceUtils
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.model.hra.HraMedicalProfileSummaryModel
import com.techglock.health.app.model.hra.LabRecordsModel
import com.techglock.health.app.model.toolscalculators.*
import com.techglock.health.app.model.toolscalculators.StartQuizModel.Contact
import com.techglock.health.app.model.toolscalculators.StartQuizModel.ParticipationDetails
import com.techglock.health.app.repository.utils.Resource
import com.techglock.health.app.tools_calculators.common.DataHandler
import com.techglock.health.app.tools_calculators.domain.ToolsCalculatorsUseCase
import com.techglock.health.app.tools_calculators.model.*
import com.techglock.health.app.tools_calculators.ui.DiabetesCalculator.DiabetesCalculatorInputFragmentDirections
import com.techglock.health.app.tools_calculators.ui.HeartAgeCalculator.HeartAgeFragmentDirections
import com.techglock.health.app.tools_calculators.ui.HeartAgeCalculator.HeartAgeRecalculateFragmentDirections
import com.techglock.health.app.tools_calculators.ui.HypertensionCalculator.HypertensionInputFragmentDirections
import com.techglock.health.app.tools_calculators.ui.HypertensionCalculator.HypertensionRecalculateFragmentDirections
import com.techglock.health.app.tools_calculators.ui.SmartPhoneAddiction.SmartPhoneInputFragmentDirections
import com.techglock.health.app.tools_calculators.ui.StressAndAnxietyCalculator.StressAndAnxietyInputFragmentDirections
import com.techglock.health.app.tools_calculators.ui.ToolsCalculatorsDashboardFragmentDirections

import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class ToolsCalculatorsViewModel @Inject constructor(
    application: Application,
    private val useCase: ToolsCalculatorsUseCase,
    private val preferenceUtils: PreferenceUtils,
    val dataHandler: DataHandler,
    private val context: Context?
) : BaseViewModel(application) {

    var authToken = preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
    var personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
    var firstName = preferenceUtils.getPreference(PreferenceConstants.FIRSTNAME, "")
    var email = preferenceUtils.getPreference(PreferenceConstants.EMAIL, "")
    var phone = preferenceUtils.getPreference(PreferenceConstants.PHONE, "")

    private var calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!

    var trackersList = MutableLiveData<List<TrackerDashboardModel>>()

    private var startQuizSource: LiveData<Resource<StartQuizModel.StartQuizResponse>> =
        MutableLiveData()
    private val _startQuiz = MediatorLiveData<StartQuizModel.StartQuizResponse?>()
    val startQuiz: LiveData<StartQuizModel.StartQuizResponse?> get() = _startQuiz

    private var heartAgeSaveResponceSource: LiveData<Resource<HeartAgeSaveResponceModel.HeartAgeSaveResponce>> =
        MutableLiveData()
    private val _heartAgeSaveResp =
        MediatorLiveData<HeartAgeSaveResponceModel.HeartAgeSaveResponce?>()
    val heartAgeSaveResp: LiveData<HeartAgeSaveResponceModel.HeartAgeSaveResponce?> get() = _heartAgeSaveResp

    private var diabetesSaveResponceSource: LiveData<Resource<DiabetesSaveResponceModel.DiabetesSaveResponce>> =
        MutableLiveData()
    private val _diabetesSaveResp =
        MediatorLiveData<DiabetesSaveResponceModel.DiabetesSaveResponce?>()
    val diabetesSaveResp: LiveData<DiabetesSaveResponceModel.DiabetesSaveResponce?> get() = _diabetesSaveResp

    private var hypertensionSaveResponceSource: LiveData<Resource<HypertensionSaveResponceModel.HypertensionSaveResponce>> =
        MutableLiveData()
    private val _hypertensionSaveResp =
        MediatorLiveData<HypertensionSaveResponceModel.HypertensionSaveResponce?>()
    val hypertensionSaveResp: LiveData<HypertensionSaveResponceModel.HypertensionSaveResponce?> get() = _hypertensionSaveResp

    private var stressAndAnxietySaveRespSource: LiveData<Resource<StressAndAnxietySaveResponceModel.StressAndAnxietySaveResponse>> =
        MutableLiveData()
    private val _stressAndAnxietySaveResp =
        MediatorLiveData<StressAndAnxietySaveResponceModel.StressAndAnxietySaveResponse?>()
    val stressAndAnxietySaveResp: LiveData<StressAndAnxietySaveResponceModel.StressAndAnxietySaveResponse?> get() = _stressAndAnxietySaveResp

    private var smartPhoneSaveRespSource: LiveData<Resource<SmartPhoneSaveResponceModel.SmartPhoneSaveResponce>> =
        MutableLiveData()
    private val _smartPhoneSaveResp =
        MediatorLiveData<SmartPhoneSaveResponceModel.SmartPhoneSaveResponce?>()
    val smartPhoneSaveResp: LiveData<SmartPhoneSaveResponceModel.SmartPhoneSaveResponce?> get() = _smartPhoneSaveResp

    private var medicalProfileSummarySource: LiveData<Resource<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse>> =
        MutableLiveData()
    private val _medicalProfileSummary =
        MediatorLiveData<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse?>()
    val medicalProfileSummary: LiveData<HraMedicalProfileSummaryModel.MedicalProfileSummaryResponse?> get() = _medicalProfileSummary

    private var labRecordSource: LiveData<Resource<LabRecordsModel.LabRecordsExistResponse>> =
        MutableLiveData()
    private val _labRecords = MediatorLiveData<LabRecordsModel.LabRecordsExistResponse?>()
    val labRecords: LiveData<LabRecordsModel.LabRecordsExistResponse?> get() = _labRecords

    var apiStatusLiveData: MutableLiveData<Boolean> = MutableLiveData()

    fun getAllTrackersList() {
        trackersList.postValue(dataHandler.getTrackersList())
    }

    fun callStartQuizApi(forceRefresh: Boolean, templateId: String) =
        viewModelScope.launch(Dispatchers.Main) {

            val participationDetails = ParticipationDetails(
                id = "0",
                contact = Contact(emailAddress = email, primaryContactNo = phone),
                firstName = firstName,
                personID = personId,
                clusterID = "2425"
            )

            val requestData = StartQuizModel(
                Gson().toJson(
                    StartQuizModel.JSONDataRequest(
                        participationDetails = participationDetails,
                        templatesCode = templateId
                    ), StartQuizModel.JSONDataRequest::class.java
                ), authToken
            )

            if (forceRefresh) {
                _progressBar.value = Event("Starting Quiz.....")
            }

            _startQuiz.removeSource(startQuizSource)
            withContext(Dispatchers.IO) {
                startQuizSource = useCase.invokeStartQuiz(forceRefresh, requestData)
            }
            _startQuiz.addSource(startQuizSource) {
                try {
                    _startQuiz.value = it.data

                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            if (it.data != null) {
                                val quizCode = it.data!!.template.quizCode
                                val participantID = it.data!!.participantID
                                Utilities.printLog("QuizCode=> $quizCode")
                                if (!Utilities.isNullOrEmpty(quizCode)) {
                                    calculatorDataSingleton =
                                        CalculatorDataSingleton.getInstance()!!
                                    calculatorDataSingleton.participantID = participantID
                                    when {
                                        quizCode.equals(
                                            Constants.QUIZ_CODE_HEART_AGE,
                                            ignoreCase = true
                                        )
                                        -> {
                                            calculatorDataSingleton.quizId = "5"
                                            navigate(ToolsCalculatorsDashboardFragmentDirections.actionToolsCalculatorsDashboardFragmentToHeartAgeFragment())
                                        }

                                        quizCode.equals(
                                            Constants.QUIZ_CODE_DIABETES,
                                            ignoreCase = true
                                        )
                                        -> {
                                            calculatorDataSingleton.quizId = "8"
                                            navigate(ToolsCalculatorsDashboardFragmentDirections.actionToolsCalculatorsDashboardFragmentToDiabetesCalculatorFragment())
                                        }

                                        quizCode.equals(
                                            Constants.QUIZ_CODE_HYPERTENSION,
                                            ignoreCase = true
                                        )
                                        -> {
                                            calculatorDataSingleton.quizId = "9"
                                            navigate(ToolsCalculatorsDashboardFragmentDirections.actionToolsCalculatorsDashboardFragmentToHypertensionInputFragment())
                                        }

                                        quizCode.equals(
                                            Constants.QUIZ_CODE_STRESS_ANXIETY,
                                            ignoreCase = true
                                        )
                                        -> {
                                            calculatorDataSingleton.quizId = "7"
                                            navigate(ToolsCalculatorsDashboardFragmentDirections.actionToolsCalculatorsDashboardFragmentToStressAndAnxietyInputFragment())
                                        }

                                        quizCode.equals(
                                            Constants.QUIZ_CODE_SMART_PHONE,
                                            ignoreCase = true
                                        )
                                        -> {
                                            calculatorDataSingleton.quizId = "4"
                                            navigate(ToolsCalculatorsDashboardFragmentDirections.actionToolsCalculatorsDashboardFragmentToSmartPhoneInputFragment())
                                        }
                                    }

                                }
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

    fun callHeartAgeCalculateApi(
        from: String,
        participationID: String,
        quizID: String,
        answersList: ArrayList<Answer>
    ) = viewModelScope.launch(Dispatchers.Main) {

        calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!
        val questions: MutableList<HeartAgeSaveResponceModel.Question> = mutableListOf()

        for (ans in answersList) {
            val answers: MutableList<HeartAgeSaveResponceModel.Answer> = mutableListOf()
            val answer = HeartAgeSaveResponceModel.Answer(
                questionCode = ans.questionCode,
                code = ans.answerCode,
                score = 0
            )
            answers.add(answer)
            val question = HeartAgeSaveResponceModel.Question(
                quizID = quizID.toInt(),
                templateID = quizID.toInt(),
                code = ans.questionCode,
                description = "",
                sectionID = 1,
                questionTypeCode = 1,
                answers = answers
            )
            questions.add(question)
        }
        Utilities.printLog("Request=> $questions")
        val requestData = HeartAgeSaveResponceModel(
            Gson().toJson(
                HeartAgeSaveResponceModel.JSONDataRequest(
                    questions = questions,
                    participationID = participationID.toInt(),
                    saveResponse = true,
                    calculationModel = calculatorDataSingleton.heartAgeModel
                ), HeartAgeSaveResponceModel.JSONDataRequest::class.java
            ), authToken
        )

        _progressBar.value = Event("Submitting Response...")
        _heartAgeSaveResp.removeSource(heartAgeSaveResponceSource)
        withContext(Dispatchers.IO) {
            heartAgeSaveResponceSource = useCase.invokeHeartAgeSaveResponce(true, requestData)
        }
        _heartAgeSaveResp.addSource(heartAgeSaveResponceSource) {
            try {
                _heartAgeSaveResp.value = it.data

                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        if (it.data != null) {

                            val result = it.data!!.result
                            val parameterReportList = it.data!!.result.parameterReport
                            val heartRiskReportList = it.data!!.result.heartRiskReport
                            val heartAgeSummeryModel: HeartAgeSummeryModel

                            if (from.equals("First", ignoreCase = true)) {
                                calculatorDataSingleton.heartAge = result.heartAge.toString()
                                calculatorDataSingleton.riskScorePercentage =
                                    result.riskScorePercentage.toString()
                                calculatorDataSingleton.riskLabel = result.riskLabel

                                heartAgeSummeryModel = HeartAgeSummeryModel()
                                heartAgeSummeryModel.heartAge = result.heartAge.toString()
                                heartAgeSummeryModel.heartRisk =
                                    result.riskScorePercentage.toString()
                                heartAgeSummeryModel.riskLabel = result.riskLabel

                                val parameterReport = ArrayList<HeartAgeReport>()
                                val heartRiskReport = ArrayList<HeartAgeReport>()
                                var heartAgeReport = HeartAgeReport()
                                heartAgeReport.title = result.heartAgeReport.title
                                heartAgeReport.description = result.heartAgeReport.description
                                heartAgeSummeryModel.heartAgeReport = heartAgeReport

                                for (parameter in parameterReportList) {
                                    heartAgeReport = HeartAgeReport()
                                    heartAgeReport.title = parameter.title
                                    heartAgeReport.description = parameter.description
                                    parameterReport.add(heartAgeReport)
                                }
                                heartAgeSummeryModel.parameterReport = parameterReport

                                for (heartRisk in heartRiskReportList) {
                                    heartAgeReport = HeartAgeReport()
                                    heartAgeReport.title = heartRisk.title
                                    heartAgeReport.description = heartRisk.description
                                    heartRiskReport.add(heartAgeReport)
                                }
                                heartAgeSummeryModel.heartRiskReport = heartRiskReport

                                calculatorDataSingleton.heartAgeSummery = heartAgeSummeryModel

                                calculatorDataSingleton.heartAgeSummeryList.clear()
                                calculatorDataSingleton.heartAgeSummeryList.add(heartAgeSummeryModel)
                                navigate(HeartAgeFragmentDirections.actionHeartAgeFragmentToHeartSummaryFragment())
                            } else {
                                calculatorDataSingleton.heartAge = result.heartAge.toString()
                                calculatorDataSingleton.riskScorePercentage =
                                    result.riskScorePercentage.toString()
                                calculatorDataSingleton.riskLabel = result.riskLabel

                                heartAgeSummeryModel = HeartAgeSummeryModel()
                                heartAgeSummeryModel.heartAge = result.heartAge.toString()
                                heartAgeSummeryModel.heartRisk =
                                    result.riskScorePercentage.toString()
                                heartAgeSummeryModel.riskLabel = result.riskLabel

                                val parameterReport = ArrayList<HeartAgeReport>()
                                val heartRiskReport = ArrayList<HeartAgeReport>()
                                var heartAgeReport = HeartAgeReport()
                                heartAgeReport.title = result.heartAgeReport.title
                                heartAgeReport.description = result.heartAgeReport.description
                                heartAgeSummeryModel.heartAgeReport = heartAgeReport

                                for (parameter in parameterReportList) {
                                    heartAgeReport = HeartAgeReport()
                                    heartAgeReport.title = parameter.title
                                    heartAgeReport.description = parameter.description
                                    parameterReport.add(heartAgeReport)
                                }
                                heartAgeSummeryModel.parameterReport = parameterReport

                                for (heartRisk in heartRiskReportList) {
                                    heartAgeReport = HeartAgeReport()
                                    heartAgeReport.title = heartRisk.title
                                    heartAgeReport.description = heartRisk.description
                                    heartRiskReport.add(heartAgeReport)
                                }
                                heartAgeSummeryModel.heartRiskReport = heartRiskReport

                                calculatorDataSingleton.heartAgeSummery = heartAgeSummeryModel

                                calculatorDataSingleton.heartAgeSummeryList.add(heartAgeSummeryModel)
                                navigate(HeartAgeRecalculateFragmentDirections.actionHeartAgeRecalculateFragmentToHeartSummaryFragment())
                            }

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

    fun callDiabetesSaveResponseApi(
        participationID: String,
        quizID: String,
        answersList: ArrayList<Answer>
    ) = viewModelScope.launch(Dispatchers.Main) {

        calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!
        val questions: MutableList<DiabetesSaveResponceModel.Question> = mutableListOf()

        for (ans in answersList) {
            val answers: MutableList<DiabetesSaveResponceModel.Answer> = mutableListOf()
            val answer = DiabetesSaveResponceModel.Answer(
                questionCode = ans.questionCode,
                code = ans.answerCode,
                score = ans.value.toInt()
            )
            answers.add(answer)

            val question = DiabetesSaveResponceModel.Question(
                quizID = quizID.toInt(),
                templateID = quizID.toInt(),
                code = ans.questionCode,
                description = "",
                sectionID = 1,
                questionTypeCode = 1,
                answers = answers
            )
            questions.add(question)
        }
        Utilities.printLog("Request=> $questions")
        val requestData = DiabetesSaveResponceModel(
            Gson().toJson(
                DiabetesSaveResponceModel.JSONDataRequest(
                    questions = questions,
                    participationID = participationID.toInt()
                ), DiabetesSaveResponceModel.JSONDataRequest::class.java
            ), authToken
        )

        _progressBar.value = Event("Submitting Responce...")
        _diabetesSaveResp.removeSource(diabetesSaveResponceSource)
        withContext(Dispatchers.IO) {
            diabetesSaveResponceSource = useCase.invokeDiabetesSaveResponce(true, requestData)
        }
        _diabetesSaveResp.addSource(diabetesSaveResponceSource) {
            try {
                _diabetesSaveResp.value = it.data

                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        if (it.data != null) {
                            val diabetesData = it.data!!.result
                            val diabetesSummeryModel = DiabetesSummeryModel()
                            diabetesSummeryModel.totalScore = diabetesData.totalScore.toString()
                            diabetesSummeryModel.riskLabel = diabetesData.riskLabel
                            diabetesSummeryModel.probabilityPercentage =
                                diabetesData.probabilityPercentage.toString()

                            diabetesSummeryModel.goodIn = diabetesData.observations.goodIn
                            diabetesSummeryModel.needImprovement =
                                diabetesData.observations.needImprovements
                            diabetesSummeryModel.nonModifiableRisk =
                                diabetesData.observations.nonModifiableRisk

                            if (diabetesData.detailedReport.isNotEmpty()) {

                                val detailedReport = diabetesData.detailedReport
                                val titleArrayMap = ArrayMap<String, ArrayList<SubSectionModel>>()
                                var subSectionModelArrayList: ArrayList<SubSectionModel>
                                var subSectionModel: SubSectionModel
                                var bulletMenuList: ArrayList<String>

                                for (reportItem in detailedReport) {
                                    subSectionModelArrayList = ArrayList()
                                    val contentList = reportItem.subSection
                                    for (content in contentList) {
                                        subSectionModel = SubSectionModel()
                                        subSectionModel.type = content.type
                                        subSectionModel.text = content.text
                                        bulletMenuList = ArrayList()
                                        for (element in content.list) {
                                            bulletMenuList.add(element)
                                        }
                                        subSectionModel.bulletPointList = bulletMenuList
                                        subSectionModelArrayList.add(subSectionModel)
                                    }
                                    titleArrayMap[reportItem.title] = subSectionModelArrayList
                                }
                                diabetesSummeryModel.detailReport = titleArrayMap
                            }
                            calculatorDataSingleton.diabetesSummeryModel = diabetesSummeryModel

                            navigate(DiabetesCalculatorInputFragmentDirections.actionDiabetesCalculatorFragmentToDiabetesSummaryFragment())
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

    fun callHypertensionSaveResponseApi(
        from: String,
        participationID: String,
        quizID: String,
        answersList: ArrayList<Answer>
    ) = viewModelScope.launch(Dispatchers.Main) {

        calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!
        val questions: MutableList<HypertensionSaveResponceModel.Question> = mutableListOf()

        for (i in 0 until answersList.size) {
            val answers: MutableList<HypertensionSaveResponceModel.Answer> = mutableListOf()
            val answer = HypertensionSaveResponceModel.Answer(
                questionCode = answersList[i].questionCode,
                code = answersList[i].answerCode,
                score = 0
            )
            answers.add(answer)
            val question = HypertensionSaveResponceModel.Question(
                quizID = quizID.toInt(),
                templateID = quizID.toInt(),
                code = answersList[i].questionCode,
                description = "",
                sectionID = 1,
                questionTypeCode = 1,
                answers = answers
            )
            questions.add(question)
        }
        Utilities.printLog("Request=> $questions")
        val requestData = HypertensionSaveResponceModel(
            Gson().toJson(
                HypertensionSaveResponceModel.JSONDataRequest(
                    questions = questions,
                    participationID = participationID.toInt(),
                    saveResponse = true,
                    calculationModel = calculatorDataSingleton.heartAgeModel
                ), HypertensionSaveResponceModel.JSONDataRequest::class.java
            ), authToken
        )

        _progressBar.value = Event("Submitting Response...")
        _hypertensionSaveResp.removeSource(hypertensionSaveResponceSource)
        withContext(Dispatchers.IO) {
            hypertensionSaveResponceSource =
                useCase.invokeHypertensionSaveResponce(true, requestData)
        }
        _hypertensionSaveResp.addSource(hypertensionSaveResponceSource) {

            try {
                _hypertensionSaveResp.value = it.data
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        if (it.data != null) {

                            val result = it.data!!.result
                            val stageResp = it.data!!.result.stage
                            val recommendationResp = it.data!!.result.recommendation
                            val smokingReportResp = it.data!!.result.smokingReport
                            val bpReportResp = it.data!!.result.bPReport
                            val parameterReportResp = it.data!!.result.parameterReport

                            if (from.equals("First", ignoreCase = true)) {
                                val model = HypertensionSummeryModel()

                                model.color = result.color
                                model.diastolicBp = result.diastolicBp.toString()
                                model.systolicBp = result.systolicBp.toString()
                                model.status = result.status

                                val stage = HypertensionResultPojo()
                                stage.title = stageResp.title
                                stage.subTitle = stageResp.subTitle
                                stage.description = stageResp.description
                                model.stage = stage

                                val recommendation = HypertensionResultPojo()
                                recommendation.title = recommendationResp.title
                                recommendation.subTitle = recommendationResp.subTitle
                                recommendation.description = recommendationResp.description
                                model.recommendation = recommendation

                                val smokingReport = HypertensionResultPojo()
                                smokingReport.title = smokingReportResp.title
                                smokingReport.description = smokingReportResp.description
                                model.smokingReport = smokingReport

                                val bpReport = HypertensionResultPojo()
                                bpReport.title = bpReportResp.title
                                bpReport.description = bpReportResp.description
                                model.bpReport = bpReport

                                val parameterReport = HypertensionResultPojo()
                                parameterReport.title = parameterReportResp.title
                                parameterReport.description = parameterReportResp.description
                                model.parameterReport = parameterReport

                                val listRisk = ArrayList<String>()
                                listRisk.add(result.risk1.toString())
                                listRisk.add(result.risk2.toString())
                                listRisk.add(result.risk4.toString())

                                val listOptimumRisk = ArrayList<String>()
                                listOptimumRisk.add(result.optRisk1.toString())
                                listOptimumRisk.add(result.optRisk2.toString())
                                listOptimumRisk.add(result.optRisk4.toString())

                                val masterRiskList = ArrayMap<String, ArrayList<String>>()
                                masterRiskList["ORISK"] = listOptimumRisk
                                masterRiskList["RISK1"] = listRisk
                                model.hypertensionRisk = masterRiskList

                                calculatorDataSingleton.hypertensionSummery = model
                                navigate(HypertensionInputFragmentDirections.actionHypertensionInputFragmentToHypertensionSummeryFragment())
                            } else {
                                val listRisk = ArrayList<String>()
                                listRisk.add(result.risk1.toString())
                                listRisk.add(result.risk2.toString())
                                listRisk.add(result.risk4.toString())

                                calculatorDataSingleton.hypertensionSummery.hypertensionRisk["RISK2"] =
                                    listRisk
                                navigate(HypertensionRecalculateFragmentDirections.actionHypertensionRecalculateFragmentToHypertensionSummeryFragment())
                            }

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

    fun callStressAndAnxietySaveResponseApi(
        participationID: String,
        quizID: Int,
        answersList: ArrayList<Answer>
    ) = viewModelScope.launch(Dispatchers.Main) {

        calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!
        val questions: MutableList<StressAndAnxietySaveResponceModel.Question> = mutableListOf()

        for (ans in answersList) {
            val answers: MutableList<StressAndAnxietySaveResponceModel.Answer> = mutableListOf()
            val answer = StressAndAnxietySaveResponceModel.Answer(
                questionCode = ans.questionCode,
                code = ans.answerCode,
                score = ans.value.toInt()
            )
            answers.add(answer)
            val question = StressAndAnxietySaveResponceModel.Question(
                quizID = quizID,
                templateID = quizID,
                code = ans.questionCode,
                description = "",
                sectionID = 1,
                questionTypeCode = 1,
                answers = answers
            )
            questions.add(question)
        }
        Utilities.printLog("Request=> $questions")
        val requestData = StressAndAnxietySaveResponceModel(
            Gson().toJson(
                StressAndAnxietySaveResponceModel.JSONDataRequest(
                    participationID = participationID.toInt(),
                    questions = questions
                ), StressAndAnxietySaveResponceModel.JSONDataRequest::class.java
            ), authToken
        )

        _progressBar.value = Event("Submitting Responce...")
        _stressAndAnxietySaveResp.removeSource(stressAndAnxietySaveRespSource)
        withContext(Dispatchers.IO) {
            stressAndAnxietySaveRespSource =
                useCase.invokeStressAndAnxietySaveResponce(true, requestData)
        }
        _stressAndAnxietySaveResp.addSource(stressAndAnxietySaveRespSource) {
            try {
                _stressAndAnxietySaveResp.value = it.data
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        if (it.data != null) {

                            val stressCalculatorSummeryModel = StressCalculatorSummeryModel()
                            val depression = it.data!!.result.depression
                            val anxiety = it.data!!.result.anxiety
                            val stress = it.data!!.result.stress
                            val stage = it.data!!.result.stage
                            val parameterReport = it.data!!.result.parameterReport

                            val depressionData = StressData()
                            depressionData.riskLabel = depression.riskLabel
                            depressionData.score = depression.score.toString()
                            depressionData.stage = depression.stage.toString()
                            depressionData.normal = depression.scale.normal
                            depressionData.mild = depression.scale.mild
                            depressionData.moderate = depression.scale.moderate
                            depressionData.severe = depression.scale.severe
                            depressionData.extremeSevere = depression.scale.extremelySevere
                            stressCalculatorSummeryModel.depression = depressionData

                            val anxietyData = StressData()
                            anxietyData.riskLabel = anxiety.riskLabel
                            anxietyData.score = anxiety.score.toString()
                            anxietyData.stage = anxiety.stage.toString()
                            anxietyData.normal = anxiety.scale.normal
                            anxietyData.mild = anxiety.scale.mild
                            anxietyData.moderate = anxiety.scale.moderate
                            anxietyData.severe = anxiety.scale.severe
                            anxietyData.extremeSevere = anxiety.scale.extremelySevere
                            stressCalculatorSummeryModel.anxiety = anxietyData

                            val stressData = StressData()
                            stressData.riskLabel = stress.riskLabel
                            stressData.score = stress.score.toString()
                            stressData.stage = stress.stage.toString()
                            stressData.normal = stress.scale.normal
                            stressData.mild = stress.scale.mild
                            stressData.moderate = stress.scale.moderate
                            stressData.severe = stress.scale.severe
                            stressData.extremeSevere = stress.scale.extremelySevere
                            stressCalculatorSummeryModel.stress = stressData

                            stressCalculatorSummeryModel.stage = stage.toString()

                            val paramList = ArrayList<HypertensionResultPojo>()
                            for (i in parameterReport) {
                                val model = HypertensionResultPojo()
                                model.title = i.title
                                model.description = i.description
                                paramList.add(model)
                            }
                            stressCalculatorSummeryModel.parameterReport = paramList

                            calculatorDataSingleton.stressSummeryData = stressCalculatorSummeryModel
                            navigate(StressAndAnxietyInputFragmentDirections.actionStressAndAnxietyInputFragmentToStressAndAnxietySummeryFragment())
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

    fun callSmartPhoneSaveResponseApi(
        participationID: String,
        quizID: String,
        answersList: ArrayList<Answer>
    ) =
        viewModelScope.launch(Dispatchers.Main) {

            calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!
            val questions: MutableList<SmartPhoneSaveResponceModel.Question> = mutableListOf()

            var questionCode = ""
            for (ans in answersList) {
                questionCode = dataHandler.getSmartPhoneAddictionQuesCode(ans.questionCode)
                val answers: MutableList<SmartPhoneSaveResponceModel.Answer> = mutableListOf()
                val answer = SmartPhoneSaveResponceModel.Answer(
                    questionCode = questionCode,
                    code = ans.value,
                    score = ans.value.toInt()
                )
                answers.add(answer)
                val question = SmartPhoneSaveResponceModel.Question(
                    answers = answers,
                    code = questionCode,
                    description = "",
                    questionTypeCode = 1,
                    quizID = quizID.toInt(),
                    sectionID = 1,
                    templateID = quizID.toInt()
                )
                questions.add(question)
            }
            //Utilities.printLog("Request=> $questions")
            Utilities.printData("SmartPhoneRequest", questions, true)

            val requestData = SmartPhoneSaveResponceModel(
                Gson().toJson(
                    SmartPhoneSaveResponceModel.JSONDataRequest(
                        participationID = participationID.toInt(),
                        questions = questions
                    ), SmartPhoneSaveResponceModel.JSONDataRequest::class.java
                ), authToken
            )

            _progressBar.value = Event("Submitting Responce...")
            _smartPhoneSaveResp.removeSource(smartPhoneSaveRespSource)
            withContext(Dispatchers.IO) {
                smartPhoneSaveRespSource = useCase.invokeSmartPhoneSaveResponce(true, requestData)
            }
            _smartPhoneSaveResp.addSource(smartPhoneSaveRespSource) {
                try {
                    _smartPhoneSaveResp.value = it.data
                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            if (it.data != null) {
                                val score = it.data!!.result!!.score!!.toInt().toString()
                                val smartPhoneSummeryData = SmartPhoneSummeryModel()
                                smartPhoneSummeryData.score = it.data!!.result!!.score
                                smartPhoneSummeryData.smartPhoneRisk =
                                    it.data!!.result!!.smartPhoneRisk!!
                                smartPhoneSummeryData.smartPhoneReport =
                                    it.data!!.result!!.smartPhoneReport!!
                                calculatorDataSingleton.smartPhoneSummeryData =
                                    smartPhoneSummeryData
                                navigate(
                                    SmartPhoneInputFragmentDirections.actionSmartPhoneInputFragmentToSmartPhoneAddictionSummaryFragment(
                                        score
                                    )
                                )
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

    /*    fun callSmartPhoneSaveResponseApi(score: String, participationID: String, quizID: String) =
            viewModelScope.launch(Dispatchers.Main) {

                calculatorDataSingleton = CalculatorDataSingleton.getInstance()!!
                val questionCode = "SMARTPHADDICTION"
                val questions: MutableList<SmartPhoneSaveResponceModel.Question> = mutableListOf()
                val answers: MutableList<SmartPhoneSaveResponceModel.Answer> = mutableListOf()

                val answer = SmartPhoneSaveResponceModel.Answer(
                    questionCode = questionCode,
                    code = score,
                    score = score)
                answers.add(answer)

                val question = SmartPhoneSaveResponceModel.Question(
                    quizID = quizID,
                    templateID = quizID,
                    code = questionCode,
                    description = "",
                    sectionID = 1,
                    questionTypeCode = 1,
                    answers = answers)
                questions.add(question)

                val requestData = SmartPhoneSaveResponceModel(Gson().toJson(SmartPhoneSaveResponceModel.JSONDataRequest(
                    participationID = participationID,
                    questions = questions), SmartPhoneSaveResponceModel.JSONDataRequest::class.java), authToken)

                _progressBar.value = Event("Submitting Responce...")
                _smartPhoneSaveResp.removeSource(smartPhoneSaveRespSource)
                withContext(Dispatchers.IO) {
                    smartPhoneSaveRespSource = useCase.invokeSmartPhoneSaveResponce(true, requestData)
                }
                _smartPhoneSaveResp.addSource(smartPhoneSaveRespSource) {
                    _smartPhoneSaveResp.value = it.data

                    if (it.status == Resource.Status.SUCCESS) {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        if (it.data != null) {
                            calculatorDataSingleton.smartPhoneScore = score
                            navigate(SmartPhoneInputFragmentDirections.actionSmartPhoneInputFragmentToSmartPhoneAddictionSummaryFragment(score))
                        }
                    }
                    if (it.status == Resource.Status.ERROR) {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        if (it.errorNumber.equals("1100014", true)) {
                            _sessionError.value = Event(true)
                        } else {
                            toastMessage(it.errorMessage)
                        }
                    }
                }

            }*/

    fun getMedicalProfileSummary(forceRefresh: Boolean) = viewModelScope.launch(Dispatchers.Main) {

        val requestData = HraMedicalProfileSummaryModel(
            Gson().toJson(
                HraMedicalProfileSummaryModel.JSONDataRequest(
                    PersonID = personId
                ), HraMedicalProfileSummaryModel.JSONDataRequest::class.java
            ), authToken
        )

        if (forceRefresh) {
            _progressBar.value = Event("Initialising Calculator")
        }
        _medicalProfileSummary.removeSource(medicalProfileSummarySource)
        withContext(Dispatchers.IO) {
            medicalProfileSummarySource = useCase.invokeMedicalProfileSummary(
                isForceRefresh = forceRefresh,
                data = requestData,
                personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
            )
        }
        _medicalProfileSummary.addSource(medicalProfileSummarySource) {
            try {
                _medicalProfileSummary.value = it.data
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        if (forceRefresh) {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                        }
                        if (it.data != null) {
                            val summary = it.data!!.MedicalProfileSummary!!
                            Utilities.printLog("MedicalProfileSummary :- $it.data")
                            try {
                                val userInfo = UserInfoModel.getInstance()!!
                                userInfo.setAge(summary.age.toString())
                                userInfo.isMale = summary.gender.equals("M", ignoreCase = true)
                                userInfo.dob = summary.dateOfBirth
                                userInfo.setHeight(summary.height.toString())
                                userInfo.setWeight(summary.weight.toString())
                                userInfo.setWaistSize(summary.waist.toString())
                                userInfo.setSystolicBp(summary.systolic.toString())
                                userInfo.setDiastolicBp(summary.diastolic.toString())
                                userInfo.isDataLoaded = true
                                getParameterDetails()
                                apiStatusLiveData.postValue(true)
                                //                        callGetLabRecords(false)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                    Resource.Status.ERROR -> {
                        if (forceRefresh) {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                        }
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

    private fun callGetLabRecords(forceRefresh: Boolean) = viewModelScope.launch(Dispatchers.Main) {

        val requestData = LabRecordsModel(
            Gson().toJson(
                LabRecordsModel.JSONDataRequest(
                    PersonID = personId
                ), LabRecordsModel.JSONDataRequest::class.java
            ), authToken
        )

        _progressBar.value = Event("Initialising Calculator")
        _labRecords.removeSource(labRecordSource)
        withContext(Dispatchers.IO) {
            labRecordSource =
                useCase.invokeLabRecords(isForceRefresh = forceRefresh, data = requestData)
        }
        _labRecords.addSource(labRecordSource) {
            try {
                _labRecords.value = it.data
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        if (it != null) {
                            val labRecords = it.data!!.LabRecords!!
                            try {
                                if (labRecords.isNotEmpty()) {
                                    for (record in labRecords) {
                                        val parameterCode = record.ParameterCode!!
                                        val value = record.Value!!
                                        saveHRALabDetails(parameterCode, value)
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
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

    private fun saveHRALabDetails(strParameterCode: String, strLabValue: String) {
        if (!Utilities.isNullOrEmpty(strParameterCode) && isParameterCode(strParameterCode)
            && !Utilities.isNullOrEmpty(strLabValue)
        ) {
            val userInfo = UserInfoModel.getInstance()!!
            if (strParameterCode.equals("CHOL_TOTAL", ignoreCase = true)) {
                userInfo.setCholesterol(strLabValue)
            }
            if (strParameterCode.equals("CHOL_HDL", ignoreCase = true)) {
                userInfo.setHdl(strLabValue)
            }
        }
    }

    private fun isParameterCode(strParameterCode: String): Boolean {
        return strParameterCode.equals("CHOL_TOTAL", ignoreCase = true) ||
                strParameterCode.equals("CHOL_HDL", ignoreCase = true)
    }

    fun updateUserPreference(unit: String?) {
        if (!unit.isNullOrEmpty()) {
            when (unit.lowercase()) {
                /*"cm" -> {
                    sharedPref.edit().putString(PreferenceConstants.HEIGHT_PREFERENCE, "cm").apply()
                }
                "kg" -> {
                    sharedPref.edit().putString(PreferenceConstants.WEIGHT_PREFERENCE, "kg").apply()
                }
                "lbs" -> {
                    sharedPref.edit().putString(PreferenceConstants.WEIGHT_PREFERENCE, "lib")
                        .apply()
                }
                "feet/inch" -> {
                    sharedPref.edit().putString(PreferenceConstants.HEIGHT_PREFERENCE, "feet")
                        .apply()
                }*/
            }
        }
    }

    fun getParameterDetails() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            var hdl = useCase.invokeLatestRecordHisBaseOnParameterCode("CHOL_HDL", personId)
            var cholestrerol =
                useCase.invokeLatestRecordHisBaseOnParameterCode("CHOL_TOTAL", personId)
            Utilities.printLog("HDL=> " + hdl + " :: CHOL_TOTAL=> " + cholestrerol)

            val userInfo = UserInfoModel.getInstance()!!
            if (cholestrerol != null) {
                if (!cholestrerol.isNullOrEmpty() && cholestrerol.size > 0) {
                    userInfo.setCholesterol(cholesterol = cholestrerol.get(0).value.toString())
                } else {
                    userInfo.setCholesterol("0")
                }
            }
            if (hdl != null) {
                if (!hdl.isNullOrEmpty() && hdl.size > 0) {
                    userInfo.setHdl(hdl = hdl.get(0).value.toString())
                } else {
                    userInfo.setHdl("0")
                }
            }
            //ToolsTrackerSingleton.instance.hraSummary
        }
    }

    fun getPreference(type: String): String {
        if (type.equals("HEIGHT", true)) {
            return preferenceUtils.getPreference(PreferenceConstants.HEIGHT_PREFERENCE, "feet")
        } else if (type.equals("WEIGHT", true)) {
            return preferenceUtils.getPreference(PreferenceConstants.WEIGHT_PREFERENCE, "kg")
        }
        return ""
    }

}
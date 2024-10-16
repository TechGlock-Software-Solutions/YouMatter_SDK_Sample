package com.techglock.health.app.fitness_tracker.util

//import com.caressa.allizhealth.app.model.tempconst.Configuration
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Handler
import android.os.Looper
import com.techglock.health.app.common.base.CustomProgressBar
import com.techglock.health.app.common.constants.Configuration
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.PreferenceConstants
import com.techglock.health.app.common.fitness.FitRequestCode
import com.techglock.health.app.common.fitness.FitnessDataManager
import com.techglock.health.app.common.utils.*
import com.techglock.health.app.fitness_tracker.FitnessApiService
import com.techglock.health.app.fitness_tracker.common.StepsDataSingleton
import com.techglock.health.app.fitness_tracker.ui.FitnessDataActivity
import com.techglock.health.app.fitness_tracker.util.workmanager.JobDispatcherStepsHelper
import com.techglock.health.app.model.fitness.StepsHistoryModel
import com.techglock.health.app.model.fitness.StepsSaveListModel
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class StepCountHelper @Inject constructor(private val context: Context) {

    private val TAG = StepCountHelper::class.java.simpleName

    /* @Inject
     lateinit var viewModel: ActivityTrackerViewModel


 */
    private var activity: FitnessDataActivity? = null

    private var progressDialog: CustomProgressBar? = null

    private var bgSync = false
    private var syncCount = 0

    private val stepsDataSingleton = StepsDataSingleton.instance!!

    fun initialize(mContext: Context?) {
        //ToDo: Keyur, New - Steps Sync job scheduler
        Utilities.printLogError("$TAG initialize")
        JobDispatcherStepsHelper.checkJobManagerInstanceAvailability(mContext)
        JobDispatcherStepsHelper.scheduleMidnightJobDispatcher()
        JobDispatcherStepsHelper.schedulePeriodicJobDispatcher()
    }

    fun synchronize(
        mContext: Context?,
        mActivity: FitnessDataActivity?,
        showProgress: Boolean = true
    ) {
        activity = mActivity
        showProgress(showProgress)
        refreshStepCountSync(mContext)
    }

    /*    fun synchronize(mContext: Context?) {
            refreshStepCountSync(mContext)
        }*/

    fun synchronizeForce(mContext: Context?) {
        refreshStepCountSyncForce(mContext)
    }

    @Synchronized
    fun pullFitnessDataFromGoogleFitForDate(mContext: Context?, startDate: Date?, endDate: Date?) {
        getStepsDelta(mContext, startDate, endDate)
    }

    private fun refreshStepCountSync(mContext: Context?) {
        try {
            val stepsDataSingleton = StepsDataSingleton.instance!!
            var calendar = Calendar.getInstance()
            var dateBefore: Date? = null
            var dateAfter = Calendar.getInstance().time
            try {
                if (stepsDataSingleton.stepHistoryList.isNotEmpty()) {
                    val dateListHistory = ArrayList<Date?>()
                    for (i in 0 until stepsDataSingleton.stepHistoryList.size) {
                        dateListHistory.add(
                            DateHelper.getDate(
                                stepsDataSingleton.stepHistoryList[i].recordDate,
                                DateHelper.SERVER_DATE_YYYYMMDD
                            )
                        )
                    }
                    Utilities.printLogError(
                        "LAST RECORD_DATE FOUND---> ${
                            Collections.max(
                                dateListHistory
                            )
                        }"
                    )
                    if (Collections.max(dateListHistory) != null) {
                        //DateHelper.convertStrToDateOBJ(Collections.min(dateListHistory));
                        dateBefore = Collections.max(dateListHistory)
                    }
                } else {
                    /*                    calendar = Calendar.getInstance()
                                        calendar.add(Calendar.DATE, -365)
                                        dateBefore = calendar.time
                                        dateAfter = Calendar.getInstance().time*/
                    val differenceInDays = DateHelper.getDateDifferenceInDays(
                        activity?.viewModel?.joiningDate ?: "",
                        DateHelper.currentDateAsStringyyyyMMdd,
                        DateHelper.SERVER_DATE_YYYYMMDD
                    )
                    calendar = Calendar.getInstance()
                    calendar.add(Calendar.DATE, -differenceInDays)
                    dateBefore = calendar.time
                    dateAfter = Calendar.getInstance().time
                }
            } catch (e: Exception) {
                e.printStackTrace()
                calendar = Calendar.getInstance()
                calendar.add(Calendar.DATE, -365)
                dateBefore = calendar.time
                dateAfter = Calendar.getInstance().time
            }
            Utilities.printLogError("dateBefore---> $dateBefore")
            Utilities.printLogError("dateAfter---> $dateAfter")

            if (dateBefore != null && dateAfter != null) {
                val difference = dateAfter.time - dateBefore.time
                val daysDifference = (difference / (1000 * 60 * 60 * 24)).toInt()
                //Utilities.printLogError("$TAG StepCountHelper daysDifference ---> $daysDifference")
                //ToDo: Keyur, force sync for at-least 1 day
                //daysDifference = (Math.max(daysDifference, 1));
                if (daysDifference >= 0) {
                    calendar = Calendar.getInstance()
                    calendar.add(Calendar.DATE, -daysDifference)
                    Utilities.printLogError("Sync for date ---> ${calendar.time}")
                    pullFitnessDataFromGoogleFitForDate(
                        mContext,
                        calendar.time,
                        Calendar.getInstance().time
                    )
                }
            }

            /*            if (dateBefore != null!! and dateAfter != null) {
                            val difference = dateAfter.time - dateBefore!!.time
                            val daysDifference = (difference / (1000 * 60 * 60 * 24)).toInt()
                            //Utilities.printLog("StepCountHelper", " daysDifference -- $daysDifference")
                            //ToDo: Keyur, force sync for at-least 1 day
                            //daysDifference = (Math.max(daysDifference, 1));
                            if (daysDifference >= 0) {
                                calendar = Calendar.getInstance()
                                calendar.add(Calendar.DATE, -daysDifference)
                                //Utilities.printLog(TAG, " sync for date -- " + calendar.time)
                                pullFitnessDataFromGoogleFitForDate(mContext, calendar.time, Calendar.getInstance().time)
                            }
                        }*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*    private fun refreshStepCountSyncForce1(mContext: Context?) {
            val policyDetails = JSONArray()
            try {
                var calendar = Calendar.getInstance()
                var dateBefore: Date? = null
                var dateAfter = Calendar.getInstance().time
                try {
                    calendar = Calendar.getInstance()
                    calendar.add(Calendar.DATE, -365)
                    dateBefore = calendar.time
                    dateAfter = Calendar.getInstance().time
                    val dateList = ArrayList<Date>()
                    for (i in 0 until policyDetails.length()) {
                        dateList.add(DateHelper.getDate(
                            policyDetails.getJSONObject(i).getString("StartDate"),
                            DateHelper.SERVER_DATE_YYYYMMDD))
                    }
                    if (dateList.size > 0) {
                        //Utilities.printLog("%s%s", TAG, Collections.min(dateList).toString())
                        dateBefore = Collections.min(dateList)
                    }
                    if (Calendar.getInstance().time.before(dateBefore)) {
                        //Utilities.printLog("today.isBefore--> $dateBefore")
                        calendar = Calendar.getInstance()
                        calendar.add(Calendar.DATE, -365)
                        dateBefore = calendar.time
                        dateAfter = Calendar.getInstance().time
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    calendar = Calendar.getInstance()
                    calendar.add(Calendar.DATE, -365)
                    dateBefore = calendar.time
                    dateAfter = Calendar.getInstance().time
                }

                if (dateBefore != null && dateAfter != null) {
                    val difference = dateAfter.time - dateBefore.time
                    val daysDifference = (difference / (1000 * 60 * 60 * 24)).toInt()
                    //Utilities.printLog("StepCountHelper", " daysDifference -- " + daysDifference);
                    //ToDo: Keyur, force sync for at-least 30 days, will change logic for force sync in next phase
                    //daysDifference = (Math.max(daysDifference, 30));
                    if (daysDifference >= 0) {
                        calendar = Calendar.getInstance()
                        calendar.add(Calendar.DATE, -daysDifference)
                        //Utilities.printLog("$TAG sync for date ---> ${calendar.time}")
                        pullFitnessDataFromGoogleFitForDate(
                            mContext,
                            calendar.time,
                            Calendar.getInstance().time
                        )
                    }
                }

    *//*            if (dateBefore != null!! and dateAfter != null) {
                val difference = dateAfter.time - dateBefore!!.time
                val daysDifference = (difference / (1000 * 60 * 60 * 24)).toInt()
                //Utilities.printLog("StepCountHelper", " daysDifference -- " + daysDifference);
                //ToDo: Keyur, force sync for at-least 30 days, will change logic for force sync in next phase
                //daysDifference = (Math.max(daysDifference, 30));
                if (daysDifference >= 0) {
                    calendar = Calendar.getInstance()
                    calendar.add(Calendar.DATE, -daysDifference)
                    //Utilities.printLog(TAG, " sync for date -- " + calendar.time)
                    pullFitnessDataFromGoogleFitForDate(mContext, calendar.time, Calendar.getInstance().time)
                }
            }*//*
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/

    private fun refreshStepCountSyncForce(mContext: Context?) {
        val policyDetails = JSONArray()
        try {
            var calendar = Calendar.getInstance()
            var dateBefore: Date? = null
            var dateAfter = Calendar.getInstance().time
            // TODO : Force Sync from the Current day Selected by the User
            try {
                calendar = Calendar.getInstance()

                //calendar.add(Calendar.DATE, -365)
                dateBefore = calendar.time
                dateAfter = Calendar.getInstance().time
                val dateList = ArrayList<Date>()
                for (i in 0 until policyDetails.length()) {
                    dateList.add(
                        DateHelper.getDate(
                            policyDetails.getJSONObject(i).getString("StartDate"),
                            DateHelper.SERVER_DATE_YYYYMMDD
                        )
                    )
                }
                if (dateList.size > 0) {
                    //Utilities.printLog("%s%s", TAG, Collections.min(dateList).toString())
                    dateBefore = Collections.min(dateList)
                }
                if (Calendar.getInstance().time.before(dateBefore)) {
                    //Utilities.printLog("today.isBefore--> $dateBefore")
                    calendar = Calendar.getInstance()
                    //calendar.add(Calendar.DATE, -365)
                    dateBefore = calendar.time
                    dateAfter = Calendar.getInstance().time
                }
            } catch (e: Exception) {
                e.printStackTrace()
                calendar = Calendar.getInstance()
                //calendar.add(Calendar.DATE, -365)
                dateBefore = calendar.time
                dateAfter = Calendar.getInstance().time
            }

            if (dateBefore != null && dateAfter != null) {
                val difference = dateAfter.time - dateBefore.time
                val daysDifference = (difference / (1000 * 60 * 60 * 24)).toInt()
                //Utilities.printLog("StepCountHelper", " daysDifference -- " + daysDifference);
                //ToDo: Keyur, force sync for at-least 30 days, will change logic for force sync in next phase
                //daysDifference = (Math.max(daysDifference, 30));
                if (daysDifference >= 0) {
                    calendar = Calendar.getInstance()
                    calendar.add(Calendar.DATE, -daysDifference)
                    //Utilities.printLog("$TAG sync for date ---> ${calendar.time}")
                    pullFitnessDataFromGoogleFitForDate(
                        mContext,
                        calendar.time,
                        Calendar.getInstance().time
                    )
                }
            }

            /*            if (dateBefore != null!! and dateAfter != null) {
                            val difference = dateAfter.time - dateBefore!!.time
                            val daysDifference = (difference / (1000 * 60 * 60 * 24)).toInt()
                            //Utilities.printLog("StepCountHelper", " daysDifference -- " + daysDifference);
                            //ToDo: Keyur, force sync for at-least 30 days, will change logic for force sync in next phase
                            //daysDifference = (Math.max(daysDifference, 30));
                            if (daysDifference >= 0) {
                                calendar = Calendar.getInstance()
                                calendar.add(Calendar.DATE, -daysDifference)
                                //Utilities.printLog(TAG, " sync for date -- " + calendar.time)
                                pullFitnessDataFromGoogleFitForDate(mContext, calendar.time, Calendar.getInstance().time)
                            }
                        }*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Synchronized
    fun getStepsDelta(mContext: Context?, startDate: Date?, endDate: Date?) {
        FitnessDataManager.getInstance(mContext)!!
            .readHistoryData(startDate!!, endDate!!).addOnCompleteListener { task ->
                val googleFitData = FitnessDataManager.getInstance(mContext)!!.fitnessDataArray
                Utilities.printLogError("Data from Google Fit--->$googleFitData")
                if (googleFitData.length() > 0) {
                    saveStepsList(googleFitData)
                    if (activity != null) {
                        activity!!.updateFromForceSync()
                    }
                } else {
                    showProgress(false)
                    if (syncCount == 0) {
                        syncForLastTwoYears()
                    }
                }
            }
    }

    private fun saveStepsList(fitnessDataJSONArray: JSONArray) {
        try {
            val personId =
                Utilities.preferenceUtils.getPreference(PreferenceConstants.ADMIN_PERSON_ID, "0")
            if (!Utilities.isNullOrEmptyOrZero(personId)) {
                val jsonArrayStepsDetails = JSONArray()
                var jsonObjStepsDetails: JSONObject
                for (i in 0 until fitnessDataJSONArray.length()) {
                    if (fitnessDataJSONArray.getJSONObject(i) != null) {
                        val stepsDataObj = fitnessDataJSONArray.getJSONObject(i)
                        jsonObjStepsDetails = JSONObject()
                        jsonObjStepsDetails.put("PersonID", personId)
                        jsonObjStepsDetails.put(
                            "RecordDate",
                            DateHelper.convertDateTimeValue(
                                stepsDataObj.getString(Constants.RECORD_DATE),
                                DateHelper.SERVER_DATE_YYYYMMDD,
                                DateHelper.SERVER_DATE_YYYYMMDD
                            )!!
                        )
                        jsonObjStepsDetails.put(
                            "Count",
                            stepsDataObj.getString(Constants.STEPS_COUNT)
                        )
                        jsonObjStepsDetails.put(
                            "Calories",
                            stepsDataObj.getString(Constants.CALORIES)
                        )
                        jsonObjStepsDetails.put(
                            "Distance",
                            stepsDataObj.getString(Constants.DISTANCE)
                        )
                        jsonArrayStepsDetails.put(jsonObjStepsDetails)
                    }
                }

                val jsonObjectJSONData = JSONObject()
                jsonObjectJSONData.put("StepsDetails", jsonArrayStepsDetails)
                getResponseFromServer(jsonObjectJSONData, 2)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getStepsHistoryAndSync() {
        try {
            val personId =
                Utilities.preferenceUtils.getPreference(PreferenceConstants.ADMIN_PERSON_ID, "0")
            if (!Utilities.isNullOrEmptyOrZero(personId)) {
                bgSync = true
                val df = SimpleDateFormat(DateHelper.SERVER_DATE_YYYYMMDD, Locale.ENGLISH)
                val calEndTime = Calendar.getInstance()
                calEndTime.add(Calendar.DATE, -30)
                val fromDate = df.format(calEndTime.time)
                val toDate = DateHelper.currentDateAsStringyyyyMMdd

                val jsonObjSearchCriteria = JSONObject()
                jsonObjSearchCriteria.put("PersonID", personId)
                jsonObjSearchCriteria.put("FromDate", fromDate)
                jsonObjSearchCriteria.put("ToDate", toDate)

                val jsonObjectJSONData = JSONObject()
                jsonObjectJSONData.put("SearchCriteria", jsonObjSearchCriteria)
                getResponseFromServer(jsonObjectJSONData, 1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getResponseFromServer(jsonRequest: JSONObject, from: Int): String {
        var jsonData = JSONObject()
        try {
            val jsonObject = JSONObject()

            val jsonObjectHeader = JSONObject()
            jsonObjectHeader.put("RequestID", Configuration.RequestID)
            jsonObjectHeader.put("DateTime", DateHelper.currentDateAsStringddMMMyyyy)
            jsonObjectHeader.put("ApplicationCode", Configuration.ApplicationCode)
            jsonObjectHeader.put(
                "AuthTicket",
                Utilities.preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
            )
            jsonObjectHeader.put("PartnerCode", Constants.PartnerCode)
            jsonObjectHeader.put("EntityType", Configuration.EntityType)
            jsonObjectHeader.put("HandShake", Configuration.Handshake)
            jsonObjectHeader.put("UTMSource", Configuration.UTMSource)
            jsonObjectHeader.put("UTMMedium", Configuration.UTMMedium)
            jsonObjectHeader.put("LanguageCode", Configuration.LanguageCode)
            jsonObjectHeader.put("EntityID", Configuration.EntityID)

            jsonObject.put("Header", jsonObjectHeader)
            jsonObject.put("JSONData", jsonRequest.toString())

            val logging = HttpLoggingInterceptor {
                //Utilities.printLog("HttpLogging--> $it")
            }
            logging.level = HttpLoggingInterceptor.Level.BODY
            // Create Retrofit
            val retrofit = Retrofit.Builder()
                .client(
                    OkHttpClient.Builder()
                        .retryOnConnectionFailure(true)
                        .protocols(Arrays.asList(Protocol.HTTP_1_1))
                        .addInterceptor(logging)
                        .connectTimeout(3, TimeUnit.MINUTES)
                        .writeTimeout(3, TimeUnit.MINUTES)
                        .readTimeout(3, TimeUnit.MINUTES)
                        .build()
                )
                .baseUrl(Constants.strAPIUrl)
                .addConverterFactory(GsonConverterFactory.create())
                //.addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
                //.addConverterFactory(ScalarsConverterFactory.create())
                //.addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()

            // Create Service
            val service = retrofit.create(FitnessApiService::class.java)
            //Utilities.printLogError("Request--->$jsonObject")
            val body = getEncryptedRequestBody(jsonObject.toString())

            CoroutineScope(Dispatchers.Main).launch {
                var response: Response<ResponseBody>? = null
                when (from) {
                    1 -> {
                        Utilities.printLogError("Request Url--->${Constants.FITNESS_LIST_HISTORY_API}")
                        try {
                            response = service.getStepsHistoryApi(body)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    2 -> {
                        Utilities.printLogError("Request Url--->${Constants.FITNESS_STEP_SAVE_LIST_API}")
                        try {
                            response = service.saveStepsListApi(body)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                Utilities.printLogError("Request--->${jsonObject}")
                if (response != null) {
                    withContext(Dispatchers.IO) {
                        if (response.isSuccessful) {
                            val resp = getResponse(response.body()!!.string())
                            //Utilities.printLogError("Resp--->$resp")
                            Utilities.printData("Resp", resp, true)
                            if (!Utilities.isNullOrEmpty(resp)) {
                                val jsonObjectResponse = JSONObject(resp)
                                if (!jsonObjectResponse.isNull("JSONData")
                                    && !Utilities.isNullOrEmpty(jsonObjectResponse.optString("JSONData"))
                                ) {
                                    jsonData = JSONObject(jsonObjectResponse.optString("JSONData"))

                                    when (from) {
                                        1 -> getStepsHistoryFromResp(jsonObjectResponse.optString("JSONData"))
                                        2 -> checkSaveStepsListResp(jsonObjectResponse.optString("JSONData"))
                                    }
                                }
                            }
                        } else {
                            Utilities.printLogError("RETROFIT_ERROR--->${response.code()}")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jsonData.toString()
    }

    private fun getStepsHistoryFromResp(response: String) {
        try {
            if (!Utilities.isNullOrEmpty(response)) {
                val gson = GsonBuilder().setPrettyPrinting().create()
                val jsonData: StepsHistoryModel.Response = gson.fromJson(
                    response,
                    object : TypeToken<StepsHistoryModel.Response?>() {}.type
                )

                val history = jsonData.stepGoalHistory.toMutableList()
                //Utilities.printData("StepGoalHistory", history)
                if (!history.isNullOrEmpty()) {
                    stepsDataSingleton.stepHistoryList.clear()
                    stepsDataSingleton.stepHistoryList.addAll(history)
                    if (FitnessDataManager.getInstance(context)!!.oAuthPermissionsApproved()) {
                        refreshStepCountSync(context)
                    }
                }
            } else {
                Utilities.printLogError("Error while fetching Response from server")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkSaveStepsListResp(response: String) {
        try {
            if (!Utilities.isNullOrEmpty(response)) {
                val gson = GsonBuilder().setPrettyPrinting().create()
                val jsonData: StepsSaveListModel.StepsSaveListResponse = gson.fromJson(
                    response,
                    object : TypeToken<StepsSaveListModel.StepsSaveListResponse?>() {}.type
                )

                val stepsDetails = jsonData.stepsDetails
                if (stepsDetails.equals(Constants.SUCCESS, ignoreCase = true)) {
                    Utilities.printLogError("Steps Saved Successfully")
                    Utilities.preferenceUtils.storeBooleanPreference(
                        PreferenceConstants.IS_FITNESS_DATA_NOT_SYNC,
                        false
                    )
                }
                showProgress(false)
            } else {
                Utilities.printLogError("Error while fetching Response from server")
                showProgress(false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showProgress(showProgress: Boolean) {
        try {
            Handler(Looper.getMainLooper()).post {
                Utilities.printLogError("showProgress--->$showProgress")
                if (showProgress) {
                    progressDialog = CustomProgressBar(AppAH.instance.getActivityContextInstance())
                    progressDialog!!.setCancelable(false)
                    progressDialog!!.setCanceledOnTouchOutside(false)
                    progressDialog!!.setLoaderType("")
                    progressDialog!!.show()
                } else {
                    if (progressDialog != null) {
                        if (progressDialog!!.isShowing) {
                            //get the Context object that was used to great the dialog
                            val context = (progressDialog!!.context as ContextWrapper).baseContext
                            //if the Context used here was an activity AND it hasn't been finished or destroyed
                            //then dismiss it
                            if (context is Activity) {
                                if (!context.isFinishing && !context.isDestroyed) progressDialog!!.dismiss()
                            } else {
                                //if the Context used wasnt an Activity, then dismiss it too
                                progressDialog!!.dismiss()
                            }
                        }
                        progressDialog = null
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getEncryptedRequestBody(request: String): RequestBody {
        val encryptedReq = EncryptionUtility.encrypt(request)
        return RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), encryptedReq)
        //return encryptedReq.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }

    private fun getResponse(response: String): String {
        val decrypted = EncryptionUtility.decrypt(response)
        val decryptedResponse: String = decrypted
            .replace("\\r\\n", "")
            .replace("\\\"", "\"")
            .replace("\\\\\"", "\"")
            .replace("\"{", "{")
            .replace("\"[", "[")
            .replace("}\"", "}")
            .replace("]\"", "]")
        return decryptedResponse
    }

    fun forceSyncStepsData(mContext: Context?) {
        val nDays = 30
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -nDays)
        //Utilities.printLog("StepCountHelper sync for date ---> $calendar.time")
        pullFitnessDataFromGoogleFitForDate(mContext, calendar.time, Calendar.getInstance().time)
    }

    fun subscribe(context: Context?) {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
        FitnessDataManager(context).fitSignIn(FitRequestCode.READ_DATA)
    }

    /**
     * https://fitness.stackexchange.com/questions/25472/how-to-calculate-calorie-from-pedometer
     * https://www.livestrong.com/article/353942-how-long-does-it-take-to-walk-10-000-steps/
     * https://www.walkingwithattitude.com/articles/features/how-to-measure-stride-or-step-length-for-your-pedometer
     * http://www.gearedtobefit.com/fitness_calculators.cfm
     *
     *
     * http://www.gearedtobefit.com/fitness_calculators.cfm#calburn
     * function Calculate2(form)
     * {
     * var weight = document.burn2.weight.options[document.burn2.weight.selectedIndex].value;;
     * var stride = document.burn2.stride.options[document.burn2.stride.selectedIndex].value;
     * var steps = document.burn2.steps.options[document.burn2.steps.selectedIndex].value;
     * var calories = (weight*stride*steps)/138.462;
     * document.burn2.calories.value = Math.round(calories);
     * }
     */
    private fun getCalculatedFitnessDataFromSteps(stepsCount: Int): HashMap<String, String> {
        val dataMap = HashMap<String, String>()
        // default values
        var totalCalories = 0
        var totalDistanceInMtr = 0f
        var stepsActiveTime = 0
        val weightValue = 65f
        //val heightValue = 165f
        val gender = 1
        try {
            /*if (heightWeightMap != null) {
                weightValue = Float.parseFloat(heightWeightMap.get(GlobalConstants.WEIGHT));
                heightValue = Float.parseFloat(heightWeightMap.get(GlobalConstants.HEIGHT));
                gender = Integer.parseInt(heightWeightMap.get(GlobalConstants.GENDER));
            }*/
            val strideAsPerGenderInches =
                if (gender == 1) 84 * 0.415 else 84 * 0.413 // 0.415 for male, 0.413 for female
            val activityTimeFactor = 100 //avg 100 steps per minute
            val calorieBurnedFactor = 138.462 //weightValue * (strideLengthInCms / 100);
            totalDistanceInMtr =
                (stepsCount * (CalculateParameters.convertInchToCm(strideAsPerGenderInches.toString())
                    .toDouble() / 100)).toFloat()
            stepsActiveTime = stepsCount / activityTimeFactor
            stepsActiveTime = if (stepsActiveTime <= 0) 1 else stepsActiveTime // At least 1 min
            //===Calories based on weight and distance
            //Double distanceInmiles = totalDistanceInMtr * 0.00062137;
            //totalCalories = (int) (Double.parseDouble(Helper.convertKgToLbs(String.valueOf(weightValue))) * distanceInmiles * 0.468);

            //===Calories based on weight,steps, stride length
            var weightFactor = 107
            if (weightValue in 45.0..52.0) {
                weightFactor = 107
            } else if (weightValue in 53.0..59.0) {
                weightFactor = 123
            } else if (weightValue in 60.0..66.0) {
                weightFactor = 138
            } else if (weightValue in 67.0..73.0) {
                weightFactor = 157
            } else if (weightValue in 74.0..80.0) {
                weightFactor = 169
            } else if (weightValue in 81.0..86.0) {
                weightFactor = 183
            } else if (weightValue in 87.0..93.0) {
                weightFactor = 198
            } else if (weightValue in 94.0..100.0) {
                weightFactor = 213
            } else if (weightValue in 101.0..107.0) {
                weightFactor = 228
            } else if (weightValue in 108.0..130.0) {
                weightFactor = 260
            } else if (weightValue >= 131) {
                weightFactor = 310
            }
            totalCalories =
                (weightFactor * strideAsPerGenderInches * stepsCount / calorieBurnedFactor).toInt() / 1000

            /*//Utilities.printLog("History", "CalculatedFitness -- weightValue LBS" + Double.parseDouble(Helper.convertKgToLbs(String.valueOf(weightValue))));
            //Utilities.printLog("History", "CalculatedFitness -- strideLengthIn Inch" + Double.parseDouble(Helper.convertCmToInch(String.valueOf(strideLengthInInches))));
            //Utilities.printLog("History", "CalculatedFitness -- distanceInmiles " + distanceInmiles);
            //Utilities.printLog("History", "CalculatedFitness -- weightValue" + weightValue);
            //Utilities.printLog("History", "CalculatedFitness -- heightValue" + heightValue);
            //Utilities.printLog("History", "CalculatedFitness -- gender" + gender);
            //Utilities.printLog("History", "CalculatedFitness -- strideLengthInCms" + strideLengthInCms);
            //Utilities.printLog("History", "CalculatedFitness -- totalDistanceInMtr" + totalDistanceInMtr);
            //Utilities.printLog("History", "CalculatedFitness -- totalCalories" + totalCalories);
            //Utilities.printLog("History", "CalculatedFitness -- stepsActiveTime" + stepsActiveTime);*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
        dataMap[Constants.CALORIES] = totalCalories.toString()
        dataMap[Constants.DISTANCE] = totalDistanceInMtr.toString()
        dataMap[Constants.ACTIVE_TIME] = stepsActiveTime.toString()
        return dataMap
    }

    /*    private fun updateAllStepsAndCallService(fitnessDataMap: JSONArray?) {
            try {
                if (fitnessDataMap != null) {
                    if (fitnessDataMap.length() > 0) {
                        val fitnessDataMapList = ArrayList<ArrayMap<String, String>>()
                        for (i in 0 until fitnessDataMap.length()) {
                            if (fitnessDataMap.getJSONObject(i) != null) {
                                val stepsDataMap = fitnessDataMap.getJSONObject(i)
                                val dataMap = ArrayMap<String, String>()
                                dataMap[Constants.STEPS_COUNT] = stepsDataMap.getString(Constants.STEPS_COUNT)
                                dataMap[Constants.ACTIVE_TIME] = stepsDataMap.getString(Constants.ACTIVE_TIME)
                                dataMap[Constants.CALORIES] = stepsDataMap.getString(Constants.CALORIES)
                                dataMap[Constants.DISTANCE] = stepsDataMap.getString(Constants.DISTANCE)
                                dataMap[Constants.RECORD_DATE] = DateHelper.convertDateTimeValue(stepsDataMap.getString(Constants.RECORD_DATE),
                                    DateHelper.SERVER_DATE_YYYYMMDD,
                                    DateHelper.SERVER_DATE_YYYYMMDD)
                                dataMap[Constants.LAST_UPDATED_TIME] = DateHelper.convertDateTimeValue(
                                        stepsDataMap.getString(Constants.RECORD_DATE),
                                        DateHelper.SERVER_DATE_YYYYMMDD,
                                        DateHelper.SERVER_DATE_YYYYMMDD)
                                dataMap[Constants.SYNC] = Constants.TRUE
                                //Utilities.printLog(TAG, "Storing today's FIT data -- > $dataMap")
                                fitnessDataMapList.add(dataMap)
                            }
                        }
                        var showProgressBar = false
                        if (fitnessDataMapList.size >= 30) {
                            showProgressBar = true
                        }
                        viewModel.saveStepsList(showProgressBar)
                        //FitnessServiceDispatcher.callSaveStepsServiceAll(context,StepHomeActivity::class.java.getName(),showProgressBar, fitnessDataMapList)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }*/

    /**
     * Below logic is to get fitness data from Google Fit
     * --
     */
    /*public static void refreshStepCountSync(Context mContext) {
        try {
            Calendar calendar = Calendar.getInstance();
            boolean getFitDataForToday = false;
            if (!StepsDataSingleton.getInstance().getStepHistoryList().getStepGoalHistory().isEmpty()) {
                StepGoalHistory latestFitnessData = StepsDataSingleton.getInstance().getStepHistoryList().getStepGoalHistory().get(0);
                if (latestFitnessData != null) {
                    if (latestFitnessData.getRecordDate() != null) {
                        //Utilities.printLog("StepCountHelper", "LAST RECORD_DATE FOUND-- >" + latestFitnessData.getRecordDate());
                        Date dateBefore = DateHelper.convertStrToDateOBJ(latestFitnessData.getRecordDate());
                        Date dateAfter = DateHelper.convertStrToDateOBJ(DateHelper.getCurrentDateAsStringyyyyMMdd());

                        if (dateBefore != null & dateAfter != null) {
                            long difference = dateAfter.getTime() - dateBefore.getTime();
                            int daysDifference = (int) (difference / (1000 * 60 * 60 * 24));
                            //Utilities.printLog("StepCountHelper", " daysDifference -- " + daysDifference);
                            //ToDo: Keyur, force sync for at-least 30 days, will change logic for force sync in next phase
                            daysDifference = (Math.max(daysDifference, 30));
                            if (daysDifference > 0) {
                                calendar = Calendar.getInstance();
                                calendar.add(Calendar.DATE, -daysDifference);
                                //Utilities.printLog("StepCountHelper", " sync for date -- " + calendar.getTime());
                                pullFitnessDataFromGoogleFitForDate(mContext, calendar.getTime(), Calendar.getInstance().getTime());
                            } else {
                                //fail safe
                                getFitDataForToday = true;
                            }
                        } else {
                            //fail safe
                            getFitDataForToday = true;
                        }
                    } else {
                        //Utilities.printLog("StepCountHelper", "LAST RECORD_DATE NOT found so fetch all data from FIT-- >");
                        //We will try to fetch last n days data from google fit.
                        getFitDataForToday = true;
                        int nDays = 365;
                        calendar = Calendar.getInstance();
                        calendar.add(Calendar.DATE, -nDays);
                        //Utilities.printLog("StepCountHelper", " sync for date -- " + calendar.getTime());
                        pullFitnessDataFromGoogleFitForDate(mContext, calendar.getTime(), Calendar.getInstance().getTime());
                    }
                } else {
                    //fail safe
                    getFitDataForToday = true;
                    pullFitnessDataFromGoogleFitForDate(mContext, Calendar.getInstance().getTime(), Calendar.getInstance().getTime());
                }
            } else {
                //Utilities.printLog("StepCountHelper", "getStepHistoryList() is EMPTY-----");
                //Utilities.printLog("StepCountHelper", "LAST RECORD_DATE NOT found so fetch all data from FIT-- >");
                //We will try to fetch last n days data from google fit.
                getFitDataForToday = true;
                int nDays = 365;
                calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, -nDays);
                //Utilities.printLog("StepCountHelper", " sync for date -- " + calendar.getTime());
                pullFitnessDataFromGoogleFitForDate(mContext, calendar.getTime(), Calendar.getInstance().getTime());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }*/

    /*    fun getTodayStepsDataMap(mContext: Context?): ArrayMap<String, String> {
        val arrayMap = ArrayMap<String, String>()
        object : AsyncTask<Void?, Void?, Void?>() {

            override fun onPostExecute(aVoid: Void?) {
                super.onPostExecute(aVoid)
            }

            override fun doInBackground(vararg params: Void?): Void? {
                FitnessDataManager.getInstance(mContext)!!
                    .readHistoryData(Calendar.getInstance().time, Calendar.getInstance().time)
                    .addOnCompleteListener { task ->
                        if (FitnessDataManager.getInstance(mContext)!!.fitnessDataMap != null) {
                            //Utilities.printLog(TAG, FitnessDataManager.getInstance(mContext)!!.fitnessDataMap.toString())
                            try {
                                arrayMap[Constants.STEP_ID] = "1"
                                arrayMap[Constants.GOAL_ID] = "1"
                                arrayMap[Constants.STEPS_COUNT] =
                                    FitnessDataManager.getInstance(mContext)!!.fitnessDataMap!!.getJSONObject(
                                        0).getString(Constants.STEPS_COUNT)
                                arrayMap[Constants.TOTAL_GOAL] = Constants.DEFAULT_STEP_GOAL
                                arrayMap[Constants.GOAL_PERCENTILE] = "0"
                                arrayMap[Constants.RECORD_DATE] =
                                    FitnessDataManager.getInstance(mContext)!!.fitnessDataMap!!.getJSONObject(
                                        0).getString(Constants.RECORD_DATE)
                                arrayMap[Constants.CALORIES] =
                                    FitnessDataManager.getInstance(mContext)!!.fitnessDataMap!!.getJSONObject(
                                        0).getString(Constants.CALORIES)
                                arrayMap[Constants.DISTANCE] =
                                    FitnessDataManager.getInstance(mContext)!!.fitnessDataMap!!.getJSONObject(
                                        0).getString(Constants.DISTANCE)
                                arrayMap[Constants.ACTIVE_TIME] =
                                    FitnessDataManager.getInstance(mContext)!!.fitnessDataMap!!.getJSONObject(
                                        0).getString(Constants.ACTIVE_TIME)
                                arrayMap[Constants.STEP_NOTIFICATION] = "1"
                                arrayMap[Constants.LAST_UPDATED_TIME] =
                                    FitnessDataManager.getInstance(mContext)!!.fitnessDataMap!!.getJSONObject(
                                        0).getString(Constants.RECORD_DATE)
                                arrayMap[Constants.SYNC] = Constants.FALSE
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                return null
            }
        }.execute()
        return arrayMap
    }*/

    fun syncForLastTwoYears() {
        syncCount += 1
        val differenceInDays = DateHelper.getDateDifferenceInDays(
            activity?.viewModel?.joiningDate ?: "",
            DateHelper.currentDateAsStringyyyyMMdd,
            DateHelper.SERVER_DATE_YYYYMMDD
        )
        Utilities.printLogError("daysDifference---> $differenceInDays")
        if (differenceInDays > 730) {
            if (!bgSync) {
                showProgress(true)
            }
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, -730)
            val dateBefore = calendar.time
            val dateAfter = Calendar.getInstance().time
            pullFitnessDataFromGoogleFitForDate(context, dateBefore, dateAfter)
        }
    }

}

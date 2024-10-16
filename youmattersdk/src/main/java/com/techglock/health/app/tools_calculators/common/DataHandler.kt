package com.techglock.health.app.tools_calculators.common

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.techglock.health.app.R
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.CalculateParameters
import com.techglock.health.app.common.utils.LocaleHelper
import com.techglock.health.app.common.utils.ParameterDataModel
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.common.utils.VitalParameter
import com.techglock.health.app.common.view.SpinnerModel
import com.techglock.health.app.model.toolscalculators.UserInfoModel
import com.techglock.health.app.tools_calculators.model.CalculatorDataSingleton
import com.techglock.health.app.tools_calculators.model.ScaleAsset
import com.techglock.health.app.tools_calculators.model.TrackerDashboardModel
import com.techglock.health.app.tools_calculators.ui.HeartAgeCalculator.HeartAgeFragment
import com.techglock.health.app.tools_calculators.ui.HypertensionCalculator.HypertensionInputFragment
import java.util.LinkedList
import java.util.Locale
import kotlin.math.roundToInt

class DataHandler(val context: Context) {

    fun getTrackersList(): ArrayList<TrackerDashboardModel> {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val list: ArrayList<TrackerDashboardModel> = ArrayList()
        list.add(TrackerDashboardModel(localResource.getString(R.string.TRACKER_HEART_AGE2), localResource.getString(R.string.TRACKER_DESC_HEART_AGE2), R.drawable.img_tools_heart_age, ContextCompat.getColor(context, R.color.colorPrimary), Constants.CODE_HEART_AGE_CALCULATOR))
        list.add(TrackerDashboardModel(localResource.getString(R.string.TRACKER_DIABETES2), localResource.getString(R.string.TRACKER_DESC_DIABETES2), R.drawable.img_tools_diabetes, ContextCompat.getColor(context, R.color.colorPrimary), Constants.CODE_DIABETES_CALCULATOR))
        list.add(TrackerDashboardModel(localResource.getString(R.string.TRACKER_HYPERTENSION2), localResource.getString(R.string.TRACKER_DESC_HYPERTENSION2), R.drawable.img_tools_hypertension, ContextCompat.getColor(context, R.color.colorPrimary), Constants.CODE_HYPERTENSION_CALCULATOR))
        list.add(TrackerDashboardModel(localResource.getString(R.string.TRACKER_STRESS_ANXIETY2), localResource.getString(R.string.TRACKER_DESC_STRESS_ANXIETY2), R.drawable.img_tools_stress_anxiety, ContextCompat.getColor(context, R.color.colorPrimary), Constants.CODE_STRESS_ANXIETY_CALCULATOR))
        list.add(TrackerDashboardModel(localResource.getString(R.string.TRACKER_SMART_PHONE2), localResource.getString(R.string.TRACKER_DESC_SMART_PHONE2), R.drawable.img_tools_smartphone_addiction, ContextCompat.getColor(context, R.color.colorPrimary), Constants.CODE_SMART_PHONE_ADDICTION_CALCULATOR))
        list.add(TrackerDashboardModel(localResource.getString(R.string.TRACKER_DUE_DATE2), localResource.getString(R.string.TRACKER_DESC_DUE_DATE2), R.drawable.img_due_date_cal, ContextCompat.getColor(context, R.color.colorPrimary), Constants.CODE_DUE_DATE_CALCULATOR))
        //list.add(TrackerDashboardModel(localResource.getString(R.string.tracker_vaccination), localResource.getString(R.string.tracker_desc__vaccination), R.drawable.img_vaccination,ContextCompat.getColor(context,R.color.vivantRed), "VC"))
        return list
    }

    fun getCategoryImageByCode(screen: String): TrackerDashboardModel {
        val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        var tracker = TrackerDashboardModel()
        when (screen) {
            "HEART_AGE_CALC" -> {
                tracker = TrackerDashboardModel(localResource.getString(R.string.HEART_AGE_CALCULATOR), localResource.getString(R.string.TRACKER_DESC_HEART_AGE), R.drawable.img_tools_heart_age, ContextCompat.getColor(context, R.color.colorPrimary), Constants.CODE_HEART_AGE_CALCULATOR)
            }

            "DIABETES_CALCULATOR" -> {
                tracker = TrackerDashboardModel(localResource.getString(R.string.DIABETES_CALCULATOR), localResource.getString(R.string.TRACKER_DESC_DIABETES), R.drawable.img_tools_diabetes, ContextCompat.getColor(context, R.color.colorPrimary), Constants.CODE_DIABETES_CALCULATOR)
            }

            "HTN_CALC" -> {
                tracker = TrackerDashboardModel(localResource.getString(R.string.HYPERTENSION_CALCULATOR), localResource.getString(R.string.TRACKER_DESC_HYPERTENSION), R.drawable.img_tools_hypertension, ContextCompat.getColor(context, R.color.colorPrimary), Constants.CODE_HYPERTENSION_CALCULATOR)
            }

            "DASS_21" -> {
                tracker = TrackerDashboardModel(localResource.getString(R.string.STRESS_ANXIETY_CALCULATOR), localResource.getString(R.string.TRACKER_DESC_STRESS_ANXIETY), R.drawable.img_tools_stress_anxiety, ContextCompat.getColor(context, R.color.colorPrimary), Constants.CODE_STRESS_ANXIETY_CALCULATOR)
            }

            "SMART_PHONE" -> {
                tracker = TrackerDashboardModel(localResource.getString(R.string.SMART_PHONE_CALCULATOR), localResource.getString(R.string.TRACKER_DESC_SMART_PHONE), R.drawable.img_tools_smartphone_addiction, ContextCompat.getColor(context, R.color.colorPrimary), Constants.CODE_SMART_PHONE_ADDICTION_CALCULATOR)
            }
        }
        return tracker
    }

    fun scaleAssetsList(): ArrayList<ScaleAsset> {
        val localResource = LocaleHelper.getLocalizedResources(
            context, Locale(LocaleHelper.getLanguage(context))
        )!!
        val list: ArrayList<ScaleAsset> = ArrayList()
        list.add(
            ScaleAsset(
                R.drawable.img_depression,
                localResource.getString(R.string.DEPRESSION_SCALE_ASSETS),
                getDepressionList(),
                "DEPRESSION"
            )
        )
        list.add(
            ScaleAsset(
                R.drawable.img_anxiety,
                localResource.getString(R.string.ANXIETY_SCALE_ASSETS),
                getAnxietyList(),
                "ANXIETY"
            )
        )
        list.add(
            ScaleAsset(
                R.drawable.img_stress,
                localResource.getString(R.string.STRESS_SCALE_ASSETS),
                getStressList(),
                "STRESS"
            )
        )
        return list
    }

    fun getGenderList(): ArrayList<SpinnerModel> {
        val localResource = LocaleHelper.getLocalizedResources(
            context, Locale(LocaleHelper.getLanguage(context))
        )!!
        val list: ArrayList<SpinnerModel> = ArrayList()
        list.add(SpinnerModel(localResource.getString(R.string.MALE), "Male", 0, true))
        list.add(SpinnerModel(localResource.getString(R.string.FEMALE), "Female", 1, false))
        return list
    }

    fun getModelList(): ArrayList<SpinnerModel> {
        val localResource = LocaleHelper.getLocalizedResources(
            context, Locale(LocaleHelper.getLanguage(context))
        )!!
        val list: ArrayList<SpinnerModel> = ArrayList()
        list.add(SpinnerModel(localResource.getString(R.string.BMI), "BMI", 0, true))
        list.add(SpinnerModel(localResource.getString(R.string.LIPID), "LIPID", 1, false))
        return list
    }

    fun getAgeGroupList(): ArrayList<SpinnerModel> {
        val localResource = LocaleHelper.getLocalizedResources(
            context, Locale(LocaleHelper.getLanguage(context))
        )!!
        val list: ArrayList<SpinnerModel> = ArrayList()
        list.add(SpinnerModel(localResource.getString(R.string.UNDER_35_YEARS), "", 0, true))
        list.add(SpinnerModel(localResource.getString(R.string.AGE_35_TO_44), "", 1, false))
        list.add(SpinnerModel(localResource.getString(R.string.AGE_45_TO_54), "", 2, false))
        list.add(SpinnerModel(localResource.getString(R.string.AGE_55_TO_64), "", 3, false))
        list.add(SpinnerModel(localResource.getString(R.string.AGE_65_YRS_OR_OVER), "", 4, false))
        return list
    }

    private fun getDepressionList(): List<String> {
        val localResource = LocaleHelper.getLocalizedResources(
            context, Locale(LocaleHelper.getLanguage(context))
        )!!
        val list = ArrayList<String>()
        list.add(localResource.getString(R.string.DYSPHORIA))
        list.add(localResource.getString(R.string.LACK_OF_INTEREST_INVOLVEMENT))
        list.add(localResource.getString(R.string.HOPELESSNESS))
        list.add(localResource.getString(R.string.ANHEDONIA))
        list.add(localResource.getString(R.string.DEVALUATION_OF_LIFE))
        list.add(localResource.getString(R.string.INERTIA))
        list.add(localResource.getString(R.string.SELF_DEPRECATION))
        return list
    }

    private fun getAnxietyList(): List<String> {
        val localResource = LocaleHelper.getLocalizedResources(
            context, Locale(LocaleHelper.getLanguage(context))
        )!!
        val list = ArrayList<String>()
        list.add(localResource.getString(R.string.AUTONOMIC_AROUSAL))
        list.add(localResource.getString(R.string.SELF_DEPRECATION))
        list.add(localResource.getString(R.string.SKELETAL_MUSCLE_EFFECTS))
        list.add(localResource.getString(R.string.SUBJECTIVE_EXPERIENCE_OF_ANXIOUS_AFFECT))
        list.add(localResource.getString(R.string.SITUATIONAL_ANXIETY))
        return list
    }

    private fun getStressList(): List<String> {
        val localResource = LocaleHelper.getLocalizedResources(
            context, Locale(LocaleHelper.getLanguage(context))
        )!!
        val list = ArrayList<String>()
        list.add(localResource.getString(R.string.DIFFICULTY_RELAXING))
        list.add(localResource.getString(R.string.NERVOUS_AROUSAL))
        list.add(localResource.getString(R.string.BEING_EASILY_UPSET_AGITATED_IRRITABLE_OVER_REACTIVE_AND_IMPATIENT))
        return list
    }

    /*    fun getSACList1(): ArrayList<ScaleAsset> {
            val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)!!))!!
            val list: ArrayList<ScaleAsset> = ArrayList()
            list.add(ScaleAsset(R.drawable.img_digital_eye_strain,localResource.getString(R.string.DIGITAL_EYE_STRAIN),getSACSubPointsList("1")))
            list.add(ScaleAsset(R.drawable.img_neck_problem, localResource.getString(R.string.NECK_PROBLEMS),getSACSubPointsList("2")))
            list.add(ScaleAsset(R.drawable.img_illness, localResource.getString(R.string.INCREASED_ILLNESSES_DUE_TO_GERMS),getSACSubPointsList("3")))
            return list
        }

        fun getSACList2(): ArrayList<ScaleAsset> {
            val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)!!))!!
            val list: ArrayList<ScaleAsset> = ArrayList()
            list.add(ScaleAsset(R.drawable.img_sleep_disturbances, localResource.getString(R.string.SLEEP_DISTURBANCES),getSACSubPointsList("4")))
            list.add(ScaleAsset(R.drawable.img_depression, localResource.getString(R.string.DEPRESSION),getSACSubPointsList("5")))
            list.add(ScaleAsset(R.drawable.img_obsession, localResource.getString(R.string.OBSESSIVE_COMPULSIVE_DISORDER),getSACSubPointsList("6")))
            list.add(ScaleAsset(R.drawable.img_relationship_problem, localResource.getString(R.string.RELATIONSHIP_PROBLEMS),getSACSubPointsList("7")))
            list.add(ScaleAsset(R.drawable.img_anxiety, localResource.getString(R.string.ANXIETY),getSACSubPointsList("8")))
            return list
        }*/

    /*    fun getSmartPhoneAddictionImage(code: String): Int {
            val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)!!))!!
            var result : Int = 0
            when (code) {
                localResource.getString(R.string.DIGITAL_EYE_STRAIN) -> result = R.drawable.img_digital_eye_strain
                localResource.getString(R.string.NECK_PROBLEMS) -> result = R.drawable.img_neck_problem
                localResource.getString(R.string.INCREASED_ILLNESSES_DUE_TO_GERMS) -> result = R.drawable.img_illness
                localResource.getString(R.string.SLEEP_DISTURBANCES) -> result = R.drawable.img_sleep_disturbances
                localResource.getString(R.string.DEPRESSION) -> result = R.drawable.img_depression_bg
                localResource.getString(R.string.OBSESSIVE_COMPULSIVE_DISORDER) -> result = R.drawable.img_obsession
                localResource.getString(R.string.RELATIONSHIP_PROBLEMS) -> result = R.drawable.img_relationship_problem
                localResource.getString(R.string.ANXIETY) -> result = R.drawable.img_anxiety_bg
            }
            return result
        }*/

    fun getSmartPhoneAddictionImage(code: String): Int {
        var result = R.drawable.img_digital_eye_strain
        when (code) {
            "Digital eye strain" -> result = R.drawable.img_digital_eye_strain
            "Neck problems" -> result = R.drawable.img_neck_problem
            "Increased illnesses due to germs" -> result = R.drawable.img_illness
            "Sleep disturbances" -> result = R.drawable.img_sleep_disturbances
            "Depression" -> result = R.drawable.img_depression_bg
            "Obsessive Compulsive Disorder" -> result = R.drawable.img_obsession
            "Relationship problems" -> result = R.drawable.img_relationship_problem
            "Anxiety" -> result = R.drawable.img_anxiety_bg
        }
        return result
    }

    fun getSmartPhoneAddictionQuesCode(code: String): String {
        var result = ""
        when (code) {
            "ADDIC1" -> result = "SMRT_RING"
            "ADDIC2" -> result = "SMRT_BUZZ"
            "ADDIC3" -> result = "SMRT_MORN"
            "ADDIC4" -> result = "SMRT_SLEEP"
            "ADDIC5" -> result = "SMRT_TRAFFIC"
            "ADDIC6" -> result = "SMRT_SIGNAL"
            "ADDIC7" -> result = "SMRT_ANXTY"
            "ADDIC8" -> result = "SMRT_TIME"
            "ADDIC9" -> result = "SMRT_REAL"
            "ADDIC10" -> result = "SMRT_MEALS"
            "ADDIC11" -> result = "SMRT_LONELY"
        }
        return result
    }

    fun getRiskConverted(rsk: String, context: Context): String {
        val localResource =
            LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        var risk = ""
        when (rsk.uppercase()) {
            "NORMAL" -> risk = localResource.getString(R.string.RISK_NORMAL)
            "MILD" -> risk = localResource.getString(R.string.RISK_MILD)
            "MODERATE" -> risk = localResource.getString(R.string.RISK_MODERATE)
            "SEVERE" -> risk = localResource.getString(R.string.RISK_SEVERE)
            "EXTREMELY SEVERE" -> risk = localResource.getString(R.string.RISK_EXTREMELY_SEVERE)
        }
        return risk
    }

    fun getSmartPhoneRiskTitleConverted(rsk: String, context: Context): String {
        val localResource =
            LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        var risk = ""
        when (rsk.uppercase()) {
            "NOT AT ALL NOMOPHOBIC" -> risk =
                localResource.getString(R.string.NOT_AT_ALL_NOMOPHOBIC)

            "MILD NOMOPHOBIC" -> risk = localResource.getString(R.string.MILD_NOMOPHOBIA)
            "MODERATE NOMOPHOBIC" -> risk = localResource.getString(R.string.MODERATE_NOMOPHOBIA)
            "SEVERE NOMOPHOBIC" -> risk = localResource.getString(R.string.SEVERE_NOMOPHOBIA)
        }
        return risk
    }

    fun getSmartPhoneRiskLabelConverted(rsk: String, context: Context): String {
        val localResource =
            LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        var risk = ""
        when (rsk.uppercase()) {
            "NO NOMOPHOBIA" -> risk = localResource.getString(R.string.HAVE_NO_NOMOPHOBIA)
            "MILD NOMOPHOBIA" -> risk = localResource.getString(R.string.HAVE_MILD_NOMOPHOBIA)
            "MODERATE NOMOPHOBIA" -> risk =
                localResource.getString(R.string.HAVE_MODERATE_NOMOPHOBIA)

            "SEVERE NOMOPHOBIA" -> risk = localResource.getString(R.string.HAVE_SEVERE_NOMOPHOBIA)
        }
        return risk
    }

    /*    private fun getSACSubPointsList(code:String): List<String> {
            val localResource = LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)!!))!!
            val list : ArrayList<String> = arrayListOf()

            when(code) {
                "1" -> {
                    list.add(localResource.getString(R.string.SUB_1_DIGITAL_EYE_STRAIN))
                    list.add(localResource.getString(R.string.SUB_2_DIGITAL_EYE_STRAIN))
                    list.add(localResource.getString(R.string.SUB_3_DIGITAL_EYE_STRAIN))
                    list.add(localResource.getString(R.string.SUB_4_DIGITAL_EYE_STRAIN))
                    list.add(localResource.getString(R.string.SUB_5_DIGITAL_EYE_STRAIN))
                }

                "2" -> {
                    list.add(localResource.getString(R.string.SUB_1_NECK_PROBLEMS))
                }

                "3" -> {
                    list.add(localResource.getString(R.string.SUB_1_INCREASED_ILLNESSES_DUE_TO_GERMS))
                    list.add(localResource.getString(R.string.SUB_2_INCREASED_ILLNESSES_DUE_TO_GERMS))
                    list.add(localResource.getString(R.string.SUB_3_INCREASED_ILLNESSES_DUE_TO_GERMS))
                    list.add(localResource.getString(R.string.SUB_31_INCREASED_ILLNESSES_DUE_TO_GERMS))
                    list.add(localResource.getString(R.string.SUB_32_INCREASED_ILLNESSES_DUE_TO_GERMS))

                    list.add(localResource.getString(R.string.SUB_4_INCREASED_ILLNESSES_DUE_TO_GERMS))
                    list.add(localResource.getString(R.string.SUB_41_INCREASED_ILLNESSES_DUE_TO_GERMS))
                    list.add(localResource.getString(R.string.SUB_42_INCREASED_ILLNESSES_DUE_TO_GERMS))
                    list.add(localResource.getString(R.string.SUB_5_INCREASED_ILLNESSES_DUE_TO_GERMS))
                    list.add(localResource.getString(R.string.SUB_51_INCREASED_ILLNESSES_DUE_TO_GERMS))
                }

                "4" -> {
                    list.add(localResource.getString(R.string.SUB_1_SLEEP_DISTURBANCES))
                    list.add(localResource.getString(R.string.SUB_2_SLEEP_DISTURBANCES))
                    list.add(localResource.getString(R.string.SUB_21_SLEEP_DISTURBANCES))
                    list.add(localResource.getString(R.string.SUB_22_SLEEP_DISTURBANCES))
                    list.add(localResource.getString(R.string.SUB_23_SLEEP_DISTURBANCES))
                }



                "7" -> {
                    list.add(localResource.getString(R.string.SUB_1_RELATIONSHIP_PROBLEMS))
                }

                "8" -> {
                    list.add(localResource.getString(R.string.SUB_1_ANXIETY))
                }

            }

            return list
        }*/

    fun getParameterList(model: String, fragment: Fragment): ArrayList<ParameterDataModel> {
        val localResource = LocaleHelper.getLocalizedResources(
            context, Locale(LocaleHelper.getLanguage(context))
        )!!
        val paramList: ArrayList<ParameterDataModel> = ArrayList()
        val userInfoModel: UserInfoModel? =
            if (fragment is HeartAgeFragment || fragment is HypertensionInputFragment) {
                UserInfoModel.getInstance()
            } else {
                CalculatorDataSingleton.getInstance()!!.userPreferences
            }
        var data = ParameterDataModel()
        if (model.equals("BMI", ignoreCase = true)) {
            data.title = localResource.getString(R.string.HEIGHT)
            if (!Utilities.isNullOrEmptyOrZero(userInfoModel!!.getHeight())) {
                val `val`: String = CalculateParameters.convertCmToFeet(userInfoModel.getHeight())
                    .toString() + "'" + CalculateParameters.convertCmToInch(
                    userInfoModel.getHeight()
                ) + "''"
                data.value = `val`
                data.finalValue = userInfoModel.getHeight()
            } else {
                data.value = "- -"
                data.finalValue = "0"
            }
            data.unit = localResource.getString(R.string.FEET_INCH)
            data.code = "HEIGHT"
            data.color = R.color.colorPrimary
            data.img = R.drawable.img_height
            data.description = localResource.getString(R.string.TODAY_VIA_MOBILE_ENTRY)
            paramList.add(data)

            data = ParameterDataModel()
            data.title = localResource.getString(R.string.WEIGHT)
            if (!Utilities.isNullOrEmptyOrZero(userInfoModel.getWeight())) {
                val `val` = "" + userInfoModel.getWeight().toDouble().toInt()
                data.value = `val`
                data.finalValue = `val`
            } else {
                data.value = "- -"
                data.finalValue = "50"
            }
            data.unit = localResource.getString(R.string.KG)
            data.code = "WEIGHT"
            data.color = R.color.colorPrimary
            data.img = R.drawable.img_weight
            data.description = localResource.getString(R.string.TODAY_VIA_MOBILE_ENTRY)
            paramList.add(data)

            data = ParameterDataModel()
            data.title = localResource.getString(R.string.SYSTOLIC_BP)
            if (!Utilities.isNullOrEmptyOrZero(userInfoModel.getSystolicBp())) {
                val `val` = "" + userInfoModel.getSystolicBp().toDouble().toInt()
                data.value = `val`
                data.finalValue = `val`
            } else {
                data.value = "- -"
            }
            data.unit = localResource.getString(R.string.MM_HG)
            data.code = "SYSTOLIC_BP"
            data.color = R.color.colorPrimary
            data.img = R.drawable.img_blood_pressure
            data.description = localResource.getString(R.string.TODAY_VIA_MOBILE_ENTRY)
            data.minRange = 10.0
            data.maxRange = 350.0
            paramList.add(data)

            data = ParameterDataModel()
            data.title = localResource.getString(R.string.DIASTOLIC_BP)
            if (!Utilities.isNullOrEmptyOrZero(userInfoModel.getDiastolicBp())) {
                val `val` = "" + userInfoModel.getDiastolicBp().toDouble().toInt()
                data.value = `val`
                data.finalValue = `val`
            } else {
                data.value = "- -"
            }
            data.unit = localResource.getString(R.string.MM_HG)
            data.code = "DIASTOLIC_BP"
            data.color = R.color.colorPrimary
            data.img = R.drawable.img_blood_pressure
            data.description = localResource.getString(R.string.TODAY_VIA_MOBILE_ENTRY)
            data.minRange = 0.0
            data.maxRange = 150.0
            paramList.add(data)
        } else if (model.equals("LIPID", ignoreCase = true)) {
            data.title = localResource.getString(R.string.CHOLESTEROL)
            if (!Utilities.isNullOrEmptyOrZero(userInfoModel!!.getCholesterol())) {
                val `val` = "" + userInfoModel.getCholesterol().toDouble().toInt()
                data.value = `val`
                data.finalValue = `val`
            } else {
                data.value = "- -"
            }
            data.description = localResource.getString(R.string.TODAY_VIA_MOBILE_ENTRY)
            data.unit = localResource.getString(R.string.MG_DL)
            data.code = "TOTAL_CHOL"
            data.color = R.color.colorPrimary
            data.img = R.drawable.img_cholesteroal
            data.minRange = 10.0
            data.maxRange = 700.0
            paramList.add(data)

            data = ParameterDataModel()
            data.title = localResource.getString(R.string.HDL)
            if (!Utilities.isNullOrEmptyOrZero(userInfoModel.getHdl())) {
                val `val` = "" + userInfoModel.getHdl().toDouble().toInt()
                data.value = `val`
                data.finalValue = `val`
            } else {
                data.value = "- -"
            }
            data.unit = localResource.getString(R.string.MG_DL)
            data.code = "HDL"
            data.color = R.color.colorPrimary
            data.img = R.drawable.img_cholesteroal
            data.description = localResource.getString(R.string.TODAY_VIA_MOBILE_ENTRY)
            data.minRange = 5.0
            data.maxRange = 150.0
            paramList.add(data)

            data = ParameterDataModel()
            data.title = localResource.getString(R.string.SYSTOLIC_BP)
            if (!Utilities.isNullOrEmptyOrZero(userInfoModel.getSystolicBp())) {
                val `val` = "" + userInfoModel.getSystolicBp().toDouble().toInt()
                data.value = `val`
                data.finalValue = `val`
            } else {
                data.value = "- -"
            }
            data.unit = localResource.getString(R.string.MM_HG)
            data.code = "SYSTOLIC_BP"
            data.color = R.color.colorPrimary
            data.img = R.drawable.img_blood_pressure
            data.description = localResource.getString(R.string.TODAY_VIA_MOBILE_ENTRY)
            data.minRange = 10.0
            data.maxRange = 350.0
            paramList.add(data)

            data = ParameterDataModel()
            data.title = localResource.getString(R.string.DIASTOLIC_BP)
            if (!Utilities.isNullOrEmptyOrZero(userInfoModel.getDiastolicBp())) {
                val `val` = "" + userInfoModel.getDiastolicBp().toDouble().toInt()
                data.value = `val`
                data.finalValue = `val`
            } else {
                data.value = "- -"
            }
            data.unit = localResource.getString(R.string.MM_HG)
            data.code = "DIASTOLIC_BP"
            data.color = R.color.colorPrimary
            data.img = R.drawable.img_blood_pressure
            data.description = localResource.getString(R.string.TODAY_VIA_MOBILE_ENTRY)
            data.minRange = 0.0
            data.maxRange = 150.0
            paramList.add(data)
        }
        return paramList
    }

    fun getDiabetesParameterList(): ArrayList<ParameterDataModel> {
        val localResource = LocaleHelper.getLocalizedResources(
            context, Locale(LocaleHelper.getLanguage(context))
        )!!
        val waistSize = UserInfoModel.getInstance()!!.getWaistSize()
        val paramList = ArrayList<ParameterDataModel>()
        val data = ParameterDataModel()
        data.title = localResource.getString(R.string.WAIST)
        Utilities.printLogError("Waist:: $waistSize")
        if (!Utilities.isNullOrEmptyOrZero(waistSize)) {
            try {
                val `val` = "" + waistSize.toDouble().toInt()
                val waist = CalculateParameters.convertCmToInch2(`val`).toDouble().roundToInt()
                data.value = waist.toString()
                data.finalValue = `val`
                //data.finalValue = waist.toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            data.value = "- -"
        }
        data.unit = localResource.getString(R.string.INCH)
        data.code = "WAISTMEASUREMENT"
        data.color = R.color.colorPrimary
        data.img = R.drawable.img_waist
        data.minRange = 25.0
        data.maxRange = 65.0
        data.description = localResource.getString(R.string.TODAY_VIA_MOBILE_ENTRY)
        paramList.add(data)
        return paramList
    }

    private val sequenceList = LinkedList<String>()

    fun getSmartPhoneAddictionNextQuestion(currentQuestionId: String): String {
        var result = "FIRST"
        when (currentQuestionId) {
            "FIRST" -> result = "ADDIC1"
            "ADDIC1" -> result = "ADDIC2"
            "ADDIC2" -> result = "ADDIC3"
            "ADDIC3" -> result = "ADDIC4"
            "ADDIC4" -> result = "ADDIC5"
            "ADDIC5" -> result = "ADDIC6"
            "ADDIC6" -> result = "ADDIC7"
            "ADDIC7" -> result = "ADDIC8"
            "ADDIC8" -> result = "ADDIC9"
            "ADDIC9" -> result = "ADDIC10"
            "ADDIC10" -> result = "ADDIC11"
            "ADDIC11" -> result = "ADDIC11"
        }
        return result
    }

    fun getSmartPhoneAddictionPrevious(questionId: String): String {
        var result = "FIRST"
        when (questionId) {
            "FIRST" -> result = "FIRST"
            "ADDIC1" -> result = "FIRST"
            "ADDIC2" -> result = "ADDIC1"
            "ADDIC3" -> result = "ADDIC2"
            "ADDIC4" -> result = "ADDIC3"
            "ADDIC5" -> result = "ADDIC4"
            "ADDIC6" -> result = "ADDIC5"
            "ADDIC7" -> result = "ADDIC6"
            "ADDIC8" -> result = "ADDIC7"
            "ADDIC9" -> result = "ADDIC8"
            "ADDIC10" -> result = "ADDIC9"
            "ADDIC11" -> result = "ADDIC10"
        }
        return result
    }

    fun getStressNextQuestion(currentQuesId: String): String {
        var result = "FIRST"
        when (currentQuesId) {
            "FIRST" -> result = "DASS-21_D_LIFEMEANINGLESS"
            "DASS-21_D_LIFEMEANINGLESS" -> result = "DASS-21_D_WORTHPERSON"
            "DASS-21_D_WORTHPERSON" -> result = "DASS-21_D_BECOMEENTHUSIASTIC"
            "DASS-21_D_BECOMEENTHUSIASTIC" -> result = "DASS-21_D_DOWNHEARTEDBLUE"
            "DASS-21_D_DOWNHEARTEDBLUE" -> result = "DASS-21_D_NOTHINGLOOKFORWARD"
            "DASS-21_D_NOTHINGLOOKFORWARD" -> result = "DASS-21_D_INITIATIVETHINGS"
            "DASS-21_D_INITIATIVETHINGS" -> result = "DASS-21_D_POSITIVEFEELING"
            "DASS-21_D_POSITIVEFEELING" -> result = "DASS-21_A_GOODREASON"
            "DASS-21_A_GOODREASON" -> result = "DASS-21_A_ABSENCEPHYSICALEXERTION"
            "DASS-21_A_ABSENCEPHYSICALEXERTION" -> result = "DASS-21_A_CLOSEPANIC"
            "DASS-21_A_CLOSEPANIC" -> result = "DASS-21_A_WORRIEDSITUATIONS"
            "DASS-21_A_WORRIEDSITUATIONS" -> result = "DASS-21_A_EXPERIENCEDTREMBLING"
            "DASS-21_A_EXPERIENCEDTREMBLING" -> result = "DASS-21_A_BREATHINGDIFFICULTY"
            "DASS-21_A_BREATHINGDIFFICULTY" -> result = "DASS-21_A_DRYNESSMOUTH"
            "DASS-21_A_DRYNESSMOUTH" -> result = "DASS-21_S_RATHERTOUCHY"
            "DASS-21_S_RATHERTOUCHY" -> result = "DASS-21_S_INTOLERANTANYTHING"
            "DASS-21_S_INTOLERANTANYTHING" -> result = "DASS-21_S_DIFFICULTTORELAX"
            "DASS-21_S_DIFFICULTTORELAX" -> result = "DASS-21_S_GETTINGAGITATED"
            "DASS-21_S_GETTINGAGITATED" -> result = "DASS-21_S_NERVOUSENERGY"
            "DASS-21_S_NERVOUSENERGY" -> result = "DASS-21_S_OVERREACTSITUATIONS"
            "DASS-21_S_OVERREACTSITUATIONS" -> result = "DASS-21_S_HRDWINDDOWN"
            "DASS-21_S_HRDWINDDOWN" -> result = "DASS-21_S_HRDWINDDOWN"
        }
        sequenceList.add(currentQuesId)
        //Utilities.printLog("SEQUENCELIST => " + sequenceList)
        return result
    }

    fun getStressPreviousQuestion(questionId: String): String {
        //Utilities.printLog("SEQUENCELIST => " + sequenceList)
        return if (sequenceList.size > 0) {
            sequenceList.removeLast()
        } else {
            "FIRST"
        }
    }

    fun clearSequenceList() {
        sequenceList.clear()
    }

    fun getVitalParameterData(parameter: String, context: Context): VitalParameter {
        val localResource = LocaleHelper.getLocalizedResources(
            context, Locale(LocaleHelper.getLanguage(context))
        )!!
        val vitalParameter = VitalParameter()
        when (parameter) {

            localResource.getString(R.string.FT) -> {
                vitalParameter.unit = localResource.getString(R.string.FT)
                vitalParameter.minRange = 4
                vitalParameter.maxRange = 7
            }

            localResource.getString(R.string.CM) -> {
                vitalParameter.unit = localResource.getString(R.string.CM)
                vitalParameter.minRange = 120
                vitalParameter.maxRange = 240
            }

            localResource.getString(R.string.LBS) -> {
                vitalParameter.unit = localResource.getString(R.string.LBS)
                vitalParameter.minRange = 64
                vitalParameter.maxRange = 550
            }

            localResource.getString(R.string.KG) -> {
                vitalParameter.unit = localResource.getString(R.string.KG)
                vitalParameter.minRange = 30
                vitalParameter.maxRange = 250
            }

        }
        return vitalParameter
    }

}

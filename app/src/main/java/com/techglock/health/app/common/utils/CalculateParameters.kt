package com.techglock.health.app.common.utils

import android.content.Context
import com.techglock.health.app.R
import com.techglock.health.app.common.constants.PreferenceConstants
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.Locale
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.roundToLong


object CalculateParameters {

    //    private lateinit var
    fun getBMI(heightParam: Double, weightParam: Double): Double {
        val height: Double
        val weight: Double
        var bmi = 0.0
        try {
            height = heightParam
            weight = weightParam

            if (Utilities.isNullOrEmpty(height.toString()) || height <= 0) {
                return 0.0
            } else if (Utilities.isNullOrEmpty(weight.toString()) || weight <= 0) {
                return 0.0
            } else {
                try {
                    //validationBMI = Math.round(Float.parseFloat(String.valueOf(weight)) / Math.pow(Float.parseFloat(String.valueOf(height)) / 100, 2));
                    bmi = weight / (height / 100).pow(2.0)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bmi
    }

    fun getWHR(waistParam: Double, hipParam: Double): String {
        val waist: String?
        val hip: String?
        val whr: Float
        var whrAsString = ""

        try {
            waist = waistParam.toString() + ""
            hip = hipParam.toString() + ""

            if (Utilities.isNullOrEmpty(waist) || waistParam <= 0) {
                return ""
            } else if (Utilities.isNullOrEmpty(hip) || hipParam <= 0) {
                return ""
            } else {
                try {
                    whr = java.lang.Float.parseFloat(waist) / java.lang.Float.parseFloat(hip)
                    //val nm = NumberFormat.getNumberInstance()
                    //whrAsString=String.valueOf(whr);
                    //whrAsString=(nm.format(whr));
                    whrAsString = DecimalFormat("##.##").format(whr.toDouble()).toString()
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }

            }
        } finally {
        }
        return whrAsString
    }

    fun getBMIObservation(parameterVal: String, context: Context): String {
        var observation = ""
        try {
            if (!parameterVal.isEmpty()) {
                //var localResource = LocaleHelper.getLocalizedResources(context, Locale("en"))!!
                val localResource = LocaleHelper.getLocalizedResources(
                    context, Locale(LocaleHelper.getLanguage(context))
                )!!
                val bmi: Double = parameterVal.toDouble()

                if (bmi <= 18.49) {
                    observation = localResource.getString(R.string.UNDERWEIGHT)
                } else if (bmi > 18.49 && bmi <= 22.99) {
                    observation = localResource.getString(R.string.NORMAL)
                } else if (bmi > 22.99 && bmi <= 24.99) {
                    observation = localResource.getString(R.string.OVERWEIGHT)
                } else if (bmi > 24.99 && bmi <= 29.99) {
                    observation = localResource.getString(R.string.PRE_OBESE)
                } else if (bmi > 29.99 && bmi <= 34.99) {
                    observation = localResource.getString(R.string.OBESE_LEVEL_1)
                } else if (bmi > 34.99 && bmi <= 39.99) {
                    observation = localResource.getString(R.string.OBESE_LEVEL_2)
                } else {
                    observation = localResource.getString(R.string.OBESE_LEVEL_3)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return observation
    }

    fun getBPObservation(systolicParam: Int, diastolicParam: Int, context: Context): String {
        val localResource =
            LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        var strObservation = ""
        var S = 0
        var D = 0
        var BP = 0
        S = if (systolicParam <= 90) {
            1
        } else if (systolicParam <= 120 && systolicParam > 90) {
            2
        } else if (systolicParam <= 129 && systolicParam > 120) {
            3
        } else if (systolicParam <= 139 && systolicParam > 129) {
            4
        } else if (systolicParam <= 180 && systolicParam > 139) {
            5
        } else {
            6
        }
        D = if (diastolicParam <= 60) {
            1
        } else if (diastolicParam <= 80 && diastolicParam > 60) {
            2
        } else if (diastolicParam <= 90 && diastolicParam > 80) {
            3
        } else if (diastolicParam <= 100 && diastolicParam > 90) {
            4
        } else if (diastolicParam <= 110 && diastolicParam > 100) {
            5
        } else {
            6
        }
        BP = S
        if (D > S) {
            BP = D
        }
        if (BP == 1) {
            strObservation = localResource.getString(R.string.LOW)
        } else if (BP == 2) {
            strObservation = localResource.getString(R.string.NORMAL)
        } else if (BP == 3) {
            strObservation = localResource.getString(R.string.PRE_HYPERTENSION)
        } else if (BP == 4) {
            strObservation = localResource.getString(R.string.HYPERTENSION_STAGE_1)
        } else if (BP == 5) {
            strObservation = localResource.getString(R.string.HYPERTENSION_STAGE_2)
        } else if (BP == 6) {
            strObservation = localResource.getString(R.string.HYPERTENSION_CRITICAL)
        }
        return strObservation
    }

    fun getWHRObservation(parameterVal: String, gender: Int, context: Context): String {
        var observation = ""
        try {
            if (!parameterVal.isEmpty()) {
                //var localResource = LocaleHelper.getLocalizedResources(context, Locale("en"))!!
                val localResource = LocaleHelper.getLocalizedResources(
                    context, Locale(LocaleHelper.getLanguage(context))
                )!!
                val whr: Double = parameterVal.toDouble()
                if (gender == 1) {
                    if (whr <= 0.95) {
                        observation = localResource.getString(R.string.NORMAL)
                    } else if (whr > 0.95 && whr <= 1) {
                        observation = localResource.getString(R.string.MODERATE)
                    } else if (whr > 1) {
                        observation = localResource.getString(R.string.HIGH)
                    }
                } else {
                    if (whr <= 0.80) {
                        observation = localResource.getString(R.string.NORMAL)
                    } else if (whr > 0.80 && whr <= 0.85) {
                        observation = localResource.getString(R.string.MODERATE)
                    } else if (whr > 0.85) {
                        observation = localResource.getString(R.string.HIGH)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return observation
    }

    fun getPulseObservation(pulseStr: String, context: Context): String {
        //var localResource = LocaleHelper.getLocalizedResources(context, Locale("en"))!!
        val localResource =
            LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        var observation = ""
        if (!pulseStr.isNullOrEmpty()) {
            val pulse = pulseStr.toDouble().toInt()
            if (pulse in 1..59) {
                observation = localResource.getString(R.string.LOW)
            } else if (pulse in 60..100) {
                observation = localResource.getString(R.string.NORMAL)
            } else if (pulse in 101..999) {
                observation = localResource.getString(R.string.HIGH)
            }
        }
        return observation
    }

    /*    private fun getBPObservation(systolic: Int, diastolic: Int, context: Context): String {
        var strObservation = ""
        when {
            ( systolic < 90 || diastolic < 60 ) -> {
                strObservation = context.resources.getString(R.string.LOW)
            }
            (systolic in 90..120 && diastolic in 60..80) -> {
                strObservation = context.resources.getString(R.string.NORMAL)
            }
            (systolic in 121..139 || diastolic in 81..89) -> {
                strObservation = context.resources.getString(R.string.HIGH_NORMAL)
            }
            (systolic >= 140 || diastolic >= 90) -> {
                strObservation = context.resources.getString(R.string.ABNORMAL)
            }
        }
        return strObservation
    }*/

    /*    fun convertToMetricFormat(strUnit: String, strCode: String, strValue: String): String {
            var strValue = strValue
            var strConvertedValue = ""
            if (!Utilities.isNullOrEmpty(strValue) && !Utilities.isNullOrEmpty(strValue.trim { it <= ' ' }) && !Utilities.isNullOrEmpty(strUnit)) {
                if (strCode.equals("WEIGHT", ignoreCase = true)) {
                    strConvertedValue = convertLbsToKg(strValue)
                } else if (strCode.equals("HEIGHT", ignoreCase = true)) {
                    if (!strValue.contains("/")) {
                        strValue = convertToImperialFormat(
                            HRAConstants.UNIT_IMPERIAL,
                            "HEIGHT",
                            strValue
                        )
                    }
                    if (!Utilities.isNullOrEmpty(strValue)) {
                        val separated = strValue.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        val first = separated[0]
                        val second = separated[1]
                        strConvertedValue = convertFeetInchToCm(first, second)
                    }
                } else if (strCode.equals("WAIST", ignoreCase = true) || strCode.equals("HIP", ignoreCase = true)) {
                    strConvertedValue = convertInchToCm(strValue)
                } else {
                    strConvertedValue = strValue
                }
            } else {
                strConvertedValue = strValue
            }
            return strConvertedValue
        }*/

    /*fun convertToImperialFormat(strUnit: String, strCode: String, strValue: String): String {
        var strConvertedValue = ""
        if (!Utilities.isNullOrEmpty(strValue) && !Utilities.isNullOrEmpty(strUnit) && !Utilities.isNullOrEmpty(strCode)) {
            if (!Utilities.isNullOrEmpty(strValue.trim { it <= ' ' }) && !Utilities.isNullOrEmpty(strUnit) && strUnit.equals(
                    HRAConstants.UNIT_IMPERIAL,
                    ignoreCase = true
                )
            ) {
                if (strCode.equals("WEIGHT", ignoreCase = true)) {
                    strConvertedValue = convertKgToLbs(strValue)
                } else if (strCode.equals("HEIGHT", ignoreCase = true)) {
                    strConvertedValue = convertCmToFeetInch(strValue)
                } else if (strCode.equals("WAIST", ignoreCase = true) || strCode.equals("HIP", ignoreCase = true)) {
                    strConvertedValue = convertCmToInch(strValue)
                } else {
                    strConvertedValue = strValue
                }
            } else {
                strConvertedValue = strValue
            }
        }
        return strConvertedValue
    }*/

    fun convertCmToFeetInch(strValue: String): String {
        try {
            val strConvertedValue: String
            var feet = floor(java.lang.Double.parseDouble(strValue) / 30.48).toInt()
            var inch =
                (java.lang.Double.parseDouble(strValue) / 2.54 - feet * 12).roundToLong().toInt()
            if (inch >= 12) {
                feet += 1
                inch = 0
            }
            strConvertedValue = "$feet ft, $inch in"
            return strConvertedValue
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "0"
    }

    fun convertCmToFeetInchDecimal(strValue: String): String {
        try {
            val strConvertedValue: String
            var feet = floor(java.lang.Double.parseDouble(strValue) / 30.48).toInt()
            var inch =
                (java.lang.Double.parseDouble(strValue) / 2.54 - feet * 12).roundToLong().toInt()
            if (inch >= 12) {
                feet += 1
                inch = 0
            }
            strConvertedValue = "$feet.$inch"
            return strConvertedValue
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "0"
    }

    /*    fun convertCmToInch2(strValue: String): String {
            val strConvertedValue: String = (java.lang.Double.parseDouble(strValue) * 0.393701).toString() + ""
            return roundOffPrecision(java.lang.Double.valueOf(strConvertedValue), 1).toString()
        }*/

    fun convertCmToInch2(strValue: String): String {
        val strConvertedValue: Double = (strValue.toDouble() * 0.393701)
        val value = roundOffPrecision(strConvertedValue, 1).toString()
        Utilities.printLogError("Converted_Inch--->$value")
        return value
    }

    fun convertCmToInch(strValue: String): Int {
        val strConvertedValue: Int
        val feet = floor(strValue.toDouble() / 30.48).toInt()
        var inch = (strValue.toDouble() / 2.54 - feet * 12).roundToLong().toInt()
        if (inch >= 12) {
            inch = 0
        }
        strConvertedValue = inch
        return strConvertedValue
    }

    /*    fun convertCmToFeet(strValue: String): Int {
            var strConvertedValue = 0
            var feet = Math.floor(strValue.toDouble() / 30.48).toInt()
            val inch = Math.round(strValue.toDouble() / 2.54 - feet * 12).toInt()

            if (inch >= 12) {
                feet = feet + 1
            }

            strConvertedValue = feet
            return strConvertedValue
        }*/

    fun convertCmToFeet(strValue: String): Int {
        val strConvertedValue: Int
        var feet = floor(strValue.toDouble() / 30.48).toInt()
        val inch = (strValue.toDouble() / 2.54 - feet * 12).roundToLong().toInt()

        if (inch >= 12) {
            feet += 1
        }

        strConvertedValue = feet
        return strConvertedValue
    }

    fun convertInchToCm(strValue: String): String {
        val strConvertedValue: Double = (strValue.toDouble() * 2.54)
        return roundOffPrecision(strConvertedValue, 2).toString()
    }

    fun inchesToCms(inches: Double): Double {
        return inches * 2.54f
    }

    fun convertKgToLbs(strValue: String): String {
        try {
            val strConvertedValue: Double = (strValue.toDouble() * 2.20462)
            return roundOffPrecision(strConvertedValue, 1).toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "0"
    }

    fun convertLbsToKg(strValue: String): String {
        val strConvertedValue: Double = (strValue.toDouble() * 0.453592)
        return roundOffPrecision(strConvertedValue, 1).toString()
    }

    fun convertMtrToKms(strValue: String): String {
        var dist = ""
        try {
            var convertedValue = strValue.toDouble()
            if (convertedValue >= 1000) {
                convertedValue = (convertedValue * 0.001)
                dist = Utilities.roundOffPrecision(convertedValue, 2).toString() + " km"
            } else {
                val abc = Utilities.roundOffPrecision(convertedValue, 2)
                dist = if (abc == 0.0) {
                    "0 mtr"
                } else {
                    "$abc mtr"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return dist
    }

    /*    fun convertMtrToKms(strValue: String): String {
            try {
                if (java.lang.Double.parseDouble(strValue) >= 1000) {
                    var strConvertedValue = ""
                    strConvertedValue = (java.lang.Double.parseDouble(strValue) * 0.001).toString() + ""
                    return (Utilities.roundOffPrecision(java.lang.Double.valueOf(strConvertedValue), 2) + " km").toString()
                } else {
                    return (Utilities.roundOffPrecision(java.lang.Double.valueOf(strValue), 2).toInt() + " mtr").toString()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return strValue
        }*/

    fun convertFeetInchToCm(strFeetValue: String, strInchValue: String): Double {
        //var strConvertedValue = ""
        var strConvertedValue = 0.0
        if (!Utilities.isNullOrEmpty(strFeetValue) && !Utilities.isNullOrEmpty(strInchValue)) {
            val cm =
                java.lang.Double.parseDouble(strFeetValue) * 30.48 + java.lang.Double.parseDouble(
                    strInchValue
                ) * 2.54
            //strConvertedValue = cm.toString() + ""
            strConvertedValue = cm
        }
        return strConvertedValue
    }

    fun roundOff(inputValue: Double, places: Int): Double {
        var value = inputValue
        if (places < 0) throw IllegalArgumentException()

        val factor = 10.0.pow(places.toDouble()).toLong()
        value *= factor
        val tmp = value.roundToInt()
        return tmp.toDouble() / factor
    }

    fun round(d: Double, decimalPlace: Int): Float {
        try {
            var bd = BigDecimal(d.toString())
            bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP)
            return bd.toFloat()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return d.toFloat()
    }

    fun roundOffPrecision(value: Double, precision: Int): Double {
        val scale = 10.0.pow(precision.toDouble()).toInt()
        return (value * scale).roundToInt().toDouble() / scale
    }

    fun getCaloriesFromSteps(stepsCount: Int, preferenceUtils: PreferenceUtils): Int {
        val gender = preferenceUtils.getPreference(PreferenceConstants.GENDER, "")
        val strideAsPerGenderInches = if (gender == "1") {
            84 * 0.415
        } else {
            84 * 0.413
        }
        return (138 * strideAsPerGenderInches * stepsCount / 138.462).toInt() / 1000
    }
}
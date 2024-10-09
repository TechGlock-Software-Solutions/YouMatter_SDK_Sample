package com.techglock.health.app.track_parameter.util

import android.content.Context
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.LocaleHelper
import com.techglock.health.app.common.utils.Utilities
import java.util.Locale

object TrackParameterHelper {

    fun getProfileImageByProfileCode(code: String, parameterCode: String = ""): Int {
        var image: Int = R.drawable.img_profile_bmi
        if (code.equals("BMI", ignoreCase = true)) {
            image = R.drawable.img_bmi
        } else if (code.equals("BLOODPRESSURE", ignoreCase = true)) {
            image = R.drawable.img_blood_pressure
        } else if (code.equals("DIABETIC", ignoreCase = true)) {
            image = R.drawable.img_blood_sugar
        } else if (code.equals("HEMOGRAM", ignoreCase = true)) {
            image = R.drawable.img_profile_hemogram
        } else if (code.equals("KIDNEY", ignoreCase = true)) {
            image = R.drawable.img_profile_kidney
        } else if (code.equals("LIPID", ignoreCase = true)) {
            image = R.drawable.img_profile_lipid
        } else if (code.equals("LIVER", ignoreCase = true)) {
            image = R.drawable.img_profile_liver
        } else if (code.equals("THYROID", ignoreCase = true)) {
            image = R.drawable.img_profile_thyroid
        } else if (code.equals("URINE", ignoreCase = true)) {
            image = R.drawable.img_profile_urine
        } else if (code.equals("WHR", ignoreCase = true)) {
            image = R.drawable.img_whr
        }

        if (parameterCode.equals("HEIGHT", true)) {
            image = R.drawable.img_height
        } else if (parameterCode.equals("WEIGHT", true)) {
            image = R.drawable.img_weight
        }
        return image
    }

    fun getParameterImageByProfileCode(code: String, parameterCode: String = ""): Int {
        var image: Int = R.drawable.img_profile_bmi
        if (code.equals("BMI", ignoreCase = true)) {
            image = R.drawable.img_bmi
        } else if (code.equals("BLOODPRESSURE", ignoreCase = true)) {
            image = R.drawable.img_blood_pressure
        } else if (code.equals("DIABETIC", ignoreCase = true)) {
            image = R.drawable.img_blood_sugar
        } else if (code.equals("HEMOGRAM", ignoreCase = true)) {
            image = R.drawable.img_profile_hemogram
        } else if (code.equals("KIDNEY", ignoreCase = true)) {
            image = R.drawable.img_profile_kidney
        } else if (code.equals("LIPID", ignoreCase = true)) {
            image = R.drawable.img_cholesteroal
        } else if (code.equals("LIVER", ignoreCase = true)) {
            image = R.drawable.img_profile_liver
        } else if (code.equals("THYROID", ignoreCase = true)) {
            image = R.drawable.img_profile_thyroid
        } else if (code.equals("URINE", ignoreCase = true)) {
            image = R.drawable.img_profile_urine
        } else if (code.equals("WHR", ignoreCase = true)) {
            image = R.drawable.img_whr
        }

        if (parameterCode.equals("HEIGHT", true)) {
            image = R.drawable.img_height
        } else if (parameterCode.equals("WEIGHT", true)) {
            image = R.drawable.img_weight
        }
        return image
    }

    fun getProfileNameByProfileCode(code: String): Int {
        var profileName: Int = R.string.BMI
        if (code.equals("BMI", ignoreCase = true)) {
            profileName = R.string.BMI
        } else if (code.equals("BLOODPRESSURE", ignoreCase = true)) {
            profileName = R.string.BLOOD_PRESSURE
        } else if (code.equals("DIABETIC", ignoreCase = true)) {
            profileName = R.string.BLOOD_SUGAR
        } else if (code.equals("HEMOGRAM", ignoreCase = true)) {
            profileName = R.string.HEMOGRAM
        } else if (code.equals("KIDNEY", ignoreCase = true)) {
            profileName = R.string.KIDNEY
        } else if (code.equals("LIPID", ignoreCase = true)) {
            profileName = R.string.LIPID
        } else if (code.equals("LIVER", ignoreCase = true)) {
            profileName = R.string.LIVER
        } else if (code.equals("THYROID", ignoreCase = true)) {
            profileName = R.string.THYROID
        } else if (code.equals("URINE", ignoreCase = true)) {
            profileName = R.string.URINE
        } else if (code.equals("WHR", ignoreCase = true)) {
            profileName = R.string.WHR
        }
        return profileName
    }

    fun getProfileByProfileCode(code: String): Int {
        var profileName: Int = R.string.BMI
        if (code.equals("BMI", ignoreCase = true)) {
            profileName = R.string.BMI
        } else if (code.equals("BLOODPRESSURE", ignoreCase = true)) {
            profileName = R.string.BLOOD_PRESSURE
        } else if (code.equals("DIABETIC", ignoreCase = true)) {
            profileName = R.string.BLOOD_SUGAR_PROFILE
        } else if (code.equals("HEMOGRAM", ignoreCase = true)) {
            profileName = R.string.HEMOGRAM_PROFILE
        } else if (code.equals("KIDNEY", ignoreCase = true)) {
            profileName = R.string.KIDNEY_PROFILE
        } else if (code.equals("LIPID", ignoreCase = true)) {
            profileName = R.string.LIPID_PROFILE
        } else if (code.equals("LIVER", ignoreCase = true)) {
            profileName = R.string.LIVER_PROFILE
        } else if (code.equals("THYROID", ignoreCase = true)) {
            profileName = R.string.THYROID_PROFILE
        } else if (code.equals("URINE", ignoreCase = true)) {
            profileName = R.string.URINE
        } else if (code.equals("WHR", ignoreCase = true)) {
            profileName = R.string.WHR
        }
        return profileName
    }

    fun convertFeetInchToCm(strFeetValue: String, strInchValue: String?): String {
        var strConvertedValue = ""
        if (strInchValue != null) {
            if (strInchValue.isNotEmpty() && strInchValue.isNotEmpty()) {
                val cm = strFeetValue.toDouble() * 30.48 + strInchValue.toDouble() * 2.54
                strConvertedValue = cm.toInt().toString() + ""
            }
        }
        return strConvertedValue
    }

    fun isNullOrEmptyOrZero(maxPermissibleValue: String?): Boolean {
        var result = false
        try {
            result = (maxPermissibleValue == null
                    || maxPermissibleValue == ""
                    || maxPermissibleValue.equals("null", ignoreCase = true)
                    || maxPermissibleValue.equals("NULL", ignoreCase = true)
                    || maxPermissibleValue == "."
                    || maxPermissibleValue == "0"
                    || maxPermissibleValue == "0.0"
                    || maxPermissibleValue == "0.00")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    fun getObservationColor(observation: String?, profileCode: String): Int {
        var color: Int = R.color.almost_black
        if (!Utilities.isNullOrEmpty(observation)) {
            color =
                when (observation!!.uppercase()) {
                    "VERY HIGH", "HIGH RISK", "HIGH", "HIGH NORMAL", "DIABETIC", "MILDLY HIGH", "VERY LOW", "LOW", "POOR", "MILDLY LOW", "ABNORMAL" -> R.color.vivant_watermelon
                    "BORDERLINE HIGH", "LOW RISK", "EARLY DIABETIC", "MODERATE", "BETTER", "NEAR OPTIMAL", "MODERATE LOW", "MILD LOW", "MILD HIGH", "MODERATE HIGH", "AVERAGE RISK" -> R.color.vivant_orange_yellow
                    "DESIRABLE", "BEST", "NORMAL", "OPTIMAL", "GOOD" -> R.color.vivant_nasty_green
                    else -> R.color.almost_black
                }
            if (profileCode.equals("BMI", ignoreCase = true)) {
                /* if (observation.equalsIgnoreCase("Underweight") || observation.equalsIgnoreCase("Overweight")) {
                    color = R.color.vivant_orange_yellow;
                } else*/
                color = if (observation.equals("Normal", ignoreCase = true)) {
                    R.color.vivant_nasty_green
                } else {
                    R.color.vivant_watermelon
                }
            }
            if (profileCode.equals("BLOODPRESSURE", ignoreCase = true)) {
                color = if (observation.equals("Low", ignoreCase = true)) {
                    R.color.vivant_orange_yellow
                } else if (observation.equals("Normal", ignoreCase = true)) {
                    R.color.vivant_nasty_green
                } else {
                    R.color.vivant_watermelon
                }
            }
        }
        return color
    }

    fun getObservationColorInHex(observation: String, profileCode: String): String {
        var color = "#3A393B"
        if (!Utilities.isNullOrEmpty(observation)) {
            color =
                when (observation.uppercase()) {
                    "VERY HIGH", "HIGH RISK", "HIGH", "HIGH NORMAL", "DIABETIC", "MILDLY HIGH", "VERY LOW", "LOW", "POOR", "MILDLY LOW", "ABNORMAL" -> "#ff485d"
                    "BORDERLINE HIGH", "LOW RISK", "EARLY DIABETIC", "MODERATE", "BETTER", "NEAR OPTIMAL", "MODERATE LOW", "MILD LOW", "MILD HIGH", "MODERATE HIGH", "AVERAGE RISK" -> "#ffa000"
                    "DESIRABLE", "BEST", "NORMAL", "OPTIMAL", "GOOD" -> "#84c449"
                    else -> "#3A393B"
                }
            if (profileCode.equals("BMI", ignoreCase = true)) {
                /* if (observation.equalsIgnoreCase("Underweight") || observation.equalsIgnoreCase("Overweight")) {
                    color = R.color.vivant_orange_yellow;
                } else*/
                color = if (observation.equals("Normal", ignoreCase = true)) {
                    "#84c449"
                } else {
                    "#ff485d"
                }
            }
            if (profileCode.equals("BLOODPRESSURE", ignoreCase = true)) {
                color = if (observation.equals("Low", ignoreCase = true)) {
                    "#ffa000"
                } else if (observation.equals("Normal", ignoreCase = true)) {
                    "#84c449"
                } else {
                    "#ff485d"
                }
            }
        }
        return color
    }

    fun getLocalizeObservation(obsVal: String?, context: Context?): String? {
        var obs = obsVal
        if (context != null && !obsVal.isNullOrEmpty()) {
            val observation = obsVal.uppercase()
            val localResource = LocaleHelper.getLocalizedResources(
                context,
                Locale(LocaleHelper.getLanguage(context))
            )!!
            if (observation.equals("NORMAL", true)) {
                obs = localResource.getString(R.string.NORMAL)
            } else if (observation.equals("HIGH", true)) {
                obs = localResource.getString(R.string.HIGH)
            } else if (observation.equals("LOW", true)) {
                obs = localResource.getString(R.string.LOW)
            } else if (observation.equals("MODERATE", true)) {
                obs = localResource.getString(R.string.MODERATE)
            } else if (observation.equals("UNDERWEIGHT", true)) {
                obs = localResource.getString(R.string.UNDERWEIGHT)
            } else if (observation.equals("OVERWEIGHT", true)) {
                obs = localResource.getString(R.string.OVERWEIGHT)
            } else if (observation.equals("PRE-OBESE", true)) {
                obs = localResource.getString(R.string.PRE_OBESE)
            } else if (observation.equals("HIGH NORNAL", true)) {
                obs = localResource.getString(R.string.HIGH_NORMAL)
            } else if (observation.equals("ABNORMAL", true)) {
                obs = localResource.getString(R.string.ABNORMAL)
            } else if (observation.equals("Obese Level 1", true)) {
                obs = localResource.getString(R.string.OBESE_LEVEL_1)
            } else if (observation.equals("Obese Level 2", true)) {
                obs = localResource.getString(R.string.OBESE_LEVEL_2)
            } else if (observation.equals("Obese Level 3", true)) {
                obs = localResource.getString(R.string.OBESE_LEVEL_3)
            } else if (observation.equals("PREHYPERTENSION", true)) {
                obs = localResource.getString(R.string.PRE_HYPERTENSION)
            } else if (observation.equals("DIABETIC", true)) {
                obs = localResource.getString(R.string.DIABETIC)
            } else if (observation.equals("VERY HIGH", true)) {
                obs = localResource.getString(R.string.VERY_HIGH)
            } else if (observation.equals("VERY LOW", true)) {
                obs = localResource.getString(R.string.VERY_LOW)
            } else if (observation.equals("MILDLY HIGH", true)) {
                obs = localResource.getString(R.string.MILDLY_HIGH)
            } else if (observation.equals("MILDLY LOW", true)) {
                obs = localResource.getString(R.string.MILDLY_LOW)
            } else if (observation.equals("BORDERLINE HIGH", true)) {
                obs = localResource.getString(R.string.BORDERLINE_HIGH)
            } else if (observation.equals("EARLY DIABETIC", true)) {
                obs = localResource.getString(R.string.EARLY_DIABETIC)
            } else if (observation.equals("BETTER", true)) {
                obs = localResource.getString(R.string.BETTER)
            } else if (observation.equals("NEAR OPTIMAL", true)) {
                obs = localResource.getString(R.string.NEAR_OPTIMAL)
            } else if (observation.equals("MODERATE LOW", true)) {
                obs = localResource.getString(R.string.MODERATE_LOW)
            } else if (observation.equals("MODERATE HIGH", true)) {
                obs = localResource.getString(R.string.MODERATE_HIGH)
            } else if (observation.equals("MILD LOW", true)) {
                obs = localResource.getString(R.string.MILD_LOW)
            } else if (observation.equals("MILD HIGH", true)) {
                obs = localResource.getString(R.string.MILD_HIGH)
            } else if (observation.equals("AVERAGE RISK", true)) {
                obs = localResource.getString(R.string.AVERAGE_RISK)
            } else if (observation.equals("DESIRABLE", true)) {
                obs = localResource.getString(R.string.DESIRABLE)
            } else if (observation.equals("BEST", true)) {
                obs = localResource.getString(R.string.BEST)
            } else if (observation.equals("OPTIMAL", true)) {
                obs = localResource.getString(R.string.OPTIMAL)
            } else if (observation.equals("GOOD", true)) {
                obs = localResource.getString(R.string.GOOD)
            }
        }
        return obs
    }

    /*    fun getObservationColor(observation: String, profileCode: String): Int {
        var color: Int = R.color.vivant_charcoal_grey_55
        if (!Utilities.isNullOrEmpty(observation)) {
            color =
                when (observation.uppercase()) {
                    "VERY HIGH", "HIGH", "DIABETIC", "MILDLY HIGH", "VERY LOW", "LOW", "POOR", "MILDLY LOW" -> R.color.vivant_watermelon
                    "BORDERLINE HIGH", "EARLY DIABETIC", "MODERATE", "BETTER", "NEAR OPTIMAL", "MODERATE LOW", "MILD LOW", "MILD HIGH", "MODERATE HIGH" -> R.color.vivant_orange_yellow
                    "DESIRABLE", "BEST", "NORMAL", "OPTIMAL", "GOOD" -> R.color.vivant_nasty_green
                    else -> R.color.vivant_charcoal_grey_55
                }
            if (profileCode.equals("BMI", ignoreCase = true)) {
                *//* if (observation.equalsIgnoreCase("Underweight") || observation.equalsIgnoreCase("Overweight")) {
                    color = R.color.vivant_orange_yellow;
                } else*//*
                color = if (observation.equals("Normal", ignoreCase = true)) {
                    R.color.vivant_nasty_green
                } else {
                    R.color.vivant_watermelon
                }
            }
            if (profileCode.equals("BLOODPRESSURE", ignoreCase = true)) {
                color = if (observation.equals("Low", ignoreCase = true)) {
                    R.color.vivant_orange_yellow
                } else if (observation.equals("Normal", ignoreCase = true)) {
                    R.color.vivant_nasty_green
                } else {
                    R.color.vivant_watermelon
                }
            }
        }
        return color
    }*/

    /*fun getProfilePosition(selectedProfilesList: ArrayList<ArrayMap<String?, String?>>): Int {
    var position = 0
    if (CacheFactory.has(GlobalConstants.PROFILE_CODE)) {
        for (i in selectedProfilesList.indices) {
            if (selectedProfilesList[i][GlobalConstants.PROFILE_CODE].equals(
                    CacheFactory.get(
                        GlobalConstants.PROFILE_CODE
                    ), ignoreCase = true
                )
            ) {
                position = i
                //                    CacheFactory.remove(GlobalConstants.PROFILE_CODE);
                break
            }
        }
    }
    return position
}*/

}
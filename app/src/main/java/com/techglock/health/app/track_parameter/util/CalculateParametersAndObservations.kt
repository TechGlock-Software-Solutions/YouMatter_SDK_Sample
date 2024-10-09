package com.techglock.health.app.track_parameter.util

import android.util.ArrayMap
import com.techglock.health.app.common.utils.Utilities
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * This class is used for calculate trackparameters and also get observation
 */
object CalculateParametersAndObservations {

    object GlobalConstants {

        val BMI_OBSERVATION_COLOR = "bmi_color"
        val BMI_OBSERVATION_DESCRIPTION = "bmi_description"
    }

    /**
     * gets BMI
     *
     * @param heightParam
     * @param weightParam
     * @return
     */
    fun getBMIFromMetric(heightParam: Double, weightParam: Double): Double {
        var height = 0.0
        var weight = 0.0
        var bmi = 0.0
        try {
            height = heightParam
            weight = weightParam

            /* if (Helper.isNullOrEmpty(String.valueOf(height)) || height <= 0) {
                return 0;
            } else if (Helper.isNullOrEmpty(String.valueOf(weight)) || weight <= 0) {
                return 0;
            } else {
                try {
                    //bmi = Math.round(Float.parseFloat(String.valueOf(weight)) / Math.pow(Float.parseFloat(String.valueOf(height)) / 100, 2));

                    bmi = Float.parseFloat(String.valueOf(weight)) / Math.pow(Float.parseFloat(String.valueOf(height)) / 100, 2);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }*/


            if (Utilities.isNullOrEmpty(height.toString())) {
                return 0.0
            } else if (Utilities.isNullOrEmpty(weight.toString())) {
                return 0.0
            } else {
                try {
                    val weightValue = java.lang.Float.parseFloat(weight.toString())
                    val heightValue = java.lang.Float.parseFloat(height.toString())/* if ((!Helper.isNullOrEmpty(strUnit) && strUnit.equalsIgnoreCase(GlobalConstants.UNIT_IMPERIAL)
                            && heightValue >= 121 && heightValue <= 214 && weightValue >= 66 && weightValue <= 551)
                            || (!Helper.isNullOrEmpty(strUnit) && strUnit.equalsIgnoreCase(GlobalConstants.UNIT_METRIC)
                            && heightValue >= 121 && heightValue <= 214 && weightValue >= 30 && weightValue <= 250)) {*/
                    if (weightValue >= 30 && weightValue <= 151 && heightValue >= 121 && heightValue <= 214) {
                        bmi = weightValue / Math.pow((heightValue / 100).toDouble(), 2.0)
                        //bmiAsString = new DecimalFormat("##.##").format(bmi).toString();
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

        } finally {
            // height = null;
            // weight = null;
        }
        return bmi
    }

    fun getBMIFromImperial(heightFeet: Double, heightInch: Double, weightParam: Double): Double {
        var height = 0.0
        var weight = 0.0
        var bmi = 0.0
        try {
            height = java.lang.Double.parseDouble(
                Utilities.convertFeetInchToCm(
                    heightFeet.toString(), heightInch.toString()
                )
            )
            weight = weightParam

            bmi = getBMIFromMetric(height, weightParam)
        } finally {
            // height = null;
            // weight = null;
        }
        return bmi
    }

    /**
     * gets WHR
     *
     * @param waistParam
     * @param hipParam
     * @return
     */
    fun getWHR(waistParam: Double, hipParam: Double): String {
        var waist: String? = ""
        var hip: String? = ""
        var whr = 0f
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
                    val nm = NumberFormat.getNumberInstance()
                    //whrAsString=String.valueOf(whr);
                    //whrAsString=(nm.format(whr));
                    whrAsString = DecimalFormat("##.##").format(whr.toDouble()).toString()
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }

            }
        } finally {
            waist = null
            hip = null
        }
        return whrAsString
    }

    /**
     * get BP Observation
     *
     * @param systolicParam
     * @param diastolicParam
     * @return
     */
    fun getBPObservation(systolicParam: Int, diastolicParam: Int): String {
        var strObservation = ""
        var S = 0
        var D = 0
        var BP = 0
        S = if (systolicParam < 90) {
            1
        } else if (systolicParam in 90..119) {
            2
        } else if (systolicParam in 120..139) {
            3
        } else if (systolicParam in 140..159) {
            4
        } else if (systolicParam in 160..179) {
            5
        } else {
            6
        }

        D = if (diastolicParam < 60) {
            1
        } else if (diastolicParam in 60..79) {
            2
        } else if (diastolicParam in 80..89) {
            3
        } else if (diastolicParam in 90..99) {
            4
        } else if (diastolicParam in 100..109) {
            5
        } else {
            6
        }

        BP = S
        if (D > S) {
            BP = D
        }

        when (BP) {
            1 -> {
                strObservation = "Low"
            }

            2 -> {
                strObservation = "Normal"
            }

            3 -> {
                strObservation = "PreHypertension"
            }

            4 -> {
                strObservation = "Hypertension- Stage 1"
            }

            5 -> {
                strObservation = "Hypertension- Stage 2"
            }

            6 -> {
                strObservation = "Hypertension- Critical"
            }
        }
        return strObservation
    }

    fun getBloodSugarObservation(strLabValue: String): String {
        var strObservations = ""
        val lab = roundOff(java.lang.Double.parseDouble(strLabValue), 1)
        if (lab in 61.0..79.99) {
            strObservations = "Low"
        } else if (lab in 80.0..140.0) {
            strObservations = "Normal"
        } else if (lab in 140.0..199.99) {
            strObservations = "Early Diabetic"
        } else if (lab in 200.0..999.99) {
            strObservations = "Diabetic"
        }
        return strObservations
    }

    fun getCholesterolObservation(strLabValue: String): String {
        var strObservations = ""
        val lab = roundOff(java.lang.Double.parseDouble(strLabValue), 1)
        if (lab in 50.0..199.99) {
            strObservations = "Desirable"
        } else if (lab in 200.0..239.99) {
            strObservations = "Mildly High"
        } else if (lab in 240.0..400.0) {
            strObservations = "High"
        }
        return strObservations
    }

    /**
     * get BMI Observation
     *
     * @param bmiParam
     * @return
     */
    fun getBMIObservation(bmiParam: Double): ArrayMap<String, String> {
        val bmiObservationMap = ArrayMap<String, String>()
        if (bmiParam <= 18.49) {
            bmiObservationMap[GlobalConstants.BMI_OBSERVATION_COLOR] = "#4FC1C1"
            bmiObservationMap[GlobalConstants.BMI_OBSERVATION_DESCRIPTION] = "Underweight"
            return bmiObservationMap
        } else if (bmiParam in 18.5..22.99) {
            bmiObservationMap[GlobalConstants.BMI_OBSERVATION_COLOR] = "#B4D852"
            bmiObservationMap[GlobalConstants.BMI_OBSERVATION_DESCRIPTION] = "Normal"
            return bmiObservationMap
        } else if (bmiParam in 23.0..24.99) {
            bmiObservationMap[GlobalConstants.BMI_OBSERVATION_COLOR] = "#FFD401"
            bmiObservationMap[GlobalConstants.BMI_OBSERVATION_DESCRIPTION] = "Overweight"
            return bmiObservationMap
        } else if (bmiParam in 25.0..29.99) {
            bmiObservationMap[GlobalConstants.BMI_OBSERVATION_COLOR] = "#FD836E"
            bmiObservationMap[GlobalConstants.BMI_OBSERVATION_DESCRIPTION] = "Pre-Obese"
            return bmiObservationMap
        } else if (bmiParam in 30.0..34.99) {
            bmiObservationMap[GlobalConstants.BMI_OBSERVATION_COLOR] = "#FD836E"
            bmiObservationMap[GlobalConstants.BMI_OBSERVATION_DESCRIPTION] = "Obese Level 1"
            return bmiObservationMap
        } else if (bmiParam in 35.0..39.99) {
            bmiObservationMap[GlobalConstants.BMI_OBSERVATION_COLOR] = "#FD836E"
            bmiObservationMap[GlobalConstants.BMI_OBSERVATION_DESCRIPTION] = "Obese Level 2"
            return bmiObservationMap
        } else {
            bmiObservationMap[GlobalConstants.BMI_OBSERVATION_COLOR] = "#FD836E"
            bmiObservationMap[GlobalConstants.BMI_OBSERVATION_DESCRIPTION] = "Obese Level 3"
            return bmiObservationMap
        }
    }

    fun getBMIObservation(strBMI: String): String {
        val bmi = roundOff(java.lang.Double.parseDouble(strBMI), 1)
        var strObservations = ""
        strObservations = if (bmi <= 18.49) {
            "Underweight"
        } else if (bmi in 18.5..22.99) {
            "Normal"
        } else if (bmi in 23.0..24.99) {
            "Overweight"
        } else if (bmi in 25.0..29.99) {
            "Pre-Obese"
        } else if (bmi in 30.0..34.99) {
            "Obese Level 1"
        } else if (bmi in 35.0..39.99) {
            "Obese Level 2"
        } else {
            "Obese Level 3"
        }
        return strObservations
    }

    private fun roundOff(value: Double, places: Int): Double {
        var newValue = value
        require(places >= 0)

        val factor = 10.0.pow(places.toDouble()).toLong()
        newValue *= factor
        val tmp = newValue.roundToInt()
        return tmp.toDouble() / factor
    }
}

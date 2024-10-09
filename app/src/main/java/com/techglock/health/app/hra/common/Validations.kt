package com.techglock.health.app.hra.common

import android.content.Context
import android.widget.CheckBox
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.LocaleHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.common.view.FlowLayout
import java.util.Locale

object Validations {

    fun validationBMI(height: Double, weight: Double, context: Context): Boolean {
        val localResource =
            LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        Utilities.printLog("Height , Weight----->$height $weight")
        var isValid = false

        when {

            (height in 120.0..245.0 && weight in 30.0..251.0) -> {
                isValid = true
            }

            (height <= 0) -> {
                isValid = false
            }

            (weight <= 0) -> {
                isValid = false
            }

            (height < 120 || height > 245) -> {
                Utilities.toastMessageShort(
                    context,
                    localResource.getString(R.string.PLEASE_SELECT_VALID_INPUT_HEIGHT)
                )
                isValid = false
            }

            (weight < 30 || weight > 251) -> {
                Utilities.toastMessageShort(
                    context,
                    localResource.getString(R.string.PLEASE_SELECT_VALID_INPUT_WEIGHT)
                )
                isValid = false
            }

        }
        return isValid
    }

    fun validateBP(systolic: Int, diastolic: Int, context: Context): Boolean {
        val localResource =
            LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        var isValid = false
        Utilities.printLog("BloodPressure---> $systolic , $diastolic")
        when {

            systolic in 10..350 && diastolic in 0..150 && (systolic > diastolic) -> {
                isValid = true
            }

            systolic in 10..350 && diastolic in 0..150 && (systolic <= diastolic) -> {
                Utilities.toastMessageShort(
                    context,
                    localResource.getString(R.string.SYSTOLIC_BP_SHOULD_NOT_LESS_THAN_DIASTOLIC_BP)
                )
                isValid = false
            }

            (systolic < 10) || (systolic > 350) -> {
                Utilities.toastMessageShort(
                    context,
                    localResource.getString(R.string.PLEASE_INSERT_SYSTOLIC_BP_VALUE_BETWEEN_10_TO_300)
                )
                isValid = false
            }

            (diastolic < 0) || (diastolic > 150) -> {
                Utilities.toastMessageShort(
                    context,
                    localResource.getString(R.string.PLEASE_INSERT_DIASTOLIC_BP_VALUE_BETWEEN_0_TO_150)
                )
                isValid = false
            }


        }
        return isValid
    }

    fun validateMultiSelectionOptions(flowLayout: FlowLayout): Boolean {
        var isValid = false
        for (i in 0 until flowLayout.childCount) {
            val chk = flowLayout.getChildAt(i) as CheckBox
            if (chk.isChecked) {
                isValid = true
                break
            }
        }
        return isValid
    }

}
package com.techglock.health.app.security

import android.content.Context
import com.techglock.health.app.R
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.LocaleHelper
import com.techglock.health.app.model.home.LanguageModel
import com.techglock.health.app.security.model.EmployerModel
import com.techglock.health.app.security.model.GenderModel
import java.util.Locale

object SecurityHelper {

    fun getLanguageList(context: Context): ArrayList<LanguageModel> {
        val localResource =
            LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val list: ArrayList<LanguageModel> = ArrayList()
        if (LocaleHelper.getLanguage(context) == Constants.LANGUAGE_CODE_ENGLISH) {
            list.add(
                LanguageModel(
                    localResource.getString(R.string.ENGLISH),
                    Constants.LANGUAGE_CODE_ENGLISH,
                    R.drawable.img_english,
                    R.color.color_english,
                    true
                )
            )
            list.add(
                LanguageModel(
                    localResource.getString(R.string.HINDI),
                    Constants.LANGUAGE_CODE_HINDI,
                    R.drawable.img_hindi,
                    R.color.color_hindi,
                    false
                )
            )
        } else {
            list.add(
                LanguageModel(
                    localResource.getString(R.string.ENGLISH),
                    Constants.LANGUAGE_CODE_ENGLISH,
                    R.drawable.img_english,
                    R.color.color_english,
                    false
                )
            )
            list.add(
                LanguageModel(
                    localResource.getString(R.string.HINDI),
                    Constants.LANGUAGE_CODE_HINDI,
                    R.drawable.img_hindi,
                    R.color.color_hindi,
                    true
                )
            )
        }
        return list
    }

    fun getUserGenderList(context: Context): MutableList<GenderModel> {
        val localResource =
            LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val list: MutableList<GenderModel> = mutableListOf()
        list.add(
            GenderModel(
                R.drawable.img_signup_male,
                localResource.getString(R.string.MALE),
                "Male"
            )
        )
        list.add(
            GenderModel(
                R.drawable.img_signup_female,
                localResource.getString(R.string.FEMALE),
                "Female"
            )
        )
        //list.add(GenderModel(R.drawable.img_signup_other,localResource.getString(R.string.OTHER),"Other"))
        return list
    }

    fun getEmployerList(context: Context): MutableList<EmployerModel> {
        val localResource =
            LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        val list: MutableList<EmployerModel> = mutableListOf()
        list.add(
            EmployerModel(
                localResource.getString(R.string.SUD_LIFE_INSURANCE),
                Constants.SUD_LIFE
            )
        )
        list.add(EmployerModel(localResource.getString(R.string.BANK_OF_INDIA), Constants.BOI))
        list.add(
            EmployerModel(
                localResource.getString(R.string.UNION_BANK_OF_INDIA),
                Constants.UBI
            )
        )
        list.add(EmployerModel(localResource.getString(R.string.NONE_OF_ABOVE), Constants.CUSTOMER))
        return list
    }

}
package com.techglock.health.app.security.ui_dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import com.techglock.health.app.R
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.LocaleHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.DialogLanguageBinding
import com.techglock.health.app.model.home.LanguageModel
import com.techglock.health.app.security.SecurityHelper
import com.techglock.health.app.security.adapter.LanguageDialogAdapter
import java.util.Locale

class DialogLanguage(
    private val mContext: Context,
    private val listener: OnLanguageClickListener
) : Dialog(mContext), LanguageDialogAdapter.OnLanguageItemClickListener {

    private lateinit var binding: DialogLanguageBinding

    private var languageDialogAdapter: LanguageDialogAdapter? = null
    private var languageDataSet = LanguageModel()
    //private var languageDataSet:LanguageModel = LanguageModel("English","en")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.currentFocus
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        initialise()
    }

    private fun initialise() {
        val localResource =
            LocaleHelper.getLocalizedResources(context, Locale(LocaleHelper.getLanguage(context)))!!
        languageDataSet = if (LocaleHelper.getLanguage(mContext) == Constants.LANGUAGE_CODE_HINDI) {
            LanguageModel(
                localResource.getString(R.string.HINDI),
                Constants.LANGUAGE_CODE_HINDI,
                R.drawable.img_hindi,
                R.color.color_hindi,
                true
            )
        } else {
            LanguageModel(
                localResource.getString(R.string.ENGLISH),
                Constants.LANGUAGE_CODE_ENGLISH,
                R.drawable.img_english,
                R.color.color_english,
                true
            )
        }

        languageDialogAdapter = LanguageDialogAdapter(mContext, this)
        binding.rvLanguageList.adapter = languageDialogAdapter
        languageDialogAdapter!!.updateList(SecurityHelper.getLanguageList(mContext))

        binding.btnProceed.setOnClickListener {
            languageDataSet.selectionStatus = true
            listener.onLanguageSelection(languageDataSet)
            dismiss()
        }

        binding.imgCloseDialog.setOnClickListener {
            dismiss()
        }
    }

    override fun onLanguageItemSelection(position: Int, data: LanguageModel) {
        languageDataSet = data
        Utilities.printLogError("LanguageCode---> " + data.language + " :: " + data.languageCode)
    }

    interface OnLanguageClickListener {
        fun onLanguageSelection(data: LanguageModel)
    }

}
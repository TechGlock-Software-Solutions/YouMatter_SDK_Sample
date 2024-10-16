package com.techglock.health.app.tools_calculators.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.view.ViewGroup
import android.view.Window
import com.techglock.health.app.R
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.LocaleHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.DialogDisclaimerBinding

class DialogDisclaimer(context: Context) : Dialog(context) {

    private lateinit var binding: DialogDisclaimerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogDisclaimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.currentFocus
        setCancelable(false)
        setCanceledOnTouchOutside(false)

        if (LocaleHelper.getLanguage(context) == Constants.LANGUAGE_CODE_HINDI) {
            binding.txtDisclaimerCardiovascularDesc.text = Html.fromHtml(
                "<a><font color='#0273ff'>" + context.resources.getString(R.string.HERE) + "</font></a> " + "<a>" + context.resources.getString(
                    R.string.CLICK
                ) + "</a>"
            )
            binding.txtDisclaimerHeartAgeDesc.text = Html.fromHtml(
                "<a><font color='#0273ff'>" + context.resources.getString(R.string.HERE) + "</font></a> " + "<a>" + context.resources.getString(
                    R.string.CLICK
                ) + "</a>"
            )
            binding.txtDisclaimerWomenHealthDesc.text = Html.fromHtml(
                "<a><font color='#0273ff'>" + context.resources.getString(R.string.HERE) + "</font></a> " + "<a>" + context.resources.getString(
                    R.string.CLICK
                ) + "</a>"
            )
            binding.txtDisclaimerDassDesc.text = Html.fromHtml(
                "<a><font color='#0273ff'>" + context.resources.getString(R.string.HERE) + "</font></a> " + "<a>" + context.resources.getString(
                    R.string.CLICK
                ) + "</a>"
            )
        } else {
            binding.txtDisclaimerCardiovascularDesc.text = Html.fromHtml(
                "<a>" + context.resources.getString(R.string.CLICK) + "</a> " + "<a><font color='#0273ff'>" + context.resources.getString(
                    R.string.HERE
                ) + "</font></a>"
            )
            binding.txtDisclaimerHeartAgeDesc.text = Html.fromHtml(
                "<a>" + context.resources.getString(R.string.CLICK) + "</a> " + "<a><font color='#0273ff'>" + context.resources.getString(
                    R.string.HERE
                ) + "</font></a>"
            )
            binding.txtDisclaimerWomenHealthDesc.text = Html.fromHtml(
                "<a>" + context.resources.getString(R.string.CLICK) + "</a> " + "<a><font color='#0273ff'>" + context.resources.getString(
                    R.string.HERE
                ) + "</font></a>"
            )
            binding.txtDisclaimerDassDesc.text = Html.fromHtml(
                "<a>" + context.resources.getString(R.string.CLICK) + "</a> " + "<a><font color='#0273ff'>" + context.resources.getString(
                    R.string.HERE
                ) + "</font></a>"
            )
        }

        setClickable()
    }

    private fun setClickable() {

        binding.txtDisclaimerCardiovascularDesc.setOnClickListener {
            Utilities.redirectToChrome(Constants.LINK_CARDIOVASCULAR, context)
        }

        binding.txtDisclaimerHeartAgeDesc.setOnClickListener {
            Utilities.redirectToChrome(Constants.LINK_HEART_AGE, context)
        }

        binding.txtDisclaimerWomenHealthDesc.setOnClickListener {
            Utilities.redirectToChrome(Constants.LINK_WOMEN_HEALTH, context)
        }

        binding.txtDisclaimerDassDesc.setOnClickListener {
            Utilities.redirectToChrome(Constants.LINK_DASS, context)
        }

        binding.btnOk.setOnClickListener {
            dismiss()
        }

        binding.imgCloseInput.setOnClickListener {
            dismiss()
        }

    }

}
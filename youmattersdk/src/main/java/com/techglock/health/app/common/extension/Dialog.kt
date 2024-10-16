package com.techglock.health.app.common.extension

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseAdapter
import com.techglock.health.app.databinding.DialogLanguageBinding
import com.techglock.health.app.security.SecurityHelper
import com.techglock.health.app.security.adapter.AdapterLanguageDialog
import java.util.Objects

fun Context.showLanguageDialog(
    handleClick: (positiveClick: Boolean, languageModel: String?) -> Unit = { type, dialogBinding -> }
): Dialog {

    var isKeyboardShowing = false
    val customDialog = Dialog(this)
    val dialogBinding = DataBindingUtil.inflate<DialogLanguageBinding>(
        LayoutInflater.from(this), R.layout.dialog_language, null, false
    )
    customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    customDialog.setContentView(dialogBinding.root)
    Objects.requireNonNull<Window>(customDialog.window).setBackgroundDrawable(
        ColorDrawable(
            Color.TRANSPARENT
        )
    )
    customDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    customDialog.setCancelable(false)


    val lpr = WindowManager.LayoutParams()
    lpr.copyFrom(customDialog.window!!.attributes)
    lpr.width = WindowManager.LayoutParams.MATCH_PARENT
    lpr.height = WindowManager.LayoutParams.MATCH_PARENT
//    lpr.windowAnimations = R.style.bottomStyle
    lpr.gravity = Gravity.CENTER
    customDialog.window!!.attributes = lpr

    val languageList = SecurityHelper.getLanguageList(this)

    val languageDialogAdapter = AdapterLanguageDialog(languageList)
    dialogBinding.rvLanguageList.adapter = languageDialogAdapter
    languageDialogAdapter.setOnItemClickListener(object : BaseAdapter.OnItemClickListener {
        @SuppressLint("NotifyDataSetChanged")
        override fun onItemClick(vararg itemData: Any) {
            val pos = itemData[0] as Int
            for (i in languageList.indices) {
                languageList[i].selectionStatus = false
            }
            languageList[pos].selectionStatus = true
            languageDialogAdapter.notifyDataSetChanged()
        }

    })



    dialogBinding.btnProceed.setOnClickListener {
        for (i in languageList.indices) {
            if (languageList[i].selectionStatus) {
                handleClick(true, languageList[i].languageCode)
                break
            }
        }
        customDialog.dismiss()

    }

    dialogBinding.imgCloseDialog.setOnClickListener {
        customDialog.dismiss()
    }

    customDialog.show()
    return customDialog
}


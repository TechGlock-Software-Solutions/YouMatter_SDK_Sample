package com.techglock.health.app.security.ui_dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.DialogSecurityAppUpdateBinding
import com.techglock.health.app.model.entity.AppVersion

class DialogUpdateAppSecurity(
    context: Context,
    versionDetails: AppVersion,
    mListener: OnSkipUpdateListener
) : Dialog(context) {

    private lateinit var binding: DialogSecurityAppUpdateBinding

    private var appVersion: AppVersion? = null
    private var listener: OnSkipUpdateListener = mListener

    init {
        this.appVersion = versionDetails
        this.listener = mListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_security_app_update, null, false)
        binding = DialogSecurityAppUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.currentFocus
        setCancelable(false)
        setCanceledOnTouchOutside(false)

        initialise()
        setClickable()
    }

    private fun initialise() {

        val forceUpdate = appVersion!!.forceUpdate
        //val forceUpdate = false

        if (forceUpdate) {
            binding.btnLeftUpdate.visibility = View.GONE
        } else {
            binding.btnLeftUpdate.visibility = View.VISIBLE
        }

        binding.txtUpdateDescription.text = appVersion!!.description

        //app:loadAppImgUrl="@{appVersion.imagePath}"
    }

    private fun setClickable() {

        binding.btnLeftUpdate.setOnClickListener {
            dismiss()
            listener.onSkipUpdate()
        }

        binding.btnRightUpdate.setOnClickListener {
            Utilities.goToPlayStore(context)
        }

    }

    interface OnSkipUpdateListener {
        fun onSkipUpdate()
    }

}
package com.techglock.health.app.hra.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import com.techglock.health.app.databinding.DialogHraNotificationBinding

class DialogNotificationHRA(
    context: Context,
    private val msg: String,
    private val mListener: OnRestartHRA
) : Dialog(context) {

    private lateinit var binding: DialogHraNotificationBinding

    private var listener: OnRestartHRA = mListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogHraNotificationBinding.inflate(layoutInflater)
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
        binding.txtHraDescription.text = msg
    }

    private fun setClickable() {

        binding.btnSkip.setOnClickListener {
            dismiss()
        }

        binding.btnRestart.setOnClickListener {
            dismiss()
            listener.onRestartHRAClick(true)
        }

    }

    interface OnRestartHRA {
        fun onRestartHRAClick(restartHra: Boolean)
    }

}
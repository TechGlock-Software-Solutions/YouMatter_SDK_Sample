package com.techglock.health.app.tools_calculators.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import com.techglock.health.app.databinding.DialogToolsNotificationBinding
import com.techglock.health.app.tools_calculators.common.DataHandler
import com.techglock.health.app.tools_calculators.model.TrackerDashboardModel

class DialogNotificationTools(
    context: Context,
    private val screen: String,
    private val mListener: OnRestartHRA
) : Dialog(context) {

    private lateinit var binding: DialogToolsNotificationBinding

    private var listener: OnRestartHRA = mListener
    private var tracker = TrackerDashboardModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_tools_notification, null, false)
        binding = DialogToolsNotificationBinding.inflate(layoutInflater)

        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.currentFocus
        setCancelable(false)
        setCanceledOnTouchOutside(false)

        initialise()
        setClickable()
    }

    private fun initialise() {
        tracker = DataHandler(context).getCategoryImageByCode(screen)
        binding.imgTracker.setImageResource(tracker.imageId)
        binding.txtToolsTitle.text = tracker.name
        binding.txtToolsDescription.text = tracker.description
    }

    private fun setClickable() {

        binding.btnNotNow.setOnClickListener {
            dismiss()
        }

        binding.btnStartQuiz.setOnClickListener {
            listener.onRestartHRAClick(screen, tracker)
            dismiss()
        }

    }

    interface OnRestartHRA {
        fun onRestartHRAClick(screen: String, tracker: TrackerDashboardModel)
    }

}
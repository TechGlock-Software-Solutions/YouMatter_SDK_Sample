package com.techglock.health.app.common.base

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.techglock.health.app.common.utils.Event
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


abstract class BaseBottomSheetFragment : BottomSheetDialogFragment() {

    private val progressDialog by lazy {
        ProgressDialog(context)
    }

    abstract fun getViewModel(): BaseViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpProgressBar(this, getViewModel().progressBar)
    }

    private fun setUpProgressBar(
        lifecycleOwner: LifecycleOwner, progressBar: LiveData<Event<String>>
    ) {
        progressBar.observe(lifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                if (it.equals(Event.HIDE_PROGRESS, true)) showProgress(false)
                else showProgress(true, it)
            }
        }
    }

    private fun showProgress(showProgress: Boolean, message: String = "Loading...") {
        try {
            if (showProgress) {
                progressDialog.setMessage(message)
                progressDialog.isIndeterminate = false
                progressDialog.setCancelable(true)
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()
            } else {
                if (progressDialog.isShowing) {
                    progressDialog.cancel()
                    progressDialog.dismiss()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
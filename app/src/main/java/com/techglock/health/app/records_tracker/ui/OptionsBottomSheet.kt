package com.techglock.health.app.records_tracker.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.techglock.health.app.R
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.databinding.BottomSheetRecordsOptionsBinding
import com.techglock.health.app.model.entity.HealthDocument
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File


class OptionsBottomSheet(var listener: OnOptionClickListener, var record: HealthDocument) :
    BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetRecordsOptionsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetRecordsOptionsBinding.inflate(inflater, container, false)
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {
        val file = File(record.Path, record.Name!!)
        if (file.exists()) {
            binding.layoutDownload.visibility = View.GONE
        }

    }

    private fun setClickable() {

        binding.layoutDownload.setOnClickListener {
            dismiss()
            listener.onOptionClick(Constants.DOWNLOAD, record)
        }

        binding.layoutShare.setOnClickListener {
            dismiss()
            listener.onOptionClick(Constants.SHARE, record)
        }

        binding.layoutDelete.setOnClickListener {
            dismiss()
            listener.onOptionClick(Constants.DELETE, record)
        }

    }

    override fun getTheme(): Int {
        //return super.getTheme();
        return R.style.BottomSheetDialog
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //return super.onCreateDialog(savedInstanceState);
        return BottomSheetDialog(requireContext(), theme)
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }


    interface OnOptionClickListener {
        fun onOptionClick(action: String, record: HealthDocument)
    }

}
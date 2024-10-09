package com.techglock.health.app.medication_tracker.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.techglock.health.app.R
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.BottomSheetOptionsBinding
import com.techglock.health.app.model.entity.MedicationEntity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class OptionsBottomSheet(
    var listener: OnOptionClickListener,
    var position: Int,
    var medicine: MedicationEntity.Medication
) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetOptionsBinding

    private var isOngoing = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //dialog!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        binding = BottomSheetOptionsBinding.inflate(inflater, container, false)
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {
        if (!Utilities.isNullOrEmpty(medicine.EndDate)) {
            val dateDiff = DateHelper.getDateDifference(
                medicine.EndDate!!,
                DateHelper.currentDateAsStringyyyyMMdd
            )
            isOngoing = dateDiff <= 0
        } else {
            isOngoing = true
        }

        if (!isOngoing) {
            binding.txtEdit.visibility = View.GONE
            binding.view.visibility = View.GONE
        }

    }

    private fun setClickable() {

        binding.txtEdit.setOnClickListener {
            dismiss()
            listener.onOptionClick(Constants.EDIT, position, medicine)
        }
        binding.txtDelete.setOnClickListener {
            dismiss()
            listener.onOptionClick(Constants.DELETE, position, medicine)
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
        fun onOptionClick(code: String, position: Int, medicine: MedicationEntity.Medication)
    }

}
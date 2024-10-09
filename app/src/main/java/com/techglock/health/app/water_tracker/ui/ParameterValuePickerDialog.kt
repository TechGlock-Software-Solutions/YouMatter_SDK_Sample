package com.techglock.health.app.water_tracker.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.ParameterDataModel
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.DialogParameterValuePickerBinding


class ParameterValuePickerDialog(
    private val mContext: Context,
    private val listener: OnPickerDialogValueListener,
    private val dialogType: String,
    private val parameterDataModel: ParameterDataModel
) : Dialog(mContext) {

    private lateinit var binding: DialogParameterValuePickerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogParameterValuePickerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        //window!!.setGravity(Gravity.CENTER_VERTICAL)
        try {
            init()
            setClickable()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        binding.txtPickerTitle.text = mContext.resources.getString(R.string.ENTER_YOUR_WATER_INTAKE)
        binding.edtParameter.hint =
            "${parameterDataModel.minRange.toInt()} - ${parameterDataModel.maxRange.toInt()}"
        binding.txtPickerUnit.text = parameterDataModel.unit
    }

    private fun setClickable() {

        binding.btnDonePicker.setOnClickListener {
            onDoneClick()
        }

        binding.imgClose.setOnClickListener {
            dismiss()
        }

    }

    private fun onDoneClick() {
        try {
            val value = binding.edtParameter.text.toString()
            if (Utilities.isNullOrEmptyOrZero(value)) {
                Utilities.toastMessageShort(
                    mContext,
                    mContext.resources.getString(R.string.PLEASE_ENTER_WATER_INTAKE_QUANTITY)
                )
            } else if (value.toInt() < parameterDataModel.minRange.toInt() || value.toInt() > parameterDataModel.maxRange.toInt()) {
                Utilities.toastMessageShort(
                    mContext,
                    "${mContext.resources.getString(R.string.PLEASE_ENTER_VALID_WATER_INTAKE_QUANTITY_BETWEEN)} ${parameterDataModel.minRange.toInt()} - ${parameterDataModel.maxRange.toInt()} ${
                        mContext.resources.getString(R.string.ML)
                    }"
                )
            } else {
                listener.onPickerDialogValueSelection(value)
                dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    interface OnPickerDialogValueListener {
        fun onPickerDialogValueSelection(quantity: String)
    }

}
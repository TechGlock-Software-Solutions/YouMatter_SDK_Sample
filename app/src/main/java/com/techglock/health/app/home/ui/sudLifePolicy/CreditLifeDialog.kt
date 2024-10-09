package com.techglock.health.app.home.ui.sudLifePolicy

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import com.techglock.health.app.R
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.common.view.SpinnerAdapter
import com.techglock.health.app.common.view.SpinnerModel
import com.techglock.health.app.databinding.DialogCreditLifeBinding
import com.techglock.health.app.home.common.DataHandler

class CreditLifeDialog(
    private val mContext: Context,
    private val listener: OnCreditLifeDialogListener
) : Dialog(mContext) {

    private lateinit var binding: DialogCreditLifeBinding

    private var selectedTab = 0
    private var code = Constants.CL_CODE_LOAN_ACCOUNT_NO
    private var spinnerAdapter: SpinnerAdapter? = null
    private var categoryList: ArrayList<SpinnerModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogCreditLifeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.currentFocus
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        try {
            init()
            setClickable()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        categoryList = DataHandler(mContext).getCreditLifeParametersList()
        spinnerAdapter = SpinnerAdapter(mContext, categoryList!!)
        binding.spinnerParameter.adapter = spinnerAdapter
    }

    private fun setClickable() {

        binding.spinnerParameter.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    spinnerAdapter!!.selectedPos = position
                    val name: String = categoryList!![position].name
                    binding.txtModelSpinner.text = name
                    selectedTab = categoryList!![position].position
                    code = categoryList!![position].code
                    Utilities.printLogError("Selected Item--->$selectedTab , $code")
                    binding.edtParameter.hint = name
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        binding.tabParameter.setOnClickListener {
            binding.spinnerParameter.performClick()
        }

        binding.btnDonePicker.setOnClickListener {
            onProceedClick()
        }

        binding.imgClose.setOnClickListener {
            dismiss()
        }
    }

    private fun onProceedClick() {
        val value = binding.edtParameter.text.toString()
        if (Utilities.isNullOrEmptyOrZero(value)) {
            Utilities.toastMessageShort(
                mContext,
                mContext.resources.getString(R.string.PLEASE_ENTER_VALUE)
            )
        } else {
            listener.onCreditLifeDialogSelection(code, value)
            dismiss()
        }
    }

    interface OnCreditLifeDialogListener {
        fun onCreditLifeDialogSelection(parameter: String, value: String)
    }

}
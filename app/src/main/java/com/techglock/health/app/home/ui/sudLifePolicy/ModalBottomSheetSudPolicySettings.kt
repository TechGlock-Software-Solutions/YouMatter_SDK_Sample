package com.techglock.health.app.home.ui.sudLifePolicy

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.techglock.health.app.R
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.databinding.ModalBottomSheetSudPolicySettingsBinding
import com.techglock.health.app.home.viewmodel.SudLifePolicyViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ModalBottomSheetSudPolicySettings(
    var listener: OnSettingsOptionClickListener,
    val viewModel: SudLifePolicyViewModel
) : BottomSheetDialogFragment() {

    private lateinit var binding: ModalBottomSheetSudPolicySettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //dialog!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        binding = ModalBottomSheetSudPolicySettingsBinding.inflate(inflater, container, false)
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {
        binding.txtPolicyMobileNumber.text = viewModel.policyMobileNumber
    }

    private fun setClickable() {
        binding.layoutChangeMobile.setOnClickListener {
            dismiss()
            listener.onSettingsOptionClick(Constants.CHANGE_POLICY_MOBILE_NUMBER)
        }
        binding.layoutCallCustomerCare.setOnClickListener {
            dismiss()
            listener.onSettingsOptionClick(Constants.CALL_CUSTOMER_CARE)
        }
        binding.layoutWhatsAppBot.setOnClickListener {
            dismiss()
            listener.onSettingsOptionClick(Constants.WHATS_APP_BOT)
        }
        binding.imgClose.setOnClickListener {
            dismiss()
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

    interface OnSettingsOptionClickListener {
        fun onSettingsOptionClick(code: String)
    }

}
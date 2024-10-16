package com.techglock.health.app.home.ui.sudLifePolicy

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.techglock.health.app.R
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.databinding.ModalBottomSheetSudPolicyBinding
import com.techglock.health.app.home.common.PolicyDataSingleton
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ModalBottomSheetSudPolicy(var listener: OnItemClickListener) : BottomSheetDialogFragment() {

    private lateinit var binding: ModalBottomSheetSudPolicyBinding
    private var policyDataSingleton: PolicyDataSingleton? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //dialog!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        binding = ModalBottomSheetSudPolicyBinding.inflate(inflater, container, false)
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {
        policyDataSingleton = PolicyDataSingleton.getInstance()!!
        binding.txtPolicyName.text = policyDataSingleton!!.kypDetails.contractTypeDesc
        binding.txtPolicyNumber.text = policyDataSingleton!!.kypDetails.policyNumber
    }

    private fun setClickable() {
        binding.layoutPremiumReceipt.setOnClickListener {
            dismiss()
            listener.onClick(Constants.PREMIUM_RECEIPT)
        }
        binding.layoutPolicyDocument.setOnClickListener {
            dismiss()
            listener.onClick(Constants.POLICY_DOCUMENT)
        }
        binding.layoutKnowYourPolicy.setOnClickListener {
            dismiss()
            listener.onClick(Constants.KNOW_YOUR_POLICY)
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

    interface OnItemClickListener {
        fun onClick(code: String)
    }
}
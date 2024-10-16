package com.techglock.health.app.home.ui.sudLifePolicy

import android.app.Dialog
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.techglock.health.app.R
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ModalBottomSheetSudPolicyAuthenticationBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ModalBottomSheetSudPolicyAuthentication(
    val mobile: String, var listener: OnVerifyClickListener,
    val activity: FragmentSudPolicyAuthentication
) : BottomSheetDialogFragment() {

    lateinit var binding: ModalBottomSheetSudPolicyAuthenticationBinding
    lateinit var otpTimer: CountDownTimer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //dialog!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        binding = ModalBottomSheetSudPolicyAuthenticationBinding.inflate(inflater, container, false)
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {
        if (activity.isVisible) {
            refreshTimer()
        }
    }

    private fun setClickable() {
        binding.layoutCodeView.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                val verificationCode = editable.toString()
                if (verificationCode.length == 6) {
                    binding.layoutCodeView.setLineColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.state_success
                        )
                    )
                    Utilities.printLog("VerificationCode--->$verificationCode")
                } else {
                    binding.layoutCodeView.setLineColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.vivant_charcoal_grey
                        )
                    )
                }
            }
        })

        binding.btnVerify.setOnClickListener {
            if (Utilities.isNullOrEmpty(binding.layoutCodeView.text.toString()) || binding.layoutCodeView.text.toString().length != 6) {
                Utilities.toastMessageShort(
                    context,
                    resources.getString(R.string.ERROR_VALIDATE_OTP)
                )
            } else {
                listener.onVerifyClick(binding.layoutCodeView.text.toString())
            }
        }
        binding.txtResend.setOnClickListener {
            activity.viewModel.callGenerateVerificationCode(mobile)
        }
        binding.imgClose.setOnClickListener {
            listener.onBottomSheetClosed()
            dismiss()
        }
    }

    fun refreshTimer() {
        binding.imgClose.visibility = View.INVISIBLE
        binding.layoutTimer.visibility = View.VISIBLE
        binding.progressOtp.maxValue = Constants.OTP_COUNT_DOWN_TIME.toFloat()
        binding.progressOtp.setValue(Constants.OTP_COUNT_DOWN_TIME.toFloat())
        binding.txtSecondsLeft.text = Constants.OTP_COUNT_DOWN_TIME.toFloat().toString()
        binding.txtResend.isEnabled = false
        binding.txtResend.setTextColor(
            ContextCompat.getColor(
                activity.requireContext(),
                R.color.vivantInactive
            )
        )

        //otpTimer.cancel()
        val time = ((Constants.OTP_COUNT_DOWN_TIME * 1000) + 1000).toLong() // i.e. 31000
        otpTimer = object : CountDownTimer(time, 1000) {
            var total = 0
            override fun onTick(millisUntilFinished: Long) {
                total = (millisUntilFinished / 1000).toInt()
                binding.progressOtp.setValue(total.toFloat())
                binding.txtSecondsLeft.text = total.toString()
            }

            override fun onFinish() {
                cancel()
                binding.progressOtp.setValue(0f)
                binding.txtSecondsLeft.text = 0.toString()
                binding.txtResend.isEnabled = true
                binding.txtResend.setTextColor(
                    ContextCompat.getColor(
                        activity.requireContext(),
                        R.color.colorPrimary
                    )
                )
                binding.layoutTimer.visibility = View.INVISIBLE
                binding.imgClose.visibility = View.VISIBLE
            }
        }.start()
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

    interface OnVerifyClickListener {
        fun onVerifyClick(code: String)
        fun onBottomSheetClosed()
    }

}
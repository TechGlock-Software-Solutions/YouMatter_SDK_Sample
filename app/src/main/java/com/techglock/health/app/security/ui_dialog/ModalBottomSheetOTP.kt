package com.techglock.health.app.security.ui_dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.techglock.health.app.R
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.ModalBottomSheetOtpBinding
import com.techglock.health.app.security.ui.LoginFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ModalBottomSheetOTP(
    private val loginType: String, private val email: String, private val mobile: String,
    private var listener: OnVerifyClickListener, private val loginFragment: LoginFragment
) : BottomSheetDialogFragment() {

    lateinit var binding: ModalBottomSheetOtpBinding
    lateinit var otpTimer: CountDownTimer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //dialog!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        binding = ModalBottomSheetOtpBinding.inflate(inflater, container, false)
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {
        when (loginType) {
            Constants.EMAIL -> {
                binding.txtOtpVerificationDesc.text =
                    Html.fromHtml("<a>${resources.getString(R.string.PLEASE_ENTER_THE_OTP_SENT_TO)} <B><font color='#000000'>$email</font></B> </a>")
            }

            Constants.PHONE -> {
                binding.txtOtpVerificationDesc.text =
                    Html.fromHtml("<a>${resources.getString(R.string.PLEASE_ENTER_THE_OTP_SENT_TO)} <B><font color='#000000'>$mobile</font></B> </a>")
            }
        }
        refreshTimer()
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
                    binding.btnVerify.context,
                    resources.getString(R.string.ERROR_VALIDATE_OTP)
                )
            } else {
                listener.onVerifyClick(binding.layoutCodeView.text.toString())
            }
        }
        binding.txtResend.setOnClickListener {
            loginFragment.viewModel.callGenerateVerificationCode("", mobile, loginFragment)
        }
        binding.imgClose.setOnClickListener {
            listener.onBottomSheetClosed()
            dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    fun refreshTimer() {
        val context = binding.btnVerify.context
        binding.imgClose.visibility = View.INVISIBLE
        binding.txtResend.text =
            "${resources.getString(R.string.RESEND_OTP_IN)} ${Constants.OTP_COUNT_DOWN_TIME} ${
                resources.getString(R.string.SEC)
            }"
        binding.txtResend.isEnabled = false
        binding.lblDidNotReceiveOtp.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.vivantInactive
            )
        )
        binding.txtResend.setTextColor(ContextCompat.getColor(context, R.color.almost_black))

        //otpTimer.cancel()
        val time = ((Constants.OTP_COUNT_DOWN_TIME * 1000) + 1000).toLong() // i.e. 31000
        otpTimer = object : CountDownTimer(time, 1000) {
            var total = 0
            override fun onTick(millisUntilFinished: Long) {
                total = (millisUntilFinished / 1000).toInt()
                binding.txtResend.text =
                    "${resources.getString(R.string.RESEND_OTP_IN)} $total ${resources.getString(R.string.SEC)}"
            }

            override fun onFinish() {
                cancel()
                binding.txtResend.isEnabled = true
                binding.lblDidNotReceiveOtp.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.dark_gray
                    )
                )
                binding.txtResend.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorPrimary
                    )
                )
                binding.txtResend.text = resources.getString(R.string.RESEND_OTP)
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
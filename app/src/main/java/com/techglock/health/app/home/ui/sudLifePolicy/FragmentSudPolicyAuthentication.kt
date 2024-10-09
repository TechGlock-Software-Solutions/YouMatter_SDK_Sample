package com.techglock.health.app.home.ui.sudLifePolicy

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.extension.replaceFragment
import com.techglock.health.app.common.receiver.SmsBroadcastReceiver
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.common.utils.Validation
import com.techglock.health.app.databinding.ActivitySudPolicyAuthenticationBinding
import com.techglock.health.app.home.common.PolicyDataSingleton
import com.techglock.health.app.home.ui.HomeMainActivity
import com.techglock.health.app.home.viewmodel.SudLifePolicyViewModel
import com.techglock.health.app.repository.utils.Resource
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetrieverClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.regex.Matcher
import java.util.regex.Pattern

@AndroidEntryPoint
class FragmentSudPolicyAuthentication : BaseFragment() {
    private lateinit var binding: ActivitySudPolicyAuthenticationBinding
    val viewModel: SudLifePolicyViewModel by lazy {
        ViewModelProvider(this)[SudLifePolicyViewModel::class.java]
    }

    private val appColorHelper = AppColorHelper.instance!!
    private var smsBroadcastReceiver: SmsBroadcastReceiver? = null
    private var modalBottomSheet: ModalBottomSheetSudPolicyAuthentication? = null
    private var policyDataSingleton: PolicyDataSingleton? = null
    var isBottomSheetOpen = false

    override fun getViewModel(): BaseViewModel = viewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = ActivitySudPolicyAuthenticationBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            (activity as HomeMainActivity).setToolbarInfo(5, showAppLogo = true, title = "")
            initialise()
            registerObserver()
            setClickable()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initialise() {
        policyDataSingleton = PolicyDataSingleton.getInstance()!!
        if (!Utilities.isNullOrEmpty(viewModel.policyMobileNumber)) {
            binding.edtMobile.setText("")
        } else {
            binding.edtMobile.setText(viewModel.phone)
        }
    }

    private fun setClickable() {
        binding.btnContinue.setOnClickListener {
            val mobile = binding.edtMobile.text.toString()
            if (Utilities.isNullOrEmpty(mobile) || !Validation.isValidPhoneNumber(mobile)) {
                Utilities.toastMessageShort(
                    requireContext(),
                    resources.getString(R.string.VALIDATE_PHONE)
                )
            } else {
                viewModel.callGenerateVerificationCode(mobile)
                //refreshPolicyData()
            }
        }
        binding.txtResendOtp.setOnClickListener {

        }
    }

    private fun registerObserver() {
        viewModel.otpGenerateData.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {
                if (it.data!!.status.equals(Constants.SUCCESS, ignoreCase = true)) {
                    Utilities.printLogError("IsBottomSheetOpen--->$isBottomSheetOpen")
                    if (!isBottomSheetOpen) {
                        showBottomSheet()
                    } else {
                        if (this.isVisible) {
                            modalBottomSheet!!.refreshTimer()
                        }
                    }
                    startSmsUserConsent()
                }
            }
        }
        viewModel.otpValidateData.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {


                if (it.data!!.validity.equals(Constants.TRUE, true)) {
                    modalBottomSheet!!.otpTimer.cancel()
                    modalBottomSheet!!.dismiss()
                    refreshPolicyData()
                } else {
                    Utilities.toastMessageShort(
                        requireContext(),
                        resources.getString(R.string.ERROR_INVALID_OTP)
                    )
                }
            }
        }
    }

    private fun showBottomSheet() {
        modalBottomSheet = ModalBottomSheetSudPolicyAuthentication(
            viewModel.phone,
            object : ModalBottomSheetSudPolicyAuthentication.OnVerifyClickListener {
                override fun onVerifyClick(code: String) {
                    if (Constants.IS_BYPASS_OTP) {
                        if (code == "123456") {
                            modalBottomSheet!!.otpTimer.cancel()
                            modalBottomSheet!!.dismiss()
                            refreshPolicyData()
                        } else {
                            viewModel.callValidateVerificationCode(code)
                        }
                    } else {
                        viewModel.callValidateVerificationCode(code)
                    }
                }

                override fun onBottomSheetClosed() {
                    isBottomSheetOpen = false
                }
            },
            this
        )
        modalBottomSheet!!.isCancelable = false
        modalBottomSheet!!.show(childFragmentManager, ModalBottomSheetSudPolicyAuthentication.TAG)
        isBottomSheetOpen = true
    }

    private fun refreshPolicyData() {
        viewModel.hideProgress()

        lifecycleScope.launch(Dispatchers.Main) {
            delay(600)
            policyDataSingleton!!.clearData()
            viewModel.setSudPolicyMobileNumber(binding.edtMobile.text.toString())
            viewModel.setIsOtpAuthenticatedStatus(true)
            (activity as HomeMainActivity).replaceFragment(SudLifePolicyDashboardFragment())
//            openAnotherActivity(destination = NavigationConstants.SUD_LIFE_POLICY, clearTop = true)
        }

    }

    private fun startSmsUserConsent() {
        val client: SmsRetrieverClient = SmsRetriever.getClient(requireActivity())
        //We can add sender phone number or leave it blank
        //We are adding null here
        client.startSmsUserConsent(null).addOnSuccessListener {
            Utilities.printLogError("OTP Auto Read--->On Success")
        }.addOnFailureListener {
            Utilities.printLogError("OTP Auto Read--->On OnFailure")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQ_CODE_SMS_CONSENT) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                try {
                    //That gives all message to us.
                    // We need to get the code from inside with regex
                    val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                    Utilities.printLogError("Received_message--->$message")
                    getOtpFromMessage(message!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun getOtpFromMessage(message: String) {
        // This will match any 6 digit number in the message
        val pattern: Pattern = Pattern.compile("(|^)\\d{6}")
        val matcher: Matcher = pattern.matcher(message)
        if (matcher.find()) {
            modalBottomSheet!!.binding.layoutCodeView.setText(matcher.group(0))
            modalBottomSheet!!.binding.btnVerify.performClick()
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerBroadcastReceiver() {
        smsBroadcastReceiver = SmsBroadcastReceiver()
        smsBroadcastReceiver!!.smsBroadcastReceiverListener =
            object : SmsBroadcastReceiver.SmsBroadcastReceiverListener {
                override fun onSuccess(intent: Intent?) {
                    startActivityForResult(intent!!, Constants.REQ_CODE_SMS_CONSENT)
                }

                override fun onFailure() {}
            }
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        //(requireActivity() as HomeMainActivity).registerReceiver(smsBroadcastReceiver, intentFilter)
        ContextCompat.registerReceiver(
            requireContext(),
            smsBroadcastReceiver,
            intentFilter,
            ContextCompat.RECEIVER_EXPORTED
        )
    }

    override fun onStart() {
        super.onStart()
        registerBroadcastReceiver()
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as HomeMainActivity).unregisterReceiver(smsBroadcastReceiver)
    }


    /* override fun onBackPressed() {
         super.onBackPressed()
         routeToHomeScreen()

     }

     fun routeToHomeScreen() {
         openAnotherActivity(destination = NavigationConstants.HOME, clearTop = true)

     }*/


}
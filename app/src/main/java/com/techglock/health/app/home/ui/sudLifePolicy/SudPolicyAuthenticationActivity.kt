package com.techglock.health.app.home.ui.sudLifePolicy

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseActivity
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.receiver.SmsBroadcastReceiver
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.common.utils.Validation
import com.techglock.health.app.databinding.ActivitySudPolicyAuthenticationBinding
import com.techglock.health.app.home.common.PolicyDataSingleton
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
class SudPolicyAuthenticationActivity : BaseActivity() {

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
    private val onBackPressedCallBack = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivitySudPolicyAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallBack)
        try {
            setupToolbar()
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
                Utilities.toastMessageShort(this, resources.getString(R.string.VALIDATE_PHONE))
            } else {
                viewModel.callGenerateVerificationCode(mobile)
                //refreshPolicyData()
            }
        }
        binding.txtResendOtp.setOnClickListener {

        }
    }

    private fun registerObserver() {
        viewModel.otpGenerateData.observe(this) {
            if (it.status == Resource.Status.SUCCESS) {
                if (it.data!!.status.equals(Constants.SUCCESS, ignoreCase = true)) {
                    Utilities.printLogError("IsBottomSheetOpen--->$isBottomSheetOpen")
                    if (!isBottomSheetOpen) {
                        showBottomSheet()
                    } else {
                        modalBottomSheet!!.refreshTimer()
                    }
                    startSmsUserConsent()
                }
            }
        }
        viewModel.otpValidateData.observe(this) {
            if (it.status == Resource.Status.SUCCESS) {


                if (it.data!!.validity.equals(Constants.TRUE, true)) {
                    modalBottomSheet!!.otpTimer.cancel()
                    modalBottomSheet!!.dismiss()
                    refreshPolicyData()
                } else {
                    Utilities.toastMessageShort(
                        this,
                        resources.getString(R.string.ERROR_INVALID_OTP)
                    )
                }
            }
        }
    }

    private fun showBottomSheet() {
        /* modalBottomSheet = ModalBottomSheetSudPolicyAuthentication(
             viewModel.phone,
             object : ModalBottomSheetSudPolicyAuthentication.OnVerifyClickListener {
                 override fun onVerifyClick(code: String) {
                     if (Constants.IS_BYPASS_OTP) {
                         if (code == Constants.BYPASS_OTP) {
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
             this@SudPolicyAuthenticationActivity
         )
         modalBottomSheet!!.isCancelable = false
         modalBottomSheet!!.show(supportFragmentManager, ModalBottomSheetSudPolicyAuthentication.TAG)
         isBottomSheetOpen = true*/
    }

    private fun refreshPolicyData() {
        viewModel.hideProgress()

        lifecycleScope.launch(Dispatchers.Main) {
            delay(600)
            policyDataSingleton!!.clearData()
            viewModel.setSudPolicyMobileNumber(binding.edtMobile.text.toString())
            viewModel.setIsOtpAuthenticatedStatus(true)
            val intent =
                Intent(this@SudPolicyAuthenticationActivity, SudLifePolicyActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

    }

    private fun startSmsUserConsent() {
        val client: SmsRetrieverClient = SmsRetriever.getClient(this)
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
        //registerReceiver(smsBroadcastReceiver, intentFilter)
        ContextCompat.registerReceiver(
            this,
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
        unregisterReceiver(smsBroadcastReceiver)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolBarView.toolbarCommon)
        binding.toolBarView.toolbarTitle.text = ""
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        binding.toolBarView.toolbarCommon.navigationIcon?.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                appColorHelper.textColor, BlendModeCompat.SRC_ATOP
            )

        binding.toolBarView.toolbarCommon.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }


}
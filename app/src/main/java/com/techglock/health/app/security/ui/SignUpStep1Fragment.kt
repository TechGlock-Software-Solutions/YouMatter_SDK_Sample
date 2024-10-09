package com.techglock.health.app.security.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.NavigationConstants
import com.techglock.health.app.common.extension.openAnotherActivity
import com.techglock.health.app.common.receiver.SmsBroadcastReceiver
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.common.utils.Validation
import com.techglock.health.app.databinding.FragmentSignUpStep1Binding
import com.techglock.health.app.security.ui_dialog.ModalBottomSheetOTPSignup
import com.techglock.health.app.security.viewmodel.RegistrationViewModel
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetrieverClient
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Matcher
import java.util.regex.Pattern

@AndroidEntryPoint
class SignUpStep1Fragment : BaseFragment() {

    val viewModel: RegistrationViewModel by lazy {
        ViewModelProvider(this)[RegistrationViewModel::class.java]
    }
    private lateinit var binding: FragmentSignUpStep1Binding

    private var loginType = ""
    var email = ""
    var phone = ""
    var firstName = ""
    //var lastName = ""

    var isBottomSheetOpen = false
    var modalBottomSheetOTPSignup: ModalBottomSheetOTPSignup? = null
    private var smsBroadcastReceiver: SmsBroadcastReceiver? = null
    private var phoneNumberHintIntentResultLauncher: ActivityResultLauncher<IntentSenderRequest>? =
        null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Callback to Handle back button event
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //findNavController().navigateUp()
                performBackClick()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpStep1Binding.inflate(inflater, container, false)


        (activity as SecurityActivity).updateStatusBarColor(R.drawable.gradient_step1, false)


        try {
            requireArguments().let {
                loginType = it.getString(Constants.LOGIN_TYPE, "")!!
                firstName = it.getString(Constants.NAME, "")!!
                email = it.getString(Constants.EMAIL, "")!!
                phone = it.getString(Constants.PRIMARY_PHONE, "")!!
            }
            Utilities.printLogError("LoginType----->$loginType")
            Utilities.printLogError("Username----->$firstName")
            Utilities.printLogError("Email----->$email")
            Utilities.printLogError("Phone----->$phone")
            isBottomSheetOpen = false
            initialise()
            setClickable()
            registerObserver()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    private fun initialise() {
        /*        if(LocaleHelper.getLanguage(requireContext()) == "hi") {
                    binding.txtTermsConditions1.text = Html.fromHtml( "<a><B><font color='#81A684'>" + resources.getString(R.string.TERMS_AND_CONDITIONS) + "</font></B></a> " + resources.getString(R.string.I_HAVE_READ_AGREE))
                } else {
                    binding.txtTermsConditions1.text = Html.fromHtml("${resources.getString(R.string.I_HAVE_READ_AGREE)} " + "<a><B><font color='#81A684'>" + resources.getString(R.string.TERMS_AND_CONDITIONS) + "</font></B></a>" )
                }*/
        binding.txtTermsConditions.text = Html.fromHtml(
            "${resources.getString(R.string.I_HAVE_READ_AGREE)} " + " <a><B><font color='#81A684'>" + resources.getString(
                R.string.TERMS_AND_CONDITIONS
            ) + "</font></B></a>"
        )
        binding.txtPrivacyPolicy.text = Html.fromHtml(
            "${resources.getString(R.string.AND)} " + " <a><B><font color='#81A684'>" + resources.getString(
                R.string.PRIVACY_POLICY
            ) + "</font></B></a>"
        )

        when (loginType) {
            Constants.EMAIL -> {
                if (!Utilities.isNullOrEmpty(firstName)) {
                    binding.edtSignupFirstName.setText(firstName)
                }
                binding.edtSignupEmail.setText(email)
                binding.edtSignupEmail.isEnabled = false
                binding.tilEdtSignupEmail.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.light_gray)
            }

            Constants.PHONE -> {
                binding.edtSignupPhone.setText(phone)
                binding.edtSignupPhone.isEnabled = false
                binding.tilEdtSignupPhone.boxBackgroundColor =
                    ContextCompat.getColor(requireContext(), R.color.light_gray)
            }
        }

        phoneNumberHintIntentResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            try {
                val phoneNumber =
                    Identity.getSignInClient(requireContext()).getPhoneNumberFromIntent(result.data)
                Utilities.printLogError("Selected_Phone_Number--->$phoneNumber")
                binding.edtSignupPhone.setText(phoneNumber.takeLast(10))
                binding.edtSignupPhone.setSelection(binding.edtSignupPhone.length())
            } catch (e: Exception) {
                Utilities.printLogError("Phone Number Hint failed")
            }
        }
    }

    private fun setClickable() {

        binding.btnProceed.setOnClickListener {
            if (validateFields()) {
                when (loginType) {
                    Constants.EMAIL -> {
                        viewModel.checkPhoneExistAPI(phone, this, binding.btnProceed)
                    }

                    Constants.PHONE -> {
                        viewModel.checkEmailExistOrNot(email, this, binding.btnProceed)
                    }
                }
            }
        }

        binding.txtTermsConditions.setOnClickListener {
            openAnotherActivity(destination = NavigationConstants.TERMS_POLICY_SCREEN) {
                putString(Constants.FROM, Constants.TERMS_CONDITIONS)
            }
        }

        binding.txtPrivacyPolicy.setOnClickListener {
            openAnotherActivity(destination = NavigationConstants.TERMS_POLICY_SCREEN) {
                putString(Constants.FROM, Constants.PRIVACY_POLICY)
            }
        }

        binding.edtSignupPhone.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && Utilities.isNullOrEmpty(binding.edtSignupPhone.text.toString())) {
                showPhoneNumberHint()
            }
        }

        binding.edtSignupPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                try {
                    if (editable.toString() == "") {
                        showPhoneNumberHint()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

    }

    private fun validateFields(): Boolean {
        firstName = binding.edtSignupFirstName.text.toString().trim()
        phone = binding.edtSignupPhone.text.toString().trim()
        email = binding.edtSignupEmail.text.toString().trim()

        when {
            Validation.isEmpty(firstName) || !Validation.isValidName(firstName) -> {
                Utilities.toastMessageShort(
                    requireContext(),
                    resources.getString(R.string.VALIDATE_NAME)
                )
            }

            Validation.isEmpty(email) || !Validation.isValidEmail(email) -> {
                Utilities.toastMessageShort(
                    requireContext(),
                    resources.getString(R.string.VALIDATE_EMAIL)
                )
            }

            Validation.isEmpty(phone) || !Validation.isValidPhoneNumber(phone) -> {
                Utilities.toastMessageShort(
                    requireContext(),
                    resources.getString(R.string.VALIDATE_PHONE)
                )
            }

            !binding.checkBoxTermsConditions1.isChecked -> {
                Utilities.toastMessageShort(
                    requireContext(),
                    resources.getString(R.string.PLEASE_ACCEPT_TERMS_PRIVACY_POLICY)
                )
            }

            else -> {
                return true
            }
        }
        return false
    }

    private fun registerObserver() {

        viewModel.isEmail.observe(viewLifecycleOwner) {
/*            if (it.status == Resource.Status.SUCCESS) {
                if (it.data?.isExist.equals(Constants.TRUE, true)) {
                    Utilities.toastMessageShort(requireContext(),resources.getString(R.string.ERROR_EMAIL_REGISTERED))
                } else {
                    navigateToStepTwo()
                }
            }*/
        }

        viewModel.isPhone.observe(viewLifecycleOwner) {
/*            if (it.status == Resource.Status.SUCCESS) {
                if (it.data?.isExist.equals(Constants.TRUE, true)) {
                    Utilities.toastMessageShort(requireContext(),resources.getString(R.string.ERROR_PHONE_REGISTERED))
                } else {
                    navigateToStepTwo()
                }
            }*/
        }

        viewModel.otpGenerateData.observe(viewLifecycleOwner) {}
        viewModel.otpValidateData.observe(viewLifecycleOwner) {}
    }

    fun navigateToStepTwo() {
        val bundle = Bundle()
        bundle.putString(Constants.LOGIN_TYPE, loginType)
        bundle.putString(Constants.FIRST_NAME, firstName)
        //bundle.putString(Constants.LAST_NAME,lastName)
        bundle.putString(Constants.PRIMARY_PHONE, phone)
        bundle.putString(Constants.EMAIL, email)
        findNavController().navigate(R.id.action_signUpStep1Fragment_to_signUpStep2Fragment, bundle)
    }

    fun performBackClick() {
        findNavController().navigate(R.id.action_signUpStep1Fragment_to_loginFragment)
    }

    fun showBottomSheet() {
        modalBottomSheetOTPSignup = ModalBottomSheetOTPSignup(phone, object :
            ModalBottomSheetOTPSignup.OnVerifyClickListener {
            override fun onVerifyClick(code: String) {
                if (Constants.IS_BYPASS_OTP) {
                    if (code == "123456") {
                        modalBottomSheetOTPSignup!!.otpTimer.cancel()
                        modalBottomSheetOTPSignup!!.dismiss()
                        navigateToStepTwo()
                    } else {
                        viewModel.callValidateVerificationCode(
                            code,
                            email,
                            phone,
                            this@SignUpStep1Fragment
                        )
                    }
                } else {
                    viewModel.callValidateVerificationCode(
                        code,
                        email,
                        phone,
                        this@SignUpStep1Fragment
                    )
                }
            }

            override fun onBottomSheetClosed() {
                isBottomSheetOpen = false
            }
        }, this)
        modalBottomSheetOTPSignup!!.isCancelable = false
        modalBottomSheetOTPSignup!!.show(childFragmentManager, ModalBottomSheetOTPSignup.TAG)
        isBottomSheetOpen = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Utilities.printLogError("requestCode-> $requestCode")
        Utilities.printLogError("resultCode-> $resultCode")
        Utilities.printLogError("data-> $data")
        try {
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startSmsUserConsent() {
        val client: SmsRetrieverClient = SmsRetriever.getClient(requireActivity())
        //We can add sender phone number or leave it blank
        //We are adding null here
        client.startSmsUserConsent(null).addOnSuccessListener {
            Utilities.printLogError("OTP Auto Read--->On Success")
        }.addOnFailureListener {
            Utilities.printLogError("OTP Auto Read--->On OnFailure")
        }
    }

    private fun getOtpFromMessage(message: String) {
        // This will match any 6 digit number in the message
        val pattern: Pattern = Pattern.compile("(|^)\\d{6}")
        val matcher: Matcher = pattern.matcher(message)
        if (matcher.find()) {
            modalBottomSheetOTPSignup!!.binding.layoutCodeView.setText(matcher.group(0))
            modalBottomSheetOTPSignup!!.binding.btnVerify.performClick()
        }
    }

    private fun registerBroadcastReceiver() {
        smsBroadcastReceiver = SmsBroadcastReceiver()
        smsBroadcastReceiver!!.smsBroadcastReceiverListener = object :
            SmsBroadcastReceiver.SmsBroadcastReceiverListener {
            override fun onSuccess(intent: Intent?) {
                intent?.let { startActivityForResult(it, Constants.REQ_CODE_SMS_CONSENT) }
            }

            override fun onFailure() {}
        }
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        //requireActivity().registerReceiver(smsBroadcastReceiver, intentFilter)
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
        requireActivity().unregisterReceiver(smsBroadcastReceiver)
    }

    private fun showPhoneNumberHint() {
        try {
            Identity.getSignInClient(requireContext())
                .getPhoneNumberHintIntent(GetPhoneNumberHintIntentRequest.builder().build())
                .addOnSuccessListener { result: PendingIntent ->
                    try {
                        phoneNumberHintIntentResultLauncher!!.launch(
                            IntentSenderRequest.Builder(
                                result
                            ).build()
                        )
                    } catch (e: Exception) {
                        Utilities.printLogError("Launching the PendingIntent failed")
                    }
                }
                .addOnFailureListener {
                    Utilities.printLogError("Phone Number Hint failed")
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
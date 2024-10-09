package com.techglock.health.app.security.ui

//import com.caressa.allizhealth.app.model.tempconst.Configuration.EntityID
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Configuration.EntityID
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.extension.showSnackbar
import com.techglock.health.app.common.fitness.FitnessDataManager
import com.techglock.health.app.common.receiver.SmsBroadcastReceiver
import com.techglock.health.app.common.utils.DefaultNotificationDialog
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.common.utils.Validation
import com.techglock.health.app.databinding.FragmentLoginBinding
import com.techglock.health.app.security.ui_dialog.ModalBottomSheetOTP
import com.techglock.health.app.security.viewmodel.LoginNewViewModel
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetrieverClient
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    val viewModel: LoginNewViewModel by lazy {
        ViewModelProvider(this)[LoginNewViewModel::class.java]
    }
    lateinit var binding: FragmentLoginBinding

    //private lateinit var mCallbackManager: CallbackManager
    //private var accessToken: AccessToken? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private var isGoogleLoginClicked = false
    private var mGoogleApiClient: GoogleApiClient? = null
    private var smsBroadcastReceiver: SmsBroadcastReceiver? = null
    var modalBottomSheetOTP: ModalBottomSheetOTP? = null
    private var phoneNumberHintIntentResultLauncher: ActivityResultLauncher<IntentSenderRequest>? =
        null

    var isBottomSheetOpen = false
    var isExist: Boolean = false
    var loginType = ""
    var email = ""
    var phone = ""
    var username = ""

    companion object {
        const val RC_SIGN_IN = 1000
    }

    override fun getViewModel(): BaseViewModel = viewModel

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        (activity as SecurityActivity).updateStatusBarColor(R.drawable.gradient_login, false)

        try {
            isBottomSheetOpen = false
            initialise()
            setClickable()
            registerObserver()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun initialise() {
        val versionName = Utilities.getVersionName(requireContext())
        var env = ""
        if (!Utilities.isNullOrEmpty(versionName)) {
            val versionText = "${resources.getString(R.string.VERSION)} : $versionName"
            if (Constants.environment.equals("UAT", ignoreCase = true)) {
                env = " UAT"
            }
            binding.txtVersionName.text = versionText + env
        }

        //firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth = Firebase.auth
        //accessToken = AccessToken.getCurrentAccessToken()
        disconnectFromFacebook()

        googlePlusInit1()
        googleSignOut1()
        //googlePlusInit()

        phoneNumberHintIntentResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            try {
                val phoneNumber =
                    Identity.getSignInClient(requireContext()).getPhoneNumberFromIntent(result.data)
                Utilities.printLogError("Selected_Phone_Number--->$phoneNumber")
                binding.edtLoginEmailPhone.setText(phoneNumber.takeLast(10))
            } catch (e: Exception) {
                Utilities.printLogError("Phone Number Hint failed")
            }
        }
    }

    private fun setClickable() {

        binding.btnGetOtp.setOnClickListener {
            validateLogin()
        }

        binding.btnLoginFacebook.setOnClickListener {
            EntityID = "0"
            logoutFacebook()
            facebookInit()
        }
        binding.btnLoginGoogle.setOnClickListener {
            googleLogin()
        }

        /*        binding.edtLoginEmailPhone.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus && Utilities.isNullOrEmpty(binding.edtLoginEmailPhone.text.toString()) ) {
                        showPhoneNumberHint()
                    }
                }

                binding.edtLoginEmailPhone.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
                    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
                    override fun afterTextChanged(editable: Editable) {
                        try {
                            if (editable.toString() == "") {
                                showPhoneNumberHint()
                            }
                        } catch ( e:Exception ) {
                            e.printStackTrace()
                        }
                    }
                })*/

    }

    private fun validateLogin() {
        EntityID = "0"
        val emailOrPhone = binding.edtLoginEmailPhone.text.toString().trim()
        username = ""
        if (Utilities.isNullOrEmpty(emailOrPhone) || !Validation.isValidPhoneNumber(emailOrPhone)) {
            Utilities.toastMessageShort(
                requireContext(),
                resources.getString(R.string.VALIDATE_PHONE)
            )
        } else {
            viewModel.checkPhoneExistAPI(phoneNumber = emailOrPhone, fragment = this)
        }
    }

    /*    private fun validateLogin() {
            com.caressa.allizhealth.app.model.tempconst.Configuration.EntityID = "0"
            val emailOrPhone = binding.edtLoginEmailPhone.text.toString().trim()

            if ( Utilities.isNullOrEmpty(emailOrPhone) ) {
                Utilities.toastMessageShort(requireContext(),resources.getString(R.string.VALIDATE_EMAIL_PHONE))
            } else if (emailOrPhone.contains("@", true)) {
                if (Validation.isValidEmail(emailOrPhone)) {
                    viewModel.checkEmailExistOrNot( email = emailOrPhone,fragment = this )
                } else {
                    Utilities.toastMessageShort(requireContext(),resources.getString(R.string.VALIDATE_EMAIL))
                }
            } else if ( TextUtils.isDigitsOnly(emailOrPhone) ) {
                if (Validation.isValidPhoneNumber(emailOrPhone)) {
                    viewModel.checkPhoneExistAPI( phoneNumber = emailOrPhone , fragment = this )
                } else if ( Utilities.isNullOrEmpty(emailOrPhone) ) {
                    Utilities.toastMessageShort(requireContext(),resources.getString(R.string.VALIDATE_PHONE))
                }
            }
        }*/

    private fun registerObserver() {
        viewModel.isEmail.observe(viewLifecycleOwner) {
/*            if (it.status == Resource.Status.SUCCESS) {
                isExist = it.data?.isExist.equals(Constants.TRUE, true)
                loginType = Constants.EMAIL
                email = binding.edtLoginEmailPhone.text.toString().trim()
                if (isExist) {
                    phone = it.data?.account!!.primaryPhone!!
                    viewModel.callGenerateVerificationCode(
                        email = it.data?.account!!.emailAddress!!,
                        phone = it.data?.account!!.primaryPhone!!,this)
                } else {
                    phone = ""
                    showNotRegisteredDialog(Constants.EMAIL,email)
                }
            }*/
        }

        viewModel.isPhone.observe(viewLifecycleOwner) {
/*            if (it.status == Resource.Status.SUCCESS) {
                isExist = it.data?.isExist.equals(Constants.TRUE, true)
                loginType = Constants.PHONE
                phone = binding.edtLoginEmailPhone.text.toString().trim()
                if (isExist) {
                    email = it.data?.account!!.emailAddress!!
                    viewModel.callGenerateVerificationCode(
                        email = it.data?.account!!.emailAddress!!,
                        phone = it.data?.account!!.primaryPhone!!,this)
                } else {
                    showNotRegisteredDialog(Constants.PHONE,phone)
                }
            }*/
        }

        viewModel.isEmailGoogle.observe(viewLifecycleOwner) {
/*            if (it.status == Resource.Status.SUCCESS) {
                isExist = it.data?.isExist.equals(Constants.TRUE, true)
                loginType = Constants.EMAIL
                if (isExist) {
                    viewModel.callLogin(
                        emailStr = it.data?.account!!.emailAddress!!,
                        passwordStr = "",
                        socialLogin = true,
                        source = Constants.GOOGLE_SOURCE,
                        view = binding.btnGetOtp)
                } else {
                    phone = ""
                    showNotRegisteredDialog(Constants.EMAIL,email)
                }
            }*/
        }

        viewModel.otpGenerateData.observe(viewLifecycleOwner) {
/*            if (it.status == Resource.Status.SUCCESS) {
                if (it.data!!.status.equals(Constants.SUCCESS,ignoreCase = true)) {
                    Utilities.printLogError("IsBottomSheetOpen--->$isBottomSheetOpen")
                    if (!isBottomSheetOpen) {
                        showBottomSheet()
                    } else {
                        modalBottomSheetOTP!!.refreshTimer()
                    }
                    startSmsUserConsent()
                }
            }*/
        }

        viewModel.otpValidateData.observe(viewLifecycleOwner) {
/*            if (it.status == Resource.Status.SUCCESS) {
                if (it.data!!.validity.equals(Constants.TRUE,true)) {
                    modalBottomSheetOTP!!.otpTimer.cancel()
                    modalBottomSheetOTP!!.dismiss()
                    loginOrProceedRegistration()
                } else {
                    Utilities.toastMessageShort(requireContext(),resources.getString(R.string.ERROR_INVALID_OTP))
                }
            }*/
        }

        viewModel.isLogin.observe(viewLifecycleOwner) {}
        viewModel.addFeatureAccessLog.observe(viewLifecycleOwner) {}
    }

    fun loginOrProceedRegistration() {
        if (isExist) {
            viewModel.callLogin(
                emailStr = email,
                socialLogin = true,
                source = Constants.LOGIN_SOURCE,
                view = binding.btnGetOtp
            )
        } else {
            //Navigate to Registration Screen
            viewModel.hideProgress()
            navigateToStepOne()
        }
    }

    private fun navigateToStepOne() {
        val bundle = Bundle()
        when (loginType) {
            Constants.EMAIL -> {
                Utilities.printLogError("$email : is not registered")
                bundle.putString(Constants.LOGIN_TYPE, Constants.EMAIL)
                bundle.putString(Constants.EMAIL, email)
                bundle.putString(Constants.NAME, username)
                bundle.putString(Constants.PRIMARY_PHONE, "")
            }

            Constants.PHONE -> {
                Utilities.printLogError("$phone : is not registered")
                bundle.putString(Constants.LOGIN_TYPE, Constants.PHONE)
                bundle.putString(Constants.EMAIL, "")
                bundle.putString(Constants.NAME, "")
                bundle.putString(Constants.PRIMARY_PHONE, phone)
            }
        }
        bundle.putString(Constants.PASSWORD,"Test@1234")
        findNavController().navigate(R.id.action_loginFragment_to_signUpStep1Fragment, bundle)
    }

    private fun googleLogin() {
        EntityID = "0"
        //googleSignOut()
        //startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)

        if (mGoogleApiClient != null && mGoogleApiClient!!.isConnected) {
            mGoogleApiClient!!.clearDefaultAccountAndReconnect()
        }
        isGoogleLoginClicked = true
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient!!)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    /*    private fun validateLogin() {

            val emailStr = binding.edtLoginEmailaddress.text.toString().trim()
            val passwordStr = binding.edtLoginPassword.text.toString().trim()

            if ( Utilities.isNullOrEmpty(emailStr) ) {
                Utilities.toastMessageShort(requireContext(),resources.getString(R.string.VALIDATE_EMAIL_PHONE))
            } else if ( Utilities.isNullOrEmpty(passwordStr) ) {
                Utilities.toastMessageShort(requireContext(),resources.getString(R.string.VALIDATE_PASSWORD))
            } else if (emailStr.contains("@", true)) {
                if (Validation.isValidEmail(emailStr)) {
                    viewModel.callLogin(forceRefresh = true, emailStr = emailStr, passwordStr = passwordStr)
                } else {
                    Utilities.toastMessageShort(requireContext(),resources.getString(R.string.VALIDATE_EMAIL))
                }
            } else if ( TextUtils.isDigitsOnly(emailStr) ) {
                if ( Utilities.isNullOrEmpty(emailStr) ) {
                    Utilities.toastMessageShort(requireContext(),resources.getString(R.string.VALIDATE_PHONE))
                } else if (Validation.isValidPhoneNumber(emailStr)) {
                    viewModel.checkPhoneExistAPI(phoneNumber = emailStr, passwordStr = passwordStr)
                }
            }
        }*/

    private fun facebookInit() {
        /*        FacebookSdk.setIsDebugEnabled(true)
                FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS)

                mCallbackManager = CallbackManager.Factory.create()

                //LoginManager.getInstance().logInWithReadPermissions(this,listOf("public_profile", "email"))
                LoginManager.getInstance().logInWithReadPermissions(this,mCallbackManager,listOf("public_profile", "email"))

                LoginManager.getInstance().registerCallback(
                    mCallbackManager,object : FacebookCallback<LoginResult> {

                        override fun onSuccess(result: LoginResult) {
                            Utilities.printLogError("facebook:onSuccess:$result")
                            accessToken = result.accessToken
                            firebaseAuthWithFacebook(result.accessToken)
                        }

                        override fun onCancel() {
                            Utilities.printLogError("facebook:onCancel")
                            logoutFacebook()
                        }

                        override fun onError(error: FacebookException) {
                            Utilities.printLogError("facebook:onError--->$error")
                            logoutFacebook()
                        }
                    })*/
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
//        Utilities.printLog("firebaseAuthWithGoogle "+acct.email)
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(activity as SecurityActivity) { task ->
                if (task.isSuccessful) {
                    val strEmail: String?
                    val googleId: String
                    var strName: String? = null
                    try {
                        val profile = task.result!!.additionalUserInfo!!.profile
                        val email = profile!!["email"]
                        val user = firebaseAuth.currentUser
                        strEmail = email.toString()//user?.email
                        googleId = user!!.uid
                        strName = Objects.requireNonNull<String>(strEmail).split("@".toRegex())
                            .dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                        if (user.displayName != null) {
                            strName = user.displayName
                        }
                        /*                        viewModel.checkEmailExistOrNot(
                                                    name = strName!!,
                                                    emailStr = strEmail,
                                                    socialLogin = true,
                                                    socialId = googleId,
                                                    view = binding.tvLoginSignup
                                                )*/

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    //Utilities.printLogError(task.exception?.message)
                    googleSignOut()
                    showSnackbar(
                        resources.getString(R.string.GOOGLE_SIGN_IN_FAILED),
                        Snackbar.LENGTH_SHORT
                    )
                }

            }
    }

    private fun googlePlusInit() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient =
            GoogleSignIn.getClient(activity as SecurityActivity, googleSignInOptions)
        googleSignOut()
    }

    private fun googlePlusInit1() {

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleApiClient = GoogleApiClient.Builder(requireContext())
            .enableAutoManage(requireActivity()) {
                Utilities.printLogError("onConnectionFailed FAILED")
            } /* OnConnectionFailedListener */
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {

                override fun onConnected(bundle: Bundle?) {
                    Utilities.printLogError("LOGIN_ACTIVITY")
                }

                override fun onConnectionSuspended(i: Int) {}
            })
            .build()

        Handler().postDelayed({
            if (mGoogleApiClient != null && mGoogleApiClient!!.isConnected) {
                mGoogleApiClient!!.clearDefaultAccountAndReconnect()
            }
        }, 500)

    }

    private fun googleSignOut() {
        try {
            googleSignInClient.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun googleSignOut1() {
        FitnessDataManager(requireContext()).signOutGoogleAccount()
    }

    private fun disconnectFromFacebook() {
        /*        if (AccessToken.getCurrentAccessToken() != null) {
                    GraphRequest(AccessToken.getCurrentAccessToken(),
                        "/me/permissions/",
                        null,
                        HttpMethod.DELETE,
                        GraphRequest.Callback { LoginManager.getInstance().logOut() }).executeAsync()
                }*/
    }

    private fun logoutFacebook() {
        // Facebook sign out
        /*        GraphRequest(
                    accessToken,
                    "/me/permissions/",
                    null,
                    HttpMethod.DELETE,
                    GraphRequest.Callback { LoginManager.getInstance().logOut() }).executeAsync()
                LoginManager.getInstance().logOut()*/
    }

    /*    private fun firebaseAuthWithFacebook(token: AccessToken) {
            //Utilities.printLogError("FB ACCESS TOKEN" + token.token)

            val credential = FacebookAuthProvider.getCredential(token.token)
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity as SecurityActivity) { task ->
                    if (task.isSuccessful) {
                        val strEmail: String?
                        val facebookId: String
                        var strName: String? = null
                        try {
                            val profile = task.result!!.additionalUserInfo!!.profile
                            val email = profile!!["email"]
                            val user = firebaseAuth.currentUser
                            strEmail = email.toString()//user?.email
                            facebookId = user!!.uid
                            strName = Objects.requireNonNull<String>(strEmail).split("@".toRegex())
                                .dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                            if (user.displayName != null) {
                                strName = user.displayName
                            }
                            Utilities.printLogError("Name--->$strName")
                            Utilities.printLogError("Email--->$strEmail")
                            viewModel.checkEmailExistOrNot(
                                name = strName!!,
                                emailStr = strEmail,
                                socialLogin = true,
                                source = Constants.FACEBOOK_SOURCE,
                                socialId = facebookId,
                                view = binding.tvLoginSignup
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Utilities.printLogError(task.exception?.message!!)
                        logoutFacebook()
                        showSnackbar(resources.getString(R.string.FB_SIGN_IN_FAILED), Snackbar.LENGTH_SHORT)
                    }
                }
        }*/

    override fun onPause() {
        super.onPause()
        mGoogleApiClient!!.stopAutoManage(requireActivity())
        mGoogleApiClient!!.disconnect()
    }

    fun showBottomSheet() {
        modalBottomSheetOTP = ModalBottomSheetOTP(
            loginType,
            email,
            phone,
            object : ModalBottomSheetOTP.OnVerifyClickListener {
                override fun onVerifyClick(code: String) {
                    if (Constants.IS_BYPASS_OTP) {
                        if (code == "123456") {
                            modalBottomSheetOTP!!.otpTimer.cancel()
                            modalBottomSheetOTP!!.dismiss()
                            loginOrProceedRegistration()
                        } else {
                            viewModel.callValidateVerificationCode(
                                code,
                                email,
                                phone,
                                this@LoginFragment
                            )
                        }
                    } else {
                        viewModel.callValidateVerificationCode(
                            code,
                            email,
                            phone,
                            this@LoginFragment
                        )
                    }
                }

                override fun onBottomSheetClosed() {
                    isBottomSheetOpen = false
                }
            },
            this
        )
        modalBottomSheetOTP!!.isCancelable = false
        modalBottomSheetOTP!!.show(childFragmentManager, ModalBottomSheetOTP.TAG)
        isBottomSheetOpen = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Utilities.printLogError("requestCode-> $requestCode")
        Utilities.printLogError("resultCode-> $resultCode")
        Utilities.printLogError("data-> $data")
        try {
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN) {
                if (isGoogleLoginClicked) {
                    val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)
                    Utilities.printLog("Result--->${result!!.isSuccess}")
                    if (result.isSuccess) {
                        // Signed in successfully, show authenticated UI.
                        val googleId: String? = null
                        //var strName: String? = null
                        val acct = result.signInAccount
                        val strEmail = acct!!.email
                        email = strEmail!!
                        var strName: String? =
                            Objects.requireNonNull(acct.email)!!.split("@".toRegex())
                                .toTypedArray()[0]
                        if (acct.displayName == null) {
                            if (acct.givenName != null) {
                                strName =
                                    (acct.givenName + " " + Objects.requireNonNull(acct.familyName)).trim { it <= ' ' }
                            }
                        } else {
                            strName = acct.displayName
                        }
                        username = strName!!
                        viewModel.checkEmailExistOrNotGoogle(
                            name = strName!!,
                            email = strEmail!!,
                            fragment = this@LoginFragment
                        )
                    } else {
                        Utilities.printLogError("Authentication failure.")
                        googleSignOut1()
                        // Signed out, show unauthenticated UI.
                    }
                }
            } else {
                //mCallbackManager!!.onActivityResult(requestCode, resultCode, data)
            }
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

    /*    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            Utilities.printLogError("requestCode-> $requestCode")
            Utilities.printLogError("resultCode-> $resultCode")
            Utilities.printLogError("data-> $data")
            if (requestCode == RC_SIGN_IN) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogle(account!!)
                } catch (e: ApiException) {
                    googleSignOut()
                }
            } else {
                mCallbackManager?.onActivityResult(requestCode, resultCode, data)
            }
        }*/

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
            modalBottomSheetOTP!!.binding.layoutCodeView.setText(matcher.group(0))
            modalBottomSheetOTP!!.binding.btnVerify.performClick()
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

    fun showNotRegisteredDialog(field: String, value: String) {
        Handler(Looper.getMainLooper()).postDelayed({
            val dialogData = DefaultNotificationDialog.DialogData()
            when (field) {
                Constants.EMAIL -> {
                    dialogData.message =
                        "<a><B><font color='#000000'>${resources.getString(R.string.EMAIL)} - $value</font></B> ${
                            resources.getString(
                                R.string.IS_NOT_REGISTERED_WITH_US
                            )
                        }<br/>${resources.getString(R.string.DO_YOU_WANT_REGISTER_IT_NOW)}</a>"
                }

                Constants.PHONE -> {
                    dialogData.message =
                        "<a><B><font color='#000000'>${resources.getString(R.string.MOBILE_NUMBER)} - $value</font></B> ${
                            resources.getString(
                                R.string.IS_NOT_REGISTERED_WITH_US
                            )
                        }<br/>${resources.getString(R.string.DO_YOU_WANT_REGISTER_IT_NOW)}</a>"
                }
            }
            dialogData.btnRightName = resources.getString(R.string.YES)
            dialogData.btnLeftName = resources.getString(R.string.NO)
            val defaultNotificationDialog = DefaultNotificationDialog(
                context,
                object : DefaultNotificationDialog.OnDialogValueListener {
                    override fun onDialogClickListener(
                        isButtonLeft: Boolean,
                        isButtonRight: Boolean
                    ) {
                        if (isButtonRight) {
                            when (field) {
                                Constants.EMAIL -> {
                                    //viewModel.callGenerateVerificationCode(email = value,phone = "",this@LoginFragmentNew)
                                    navigateToStepOne()
                                }

                                Constants.PHONE -> viewModel.callGenerateVerificationCode(
                                    email = "",
                                    phone = value,
                                    this@LoginFragment
                                )
                            }
                        }
                    }
                }, dialogData
            )
            defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            defaultNotificationDialog.show()
        }, (Constants.LOADER_ANIM_DELAY_IN_MS).toLong())
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

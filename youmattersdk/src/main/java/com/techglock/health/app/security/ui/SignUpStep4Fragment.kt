package com.techglock.health.app.security.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentSignUpStep4Binding
import com.techglock.health.app.security.SecurityHelper
import com.techglock.health.app.security.adapter.EmployerSelectionAdapter
import com.techglock.health.app.security.model.EmployerModel
import com.techglock.health.app.security.viewmodel.RegistrationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpStep4Fragment : BaseFragment(), EmployerSelectionAdapter.OnEmployerListener {

    private val viewModel: RegistrationViewModel by lazy {
        ViewModelProvider(this)[RegistrationViewModel::class.java]
    }
    private lateinit var binding: FragmentSignUpStep4Binding

    private var loginType = ""
    private var firstName = ""

    //private var lastName = ""
    private var phone = ""
    private var email = ""
    private var gender = ""
    private var dob = ""
    private var employer = ""
    private var employerSelectionAdapter: EmployerSelectionAdapter? = null

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
        binding = FragmentSignUpStep4Binding.inflate(inflater, container, false)
        (activity as SecurityActivity).updateStatusBarColor(R.drawable.gradient_step4, false)
        try {
            requireArguments().let {
                loginType = it.getString(Constants.LOGIN_TYPE, "")!!
                firstName = it.getString(Constants.FIRST_NAME, "")!!
                //lastName = it.getString(Constants.LAST_NAME, "")!!
                phone = it.getString(Constants.PRIMARY_PHONE, "")!!
                email = it.getString(Constants.EMAIL, "")!!
                gender = it.getString(Constants.GENDER, "")!!
                dob = it.getString(Constants.DATE_OF_BIRTH, "")!!
            }
            Utilities.printLogError("LoginType----->$loginType")
            Utilities.printLogError("FirstName----->$firstName")
            //Utilities.printLogError("LastName----->$lastName")
            Utilities.printLogError("Phone----->$phone")
            Utilities.printLogError("Email----->$email")
            Utilities.printLogError("Gender----->$gender")
            Utilities.printLogError("Dob----->$dob")
            setClickable()
            registerObserver()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    private fun setClickable() {
        employerSelectionAdapter = EmployerSelectionAdapter(requireContext(), this)
        binding.rvGenderList.adapter = employerSelectionAdapter
        employerSelectionAdapter!!.updateList(SecurityHelper.getEmployerList(requireContext()))

        binding.btnDone.setOnClickListener {
            validate()
        }

    }

    private fun registerObserver() {
        viewModel.isRegister.observe(viewLifecycleOwner) { }
        viewModel.addFeatureAccessLog.observe(viewLifecycleOwner) {}
    }

    private fun validate() {
        if (!Utilities.isNullOrEmpty(employer)) {
            var source = Constants.LOGIN_SOURCE
            //var socialLogin = false
            when (loginType) {
                Constants.EMAIL -> {
                    source = Constants.GOOGLE_SOURCE
                    //socialLogin = true
                }

                Constants.PHONE -> {
                    source = Constants.LOGIN_SOURCE
                    //socialLogin = false
                }
            }
            Utilities.printLogError("Source----->$source")
            //Utilities.printLogError("SocialLogin----->$socialLogin")
            Utilities.printLogError("FirstName----->$firstName")
            //Utilities.printLogError("LastName----->$lastName")
            Utilities.printLogError("Phone----->$phone")
            Utilities.printLogError("Email----->$email")
            Utilities.printLogError("Gender----->$gender")
            Utilities.printLogError("Dob----->$dob")
            Utilities.printLogError("Employer----->$employer")
            viewModel.callRegisterAPI(
                firstName = firstName,
                //lastName = lastName,
                emailStr = email,
                passwordStr = "Test@1234",
                phoneNumber = phone,
                gender = gender,
                dob = dob,
                //socialLogin = socialLogin,
                source = source,
                userType = employer,
                view = binding.btnDone
            )
        } else {
            Utilities.toastMessageShort(requireContext(), "Please select the relevant Employer")
        }
    }

    override fun onEmployerSelection(item: EmployerModel) {
        employer = item.employerCode
        Utilities.printLogError("Employer----->$employer")
    }

    fun performBackClick() {
        val bundle = Bundle()
        bundle.putString(Constants.LOGIN_TYPE, loginType)
        bundle.putString(Constants.FIRST_NAME, firstName)
        //bundle.putString(Constants.LAST_NAME,lastName)
        bundle.putString(Constants.PRIMARY_PHONE, phone)
        bundle.putString(Constants.EMAIL, email)
        bundle.putString(Constants.GENDER, gender)
        bundle.putString(Constants.DATE_OF_BIRTH, dob)
        findNavController().navigate(R.id.action_signUpStep4Fragment_to_signUpStep3Fragment, bundle)
    }

}
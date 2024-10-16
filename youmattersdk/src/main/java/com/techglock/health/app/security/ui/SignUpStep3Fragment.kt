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
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.FragmentSignUpStep3Binding
import com.techglock.health.app.security.viewmodel.LoginNewViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class SignUpStep3Fragment : BaseFragment() {

    private val viewModel: LoginNewViewModel by lazy {
        ViewModelProvider(this)[LoginNewViewModel::class.java]
    }
    private lateinit var binding: FragmentSignUpStep3Binding

    private var loginType = ""
    private var firstName = ""

    //private var lastName = ""
    private var phone = ""
    private var email = ""
    private var gender = ""
    private var dob = ""

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
        binding = FragmentSignUpStep3Binding.inflate(inflater, container, false)

        (activity as SecurityActivity).updateStatusBarColor(R.drawable.gradient_step3, false)

        try {
            requireArguments().let {
                loginType = it.getString(Constants.LOGIN_TYPE, "")!!
                firstName = it.getString(Constants.FIRST_NAME, "")!!
                //lastName = it.getString(Constants.LAST_NAME, "")!!
                phone = it.getString(Constants.PRIMARY_PHONE, "")!!
                email = it.getString(Constants.EMAIL, "")!!
                gender = it.getString(Constants.GENDER, "")!!
            }
            Utilities.printLogError("LoginType----->$loginType")
            Utilities.printLogError("FirstName----->$firstName")
            //Utilities.printLogError("LastName----->$lastName")
            Utilities.printLogError("Phone----->$phone")
            Utilities.printLogError("Email----->$email")
            Utilities.printLogError("Gender----->$gender")
            setClickable()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    private fun setClickable() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -18)
        binding.datePicker.maxDate = calendar.timeInMillis

        binding.btnProceed.setOnClickListener {
            dob = binding.datePicker.year.toString() +
                    "-" + (binding.datePicker.month + 1) +
                    "-" + binding.datePicker.dayOfMonth
            if (!Utilities.isNullOrEmpty(dob)) {
                dob = DateHelper.convertDateToStr(
                    DateHelper.convertStringDateToDate(
                        dob,
                        "yyyy-MM-dd"
                    ), "yyyy-MM-dd"
                )
                navigateToStepFour()
            } else {
                Utilities.toastMessageShort(requireContext(), "Please select your date of birth")
            }
        }

    }

    private fun navigateToStepFour() {
        val bundle = Bundle()
        bundle.putString(Constants.LOGIN_TYPE, loginType)
        bundle.putString(Constants.FIRST_NAME, firstName)
        //bundle.putString(Constants.LAST_NAME,lastName)
        bundle.putString(Constants.PRIMARY_PHONE, phone)
        bundle.putString(Constants.EMAIL, email)
        bundle.putString(Constants.GENDER, gender)
        bundle.putString(Constants.DATE_OF_BIRTH, dob)
        findNavController().navigate(R.id.action_signUpStep3Fragment_to_signUpStep4Fragment, bundle)
    }

    fun performBackClick() {
        val bundle = Bundle()
        bundle.putString(Constants.LOGIN_TYPE, loginType)
        bundle.putString(Constants.FIRST_NAME, firstName)
        //bundle.putString(Constants.LAST_NAME,lastName)
        bundle.putString(Constants.PRIMARY_PHONE, phone)
        bundle.putString(Constants.EMAIL, email)
        bundle.putString(Constants.GENDER, gender)
        findNavController().navigate(R.id.action_signUpStep3Fragment_to_signUpStep2Fragment, bundle)
    }

}
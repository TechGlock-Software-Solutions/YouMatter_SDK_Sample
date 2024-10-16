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
import com.techglock.health.app.databinding.FragmentSignUpStep2Binding
import com.techglock.health.app.security.SecurityHelper
import com.techglock.health.app.security.adapter.GenderSelectionAdapter
import com.techglock.health.app.security.model.GenderModel
import com.techglock.health.app.security.viewmodel.RegistrationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpStep2Fragment : BaseFragment(), GenderSelectionAdapter.OnGenderListener {

    private val viewModel: RegistrationViewModel by lazy {
        ViewModelProvider(this)[RegistrationViewModel::class.java]
    }
    private lateinit var binding: FragmentSignUpStep2Binding

    private var loginType = ""
    private var firstName = ""

    //private var lastName = ""
    private var phone = ""
    private var email = ""
    private var gender = ""
    private var genderSelectionAdapter: GenderSelectionAdapter? = null

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
        binding = FragmentSignUpStep2Binding.inflate(inflater, container, false)
        (activity as SecurityActivity).updateStatusBarColor(R.drawable.gradient_step2, false)

        try {
            requireArguments().let {
                loginType = it.getString(Constants.LOGIN_TYPE, "")!!
                firstName = it.getString(Constants.FIRST_NAME, "")!!
                //lastName = it.getString(Constants.LAST_NAME, "")!!
                phone = it.getString(Constants.PRIMARY_PHONE, "")!!
                email = it.getString(Constants.EMAIL, "")!!
            }
            Utilities.printLogError("LoginType----->$loginType")
            Utilities.printLogError("FirstName----->$firstName")
            //Utilities.printLogError("LastName----->$lastName")
            Utilities.printLogError("Phone----->$phone")
            Utilities.printLogError("Email----->$email")
            setClickable()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    private fun setClickable() {
        genderSelectionAdapter = GenderSelectionAdapter(requireContext(), this)
        binding.rvGenderList.adapter = genderSelectionAdapter
        val data = SecurityHelper.getUserGenderList(requireContext())
        for (i in data.indices) {
            data[i].isChecked = data[i].gender == gender
        }
        genderSelectionAdapter!!.updateList(data)

        binding.btnProceed.setOnClickListener {
            if (!Utilities.isNullOrEmpty(gender)) {
                navigateToStepThree()
            } else {
                Utilities.toastMessageShort(requireContext(), "Please select your gender")
            }
        }

    }

    private fun navigateToStepThree() {
        val bundle = Bundle()
        bundle.putString(Constants.LOGIN_TYPE, loginType)
        bundle.putString(Constants.FIRST_NAME, firstName)
        //bundle.putString(Constants.LAST_NAME,lastName)
        bundle.putString(Constants.PRIMARY_PHONE, phone)
        bundle.putString(Constants.EMAIL, email)
        bundle.putString(Constants.GENDER, gender)
        findNavController().navigate(R.id.action_signUpStep2Fragment_to_signUpStep3Fragment, bundle)
    }

    override fun onGenderSelection(item: GenderModel) {
        gender = item.gender
        Utilities.printLogError("Gender----->$gender")
    }

    fun performBackClick() {
        val bundle = Bundle()
        bundle.putString(Constants.LOGIN_TYPE, loginType)
        bundle.putString(Constants.FIRST_NAME, firstName)
        //bundle.putString(Constants.LAST_NAME,lastName)
        bundle.putString(Constants.PRIMARY_PHONE, phone)
        bundle.putString(Constants.EMAIL, email)
        findNavController().navigate(R.id.action_signUpStep2Fragment_to_signUpStep1Fragment, bundle)
    }

}
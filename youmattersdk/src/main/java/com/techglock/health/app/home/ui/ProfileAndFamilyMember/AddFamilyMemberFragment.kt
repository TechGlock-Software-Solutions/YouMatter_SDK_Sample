package com.techglock.health.app.home.ui.ProfileAndFamilyMember

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.DialogHelper
import com.techglock.health.app.common.utils.FileUtils
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.common.utils.Validation
import com.techglock.health.app.databinding.FragmentAddFamilyMemberBinding
import com.techglock.health.app.home.viewmodel.ProfileFamilyMemberViewModel
import com.techglock.health.app.model.entity.UserRelatives
import com.techglock.health.app.repository.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class AddFamilyMemberFragment : BaseFragment() {

    private val viewModel: ProfileFamilyMemberViewModel by lazy {
        ViewModelProvider(this)[ProfileFamilyMemberViewModel::class.java]
    }
    private lateinit var binding: FragmentAddFamilyMemberBinding

    private var from = ""
    private var relationCode = ""
    private var relation = ""
    private var gender = ""
    private var isValidFAM: Boolean = false
    private var userDob = ""
    private var relativeDob = ""

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddFamilyMemberBinding.inflate(inflater, container, false)
        from = requireArguments().getString(Constants.FROM, "")!!
        relationCode = requireArguments().getString(Constants.RELATION_CODE)!!
        relation = requireArguments().getString(Constants.RELATION)!!
        gender = requireArguments().getString(Constants.GENDER)!!
        //(activity as FamilyProfileActivity).setToolbarTitle(relation)
        Utilities.printLog("from----->$from")
        Utilities.printLog("Relation , RelationCode , GENDER----->$relation , $relationCode , $gender")
        initialise()
        setClickable()
        return binding.root
    }

    private fun initialise() {
        viewModel.getLoggedInPersonDetails()
        binding.imgFamilyMember.setImageResource(viewModel.getRelationImgId(relationCode))
        binding.txtRelationship.text = relation

        viewModel.addRelative.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {
                val newRelative = it.data!!.person
                if (!Utilities.isNullOrEmptyOrZero(newRelative.id.toString())) {
                    viewModel.toastMessage(resources.getString(R.string.MEMBER_ADDED))
                    Utilities.printLog("from----->$from")
                    requireActivity().finish()
                    /*                    if ( from.equals(Constants.HRA,ignoreCase = true) ) {
                                        } else {
                                            requireActivity().finish()
                                        }*/
                }
            }
        }

    }

    private fun setClickable() {

        binding.layoutDOB.setOnClickListener {
            showDatePicker()
        }

        binding.edtMemberDob.setOnClickListener {
            showDatePicker()
        }

        binding.btnAddMember.setOnClickListener {
            val name = binding.edtMemberName.text.toString()
            val dob = relativeDob
            val mobile = binding.edtMemberMobile.text.toString()
            val email = binding.edtMemberEmail.text.toString()

            if (!Validation.isValidName(name)) {
                Utilities.toastMessageShort(
                    requireContext(),
                    resources.getString(R.string.VALIDATE_NAME)
                )
            } else if (Utilities.isNullOrEmpty(dob)) {
                Utilities.toastMessageShort(
                    requireContext(),
                    resources.getString(R.string.VALIDATE_DATE_OF_BIRTH)
                )
            } else if (Utilities.isNullOrEmpty(mobile) || !Validation.isValidPhoneNumber(mobile)) {
                Utilities.toastMessageShort(
                    requireContext(),
                    resources.getString(R.string.VALIDATE_PHONE)
                )
            } else if (Utilities.isNullOrEmpty(email) || !Validation.isValidEmail(email)) {
                Utilities.toastMessageShort(
                    requireContext(),
                    resources.getString(R.string.VALIDATE_EMAIL)
                )
            } else {
                //***********************
                val relativeId = FileUtils.getUniqueIdLong()
                viewModel.userDetails.observe(viewLifecycleOwner) {
                    if (it != null) {
                        userDob = DateHelper.getDateTimeAs_ddMMMyyyy(it.dateOfBirth)
                        val relDob = DateHelper.getDateTimeAs_ddMMMyyyy(dob)
                        Utilities.printLog("UserDob  , RelativeDob----->$userDob , $relDob")
                        val famDate = DateHelper.convertStringToDate(relDob)
                        val userDOB = DateHelper.convertStringToDate(userDob)

                        // Validations for Age
                        when (relationCode) {

                            Constants.FATHER_RELATIONSHIP_CODE, Constants.MOTHER_RELATIONSHIP_CODE -> {
                                if (userDOB!! > famDate) {
                                    isValidFAM = true
                                    relativeDob = dob
                                } else {
                                    Utilities.toastMessageShort(
                                        requireContext(),
                                        resources.getString(R.string.ERROR_PARENTS_AGE)
                                    )
                                }
                            }

                            Constants.SON_RELATIONSHIP_CODE, Constants.DAUGHTER_RELATIONSHIP_CODE -> {
                                if (userDOB!! < famDate) {
                                    isValidFAM = true
                                    relativeDob = dob
                                } else {
                                    Utilities.toastMessageShort(
                                        requireContext(),
                                        resources.getString(R.string.ERROR_KIDS_AGE)
                                    )
                                }
                            }

                            Constants.GRANDFATHER_RELATIONSHIP_CODE, Constants.GRANDMOTHER_RELATIONSHIP_CODE -> {
                                if (userDOB!! > famDate) {
                                    isValidFAM = true
                                    relativeDob = dob
                                } else {
                                    Utilities.toastMessageShort(
                                        requireContext(),
                                        resources.getString(R.string.ERROR__GP_AGE)
                                    )
                                }
                            }

                            Constants.HUSBAND_RELATIONSHIP_CODE, Constants.WIFE_RELATIONSHIP_CODE -> {
                                if (!DateHelper.isDateAbove18Years(dob)) {
                                    Utilities.toastMessageShort(
                                        requireContext(),
                                        resources.getString(R.string.ERROR_AGE_NOT_LESS_THAN_18)
                                    )
                                } else {
                                    isValidFAM = true
                                    relativeDob = dob
                                }
                            }

                            Constants.BROTHER_RELATIONSHIP_CODE, Constants.SISTER_RELATIONSHIP_CODE -> {
                                isValidFAM = true
                                relativeDob = dob
                            }

                        }

                        if (isValidFAM) {
                            val newRelative = UserRelatives(
                                relativeID = relativeId,
                                firstName = name,
                                lastName = "",
                                dateOfBirth = dob,
                                gender = gender,
                                contactNo = mobile,
                                emailAddress = email,
                                relationshipCode = relationCode,
                                relationship = relation
                            )
                            viewModel.callAddNewRelativeApi(true, newRelative, from, this)
                        }
                    }
                }
                // it.findNavController().navigate(R.id.action_addFamilyMemberFragment_to_familyMembersListFragment)
                //***********************
            }

            /*  if ( !DateHelper.isDateAbove18Years(DateHelper.getDateTimeAs_ddMMMyyyy(dob)) ) {
                  binding.tilEdtMemberDob.isErrorEnabled = true
                  binding.tilEdtMemberDob.error = "Age must be more than 18 years"
              }*/

        }
    }

    private fun showDatePicker() {
        try {
            DialogHelper().showDatePickerDialog(resources.getString(R.string.DATE_OF_BIRTH),
                requireContext(),
                Calendar.getInstance(),
                null,
                Calendar.getInstance(),

                object : DialogHelper.DateListener {

                    override fun onDateSet(
                        date: String,
                        year: String,
                        month: String,
                        dayOfMonth: String
                    ) {
                        val selectedDate = DateHelper.convertDateSourceToDestination(
                            date,
                            DateHelper.DISPLAY_DATE_DDMMMYYYY,
                            DateHelper.SERVER_DATE_YYYYMMDD
                        )
                        Utilities.printLog("SelectedStartDate--->$selectedDate")
                        //val date = year + "-" + (month + 1) + "-" + dayOfMonth
                        relativeDob =
                            DateHelper.formatDateValue(DateHelper.DISPLAY_DATE_DDMMMYYYY, date)!!
                        Utilities.printLog("DOB=> $relativeDob")
                        binding.edtMemberDob.setText(
                            DateHelper.formatDateValue(
                                DateHelper.DATEFORMAT_DDMMMYYYY_NEW,
                                date
                            )
                        )
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun navigateToHRA() {
        requireActivity().finish()
    }

}

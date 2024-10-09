package com.techglock.health.app.home.ui.ProfileAndFamilyMember

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseActivity
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.DialogHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.common.utils.Validation
import com.techglock.health.app.databinding.ActivityEditFamilyMemberBinding
import com.techglock.health.app.home.viewmodel.ProfileFamilyMemberViewModel
import com.techglock.health.app.model.entity.UserRelatives
import com.techglock.health.app.repository.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class EditFamilyMemberActivity : BaseActivity() {

    private val viewModel: ProfileFamilyMemberViewModel by lazy {
        ViewModelProvider(this)[ProfileFamilyMemberViewModel::class.java]
    }
    private lateinit var binding: ActivityEditFamilyMemberBinding

    private val appColorHelper = AppColorHelper.instance!!

    private var relationCode = ""
    private var relation = ""
    private var relativeId = ""
    private var relationShipID = ""
    private var dateOfBirth = ""
    private var dob = ""
    private var userDob = ""
    private var isValidFAM: Boolean = false

    override fun getViewModel(): BaseViewModel = viewModel
    private val onBackPressedCallBack = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        //binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_family_member)
        binding = ActivityEditFamilyMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallBack)
        relativeId = intent.getStringExtra(Constants.RELATIVE_ID)!!
        relationShipID = intent.getStringExtra(Constants.RELATION_SHIP_ID)!!
        relationCode = intent.getStringExtra(Constants.RELATION_CODE)!!
        relation = intent.getStringExtra(Constants.RELATION)!!
        Utilities.printLog("RelativeId , RelationShipID , RelationCode----->$relativeId , $relationShipID , $relationCode")
        setupToolbar()
        initialise()
        registerObservers()
        setClickable()
    }

    private fun initialise() {
        binding.imgFamilyMember.setImageResource(viewModel.getRelationImgId(relationCode))
        binding.txtRelationship.text = relation

        viewModel.removeRelative.observe(this) {}
        viewModel.updateRelative.observe(this) {
            if (it.status == Resource.Status.SUCCESS) {
                val personDetails = it.data!!.person
                if (!Utilities.isNullOrEmptyOrZero(personDetails.id.toString())) {
                    viewModel.toastMessage(resources.getString(R.string.PROFILE_UPDATED))
                    finish()
                }
            }
        }

        viewModel.getUserRelativeForRelativeId(relativeId)
        viewModel.getLoggedInPersonDetails()
        viewModel.userDetails.observe(this) {
            if (it != null) {
                userDob = DateHelper.getDateTimeAs_ddMMMyyyy(it.dateOfBirth)
            }
        }
        viewModel.alreadyExistRelatives.observe(this) {
            if (it != null) {
                val relativeDetails = it[0]
                if (!Utilities.isNullOrEmpty(relativeDetails.dateOfBirth)) {

                    if (!Utilities.isNullOrEmpty(relativeDetails.firstName)) {
                        binding.edtMemberName.setText(relativeDetails.firstName)
                    }

                    if (!Utilities.isNullOrEmpty(relativeDetails.emailAddress)) {
                        binding.edtMemberEmail.setText(relativeDetails.emailAddress)
                    }

                    if (!Utilities.isNullOrEmpty(relativeDetails.contactNo)) {
                        binding.edtMemberMobile.setText(relativeDetails.contactNo)
                    }

                    //dob = DateHelper.getDateTimeAs_ddMMMyyyy(relativeDetails.dateOfBirth)
                    dob = DateHelper.formatDateValue(
                        DateHelper.DATEFORMAT_DDMMMYYYY_NEW,
                        relativeDetails.dateOfBirth
                    )!!
                    dateOfBirth =
                        DateHelper.formatDateValue("yyyy-MM-dd", relativeDetails.dateOfBirth)!!
                    try {
                        if (!Utilities.isNullOrEmpty(dob)) {
                            binding.edtMemberDob.setText(dob)
                        } else {
                            binding.edtMemberDob.setText(dob)
                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

    }

    private fun registerObservers() {

    }

    fun setClickable() {
        binding.layoutDOB.setOnClickListener {
            showDatePicker()
        }

        binding.edtMemberDob.setOnClickListener {
            showDatePicker()
        }

        binding.btnUpdateMember.setOnClickListener {

            if (!Utilities.isNullOrEmpty(relativeId) && checkIfUSerChangesValues()) {
                val username = binding.edtMemberName.text.toString().trim { it <= ' ' }
                val mobile = binding.edtMemberMobile.text.toString().trim { it <= ' ' }
                val email = binding.edtMemberEmail.text.toString().trim { it <= ' ' }
                val relativeDob = dateOfBirth

                if (!Validation.isValidName(username)) {
                    Utilities.toastMessageShort(this, resources.getString(R.string.VALIDATE_NAME))
                } else if (Utilities.isNullOrEmpty(dob)) {
                    Utilities.toastMessageShort(
                        this,
                        resources.getString(R.string.VALIDATE_DATE_OF_BIRTH)
                    )
                } else if (Utilities.isNullOrEmpty(mobile) || !Validation.isValidPhoneNumber(mobile)) {
                    Utilities.toastMessageShort(this, resources.getString(R.string.VALIDATE_PHONE))
                } else if (Utilities.isNullOrEmpty(email) || !Validation.isValidEmail(email)) {
                    Utilities.toastMessageShort(this, resources.getString(R.string.VALIDATE_EMAIL))
                } else {
                    viewModel.alreadyExistRelatives.observe(this) {
                        if (it != null) {
                            val relativeDetails = it[0]
                            val gender = relativeDetails.gender
                            val relation = relativeDetails.relationship
                            dob = DateHelper.getDateTimeAs_ddMMMyyyy(relativeDetails.dateOfBirth)
                            if (!Utilities.isNullOrEmpty(userDob)) {
                                val famDate = DateHelper.convertStringToDate(dob)
                                val userDOB = DateHelper.convertStringToDate(userDob)

                                // Validations for Age
                                when (relationCode) {

                                    Constants.FATHER_RELATIONSHIP_CODE, Constants.MOTHER_RELATIONSHIP_CODE -> {
                                        if (userDOB!! > famDate) {
                                            isValidFAM = true
                                        } else {
                                            Utilities.toastMessageShort(
                                                this,
                                                resources.getString(R.string.ERROR_PARENTS_AGE)
                                            )
                                        }
                                    }

                                    Constants.SON_RELATIONSHIP_CODE, Constants.DAUGHTER_RELATIONSHIP_CODE -> {
                                        if (userDOB!! < famDate) {
                                            isValidFAM = true
                                        } else {
                                            Utilities.toastMessageShort(
                                                this,
                                                resources.getString(R.string.ERROR_KIDS_AGE)
                                            )
                                        }
                                    }

                                    Constants.GRANDFATHER_RELATIONSHIP_CODE, Constants.GRANDMOTHER_RELATIONSHIP_CODE -> {
                                        if (userDOB!! > famDate) {
                                            isValidFAM = true
                                        } else {
                                            Utilities.toastMessageShort(
                                                this,
                                                resources.getString(R.string.ERROR__GP_AGE)
                                            )
                                        }
                                    }

                                    Constants.HUSBAND_RELATIONSHIP_CODE, Constants.WIFE_RELATIONSHIP_CODE -> {
                                        if (!DateHelper.isDateAbove18Years(dob)) {
                                            Utilities.toastMessageShort(
                                                this,
                                                resources.getString(R.string.ERROR_AGE_NOT_LESS_THAN_18)
                                            )
                                        } else {
                                            isValidFAM = true
                                        }
                                    }

                                    Constants.BROTHER_RELATIONSHIP_CODE, Constants.SISTER_RELATIONSHIP_CODE -> {
                                        isValidFAM = true
                                    }

                                }

                                if (isValidFAM) {
                                    val newRelative = UserRelatives(
                                        relativeID = relativeId,
                                        firstName = username,
                                        lastName = "",
                                        dateOfBirth = relativeDob,
                                        gender = gender,
                                        contactNo = mobile,
                                        emailAddress = email,
                                        relationshipCode = relationCode,
                                        relationship = relation,
                                        relationShipID = relationShipID
                                    )
                                    viewModel.callUpdateRelativesApi(
                                        true,
                                        newRelative,
                                        Constants.RELATIVE
                                    )
                                }
                            } else {
                                Utilities.toastMessageLong(
                                    this,
                                    resources.getString(R.string.ERROR_DOB_UNAVAILABLE)
                                )
                            }
                        }
                    }
                }

//                if ( !DateHelper.isDateAbove18Years(DateHelper.getDateTimeAs_ddMMMyyyy(relativeDob)) ) {
//                    binding.tilEdtMemberDob.isErrorEnabled = true
//                    binding.tilEdtMemberDob.error = "Age must be more than 18 years"
//                }

            } else {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun showDatePicker() {

        try {
            DialogHelper().showDatePickerDialog(resources.getString(R.string.DATE_OF_BIRTH),
                this,
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
//                        val date = year + "-" + (month + 1) + "-" + dayOfMonth
                        if (!Utilities.isNullOrEmpty(date)) {
                            dateOfBirth = date
                            binding.edtMemberDob.setText(
                                DateHelper.formatDateValue(
                                    DateHelper.DATEFORMAT_DDMMMYYYY_NEW,
                                    date
                                )
                            )
                        }
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkIfUSerChangesValues(): Boolean {
        var isChanges = false
        val username = binding.edtMemberName.text.toString()
        val mobile = binding.edtMemberMobile.text.toString()
        val email = binding.edtMemberMobile.text.toString()
        val dobNew = DateHelper.getDateTimeAs_ddMMMyyyy(dateOfBirth)
        Utilities.printLog("username,mobile,email,DateOfBirth----->$username , $mobile , $email , $dobNew")
        viewModel.alreadyExistRelatives.observe(this) {
            if (it != null) {
                val relativeDetails = it[0]
                Utilities.printLog("RelativeDetailsBefore----->" + it[0])
                val userNameBefore = relativeDetails.firstName
                val mobileBefore = relativeDetails.contactNo
                val emailBefore = relativeDetails.emailAddress
                val ageStringBefore = relativeDetails.dateOfBirth

                if (username != userNameBefore) {
                    isChanges = true
                }
                if (mobile != mobileBefore) {
                    isChanges = true
                }
                if (email != emailBefore) {
                    isChanges = true
                }
                if (dobNew != ageStringBefore) {
                    isChanges = true
                }
            }
        }
        Utilities.printLog("Details_Changed----->$isChanges")
        return isChanges
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolBar.toolbarCommon)
        binding.toolBar.toolbarTitle.text = resources.getString(R.string.TITLE_EDIT_DETAILS)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        binding.toolBar.toolbarCommon.navigationIcon?.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                appColorHelper.textColor,
                BlendModeCompat.SRC_ATOP
            )

        binding.toolBar.toolbarCommon.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    //    override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
//        if (isButtonRight) {
//            viewModel.callRemoveRelativesApi(true, relativeId, relationShipID)
//        }
//    }

}
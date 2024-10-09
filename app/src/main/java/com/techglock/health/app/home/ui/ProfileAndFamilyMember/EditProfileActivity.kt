package com.techglock.health.app.home.ui.ProfileAndFamilyMember

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseActivity
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Configuration
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.AppColorHelper
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.DefaultNotificationDialog
import com.techglock.health.app.common.utils.DialogHelper
import com.techglock.health.app.common.utils.FileUtils
import com.techglock.health.app.common.utils.PermissionUtil
import com.techglock.health.app.common.utils.UserSingleton
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.common.utils.Validation
import com.techglock.health.app.databinding.ActivityEditProfileBinding
import com.techglock.health.app.home.common.DataHandler
import com.techglock.health.app.home.viewmodel.ProfileFamilyMemberViewModel
import com.techglock.health.app.model.home.Person
import com.techglock.health.app.model.home.UpdateUserDetailsModel
import com.techglock.health.app.repository.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

@AndroidEntryPoint
class EditProfileActivity : BaseActivity(), EditProfileBottomSheet.OnOptionClickListener {

    private val viewModel: ProfileFamilyMemberViewModel by lazy {
        ViewModelProvider(this)[ProfileFamilyMemberViewModel::class.java]
    }
    private lateinit var binding: ActivityEditProfileBinding

    private val appColorHelper = AppColorHelper.instance!!
    private val permissionUtil = PermissionUtil
    private val fileUtils = FileUtils

    private var strAgeGender = ""
    private var dateOfBirth = ""
    var completeFilePath = ""
    var hasProfileImage = false
    var needToSet = true
    var user: Person = Person()
    var gender = ""

    //private var profPicBitmap : Bitmap? = null
//    private var aktivoManager: AktivoManager? = null

    override fun getViewModel(): BaseViewModel = viewModel
    private val onBackPressedCallBack = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        //binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallBack)
        setUpToolbar()
        initialise()
        registerObservers()
        setClickable()
    }

    private fun initialise() {
//        aktivoManager = AktivoManager.getInstance(this)
        startImageShimmer()
        if (viewModel.isSelfUser()) {
            //binding.layoutChangeProfilePic.visibility = View.VISIBLE
            binding.imgEditPic.visibility = View.VISIBLE
        } else {
            //binding.layoutChangeProfilePic.visibility = View.GONE
            binding.imgEditPic.visibility = View.GONE
        }
        viewModel.callGetUserDetailsEditApi()

        binding.edtUsername.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                if (!editable.toString().equals("", ignoreCase = true)) {
                    binding.tilEdtUsername.error = null
                    binding.tilEdtUsername.isErrorEnabled = false
                    //binding.txtUsername.text = editable.toString()
                } else {
                    //binding.txtUsername.text = user.firstName
                }
            }
        })


        binding.layoutBtnProfile.visibility = View.VISIBLE
        binding.layoutEditDetails.visibility = View.VISIBLE
    }

    private fun registerObservers() {

        viewModel.userProfileDetailsEdit.observe(this) {
            if (it.status == Resource.Status.SUCCESS) {
                val person = it.data!!.person
                if (!Utilities.isNullOrEmptyOrZero(person.profileImageID.toString())) {
                    hasProfileImage = true
                    //viewModel.callGetProfileImageApi(this, person.profileImageID.toString())
                    viewModel.callGetProfileImageApiInner(this, person.profileImageID.toString())
                } else {
                    stopImageShimmer()
                    binding.imgUserPic.setImageResource(
                        Utilities.getRelativeImgIdWithGender(
                            "",
                            person.gender.toString()
                        )
                    )
                }
                user = person
                setUserDetails(person)
            }
        }

        viewModel.updateUserDetails.observe(this) {
            viewModel.hideProgressBar()
            lifecycleScope.launch(Dispatchers.Main) {
                delay(600)
                if (it.status == Resource.Status.SUCCESS) {
                    val person = it.data!!.person
                    user = person
                    setUserDetails(person)
                    //updateAktivoUserProfile(person)
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }
        viewModel.profileImage.observe(this) {}
        viewModel.uploadProfileImage.observe(this) {

        }
        viewModel.removeProfileImage.observe(this) {}
    }

    fun setClickable() {

        binding.tilEdtBirthdate.setOnClickListener {
            showDatePicker()
        }
        binding.edtBirthdate.setOnClickListener {
            showDatePicker()
        }

        binding.imgUserPic.setOnClickListener {
            viewBottomSheet()
        }

        binding.imgEditPic.setOnClickListener {
            viewBottomSheet()
        }

        binding.btnUpdateProfile.setOnClickListener {
            validateAndUpdate()
        }

        /*        binding.imgUserPic.setOnClickListener {
            val bitmap = HomeSingleton.getInstance()!!.profPicBitmap
            if ( bitmap != null ) {
                Utilities.showFullImageWithBitmap(bitmap,this,true)
            } else {
                viewProfilePhoto()
            }
        }*/

        /*        binding.layoutChangeProfilePic.setOnClickListener {
                    viewBottomSheet()
                }*/

    }

    private fun showDatePicker() {
        try {
            val mCalendar = Calendar.getInstance()
            mCalendar.add(Calendar.YEAR, -18)
            DialogHelper().showDatePickerDialog(resources.getString(R.string.DATE_OF_BIRTH),
                this,
                Calendar.getInstance(),
                null,
                mCalendar,

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
                            binding.edtBirthdate.setText(
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

    private fun validateAndUpdate() {
        val username = binding.edtUsername.text.toString().trim { it <= ' ' }
        //val newEmail = binding.edtEmail.text.toString().trim { it <= ' ' }
        //val newNumber = binding.edtNumber.text.toString().trim { it <= ' ' }
        val dob = binding.edtBirthdate.text.toString().trim { it <= ' ' }

        var isValid = true

        if (Validation.isEmpty(username) || !Validation.isValidName(username)) {
            isValid = false
            viewModel.toastMessage(binding.edtBirthdate.context.resources.getString(R.string.VALIDATE_USERNAME))
        }

        if (Utilities.isNullOrEmpty(binding.edtBirthdate.text.toString())) {
            isValid = false
            viewModel.toastMessage(binding.edtBirthdate.context.resources.getString(R.string.VALIDATE_DATE_OF_BIRTH))
        }

        dateOfBirth = DateHelper.convertDateSourceToDestination(
            dob,
            DateHelper.DATEFORMAT_DDMMMYYYY_NEW,
            DateHelper.SERVER_DATE_YYYYMMDD
        )

        if (isValid) {
            //Helper.showMessage(getContext(),"Details Updated");
            val newUserDetails = UpdateUserDetailsModel.PersonRequest(
                id = user.id,
                firstName = username,
                dateOfBirth = dateOfBirth,
                gender = user.gender.toString(),
                contact = UpdateUserDetailsModel.Contact(
                    emailAddress = user.contact.emailAddress,
                    primaryContactNo = user.contact.primaryContactNo,
                    alternateEmailAddress = "",
                    alternateContactNo = "",
                ),
                address = UpdateUserDetailsModel.Address(
                    addressLine1 = ""
                )
            )
            viewModel.callUpdateUserDetailsApi(newUserDetails)
        }
    }

    private fun viewBottomSheet() {
        try {
            val bottomSheet = EditProfileBottomSheet(this, hasProfileImage)
            bottomSheet.show(supportFragmentManager, EditProfileBottomSheet.TAG)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onOptionClick(position: Int, code: String) {

        if (code == DataHandler.ProfileImgOption.View) {
            viewProfilePhoto()
            /*            val bitmap = UserSingleton.getInstance()!!.profPicBitmap
                        if ( bitmap != null ) {
                            Utilities.showFullImageWithBitmap(bitmap,this,true)
                        } else {
                            viewProfilePhoto()
                        }*/
        }
        if (code == DataHandler.ProfileImgOption.Gallery) {
            showImageChooser()
        }
        if (code == DataHandler.ProfileImgOption.Photo) {
            proceedWithCameraPermission()
        }
        if (code == DataHandler.ProfileImgOption.Remove) {
            val dialogData = DefaultNotificationDialog.DialogData()
            dialogData.title = resources.getString(R.string.REMOVE_PROFILE_PHOTO)
            dialogData.message = resources.getString(R.string.MSG_REMOVE_PROFILE_PHOTO_CONFORMATION)
            dialogData.btnLeftName = resources.getString(R.string.NO)
            dialogData.btnRightName = resources.getString(R.string.YES)
            dialogData.hasErrorBtn = true
            val defaultNotificationDialog =
                DefaultNotificationDialog(
                    this,
                    object : DefaultNotificationDialog.OnDialogValueListener {
                        override fun onDialogClickListener(
                            isButtonLeft: Boolean,
                            isButtonRight: Boolean
                        ) {
                            if (isButtonRight) {
                                viewModel.callRemoveProfileImageApi(
                                    this@EditProfileActivity,
                                    this@EditProfileActivity
                                )
                            }
                        }
                    },
                    dialogData
                )
            defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            defaultNotificationDialog.show()
        }

    }

    private fun viewProfilePhoto() {
        try {
            //viewModel.getLoggedInPersonDetails()
            if (!Utilities.isNullOrEmpty(completeFilePath)) {
                val file = File(completeFilePath)
                if (file.exists()) {
                    val type = "image/*"
                    val intent = Intent(Intent.ACTION_VIEW)
                    val uri = Uri.fromFile(file)
                    intent.setDataAndType(uri, type)
                    //intent.setDataAndType(FileProvider.getUriForFile(this, getPackageName().toString() + ".provider", file), type)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    try {
                        startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Utilities.toastMessageShort(
                            this,
                            resources.getString(R.string.ERROR_UNABLE_TO_OPEN_FILE)
                        )
                    }
                } else {
                    Utilities.toastMessageShort(
                        this,
                        resources.getString(R.string.ERROR_FILE_DOES_NOT_EXIST)
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun proceedWithCameraPermission() {
        val permissionResult: Boolean = permissionUtil.checkCameraPermission(object :
            PermissionUtil.AppPermissionListener {
            override fun isPermissionGranted(isGranted: Boolean) {
                Utilities.printLogError("$isGranted")
                if (isGranted) {
                    dispatchTakePictureIntent()
                }
            }
        }, this)
        if (permissionResult) {
            dispatchTakePictureIntent()
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(Objects.requireNonNull(this).packageManager) != null) {
            startActivityForResult(takePictureIntent, Constants.CAMERA_SELECT_CODE)
        }
    }

    private fun showImageCropper(uriImage: Uri) {
        CropImage.activity(uriImage)
            .start(this)
    }

    private fun showImageChooser() {
        val pickIntent = Intent(Intent.ACTION_PICK)
        pickIntent.type = "image/*"
        startActivityForResult(pickIntent, Constants.GALLERY_SELECT_CODE)
    }

    private fun setUserDetails(user: Person) {
        try {
            if (!Utilities.isNullOrEmptyOrZero(user.id.toString())) {
                val email = user.contact.emailAddress
                val dob = user.dateOfBirth
                val alternateEmail = user.contact.alternateEmailAddress
                val number = user.contact.primaryContactNo
                val alternateNumber = user.contact.alternateContactNo
                var address = ""
                if (user.address != null) {
                    address = user.address.addressLine1
                }
                gender = user.gender.toString()

                /*if (!user.dateOfBirth.isNullOrEmpty()) {
                    var dateOfBirth = user.dateOfBirth
                    dateOfBirth = DateHelper.formatDateValue(DateHelper.DISPLAY_DATE_DDMMMYYYY, dateOfBirth)!!
                    val viewDob = DateHelper.formatDateValue(DateHelper.DATEFORMAT_DDMMMYYYY_NEW, dateOfBirth)!!
                    Utilities.printLog("DOB--->$dateOfBirth")
                    if (!Utilities.isNullOrEmpty(dateOfBirth)) {
                        val age: String = DateHelper.calculatePersonAge(dateOfBirth)
                        strAgeGender = if (!Utilities.isNullOrEmptyOrZero(user.age.toString())) {
                            user.age.toString() + " Yrs"
                        } else {
                            age
                        }
                    }
                } else {

                }*/

                /*                if (user.gender.toString().equals("1", ignoreCase = true)) {
                                    if (!Utilities.isNullOrEmpty(strAgeGender)) {
                                        binding.txtAgeGender.text =
                                            strAgeGender + " , " + resources.getString(R.string.MALE)
                                    } else {
                                        binding.txtAgeGender.text = resources.getString(R.string.MALE)
                                    }
                                } else if (user.gender.toString().equals("2", ignoreCase = true)) {
                                    if (!Utilities.isNullOrEmpty(strAgeGender)) {
                                        binding.txtAgeGender.text =
                                            strAgeGender + " , " + resources.getString(R.string.FEMALE)
                                    } else {
                                        binding.txtAgeGender.text = resources.getString(R.string.FEMALE)
                                    }
                                }*/

                if (!Utilities.isNullOrEmpty(user.firstName)) {
                    //binding.txtUsername.text = user.firstName
                    viewModel.updateFirstName(user.firstName)
                    binding.edtUsername.setText(user.firstName)
                }

                if (!Utilities.isNullOrEmpty(email)) {
                    binding.edtEmail.setText(email)
                }

                if (!Utilities.isNullOrEmpty(number)) {
                    binding.tilEdtNumber.visibility = View.VISIBLE
                    binding.edtNumber.setText(number)
                } else {
                    binding.tilEdtNumber.visibility = View.GONE
                }
                if (!Utilities.isNullOrEmpty(user.dateOfBirth)) {
                    var dateOfBirth = user.dateOfBirth
                    dateOfBirth =
                        DateHelper.formatDateValue(DateHelper.DISPLAY_DATE_DDMMMYYYY, dateOfBirth)!!
                    val viewDob = DateHelper.formatDateValue(
                        DateHelper.DATEFORMAT_DDMMMYYYY_NEW,
                        dateOfBirth
                    )!!
                    Utilities.printLog("DOB--->$dateOfBirth")
                    if (!Utilities.isNullOrEmpty(dateOfBirth)) {
                        val age: String = DateHelper.calculatePersonAge(dateOfBirth, this)
                        strAgeGender = if (!Utilities.isNullOrEmptyOrZero(user.age.toString())) {
                            user.age.toString() + " Yrs"
                        } else {
                            age
                        }
                        binding.edtBirthdate.setText(viewDob)
                    }
                } else {
                    binding.edtBirthdate.setText("")
                }

                if (!Utilities.isNullOrEmpty(gender)) {
                    if (gender.equals("1", true)) {
                        binding.edtGender.setText(resources.getString(R.string.MALE))
                    } else {
                        binding.edtGender.setText(resources.getString(R.string.FEMALE))
                    }

                } else {
                    binding.edtGender.setText("")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setProfilePic() {
        try {
            Utilities.printLogError("completeFilePath----->$completeFilePath")
            Utilities.printLogError("needToSet----->$needToSet")
            if (needToSet) {
                if (!Utilities.isNullOrEmpty(completeFilePath)) {
                    val bitmap = BitmapFactory.decodeFile(completeFilePath)
                    if (bitmap != null) {
                        binding.imgUserPic.setImageBitmap(bitmap)
                        //profPicBitmap = bitmap
                        UserSingleton.getInstance()!!.profPicBitmap = bitmap
                        needToSet = false
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun removeProfilePic() {
        hasProfileImage = false
        gender
        when (gender) {
            "1" -> {
                binding.imgUserPic.setImageResource(
                    Utilities.getRelativeImgIdWithGender(
                        "",
                        gender
                    )
                )
            }

            "2" -> {
                binding.imgUserPic.setImageResource(
                    Utilities.getRelativeImgIdWithGender(
                        "",
                        gender
                    )
                )
            }

            else -> {
                binding.imgUserPic.setImageResource(R.drawable.img_my_profile)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Utilities.printLogError("requestCode,resultCode,data----->$requestCode,$resultCode,$data")
        try {

            if (resultCode == Activity.RESULT_OK && data != null) {
                when (requestCode) {

                    Constants.CAMERA_SELECT_CODE -> {
                        val photo = data.extras!!.get("data") as Bitmap
                        val uriImage = fileUtils.getImageUri(this, photo)
                        val cameraImgPath = fileUtils.getFilePath(this, uriImage)!!
                        val fileSize = fileUtils.calculateFileSize(cameraImgPath, "MB")
                        if (fileSize <= 5.0) {
                            showImageCropper(uriImage)
                        } else {
                            Utilities.toastMessageShort(
                                this,
                                resources.getString(R.string.ERROR_FILE_SIZE_LESS_THEN_5MB)
                            )
                        }
                    }

                    Constants.GALLERY_SELECT_CODE -> {
                        val uriImage = data.data
                        val imagePath = fileUtils.getFilePath(this, uriImage!!)!!
                        val fileSize = fileUtils.calculateFileSize(imagePath, "MB")
                        if (fileSize <= 5.0) {
                            val extension = fileUtils.getFileExt(imagePath)
                            if (Utilities.isAcceptableDocumentType(extension)) {

                                showImageCropper(uriImage)
                            } else {
                                Utilities.toastMessageLong(
                                    this,
                                    extension + " " + resources.getString(R.string.ERROR_FILES_NOT_ACCEPTED)
                                )
                            }
                        } else {
                            Utilities.toastMessageShort(
                                this,
                                resources.getString(R.string.ERROR_FILE_SIZE_LESS_THEN_5MB)
                            )
                        }
                    }

                }
            }

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data)
                if (resultCode == RESULT_OK) {
                    val imageUri = result.uri
                    val imagePath = fileUtils.getFilePath(this, imageUri!!)!!
                    val fileSize = fileUtils.calculateFileSize(imagePath, "MB")
                    if (fileSize <= 5.0) {
                        val extension = fileUtils.getFileExt(imagePath)
                        if (Utilities.isAcceptableDocumentType(extension)) {
                            val fileName = fileUtils.generateUniqueFileName(
                                Configuration.strAppIdentifier + "_PROFPIC",
                                imagePath
                            )
                            Utilities.printLogError("File Path---> $imagePath")
                            val saveImage = fileUtils.saveRecordToExternalStorage(
                                this,
                                imagePath,
                                imageUri,
                                fileName
                            )
                            if (saveImage != null) {
                                Utilities.deleteFileFromLocalSystem(imagePath)
                                viewModel.callUploadProfileImageApi(this, fileName, saveImage)
                            }
                        } else {
                            Utilities.toastMessageLong(
                                this,
                                extension + " " + resources.getString(R.string.ERROR_FILES_NOT_ACCEPTED)
                            )
                        }
                    } else {
                        Utilities.toastMessageLong(
                            this,
                            resources.getString(R.string.ERROR_FILE_SIZE_LESS_THEN_5MB)
                        )
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val error = result.error
                    Utilities.printLogError("ImageCropperError--->$error")
                }
            }

            /*            if ( requestCode == Constants.CAMERA_SELECT_CODE && resultCode == Activity.RESULT_OK ) {
                Utilities.printLogError("********onCameraPhotoClicked********")
                //val photo = MediaStore.Images.Media.getBitmap(contentResolver,Uri.fromFile(photoFile))
                val photo = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(contentResolver,cameraFileUri)
                } else {
                    val source: ImageDecoder.Source = ImageDecoder.createSource(contentResolver,cameraFileUri!!)
                    ImageDecoder.decodeBitmap(source)
                }
                val fileSize = RealPathUtil.calculateDocumentFileSize(RealPathUtil.recordFile!!,"MB")
                if (fileSize <= 5.0) {
                    showImageCropper(RealPathUtil.recordUri.toUri())
                    //Utilities.deleteDocumentFileFromLocalSystem(this,RealPathUtil.recordUri.toUri(),tempFileName)
                } else {
                    Utilities.deleteDocumentFileFromLocalSystem(this,cameraFileUri!!,tempFileName)
                    Utilities.toastMessageLong(this,resources.getString(R.string.ERROR_FILE_SIZE_LESS_THEN_5MB))
                }
                Utilities.deleteFile(photoFile)
            }*/

            //Utilities.hideKeyboard(this)
            super.onActivityResult(requestCode, resultCode, data)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startImageShimmer() {
        binding.layoutImgShimmer.startShimmer()
        binding.layoutImgShimmer.visibility = View.VISIBLE
        binding.layoutImgDetails.visibility = View.GONE
    }


    fun stopImageShimmer() {
        binding.layoutImgShimmer.stopShimmer()
        binding.layoutImgShimmer.visibility = View.GONE
        binding.layoutImgDetails.visibility = View.VISIBLE
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolBarView.toolbarCommon)
        binding.toolBarView.toolbarTitle.text = resources.getString(R.string.TITLE_EDIT_PROFILE)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        binding.toolBarView.toolbarCommon.navigationIcon?.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                appColorHelper.textColor,
                BlendModeCompat.SRC_ATOP
            )

        binding.toolBarView.toolbarCommon.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    /*private fun updateAktivoUserProfile(user: Person) {
        try {
            val date = DateHelper.convertDateSourceToDestination(user.dateOfBirth.split("T").toTypedArray()[0],DateHelper.SERVER_DATE_YYYYMMDD,"yyyy/MM/dd")
            val dateOfBirth = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy/MM/dd"))
            var gender = Gender.Male
            when(user.gender.toString()) {
                "1" -> gender = Gender.Male
                "2" -> gender = Gender.Female
            }
            val userProfile = UserProfile(
                nickName = user.firstName,
                dateOfBirth = dateOfBirth,
                gender = gender,
                height = Height(HeightCm(172)), weight = Weight(WeightKg(64))
            )
            Utilities.printData("UserProfile",userProfile,true)
            aktivoManager!!.updateUserProfile(userProfile).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Boolean> {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onSuccess(aBoolean: Boolean) {
                        Utilities.printLogError("Response for update profile: $aBoolean")
                    }

                    override fun onError(e: Throwable) {
                        Utilities.printLogError("Response error for update profile: " + e.message)
                    }
                })
        } catch ( e:Exception ) {
            e.printStackTrace()
        }
    }*/


}
package com.techglock.health.app.home.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseFragment
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.DefaultNotificationDialog
import com.techglock.health.app.common.utils.FileUtils
import com.techglock.health.app.common.utils.PermissionUtil
import com.techglock.health.app.common.utils.UserSingleton
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.databinding.DialogEditProfileBinding
import com.techglock.health.app.databinding.DialogSwitchProfileBinding
import com.techglock.health.app.databinding.FragmentProfileHomeBinding
import com.techglock.health.app.home.adapter.RvpFamilyMemberListAdapter
import com.techglock.health.app.home.adapter.SudPolicyListAdapter
import com.techglock.health.app.home.adapter.SwitchProfileAdapter
import com.techglock.health.app.home.common.PolicyDataSingleton
import com.techglock.health.app.home.ui.ProfileAndFamilyMember.EditProfileActivity
import com.techglock.health.app.home.ui.ProfileAndFamilyMember.FamilyProfileActivity
import com.techglock.health.app.home.ui.sudLifePolicy.SudLifePolicyActivity
import com.techglock.health.app.home.ui.wellfie.WellfieActivity
import com.techglock.health.app.home.viewmodel.BackgroundCallViewModel
import com.techglock.health.app.home.viewmodel.DashboardViewModel
import com.techglock.health.app.home.viewmodel.ProfileFamilyMemberViewModel
import com.techglock.health.app.home.viewmodel.SudLifePolicyViewModel
import com.techglock.health.app.model.entity.UserRelatives
import com.techglock.health.app.model.home.Contact
import com.techglock.health.app.model.home.Person
import com.techglock.health.app.model.sudLifePolicy.SudKYPModel
import com.techglock.health.app.repository.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.blurry.Blurry
import java.io.File

@AndroidEntryPoint
class FragmentProfile : BaseFragment(), SudPolicyListAdapter.OnPolicyClickListener {

    private lateinit var binding: FragmentProfileHomeBinding
    private val viewModel: DashboardViewModel by lazy {
        ViewModelProvider(this)[DashboardViewModel::class.java]
    }
    private val viewModelProfile: ProfileFamilyMemberViewModel by lazy {
        ViewModelProvider(this)[ProfileFamilyMemberViewModel::class.java]
    }
    private val backGroundCallViewModel: BackgroundCallViewModel by lazy {
        ViewModelProvider(this)[BackgroundCallViewModel::class.java]
    }
    private val sudLifePolicyViewModel: SudLifePolicyViewModel by lazy {
        ViewModelProvider(this)[SudLifePolicyViewModel::class.java]
    }

    private val permissionUtil = PermissionUtil

    private var strAgeGender = ""
    private var hasProfileImage = false
    private var completeFilePath = ""
    private var needToSet = true
    private var userGender = ""
    private val fileUtils = FileUtils

    var policyListSizeFinal = 0
    var policyListSize = 0

    //private val sudPolicyList: MutableList<SudPolicyDetailsByPolicyNumberModel.PolicyDetails> = mutableListOf()
    val sudPolicyList: MutableList<SudKYPModel.KYP> = mutableListOf()
    private var sudPolicyListAdapter: SudPolicyListAdapter? = null
    var policyDataSingleton: PolicyDataSingleton? = null

    private var familyMembersAdapter: RvpFamilyMemberListAdapter? = null
    private var familyList: MutableList<UserRelatives> = mutableListOf()

    override fun getViewModel(): BaseViewModel = viewModelProfile

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileHomeBinding.inflate(inflater, container, false)


        initialise()
        setObserver()
        setClickable()
        //viewModelProfile.callListRelativesApi(true)
        //viewModelProfile.callListDocumentsApi(true,"")
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as HomeMainActivity).setToolbarInfo(
            4,
            true,
            showToolBar = false,
            title = resources.getString(R.string.PROFILE)
        )

    }


    override fun onResume() {
        super.onResume()
        requireActivity().window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR

        Utilities.printLogError("Inside_Profile_onResume=")
        clearPolicyList()
        val profPicBitmap = UserSingleton.getInstance()!!.profPicBitmap
        if (viewModelProfile.personId == viewModelProfile.adminPersonId && profPicBitmap != null) {
            binding.imgUserPic.setImageBitmap(profPicBitmap)
            binding.imgUserPicBanner.setImageBitmap(profPicBitmap)
            blurBanner(profPicBitmap)
        } else if (!Utilities.isNullOrEmpty(userGender)) {
            //binding.imgUserPic.setImageResource(Utilities.getRelativeImgIdWithGender("",userGender))
            binding.imgUserPic.setImageResource(
                Utilities.getRelativeImgIdWithGender(
                    viewModelProfile.relationshipCode, userGender
                )
            )
            binding.imgUserPicBanner.setImageResource(R.drawable.btn_fill_round)
        }/*        if ( profPicBitmap != null ) {
                    binding.imgUserPic.setImageBitmap(profPicBitmap)
                } else if ( !Utilities.isNullOrEmpty(userGender) ) {
                    binding.imgUserPic.setImageResource(Utilities.getRelativeImgIdWithGender("",userGender))
                }*/

        startImageShimmer()
        //viewModelProfile.getAllUserRelatives()
        viewModelProfile.callGetUserDetailsApi()
        //viewModel.getLoggedInPersonDetails()

        /*   if ( viewModel.getOtpAuthenticatedStatus() ) {
                    binding.layoutPolicy.visibility = View.VISIBLE
                    startShimmer()
                    viewModelProfile.callSudPolicyByMobileNumberApi(this)
                } else {
                    binding.layoutPolicy.visibility = View.GONE
                }*/
    }

    private fun initialise() {
        //binding.layoutPolicy.visibility = View.GONE
        sudPolicyListAdapter = SudPolicyListAdapter(requireContext(), this)
        binding.rvDashboardPolicy.adapter = sudPolicyListAdapter
        binding.rvFamilyMember.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        familyMembersAdapter =
            RvpFamilyMemberListAdapter(object : RvpFamilyMemberListAdapter.OnItemClickListener {

                override fun onItemClick(position: Int, item: UserRelatives) {
                    if (item.relationshipCode == "ADD") {
//                        openAnotherActivity(destination = NavigationConstants.FAMILY_PROFILE)
                        startActivity(Intent(requireContext(), FamilyProfileActivity::class.java))
                    } else if (item.relationshipCode != "SELF" && item.relativeID != viewModelProfile.personId) {
                        showEditProfileDialog(position, item)
                    } else if (item.relationshipCode == "SELF" && item.relativeID != viewModelProfile.personId) {
                        changeFamilyProfile(item)
                    }
                }
            }, requireContext(), viewModelProfile)
        binding.rvFamilyMember.adapter = familyMembersAdapter

        /*        (activity as HomeMainActivity).registerSwitchProfileListener(object : HomeMainActivity.OnSwitchProfileClickListener{
                    override fun onClick() {
                        Utilities.printLog("SwitchProfile=> ")
                        showSwitchProfileDialog(familyList.toMutableList())
                    }

                })*/

    }

    private fun setObserver() {

        /*        viewModel.userDetails.observe(viewLifecycleOwner) {
                    if (it.status == Resource.Status.SUCCESS) {
                        if ( it.isOtpAuthenticated ) {
                            binding.layoutPolicy.visibility = View.VISIBLE
                            startShimmer()
                            viewModelProfile.callSudPolicyByMobileNumberApi(viewModel.phone,this)
                            //viewModelProfile.callSudPolicyByMobileNumberApi("9094697380",this)
                        } else {
                            binding.layoutPolicy.visibility = View.GONE
                        }
                    }
                }*/

        viewModelProfile.userProfileDetails.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {
                //val person = it.data!!.person
                if (!Utilities.isNullOrEmptyOrZero(it.data!!.person.profileImageID.toString())) {
                    hasProfileImage = true
                    val permissionResult = permissionUtil.checkStoragePermission(object :
                        PermissionUtil.AppPermissionListener {
                        override fun isPermissionGranted(isGranted: Boolean) {
                            Utilities.printLogError("$isGranted")
                            if (isGranted) {
                                viewModelProfile.callGetProfileImageApiMain(
                                    this@FragmentProfile, it.data.person.profileImageID.toString()
                                )
                            } else {
                                stopImageShimmer()
                                binding.imgUserPic.setImageResource(
                                    Utilities.getRelativeImgIdWithGender(
                                        "", it.data.person.gender.toString()
                                    )
                                )
                                binding.imgUserPicBanner.setImageResource(R.drawable.btn_fill_round)
                            }
                        }
                    }, requireContext())
                    if (permissionResult) {
                        viewModelProfile.callGetProfileImageApiMain(
                            this@FragmentProfile, it.data.person.profileImageID.toString()
                        )
                    }
                } else {
                    stopImageShimmer()
                    binding.imgUserPic.setImageResource(
                        Utilities.getRelativeImgIdWithGender(
                            "", it.data.person.gender.toString()
                        )
                    )
                    if (it.data.person.id.toString() != viewModelProfile.adminPersonId) {
                        binding.imgUserPic.setImageResource(
                            Utilities.getRelativeImgIdWithGender(
                                viewModelProfile.relationshipCode, viewModelProfile.gender
                            )
                        )
                    }
                    binding.imgUserPicBanner.setImageResource(R.drawable.btn_fill_round)
                }
                setUserDetails(it.data.person)
            }
        }

        viewModelProfile.profileImage.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {
                val document = it.data!!.healthRelatedDocument
                val fileName = document.fileName
                val fileBytes = document.fileBytes
                try {
                    val path = Utilities.getAppFolderLocation(requireContext())
                    if (!File(path, fileName).exists()) {
                        if (!Utilities.isNullOrEmpty(fileBytes)) {
                            val decodedImage = fileUtils.convertBase64ToBitmap(fileBytes)
                            if (decodedImage != null) {
                                val saveRecordUri = fileUtils.saveBitmapToExternalStorage(
                                    requireContext(), decodedImage, fileName
                                )
                                if (saveRecordUri != null) {
                                    viewModelProfile.updateUserProfileImgPath(fileName, path)
                                }
                            }
                        }
                    } else {
                        viewModelProfile.updateUserProfileImgPath(fileName, path)
                    }
                    completeFilePath = "$path/$fileName"
                    setProfilePic()
                    stopImageShimmer()
                    familyMembersAdapter!!.notifyDataSetChanged()
                } catch (e: Exception) {
                    e.printStackTrace()
                    stopImageShimmer()
                }
            }
        }

        viewModelProfile.sudPolicyByMobileNumber.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {
                policyListSize = 0
                clearPolicyList()
            }
        }

        viewModelProfile.removeRelative.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {
                viewModelProfile.getAllUserRelatives()
            }
        }

        /*        viewModelProfile.listRelatives.observe(viewLifecycleOwner) {
                    Utilities.printLog("RelativeData--->$it")
                    if (it != null && !it.data!!.relativeList.isNullOrEmpty()) {
                        familyList.clear()
                        familyList.addAll(it.data!!.relativeList.toMutableList())
                        familyMembersAdapter!!.updateRelationshipCode(viewModel.relationshipCode)
                    }
                }*/

        viewModelProfile.userRelativesList.observe(viewLifecycleOwner) {
            it?.let {
                if (familyMembersAdapter != null) {
                    familyMembersAdapter!!.updateFamilyMembersList(it)
                }
            }

        }

        backGroundCallViewModel.stepsHistoryList.observe(viewLifecycleOwner) {
            if (it.status == Resource.Status.SUCCESS) {
                Utilities.printLog("stepsHistoryList--->$it")
            }
        }

    }

    private fun setClickable() {

        binding.imgSwitchProfile.setOnClickListener {
            Utilities.printLog("SwitchProfile=> ")
            showSwitchProfileDialog(familyList.toMutableList())
        }

        binding.imgUserPic.setOnClickListener {
            binding.imgEditPic.performClick()
        }

        binding.imgEditPic.setOnClickListener {
            val permissionResult = permissionUtil.checkStoragePermission(object :
                PermissionUtil.AppPermissionListener {
                override fun isPermissionGranted(isGranted: Boolean) {
                    Utilities.printLogError("$isGranted")
                    if (isGranted) {
                        if (viewModel.isSelfUser()) {
//                            viewModelProfile.navigate(FragmentHomeMainDirections.actionDashboardFragmentToEditProfileActivity())
//                            openAnotherActivity(destination = NavigationConstants.EDIT_PROFILE_ACTIVITY)
                            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
                        } else {
                            val relative = getRelativeObject(viewModel.personId)
                            navigateToEditFamilyProfileActivity(
                                relative.relativeID,
                                relative.relationShipID,
                                relative.relationshipCode,
                                relative.relationship
                            )
                        }
                    }
                }
            }, requireContext())
            if (permissionResult) {
                if (viewModel.isSelfUser()) {
//                    openAnotherActivity(destination = NavigationConstants.EDIT_PROFILE_ACTIVITY)
                    startActivity(Intent(requireContext(), EditProfileActivity::class.java))
                } else {
                    val relative = getRelativeObject(viewModel.personId)
                    //Utilities.printLog("relativeDetails=> "+relative)
                    navigateToEditFamilyProfileActivity(
                        relative.relativeID,
                        relative.relationShipID,
                        relative.relationshipCode,
                        relative.relationship
                    )
                }
            }
        }

    }

    private fun navigateToEditFamilyProfileActivity(
        relativeId: String, relationShipId: String, relationCode: String, relation: String
    ) {
        /*openAnotherActivity(destination = NavigationConstants.WELLFIE_SCREEN) {
            putString(Constants.RELATIVE_ID, relativeId)
            putString(Constants.RELATION_SHIP_ID, relationShipId)
            putString(Constants.RELATION_CODE, relationCode)
            putString(Constants.RELATION, relation)
        }*/
        val intent = Intent(requireContext(), WellfieActivity::class.java)
        intent.putExtra(Constants.RELATIVE_ID, relativeId)
        intent.putExtra(Constants.RELATION_SHIP_ID, relationShipId)
        intent.putExtra(Constants.RELATION_CODE, relationCode)
        intent.putExtra(Constants.RELATION, relation)
        startActivity(intent)
    }

    private fun getRelativeObject(personId: String): UserRelatives {
        for (item in familyList) {
            if (item.relativeID.equals(personId, true)) {
                return item
            }
        }
        return UserRelatives()
    }

    @SuppressLint("SetTextI18n")
    private fun showEditProfileDialog(position: Int, item: UserRelatives) {
        try {
            val dialog = Dialog(requireContext())
            val dialogBinding = DialogEditProfileBinding.inflate(layoutInflater)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(dialogBinding.root)
            //dialog.setContentView(R.layout.dialog_edit_profile)
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )

            val relativeImgId =
                Utilities.getRelativeImgIdWithGender(item.relationshipCode, item.gender)
            dialogBinding.imgUser.setImageResource(relativeImgId)
            dialogBinding.txtName.text = item.firstName
            dialogBinding.txtDOB.text = item.dateOfBirth
            dialogBinding.txtEmail.text = item.emailAddress
            dialogBinding.txtRelation.text = item.relationship
            dialogBinding.txtPhone.text = item.contactNo
            dialogBinding.dialogBtnSwitch.text =
                "${resources.getString(R.string.SWITCH_TO)} ${item.firstName}"

            if (DateHelper.isDateAbove18Years(item.dateOfBirth)) {
                dialogBinding.dialogBtnSwitch.visibility = View.VISIBLE
            } else {
                dialogBinding.dialogBtnSwitch.visibility = View.GONE
            }

            dialog.show()

            dialogBinding.imgClose.setOnClickListener {
                dialog.dismiss()
            }

            dialogBinding.dialogBtnEdit.setOnClickListener {
                dialog.dismiss()
                navigateToEditFamilyProfileActivity(
                    item.relativeID, item.relationShipID, item.relationshipCode, item.relationship
                )
            }

            dialogBinding.dialogBtnDelete.setOnClickListener {
                dialog.dismiss()
                deleteFamilyMember(item)
            }

            dialogBinding.dialogBtnSwitch.setOnClickListener {
                dialog.dismiss()
                changeFamilyProfile(item)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showSwitchProfileDialog(list: MutableList<UserRelatives>) {
        try {
            var relative = UserRelatives()
            val dialog = Dialog(requireContext())
            val dialogBinding = DialogSwitchProfileBinding.inflate(layoutInflater)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(dialogBinding.root)
            //dialog.setContentView(R.layout.dialog_switch_profile)
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )

            var selectedPos = -1
            for (i in list.indices) {
                if (list[i].relativeID == viewModelProfile.personId) {
                    selectedPos = i
                }
            }

            val adapter = SwitchProfileAdapter(
                selectedPos, object : SwitchProfileAdapter.OnItemClickListener {
                    override fun onItemClick(user: UserRelatives) {
                        relative = user
                        dialogBinding.dialogBtnSwitch.isEnabled =
                            relative.relativeID != viewModelProfile.personId
                    }
                }, requireContext(), viewModelProfile
            )
            dialogBinding.recyclerView.adapter = adapter
            adapter.updateFamilyMembersList(list)
            dialogBinding.dialogBtnSwitch.isEnabled = false
            dialog.show()

            dialogBinding.imgClose.setOnClickListener {
                dialog.dismiss()
            }

            dialogBinding.dialogBtnSwitch.setOnClickListener {
                dialog.dismiss()
                Utilities.printData("relative", relative, true)
                changeFamilyProfile(relative)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteFamilyMember(userRelative: UserRelatives) {
        val dialogData = DefaultNotificationDialog.DialogData()
        dialogData.title = resources.getString(R.string.DELETE_FAMILY_MEMBER)
        dialogData.message = resources.getString(R.string.MSG_DELETE_MEMBER_CONFIRMATION)
        dialogData.btnLeftName = resources.getString(R.string.CANCEL)
        dialogData.btnRightName = resources.getString(R.string.CONFIRM)
        dialogData.hasErrorBtn = true
        val defaultNotificationDialog = DefaultNotificationDialog(
            activity, object : DefaultNotificationDialog.OnDialogValueListener {

                override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
                    if (isButtonRight) {
                        viewModelProfile.callRemoveRelativesApiNew(
                            true, userRelative.relativeID, userRelative.relationShipID
                        )
                    }
                }
            }, dialogData
        )
        defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        defaultNotificationDialog.show()
    }

    private fun changeFamilyProfile(userRelative: UserRelatives) {
        val dialogData = DefaultNotificationDialog.DialogData()
        dialogData.title = resources.getString(R.string.SWITCH_PROFILE)
        dialogData.message =
            resources.getString(R.string.MSG_SWITCH_PROFILE_CONFIRMATION1) + " " + userRelative.firstName + "." + resources.getString(
                R.string.MSG_SWITCH_PROFILE_CONFIRMATION2
            )
        dialogData.btnLeftName = resources.getString(R.string.NO)
        dialogData.btnRightName = resources.getString(R.string.CONFIRM)
        val defaultNotificationDialog = DefaultNotificationDialog(
            activity, object : DefaultNotificationDialog.OnDialogValueListener {

                override fun onDialogClickListener(isButtonLeft: Boolean, isButtonRight: Boolean) {
                    if (isButtonRight) {
                        val person = Person(
                            id = userRelative.relativeID.toInt(),
                            firstName = userRelative.firstName,
                            dateOfBirth = userRelative.dateOfBirth,
                            age = userRelative.age.toInt(),
                            gender = userRelative.gender.toInt(),
                            contact = Contact(emailAddress = userRelative.emailAddress)
                        )
                        setUserDetails(person)

                        binding.imgUserPic.setImageResource(
                            Utilities.getRelativeImgIdWithGender(
                                userRelative.relationshipCode, userRelative.gender
                            )
                        )
                        binding.imgUserPicBanner.setImageResource(R.drawable.btn_fill_round)
                        val bitmap = UserSingleton.getInstance()!!.profPicBitmap
                        if (userRelative.relativeID == viewModelProfile.adminPersonId && bitmap != null) {
                            binding.imgUserPic.setImageBitmap(bitmap)
                            binding.imgUserPicBanner.setImageBitmap(bitmap)
                            blurBanner(bitmap)

                        }
                        // RefreshView
                        viewModel.switchProfile(userRelative)
                        backGroundCallViewModel.refreshPersonId()
                        viewModel.refreshPersonId()
                        viewModelProfile.refreshPersonId()
                        backGroundCallViewModel.isBackgroundApiCall = false
                        backGroundCallViewModel.profileSwitched = true
                        backGroundCallViewModel.callBackgroundApiCall(true)
                        viewModelProfile.callListRelativesApi(true)
                        //viewModelProfile.callListDocumentsApi(true,Constants.SWITCH_PROFILE)
                    }
                }
            }, dialogData
        )
        defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        defaultNotificationDialog.show()
    }

    private fun setUserDetails(user: Person) {
        try {
            if (!Utilities.isNullOrEmptyOrZero(user.id.toString())) {
                val firstName = user.firstName
                val email = user.contact.emailAddress
                var dateOfBirth = user.dateOfBirth
                //val age = user.age
                val gender = user.gender

                if (!Utilities.isNullOrEmpty(firstName)) {
                    binding.txtName.text = firstName
                }

                if (!Utilities.isNullOrEmpty(email)) {
                    binding.txtEmail.text = email
                }

                if (!Utilities.isNullOrEmpty(dateOfBirth)) {
                    dateOfBirth = DateHelper.formatDateValue(DateHelper.DISPLAY_DATE_DDMMMYYYY, dateOfBirth)!!
                    Utilities.printLogError("DOB--->$dateOfBirth")
                    if (!Utilities.isNullOrEmpty(dateOfBirth)) {
                        val calculatedAge: String = DateHelper.calculatePersonAge(dateOfBirth, requireContext())
                        Utilities.printLogError("calculatedAge--->$calculatedAge")
                        strAgeGender = calculatedAge
                        /*strAgeGender = if (!Utilities.isNullOrEmptyOrZero(age.toString())) {
                                                    "$age Yrs"
                                                } else {
                                                    calculatedAge
                                                }*/
                        binding.txtDOB.text = DateHelper.formatDateValue(DateHelper.DATEFORMAT_DDMMMYYYY_NEW, dateOfBirth)!!
                        binding.txtAge.text = strAgeGender
                    }
                } else {
                    binding.txtDOB.text = " -- "
                    binding.txtAge.text = " -- "
                }

                when (gender) {
                    1 -> {
                        binding.txtGender.text = resources.getString(R.string.MALE)
                    }

                    2 -> {
                        binding.txtGender.text = resources.getString(R.string.FEMALE)
                    }
                }
                userGender = gender.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setProfilePic() {
        try {
            Utilities.printLog("completeFilePath----->$completeFilePath")
            Utilities.printLog("needToSet----->$needToSet")
            if (!Utilities.isNullOrEmpty(completeFilePath)) {
                val bitmap = BitmapFactory.decodeFile(completeFilePath)
                if (bitmap != null) {
                    binding.imgUserPic.setImageBitmap(bitmap)
                    binding.imgUserPicBanner.setImageBitmap(bitmap)
                    blurBanner(bitmap)
                    //profPicBitmap = bitmap
                    UserSingleton.getInstance()!!.profPicBitmap = bitmap
                    //needToSet = false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*    fun setProfilePic() {
            try {
                Utilities.printLogError("completeFilePath----->$completeFilePath")
                Utilities.printLogError("needToSet----->$needToSet")
                if ( needToSet  ) {
                    if (!Utilities.isNullOrEmpty(completeFilePath)) {
                        val bitmap = BitmapFactory.decodeFile(completeFilePath)
                        if (bitmap != null) {
                            binding.imgUserPic.setImageBitmap(bitmap)
                            binding.imgUserPicBanner.setImageBitmap(bitmap)
                            blurBanner(bitmap)
                            //profPicBitmap = bitmap
                            HomeSingleton.getInstance()!!.profPicBitmap = bitmap
                            needToSet = false
                        }
                    }
                }
            } catch ( e :Exception ) {
                e.printStackTrace()
            }
        }*/

    private fun startImageShimmer() {
        binding.layoutImgShimmer.startShimmer()
        binding.layoutImgShimmer.visibility = View.VISIBLE
        binding.layoutUserPic.visibility = View.GONE
        binding.imgUserPicBanner.setImageResource(R.drawable.btn_fill_round)
    }

    fun stopImageShimmer() {
        binding.layoutImgShimmer.stopShimmer()
        binding.layoutImgShimmer.visibility = View.GONE
        binding.layoutUserPic.visibility = View.VISIBLE
    }

    fun blurBanner(bitmap: Bitmap) {
        Blurry.with(context).from(bitmap).into(binding.imgUserPicBanner)
    }

    fun addPolicyInList(policy: SudKYPModel.KYP) {
        if (!sudPolicyList.contains(policy)) {
            sudPolicyList.add(policy)
            policyListSize++
        }
        Utilities.printLogError("policyListSizeFinal--->$policyListSizeFinal")
        Utilities.printLogError("policyListSize--->$policyListSize")
        if (policyListSizeFinal == policyListSize) {
            notifyList()
        }
    }

    fun notifyList() {
        sudPolicyList.sortBy { it.policyNumber }
        sudPolicyListAdapter!!.updateList(sudPolicyList)
        binding.layoutPolicy.visibility = View.VISIBLE
        stopShimmer()
    }

    private fun clearPolicyList() {
        sudPolicyList.clear()
        sudPolicyListAdapter!!.updateList(sudPolicyList)
    }

    fun showNoDataView() {
        binding.layoutPolicy.visibility = View.GONE
        stopShimmer()
    }

    private fun startShimmer() {
        binding.layoutDashboardPolicyShimmer.startShimmer()
        binding.layoutDashboardPolicyShimmer.visibility = View.VISIBLE
    }

    private fun stopShimmer() {
        binding.layoutDashboardPolicyShimmer.stopShimmer()
        binding.layoutDashboardPolicyShimmer.visibility = View.GONE
    }

    override fun onPolicyClick(policyDetails: SudKYPModel.KYP) {
        policyDataSingleton = PolicyDataSingleton.getInstance()!!
        policyDataSingleton!!.kypDetails = policyDetails
        /*openAnotherActivity(destination = NavigationConstants.SUD_LIFE_POLICY) {
            putString(Constants.FROM, Constants.PROFILE)
        }*/
        val intent = Intent(requireContext(), SudLifePolicyActivity::class.java)
        intent.putExtra(Constants.FROM, Constants.PROFILE)
        startActivity(intent)


    }

}
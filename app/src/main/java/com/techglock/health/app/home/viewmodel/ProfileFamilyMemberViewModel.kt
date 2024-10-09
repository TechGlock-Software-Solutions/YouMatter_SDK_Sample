package com.techglock.health.app.home.viewmodel


import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.PreferenceConstants
import com.techglock.health.app.common.utils.*
import com.techglock.health.app.home.common.DataHandler
import com.techglock.health.app.home.common.SudPolicyApiService
import com.techglock.health.app.home.domain.HomeManagementUseCase
import com.techglock.health.app.home.domain.SudLifePolicyManagementUseCase
import com.techglock.health.app.home.ui.FragmentProfile
import com.techglock.health.app.home.ui.ProfileAndFamilyMember.*
import com.techglock.health.app.model.entity.HealthDocument
import com.techglock.health.app.model.entity.UserRelatives
import com.techglock.health.app.model.entity.Users
import com.techglock.health.app.model.home.*
import com.techglock.health.app.model.home.AddRelativeModel.Relationship
import com.techglock.health.app.model.security.PhoneExistsModel
import com.techglock.health.app.model.shr.ListDocumentsModel
import com.techglock.health.app.model.shr.ListRelativesModel
import com.techglock.health.app.model.sudLifePolicy.SudKYPModel
import com.techglock.health.app.model.sudLifePolicy.SudPolicyByMobileNumberModel
import com.techglock.health.app.repository.utils.Resource
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileInputStream
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class ProfileFamilyMemberViewModel @Inject constructor(
    application: Application,
    private val homeManagementUseCase: HomeManagementUseCase,
    private val sudLifePolicyManagementUseCase: SudLifePolicyManagementUseCase,
    private val preferenceUtils: PreferenceUtils,
    private val dataHandler: DataHandler,
    val context: Context?
) : BaseViewModel(application) {

    var adminPersonId = preferenceUtils.getPreference(PreferenceConstants.ADMIN_PERSON_ID, "0")
    var personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
    var gender = preferenceUtils.getPreference(PreferenceConstants.GENDER, "")
    var relationshipCode = preferenceUtils.getPreference(PreferenceConstants.RELATIONSHIPCODE, "")
    var authToken = preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")

    private val localResource =
        LocaleHelper.getLocalizedResources(context!!, Locale(LocaleHelper.getLanguage(context)))!!

    private val fileUtils = FileUtils

    private var relativeToRemove: List<UserRelatives> = listOf()

    var userDetails = MutableLiveData<Users>()
    val userRelativesList = MutableLiveData<List<UserRelatives>>()
    val alreadyExistRelatives = MutableLiveData<List<UserRelatives>>()
    var familyRelationList = MutableLiveData<List<DataHandler.FamilyRelationOption>>()
    val allHealthDocuments = MutableLiveData<List<HealthDocument>>()

    private var userProfileDetailsSource: LiveData<Resource<UserDetailsModel.UserDetailsResponse>> =
        MutableLiveData()
    private val _userProfileDetails =
        MediatorLiveData<Resource<UserDetailsModel.UserDetailsResponse>>()
    val userProfileDetails: LiveData<Resource<UserDetailsModel.UserDetailsResponse>> get() = _userProfileDetails

    private var userProfileDetailsEditSource: LiveData<Resource<UserDetailsModel.UserDetailsResponse>> =
        MutableLiveData()
    private val _userProfileDetailsEdit =
        MediatorLiveData<Resource<UserDetailsModel.UserDetailsResponse>>()
    val userProfileDetailsEdit: LiveData<Resource<UserDetailsModel.UserDetailsResponse>> get() = _userProfileDetailsEdit

    private var updateUserDetailsSource: LiveData<Resource<UpdateUserDetailsModel.UpdateUserDetailsResponse>> =
        MutableLiveData()
    private val _updateUserDetails =
        MediatorLiveData<Resource<UpdateUserDetailsModel.UpdateUserDetailsResponse>>()
    val updateUserDetails: LiveData<Resource<UpdateUserDetailsModel.UpdateUserDetailsResponse>> get() = _updateUserDetails

    private var profileImageSource: LiveData<Resource<ProfileImageModel.ProfileImageResponse>> =
        MutableLiveData()
    private val _profileImage = MediatorLiveData<Resource<ProfileImageModel.ProfileImageResponse>>()
    val profileImage: LiveData<Resource<ProfileImageModel.ProfileImageResponse>> get() = _profileImage

    private var uploadProfileImageSource: LiveData<Resource<UploadProfileImageResponce>> =
        MutableLiveData()
    private val _uploadProfileImage = MediatorLiveData<Resource<UploadProfileImageResponce>>()
    val uploadProfileImage: LiveData<Resource<UploadProfileImageResponce>> get() = _uploadProfileImage

    private var removeProfileImageSource: LiveData<Resource<RemoveProfileImageModel.RemoveProfileImageResponse>> =
        MutableLiveData()
    private val _removeProfileImage =
        MediatorLiveData<Resource<RemoveProfileImageModel.RemoveProfileImageResponse>>()
    val removeProfileImage: LiveData<Resource<RemoveProfileImageModel.RemoveProfileImageResponse>> get() = _removeProfileImage

    private var addRelativeSource: LiveData<Resource<AddRelativeModel.AddRelativeResponse>> =
        MutableLiveData()
    private val _addRelative = MediatorLiveData<Resource<AddRelativeModel.AddRelativeResponse>>()
    val addRelative: LiveData<Resource<AddRelativeModel.AddRelativeResponse>> get() = _addRelative

    private var updateRelativeSource: LiveData<Resource<UpdateRelativeModel.UpdateRelativeResponse>> =
        MutableLiveData()
    private val _updateRelative =
        MediatorLiveData<Resource<UpdateRelativeModel.UpdateRelativeResponse>>()
    val updateRelative: LiveData<Resource<UpdateRelativeModel.UpdateRelativeResponse>> get() = _updateRelative

    private var removeRelativeSource: LiveData<Resource<RemoveRelativeModel.RemoveRelativeResponse>> =
        MutableLiveData()
    private val _removeRelative =
        MediatorLiveData<Resource<RemoveRelativeModel.RemoveRelativeResponse>>()
    val removeRelative: LiveData<Resource<RemoveRelativeModel.RemoveRelativeResponse>> get() = _removeRelative

    private var listRelativesSource: LiveData<Resource<ListRelativesModel.ListRelativesResponse>> =
        MutableLiveData()
    private val _listRelatives =
        MediatorLiveData<Resource<ListRelativesModel.ListRelativesResponse>>()
    val listRelatives: LiveData<Resource<ListRelativesModel.ListRelativesResponse>> get() = _listRelatives

    private var listDocumentsSource: LiveData<Resource<ListDocumentsModel.ListDocumentsResponse>> =
        MutableLiveData()
    private val _listDocuments =
        MediatorLiveData<Resource<ListDocumentsModel.ListDocumentsResponse>>()
    val listRecordDocuments: LiveData<Resource<ListDocumentsModel.ListDocumentsResponse>> get() = _listDocuments

    private var phoneExistSource: LiveData<Resource<PhoneExistsModel.IsExistResponse>> =
        MutableLiveData()
    private val _phoneExist = MediatorLiveData<Resource<PhoneExistsModel.IsExistResponse>>()
    val phoneExist: LiveData<Resource<PhoneExistsModel.IsExistResponse>> get() = _phoneExist

    private var sudPolicyByMobileNumberSource: LiveData<Resource<SudPolicyByMobileNumberModel.SudPolicyByMobileNumberResponse>> =
        MutableLiveData()
    private val _sudPolicyByMobileNumber =
        MediatorLiveData<Resource<SudPolicyByMobileNumberModel.SudPolicyByMobileNumberResponse>>()
    val sudPolicyByMobileNumber: LiveData<Resource<SudPolicyByMobileNumberModel.SudPolicyByMobileNumberResponse>> get() = _sudPolicyByMobileNumber

    fun isSelfUser(): Boolean {
        val personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
        val adminPersonId = preferenceUtils.getPreference(PreferenceConstants.ADMIN_PERSON_ID, "0")
        var isSelfUser = false
        if (!Utilities.isNullOrEmptyOrZero(personId)
            && !Utilities.isNullOrEmptyOrZero(adminPersonId)
            && personId == adminPersonId
        ) {
            isSelfUser = true
        }
        return isSelfUser
    }

    fun callAddNewRelativeApi(
        forceRefresh: Boolean,
        userRelative: UserRelatives,
        from: String,
        fragment: AddFamilyMemberFragment
    ) =
        viewModelScope.launch(Dispatchers.Main) {

            val contact =
                AddRelativeModel.Contact(userRelative.emailAddress, userRelative.contactNo)
            val relationships: ArrayList<Relationship> = ArrayList()
            relationships.add(
                Relationship(
                    preferenceUtils.getPreference(
                        PreferenceConstants.PERSONID,
                        "0"
                    ), userRelative.relationshipCode
                )
            )
            var gender = ""
            if (userRelative.gender.equals("Male", ignoreCase = true)) {
                gender = "1"
            } else if (userRelative.gender.equals("Female", ignoreCase = true)) {
                gender = "2"
            }

            val requestData = AddRelativeModel(
                Gson().toJson(
                    AddRelativeModel.JSONDataRequest(
                        personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0"),
                        person = AddRelativeModel.Person(
                            firstName = userRelative.firstName,
                            relativeID = userRelative.relativeID,
                            dateOfBirth = userRelative.dateOfBirth,
                            gender = gender,
                            isProfileImageChanges = Constants.FALSE,
                            contact = contact,
                            relationships = relationships
                        )
                    ), AddRelativeModel.JSONDataRequest::class.java
                ), authToken
            )

            _progressBar.value = Event("Adding Family Member.....")
            _addRelative.removeSource(addRelativeSource)
            withContext(Dispatchers.IO) {
                addRelativeSource = homeManagementUseCase.invokeaddNewRelative(
                    isForceRefresh = forceRefresh,
                    data = requestData
                )
            }
            _addRelative.addSource(addRelativeSource) {
                _addRelative.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
//                    FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.FAMILY_MEMBER_ADD_EVENT)
                }
                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.errorNumber.equals("1100014", true)) {
                        _sessionError.value = Event(true)
                    } else {
                        toastMessage(it.errorMessage)
                    }
                }
            }

        }

    fun callListRelativesApi(forceRefresh: Boolean) =
        viewModelScope.launch(Dispatchers.Main) {

            val requestData = ListRelativesModel(
                Gson().toJson(
                    ListRelativesModel.JSONDataRequest(
                        personID = adminPersonId
                    ), ListRelativesModel.JSONDataRequest::class.java
                ), authToken
            )

            _progressBar.value = Event("Getting Relatives...")
            _listRelatives.removeSource(listRelativesSource)
            withContext(Dispatchers.IO) {
                listRelativesSource = homeManagementUseCase.invokeRelativesList(
                    isForceRefresh = forceRefresh,
                    data = requestData
                )
            }
            _listRelatives.addSource(listRelativesSource) {
                _listRelatives.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data != null) {
                        val relativesList = it.data!!.relativeList
                        if (relativesList.size > 1) {
                            val userRelatives: MutableList<UserRelatives> = mutableListOf()
                            for (i in relativesList) {
                                userRelatives.add(i)
                                /*                                if (i.relationshipCode != "SELF") {
                                                                    userRelatives.add(i)
                                                                }*/
                            }
                            userRelativesList.postValue(userRelatives)
                        } else {
//                            fragment.noDataView()
                        }
                        Utilities.printLog("RelativesList----->${relativesList.size}")
                    }
                }
                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.errorNumber.equals("1100014", true)) {
                        _sessionError.value = Event(true)
                    } else {
                        toastMessage(it.errorMessage)
                    }
                }
            }
        }

    fun callRemoveRelativesApiNew(
        forceRefresh: Boolean,
        relativeId: String,
        relationshipId: String
    ) =
        viewModelScope.launch(Dispatchers.Main) {

            val relatives: ArrayList<Int> = ArrayList()
            withContext(Dispatchers.IO) {
                relativeToRemove =
                    homeManagementUseCase.invokeGetUserRelativeForRelativeId(relativeId)
            }

            for (i in relativeToRemove) {
                relatives.add(relationshipId.toInt())
            }

            val requestData = RemoveRelativeModel(
                Gson().toJson(
                    RemoveRelativeModel.JSONDataRequest(
                        id = relatives
                    ), RemoveRelativeModel.JSONDataRequest::class.java
                ), authToken
            )

            _progressBar.value = Event(Constants.LOADER_DELETE)
            _removeRelative.removeSource(removeRelativeSource)
            withContext(Dispatchers.IO) {
                removeRelativeSource = homeManagementUseCase.invokeRemoveRelative(
                    isForceRefresh = forceRefresh,
                    data = requestData,
                    relativeId = relativeId
                )
            }
            _removeRelative.addSource(removeRelativeSource) {
                _removeRelative.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    toastMessage(localResource.getString(R.string.MEMBER_DELETED))
                }
                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.errorNumber.equals("1100014", true)) {
                        _sessionError.value = Event(true)
                    } else {
                        toastMessage(it.errorMessage)
                    }
                }
            }
        }

    fun callCheckPhoneExistApi(username: String, phone: String) =
        viewModelScope.launch(Dispatchers.Main) {

            val requestData = PhoneExistsModel(
                Gson().toJson(
                    PhoneExistsModel.JSONDataRequest(
                        primaryPhone = phone
                    ), PhoneExistsModel.JSONDataRequest::class.java
                )
            )

            _progressBar.value = Event("")
            _phoneExist.removeSource(phoneExistSource)
            withContext(Dispatchers.IO) {
                phoneExistSource = homeManagementUseCase.invokePhoneExist(true, requestData)
            }
            _phoneExist.addSource(phoneExistSource) {
                _phoneExist.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data!!.isExist.equals(Constants.TRUE, true)) {
                        toastMessage(localResource.getString(R.string.ERROR_MOBILE_ALREADY_REGISTERED))
                    } else if (it.data!!.isExist.equals(Constants.FALSE, true)) {

                    }
                }

                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.errorNumber.equals("1100014", true)) {
                        _sessionError.value = Event(true)
                    } else {
                        toastMessage(it.errorMessage)
                    }
                }
            }
        }

    fun callUpdateRelativesApi(forceRefresh: Boolean, relative: UserRelatives, from: String) =
        viewModelScope.launch(Dispatchers.Main) {

            val requestData = UpdateRelativeModel(
                Gson().toJson(
                    UpdateRelativeModel.JSONDataRequest(
                        personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0"),
                        person = UpdateRelativeModel.Person(
                            id = relative.relativeID.toInt(),
                            firstName = relative.firstName,
                            lastName = "",
                            dateOfBirth = relative.dateOfBirth,
                            gender = relative.gender,
                            isProfileImageChanges = Constants.FALSE,
                            contact = UpdateRelativeModel.Contact(
                                emailAddress = relative.emailAddress,
                                primaryContactNo = relative.contactNo
                            )
                        )
                    ), UpdateRelativeModel.JSONDataRequest::class.java
                ), authToken
            )

            _progressBar.value = Event("Updating Relative Profile.....")
            _updateRelative.removeSource(updateRelativeSource)
            withContext(Dispatchers.IO) {
                updateRelativeSource = homeManagementUseCase.invokeupdateRelative(
                    isForceRefresh = forceRefresh,
                    data = requestData,
                    relativeId = relative.relativeID
                )
            }
            _updateRelative.addSource(updateRelativeSource) {
                _updateRelative.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it != null) {
                        val personDetails = it.data!!.person
                        if (!Utilities.isNullOrEmpty(personDetails.id.toString())) {
                            //toastMessage(context.resources.getString(R.string.PROFILE_UPDATED))
                            //navigate(EditFamilyMemberDetailsFragmentDirections.actionEditFamilyMemberDetailsFragmentToFamilyMembersListFragment())
                        }
                    }
                }
                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.errorNumber.equals("1100014", true)) {
                        _sessionError.value = Event(true)
                    } else {
                        toastMessage(it.errorMessage)
                        if (from == Constants.RELATIVE) {
                            //navigate(EditFamilyMemberDetailsFragmentDirections.actionEditFamilyMemberDetailsFragmentToFamilyMembersListFragment())
                        }
                    }
                }
            }
        }

    fun callGetUserDetailsEditApi() = viewModelScope.launch(Dispatchers.Main) {

        val requestData = UserDetailsModel(
            Gson().toJson(
                UserDetailsModel.JSONDataRequest(
                    UserDetailsModel.PersonIdentificationCriteria(
                        personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
                            .toInt()
                    )
                ), UserDetailsModel.JSONDataRequest::class.java
            ), authToken
        )

        _userProfileDetailsEdit.removeSource(userProfileDetailsEditSource)
        withContext(Dispatchers.IO) {
            userProfileDetailsEditSource = homeManagementUseCase.invokeGetUserDetails(
                isForceRefresh = true,
                data = requestData
            )
        }
        _userProfileDetailsEdit.addSource(userProfileDetailsEditSource) {
            _userProfileDetailsEdit.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.data != null) {
                    val person = it.data!!.person
                    Utilities.printLog("GetUserDetails----->$person")
                }
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.errorNumber.equals("1100014", true)) {
                    _sessionError.value = Event(true)
                } else {
                    toastMessage(it.errorMessage)
                }
            }
        }
    }

    fun callGetUserDetailsApi() = viewModelScope.launch(Dispatchers.Main) {

        val requestData = UserDetailsModel(
            Gson().toJson(
                UserDetailsModel.JSONDataRequest(
                    UserDetailsModel.PersonIdentificationCriteria(
                        personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
                            .toInt()
                    )
                ), UserDetailsModel.JSONDataRequest::class.java
            ), authToken
        )

        _userProfileDetails.removeSource(userProfileDetailsSource)
        withContext(Dispatchers.IO) {
            userProfileDetailsSource = homeManagementUseCase.invokeGetUserDetails(
                isForceRefresh = true,
                data = requestData
            )
        }
        _userProfileDetails.addSource(userProfileDetailsSource) {
            _userProfileDetails.value = it

            if (it.status == Resource.Status.SUCCESS) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                /*                if (it.data != null) {
                                    val person = it.data!!.person
                                    Utilities.printLog("GetUserDetails----->$person")
                                }*/
            }
            if (it.status == Resource.Status.ERROR) {
                _progressBar.value = Event(Event.HIDE_PROGRESS)
                if (it.errorNumber.equals("1100014", true)) {
                    _sessionError.value = Event(true)
                } else {
                    toastMessage(it.errorMessage)
                }
            }
        }
    }

    fun callUpdateUserDetailsApi(person: UpdateUserDetailsModel.PersonRequest) =
        viewModelScope.launch(Dispatchers.Main) {

            val requestData = UpdateUserDetailsModel(
                Gson().toJson(
                    UpdateUserDetailsModel.JSONDataRequest(
                        personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0"),
                        person = person
                    ), UpdateUserDetailsModel.JSONDataRequest::class.java
                ), authToken
            )

            _progressBar.value = Event("Updating Profile Details.....")
            _updateUserDetails.removeSource(updateUserDetailsSource)
            withContext(Dispatchers.IO) {
                updateUserDetailsSource = homeManagementUseCase.invokeUpdateUserDetails(
                    isForceRefresh = true,
                    data = requestData
                )
            }
            _updateUserDetails.addSource(updateUserDetailsSource) {
                _updateUserDetails.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it != null) {
                        val personDetails = it.data!!.person
                        Utilities.printLog("UpdateUserDetails----->${it.data!!.person}")
                        Utilities.printLog("PersonId-----> ${personDetails.id}")
                        Utilities.printLog("UpdatedName-----> ${personDetails.firstName}")
                        Utilities.printLog("UpdatedDOB-----> ${personDetails.dateOfBirth}")
                        if (!Utilities.isNullOrEmpty(personDetails.id.toString())) {
                            updateUserDetails(
                                personDetails.firstName,
                                personDetails.dateOfBirth,
                                personDetails.id
                            )
                            Utilities.toastMessageShort(
                                context,
                                localResource.getString(R.string.PROFILE_UPDATED)
                            )
                        }
                    }

                }
                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.errorNumber.equals("1100014", true)) {
                        _sessionError.value = Event(true)
                    } else {
                        toastMessage(it.errorMessage)
                    }
                }
            }
        }

    fun callGetProfileImageApiMain(fragment: FragmentProfile, documentID: String) =
        viewModelScope.launch(Dispatchers.Main) {

            val requestData = ProfileImageModel(
                Gson().toJson(
                    ProfileImageModel.JSONDataRequest(
                        documentID = documentID
                    ),
                    ProfileImageModel.JSONDataRequest::class.java
                ), authToken
            )

            _profileImage.removeSource(profileImageSource)
            withContext(Dispatchers.IO) {
                profileImageSource = homeManagementUseCase.invokeGetProfileImage(
                    isForceRefresh = true,
                    data = requestData
                )
            }
            _profileImage.addSource(profileImageSource) {
                _profileImage.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data != null) {
                        /*                    val document = it.data!!.healthRelatedDocument
                                            val fileName = document.fileName
                                            val fileBytes = document.fileBytes
                                            try {
                                                val path = Utilities.getAppFolderLocation(context)
                                                if (!File(path,fileName).exists()) {
                                                    if ( !Utilities.isNullOrEmpty(fileBytes) ) {
                                                        val decodedImage = fileUtils.convertBase64ToBitmap(fileBytes)
                                                        if (decodedImage != null) {
                                                            val saveRecordUri = fileUtils.saveBitmapToExternalStorage(context,decodedImage,fileName)
                                                            if ( saveRecordUri != null ) {
                                                                updateUserProfileImgPath(fileName,path)
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    updateUserProfileImgPath(fileName,path)
                                                }
                                                fragment.completeFilePath = path + "/"  + fileName
                                                fragment.setProfilePic()
                                                fragment.stopImageShimmer()
                                            } catch ( e : Exception ) {
                                                e.printStackTrace()
                                                fragment.stopImageShimmer()
                                            }*/
                    }
                }
                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.errorNumber.equals("1100014", true)) {
                        _sessionError.value = Event(true)
                    } else {
                        toastMessage(it.errorMessage)
                        fragment.stopImageShimmer()
                    }
                }
            }
        }

    fun callGetProfileImageApiInner(activity: EditProfileActivity, documentID: String) =
        viewModelScope.launch(Dispatchers.Main) {

            val requestData = ProfileImageModel(
                Gson().toJson(
                    ProfileImageModel.JSONDataRequest(
                        documentID = documentID
                    ),
                    ProfileImageModel.JSONDataRequest::class.java
                ), authToken
            )

            _profileImage.removeSource(profileImageSource)
            withContext(Dispatchers.IO) {
                profileImageSource = homeManagementUseCase.invokeGetProfileImage(
                    isForceRefresh = true,
                    data = requestData
                )
            }
            _profileImage.addSource(profileImageSource) {
                _profileImage.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data != null) {
                        val document = it.data.healthRelatedDocument
                        val fileName = document.fileName
                        val fileBytes = document.fileBytes
                        try {
                            val path = Utilities.getAppFolderLocation(context!!)
                            if (!File(path, fileName).exists()) {
                                if (!Utilities.isNullOrEmpty(fileBytes)) {
                                    val decodedImage = fileUtils.convertBase64ToBitmap(fileBytes)
                                    if (decodedImage != null) {
                                        val saveRecordUri = fileUtils.saveBitmapToExternalStorage(
                                            context,
                                            decodedImage,
                                            fileName
                                        )
                                        if (saveRecordUri != null) {
                                            updateUserProfileImgPath(fileName, path)
                                        }
                                    }
                                }
                            } else {
                                updateUserProfileImgPath(fileName, path)
                            }
                            activity.completeFilePath = "$path/$fileName"
                            activity.setProfilePic()
                            activity.stopImageShimmer()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            activity.stopImageShimmer()
                        }
                    }
                }
                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.errorNumber.equals("1100014", true)) {
                        _sessionError.value = Event(true)
                    } else {
                        toastMessage(it.errorMessage)
                        activity.stopImageShimmer()
                    }
                }
            }
        }

    fun callUploadProfileImageApi(activity: EditProfileActivity, name: String, imageFile: File) =
        viewModelScope.launch(Dispatchers.Main) {
            val destPath: String = Utilities.getAppFolderLocation(context!!)
            var encodedImage = ""
            try {
                val bytesFile = ByteArray(imageFile.length().toInt())
                context.contentResolver.openFileDescriptor(Uri.fromFile(imageFile), "r")
                    ?.use { parcelFileDescriptor ->
                        FileInputStream(parcelFileDescriptor.fileDescriptor).use { inStream ->
                            inStream.read(bytesFile)
                            encodedImage = Base64.encodeToString(bytesFile, Base64.DEFAULT)
                        }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val PersonID = RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
            )
            val FileName = RequestBody.create("text/plain".toMediaTypeOrNull(), name)
            val DocumentTypeCode = RequestBody.create("text/plain".toMediaTypeOrNull(), "PROFPIC")
            val ByteArray = RequestBody.create("text/plain".toMediaTypeOrNull(), encodedImage)
            val AuthTicket = RequestBody.create("text/plain".toMediaTypeOrNull(), authToken)

            _progressBar.value = Event(Constants.LOADER_UPLOAD)
            _uploadProfileImage.removeSource(uploadProfileImageSource)
            withContext(Dispatchers.IO) {
                uploadProfileImageSource = homeManagementUseCase.invokeUploadProfileImage(
                    PersonID,
                    FileName,
                    DocumentTypeCode,
                    ByteArray,
                    AuthTicket
                )
            }
            _uploadProfileImage.addSource(uploadProfileImageSource) {
                try {
                    _uploadProfileImage.value = it

                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            if (it.data != null) {
                                //val profileImageID = it.data.profileImageID?:""
                                val profileImageID = it.data.profileImageID
                                Utilities.printLog("UploadProfileImage----->$profileImageID")
                                if (!Utilities.isNullOrEmptyOrZero(profileImageID)) {
                                    activity.hasProfileImage = true
                                    activity.needToSet = true
                                    Utilities.toastMessageShort(
                                        context,
                                        localResource.getString(R.string.PROFILE_PHOTO_UPDATED)
                                    )
                                    updateUserProfileImgPath(name, destPath)
                                    activity.completeFilePath = "$destPath/$name"
                                    activity.setProfilePic()
                                }
                            }
                        }

                        Resource.Status.ERROR -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            if (it.errorNumber.equals("1100014", true)) {
                                _sessionError.value = Event(true)
                            } else {
                                toastMessage(it.errorMessage)
                            }
                        }

                        else -> {}
                    }
                } catch (e: Exception) {
                    Utilities.printException(e)
                }
            }
        }

    fun callRemoveProfileImageApi(activity: EditProfileActivity, context: Context) =
        viewModelScope.launch(Dispatchers.Main) {

            val requestData = RemoveProfileImageModel(
                Gson().toJson(
                    RemoveProfileImageModel.JSONDataRequest(
                        personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
                            .toInt()
                    ), RemoveProfileImageModel.JSONDataRequest::class.java
                ), authToken
            )

            _progressBar.value = Event(Constants.LOADER_DELETE)
            _removeProfileImage.removeSource(removeProfileImageSource)
            withContext(Dispatchers.IO) {
                removeProfileImageSource = homeManagementUseCase.invokeRemoveProfileImage(
                    isForceRefresh = true,
                    data = requestData
                )
            }
            _removeProfileImage.addSource(removeProfileImageSource) {
                _removeProfileImage.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data != null) {
                        val isProcessed = it.data!!.isProcessed
                        Utilities.printLog("isProcessed----->$isProcessed")
                        if (isProcessed.equals(Constants.TRUE, ignoreCase = true)) {
                            Utilities.toastMessageShort(
                                context,
                                context.resources.getString(R.string.PROFILE_PHOTO_REMOVED)
                            )
                            UserSingleton.getInstance()!!.clearData()
                            activity.removeProfilePic()
                        } else {
                            Utilities.toastMessageShort(
                                context,
                                context.resources.getString(R.string.ERROR_PROFILE_PHOTO)
                            )
                        }
                    }
                }
                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.errorNumber.equals("1100014", true)) {
                        _sessionError.value = Event(true)
                    } else {
                        toastMessage(it.errorMessage)
                    }
                }
            }
        }

    fun callListDocumentsApi(forceRefresh: Boolean, from: String) =
        viewModelScope.launch(Dispatchers.Main) {

            val requestData = ListDocumentsModel(
                Gson().toJson(
                    ListDocumentsModel.JSONDataRequest(
                        searchCriteria = ListDocumentsModel.SearchCriteria(
                            personID = preferenceUtils.getPreference(
                                PreferenceConstants.PERSONID,
                                "0"
                            )
                        )
                    ),
                    ListDocumentsModel.JSONDataRequest::class.java
                ), authToken
            )

            if (from.equals(Constants.SWITCH_PROFILE, ignoreCase = true)) {
                _progressBar.value = Event("Getting Health Records...")
            }
            _listDocuments.removeSource(listDocumentsSource)
            withContext(Dispatchers.IO) {
                listDocumentsSource = homeManagementUseCase.invokeDocumentList(
                    isForceRefresh = forceRefresh,
                    data = requestData
                )
            }
            _listDocuments.addSource(listDocumentsSource) {
                //_listDocuments.value = it.data
                _listDocuments.postValue(it)

                if (it.status == Resource.Status.SUCCESS) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data != null) {
                        val list = it.data!!.documents.filter {
                            it.PersonId.toString() == preferenceUtils.getPreference(
                                PreferenceConstants.PERSONID,
                                "0"
                            )
                        }
                        Utilities.printLog("RecordCount----->${list.size}")
                        allHealthDocuments.postValue(list)
                    }
                }
                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.errorNumber.equals("1100014", true)) {
                        _sessionError.value = Event(true)
                    } else {
                        toastMessage(it.errorMessage)
                    }
                }
            }
        }

    fun callSudPolicyByMobileNumberApi(fragment: FragmentProfile) =
        viewModelScope.launch(Dispatchers.Main) {

            val obj = JsonObject()
            obj.addProperty("api_code", Constants.SUD_MOBILE_NUMBER_DETAILS)
            obj.addProperty("policy_number", "")
            obj.addProperty(
                "mobile_number",
                preferenceUtils.getPreference(PreferenceConstants.POLICY_MOBILE_NUMBER, "")
            )
            val requestData = SudPolicyByMobileNumberModel(
                obj,
                preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
            )

            //_progressBar.value = Event("")
            _sudPolicyByMobileNumber.removeSource(sudPolicyByMobileNumberSource)
            withContext(Dispatchers.IO) {
                sudPolicyByMobileNumberSource =
                    sudLifePolicyManagementUseCase.invokeSudPolicyByMobileNumber(data = requestData)
            }
            _sudPolicyByMobileNumber.addSource(sudPolicyByMobileNumberSource) {
                _sudPolicyByMobileNumber.value = it

                if (it.status == Resource.Status.SUCCESS) {
                    //_progressBar.value = Event(Event.HIDE_PROGRESS)
                    if (it.data != null) {
                        //Utilities.printLog("Response--->" + it.data!!)
                        when (it.data!!.result.status) {
                            "1" -> {
                                fragment.policyListSizeFinal = it.data!!.result.records.size
                                if (!it.data!!.result.records.isNullOrEmpty()) {
                                    for (item in it.data!!.result.records) {
                                        callSudKypApi(item.policyNumber!!, fragment)
                                    }
                                } else {
                                    fragment.showNoDataView()
                                }
                            }

                            "0" -> {
                                fragment.showNoDataView()
                            }
                        }
                        /*                    if ( it.data!!.status == "1" && it.data!!.records.isNotEmpty() ) {
                                                fragment.policyListSizeFinal = it.data!!.records.size
                                                if ( !it.data!!.records.isNullOrEmpty() ) {
                                                    for (item in it.data!!.records) {
                                                        callPolicyDetailsByPolicyNumberApi(item.policyNumber!!,fragment)
                                                    }
                                                } else {
                                                    fragment.showNoDataView()
                                                }
                                            }*/
                    }
                }
                if (it.status == Resource.Status.ERROR) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    toastMessage(it.errorMessage)
                }
            }

        }

    private fun callSudKypApi(policy_Number: String, fragment: FragmentProfile) {
        try {
            val obj = JsonObject()
            obj.addProperty("api_code", Constants.SUD_KYP)
            obj.addProperty("policy_number", policy_Number)
            //obj.addProperty("mobile_number","")
            val requestData =
                SudKYPModel(obj, preferenceUtils.getPreference(PreferenceConstants.TOKEN, ""))

            //_progressBar.value = Event("")
            val downloadService = provideRetrofit().create(SudPolicyApiService::class.java)
            val call = downloadService.getSudKyp(requestData)
            call.enqueue(object : Callback<SudKYPModel.SudKYPResponse> {
                override fun onResponse(
                    call: Call<SudKYPModel.SudKYPResponse>,
                    response: Response<SudKYPModel.SudKYPResponse>
                ) {
                    if (response.body() != null) {
                        val result = response.body()!!
                        if (response.isSuccessful) {
                            Utilities.printData("PolicyResp", result, true)
                            if (result.result.status == "1" && !result.result.kYPList.isNullOrEmpty()) {
                                fragment.addPolicyInList(result.result.kYPList[0])
                            }
                            if (result.result.status == "2") {
                                fragment.policyListSize++
                                Utilities.printLogError("policyListSizeFinal--->${fragment.policyListSizeFinal}")
                                Utilities.printLogError("policyListSize--->${fragment.policyListSize}")
                                Utilities.printLogError("No data found for Policy--->$policy_Number")
                                if (fragment.policyListSizeFinal == fragment.policyListSize) {
                                    //fragment.notifyList(fragment.sudPolicyList)
                                    fragment.notifyList()
                                    //fragment.policyDataSingleton!!.policyList = fragment.sudPolicyList
                                }
                            }
                        } else {
                            Utilities.printLogError("Server Contact failed")
                        }
                    } else {
                        Utilities.printLogError("response.body is null")
                    }
                    //_progressBar.value = Event(Event.HIDE_PROGRESS)
                }

                override fun onFailure(call: Call<SudKYPModel.SudKYPResponse>, t: Throwable) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    Utilities.printLog("Api failed" + t.printStackTrace())
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun provideRetrofit(): Retrofit {
        val logging = HttpLoggingInterceptor { message ->
            Utilities.printLog("HttpLogging--> $message")
        }
        logging.level = HttpLoggingInterceptor.Level.BODY
        return Retrofit.Builder()
            .client(
                OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .protocols(listOf(Protocol.HTTP_1_1))
                    .addInterceptor(logging)
                    .connectTimeout(3, TimeUnit.MINUTES)
                    .writeTimeout(3, TimeUnit.MINUTES)
                    .readTimeout(3, TimeUnit.MINUTES)
                    .build()
            )
            .baseUrl(Constants.strAPIUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /*    fun updateUserMobileNumber(phone: String) = viewModelScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                val userDetails = homeManagementUseCase.invokeGetLoggedInPersonDetails()
                var dob = ""
                try {
                    val db = DateHelper.formatDateValue(DateHelper.SERVER_DATE_YYYYMMDD, userDetails.dateOfBirth!!)!!
                    if (db != null) {
                        dob = db
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                val userUpdateDetails = UserRelatives(
                    relativeID = personId,
                    firstName = userDetails.firstName,
                    lastName = "",
                    dateOfBirth = dob,
                    age = userDetails.age.toString(),
                    gender = userDetails.gender,
                    contactNo = phone,
                    emailAddress = userDetails.emailAddress)
                Utilities.printLog("userUpdateDetails----->$userUpdateDetails")
                callUpdateRelativesApi(true, userUpdateDetails, Constants.USER)
            }
        }*/

    fun getLoggedInPersonDetails() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            userDetails.postValue(homeManagementUseCase.invokeGetLoggedInPersonDetails())
        }
    }

    fun updateUserDetails(name: String, dob: String, personId: Int) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            homeManagementUseCase.invokeUpdateUserDetails(name, dob, personId)
            preferenceUtils.storePreference(PreferenceConstants.FIRSTNAME, name)
            preferenceUtils.storePreference(
                PreferenceConstants.DOB,
                dob.split("T").toTypedArray()[0]
            )
        }
    }

    fun updateUserProfileImgPath(name: String, path: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            homeManagementUseCase.invokeUpdateUserProfileImgPath(name, path)
        }
    }

    fun getAllUserRelatives() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            userRelativesList.postValue(homeManagementUseCase.invokeGetUserRelatives())
        }
    }

    fun getAllHealthRecordsDocuments() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val list = homeManagementUseCase.invokeGetAllHealthDocuments().filter {
                it.PersonId.toString() == preferenceUtils.getPreference(
                    PreferenceConstants.PERSONID,
                    "0"
                )
            }
            allHealthDocuments.postValue(list)
        }
    }

    fun getRelativesList() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            userRelativesList.postValue(homeManagementUseCase.invokeGetUserRelativesExceptSelf())
        }
    }

    fun getUserRelativeSpecific(relationShipCode: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            alreadyExistRelatives.postValue(
                homeManagementUseCase.invokeGetUserRelativeSpecific(
                    relationShipCode
                )
            )
        }
    }

    fun getUserRelativeForRelativeId(relativeId: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            alreadyExistRelatives.postValue(
                homeManagementUseCase.invokeGetUserRelativeForRelativeId(
                    relativeId
                )
            )
        }
    }


    fun getFamilyRelationshipList() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val user = homeManagementUseCase.invokeGetUserRelativeDetailsByRelativeId(
                preferenceUtils.getPreference(
                    PreferenceConstants.PERSONID,
                    "0"
                )
            )
            val gender = if (user == null) {
                preferenceUtils.getPreference(PreferenceConstants.GENDER, "")
            } else {
                user.gender
            }
            Utilities.printLogError("Gender--->$gender")
            if (gender.contains("1", ignoreCase = true)) {
                familyRelationList.postValue(dataHandler.getFamilyRelationListMale())
            } else {
                familyRelationList.postValue(dataHandler.getFamilyRelationListFemale())
            }
        }
    }

    fun getRelationImgId(relationshipCode: String): Int {
        var relationImgTobeAdded: Int = R.drawable.img_husband
        when (relationshipCode) {
            "FAT" -> {
                relationImgTobeAdded = R.drawable.img_father
            }

            "MOT" -> {
                relationImgTobeAdded = R.drawable.img_mother
            }

            "SON" -> {
                relationImgTobeAdded = R.drawable.img_son
            }

            "DAU" -> {
                relationImgTobeAdded = R.drawable.img_daughter
            }

            "GRF" -> {
                relationImgTobeAdded = R.drawable.img_gf
            }

            "GRM" -> {
                relationImgTobeAdded = R.drawable.img_gm
            }

            "HUS" -> {
                relationImgTobeAdded = R.drawable.img_husband
            }

            "WIF" -> {
                relationImgTobeAdded = R.drawable.img_wife
            }

            "BRO" -> {
                relationImgTobeAdded = R.drawable.img_brother
            }

            "SIS" -> {
                relationImgTobeAdded = R.drawable.img_sister
            }
        }
        return relationImgTobeAdded
    }

    fun refreshPersonId() {
        personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
        gender = preferenceUtils.getPreference(PreferenceConstants.GENDER, "")
        relationshipCode = preferenceUtils.getPreference(PreferenceConstants.RELATIONSHIPCODE, "")
    }

    fun updateFirstName(firstName: String) {
        preferenceUtils.storePreference(PreferenceConstants.FIRSTNAME, firstName)
    }

}

/*

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.caressa.allizhealth.app.R
import com.caressa.allizhealth.app.common.base.BaseViewModel
import com.caressa.allizhealth.app.common.constants.Constants
import com.caressa.allizhealth.app.common.constants.FirebaseConstants
import com.caressa.allizhealth.app.common.constants.PreferenceConstants
import com.caressa.allizhealth.app.common.utils.Event
import com.caressa.allizhealth.app.common.utils.FileUtils
import com.caressa.allizhealth.app.common.utils.FirebaseHelper
import com.caressa.allizhealth.app.common.utils.LocaleHelper
import com.caressa.allizhealth.app.common.utils.PreferenceUtils
import com.caressa.allizhealth.app.common.utils.UserSingleton
import com.caressa.allizhealth.app.common.utils.Utilities
import com.caressa.allizhealth.app.home.common.DataHandler
import com.caressa.allizhealth.app.home.common.SudPolicyApiService
import com.caressa.allizhealth.app.home.domain.HomeManagementUseCase
import com.caressa.allizhealth.app.home.domain.SudLifePolicyManagementUseCase
import com.caressa.allizhealth.app.home.ui.FragmentProfile
import com.caressa.allizhealth.app.home.ui.ProfileAndFamilyMember.AddFamilyMemberFragment
import com.caressa.allizhealth.app.home.ui.ProfileAndFamilyMember.EditProfileActivity
import com.caressa.allizhealth.app.model.entity.HealthDocument
import com.caressa.allizhealth.app.model.entity.UserRelatives
import com.caressa.allizhealth.app.model.entity.Users
import com.caressa.allizhealth.app.model.home.AddRelativeModel
import com.caressa.allizhealth.app.model.home.ProfileImageModel
import com.caressa.allizhealth.app.model.home.RemoveProfileImageModel
import com.caressa.allizhealth.app.model.home.RemoveRelativeModel
import com.caressa.allizhealth.app.model.home.UpdateRelativeModel
import com.caressa.allizhealth.app.model.home.UpdateUserDetailsModel
import com.caressa.allizhealth.app.model.home.UploadProfileImageResponce
import com.caressa.allizhealth.app.model.home.UserDetailsModel
import com.caressa.allizhealth.app.model.security.PhoneExistsModel
import com.caressa.allizhealth.app.model.shr.ListDocumentsModel
import com.caressa.allizhealth.app.model.shr.ListRelativesModel
import com.caressa.allizhealth.app.model.sudLifePolicy.SudKYPModel
import com.caressa.allizhealth.app.model.sudLifePolicy.SudPolicyByMobileNumberModel
import com.caressa.allizhealth.app.repository.utils.Resource
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileInputStream
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class ProfileFamilyMemberViewModel @Inject constructor(
    application: Application,
    private val homeManagementUseCase: HomeManagementUseCase,
    private val sudLifePolicyManagementUseCase: SudLifePolicyManagementUseCase,
    private val preferenceUtils: PreferenceUtils,
    private val dataHandler: DataHandler,
    val context: Context?
) : BaseViewModel(application) {

    var adminPersonId = preferenceUtils.getPreference(PreferenceConstants.ADMIN_PERSON_ID, "0")
    var personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
    var gender = preferenceUtils.getPreference(PreferenceConstants.GENDER, "")
    var relationshipCode = preferenceUtils.getPreference(PreferenceConstants.RELATIONSHIPCODE, "")
    var authToken = preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")

    private val localResource =
        LocaleHelper.getLocalizedResources(context!!, Locale(LocaleHelper.getLanguage(context)))!!

    private val fileUtils = FileUtils

    private var relativeToRemove: List<UserRelatives> = listOf()

    var userDetails = MutableLiveData<Users>()
    val userRelativesList = MutableLiveData<List<UserRelatives>>()
    val alreadyExistRelatives = MutableLiveData<List<UserRelatives>>()
    var familyRelationList = MutableLiveData<List<DataHandler.FamilyRelationOption>>()
    val allHealthDocuments = MutableLiveData<List<HealthDocument>>()

    private var userProfileDetailsSource: LiveData<Resource<UserDetailsModel.UserDetailsResponse>> =
        MutableLiveData()
    private val _userProfileDetails =
        MediatorLiveData<Resource<UserDetailsModel.UserDetailsResponse>>()
    val userProfileDetails: LiveData<Resource<UserDetailsModel.UserDetailsResponse>> get() = _userProfileDetails

    private var userProfileDetailsEditSource: LiveData<Resource<UserDetailsModel.UserDetailsResponse>> =
        MutableLiveData()
    private val _userProfileDetailsEdit =
        MediatorLiveData<Resource<UserDetailsModel.UserDetailsResponse>>()
    val userProfileDetailsEdit: LiveData<Resource<UserDetailsModel.UserDetailsResponse>> get() = _userProfileDetailsEdit

    private var updateUserDetailsSource: LiveData<Resource<UpdateUserDetailsModel.UpdateUserDetailsResponse>> =
        MutableLiveData()
    private val _updateUserDetails =
        MediatorLiveData<Resource<UpdateUserDetailsModel.UpdateUserDetailsResponse>>()
    val updateUserDetails: LiveData<Resource<UpdateUserDetailsModel.UpdateUserDetailsResponse>> get() = _updateUserDetails

    private var profileImageSource: LiveData<Resource<ProfileImageModel.ProfileImageResponse>> =
        MutableLiveData()
    private val _profileImage = MediatorLiveData<Resource<ProfileImageModel.ProfileImageResponse>>()
    val profileImage: LiveData<Resource<ProfileImageModel.ProfileImageResponse>> get() = _profileImage

    private var uploadProfileImageSource: LiveData<Resource<UploadProfileImageResponce>> =
        MutableLiveData()
    private val _uploadProfileImage = MediatorLiveData<Resource<UploadProfileImageResponce>>()
    val uploadProfileImage: LiveData<Resource<UploadProfileImageResponce>> get() = _uploadProfileImage

    private var removeProfileImageSource: LiveData<Resource<RemoveProfileImageModel.RemoveProfileImageResponse>> =
        MutableLiveData()
    private val _removeProfileImage =
        MediatorLiveData<Resource<RemoveProfileImageModel.RemoveProfileImageResponse>>()
    val removeProfileImage: LiveData<Resource<RemoveProfileImageModel.RemoveProfileImageResponse>> get() = _removeProfileImage

    private var addRelativeSource: LiveData<Resource<AddRelativeModel.AddRelativeResponse>> =
        MutableLiveData()
    private val _addRelative = MediatorLiveData<Resource<AddRelativeModel.AddRelativeResponse>>()
    val addRelative: LiveData<Resource<AddRelativeModel.AddRelativeResponse>> get() = _addRelative

    private var updateRelativeSource: LiveData<Resource<UpdateRelativeModel.UpdateRelativeResponse>> =
        MutableLiveData()
    private val _updateRelative =
        MediatorLiveData<Resource<UpdateRelativeModel.UpdateRelativeResponse>>()
    val updateRelative: LiveData<Resource<UpdateRelativeModel.UpdateRelativeResponse>> get() = _updateRelative

    private var removeRelativeSource: LiveData<Resource<RemoveRelativeModel.RemoveRelativeResponse>> =
        MutableLiveData()
    private val _removeRelative =
        MediatorLiveData<Resource<RemoveRelativeModel.RemoveRelativeResponse>>()
    val removeRelative: LiveData<Resource<RemoveRelativeModel.RemoveRelativeResponse>> get() = _removeRelative

    private var listRelativesSource: LiveData<Resource<ListRelativesModel.ListRelativesResponse>> =
        MutableLiveData()
    private val _listRelatives =
        MediatorLiveData<Resource<ListRelativesModel.ListRelativesResponse>>()
    val listRelatives: LiveData<Resource<ListRelativesModel.ListRelativesResponse>> get() = _listRelatives

    private var listDocumentsSource: LiveData<Resource<ListDocumentsModel.ListDocumentsResponse>> =
        MutableLiveData()
    private val _listDocuments =
        MediatorLiveData<Resource<ListDocumentsModel.ListDocumentsResponse>>()
    val listRecordDocuments: LiveData<Resource<ListDocumentsModel.ListDocumentsResponse>> get() = _listDocuments

    private var phoneExistSource: LiveData<Resource<PhoneExistsModel.IsExistResponse>> =
        MutableLiveData()
    private val _phoneExist = MediatorLiveData<Resource<PhoneExistsModel.IsExistResponse>>()
    val phoneExist: LiveData<Resource<PhoneExistsModel.IsExistResponse>> get() = _phoneExist

    private var sudPolicyByMobileNumberSource: LiveData<Resource<SudPolicyByMobileNumberModel.SudPolicyByMobileNumberResponse>> =
        MutableLiveData()
    private val _sudPolicyByMobileNumber =
        MediatorLiveData<Resource<SudPolicyByMobileNumberModel.SudPolicyByMobileNumberResponse>>()
    val sudPolicyByMobileNumber: LiveData<Resource<SudPolicyByMobileNumberModel.SudPolicyByMobileNumberResponse>> get() = _sudPolicyByMobileNumber

    fun isSelfUser(): Boolean {
        val personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
        val adminPersonId = preferenceUtils.getPreference(PreferenceConstants.ADMIN_PERSON_ID, "0")
        var isSelfUser = false
        if (!Utilities.isNullOrEmptyOrZero(personId) && !Utilities.isNullOrEmptyOrZero(adminPersonId) && personId == adminPersonId) {
            isSelfUser = true
        }
        return isSelfUser
    }

    fun callAddNewRelativeApi(
        forceRefresh: Boolean,
        userRelative: UserRelatives,
        from: String,
        fragment: AddFamilyMemberFragment
    ) = viewModelScope.launch(Dispatchers.Main) {

        val contact = AddRelativeModel.Contact(userRelative.emailAddress, userRelative.contactNo)
        val relationships: ArrayList<AddRelativeModel.Relationship> = ArrayList()
        relationships.add(
            AddRelativeModel.Relationship(
                preferenceUtils.getPreference(
                    PreferenceConstants.PERSONID, "0"
                ), userRelative.relationshipCode
            )
        )
        var gender = ""
        if (userRelative.gender.equals("Male", ignoreCase = true)) {
            gender = "1"
        } else if (userRelative.gender.equals("Female", ignoreCase = true)) {
            gender = "2"
        }

        val requestData = AddRelativeModel(
            Gson().toJson(
                AddRelativeModel.JSONDataRequest(
                    personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0"),
                    person = AddRelativeModel.Person(
                        firstName = userRelative.firstName,
                        relativeID = userRelative.relativeID,
                        dateOfBirth = userRelative.dateOfBirth,
                        gender = gender,
                        isProfileImageChanges = Constants.FALSE,
                        contact = contact,
                        relationships = relationships
                    )
                ), AddRelativeModel.JSONDataRequest::class.java
            ), authToken
        )

        _progressBar.value = Event("Adding Family Member.....")
        _addRelative.removeSource(addRelativeSource)
        withContext(Dispatchers.IO) {
            addRelativeSource = homeManagementUseCase.invokeaddNewRelative(
                isForceRefresh = forceRefresh, data = requestData
            )
        }
        _addRelative.addSource(addRelativeSource) {
            try {
                _addRelative.value = it
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.FAMILY_MEMBER_ADD_EVENT)
                    }

                    Resource.Status.ERROR -> {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        if (it.errorNumber.equals("1100014", true)) {
                            _sessionError.value = Event(true)
                        } else {
                            toastMessage(it.errorMessage)
                        }
                    }

                    else -> {}
                }
            } catch (e: Exception) {
                Utilities.printException(e)
            }
        }

    }

    fun callListRelativesApi(forceRefresh: Boolean) = viewModelScope.launch(Dispatchers.Main) {

        val requestData = ListRelativesModel(
            Gson().toJson(
                ListRelativesModel.JSONDataRequest(
                    personID = adminPersonId
                ), ListRelativesModel.JSONDataRequest::class.java
            ), authToken
        )

        _progressBar.value = Event("Getting Relatives...")
        _listRelatives.removeSource(listRelativesSource)
        withContext(Dispatchers.IO) {
            listRelativesSource = homeManagementUseCase.invokeRelativesList(
                isForceRefresh = forceRefresh, data = requestData
            )
        }
        _listRelatives.addSource(listRelativesSource) {
            try {
                _listRelatives.value = it

                it.data?.let { data ->
                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            val relativesList = data.relativeList
                            if (relativesList.size > 1) {
                                val userRelatives: MutableList<UserRelatives> = mutableListOf()
                                for (i in relativesList) {
                                    userRelatives.add(i)
                                }
                                userRelativesList.postValue(userRelatives)
                            } else {
                                //                            fragment.noDataView()
                            }
                            Utilities.printLog("RelativesList----->${relativesList.size}")
                        }

                        Resource.Status.ERROR -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            if (it.errorNumber.equals("1100014", true)) {
                                _sessionError.value = Event(true)
                            } else {
                                toastMessage(it.errorMessage)
                            }
                        }

                        else -> {}
                    }

                }
            } catch (e: Exception) {
                Utilities.printException(e)
            }

        }
    }

    fun callRemoveRelativesApiNew(
        forceRefresh: Boolean, relativeId: String, relationshipId: String
    ) = viewModelScope.launch(Dispatchers.Main) {

        val relatives: ArrayList<Int> = ArrayList()
        withContext(Dispatchers.IO) {
            relativeToRemove = homeManagementUseCase.invokeGetUserRelativeForRelativeId(relativeId)
        }

        for (i in relativeToRemove) {
            relatives.add(relationshipId.toInt())
        }

        val requestData = RemoveRelativeModel(
            Gson().toJson(
                RemoveRelativeModel.JSONDataRequest(
                    id = relatives
                ), RemoveRelativeModel.JSONDataRequest::class.java
            ), authToken
        )

        _progressBar.value = Event(Constants.LOADER_DELETE)
        _removeRelative.removeSource(removeRelativeSource)
        withContext(Dispatchers.IO) {
            removeRelativeSource = homeManagementUseCase.invokeRemoveRelative(
                isForceRefresh = forceRefresh, data = requestData, relativeId = relativeId
            )
        }
        _removeRelative.addSource(removeRelativeSource) {
            try {
                _removeRelative.value = it
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        toastMessage(localResource.getString(R.string.MEMBER_DELETED))
                    }

                    Resource.Status.ERROR -> {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        if (it.errorNumber.equals("1100014", true)) {
                            _sessionError.value = Event(true)
                        } else {
                            toastMessage(it.errorMessage)
                        }
                    }

                    else -> {}
                }
            } catch (e: Exception) {
                Utilities.printException(e)
            }

        }
    }

    fun callCheckPhoneExistApi(username: String, phone: String) =
        viewModelScope.launch(Dispatchers.Main) {

            val requestData = PhoneExistsModel(
                Gson().toJson(
                    PhoneExistsModel.JSONDataRequest(
                        primaryPhone = phone
                    ), PhoneExistsModel.JSONDataRequest::class.java
                )
            )

            _progressBar.value = Event("")
            _phoneExist.removeSource(phoneExistSource)
            withContext(Dispatchers.IO) {
                phoneExistSource = homeManagementUseCase.invokePhoneExist(true, requestData)
            }
            _phoneExist.addSource(phoneExistSource) {

                try {
                    _phoneExist.value = it
                    it.data?.let { data ->
                        when (it.status) {
                            Resource.Status.SUCCESS -> {
                                _progressBar.value = Event(Event.HIDE_PROGRESS)
                                if (data.isExist.equals(Constants.TRUE, true)) {
                                    toastMessage(localResource.getString(R.string.ERROR_MOBILE_ALREADY_REGISTERED))
                                }
                            }

                            Resource.Status.ERROR -> {
                                _progressBar.value = Event(Event.HIDE_PROGRESS)
                                if (it.errorNumber.equals("1100014", true)) {
                                    _sessionError.value = Event(true)
                                } else {
                                    toastMessage(it.errorMessage)
                                }
                            }

                            else -> {}
                        }

                    }
                } catch (e: Exception) {
                    Utilities.printException(e)
                }

            }
        }

    fun callUpdateRelativesApi(forceRefresh: Boolean, relative: UserRelatives, from: String) =
        viewModelScope.launch(Dispatchers.Main) {

            val requestData = UpdateRelativeModel(
                Gson().toJson(
                    UpdateRelativeModel.JSONDataRequest(
                        personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0"),
                        person = UpdateRelativeModel.Person(
                            id = relative.relativeID.toInt(),
                            firstName = relative.firstName,
                            lastName = "",
                            dateOfBirth = relative.dateOfBirth,
                            gender = relative.gender,
                            isProfileImageChanges = Constants.FALSE,
                            contact = UpdateRelativeModel.Contact(
                                emailAddress = relative.emailAddress,
                                primaryContactNo = relative.contactNo
                            )
                        )
                    ), UpdateRelativeModel.JSONDataRequest::class.java
                ), authToken
            )

            _progressBar.value = Event("Updating Relative Profile.....")
            _updateRelative.removeSource(updateRelativeSource)
            withContext(Dispatchers.IO) {
                updateRelativeSource = homeManagementUseCase.invokeupdateRelative(
                    isForceRefresh = forceRefresh,
                    data = requestData,
                    relativeId = relative.relativeID
                )
            }
            _updateRelative.addSource(updateRelativeSource) {
                try {
                    _updateRelative.value = it

                    it.data?.let { data ->
                        when (it.status) {
                            Resource.Status.SUCCESS -> {
                                _progressBar.value = Event(Event.HIDE_PROGRESS)
                                if (it != null) {
                                    val personDetails = data.person
                                    if (!Utilities.isNullOrEmpty(personDetails.id.toString())) {
                                        Utilities.printLog("")
                                        //toastMessage(context.resources.getString(R.string.PROFILE_UPDATED))
                                        //navigate(EditFamilyMemberDetailsFragmentDirections.actionEditFamilyMemberDetailsFragmentToFamilyMembersListFragment())
                                    }
                                }
                            }

                            Resource.Status.ERROR -> {
                                _progressBar.value = Event(Event.HIDE_PROGRESS)
                                if (it.errorNumber.equals("1100014", true)) {
                                    _sessionError.value = Event(true)
                                } else {
                                    toastMessage(it.errorMessage)
                                    if (from == Constants.RELATIVE) {
                                        Utilities.printLog("")
                                        //navigate(EditFamilyMemberDetailsFragmentDirections.actionEditFamilyMemberDetailsFragmentToFamilyMembersListFragment())
                                    }
                                }
                            }

                            else -> {}
                        }
                    }
                } catch (e: Exception) {
                    Utilities.printException(e)
                }

            }
        }

    fun callGetUserDetailsEditApi() = viewModelScope.launch(Dispatchers.Main) {

        val requestData = UserDetailsModel(
            Gson().toJson(
                UserDetailsModel.JSONDataRequest(
                    UserDetailsModel.PersonIdentificationCriteria(
                        personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
                            .toInt()
                    )
                ), UserDetailsModel.JSONDataRequest::class.java
            ), authToken
        )

        _userProfileDetailsEdit.removeSource(userProfileDetailsEditSource)
        withContext(Dispatchers.IO) {
            userProfileDetailsEditSource = homeManagementUseCase.invokeGetUserDetails(
                isForceRefresh = true, data = requestData
            )
        }
        _userProfileDetailsEdit.addSource(userProfileDetailsEditSource) {
            try {
                _userProfileDetailsEdit.value = it

                it.data?.let { data ->
                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            val person = data.person
                            Utilities.printLog("GetUserDetails----->$person")
                        }

                        Resource.Status.ERROR -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            if (it.errorNumber.equals("1100014", true)) {
                                _sessionError.value = Event(true)
                            } else {
                                toastMessage(it.errorMessage)
                            }
                        }

                        else -> {}
                    }
                }
            } catch (e: Exception) {
                Utilities.printException(e)
            }
        }
    }

    fun callGetUserDetailsApi() = viewModelScope.launch(Dispatchers.Main) {

        val requestData = UserDetailsModel(
            Gson().toJson(
                UserDetailsModel.JSONDataRequest(
                    UserDetailsModel.PersonIdentificationCriteria(
                        personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
                            .toInt()
                    )
                ), UserDetailsModel.JSONDataRequest::class.java
            ), authToken
        )

        _userProfileDetails.removeSource(userProfileDetailsSource)
        withContext(Dispatchers.IO) {
            userProfileDetailsSource = homeManagementUseCase.invokeGetUserDetails(
                isForceRefresh = true, data = requestData
            )
        }
        _userProfileDetails.addSource(userProfileDetailsSource) {
            try {
                _userProfileDetails.value = it

                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                    }

                    Resource.Status.ERROR -> {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        if (it.errorNumber.equals("1100014", true)) {
                            _sessionError.value = Event(true)
                        } else {
                            toastMessage(it.errorMessage)
                        }
                    }

                    else -> {}
                }
            } catch (e: Exception) {
                Utilities.printException(e)
            }

        }
    }

    fun callUpdateUserDetailsApi(person: UpdateUserDetailsModel.PersonRequest) =
        viewModelScope.launch(Dispatchers.Main) {

            val requestData = UpdateUserDetailsModel(
                Gson().toJson(
                    UpdateUserDetailsModel.JSONDataRequest(
                        personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0"),
                        person = person
                    ), UpdateUserDetailsModel.JSONDataRequest::class.java
                ), authToken
            )

            _progressBar.value = Event("Updating Profile Details.....")
            _updateUserDetails.removeSource(updateUserDetailsSource)
            withContext(Dispatchers.IO) {
                updateUserDetailsSource = homeManagementUseCase.invokeUpdateUserDetails(
                    isForceRefresh = true, data = requestData
                )
            }
            _updateUserDetails.addSource(updateUserDetailsSource) {
                try {
                    _updateUserDetails.value = it
                    it?.data?.let { data ->
                        when (it.status) {
                            Resource.Status.SUCCESS -> {
                                _progressBar.value = Event(Event.HIDE_PROGRESS)
                                val personDetails = data.person
                                Utilities.printLog("UpdateUserDetails----->${data.person}")
                                Utilities.printLog("PersonId-----> ${personDetails.id}")
                                Utilities.printLog("UpdatedName-----> ${personDetails.firstName}")
                                if (!Utilities.isNullOrEmpty(personDetails.id.toString())) {
                                    updateUserDetails(
                                        personDetails.firstName,
                                        personDetails.dateOfBirth,
                                        personDetails.id
                                    )
                                    Utilities.toastMessageShort(
                                        context, localResource.getString(R.string.PROFILE_UPDATED)
                                    )
                                }
                            }

                            Resource.Status.ERROR -> {
                                _progressBar.value = Event(Event.HIDE_PROGRESS)
                                if (it.errorNumber.equals("1100014", true)) {
                                    _sessionError.value = Event(true)
                                } else {
                                    toastMessage(it.errorMessage)
                                }
                            }

                            else -> {}
                        }
                    }
                } catch (e: Exception) {
                    Utilities.printException(e)
                }
            }
        }

    fun callGetProfileImageApiMain(fragment: FragmentProfile, documentID: String) =
        viewModelScope.launch(Dispatchers.Main) {

            val requestData = ProfileImageModel(
                Gson().toJson(
                    ProfileImageModel.JSONDataRequest(
                        documentID = documentID
                    ), ProfileImageModel.JSONDataRequest::class.java
                ), authToken
            )

            _profileImage.removeSource(profileImageSource)
            withContext(Dispatchers.IO) {
                profileImageSource = homeManagementUseCase.invokeGetProfileImage(
                    isForceRefresh = true, data = requestData
                )
            }
            _profileImage.addSource(profileImageSource) {
                try {
                    _profileImage.value = it

                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)*/
/*if (it.data != null ) {
                            val document = it.data!!.healthRelatedDocument
                            val fileName = document.fileName
                            val fileBytes = document.fileBytes
                            try {
                                val path = Utilities.getAppFolderLocation(context)
                                if (!File(path, fileName).exists()) {
                                    if (!Utilities.isNullOrEmpty(fileBytes)) {
                                        val decodedImage =
                                            fileUtils.convertBase64ToBitmap(fileBytes)
                                        if (decodedImage != null) {
                                            val saveRecordUri =
                                                fileUtils.saveBitmapToExternalStorage(
                                                    context,
                                                    decodedImage,
                                                    fileName
                                                )
                                            if (saveRecordUri != null) {
                                                updateUserProfileImgPath(fileName, path)
                                            }
                                        }
                                    }
                                } else {
                                    updateUserProfileImgPath(fileName, path)
                                }
                                fragment.completeFilePath = path + "/" + fileName
                                fragment.setProfilePic()
                                fragment.stopImageShimmer()
                            } catch (e: Exception) {
                                e.printStackTrace()
                                fragment.stopImageShimmer()
                            }
                        }*//*

                        }

                        Resource.Status.ERROR -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            if (it.errorNumber.equals("1100014", true)) {
                                _sessionError.value = Event(true)
                            } else {
                                toastMessage(it.errorMessage)
                                fragment.stopImageShimmer()
                            }
                        }

                        else -> {}
                    }
                } catch (e: Exception) {
                    Utilities.printException(e)
                }
            }
        }

    fun callGetProfileImageApiInner(activity: EditProfileActivity, documentID: String) =
        viewModelScope.launch(Dispatchers.Main) {

            val requestData = ProfileImageModel(
                Gson().toJson(
                    ProfileImageModel.JSONDataRequest(
                        documentID = documentID
                    ), ProfileImageModel.JSONDataRequest::class.java
                ), authToken
            )

            _profileImage.removeSource(profileImageSource)
            withContext(Dispatchers.IO) {
                profileImageSource = homeManagementUseCase.invokeGetProfileImage(
                    isForceRefresh = true, data = requestData
                )
            }
            _profileImage.addSource(profileImageSource) {
                try {
                    _profileImage.value = it
                    it?.data?.let { data ->
                        when (it.status) {
                            Resource.Status.SUCCESS -> {
                                _progressBar.value = Event(Event.HIDE_PROGRESS)
                                val document = data.healthRelatedDocument
                                val fileName = document.fileName
                                val fileBytes = document.fileBytes
                                try {
                                    val path = Utilities.getAppFolderLocation(context!!)
                                    if (!File(path, fileName).exists()) {
                                        if (!Utilities.isNullOrEmpty(fileBytes)) {
                                            val decodedImage =
                                                fileUtils.convertBase64ToBitmap(fileBytes)
                                            if (decodedImage != null) {
                                                val saveRecordUri =
                                                    fileUtils.saveBitmapToExternalStorage(
                                                        context, decodedImage, fileName
                                                    )
                                                if (saveRecordUri != null) {
                                                    updateUserProfileImgPath(fileName, path)
                                                }
                                            }
                                        }
                                    } else {
                                        updateUserProfileImgPath(fileName, path)
                                    }
                                    activity.completeFilePath = "$path/$fileName"
                                    activity.setProfilePic()
                                    activity.stopImageShimmer()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    activity.stopImageShimmer()
                                }
                            }

                            Resource.Status.ERROR -> {
                                _progressBar.value = Event(Event.HIDE_PROGRESS)
                                if (it.errorNumber.equals("1100014", true)) {
                                    _sessionError.value = Event(true)
                                } else {
                                    toastMessage(it.errorMessage)
                                    activity.stopImageShimmer()
                                }
                            }

                            else -> {}
                        }
                    }
                } catch (e: Exception) {
                    Utilities.printException(e)
                }

            }
        }

    fun callUploadProfileImageApi(activity: EditProfileActivity, name: String, imageFile: File) =
        viewModelScope.launch(Dispatchers.Main) {
            val destPath: String = Utilities.getAppFolderLocation(context!!)
            var encodedImage = ""
            try {
                val bytesFile = ByteArray(imageFile.length().toInt())
                context.contentResolver.openFileDescriptor(Uri.fromFile(imageFile), "r")
                    ?.use { parcelFileDescriptor ->
                        FileInputStream(parcelFileDescriptor.fileDescriptor).use { inStream ->
                            inStream.read(bytesFile)
                            encodedImage = Base64.encodeToString(bytesFile, Base64.DEFAULT)
                        }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val PersonID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
                .toRequestBody("text/plain".toMediaTypeOrNull())
            val FileName = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val DocumentTypeCode = "PROFPIC".toRequestBody("text/plain".toMediaTypeOrNull())
            val ByteArray = encodedImage.toRequestBody("text/plain".toMediaTypeOrNull())
            val AuthTicket = authToken.toRequestBody("text/plain".toMediaTypeOrNull())

            _progressBar.value = Event(Constants.LOADER_UPLOAD)
            _uploadProfileImage.removeSource(uploadProfileImageSource)
            withContext(Dispatchers.IO) {
                uploadProfileImageSource = homeManagementUseCase.invokeUploadProfileImage(
                    PersonID, FileName, DocumentTypeCode, ByteArray, AuthTicket
                )
            }
            _uploadProfileImage.addSource(uploadProfileImageSource) {
                try {
                    _uploadProfileImage.value = it
                    it?.data?.let { data ->
                        when (it.status) {
                            Resource.Status.SUCCESS -> {
                                _progressBar.value = Event(Event.HIDE_PROGRESS)
                                val profileImageID = data.profileImageID
                                Utilities.printLog("UploadProfileImage----->$profileImageID")
                                if (!Utilities.isNullOrEmptyOrZero(profileImageID)) {
                                    activity.hasProfileImage = true
                                    activity.needToSet = true
                                    Utilities.toastMessageShort(
                                        context,
                                        localResource.getString(R.string.PROFILE_PHOTO_UPDATED)
                                    )
                                    updateUserProfileImgPath(name, destPath)
                                    activity.completeFilePath = "$destPath/$name"
                                    activity.setProfilePic()
                                }
                            }

                            Resource.Status.ERROR -> {
                                _progressBar.value = Event(Event.HIDE_PROGRESS)
                                if (it.errorNumber.equals("1100014", true)) {
                                    _sessionError.value = Event(true)
                                } else {
                                    toastMessage(it.errorMessage)
                                }
                            }

                            else -> {}
                        }
                    }
                } catch (e: Exception) {
                    Utilities.printException(e)
                }

            }
        }

    fun callRemoveProfileImageApi(activity: EditProfileActivity, context: Context) =
        viewModelScope.launch(Dispatchers.Main) {

            val requestData = RemoveProfileImageModel(
                Gson().toJson(
                    RemoveProfileImageModel.JSONDataRequest(
                        personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
                            .toInt()
                    ), RemoveProfileImageModel.JSONDataRequest::class.java
                ), authToken
            )

            _progressBar.value = Event(Constants.LOADER_DELETE)
            _removeProfileImage.removeSource(removeProfileImageSource)
            withContext(Dispatchers.IO) {
                removeProfileImageSource = homeManagementUseCase.invokeRemoveProfileImage(
                    isForceRefresh = true, data = requestData
                )
            }
            _removeProfileImage.addSource(removeProfileImageSource) {
                try {
                    _removeProfileImage.value = it
                    it?.data?.let { data ->
                        when (it.status) {
                            Resource.Status.SUCCESS -> {
                                _progressBar.value = Event(Event.HIDE_PROGRESS)
                                val isProcessed = data.isProcessed
                                Utilities.printLog("isProcessed----->$isProcessed")
                                if (isProcessed.equals(Constants.TRUE, ignoreCase = true)) {
                                    Utilities.toastMessageShort(
                                        context,
                                        context.resources.getString(R.string.PROFILE_PHOTO_REMOVED)
                                    )
                                    UserSingleton.getInstance()!!.clearData()
                                    activity.removeProfilePic()
                                } else {
                                    Utilities.toastMessageShort(
                                        context,
                                        context.resources.getString(R.string.ERROR_PROFILE_PHOTO)
                                    )
                                }
                            }

                            Resource.Status.ERROR -> {
                                _progressBar.value = Event(Event.HIDE_PROGRESS)
                                if (it.errorNumber.equals("1100014", true)) {
                                    _sessionError.value = Event(true)
                                } else {
                                    toastMessage(it.errorMessage)
                                }
                            }

                            else -> {}
                        }
                    }
                } catch (e: Exception) {
                    Utilities.printException(e)
                }


            }
        }

    fun callListDocumentsApi(forceRefresh: Boolean, from: String) =
        viewModelScope.launch(Dispatchers.Main) {

            val requestData = ListDocumentsModel(
                Gson().toJson(
                    ListDocumentsModel.JSONDataRequest(
                        searchCriteria = ListDocumentsModel.SearchCriteria(
                            personID = preferenceUtils.getPreference(
                                PreferenceConstants.PERSONID, "0"
                            )
                        )
                    ), ListDocumentsModel.JSONDataRequest::class.java
                ), authToken
            )

            if (from.equals(Constants.SWITCH_PROFILE, ignoreCase = true)) {
                _progressBar.value = Event("Getting Health Records...")
            }
            _listDocuments.removeSource(listDocumentsSource)
            withContext(Dispatchers.IO) {
                listDocumentsSource = homeManagementUseCase.invokeDocumentList(
                    isForceRefresh = forceRefresh, data = requestData
                )
            }
            _listDocuments.addSource(listDocumentsSource) {
                try {
                    _listDocuments.postValue(it)
                    it?.data?.let { data ->
                        when (it.status) {
                            Resource.Status.SUCCESS -> {
                                _progressBar.value = Event(Event.HIDE_PROGRESS)
                                val list = data.documents.filter {
                                    it.PersonId.toString() == preferenceUtils.getPreference(
                                        PreferenceConstants.PERSONID, "0"
                                    )
                                }
                                Utilities.printLog("RecordCount----->${list.size}")
                                allHealthDocuments.postValue(list)
                            }

                            Resource.Status.ERROR -> {
                                _progressBar.value = Event(Event.HIDE_PROGRESS)
                                if (it.errorNumber.equals("1100014", true)) {
                                    _sessionError.value = Event(true)
                                } else {
                                    toastMessage(it.errorMessage)
                                }
                            }

                            else -> {}
                        }
                    }
                } catch (e: Exception) {
                    Utilities.printException(e)
                }


            }
        }

    fun callSudPolicyByMobileNumberApi(fragment: FragmentProfile) =
        viewModelScope.launch(Dispatchers.Main) {

            val obj = JsonObject()
            obj.addProperty("api_code", Constants.SUD_MOBILE_NUMBER_DETAILS)
            obj.addProperty("policy_number", "")
            obj.addProperty(
                "mobile_number",
                preferenceUtils.getPreference(PreferenceConstants.POLICY_MOBILE_NUMBER, "")
            )
            val requestData = SudPolicyByMobileNumberModel(
                obj, preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
            )

            //_progressBar.value = Event("")
            _sudPolicyByMobileNumber.removeSource(sudPolicyByMobileNumberSource)
            withContext(Dispatchers.IO) {
                sudPolicyByMobileNumberSource =
                    sudLifePolicyManagementUseCase.invokeSudPolicyByMobileNumber(data = requestData)
            }
            _sudPolicyByMobileNumber.addSource(sudPolicyByMobileNumberSource) {

                try {
                    _sudPolicyByMobileNumber.value = it
                    it?.data?.let { data ->

                        when (it.status) {
                            Resource.Status.SUCCESS -> {
                                //_progressBar.value = Event(Event.HIDE_PROGRESS)
                                //Utilities.printLog("Response--->" + it.data!!)
                                when (data.result.status) {
                                    "1" -> {
                                        fragment.policyListSizeFinal = data.result.records.size
                                        if (data.result.records.isNotEmpty()) {
                                            for (item in data.result.records) {
                                                callSudKypApi(item.policyNumber!!, fragment)
                                            }
                                        } else {
                                            fragment.showNoDataView()
                                        }
                                    }

                                    "0" -> {
                                        fragment.showNoDataView()
                                    }
                                }

                                */
/*                    if ( it.data!!.status == "1" && it.data!!.records.isNotEmpty() ) {
                                        fragment.policyListSizeFinal = it.data!!.records.size
                                        if ( !it.data!!.records.isNullOrEmpty() ) {
                                            for (item in it.data!!.records) {
                                                callPolicyDetailsByPolicyNumberApi(item.policyNumber!!,fragment)
                                            }
                                        } else {
                                            fragment.showNoDataView()
                                        }
                                    }*//*

                            }

                            Resource.Status.ERROR -> {
                                _progressBar.value = Event(Event.HIDE_PROGRESS)
                                toastMessage(it.errorMessage)
                            }

                            else -> {}
                        }
                    }
                } catch (e: Exception) {
                    Utilities.printException(e)
                }


            }

        }

    private fun callSudKypApi(policy_Number: String, fragment: FragmentProfile) {
        try {
            val obj = JsonObject()
            obj.addProperty("api_code", Constants.SUD_KYP)
            obj.addProperty("policy_number", policy_Number)
            //obj.addProperty("mobile_number","")
            val requestData =
                SudKYPModel(obj, preferenceUtils.getPreference(PreferenceConstants.TOKEN, ""))

            //_progressBar.value = Event("")
            val downloadService = provideRetrofit().create(SudPolicyApiService::class.java)
            val call = downloadService.getSudKyp(requestData)
            call.enqueue(object : Callback<SudKYPModel.SudKYPResponse> {
                override fun onResponse(
                    call: Call<SudKYPModel.SudKYPResponse>,
                    response: Response<SudKYPModel.SudKYPResponse>
                ) {
                    if (response.body() != null) {
                        val result = response.body()!!
                        if (response.isSuccessful) {
                            Utilities.printData("PolicyResp", result, true)
                            if (result.result.status == "1" && result.result.kYPList.isNotEmpty()) {
                                fragment.addPolicyInList(result.result.kYPList[0])
                            }
                            if (result.result.status == "2") {
                                fragment.policyListSize++
                                Utilities.printLogError("policyListSizeFinal--->${fragment.policyListSizeFinal}")
                                Utilities.printLogError("policyListSize--->${fragment.policyListSize}")
                                Utilities.printLogError("No data found for Policy--->$policy_Number")
                                if (fragment.policyListSizeFinal == fragment.policyListSize) {
                                    //fragment.notifyList(fragment.sudPolicyList)
                                    fragment.notifyList()
                                    //fragment.policyDataSingleton!!.policyList = fragment.sudPolicyList
                                }
                            }
                        } else {
                            Utilities.printLogError("Server Contact failed")
                        }
                    } else {
                        Utilities.printLogError("response.body is null")
                    }
                    //_progressBar.value = Event(Event.HIDE_PROGRESS)
                }

                override fun onFailure(call: Call<SudKYPModel.SudKYPResponse>, t: Throwable) {
                    _progressBar.value = Event(Event.HIDE_PROGRESS)
                    Utilities.printLog("Api failed" + t.printStackTrace())
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun provideRetrofit(): Retrofit {
        val logging = HttpLoggingInterceptor { message ->
            Utilities.printLog("HttpLogging--> $message")
        }
        logging.level = HttpLoggingInterceptor.Level.BODY
        return Retrofit.Builder().client(
            OkHttpClient.Builder().retryOnConnectionFailure(true)
                .protocols(listOf(Protocol.HTTP_1_1)).addInterceptor(logging)
                .connectTimeout(3, TimeUnit.MINUTES).writeTimeout(3, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES).build()
        ).baseUrl(Constants.strAPIUrl).addConverterFactory(GsonConverterFactory.create()).build()
    }

    */
/*    fun updateUserMobileNumber(phone: String) = viewModelScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                val userDetails = homeManagementUseCase.invokeGetLoggedInPersonDetails()
                var dob = ""
                try {
                    val db = DateHelper.formatDateValue(DateHelper.SERVER_DATE_YYYYMMDD, userDetails.dateOfBirth!!)!!
                    if (db != null) {
                        dob = db
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                val userUpdateDetails = UserRelatives(
                    relativeID = personId,
                    firstName = userDetails.firstName,
                    lastName = "",
                    dateOfBirth = dob,
                    age = userDetails.age.toString(),
                    gender = userDetails.gender,
                    contactNo = phone,
                    emailAddress = userDetails.emailAddress)
                Utilities.printLog("userUpdateDetails----->$userUpdateDetails")
                callUpdateRelativesApi(true, userUpdateDetails, Constants.USER)
            }
        }*//*


    fun getLoggedInPersonDetails() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            userDetails.postValue(homeManagementUseCase.invokeGetLoggedInPersonDetails())
        }
    }

    fun updateUserDetails(name: String, dob: String, personId: Int) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            homeManagementUseCase.invokeUpdateUserDetails(name, dob, personId)
            preferenceUtils.storePreference(PreferenceConstants.FIRSTNAME, name)
            preferenceUtils.storePreference(PreferenceConstants.DOB, dob)
        }
    }

    fun updateUserProfileImgPath(name: String, path: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            homeManagementUseCase.invokeUpdateUserProfileImgPath(name, path)
        }
    }

    fun getAllUserRelatives() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            userRelativesList.postValue(homeManagementUseCase.invokeGetUserRelatives())
        }
    }

    fun getAllHealthRecordsDocuments() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val list = homeManagementUseCase.invokeGetAllHealthDocuments().filter {
                it.PersonId.toString() == preferenceUtils.getPreference(
                    PreferenceConstants.PERSONID, "0"
                )
            }
            allHealthDocuments.postValue(list)
        }
    }

    fun getRelativesList() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            userRelativesList.postValue(homeManagementUseCase.invokeGetUserRelativesExceptSelf())
        }
    }

    fun getUserRelativeSpecific(relationShipCode: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            alreadyExistRelatives.postValue(
                homeManagementUseCase.invokeGetUserRelativeSpecific(
                    relationShipCode
                )
            )
        }
    }

    fun getUserRelativeForRelativeId(relativeId: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            alreadyExistRelatives.postValue(
                homeManagementUseCase.invokeGetUserRelativeForRelativeId(
                    relativeId
                )
            )
        }
    }


    fun getFamilyRelationshipList() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val user = homeManagementUseCase.invokeGetUserRelativeDetailsByRelativeId(
                preferenceUtils.getPreference(
                    PreferenceConstants.PERSONID, "0"
                )
            )
            val gender = user.gender
            Utilities.printLogError("Gender--->$gender")
            if (gender.contains("1", ignoreCase = true)) {
                familyRelationList.postValue(dataHandler.getFamilyRelationListMale())
            } else {
                familyRelationList.postValue(dataHandler.getFamilyRelationListFemale())
            }
        }
    }

    fun getRelationImgId(relationshipCode: String): Int {
        var relationImgTobeAdded: Int = R.drawable.img_husband
        when (relationshipCode) {
            "FAT" -> {
                relationImgTobeAdded = R.drawable.img_father
            }

            "MOT" -> {
                relationImgTobeAdded = R.drawable.img_mother
            }

            "SON" -> {
                relationImgTobeAdded = R.drawable.img_son
            }

            "DAU" -> {
                relationImgTobeAdded = R.drawable.img_daughter
            }

            "GRF" -> {
                relationImgTobeAdded = R.drawable.img_gf
            }

            "GRM" -> {
                relationImgTobeAdded = R.drawable.img_gm
            }

            "HUS" -> {
                relationImgTobeAdded = R.drawable.img_husband
            }

            "WIF" -> {
                relationImgTobeAdded = R.drawable.img_wife
            }

            "BRO" -> {
                relationImgTobeAdded = R.drawable.img_brother
            }

            "SIS" -> {
                relationImgTobeAdded = R.drawable.img_sister
            }
        }
        return relationImgTobeAdded
    }

    fun refreshPersonId() {
        personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
        gender = preferenceUtils.getPreference(PreferenceConstants.GENDER, "")
        relationshipCode = preferenceUtils.getPreference(PreferenceConstants.RELATIONSHIPCODE, "")
    }

    fun updateFirstName(firstName: String) {
        preferenceUtils.storePreference(PreferenceConstants.FIRSTNAME, firstName)
    }

}*/

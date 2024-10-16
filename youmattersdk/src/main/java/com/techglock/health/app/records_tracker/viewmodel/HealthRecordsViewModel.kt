package com.techglock.health.app.records_tracker.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.techglock.health.app.R
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.constants.CleverTapConstants
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.PreferenceConstants
import com.techglock.health.app.common.utils.*
import com.techglock.health.app.model.entity.DocumentType
import com.techglock.health.app.model.entity.HealthDocument
import com.techglock.health.app.model.entity.RecordInSession
import com.techglock.health.app.model.entity.UserRelatives
import com.techglock.health.app.model.shr.*
import com.techglock.health.app.model.shr.DownloadDocumentModel.HealthRelatedDocument
import com.techglock.health.app.model.shr.ListDocumentsModel.SearchCriteria
import com.techglock.health.app.model.shr.SaveDocumentModel.HealthDoc
import com.techglock.health.app.records_tracker.common.DataHandler
import com.techglock.health.app.records_tracker.common.RecordSingleton
import com.techglock.health.app.records_tracker.domain.ShrManagementUseCase
import com.techglock.health.app.records_tracker.ui.DigitizeRecordFragment
import com.techglock.health.app.records_tracker.ui.DigitizeRecordListFragmentDirections
import com.techglock.health.app.records_tracker.ui.ViewRecordsFragmentDirections
import com.techglock.health.app.repository.utils.Resource
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileInputStream
import java.util.*
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class HealthRecordsViewModel @Inject constructor(
    application: Application,
    val shrManagementUseCase: ShrManagementUseCase,
    private val preferenceUtils: PreferenceUtils,
    val context: Context?,
    val dataHandler: DataHandler
) : BaseViewModel(application) {

    private val fileUtils = FileUtils

    var adminPersonId = preferenceUtils.getPreference(PreferenceConstants.ADMIN_PERSON_ID, "0")
    var personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
    var authToken = preferenceUtils.getPreference(PreferenceConstants.TOKEN, "")
    var firstName = preferenceUtils.getPreference(PreferenceConstants.FIRSTNAME, "")
    var gender = preferenceUtils.getPreference(PreferenceConstants.GENDER, "")

    private val localResource =
        LocaleHelper.getLocalizedResources(context!!, Locale(LocaleHelper.getLanguage(context)))!!

    val documentTypeList = MutableLiveData<List<DocumentType>>()
    val recordsInSessionList = MutableLiveData<List<RecordInSession>>()
    val healthDocumentsList = MutableLiveData<List<HealthDocument>>()
    val userRelativesList = MutableLiveData<List<UserRelatives>>()
    val recordToUploadRequestList = MutableLiveData<List<HealthDoc>>()

    val documentStatus = MutableLiveData<Resource.Status>()
    val postDownload = MutableLiveData<String>()

    var listDocumentsSource: LiveData<Resource<ListDocumentsModel.ListDocumentsResponse>> =
        MutableLiveData()
    val _listDocuments = MediatorLiveData<Resource<ListDocumentsModel.ListDocumentsResponse>>()
    val listDocuments: LiveData<Resource<ListDocumentsModel.ListDocumentsResponse>> get() = _listDocuments

    var saveDocSource: LiveData<Resource<SaveDocumentModel.SaveDocumentsResponse>> =
        MutableLiveData()
    val _saveDocument = MediatorLiveData<SaveDocumentModel.SaveDocumentsResponse?>()
    val saveDocument: LiveData<SaveDocumentModel.SaveDocumentsResponse?> get() = _saveDocument

    var deleteDocSource: LiveData<Resource<DeleteDocumentModel.DeleteDocumentResponse>> =
        MutableLiveData()
    val _deleteDocument = MediatorLiveData<DeleteDocumentModel.DeleteDocumentResponse?>()
    val deleteDocument: LiveData<DeleteDocumentModel.DeleteDocumentResponse?> get() = _deleteDocument

    var downloadDocSource: LiveData<Resource<DownloadDocumentModel.DownloadDocumentResponse>> =
        MutableLiveData()
    val _downloadDoc = MediatorLiveData<DownloadDocumentModel.DownloadDocumentResponse?>()
    val downloadDoc: LiveData<DownloadDocumentModel.DownloadDocumentResponse?> get() = _downloadDoc

    var ocrUnitExistSource: LiveData<Resource<OCRUnitExistModel.OCRUnitExistResponse>> =
        MutableLiveData()
    val _ocrUnitExist = MediatorLiveData<OCRUnitExistModel.OCRUnitExistResponse?>()
    val ocrUnitExist: LiveData<OCRUnitExistModel.OCRUnitExistResponse?> get() = _ocrUnitExist

    var ocrSaveDocumentSource: LiveData<Resource<OCRSaveModel.OCRSaveResponse>> = MutableLiveData()
    val _ocrSaveDocument = MediatorLiveData<OCRSaveModel.OCRSaveResponse?>()
    val ocrSaveDocument: LiveData<OCRSaveModel.OCRSaveResponse?> get() = _ocrSaveDocument

    var ocrDigitizeDocumentSource: LiveData<Resource<OcrResponce>> = MutableLiveData()
    val _ocrDigitizeDocument = MediatorLiveData<OcrResponce?>()
    val ocrDigitizeDocument: LiveData<OcrResponce?> get() = _ocrDigitizeDocument

    /*    private val _documentStatus = MutableLiveData<Resource.Status>()
        val documentStatus: LiveData<Resource.Status> get() = _documentStatus*/

    /*    private val _postDownload = MutableLiveData<String>()
        val postDownload: LiveData<String> get() = _postDownload*/

    fun callListDocumentsApi(forceRefresh: Boolean) = viewModelScope.launch(Dispatchers.Main) {
        val requestData = ListDocumentsModel(
            Gson().toJson(
                ListDocumentsModel.JSONDataRequest(
                    searchCriteria = SearchCriteria(
                        personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
                    )
                ), ListDocumentsModel.JSONDataRequest::class.java
            ), authToken
        )

        _listDocuments.removeSource(listDocumentsSource)
        withContext(Dispatchers.IO) {
            listDocumentsSource = shrManagementUseCase.invokeDocumentList(
                isForceRefresh = forceRefresh, data = requestData
            )
        }
        _listDocuments.addSource(listDocumentsSource) {
            //_listDocuments.value = it.data
            _listDocuments.postValue(it)

            if (it.status == Resource.Status.SUCCESS) {
                if (it.data != null) {
                    val list = it.data.documents
                    Utilities.printLog("RecordCount----->${list.size}")
                }
            }
            if (it.status == Resource.Status.ERROR) {
                if (it.errorNumber.equals("1100014", true)) {
                    _sessionError.value = Event(true)
                } else {
                    toastMessage(it.errorMessage)
                }
            }
        }
    }

    fun callSaveDocumentApi(
        from: String,
        code: String,
        healthDocumentsList: List<HealthDoc>,
        personName: String,
        personRel: String,
        view: View
    ) = viewModelScope.launch(Dispatchers.Main) {
        Utilities.printLog("RecordToUploadRequestList----->${healthDocumentsList.size}")
        val requestDataSave = SaveDocumentModel(
            Gson().toJson(
                SaveDocumentModel.JSONDataRequest(
                    personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0"),
                    from = 71,
                    healthDocuments = healthDocumentsList
                ), SaveDocumentModel.JSONDataRequest::class.java
            ), authToken
        )

        _progressBar.value = Event(Constants.LOADER_UPLOAD)

        _saveDocument.removeSource(saveDocSource)
        withContext(Dispatchers.IO) {
            saveDocSource = shrManagementUseCase.invokeSaveRecordToServer(
                isForceRefresh = true,
                data = requestDataSave,
                personName = personName,
                personRel = personRel,
                healthDocumentsList = healthDocumentsList
            )
        }
        _saveDocument.addSource(saveDocSource) {
            try {
                _saveDocument.value = it.data
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        if (it.data != null) {
                            if (it.data.healthDocuments.isNotEmpty()) {
                                toastMessage(localResource.getString(R.string.MSG_RECORD_SAVED))
                                val bundle = Bundle()
                                bundle.putString("from", from)
                                bundle.putString("code", code)
                                view.findNavController().navigate(
                                    R.id.action_selectRelationFragment_to_viewRecordsFragment,
                                    bundle
                                )
                            }
                            CleverTapHelper.pushEvent(
                                context, CleverTapConstants.ADD_HEALTH_RECORD
                            )
//                            FirebaseHelper.logCustomFirebaseEvent(FirebaseConstants.HEALTH_RECORDS_UPLOAD_EVENT)
                        }
                    }

                    Resource.Status.ERROR -> {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        toastMessage(it.errorMessage)
                    }

                    else -> {}
                }
            } catch (e: Exception) {
                Utilities.printException(e)
            }
        }
    }

    fun callDeleteRecordsApi(deleteRecordIds: List<String>) =
        viewModelScope.launch(Dispatchers.Main) {
            Utilities.printLog("DeleteRecordIds----->$deleteRecordIds")
            val requestData = DeleteDocumentModel(
                Gson().toJson(
                    DeleteDocumentModel.JSONDataRequest(
                        personID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0"),
                        documentIDS = deleteRecordIds
                    ), DeleteDocumentModel.JSONDataRequest::class.java
                ), authToken
            )

            _progressBar.value = Event(Constants.LOADER_DELETE)
            _deleteDocument.removeSource(deleteDocSource)
            withContext(Dispatchers.IO) {
                deleteDocSource = shrManagementUseCase.invokeDeleteRecordFromServer(
                    true, requestData, deleteRecordIds
                )
            }
            _deleteDocument.addSource(deleteDocSource) {
                try {
                    _deleteDocument.value = it.data
                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            if (it.data != null) {
                                val isProcessed = it.data.isProcessed
                                if (isProcessed.equals(Constants.TRUE, ignoreCase = true)) {
                                    documentStatus.postValue(it.status)
                                    toastMessage(localResource.getString(R.string.MSG_RECORD_DELETED))
                                    CleverTapHelper.pushEvent(
                                        context, CleverTapConstants.DELETE_HEALTH_RECORD
                                    )
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

    fun callDownloadRecordApi(from: String, record: HealthDocument) =
        viewModelScope.launch(Dispatchers.Main) {

            val documentId = record.Id.toString()
            val fileName = record.Name!!
            val requestData = DownloadDocumentModel(
                Gson().toJson(
                    DownloadDocumentModel.JSONDataRequest(
                        PersonID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0"),
                        DocumentID = documentId
                    ), DownloadDocumentModel.JSONDataRequest::class.java
                ), authToken
            )

            _progressBar.value = Event(Constants.LOADER_DOWNLOAD)
            //_progressBarType.value = Event(Constants.LOADER_DOWNLOAD)

            _downloadDoc.removeSource(downloadDocSource)
            withContext(Dispatchers.IO) {
                downloadDocSource = shrManagementUseCase.invokeDownloadDocumentFromServer(
                    isForceRefresh = true, data = requestData
                )
            }
            _downloadDoc.addSource(downloadDocSource) {
                try {
                    _downloadDoc.value = it.data
                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            saveDownloadedRecord(
                                from, it.data!!.healthRelatedDocument, it.status, fileName
                            )
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            toastMessage(localResource.getString(R.string.MSG_REOCRD_DOWNLOADED))
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

    fun callDigitizeDocumentApi(from: String, categoryCode: String, record: HealthDocument) =
        viewModelScope.launch(Dispatchers.Main) {

            var fileBytes = ""
            val path = record.Path!!
            val name = record.Name!!
            val uri = record.FileUri
            try {
                //val file = DocumentFile.fromTreeUri(context, record.FileUri.toUri())!!
                val file = File(path, name)
                if (file.exists()) {
                    val bytesFile = ByteArray(file.length().toInt())
                    context!!.contentResolver.openFileDescriptor(Uri.fromFile(file), "r")
                        ?.use { parcelFileDescriptor ->
                            FileInputStream(parcelFileDescriptor.fileDescriptor).use { inStream ->
                                inStream.read(bytesFile)
                                fileBytes = Base64.encodeToString(bytesFile, Base64.DEFAULT)
                            }
                        }
                }
            } catch (e: Exception) {
                Utilities.printLog("Error Digitizing document")
                e.printStackTrace()
            }
            val fileExt = name.substring(name.lastIndexOf('.') + 1)
            val partnercode = Constants.PartnerCode
            // Request Parameters in Parts
            val bytes = fileBytes.toRequestBody("text/plain".toMediaTypeOrNull())
            val pcode = partnercode.toRequestBody("text/plain".toMediaTypeOrNull())
            val ext = fileExt.toRequestBody("text/plain".toMediaTypeOrNull())

            //Utilities.toastMessageShort(context,context.resources.getString(R.string.MSG_DIGITIZING_RECORD))
            _progressBar.value = Event(Constants.LOADER_DIGITIZE)

            _ocrDigitizeDocument.removeSource(ocrDigitizeDocumentSource)
            withContext(Dispatchers.IO) {
                ocrDigitizeDocumentSource =
                    shrManagementUseCase.invokeocrDigitizeDocument(bytes, pcode, ext)
            }
            _ocrDigitizeDocument.addSource(ocrDigitizeDocumentSource) {
                try {
                    _ocrDigitizeDocument.value = it.data
                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            _progressBar.value = Event(Event.HIDE_PROGRESS)
                            if (it.data != null) {
                                Utilities.printLog("DATA==>${it.data}")
                                val digitizedParametersList = it.data.body.healthDataParameters
                                if (digitizedParametersList.isNotEmpty()) {
                                    RecordSingleton.getInstance()!!.setHealthRecord(record)
                                    RecordSingleton.getInstance()!!
                                        .setDigitizedParamList(digitizedParametersList)
                                    if (from.equals(Constants.DIGITIZE, ignoreCase = true)) {
                                        navigate(
                                            DigitizeRecordListFragmentDirections.actionDigitizedRecordsListFragmentToFragmentDigitize(
                                                from, categoryCode, uri
                                            )
                                        )
                                    } else {
                                        navigate(
                                            ViewRecordsFragmentDirections.actionViewRecordsFragmentToFragmentDigitize(
                                                from, categoryCode, uri
                                            )
                                        )
                                    }
                                } else {
                                    toastMessage(localResource.getString(R.string.ERROR_UNABLE_TO_READ_FILE))
                                }
                            } else {
                                //toastMessage(localResource.getString(R.string.ERROR_DOC_CANNOT_DIGITIZE))
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

    fun callCheckUnitExistApi(forceRefresh: Boolean, parameterCode: String, unit: String) =
        viewModelScope.launch(Dispatchers.Main) {

            val requestData = OCRUnitExistModel(
                Gson().toJson(
                    OCRUnitExistModel.JSONDataRequest(
                        parameterCode = parameterCode, unit = unit
                    ), OCRUnitExistModel.JSONDataRequest::class.java
                ), authToken
            )

            _ocrUnitExist.removeSource(ocrUnitExistSource)
            withContext(Dispatchers.IO) {
                ocrUnitExistSource = shrManagementUseCase.invokeCheckUnitExist(
                    isForceRefresh = forceRefresh, data = requestData
                )
            }
            _ocrUnitExist.addSource(ocrUnitExistSource) {
                try {
                    _ocrUnitExist.value = it.data
                    when (it.status) {
                        Resource.Status.SUCCESS -> {
                            if (it.data != null) {
                                Utilities.printLog("IsUnitExist----->${it.data.isExist}")
                            }
                        }

                        Resource.Status.ERROR -> {
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

    fun callOcrSaveDocumentApi(
        view: View,
        fragment: DigitizeRecordFragment,
        records: List<HealthDataParameter>,
        recordeDate: String,
        perId: String
    ) = viewModelScope.launch(Dispatchers.Main) {

        val labRecords: ArrayList<OCRSaveModel.LabRecord> = arrayListOf()
        var record: OCRSaveModel.LabRecord
        for (item in records) {
            record = OCRSaveModel.LabRecord(
                item.paramCode, perId, recordeDate, "", item.unit, item.observation
            )
            labRecords.add(record)
        }

        val requestData = OCRSaveModel(
            Gson().toJson(
                OCRSaveModel.JSONDataRequest(
                    LabRecords = labRecords
                ), OCRSaveModel.JSONDataRequest::class.java
            ), authToken
        )

        _progressBar.value = Event("Saving data.....")
        _ocrSaveDocument.removeSource(ocrSaveDocumentSource)
        withContext(Dispatchers.IO) {
            ocrSaveDocumentSource = shrManagementUseCase.invokeOcrSaveDocument(
                isForceRefresh = true, data = requestData
            )
        }
        _ocrSaveDocument.addSource(ocrSaveDocumentSource) {
            try {
                _ocrSaveDocument.value = it.data
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        _progressBar.value = Event(Event.HIDE_PROGRESS)
                        //toastMessage(localResource.getString(R.string.MSG_DATA_UPLOADED))
                        fragment.performBackClick(view)
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

    private fun saveDownloadedRecord(
        from: String, document: HealthRelatedDocument, status: Resource.Status, fileName: String
    ) = viewModelScope.launch(Dispatchers.Main) {
        try {
            val byteArray = document.FileBytes
            val decodedString = Base64.decode(byteArray, Base64.DEFAULT)
            if (decodedString != null) {
                val documentId = document.ID
                val path = Utilities.getAppFolderLocation(context!!)
                val sync = "N"
                val extension = fileUtils.getFileExt(fileName).uppercase()
                val saveRecord = when (extension) {
                    "PNG", "GIF", "BMP", "JPEG", "TIF", "TIFF", "ICO", "JPG" -> {
                        fileUtils.saveByteArrayToExternalStorage(context, decodedString, fileName)
                    }

                    else -> {
                        fileUtils.saveByteArrayToExternalStorage(context, decodedString, fileName)
                    }
                }
                if (saveRecord != null) {
                    withContext(Dispatchers.IO) {
                        RecordSingleton.getInstance()!!.getHealthRecord().Path = path
                        RecordSingleton.getInstance()!!.getHealthRecord().FileUri =
                            Uri.fromFile(saveRecord).toString()
                        shrManagementUseCase.invokeUpdateHealthRecordPathSync(
                            documentId, path, Uri.fromFile(saveRecord).toString(), sync
                        )
                        documentStatus.postValue(status)
                        postDownload.postValue(from)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showMessage(str: String) {
        toastMessage(str)
    }

    fun getDocumentTypesList() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            documentTypeList.postValue(shrManagementUseCase.invokeGetDocumentTypes())
        }
    }

    fun getAllHealthDocuments() = viewModelScope.launch(Dispatchers.Main) {
        withContext(Dispatchers.IO) {
            healthDocumentsList.postValue(shrManagementUseCase.invokeGetAllHealthDocuments())
        }
    }

    fun getHealthDocumentsWhereCode(code: String) = viewModelScope.launch(Dispatchers.Main) {
        withContext(Dispatchers.IO) {
            if (!code.equals("ALL", ignoreCase = true)) {
                healthDocumentsList.postValue(
                    shrManagementUseCase.invokeGetHealthDocumentsWhereCode(
                        code
                    )
                )
            } else {
                healthDocumentsList.postValue(shrManagementUseCase.invokeGetAllHealthDocuments())
            }
        }
    }

    fun getRecordsInSession() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            recordsInSessionList.postValue(shrManagementUseCase.invokeGetRecordsInSession())
        }
    }

    fun getUserRelatives() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val list: MutableList<UserRelatives> = mutableListOf()/*            if ( adminPersonId != personId ) {
                            list = shrManagementUseCase.invokeGetUserRelatives().filter { it.relativeID == personId }.toMutableList()
                        } else {
                            list = shrManagementUseCase.invokeGetUserRelatives().toMutableList()
                        }*/
            //list = shrManagementUseCase.invokeGetUserRelatives().filter { it.relativeID == personId }.toMutableList()
            val user = UserRelatives(
                relativeID = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0"),
                firstName = preferenceUtils.getPreference(PreferenceConstants.FIRSTNAME, ""),
                lastName = "",
                dateOfBirth = DateHelper.getDateTimeAs_ddMMMyyyy(
                    preferenceUtils.getPreference(
                        PreferenceConstants.DOB, ""
                    )
                ),
                //age = userDetails.age.toDouble().toInt().toString(),
                gender = preferenceUtils.getPreference(PreferenceConstants.GENDER, ""),
                contactNo = preferenceUtils.getPreference(PreferenceConstants.PHONE, ""),
                emailAddress = preferenceUtils.getPreference(PreferenceConstants.EMAIL, ""),
                relationshipCode = "SELF",
                relationship = "Self"
            )
            list.add(user)
            userRelativesList.postValue(list)
        }
    }

    fun getRecordToUploadList(
        code: String,
        comment: String,
        relativeId: String,
        personRelation: String,
        personName: String
    ) = viewModelScope.launch(Dispatchers.Main) {
        withContext(Dispatchers.IO) {
            recordToUploadRequestList.postValue(
                shrManagementUseCase.invokeCreateUploadList(
                    code,
                    comment,
                    preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0"),
                    relativeId,
                    personRelation,
                    personName
                )
            )
        }
    }

    fun saveRecordsInSession(recordsInSession: RecordInSession) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            shrManagementUseCase.invokeSaveRecordsInSession(recordsInSession)
        }
    }

    fun deleteRecordInSession(record: RecordInSession) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            shrManagementUseCase.invokeDeleteRecordInSession(record)
        }
    }

    fun deleteRecordsInSessionTable() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            shrManagementUseCase.invokeDeleteRecordsInSessionTable()
        }
    }

    fun isSelfUser(): Boolean {
        val personId = preferenceUtils.getPreference(PreferenceConstants.PERSONID, "0")
        val adminPersonId = preferenceUtils.getPreference(PreferenceConstants.ADMIN_PERSON_ID, "0")
        Utilities.printLogError("PersonId--->$personId")
        Utilities.printLogError("AdminPersonId--->$adminPersonId")
        var isSelfUser = false
        if (!Utilities.isNullOrEmptyOrZero(personId) && !Utilities.isNullOrEmptyOrZero(adminPersonId) && personId == adminPersonId) {
            isSelfUser = true
        }
        return isSelfUser
    }

}
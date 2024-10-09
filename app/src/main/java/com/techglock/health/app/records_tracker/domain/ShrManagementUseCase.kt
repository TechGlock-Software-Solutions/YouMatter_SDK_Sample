package com.techglock.health.app.records_tracker.domain

import androidx.lifecycle.LiveData
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.model.entity.DocumentType
import com.techglock.health.app.model.entity.HealthDocument
import com.techglock.health.app.model.entity.RecordInSession
import com.techglock.health.app.model.entity.UserRelatives
import com.techglock.health.app.model.shr.DeleteDocumentModel
import com.techglock.health.app.model.shr.DownloadDocumentModel
import com.techglock.health.app.model.shr.ListDocumentsModel
import com.techglock.health.app.model.shr.OCRSaveModel
import com.techglock.health.app.model.shr.OCRUnitExistModel
import com.techglock.health.app.model.shr.OcrResponce
import com.techglock.health.app.model.shr.SaveDocumentModel
import com.techglock.health.app.repository.StoreRecordRepository
import com.techglock.health.app.repository.utils.Resource
import okhttp3.RequestBody
import javax.inject.Inject

class ShrManagementUseCase @Inject constructor(private val repository: StoreRecordRepository) {

    suspend fun invokeDocumentList(
        isForceRefresh: Boolean,
        data: ListDocumentsModel
    ): LiveData<Resource<ListDocumentsModel.ListDocumentsResponse>> {
        return repository.fetchDocumentList(isForceRefresh, data)
    }

    suspend fun invokeDeleteRecordFromServer(
        isForceRefresh: Boolean,
        data: DeleteDocumentModel,
        deleteRecordIds: List<String>
    ): LiveData<Resource<DeleteDocumentModel.DeleteDocumentResponse>> {
        return repository.deleteRecordFromServer(isForceRefresh, data, deleteRecordIds)
    }

    suspend fun invokeDownloadDocumentFromServer(
        isForceRefresh: Boolean,
        data: DownloadDocumentModel
    ): LiveData<Resource<DownloadDocumentModel.DownloadDocumentResponse>> {
        return repository.downloadDocumentFromServer(isForceRefresh, data)
    }

    suspend fun invokeSaveRecordToServer(
        isForceRefresh: Boolean,
        data: SaveDocumentModel,
        personName: String,
        personRel: String,
        healthDocumentsList: List<SaveDocumentModel.HealthDoc>
    ): LiveData<Resource<SaveDocumentModel.SaveDocumentsResponse>> {
        return repository.saveRecordToServer(
            isForceRefresh,
            data,
            personName,
            personRel,
            healthDocumentsList
        )
    }

    suspend fun invokeCheckUnitExist(
        isForceRefresh: Boolean,
        data: OCRUnitExistModel
    ): LiveData<Resource<OCRUnitExistModel.OCRUnitExistResponse>> {
        return repository.checkUnitExist(isForceRefresh, data)
    }

    suspend fun invokeOcrSaveDocument(
        isForceRefresh: Boolean,
        data: OCRSaveModel
    ): LiveData<Resource<OCRSaveModel.OCRSaveResponse>> {
        return repository.ocrSaveDocument(isForceRefresh, data)
    }

    suspend fun invokeocrDigitizeDocument(
        fileBytes: RequestBody,
        partnerCode: RequestBody,
        fileExtension: RequestBody
    ): LiveData<Resource<OcrResponce>> {
        return repository.ocrDigitizeDocument(fileBytes, partnerCode, fileExtension)
    }

    suspend fun invokeCreateUploadList(
        code: String,
        comment: String,
        personID: String,
        relativeId: String,
        relation: String,
        personName: String
    ): MutableList<SaveDocumentModel.HealthDoc> {
        return repository.createUploadList(
            code,
            comment,
            personID,
            relativeId,
            relation,
            personName
        )
    }

    suspend fun invokeGetDocumentTypes(): List<DocumentType> {
        return repository.getDocumentTypes()
    }

    suspend fun invokeGetRecordsInSession(): List<RecordInSession> {
        return repository.getRecordsInSession()
    }


    suspend fun invokeGetUserRelatives(): List<UserRelatives> {
        return repository.getUserRelatives()
    }

    suspend fun invokeGetHealthDocumentsWhereCode(code: String): List<HealthDocument> {
        return repository.getHealthDocumentsWhereCode(code)
    }

    suspend fun invokeGetAllHealthDocuments(): List<HealthDocument> {
        return repository.getAllHealthDocuments()
    }

    suspend fun invokeUpdateHealthRecordPathSync(
        id: String,
        path: String,
        fileUri: String,
        sync: String
    ) {
        Utilities.printLogError("Updating Record Path......!!!!!!!!!!")
        repository.updateHealthRecordPathSync(id, path, fileUri, sync)
    }

    suspend fun invokeSaveRecordsInSession(recordsInSession: RecordInSession) {
        repository.saveRecordsInSession(recordsInSession)
    }

    suspend fun invokeDeleteRecordInSession(record: RecordInSession) {
        repository.deleteRecordInSession(record)
    }

    suspend fun invokeDeleteRecordsInSessionTable() {
        repository.deleteRecordInSessionTable()
    }

}
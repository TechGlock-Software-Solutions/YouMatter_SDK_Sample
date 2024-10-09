package com.techglock.health.app.records_tracker.common

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Html
import androidx.core.content.FileProvider
import com.techglock.health.app.R
import com.techglock.health.app.common.utils.FileUtils
import com.techglock.health.app.common.utils.LocaleHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.model.entity.HealthDocument
import com.techglock.health.app.model.entity.RecordInSession
import com.techglock.health.app.records_tracker.model.DocumentType
import com.techglock.health.app.records_tracker.viewmodel.HealthRecordsViewModel
import java.io.File
import java.util.Locale

class DataHandler(val context: Context) {

    private val fileUtils = FileUtils

    fun getDocumentTypeList(): MutableList<DocumentType> {
        val localResource = LocaleHelper.getLocalizedResources(
            context, Locale(LocaleHelper.getLanguage(context))
        )!!
        val list: MutableList<DocumentType> = mutableListOf()
        list.add(
            DocumentType(
                localResource.getString(R.string.TYPE_PATHOLOGY_LAB_REPORT),
                "LAB",
                R.drawable.img_pathology_report
            )
        )
        list.add(
            DocumentType(
                localResource.getString(R.string.TYPE_HOSPITAL_REPORT),
                "HOS",
                R.drawable.img_hos_report
            )
        )
        list.add(
            DocumentType(
                localResource.getString(R.string.TYPE_DOCTOR_PRESCRIPTION),
                "PRE",
                R.drawable.img_doctor_prescription
            )
        )
        list.add(
            DocumentType(
                localResource.getString(R.string.TYPE_DIET_PLAN),
                "DIET_PLAN",
                R.drawable.img_diet_plan
            )
        )
        list.add(
            DocumentType(
                localResource.getString(R.string.TYPE_FITNESS_PLAN),
                "FIT_PLAN",
                R.drawable.img_fitness_plan
            )
        )
        list.add(
            DocumentType(
                localResource.getString(R.string.TYPE_OTHER_DOCUMENT), "OTR", R.drawable.img_other
            )
        )
        return list
    }

    fun getDocumentTypeListAll(): MutableList<DocumentType> {
        val localResource = LocaleHelper.getLocalizedResources(
            context, Locale(LocaleHelper.getLanguage(context))
        )!!
        val list: MutableList<DocumentType> = mutableListOf()
        list.add(DocumentType(localResource.getString(R.string.ALL), "ALL", R.drawable.img_other))
        list.add(
            DocumentType(
                localResource.getString(R.string.TYPE_PATHOLOGY_LAB_REPORT),
                "LAB",
                R.drawable.img_pathology_report
            )
        )
        list.add(
            DocumentType(
                localResource.getString(R.string.TYPE_HOSPITAL_REPORT),
                "HOS",
                R.drawable.img_hos_report
            )
        )
        list.add(
            DocumentType(
                localResource.getString(R.string.TYPE_DOCTOR_PRESCRIPTION),
                "PRE",
                R.drawable.img_doctor_prescription
            )
        )
        list.add(
            DocumentType(
                localResource.getString(R.string.TYPE_DIET_PLAN),
                "DIET_PLAN",
                R.drawable.img_diet_plan
            )
        )
        list.add(
            DocumentType(
                localResource.getString(R.string.TYPE_FITNESS_PLAN),
                "FIT_PLAN",
                R.drawable.img_fitness_plan
            )
        )
        list.add(
            DocumentType(
                localResource.getString(R.string.HRA_REPORT), "HRAREPORT", R.drawable.img_hra_report
            )
        )
        list.add(
            DocumentType(
                localResource.getString(R.string.OTHER_DOCUMENT), "OTR", R.drawable.img_other
            )
        )
        return list
    }

    fun getCategoryByCode(code: String): String {
        val localResource = LocaleHelper.getLocalizedResources(
            context, Locale(LocaleHelper.getLanguage(context))
        )!!
        return when {
            code.equals("LAB", ignoreCase = true) -> {
                localResource.getString(R.string.PATHOLOGY_REPORT)
            }

            code.equals("HOS", ignoreCase = true) -> {
                localResource.getString(R.string.HOSPITAL_REPORT)
            }

            code.equals("PRE", ignoreCase = true) -> {
                localResource.getString(R.string.DOCTOR_PRESCRIPTION)
            }

            code.equals("DIET_PLAN", ignoreCase = true) -> {
                localResource.getString(R.string.DIET_PLAN)
            }

            code.equals("FIT_PLAN", ignoreCase = true) -> {
                localResource.getString(R.string.FITNESS_PLAN)
            }

            code.equals("HRAREPORT", ignoreCase = true) -> {
                localResource.getString(R.string.HRA_REPORT)
            }

            code.equals("OTR", ignoreCase = true) -> {
                localResource.getString(R.string.OTHER_DOCUMENT)
            }

            else -> {
                localResource.getString(R.string.OTHER_DOCUMENT)
            }
        }
    }

    /*    private fun openDownloadedFile(file : DocumentFile, type :String, context: Context) {
            try {
                val uri = file.uri
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(uri,type)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                //val openIntent = Intent.createChooser(intent,"Open using")
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
                Utilities.toastMessageShort(context,context.resources.getString(R.string.ERROR_NO_APPLICATION_TO_VIEW_PDF))
            } catch (e: Exception) {
                e.printStackTrace()
                Utilities.toastMessageShort(context,context.resources.getString(R.string.ERROR_UNABLE_TO_OPEN_FILE))
            }
        }*/

    private fun openDownloadedFile(file: File, type: String) {
        val localResource = LocaleHelper.getLocalizedResources(
            context, Locale(LocaleHelper.getLanguage(context))
        )!!
        try {
            val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, type)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            //val openIntent = Intent.createChooser(intent,"Open using")
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            Utilities.toastMessageShort(
                context, localResource.getString(R.string.ERROR_NO_APPLICATION_TO_VIEW_PDF)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Utilities.toastMessageShort(
                context, localResource.getString(R.string.ERROR_UNABLE_TO_OPEN_FILE)
            )
        }
    }

    fun viewDocument(recordData: HealthDocument) {
        val recordName = recordData.Name!!
        val recordPath = recordData.Path
        val recordType = recordData.Type
        val type: String
        when {
            recordType.equals("IMAGE", ignoreCase = true) -> {
                type = "image/*"
            }

            recordType.equals("DOC", ignoreCase = true) -> {
                type = "application/msword"
            }

            recordType.equals("PDF", ignoreCase = true) -> {
                type = "application/pdf"
            }

            fileUtils.getFileExt(recordName).equals("txt", ignoreCase = true) -> {
                type = "text/*"
            }

            else -> {
                type = "application/pdf"
            }
        }
        if (!type.equals("", ignoreCase = true)) {
            if (recordType.equals("IMAGE", ignoreCase = true)) {
                val completeFilePath = "$recordPath/$recordName"
                ////val bitmap = RealPathUtil.decodeFile(completeFilePath)
                //val bitmap = BitmapFactory.decodeFile(completeFilePath)
                //Utilities.showFullImageWithBitmap(bitmap,context,true)
                Utilities.viewImage(context, completeFilePath)
            } else {
                val file = File(recordPath, recordName)
                //val file = DocumentFile.fromTreeUri(context, recordData.FileUri.toUri())!!
                if (file.exists()) {
                    DataHandler(context).openDownloadedFile(file, type)
                }
            }
        }
    }

    fun viewRecord(recordData: RecordInSession) {
        val localResource = LocaleHelper.getLocalizedResources(
            context, Locale(LocaleHelper.getLanguage(context))
        )!!
        val recordName = recordData.Name
        val recordPath = recordData.Path
        val recordType = recordData.Type
        val type: String
        when {
            recordType.equals("IMAGE", ignoreCase = true) -> {
                type = "image/*"
            }

            recordType.equals("DOC", ignoreCase = true) -> {
                type = "application/msword"
            }

            recordType.equals("PDF", ignoreCase = true) -> {
                type = "application/pdf"
            }

            fileUtils.getFileExt(recordName).equals("txt", ignoreCase = true) -> {
                type = "text/*"
            }

            else -> {
                type = "application/pdf"
            }
        }
        if (!type.equals("", ignoreCase = true)) {
            val file = File(recordPath, recordName)
            //val file = DocumentFile.fromTreeUri(context, recordData.FileUri.toUri())!!
            if (file.exists()) {
                DataHandler(context).openDownloadedFile(file, type)
            }
        }
    }

    fun shareDataWithAppSingle(recordData: HealthDocument, viewModel: HealthRecordsViewModel) {
        val localResource = LocaleHelper.getLocalizedResources(
            context, Locale(LocaleHelper.getLanguage(context))
        )!!
        val completePath = recordData.Path + "/" + recordData.Name
        Utilities.printLogError("completePath--->$completePath")

        if (!Utilities.isNullOrEmpty(completePath)) {
            val file = File(completePath)
            generateShareIntent(context, file, viewModel)
        } else {
            Utilities.toastMessageShort(
                context, localResource.getString(R.string.DOWNLOAD_DOCUMENT_TO_PROCEED)
            )
        }

        /*        val file = DocumentFile.fromTreeUri(context, recordData.FileUri.toUri())!!
                if (file.exists()) {
                    val fileToShare = file.uri
                    generateShareIntent(context, fileToShare,viewModel)
                } else {
                    Utilities.toastMessageShort(context,context.resources.getString(R.string.DOWNLOAD_DOCUMENT_TO_PROCEED))
                }*/

    }

    private fun generateShareIntent(
        context: Context, file: File, viewModel: HealthRecordsViewModel
    ) {
        try {
            val fileToShare = Uri.fromFile(file)
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileToShare)
            shareIntent.putExtra(
                Intent.EXTRA_SUBJECT, viewModel.firstName + " has shared Health Document"
            )
            val himORher = if (viewModel.gender.equals("1", ignoreCase = true)) " him " else " her "
            val hisORher = if (viewModel.gender.equals("1", ignoreCase = true)) " his " else " her "
            val shareBody =
                ("Hello," + "\n\n" + viewModel.firstName + " has shared  health records with you." + "\n" + Html.fromHtml(
                    "<br>" + "Kindly review the records and guide" + himORher + "through" + hisORher + "health related queries." + "</br>" + "<br><br>" + "We hope this electronic health record sharing feature has saved your precious time and provided you with the necessary information. "
//                            +
//                            "If you've liked this feature, please share the Vivant portal " +
//                            "<a href=\"https://portal.vivant.me\">portal.vivant.vivant.me</a> " +
//                            "to connect with your other patients too!" + "</br></br>") +
//                    Html.fromHtml(("<br><br>" + "Team Vivant</br><br>" +
//                            "<a href=\"https://vivant.me\">www.vivant.vivant.me</a></br></br>"
                ))

            shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
            shareIntent.type = "*/*"
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            //shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(Intent.createChooser(shareIntent, " Share with .."))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
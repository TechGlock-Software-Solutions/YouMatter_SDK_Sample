package com.techglock.health.app.home.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.techglock.health.app.common.base.BaseViewModel
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.home.common.DataHandler
import com.techglock.health.app.home.domain.HomeManagementUseCase
import com.techglock.health.app.model.entity.Users
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class WebViewViewModel @Inject constructor(
    application: Application,
    private val homeManagementUseCase: HomeManagementUseCase,
    private val dataHandler: DataHandler,
    val context: Context?
) : BaseViewModel(application) {

    var userDetails = MutableLiveData<Users>()

    fun copyAndOpenAssetFile(activity: Context, assetsFolder: String, destinationFolder: String) {
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            for (fileName in activity.assets.list(assetsFolder)!!) {
                if (fileName == "ApolloStoreListwithPincodes.xlsx") {
                    Utilities.printLog("REAL_PATH_UTIL---->$assetsFolder$fileName")
                    Utilities.printLog(
                        "REAL_PATH_UTIL : Trying to open---->"
                                + assetsFolder + (if (assetsFolder.endsWith(File.pathSeparator)) "" else File.pathSeparator) + fileName
                    )
                    try {
                        val fileReader = ByteArray(4096)
                        var fileSizeDownloaded: Long = 0
                        inputStream = activity.assets.open("$assetsFolder/$fileName")
                        val file = File(destinationFolder, fileName)
                        outputStream = FileOutputStream(File(destinationFolder, fileName))

                        while (true) {
                            val read = inputStream.read(fileReader)
                            if (read == -1) {
                                break
                            }
                            outputStream.write(fileReader, 0, read)
                            fileSizeDownloaded += read.toLong()
                        }
                        outputStream.flush()
                        openDownloadedFile(file)
                    } catch (e: Exception) {
                        Utilities.printLog("Error..." + e.printStackTrace())
                    } finally {
                        inputStream?.close()
                        outputStream?.close()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openDownloadedFile(file: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.ms-excel")
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        try {
            context!!.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            //AlertDialogHelper( this , "No Application Available to View Excel." ,"You need Excel viewer to open this file.")
        }
    }

}
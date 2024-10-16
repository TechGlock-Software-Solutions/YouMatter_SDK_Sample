package com.techglock.health.app.common.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.techglock.health.app.R
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener


object PermissionUtil : ActivityCompat() {

    interface AppPermissionListener {
        fun isPermissionGranted(isGranted: Boolean)
    }

    fun checkAmahaWebviewPermissions(listener: AppPermissionListener, context: Context): Boolean {
        var isPermissionGranted = false
        if (checkSelfStoragePermission(context)
            || checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
            || checkSelfPermission(
                context,
                Manifest.permission.MODIFY_AUDIO_SETTINGS
            ) != PackageManager.PERMISSION_GRANTED
            || checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val dialogData = DefaultNotificationDialog.DialogData()
            dialogData.title = context.resources.getString(R.string.PERMISSION_REQUIRED)
            dialogData.message = context.resources.getString(R.string.NEED_WEBVIEW_PERMISSION)
            dialogData.btnLeftName = context.resources.getString(R.string.CANCEL)
            dialogData.btnRightName = context.resources.getString(R.string.OK)
            val defaultNotificationDialog = DefaultNotificationDialog(
                context,
                object : DefaultNotificationDialog.OnDialogValueListener {
                    override fun onDialogClickListener(
                        isButtonLeft: Boolean,
                        isButtonRight: Boolean
                    ) {
                        if (isButtonRight) {
                            amahaWebViewPermissionsCheck(listener, context)
                        } else {
                            listener.isPermissionGranted(false)
                        }
                    }
                }, dialogData
            )
            defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            defaultNotificationDialog.show()
        } else {
            isPermissionGranted = true
        }

        return isPermissionGranted
    }

    fun amahaWebViewPermissionsCheck(listener: AppPermissionListener, context: Context) {
        val permissions: Collection<String> =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayListOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.CAMERA,
                    Manifest.permission.MODIFY_AUDIO_SETTINGS,
                    Manifest.permission.RECORD_AUDIO
                )
            } else {
                arrayListOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.MODIFY_AUDIO_SETTINGS,
                    Manifest.permission.RECORD_AUDIO
                )
            }
        Dexter.withContext(context).withPermissions(permissions)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if (report.areAllPermissionsGranted()) {
                            listener.isPermissionGranted(true)
                            //fileUtils.makeFolderDirectories(context)
                            Utilities.getAppFolderLocation(context)
                        }
                        if (report.isAnyPermissionPermanentlyDenied) {
                            Utilities.toastMessageShort(
                                context,
                                context.resources.getString(R.string.NEED_WEBVIEW_PERMISSION)
                            )
                            listener.isPermissionGranted(false)
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            })
            .withErrorListener { }
            .check()
    }

    fun checkStoragePermission(listener: AppPermissionListener, context: Context): Boolean {
        var isPermissionGranted = false
        if (checkSelfStoragePermission(context)) {
            val dialogData = DefaultNotificationDialog.DialogData()
            dialogData.title = context.resources.getString(R.string.PERMISSION_REQUIRED)
            dialogData.message = context.resources.getString(R.string.NEED_STORAGE_PERMISSION)
            dialogData.btnLeftName = context.resources.getString(R.string.CANCEL)
            dialogData.btnRightName = context.resources.getString(R.string.OK)
            val defaultNotificationDialog = DefaultNotificationDialog(
                context,
                object : DefaultNotificationDialog.OnDialogValueListener {
                    override fun onDialogClickListener(
                        isButtonLeft: Boolean,
                        isButtonRight: Boolean
                    ) {
                        if (isButtonRight) {
                            storageDexterPermissionCheck(listener, context)
                        } else {
                            listener.isPermissionGranted(false)
                        }
                    }
                }, dialogData
            )
            defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            defaultNotificationDialog.show()
        } else {
            isPermissionGranted = true
        }

        return isPermissionGranted
    }

    private fun checkSelfStoragePermission(context: Context): Boolean {
        var isPermissionGranted = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(
                    context,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                isPermissionGranted = true
            }
        } else {
            if (checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                isPermissionGranted = true
            }
        }
        return isPermissionGranted
    }

    fun storageDexterPermissionCheck(listener: AppPermissionListener, context: Context) {
        val permissions: Collection<String> =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayListOf(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                arrayListOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }

        Dexter.withContext(context).withPermissions(permissions)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if (report.areAllPermissionsGranted()) {
                            listener.isPermissionGranted(true)
                            //fileUtils.makeFolderDirectories(context)
                            Utilities.getAppFolderLocation(context)
                        }
                        if (report.isAnyPermissionPermanentlyDenied) {
                            Utilities.toastMessageShort(
                                context,
                                context.resources.getString(R.string.ERROR_STORAGE_PERMISSION)
                            )
                            listener.isPermissionGranted(false)
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            })
            .withErrorListener { }
            .check()
    }

    fun checkCameraPermission(listener: AppPermissionListener, context: Context): Boolean {
        var isPermissionGranted = false
        if (checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            val dialogData = DefaultNotificationDialog.DialogData()
            dialogData.title = context.resources.getString(R.string.PERMISSION_REQUIRED)
            dialogData.message = context.resources.getString(R.string.NEED_CAMERA_PERMISSION)
            dialogData.btnLeftName = context.resources.getString(R.string.CANCEL)
            dialogData.btnRightName = context.resources.getString(R.string.OK)
            val defaultNotificationDialog = DefaultNotificationDialog(
                context,
                object : DefaultNotificationDialog.OnDialogValueListener {
                    override fun onDialogClickListener(
                        isButtonLeft: Boolean,
                        isButtonRight: Boolean
                    ) {
                        if (isButtonRight) {
                            cameraDexterPermissionCheck(listener, context)
                        }
                    }
                }, dialogData
            )
            defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            defaultNotificationDialog.show()
        } else {
            isPermissionGranted = true
        }
        return isPermissionGranted
    }

    fun checkCameraPermissionWellfie(listener: AppPermissionListener, context: Context): Boolean {
        var isPermissionGranted = false
        if (checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            val dialogData = DefaultNotificationDialog.DialogData()
            dialogData.title = context.resources.getString(R.string.PERMISSION_REQUIRED)
            dialogData.message =
                context.resources.getString(R.string.NEED_CAMERA_PERMISSION_WELLFIE)
            dialogData.btnLeftName = context.resources.getString(R.string.CANCEL)
            dialogData.btnRightName = context.resources.getString(R.string.OK)
            val defaultNotificationDialog = DefaultNotificationDialog(
                context,
                object : DefaultNotificationDialog.OnDialogValueListener {
                    override fun onDialogClickListener(
                        isButtonLeft: Boolean,
                        isButtonRight: Boolean
                    ) {
                        if (isButtonRight) {
                            cameraDexterPermissionCheck(listener, context)
                        }
                    }
                }, dialogData
            )
            defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            defaultNotificationDialog.show()
        } else {
            isPermissionGranted = true
        }
        return isPermissionGranted
    }

    fun cameraDexterPermissionCheck(listener: AppPermissionListener, context: Context) {
        Dexter.withContext(context)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {

                override fun onPermissionGranted(permissionResp: PermissionGrantedResponse?) {
                    listener.isPermissionGranted(true)
                }

                override fun onPermissionDenied(permissionResp: PermissionDeniedResponse?) {
                    Utilities.toastMessageShort(
                        context,
                        context.resources.getString(R.string.ERROR_CAMERA_PERMISSION)
                    )
                    listener.isPermissionGranted(false)
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

            })
            .withErrorListener { }
            .check()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun checkNotificationPermission(listener: AppPermissionListener, context: Context): Boolean {
        var isPermissionGranted = false
        if (checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val dialogData = DefaultNotificationDialog.DialogData()
            dialogData.title = context.resources.getString(R.string.PERMISSION_REQUIRED)
            dialogData.message = context.resources.getString(R.string.NEED_NOTIFICATION_PERMISSION)
            dialogData.btnLeftName = context.resources.getString(R.string.CANCEL)
            dialogData.btnRightName = context.resources.getString(R.string.OK)
            val defaultNotificationDialog = DefaultNotificationDialog(
                context,
                object : DefaultNotificationDialog.OnDialogValueListener {
                    override fun onDialogClickListener(
                        isButtonLeft: Boolean,
                        isButtonRight: Boolean
                    ) {
                        if (isButtonRight) {
                            notificationDexterPermissionCheck(listener, context)
                        } else {
                            listener.isPermissionGranted(false)
                        }
                    }
                }, dialogData
            )
            defaultNotificationDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            defaultNotificationDialog.show()
        } else {
            isPermissionGranted = true
        }

        return isPermissionGranted
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun notificationDexterPermissionCheck(listener: AppPermissionListener, context: Context) {
        Dexter.withContext(context)
            .withPermission(Manifest.permission.POST_NOTIFICATIONS)
            .withListener(object : PermissionListener {

                override fun onPermissionGranted(permissionResp: PermissionGrantedResponse?) {
                    listener.isPermissionGranted(true)
                }

                override fun onPermissionDenied(permissionResp: PermissionDeniedResponse?) {
                    Utilities.toastMessageShort(
                        context,
                        context.resources.getString(R.string.ERROR_NOTIFICATION_PERMISSION)
                    )
                    listener.isPermissionGranted(false)
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

            })
            .withErrorListener { }
            .check()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun isActivityRecognitionPermission(context: Context): Boolean {
        return checkSelfPermission(
            context,
            Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED
    }

}
package com.techglock.health.app.home.ui.wellfie

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.techglock.health.app.common.constants.Constants


class PermissionManager {

    fun checkPermission(activity: Activity, callback: () -> Unit) {
        val permissions = arrayOf(Manifest.permission.CAMERA)

        if (hasPermissionsGranted(activity, permissions)) {
            callback.invoke()
        } else {
            activity.requestPermissions(permissions, Constants.CAMERA_SELECT_CODE)
        }
    }

    fun hasPermissionsGranted(activity: Activity, permissions: Array<String>) =
        permissions.none {
            ContextCompat.checkSelfPermission(activity.baseContext, it) !=
                    PackageManager.PERMISSION_GRANTED
        }
}
package com.techglock.health.app.receiver

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.NavigationConstants
import com.techglock.health.app.common.utils.Utilities

class AppNavigationBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        try {
            //val screen = intent!!.action
            val screen = intent!!.getStringExtra(Constants.SCREEN)
            val notificationID = intent.getIntExtra(Constants.NOTIFICATION_ID, -1)
            Utilities.printLogError("screen=>$screen---notificationID--->$notificationID")
            if (screen != null) {
                if (screen == "APP_UPDATE") {
                    Utilities.goToPlayStore(context)
                } else {
                    launchApp(context, screen, intent)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun launchApp(context: Context, screen: String, intent: Intent) {
       /* FirebaseHelper.logCustomFirebaseEventWithData(
            FirebaseConstants.NOTIFICATION_CLICK,
            intent.getStringExtra(Constants.NOTIFICATION_ACTION) + " :: " + intent.getStringExtra(
                Constants.NOTIFICATION_MESSAGE
            )
        )*/
        val onClick = Intent()
        onClick.putExtra(Constants.SCREEN, screen)
        onClick.putExtra(
            Constants.NOTIFICATION_ACTION,
            intent.getStringExtra(Constants.NOTIFICATION_ACTION)
        )
        onClick.putExtra(
            Constants.NOTIFICATION_TITLE,
            intent.getStringExtra(Constants.NOTIFICATION_TITLE)
        )
        onClick.putExtra(
            Constants.NOTIFICATION_MESSAGE,
            intent.getStringExtra(Constants.NOTIFICATION_MESSAGE)
        )
        onClick.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        onClick.component =
            ComponentName(NavigationConstants.APPID, NavigationConstants.SPLASH_SCREEN)
        context.startActivity(onClick)
    }

}
package com.techglock.health.app.medication_tracker.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.techglock.health.app.R
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.NavigationConstants
import com.techglock.health.app.common.utils.LocaleHelper
import com.techglock.health.app.common.utils.NetworkUtility
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.medication_tracker.common.MedicationSingleton
import com.techglock.health.app.medication_tracker.viewmodel.MedicineTrackerViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class MedicineReminderBroadcastReceiver : BroadcastReceiver(), LifecycleOwner, CoroutineScope {

    private var lifeCycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

    @Inject
    lateinit var viewModel: MedicineTrackerViewModel


    override val lifecycle: Lifecycle get() = lifeCycleRegistry
    override fun onReceive(context: Context, intent: Intent?) {
        try {
            val action = intent!!.action
            val notificationID = intent.getIntExtra(Constants.NOTIFICATION_ID, -1)
            Utilities.printLog("Action=>$action---notificationID--->$notificationID")
            if (action != null) {
                if (action.equals(Constants.MEDICATION, ignoreCase = true)) {
                    if (NetworkUtility.isOnline(context)) {
                        /*FirebaseHelper.logCustomFirebaseEventWithData(
                            FirebaseConstants.NOTIFICATION_CLICK,
                            intent.getStringExtra(Constants.MEDICATION_ID)!! + " :: " + intent.getStringExtra(
                                Constants.SCHEDULE_TIME
                            )!! + " :: " + intent.getStringExtra(Constants.PERSON_ID)!!
                        )*/
                        launchApp(context, notificationID, intent)
                    } else {
                        /*FirebaseHelper.logCustomFirebaseEventWithData(
                            FirebaseConstants.NOTIFICATION_CLICK,
                            "MediNotiNoInternet"
                        )*/
                        closeNotificationDrawer(context)
                        Utilities.toastMessageLong(
                            context,
                            context.resources.getString(R.string.NO_INTERNET_CONNECTION)
                        )
                    }
                } else if (action.equals(Constants.TAKEN, ignoreCase = true)) {
                   /* FirebaseHelper.logCustomFirebaseEventWithData(
                        FirebaseConstants.NOTIFICATION_CLICK,
                        Constants.TAKEN + ":: " + intent.getStringExtra(Constants.MEDICATION_ID)!!
                    )*/
                    getMedicineDetails(context, intent)
                } else if (action.equals(Constants.SKIPPED, ignoreCase = true)) {
                   /* FirebaseHelper.logCustomFirebaseEventWithData(
                        FirebaseConstants.NOTIFICATION_CLICK,
                        Constants.SKIPPED + " :: " + intent.getStringExtra(Constants.MEDICATION_ID)!!
                    )*/
                    getMedicineDetails(context, intent)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getMedicineDetails(context: Context, intent: Intent) {
        if (NetworkUtility.isOnline(context)) {
            showProgressNotification(context, intent)
            MedicationSingleton.getInstance()!!.setNotificationIntent(intent)
            val medicationID = intent.getStringExtra(Constants.MEDICATION_ID)!!
            viewModel.getMedDetailsByMedicationIdApi(medicationID)/*            viewModel.getMedicineDetailsByMedicationIdApi(medicationID,Constants.NOTIFICATION)
            viewModel.getMedicine.observe(this@MedicineReminderBroadcastReceiver, Observer {})
            viewModel.listMedicationInTake.observe(this@MedicineReminderBroadcastReceiver, Observer {})
            viewModel.addMedicineIntake.observe(this@MedicineReminderBroadcastReceiver, Observer {})*/
        } else {
            closeNotificationDrawer(context)
            Utilities.toastMessageLong(
                context,
                context.resources.getString(R.string.NO_INTERNET_CONNECTION)
            )
        }
    }

    private fun showProgressNotification(context: Context, intent: Intent) {
        val localResource = LocaleHelper.getLocalizedResources(
            context,
            Locale(LocaleHelper.getLanguage(context))
        )!!
        val notificationId = intent.getIntExtra(Constants.NOTIFICATION_ID, -1)
        val remoteView = RemoteViews(context.packageName, R.layout.med_notification_progress)
        remoteView.setTextViewText(
            R.id.med_notification_title,
            intent.getStringExtra(Constants.MEDICINE_NAME)
        )
        remoteView.setTextViewText(
            R.id.med_notification_subtext,
            intent.getStringExtra(Constants.SUB_TITLE)
        )
        remoteView.setTextViewText(R.id.med_schedule_time, intent.getStringExtra(Constants.TIME))
        remoteView.setTextViewText(
            R.id.lbl_med_reminder,
            localResource.getString(R.string.MEDICINE_REMINDER)
        )

        val customNotification = NotificationCompat.Builder(context, "fcm_medication_channel")
            .setSmallIcon(R.drawable.notification_logo)
            //.setColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setStyle(NotificationCompat.DecoratedCustomViewStyle()).setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setTicker(context.resources.getString(R.string.app_name))
            //.setProgress(100,0,true)
            .setCustomContentView(remoteView).build()
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, customNotification)
    }

    private fun launchApp(context: Context, notificationId: Int, intent: Intent) {
        try {
            val currentPersonId = viewModel.personId
            val personID: String = intent.getStringExtra(Constants.PERSON_ID)!!
            val medicationID: String = intent.getStringExtra(Constants.MEDICATION_ID)!!
            //val scheduleID: String = intent.getStringExtra(Constants.SERVER_SCHEDULE_ID)!!
            val scheduleTime: String =
                intent.getStringExtra(Constants.SCHEDULE_TIME)!!.substring(0, 5)
            //val medName: String = intent.getStringExtra(Constants.MEDICINE_NAME)!!
            Utilities.printLog("currentPersonId=>$currentPersonId")
            Utilities.printLog("PersonID=>$personID")
            Utilities.printLog("MedicationID=>$medicationID")
            Utilities.printLog("ScheduleTime=>$scheduleTime")

            if (currentPersonId.equals(personID, ignoreCase = true)) {
                MedicationSingleton.getInstance()!!.setNotificationIntent(intent)
                val onClick = Intent()
                onClick.putExtra(Constants.FROM, Constants.NOTIFICATION_ACTION)
                onClick.putExtra(Constants.DATE, intent.getStringExtra(Constants.DATE))
                onClick.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                onClick.component =
                    ComponentName(NavigationConstants.APPID, NavigationConstants.SPLASH_SCREEN)
                context.startActivity(onClick)
            } else {
                Utilities.printLog("Switch")
                MedicationSingleton.getInstance()!!.setNotificationIntent(intent)
                viewModel.checkRelativeExistAndLaunchApp(intent)
            }
            closeNotificationDrawer(context)
            cancelNotification(context, notificationId)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*    private fun launchApp(context: Context, notificationId: Int, intent: Intent) {
            try {
                val currentPersonId = viewModel.personId
                val personID: String = intent.getStringExtra(Constants.PERSON_ID)!!
                val medicationID: String = intent.getStringExtra(Constants.MEDICATION_ID)!!
                //val scheduleID: String = intent.getStringExtra(Constants.SERVER_SCHEDULE_ID)!!
                val scheduleTime: String =
                    intent.getStringExtra(Constants.SCHEDULE_TIME)!!.substring(0, 5)
                //val medName: String = intent.getStringExtra(Constants.MEDICINE_NAME)!!
                Utilities.printLog("currentPersonId=>$currentPersonId")
                Utilities.printLog("PersonID=>$personID")
                Utilities.printLog("MedicationID=>$medicationID")
                Utilities.printLog("ScheduleTime=>$scheduleTime")

                if (currentPersonId.equals(personID, ignoreCase = true)) {
                    MedicationSingleton.getInstance()!!.setNotificationIntent(intent)
                    val launchIntent = Intent()
                    launchIntent.component = ComponentName(NavigationConstants.APPID, NavigationConstants.MEDICINE_TRACKER)
                    launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    launchIntent.putExtra(Constants.FROM, Constants.NOTIFICATION_ACTION)
                    launchIntent.putExtra(Constants.DATE, intent.getStringExtra(Constants.DATE))
                    context.startActivity(launchIntent)
                } else {
                    Utilities.printLog("Switch")
                    MedicationSingleton.getInstance()!!.setNotificationIntent(intent)
                    viewModel.checkRelativeExistAndLaunchApp(intent)
                }
                closeNotificationDrawer(context)
                cancelNotification(context, notificationId)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }*/

    private fun cancelNotification(context: Context, notificationId: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(notificationId)
    }

    private fun closeNotificationDrawer(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            context.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
        }
    }

    override val coroutineContext: CoroutineContext = Dispatchers.Main + SupervisorJob()


}
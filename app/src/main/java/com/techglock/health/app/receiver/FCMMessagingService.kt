package com.techglock.health.app.receiver

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import com.techglock.health.app.R
import com.techglock.health.app.common.constants.Constants
import com.techglock.health.app.common.constants.NavigationConstants
import com.techglock.health.app.common.utils.DateHelper
import com.techglock.health.app.common.utils.Utilities
import com.techglock.health.app.home.viewmodel.BackgroundCallViewModel
import com.techglock.health.app.medication_tracker.model.ReminderNotification
import com.techglock.health.app.medication_tracker.viewmodel.MedicineTrackerViewModel
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class FCMMessagingService : FirebaseMessagingService() {

    private val tag = "FCMMessagingService : "
    private val medNotification = ReminderNotification()

    //private var lifeCycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
    @Inject
    lateinit var viewModel: MedicineTrackerViewModel

    @Inject
    lateinit var backgroundApiCallViewModel: BackgroundCallViewModel

    //override fun getLifecycle(): Lifecycle = lifeCycleRegistry

    override fun onNewToken(token: String) {
        Utilities.printLogError("$tag Refreshed token--->$token")
        sendRegistrationToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        try {
            //val notification = remoteMessage.notification
            val data = remoteMessage.data
            /*FirebaseHelper.logCustomFirebaseEventWithData(
                FirebaseConstants.NOTIFICATION_RECEIVE,
                data.toString()
            )*/
            Utilities.printData("onMessageReceived(FCMMessagingService): data", data, true)
            if (viewModel.isUserLoggedIn()) {

//                if (backgroundApiCallViewModel.isPushNotificationEnabled()) {
                //val action = data["Action"]!!
                //val screen = data["Screen"]!!
                if (data.containsKey("Action") && !Utilities.isNullOrEmpty(data["Action"]!!) && data["Action"]!!.equals("HEALTHTIPS", ignoreCase = true)) {
                    showHealthTipNotification(this, data)
                } else if (data.containsKey("Action") && !Utilities.isNullOrEmpty(data["Action"]!!) && data["Action"]!!.equals("APP_UPDATE", ignoreCase = true)) {
                    showAppUpdateNotification(this, data)
                } else if (data.containsKey("Screen") && !Utilities.isNullOrEmpty(data["Screen"]!!)) {
                    when (data["Screen"]!!) {
                        "MEDICATION_REMINDER" -> {
                            showMedicineReminderNotification(data)
                        }

                        "WATER_REMINDER", "WATER_REMINDER_21_POSITIVE", "WATER_REMINDER_21_NEGATIVE" -> {
                            if (data["personid"] == backgroundApiCallViewModel.getMainUserPersonID()) {
                                showWaterReminderNotificationCustom(data)
                                //showWaterReminderNotification(this,data)
                            }
                        }

                        "DASHBOARD" -> {
                            displayAmahaNotification(this, data)
                        }

                        "STEPS", "STEPS_WEEKLY_SYNOPSIS", "STEPS_DAILY_TARGET" -> {
                            Utilities.printLogError("Fitness Tracker Notifications Disabled")
                        }

                        "SPIRITUAL_WELLNESS" -> {
                            showSpiritualHealthNotification(this, data)
                        }

                        "WEB_URL_NOTIFICATION" -> {
                            showGeneralWebUrlNotification(this, data)
                        }
                        Constants.SCREEN_FEATURE_CAMPAIGN -> {
                            showFeatureCampaignNotification(this,data)
                        }
                        else -> {
                            displayAppNavigationNotification(this, data)
                        }
                    }
                } else if (data.containsKey("notificationType") && !Utilities.isNullOrEmpty(data["notificationType"]!!)) {
                    showAktivoNotification(this, data)
                }
//                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    private fun showFeatureCampaignNotification(context: Context, data: Map<String, String>) {
        try {
            val screen = data["Screen"]!!
            val title = data["nt"]!!
            val message = data["nm"]!!
            val deepLink = data["wzrk_dl"]!!
            var imageUrl = ""
            if ( data.containsKey("wzrk_bp") ) {
                imageUrl = data["wzrk_bp"]!!
            }
            Utilities.printLogError("Screen--->$screen")
            Utilities.printLogError("deepLink--->$deepLink")

            //In Android "O" or higher version, it's Mandatory to use a channel with your Notification Builder
            //int NOTIFICATION_ID = (int) System.currentTimeMillis();
            val notificationId = (Date().time / 1000L % Int.MAX_VALUE).toInt()
            val channelId = "fcm_web_url_channel"

            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            //val sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.vivant_notification_sound_new)

            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = context.resources.getString(R.string.app_name) + " Web Url"
                val descriptionText = "Web Url Notification"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(channelId, name, importance).apply {
                    description = descriptionText
                }
                notificationManager.createNotificationChannel(channel)
            }

            // Onclick of Notification Intent
            //val onClick = createOnClickFeatureCampaignNotification(screen,deepLink,title,message,notificationId)
            val onClick = Intent()
            onClick.putExtra(Constants.SCREEN, Constants.SCREEN_FEATURE_CAMPAIGN)
            onClick.putExtra(Constants.DEEP_LINK, deepLink)
            onClick.putExtra(Constants.NOTIFICATION_TITLE, title)
            onClick.putExtra(Constants.NOTIFICATION_MESSAGE, message)
            onClick.putExtra(Constants.NOTIFICATION_ID, notificationId)
            onClick.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            onClick.component = ComponentName(NavigationConstants.APPID,NavigationConstants.SPLASH_SCREEN)

            val pendingOnClickIntent: PendingIntent? = TaskStackBuilder.create(this).run {
                // Add the intent, which inflates the back stack
                addNextIntentWithParentStack(onClick)
                // Get the PendingIntent containing the entire back stack
                getPendingIntent(notificationId,PendingIntent.FLAG_UPDATE_CURRENT or
                        // mutability flag required when targeting Android12 or higher
                        PendingIntent.FLAG_IMMUTABLE)
            }

            val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.notification_logo)
                //.setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                //.setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round))
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker(context.resources.getString(R.string.app_name))
                .setContentIntent(pendingOnClickIntent)

            if ( !Utilities.isNullOrEmpty(imageUrl) ) {
                applyImageUrl(builder,imageUrl)
            }

            Utilities.printLogError("displaying Notification")
            with(NotificationManagerCompat.from(this)) {
                // notificationId is a unique int for each notification that you must define
                notify(notificationId,builder.build())
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    private fun displayAppNavigationNotification(context: Context, data: Map<String, String>) {
        try {
            val action = data["Action"]
            val screen = data["Screen"]
            val title = data["title"]
            val message = data["body"]
            //val message = Html.fromHtml(data["body"])
            Utilities.printLogError("Screen--->$screen")

            //In Android "O" or higher version, it's Mandatory to use a channel with your Notification Builder
            //int NOTIFICATION_ID = (int) System.currentTimeMillis();
            val notificationId = (Date().time / 1000L % Int.MAX_VALUE).toInt()
            val channelId = "fcm_medication_channel"

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            //val sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.vivant_notification_sound_new)

            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = context.resources.getString(R.string.app_name) + " Notification"
                val descriptionText = "Navigation Notification"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(channelId, name, importance).apply {
                    description = descriptionText
                }
                /*                val attributes = AudioAttributes.Builder()
                                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                                    .build()
                                channel.setSound(sound, attributes)*/
                // Register the channel with the system
                notificationManager.createNotificationChannel(channel)
            }

            // Onclick of Notification Intent
            val onClick =
                createOnClickIntent(screen!!, action!!, title!!, message!!, notificationId)
            val pendingOnClickIntent: PendingIntent? = TaskStackBuilder.create(this).run {
                // Add the intent, which inflates the back stack
                addNextIntentWithParentStack(onClick)
                // Get the PendingIntent containing the entire back stack
                getPendingIntent(
                    notificationId, PendingIntent.FLAG_UPDATE_CURRENT or
                            // mutability flag required when targeting Android12 or higher
                            PendingIntent.FLAG_IMMUTABLE
                )
            }

            val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.notification_logo)
                //.setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                //.setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round))
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                //.setSound(alarmSound)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker(context.resources.getString(R.string.app_name))
                .setContentIntent(pendingOnClickIntent)
                .build()

            Utilities.printLogError("displaying Notification")
            with(NotificationManagerCompat.from(this)) {
                // notificationId is a unique int for each notification that you must define
                notify(notificationId, builder)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    private fun displayAmahaNotification(context: Context, data: Map<String, String>) {
        try {
            val action = data["Action"]
            val screen = data["Screen"]
            val title = data["title"]
            val message = data["body"]
            val redirectLink = data["redirect_link"]
            //val message = Html.fromHtml(data["body"])
            Utilities.printLogError("Screen--->$screen")

            //In Android "O" or higher version, it's Mandatory to use a channel with your Notification Builder
            //int NOTIFICATION_ID = (int) System.currentTimeMillis();
            val notificationId = (Date().time / 1000L % Int.MAX_VALUE).toInt()
            val channelId = "fcm_medication_channel"

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            //val sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.vivant_notification_sound_new)

            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = context.resources.getString(R.string.app_name) + " Notification"
                val descriptionText = "Amaha Notification"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(channelId, name, importance).apply {
                    description = descriptionText
                }
                /*                val attributes = AudioAttributes.Builder()
                                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                                    .build()
                                channel.setSound(sound, attributes)*/
                // Register the channel with the system
                notificationManager.createNotificationChannel(channel)
            }

            // Onclick of Notification Intent
            val onClick =
                createOnClickIntent(screen!!, action!!, title!!, message!!, notificationId)
            onClick.putExtra(Constants.REDIRECT_LINK, redirectLink)
            val pendingOnClickIntent: PendingIntent? = TaskStackBuilder.create(this).run {
                // Add the intent, which inflates the back stack
                addNextIntentWithParentStack(onClick)
                // Get the PendingIntent containing the entire back stack
                getPendingIntent(
                    notificationId, PendingIntent.FLAG_UPDATE_CURRENT or
                            // mutability flag required when targeting Android12 or higher
                            PendingIntent.FLAG_IMMUTABLE
                )
            }

            val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.notification_logo)
                //.setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                //.setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round))
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                //.setSound(alarmSound)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker(context.resources.getString(R.string.app_name))
                .setContentIntent(pendingOnClickIntent)
                .build()

            Utilities.printLogError("displaying Notification")
            with(NotificationManagerCompat.from(this)) {
                // notificationId is a unique int for each notification that you must define
                notify(notificationId, builder)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showMedicineReminderNotification(data: Map<String, String>) {
        try {
            val screen = data["Screen"]
            if (!Utilities.isNullOrEmpty(screen)) {
                Utilities.printLogError("Screen--->$screen")
                if (screen.equals("MEDICATION_REMINDER", ignoreCase = true)) {
                    val details = JSONObject(data["Body"]!!)
                    medNotification.action = Constants.MEDICATION
                    medNotification.personID = details.getString("PersonID")
                    medNotification.medicineName = details.getString("Name")
                    medNotification.dosage = details.getString("Dosage")
                    medNotification.instruction = details.getString("Instruction")
                    medNotification.scheduleTime = details.getString("ScheduleTime")
                    medNotification.medicationID = details.getString("MedicationID")
                    medNotification.scheduleID = details.getString("ScheduleID")
                    medNotification.drugTypeCode = details.getString("DrugTypeCode")
                    medNotification.notificationDate =
                        details.getString("NotificationDate").split("T").toTypedArray()[0]
                    // For Self and Family Member also
                    viewModel.checkPersonExistAndShowNotification(this, medNotification)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    private fun showHealthTipNotification(context: Context, data: Map<String, String>) {
        try {
            val action = data["Action"]!!
            val title = data["title"]
            val message = data["text"]
            val imageURL = data["ImageURL"]
            Utilities.printLogError("Action--->$action")

            //In Android "O" or higher version, it's Mandatory to use a channel with your Notification Builder
            //int NOTIFICATION_ID = (int) System.currentTimeMillis();
            val notificationId = (Date().time / 1000L % Int.MAX_VALUE).toInt()
            val channelId = "fcm_medication_channel"

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            //val sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.vivant_notification_sound_new)

            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = context.resources.getString(R.string.app_name) + " Health Tips"
                val descriptionText = "Health Tips Notification"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(channelId, name, importance).apply {
                    description = descriptionText
                }
                notificationManager.createNotificationChannel(channel)
            }

            // Onclick of Notification Intent
            val onClick = createOnClickIntent(action, action, title!!, message!!, notificationId)
            if (!Utilities.isNullOrEmpty(imageURL)) {
                onClick.putExtra(Constants.NOTIFICATION_URL, imageURL)
            }
            val pendingOnClickIntent: PendingIntent? = TaskStackBuilder.create(this).run {
                // Add the intent, which inflates the back stack
                addNextIntentWithParentStack(onClick)
                // Get the PendingIntent containing the entire back stack
                getPendingIntent(
                    notificationId, PendingIntent.FLAG_UPDATE_CURRENT or
                            // mutability flag required when targeting Android12 or higher
                            PendingIntent.FLAG_IMMUTABLE
                )
            }

            val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.notification_logo)
                //.setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                //.setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round))
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker(context.resources.getString(R.string.app_name))
                .setContentIntent(pendingOnClickIntent)
                .build()

            Utilities.printLogError("displaying Notification")
            with(NotificationManagerCompat.from(this)) {
                // notificationId is a unique int for each notification that you must define
                notify(notificationId, builder)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    private fun showAktivoNotification(context: Context, data: Map<String, String>) {
        try {
            val notificationType = data["notificationType"]!!
            val message = data["template"]
            Utilities.printLogError("Aktivo_Notification_Type--->$notificationType")

            //In Android "O" or higher version, it's Mandatory to use a channel with your Notification Builder
            //int NOTIFICATION_ID = (int) System.currentTimeMillis();
            val notificationId = (Date().time / 1000L % Int.MAX_VALUE).toInt()
            val channelId = "fcm_medication_channel"

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = context.resources.getString(R.string.app_name) + " Aktivo"
                val descriptionText = "Aktivo Notification"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(channelId, name, importance).apply {
                    description = descriptionText
                }
                // Register the channel with the system
                notificationManager.createNotificationChannel(channel)
            }

            // Onclick of Notification Intent
            val onClick = createOnClickIntentAktivo(notificationType, message!!, notificationId)
            val pendingOnClickIntent: PendingIntent? = TaskStackBuilder.create(this).run {
                // Add the intent, which inflates the back stack
                addNextIntentWithParentStack(onClick)
                // Get the PendingIntent containing the entire back stack
                getPendingIntent(
                    notificationId, PendingIntent.FLAG_UPDATE_CURRENT or
                            // mutability flag required when targeting Android12 or higher
                            PendingIntent.FLAG_IMMUTABLE
                )
            }

            val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.notification_logo)
                //.setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                //.setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round))
                //.setContentTitle(notificationType)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker(context.resources.getString(R.string.app_name))
                .setContentIntent(pendingOnClickIntent)
                .build()

            Utilities.printLogError("displaying Notification")
            with(NotificationManagerCompat.from(this)) {
                // notificationId is a unique int for each notification that you must define
                notify(notificationId, builder)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    private fun showSpiritualHealthNotification(context: Context, data: Map<String, String>) {
        try {
            val screen = data["Screen"]!!
            val title = data["title"]!!
            val message = data["body"]!!
            val url = data["url"]!!
            val dataId = data["dataId"]!!
            Utilities.printLogError("Screen--->$screen")

            //In Android "O" or higher version, it's Mandatory to use a channel with your Notification Builder
            //int NOTIFICATION_ID = (int) System.currentTimeMillis();
            val notificationId = (Date().time / 1000L % Int.MAX_VALUE).toInt()
            val channelId = "fcm_spiritual_health_channel"

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            //val sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.vivant_notification_sound_new)

            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = context.resources.getString(R.string.app_name) + " Spiritual Health"
                val descriptionText = "Spiritual Health Notification"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(channelId, name, importance).apply {
                    description = descriptionText
                }
                notificationManager.createNotificationChannel(channel)
            }

            // Onclick of Notification Intent
            val onClick = createOnClickIntentSpiritualHealth(
                screen,
                dataId,
                url,
                title,
                message,
                notificationId
            )
            val pendingOnClickIntent: PendingIntent? = TaskStackBuilder.create(this).run {
                // Add the intent, which inflates the back stack
                addNextIntentWithParentStack(onClick)
                // Get the PendingIntent containing the entire back stack
                getPendingIntent(
                    notificationId, PendingIntent.FLAG_UPDATE_CURRENT or
                            // mutability flag required when targeting Android12 or higher
                            PendingIntent.FLAG_IMMUTABLE
                )
            }

            val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.notification_logo)
                //.setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                //.setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round))
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker(context.resources.getString(R.string.app_name))
                .setContentIntent(pendingOnClickIntent)
                .build()

            Utilities.printLogError("displaying Notification")
            with(NotificationManagerCompat.from(this)) {
                // notificationId is a unique int for each notification that you must define
                notify(notificationId, builder)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    private fun showGeneralWebUrlNotification(context: Context, data: Map<String, String>) {
        try {
            val screen = data["Screen"]!!
            val title = data["title"]!!
            val message = data["body"]!!
            val redirectType = data["redirectType"]!!
            val url = data["url"]!!
            var imageUrl = ""
            if (data.containsKey("imageUrl")) {
                imageUrl = data["imageUrl"]!!
            }
            Utilities.printLogError("Screen--->$screen")

            //In Android "O" or higher version, it's Mandatory to use a channel with your Notification Builder
            //int NOTIFICATION_ID = (int) System.currentTimeMillis();
            val notificationId = (Date().time / 1000L % Int.MAX_VALUE).toInt()
            val channelId = "fcm_web_url_channel"

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            //val sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.vivant_notification_sound_new)

            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = context.resources.getString(R.string.app_name) + " Web Url"
                val descriptionText = "Web Url Notification"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(channelId, name, importance).apply {
                    description = descriptionText
                }
                notificationManager.createNotificationChannel(channel)
            }

            // Onclick of Notification Intent
            val onClick = createOnClickIntentGeneralWebUrlNotification(
                screen,
                redirectType,
                url,
                title,
                message,
                notificationId
            )
            val pendingOnClickIntent: PendingIntent? = TaskStackBuilder.create(this).run {
                // Add the intent, which inflates the back stack
                addNextIntentWithParentStack(onClick)
                // Get the PendingIntent containing the entire back stack
                getPendingIntent(
                    notificationId, PendingIntent.FLAG_UPDATE_CURRENT or
                            // mutability flag required when targeting Android12 or higher
                            PendingIntent.FLAG_IMMUTABLE
                )
            }

            val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.notification_logo)
                //.setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                //.setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round))
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker(context.resources.getString(R.string.app_name))
                .setContentIntent(pendingOnClickIntent)

            if (!Utilities.isNullOrEmpty(imageUrl)) {
                applyImageUrl(builder, imageUrl)
            }

            Utilities.printLogError("displaying Notification")
            with(NotificationManagerCompat.from(this)) {
                // notificationId is a unique int for each notification that you must define
                notify(notificationId, builder.build())
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun applyImageUrl(builder: NotificationCompat.Builder, imageUrl: String) = runBlocking {
        try {
            val url = URL(imageUrl)
            withContext(Dispatchers.IO) {
                val input = url.openStream()
                val bitmap = BitmapFactory.decodeStream(input)
                Utilities.printLogError("Applying Large ImageUrl")
                builder.setLargeIcon(bitmap)
                /*                builder.setStyle(NotificationCompat.BigPictureStyle()
                                    .bigPicture(bitmap)
                                    )*/
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    private fun showAppUpdateNotification(context: Context, data: Map<String, String>) {
        try {
            val action = data["Action"]
            val title = data["title"]
            val message = data["text"]
            //val imageURL = data["ImageURL"]
            Utilities.printLogError("Action--->$action")
            Utilities.printLogError("title--->$title")
            Utilities.printLogError("message--->$message")

            //In Android "O" or higher version, it's Mandatory to use a channel with your Notification Builder
            //int NOTIFICATION_ID = (int) System.currentTimeMillis();
            val notificationId = (Date().time / 1000L % Int.MAX_VALUE).toInt()
            val channelId = "fcm_medication_channel"

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            //val sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.vivant_notification_sound_new)

            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = context.resources.getString(R.string.app_name) + " Update"
                val descriptionText = "App Update Notification"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(channelId, name, importance).apply {
                    description = descriptionText
                }
                /*                val attributes = AudioAttributes.Builder()
                                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                                    .build()
                                channel.setSound(sound, attributes)*/
                // Register the channel with the system
                notificationManager.createNotificationChannel(channel)
            }

            // Onclick of Notification Intent
            val onClick =
                createOnClickIntent("APP_UPDATE", action!!, title!!, message!!, notificationId)
            onClick.action = action
            val pendingOnClickIntent: PendingIntent? = TaskStackBuilder.create(this).run {
                // Add the intent, which inflates the back stack
                addNextIntentWithParentStack(onClick)
                // Get the PendingIntent containing the entire back stack
                getPendingIntent(
                    notificationId, PendingIntent.FLAG_UPDATE_CURRENT or
                            // mutability flag required when targeting Android12 or higher
                            PendingIntent.FLAG_IMMUTABLE
                )
            }

            val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.notification_logo)
                //.setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                //.setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round))
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker(context.resources.getString(R.string.app_name))
                .setContentIntent(pendingOnClickIntent)
                .build()

            Utilities.printLogError("displaying Notification")
            with(NotificationManagerCompat.from(this)) {
                // notificationId is a unique int for each notification that you must define
                notify(notificationId, builder)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    private fun showWaterReminderNotificationCustom(data: Map<String, String>) {
        try {
            //val personId = data["personid"]
            val action = data["Action"]
            val screen = data["Screen"]
            val title = data["title"]
            val message = data["body"]
            val timeToDisplay: String =
                DateHelper.getTimeIn12HrFormatAmOrPm(DateHelper.currentTimeAs_hh_mm_ss)
            Utilities.printLogError("Screen--->$screen")

            //In Android "O" or higher version, it's Mandatory to use a channel with your Notification Builder
            //int NOTIFICATION_ID = (int) System.currentTimeMillis();
            val notificationId = (Date().time / 1000L % Int.MAX_VALUE).toInt() + 1
            val channelId = "fcm_medication_channel" // The id of the channel.
            val name: CharSequence = "Medicine Reminders" // The user-visible name of the channel.

            //val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val mNotificationManager = NotificationManagerCompat.from(this)

            //val sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.vivant_notification_sound_new)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val mChannel =
                    NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_HIGH)
                /*                val attributes = AudioAttributes.Builder()
                                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                                    .build()
                                mChannel.setSound(sound, attributes)*/
                mNotificationManager.createNotificationChannel(mChannel)
            }

            // Onclick of Notification Intent
            val onClick =
                createOnClickIntent(screen!!, action!!, title!!, message!!, notificationId)
            val pendingOnClickIntent: PendingIntent? = TaskStackBuilder.create(this).run {
                // Add the intent, which inflates the back stack
                addNextIntentWithParentStack(onClick)
                // Get the PendingIntent containing the entire back stack
                getPendingIntent(
                    notificationId, PendingIntent.FLAG_UPDATE_CURRENT or
                            // mutability flag required when targeting Android12 or higher
                            PendingIntent.FLAG_IMMUTABLE
                )
            }

            // Notification's Collapsed layout
            val remoteViewCollapsed =
                RemoteViews(packageName, R.layout.water_notification_collapsed)
            remoteViewCollapsed.setTextViewText(R.id.water_notification_title, title)
            remoteViewCollapsed.setTextViewText(R.id.water_notification_subtext, message)
            remoteViewCollapsed.setTextViewText(R.id.txt_time, timeToDisplay)
            if (screen == "WATER_REMINDER_21_POSITIVE") {
                remoteViewCollapsed.setImageViewResource(
                    R.id.img_water,
                    R.drawable.img_water_reminder_achieved
                )
            } else {
                remoteViewCollapsed.setImageViewResource(
                    R.id.img_water,
                    R.drawable.img_water_reminder
                )
            }
            remoteViewCollapsed.setOnClickPendingIntent(
                R.id.med_reminder_collapsed,
                pendingOnClickIntent
            )

            // Notification's Expanded layout
            val remoteViewExpanded = RemoteViews(packageName, R.layout.water_notification_expanded)
            remoteViewExpanded.setTextViewText(R.id.water_notification_title, title)
            remoteViewExpanded.setTextViewText(R.id.water_notification_subtext, message)
            remoteViewExpanded.setTextViewText(R.id.txt_time, timeToDisplay)
            if (screen == "WATER_REMINDER_21_POSITIVE") {
                remoteViewExpanded.setImageViewResource(
                    R.id.img_water,
                    R.drawable.img_water_reminder_achieved
                )
            } else {
                remoteViewExpanded.setImageViewResource(
                    R.id.img_water,
                    R.drawable.img_water_reminder
                )
            }
            remoteViewExpanded.setOnClickPendingIntent(
                R.id.med_reminder_expanded,
                pendingOnClickIntent
            )

            // Apply the layouts to the notification
            val customNotification = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.notification_logo)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker(resources.getString(R.string.app_name))
                .setContentIntent(pendingOnClickIntent)
                .setContent(remoteViewExpanded)
                .setContent(remoteViewCollapsed)
                .setCustomContentView(remoteViewCollapsed)
                .setCustomBigContentView(remoteViewExpanded)
                .build()

            Utilities.printLogError("displaying Notification")
            mNotificationManager.notify(notificationId, customNotification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    private fun showWaterReminderNotification(context: Context, data: Map<String, String>) {
        try {
            val action = data["Action"]
            val screen = data["Screen"]
            val title = data["title"]
            val message = data["body"]
            //val message = Html.fromHtml(data["body"])
            Utilities.printLogError("Screen--->$screen")

            //In Android "O" or higher version, it's Mandatory to use a channel with your Notification Builder
            //int NOTIFICATION_ID = (int) System.currentTimeMillis();
            val notificationId = (Date().time / 1000L % Int.MAX_VALUE).toInt()
            val channelId = "fcm_medication_channel"

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            //val sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.vivant_notification_sound_new)

            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = context.resources.getString(R.string.app_name) + " Notification"
                val descriptionText = "Navigation Notification"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(channelId, name, importance).apply {
                    description = descriptionText
                }
                /*                val attributes = AudioAttributes.Builder()
                                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                                    .build()
                                channel.setSound(sound, attributes)*/
                // Register the channel with the system
                notificationManager.createNotificationChannel(channel)
            }

            // Onclick of Notification Intent
            val onClick =
                createOnClickIntent(screen!!, action!!, title!!, message!!, notificationId)
            val pendingOnClickIntent: PendingIntent? = TaskStackBuilder.create(this).run {
                // Add the intent, which inflates the back stack
                addNextIntentWithParentStack(onClick)
                // Get the PendingIntent containing the entire back stack
                getPendingIntent(
                    notificationId, PendingIntent.FLAG_UPDATE_CURRENT or
                            // mutability flag required when targeting Android12 or higher
                            PendingIntent.FLAG_IMMUTABLE
                )
            }

            val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.notification_logo)
                //.setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        resources,
                        R.drawable.img_water_reminder
                    )
                )
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                //.setSound(alarmSound)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker(context.resources.getString(R.string.app_name))
                .setContentIntent(pendingOnClickIntent)
                .build()

            Utilities.printLogError("displaying Notification")
            with(NotificationManagerCompat.from(this)) {
                // notificationId is a unique int for each notification that you must define
                notify(notificationId, builder)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sendRegistrationToServer(fcmToken: String) {
        //viewModel2.refreshFcmToken(fcmToken)
        if (viewModel.isUserLoggedIn()) {
            backgroundApiCallViewModel.callSaveCloudMessagingIdApi(fcmToken, true)
        }
    }

    private fun createOnClickIntent(
        screen: String,
        action: String,
        title: String,
        message: String,
        notificationId: Int
    ): Intent {
        val onClick = Intent()
        onClick.putExtra(Constants.SCREEN, screen)
        onClick.putExtra(Constants.NOTIFICATION_ACTION, action)
        onClick.putExtra(Constants.NOTIFICATION_TITLE, title)
        onClick.putExtra(Constants.NOTIFICATION_MESSAGE, message)
        onClick.putExtra(Constants.NOTIFICATION_ID, notificationId)
        onClick.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        onClick.component =
            ComponentName(NavigationConstants.APPID, NavigationConstants.SPLASH_SCREEN)
        return onClick
    }

    private fun createOnClickIntentAktivo(
        notificationType: String,
        template: String,
        notificationId: Int
    ): Intent {
        val onClick = Intent()
        onClick.putExtra(Constants.NOTIFICATION_TYPE, notificationType)
        onClick.putExtra(Constants.NOTIFICATION_MESSAGE, template)
        onClick.putExtra(Constants.NOTIFICATION_ID, notificationId)
        onClick.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        onClick.component =
            ComponentName(NavigationConstants.APPID, NavigationConstants.SPLASH_SCREEN)
        return onClick
    }

    private fun createOnClickIntentSpiritualHealth(
        screen: String,
        dataId: String,
        url: String,
        title: String,
        message: String,
        notificationId: Int
    ): Intent {
        val onClick = Intent()
        onClick.putExtra(Constants.SCREEN, screen)
        onClick.putExtra(Constants.DATA_ID, dataId)
        onClick.putExtra(Constants.WEB_URL, url)
        onClick.putExtra(Constants.NOTIFICATION_TITLE, title)
        onClick.putExtra(Constants.NOTIFICATION_MESSAGE, message)
        onClick.putExtra(Constants.NOTIFICATION_ID, notificationId)
        onClick.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        onClick.component =
            ComponentName(NavigationConstants.APPID, NavigationConstants.SPLASH_SCREEN)
        return onClick
    }

    private fun createOnClickIntentGeneralWebUrlNotification(
        screen: String,
        redirectType: String,
        url: String,
        title: String,
        message: String,
        notificationId: Int
    ): Intent {
        val onClick = Intent()
        onClick.putExtra(Constants.SCREEN, screen)
        onClick.putExtra(Constants.REDIRECT_TYPE, redirectType)
        onClick.putExtra(Constants.WEB_URL, url)
        onClick.putExtra(Constants.NOTIFICATION_TITLE, title)
        onClick.putExtra(Constants.NOTIFICATION_MESSAGE, message)
        onClick.putExtra(Constants.NOTIFICATION_ID, notificationId)
        onClick.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        onClick.component =
            ComponentName(NavigationConstants.APPID, NavigationConstants.SPLASH_SCREEN)
        return onClick
    }
}

/*
class FCMMessagingService : FirebaseMessagingService() {

    private val tag = "FCMMessagingService : "
    private val medNotification = ReminderNotification()

    //private var lifeCycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

    @Inject
    lateinit var viewModel: MedicineTrackerViewModel

    @Inject
    lateinit var backgroundApiCallViewModel: BackgroundCallViewModel


    override fun onNewToken(token: String) {
        Utilities.printLogError("$tag Refreshed token--->$token")
        sendRegistrationToServer(token)

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        try {

            //val notification = remoteMessage.notification
            val data = remoteMessage.data
            FirebaseHelper.logCustomFirebaseEventWithData(
                FirebaseConstants.NOTIFICATION_RECEIVE, data.toString()
            )
            Utilities.printData("onMessageReceived(FCMMessagingService): data", data, true)
            if (viewModel.isUserLoggedIn()) {

//                if (backgroundApiCallViewModel.isPushNotificationEnabled()) {
                val action = data["Action"]!!
                //val screen = data["Screen"]!!
                if (!Utilities.isNullOrEmpty(action) && action.equals(
                        "HEALTHTIPS", ignoreCase = true
                    )
                ) {
                    showHealthTipNotification(this, data)
                } else if (!Utilities.isNullOrEmpty(action) && action.equals(
                        "APP_UPDATE", ignoreCase = true
                    )
                ) {
                    showAppUpdateNotification(this, data)
                } else if (!Utilities.isNullOrEmpty(data["Screen"]!!)) {
                    when (data["Screen"]!!) {
                        "MEDICATION_REMINDER" -> {
                            showMedicineReminderNotification(data)
                        }

                        "WATER_REMINDER", "WATER_REMINDER_21_POSITIVE", "WATER_REMINDER_21_NEGATIVE" -> {
                            if (data["personid"] == backgroundApiCallViewModel.getMainUserPersonID()) {
                                showWaterReminderNotificationCustom(data)
                                //showWaterReminderNotification(this,data)
                            }
                        }

                        "DASHBOARD" -> {
                            displayAmahaNotification(this, data)
                        }

                        "STEPS", "STEPS_WEEKLY_SYNOPSIS", "STEPS_DAILY_TARGET" -> {
                            Utilities.printLogError("Fitness Tracker Notifications Disabled")
                        }
                        "SPIRITUAL_WELLNESS" -> {
                            showSpiritualHealthNotification(this,data)
                        }
                        else -> {
                            displayAppNavigationNotification(this, data)
                        }
                    }
                }
//                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    private fun displayAppNavigationNotification(context: Context, data: Map<String, String>) {
        try {
            val action = data["Action"]
            val screen = data["Screen"]
            val title = data["title"]
            val message = data["body"]
            //val message = Html.fromHtml(data["body"])
            Utilities.printLogError("Screen--->$screen")

            //In Android "O" or higher version, it's Mandatory to use a channel with your Notification Builder
            //int NOTIFICATION_ID = (int) System.currentTimeMillis();
            val notificationId = (Date().time / 1000L % Int.MAX_VALUE).toInt()
            val channelId = "fcm_medication_channel"

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            //val sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.vivant_notification_sound_new)

            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            val name = context.resources.getString(R.string.app_name) + " Notification"
            val descriptionText = "Navigation Notification"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }*/
/*                val attributes = AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                            .build()
                        channel.setSound(sound, attributes)*//*

            // Register the channel with the system
            notificationManager.createNotificationChannel(channel)

            // Onclick of Notification Intent
            val onClick =
                createOnClickIntent(screen!!, action!!, title!!, message!!, notificationId)
            val pendingOnClickIntent: PendingIntent? = TaskStackBuilder.create(this).run {
                // Add the intent, which inflates the back stack
                addNextIntentWithParentStack(onClick)
                // Get the PendingIntent containing the entire back stack
                getPendingIntent(
                    notificationId, PendingIntent.FLAG_UPDATE_CURRENT or
                            // mutability flag required when targeting Android12 or higher
                            PendingIntent.FLAG_IMMUTABLE
                )
            }

            val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.notification_logo)
                //.setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                //.setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round))
                .setContentTitle(title).setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message)).setAutoCancel(true)
                //.setSound(alarmSound)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker(context.resources.getString(R.string.app_name))
                .setContentIntent(pendingOnClickIntent).build()

            Utilities.printLogError("displaying Notification")
            with(NotificationManagerCompat.from(this)) {
                // notificationId is a unique int for each notification that you must define
                notify(notificationId, builder)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    private fun displayAmahaNotification(context: Context, data: Map<String, String>) {
        try {
            val action = data["Action"]
            val screen = data["Screen"]
            val title = data["title"]
            val message = data["body"]
            val redirectLink = data["redirect_link"]
            //val message = Html.fromHtml(data["body"])
            Utilities.printLogError("Screen--->$screen")

            //In Android "O" or higher version, it's Mandatory to use a channel with your Notification Builder
            //int NOTIFICATION_ID = (int) System.currentTimeMillis();
            val notificationId = (Date().time / 1000L % Int.MAX_VALUE).toInt()
            val channelId = "fcm_medication_channel"

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            //val sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.vivant_notification_sound_new)

            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            val name = context.resources.getString(R.string.app_name) + " Notification"
            val descriptionText = "Amaha Notification"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }*/
/*                val attributes = AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                            .build()
                        channel.setSound(sound, attributes)*//*

            // Register the channel with the system
            notificationManager.createNotificationChannel(channel)

            // Onclick of Notification Intent
            val onClick =
                createOnClickIntent(screen!!, action!!, title!!, message!!, notificationId)
            onClick.putExtra(Constants.REDIRECT_LINK, redirectLink)
            val pendingOnClickIntent: PendingIntent? = TaskStackBuilder.create(this).run {
                // Add the intent, which inflates the back stack
                addNextIntentWithParentStack(onClick)
                // Get the PendingIntent containing the entire back stack
                getPendingIntent(
                    notificationId, PendingIntent.FLAG_UPDATE_CURRENT or
                            // mutability flag required when targeting Android12 or higher
                            PendingIntent.FLAG_IMMUTABLE
                )
            }

            val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.notification_logo)
                //.setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                //.setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round))
                .setContentTitle(title).setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message)).setAutoCancel(true)
                //.setSound(alarmSound)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker(context.resources.getString(R.string.app_name))
                .setContentIntent(pendingOnClickIntent).build()

            Utilities.printLogError("displaying Notification")
            with(NotificationManagerCompat.from(this)) {
                // notificationId is a unique int for each notification that you must define
                notify(notificationId, builder)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showMedicineReminderNotification(data: Map<String, String>) {
        try {
            val screen = data["Screen"]
            if (!Utilities.isNullOrEmpty(screen)) {
                Utilities.printLogError("Screen--->$screen")
                if (screen.equals("MEDICATION_REMINDER", ignoreCase = true)) {
                    val details = JSONObject(data["Body"]!!)
                    medNotification.action = Constants.MEDICATION
                    medNotification.personID = details.getString("PersonID")
                    medNotification.medicineName = details.getString("Name")
                    medNotification.dosage = details.getString("Dosage")
                    medNotification.instruction = details.getString("Instruction")
                    medNotification.scheduleTime = details.getString("ScheduleTime")
                    medNotification.medicationID = details.getString("MedicationID")
                    medNotification.scheduleID = details.getString("ScheduleID")
                    medNotification.drugTypeCode = details.getString("DrugTypeCode")
                    medNotification.notificationDate =
                        details.getString("NotificationDate").split("T").toTypedArray()[0]
                    // For Self and Family Member also
                    viewModel.checkRelativeExistAndShowNotification(this, medNotification)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    private fun showHealthTipNotification(context: Context, data: Map<String, String>) {
        try {
            val action = data["Action"]!!
            val title = data["title"]
            val message = data["text"]
            val imageURL = data["ImageURL"]
            Utilities.printLogError("Action--->$action")

            //In Android "O" or higher version, it's Mandatory to use a channel with your Notification Builder
            //int NOTIFICATION_ID = (int) System.currentTimeMillis();
            val notificationId = (Date().time / 1000L % Int.MAX_VALUE).toInt()
            val channelId = "fcm_medication_channel"

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            //val sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.vivant_notification_sound_new)

            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            val name = context.resources.getString(R.string.app_name) + " Health Tips"
            val descriptionText = "Health Tips Notification"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)

            // Onclick of Notification Intent
            val onClick = createOnClickIntent(action, action, title!!, message!!, notificationId)
            if (!Utilities.isNullOrEmpty(imageURL)) {
                onClick.putExtra(Constants.NOTIFICATION_URL, imageURL)
            }
            val pendingOnClickIntent: PendingIntent? = TaskStackBuilder.create(this).run {
                // Add the intent, which inflates the back stack
                addNextIntentWithParentStack(onClick)
                // Get the PendingIntent containing the entire back stack
                getPendingIntent(
                    notificationId, PendingIntent.FLAG_UPDATE_CURRENT or
                            // mutability flag required when targeting Android12 or higher
                            PendingIntent.FLAG_IMMUTABLE
                )
            }

            val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.notification_logo)
                //.setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                //.setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round))
                .setContentTitle(title).setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message)).setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker(context.resources.getString(R.string.app_name))
                .setContentIntent(pendingOnClickIntent).build()

            Utilities.printLogError("displaying Notification")
            with(NotificationManagerCompat.from(this)) {
                // notificationId is a unique int for each notification that you must define
                notify(notificationId, builder)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    private fun showSpiritualHealthNotification(context: Context, data: Map<String, String>) {
        try {
            val screen = data["Screen"]!!
            val dataId = data["DataId"]!!
            val url = data["Url"]!!
            val title = data["nt"]!!
            val message = data["nm"]!!
            Utilities.printLogError("Screen--->$screen")

            //In Android "O" or higher version, it's Mandatory to use a channel with your Notification Builder
            //int NOTIFICATION_ID = (int) System.currentTimeMillis();
            val notificationId = (Date().time / 1000L % Int.MAX_VALUE).toInt()
            val channelId = "fcm_spiritual_health_channel"

            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            //val sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.vivant_notification_sound_new)

            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = context.resources.getString(R.string.app_name) + " Spiritual Health"
                val descriptionText = "Spiritual Health Notification"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(channelId, name, importance).apply {
                    description = descriptionText
                }
                notificationManager.createNotificationChannel(channel)
            }

            // Onclick of Notification Intent
            val onClick = createOnClickIntentSpiritualHealth(screen,dataId,url,title,message,notificationId)
            val pendingOnClickIntent: PendingIntent? = TaskStackBuilder.create(this).run {
                // Add the intent, which inflates the back stack
                addNextIntentWithParentStack(onClick)
                // Get the PendingIntent containing the entire back stack
                getPendingIntent(notificationId,PendingIntent.FLAG_UPDATE_CURRENT or
                        // mutability flag required when targeting Android12 or higher
                        PendingIntent.FLAG_IMMUTABLE)
            }

            val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.notification_logo)
                //.setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                //.setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round))
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker(context.resources.getString(R.string.app_name))
                .setContentIntent(pendingOnClickIntent)
                .build()

            Utilities.printLogError("displaying Notification")
            with(NotificationManagerCompat.from(this)) {
                // notificationId is a unique int for each notification that you must define
                notify(notificationId, builder)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    private fun showAppUpdateNotification(context: Context, data: Map<String, String>) {
        try {
            val action = data["Action"]
            val title = data["title"]
            val message = data["text"]
            //val imageURL = data["ImageURL"]
            Utilities.printLogError("Action--->$action")
            Utilities.printLogError("title--->$title")
            Utilities.printLogError("message--->$message")

            //In Android "O" or higher version, it's Mandatory to use a channel with your Notification Builder
            //int NOTIFICATION_ID = (int) System.currentTimeMillis();
            val notificationId = (Date().time / 1000L % Int.MAX_VALUE).toInt()
            val channelId = "fcm_medication_channel"

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            //val sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.vivant_notification_sound_new)

            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            val name = context.resources.getString(R.string.app_name) + " Update"
            val descriptionText = "App Update Notification"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }*/
/*                val attributes = AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                            .build()
                        channel.setSound(sound, attributes)*//*

            // Register the channel with the system
            notificationManager.createNotificationChannel(channel)

            // Onclick of Notification Intent
            val onClick =
                createOnClickIntent("APP_UPDATE", action!!, title!!, message!!, notificationId)
            onClick.action = action
            val pendingOnClickIntent: PendingIntent? = TaskStackBuilder.create(this).run {
                // Add the intent, which inflates the back stack
                addNextIntentWithParentStack(onClick)
                // Get the PendingIntent containing the entire back stack
                getPendingIntent(
                    notificationId, PendingIntent.FLAG_UPDATE_CURRENT or
                            // mutability flag required when targeting Android12 or higher
                            PendingIntent.FLAG_IMMUTABLE
                )
            }

            val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.notification_logo)
                //.setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                //.setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round))
                .setContentTitle(title).setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message)).setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker(context.resources.getString(R.string.app_name))
                .setContentIntent(pendingOnClickIntent).build()

            Utilities.printLogError("displaying Notification")
            with(NotificationManagerCompat.from(this)) {
                // notificationId is a unique int for each notification that you must define
                notify(notificationId, builder)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    private fun showWaterReminderNotificationCustom(data: Map<String, String>) {
        try {
            //val personId = data["personid"]
            val action = data["Action"]
            val screen = data["Screen"]
            val title = data["title"]
            val message = data["body"]
            val timeToDisplay: String =
                DateHelper.getTimeIn12HrFormatAmOrPm(DateHelper.currentTimeAs_hh_mm_ss)
            Utilities.printLogError("Screen--->$screen")

            //In Android "O" or higher version, it's Mandatory to use a channel with your Notification Builder
            //int NOTIFICATION_ID = (int) System.currentTimeMillis();
            val notificationId = (Date().time / 1000L % Int.MAX_VALUE).toInt() + 1
            val channelId = "fcm_medication_channel" // The id of the channel.
            val name: CharSequence = "Medicine Reminders" // The user-visible name of the channel.

            //val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val mNotificationManager = NotificationManagerCompat.from(this)

            //val sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.vivant_notification_sound_new)

            val mChannel = NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_HIGH)*/
/*                val attributes = AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                            .build()
                        mChannel.setSound(sound, attributes)*//*

            mNotificationManager.createNotificationChannel(mChannel)

            // Onclick of Notification Intent
            val onClick =
                createOnClickIntent(screen!!, action!!, title!!, message!!, notificationId)
            val pendingOnClickIntent: PendingIntent? = TaskStackBuilder.create(this).run {
                // Add the intent, which inflates the back stack
                addNextIntentWithParentStack(onClick)
                // Get the PendingIntent containing the entire back stack
                getPendingIntent(
                    notificationId, PendingIntent.FLAG_UPDATE_CURRENT or
                            // mutability flag required when targeting Android12 or higher
                            PendingIntent.FLAG_IMMUTABLE
                )
            }

            // Notification's Collapsed layout
            val remoteViewCollapsed =
                RemoteViews(packageName, R.layout.water_notification_collapsed)
            remoteViewCollapsed.setTextViewText(R.id.water_notification_title, title)
            remoteViewCollapsed.setTextViewText(R.id.water_notification_subtext, message)
            remoteViewCollapsed.setTextViewText(R.id.txt_time, timeToDisplay)
            if (screen == "WATER_REMINDER_21_POSITIVE") {
                remoteViewCollapsed.setImageViewResource(
                    R.id.img_water, R.drawable.img_water_reminder_achieved
                )
            } else {
                remoteViewCollapsed.setImageViewResource(
                    R.id.img_water, R.drawable.img_water_reminder
                )
            }
            remoteViewCollapsed.setOnClickPendingIntent(
                R.id.med_reminder_collapsed, pendingOnClickIntent
            )

            // Notification's Expanded layout
            val remoteViewExpanded = RemoteViews(packageName, R.layout.water_notification_expanded)
            remoteViewExpanded.setTextViewText(R.id.water_notification_title, title)
            remoteViewExpanded.setTextViewText(R.id.water_notification_subtext, message)
            remoteViewExpanded.setTextViewText(R.id.txt_time, timeToDisplay)
            if (screen == "WATER_REMINDER_21_POSITIVE") {
                remoteViewExpanded.setImageViewResource(
                    R.id.img_water, R.drawable.img_water_reminder_achieved
                )
            } else {
                remoteViewExpanded.setImageViewResource(
                    R.id.img_water, R.drawable.img_water_reminder
                )
            }
            remoteViewExpanded.setOnClickPendingIntent(
                R.id.med_reminder_expanded, pendingOnClickIntent
            )

            // Apply the layouts to the notification
            val customNotification = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.notification_logo)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle()).setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker(resources.getString(R.string.app_name))
                .setContentIntent(pendingOnClickIntent).setContent(remoteViewExpanded)
                .setContent(remoteViewCollapsed).setCustomContentView(remoteViewCollapsed)
                .setCustomBigContentView(remoteViewExpanded).build()

            Utilities.printLogError("displaying Notification")
            mNotificationManager.notify(notificationId, customNotification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    private fun showWaterReminderNotification(context: Context, data: Map<String, String>) {
        try {
            val action = data["Action"]
            val screen = data["Screen"]
            val title = data["title"]
            val message = data["body"]
            //val message = Html.fromHtml(data["body"])
            Utilities.printLogError("Screen--->$screen")

            //In Android "O" or higher version, it's Mandatory to use a channel with your Notification Builder
            //int NOTIFICATION_ID = (int) System.currentTimeMillis();
            val notificationId = (Date().time / 1000L % Int.MAX_VALUE).toInt()
            val channelId = "fcm_medication_channel"

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            //val sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.vivant_notification_sound_new)

            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            val name = context.resources.getString(R.string.app_name) + " Notification"
            val descriptionText = "Navigation Notification"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }*/
/*                val attributes = AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                            .build()
                        channel.setSound(sound, attributes)*//*

            // Register the channel with the system
            notificationManager.createNotificationChannel(channel)

            // Onclick of Notification Intent
            val onClick =
                createOnClickIntent(screen!!, action!!, title!!, message!!, notificationId)
            val pendingOnClickIntent: PendingIntent? = TaskStackBuilder.create(this).run {
                // Add the intent, which inflates the back stack
                addNextIntentWithParentStack(onClick)
                // Get the PendingIntent containing the entire back stack
                getPendingIntent(
                    notificationId, PendingIntent.FLAG_UPDATE_CURRENT or
                            // mutability flag required when targeting Android12 or higher
                            PendingIntent.FLAG_IMMUTABLE
                )
            }

            val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.notification_logo)
                //.setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        resources, R.drawable.img_water_reminder
                    )
                ).setContentTitle(title).setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message)).setAutoCancel(true)
                //.setSound(alarmSound)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker(context.resources.getString(R.string.app_name))
                .setContentIntent(pendingOnClickIntent).build()

            Utilities.printLogError("displaying Notification")
            with(NotificationManagerCompat.from(this)) {
                // notificationId is a unique int for each notification that you must define
                notify(notificationId, builder)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sendRegistrationToServer(fcmToken: String) {
        //viewModel2.refreshFcmToken(fcmToken)
        if (viewModel.isUserLoggedIn()) {
            backgroundApiCallViewModel.callSaveCloudMessagingIdApi(fcmToken, true)
        }
    }

    private fun createOnClickIntent(
        screen: String, action: String, title: String, message: String, notificationId: Int
    ): Intent {
        val onClick = Intent()
        onClick.putExtra(Constants.SCREEN, screen)
        onClick.putExtra(Constants.NOTIFICATION_ACTION, action)
        onClick.putExtra(Constants.NOTIFICATION_TITLE, title)
        onClick.putExtra(Constants.NOTIFICATION_MESSAGE, message)
        onClick.putExtra(Constants.NOTIFICATION_ID, notificationId)
        onClick.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        onClick.component =
            ComponentName(NavigationConstants.APPID, NavigationConstants.SPLASH_SCREEN)
        return onClick
    }

    private fun createOnClickIntentSpiritualHealth(screen:String,dataId:String,url:String,title:String,message:String,notificationId:Int) : Intent {
        val onClick = Intent()
        onClick.putExtra(Constants.SCREEN, screen)
        onClick.putExtra(Constants.DATA_ID, dataId)
        onClick.putExtra(Constants.WEB_URL, url)
        onClick.putExtra(Constants.NOTIFICATION_TITLE, title)
        onClick.putExtra(Constants.NOTIFICATION_MESSAGE, message)
        onClick.putExtra(Constants.NOTIFICATION_ID, notificationId)
        onClick.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        onClick.component = ComponentName(NavigationConstants.APPID, NavigationConstants.SPLASH_SCREEN)
        return onClick
    }

}*/

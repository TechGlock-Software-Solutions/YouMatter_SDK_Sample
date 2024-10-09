package com.techglock.health.app.fitness_tracker.util.workmanager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.techglock.health.app.BuildConfig;
import com.techglock.health.app.R;
import com.techglock.health.app.common.constants.NavigationConstants;
import com.evernote.android.job.JobManager;

/**
 * - This is Helper class for starting JobService,
 * - We will set alarms everyday midnight at 12:00 for Everyday scheduling a particular task
 */

public class JobDispatcherStepsHelper {
    private static String TAG = "JobProxySteps_" + JobDispatcherStepsHelper.class.getSimpleName();

    public static void scheduleMidnightJobDispatcher() {
        DayChangeStepsSyncJob.schedule();
    }

    public static void schedulePeriodicJobDispatcher() {
        PeriodicStepsSyncJob.schedulePeriodicJobDispatcher();
        if (BuildConfig.DEBUG) {
            //PeriodicStepsSyncJob.schedulePeriodicTestJobDispatcher();
        }
    }

    public static void cancelAllAlarmJobDispatcher() {
        try {
            JobManager.instance().cancelAll();
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    public static void cancelAlarmJobDispatcher(String scheduleId) {
        try {
            JobManager.instance().cancelAllForTag(scheduleId);
        } catch (Exception e) {
            //  e.printStackTrace();
        }
    }

    public static void cancelMidnightJobDispatcher() {
        JobManager.instance().cancelAllForTag(DayChangeStepsSyncJob.DAY_CHANGE_JOB_TAG);
    }

    public static void checkJobManagerInstanceAvailability(Context mContext) {
        JobManager.create(mContext).addJobCreator(StepsSyncJobCreator.getInstance());
    }

    /**
     * Sends notification in DEBUG mode only
     */

    public static void sendDebugNotification(Context mContext, String msg) {
        try {
            if (BuildConfig.DEBUG) {
                /*if (mContext == null) {
                    mContext = AppAH.getContext();
                }*/
                if (mContext != null) {
                    NotificationManager alarmNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    //Added new : In Android "O" or higher version, it's Mandatory to use a channel with your Notification Builder
                    String CHANNEL_ID = "channel_fitness";// The id of the channel.
                    CharSequence name = "Fitness sync";// The user-visible name of the channel.
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    int NOTIFICATION_ID = (int) System.currentTimeMillis();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                        alarmNotificationManager.createNotificationChannel(mChannel);
                    }

                    if (alarmNotificationManager != null) {
                        Intent intent = new Intent();

                        //intent.setComponent(new ComponentName(ConfigurationHelper.getInstance().configurations().strCallerPackage, "com.caressa.libs.fitness.StepHomeActivity"));
                        intent.setComponent(new ComponentName(NavigationConstants.APPID, NavigationConstants.FITNESS_HOME));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //get pending intent
                        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 111111, intent, PendingIntent.FLAG_IMMUTABLE);
                        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                        //Create notification
                        NotificationCompat.Builder alarmNotificationBuilder =
                                new NotificationCompat.Builder(mContext, "FITNESS_TRACKER")
                                        .setContentTitle("Debug Notification")
                                        .setSmallIcon(R.drawable.notification_logo)
                                        //.setColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                                        //.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_app_notification_icon))
                                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                                        .setContentText(msg)
                                        .setSound(alarmSound)
                                        .setAutoCancel(true)
                                        .setChannelId(CHANNEL_ID)
                                        .setDefaults(Notification.DEFAULT_SOUND)
                                        .setPriority(Notification.PRIORITY_HIGH);

                        alarmNotificationBuilder.setContentIntent(pendingIntent);
                        alarmNotificationManager.notify(NOTIFICATION_ID, alarmNotificationBuilder.build());
                    }
                }
            }
        } catch (Exception e) {
            //  e.printStackTrace();
        }
    }
}

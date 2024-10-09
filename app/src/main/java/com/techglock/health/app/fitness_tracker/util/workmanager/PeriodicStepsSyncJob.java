package com.techglock.health.app.fitness_tracker.util.workmanager;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.techglock.health.app.common.constants.PreferenceConstants;
import com.techglock.health.app.fitness_tracker.util.StepCountHelper;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class PeriodicStepsSyncJob extends Job {

    public static final String PERIODIC_STEPS_TAG = "PERIODIC_STEPS";
    public static final String TEST_PERIODIC_STEPS_TAG = "TEST_PERIODIC_STEPS";
    private static final String TAG = "JobProxySteps_" + PeriodicStepsSyncJob.class.getSimpleName();

    public static void schedulePeriodicJobDispatcher() {
        int jobId = new JobRequest.Builder(PERIODIC_STEPS_TAG)
                //.setExact(60 * 1000)
                //.setPeriodic(JobRequest.MIN_INTERVAL)
                .setPeriodic(TimeUnit.HOURS.toMillis(3))
                .setUpdateCurrent(true)
                .build()
                .schedule();

    }

    public static void schedulePeriodicTestJobDispatcher() {
        int jobId = new JobRequest.Builder(TEST_PERIODIC_STEPS_TAG)
                .setPeriodic(JobRequest.MIN_INTERVAL)
                .setUpdateCurrent(true)
                .build()
                .schedule();

    }

    @Override
    @NonNull
    protected Result onRunJob(@NonNull Params params) {
        try {
            if (params.getTag().equals(PERIODIC_STEPS_TAG)) {
                Calendar calendar = Calendar.getInstance();
                int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                //ToDo: Keyur, Need to ensure this job runs during 08:00 PM to 12:00 AM only, it gets called multiple times within a day.
                //ToDo: Keyur, comment below if you want to run every hour.
                //if (hourOfDay == 18 || hourOfDay == 19 || hourOfDay == 20 || hourOfDay == 21 || hourOfDay == 22 || hourOfDay == 23 || hourOfDay == 0) {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("VivantPreferences", Context.MODE_PRIVATE);
                if (sharedPreferences.getBoolean(PreferenceConstants.IS_LOGIN, false)) {
                    StepCountHelper stepCountHelper = new StepCountHelper(getContext());
                    stepCountHelper.getStepsHistoryAndSync();
                    //JobDispatcherStepsHelper.sendDebugNotification(getContext(), "Synchronizing your steps data at " + DateHelper.INSTANCE.dateToString(Calendar.getInstance().getTime(), DateHelper.INSTANCE.getDATETIMEFORMAT()));
                } else {
                    //JobDispatcherStepsHelper.sendDebugNotification(getContext(), "Will retry, not logged in at " + DateHelper.INSTANCE.dateToString(Calendar.getInstance().getTime(), DateHelper.INSTANCE.getDATETIMEFORMAT()));
                }
                //}
            } else if (params.getTag().equals(TEST_PERIODIC_STEPS_TAG)) {
                //FitnessServiceDispatcher.callListStepsServiceAndSynchronize(getContext(), TAG, false);
                //JobDispatcherStepsHelper.sendDebugNotification(getContext(), "Synchronizing your steps data at " + DateHelper.INSTANCE.dateToString(Calendar.getInstance().getTime(), DateHelper.INSTANCE.getDATETIMEFORMAT()));
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }

        return Result.SUCCESS;
    }
}
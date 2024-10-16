package com.techglock.health.app.fitness_tracker.util.workmanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.techglock.health.app.common.utils.Utilities;
import com.evernote.android.job.DailyJob;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.JobUtil;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class DayChangeStepsSyncJob extends DailyJob {

    public static final String DAY_CHANGE_JOB_TAG = "DAY_CHANGE_STEPS_SYNC";
    private static final String TAG = "JobProxySteps_" + DayChangeStepsSyncJob.class.getSimpleName();

    public static void schedule() {

        /*if (!JobManager.instance().getAllJobRequestsForTag(DAY_CHANGE_JOB_TAG).isEmpty()) {
            Utilities.printLog(TAG, "already scheduled : " + DAY_CHANGE_JOB_TAG);
            return; //already scheduled
        }*/
        //DailyJob.startNowOnce(new JobRequest.Builder(DAY_CHANGE_JOB_TAG));

        // schedule between 12 and 12:30 AM
        DailyJob.scheduleAsync(new JobRequest.Builder(DAY_CHANGE_JOB_TAG),
                TimeUnit.HOURS.toMillis(0), TimeUnit.HOURS.toMillis(1), new JobRequest.JobScheduledCallback() {
                    @Override
                    public void onJobScheduled(int jobId, @NonNull String tag, @Nullable Exception exception) {
                        Utilities.INSTANCE.printLog("onJobScheduled at " + JobUtil.timeToString(JobManager.instance().getAllJobRequestsForTag(DAY_CHANGE_JOB_TAG).iterator().next().getStartMs()));
                    }
                });
    }

    @NonNull
    @Override
    protected DailyJobResult onRunDailyJob(@NonNull Params params) {
        try {
            Calendar calendar = Calendar.getInstance();
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            //ToDo: Keyur, Need to ensure this job runs during 12:00 AM only, it gets called multiple times within a day.
            if (hours == 0) {

                //if (SessionManager.IsLoggedIn()) {
                //RealPathUtil realPathUtil = new RealPathUtil(getContext());
                //realPathUtil.writeLogToFile("Log Started -DailyJob- callListStepsServiceAndSynchronize");
                //FitnessServiceDispatcher.callListStepsServiceAndSynchronize(getContext(), TAG, false);
                //JobDispatcherStepsHelper.sendDebugNotification(getContext(), "Daily Job, Synchronizing your steps data midnight." + DateHelper.INSTANCE.dateToString(Calendar.getInstance().getTime(), DateHelper.INSTANCE.getDATETIMEFORMAT()));
            /*} else {
                JobDispatcherStepsHelper.sendDebugNotification(getContext(), "Daily Job, Will retry, user not logged in at " + DateHelper.dateToString(Calendar.getInstance().getTime(), DateHelper.DATETIMEFORMAT));
            }*/

            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return DailyJobResult.SUCCESS;
    }
}

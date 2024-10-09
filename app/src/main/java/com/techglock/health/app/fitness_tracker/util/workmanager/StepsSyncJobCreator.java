package com.techglock.health.app.fitness_tracker.util.workmanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

public class StepsSyncJobCreator implements JobCreator {
    private static StepsSyncJobCreator singleton = null;

    public static StepsSyncJobCreator getInstance() {
        if (singleton == null) {
            singleton = new StepsSyncJobCreator();
        }
        return singleton;
    }

    @Override
    @Nullable
    public Job create(@NonNull String tag) {
        switch (tag) {
            case DayChangeStepsSyncJob.DAY_CHANGE_JOB_TAG:
                return new DayChangeStepsSyncJob();
            case PeriodicStepsSyncJob.PERIODIC_STEPS_TAG:
                return new PeriodicStepsSyncJob();
            default:
                return null;
        }
    }
}

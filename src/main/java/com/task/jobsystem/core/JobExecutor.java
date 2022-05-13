package com.task.jobsystem.core;

import java.util.List;

public interface JobExecutor {

    void executeJob(Job job);
    void executeBatch(List<Job> job);
    void scheduleJob(Job job, long period);
    void disableScheduleJob(Job job);
    boolean cancelJob(Job job);
    List<Job> getRunningJobs();
    int getQueueSize();
    void removeFinishedJobs();

}

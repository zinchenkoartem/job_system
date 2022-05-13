package com.task.jobsystem.core;

import java.util.List;

public interface JobStorage {

    String addJob(Job job);

    Job removeJob(String uuid);

    void clear();

    Job getJob(String uuid);

    List<Job> getAllJobs();

    JobState getJobState(String uuid);

    List<Job> getJobsByState(JobState state);
}

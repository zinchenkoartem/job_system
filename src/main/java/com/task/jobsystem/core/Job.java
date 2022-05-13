package com.task.jobsystem.core;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.UUID;

@Slf4j
public abstract class Job implements Runnable {

    private final String uuid;
    private JobState state;
    private String errDescription = "none";

    public Job() {
        this.uuid = UUID.randomUUID().toString();
        this.state = JobState.CREATED;
        changeJobState(JobState.CREATED);
    }

    public abstract void job() throws Throwable;

    @Override
    public void run() {
        changeJobState(JobState.RUNNING);
        try {
            job();
        } catch (Throwable ex) {
            log.error(uuid + ": Exception: " + ex.getMessage());
            setErrDescription(ex.toString());
            if (!JobState.CANCELED.equals(getJobState())) changeJobState(JobState.FAILED);
            return;
        }
        changeJobState(JobState.FINISHED);
    }

    public JobState getJobState() {
        return state;
    }

    public void changeJobState(JobState state) {
        this.state = state;
        if (!JobState.FAILED.equals(state)) {
            log.info("Job status is changed: " + uuid + ": " + state);
        } else {
            log.error("Job status is changed: " + uuid + ": " + state + ": " + getErrDescription());
        }
    }

    public String getUuid() {
        return uuid;
    }

    public String getErrDescription() {
        return errDescription;
    }

    public void setErrDescription(String err) {
        errDescription = err;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job job = (Job) o;
        return uuid.equals(job.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}

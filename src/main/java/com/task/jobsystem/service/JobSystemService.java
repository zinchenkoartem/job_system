package com.task.jobsystem.service;

import com.task.jobsystem.core.TestJob;
import com.task.jobsystem.core.JobExecutor;
import com.task.jobsystem.core.JobStorage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class JobSystemService {

    private final JobExecutor jobExecutor;

    private final JobStorage jobStorage;

    public JobSystemService(JobExecutor jobExecutor, JobStorage jobStorage) {
        this.jobExecutor = jobExecutor;
        this.jobStorage = jobStorage;
    }

    public void addJobToStorage() {
        IntStream.range(1, 27).boxed().forEach(e -> jobStorage.addJob(new TestJob(e * 2000)));
    }

    public String newJob(Integer ms) {
        return jobStorage.addJob(new TestJob(ms));
    }

    public List<String> getStates() {
        return jobStorage.getAllJobs().stream().map(e -> e.getUuid() + " - " + e.getJobState()).collect(Collectors.toList());

    }

    public void runAll() {
        jobExecutor.executeBatch(jobStorage.getAllJobs());
    }

    public int getRunningJobCount() {
        return jobExecutor.getRunningJobs().size();
    }

    public boolean cancelJob(String id) {
        return jobExecutor.cancelJob(jobStorage.getJob(id));
    }

    public int getQueueSize() {
        return jobExecutor.getQueueSize();
    }

    public void scheduleJob(String jobId, long period) {
        jobExecutor.scheduleJob(jobStorage.getJob(jobId), period);
    }

    public void disableScheduleJob(String jobId) {
        jobExecutor.disableScheduleJob(jobStorage.getJob(jobId));
    }

}

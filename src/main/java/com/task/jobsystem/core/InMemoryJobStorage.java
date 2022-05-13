package com.task.jobsystem.core;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class InMemoryJobStorage implements JobStorage {

    private static final ConcurrentHashMap<String, Job> JOB_STORE = new ConcurrentHashMap<>();


    @Override
    public String addJob(Job job) {
        JOB_STORE.put(job.getUuid(), job);
        return job.getUuid();
    }

    @Override
    public Job removeJob(String uuid) {
        return JOB_STORE.remove(uuid);
    }

    @Override
    public Job getJob(String uuid) {
        return JOB_STORE.get(uuid);
    }

    @Override
    public List<Job> getAllJobs() {
        return new ArrayList<>(JOB_STORE.values());
    }

    @Override
    public JobState getJobState(String uuid) {
        return JOB_STORE.get(uuid).getJobState();
    }

    @Override
    public List<Job> getJobsByState(JobState state) {
        return JOB_STORE.values().stream()
                .filter(job -> state.equals(job.getJobState()))
                .collect(Collectors.toList());
    }

    @Override
    public void clear() {
        JOB_STORE.clear();
    }
}

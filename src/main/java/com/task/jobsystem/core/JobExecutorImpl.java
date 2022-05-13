package com.task.jobsystem.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;


@Service
@Slf4j
public class JobExecutorImpl implements JobExecutor {

    private final ExecutorService executorService;
    private final ConcurrentHashMap<Job, Future<?>> runningJobs = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Job, Timer> scheduledJobs = new ConcurrentHashMap<>();

    public JobExecutorImpl(ExecutorService executorService) {
        this.executorService = executorService;
        scheduleRemoveFinishedJobTask();
    }

    @Override
    public void executeJob(Job job) {
        if (!runningJobs.contains(job)) {
            job.changeJobState(JobState.PENDING);
            Future<?> future = executorService.submit(job);
            runningJobs.put(job, future);
        }
    }

    @Override
    public void executeBatch(List<Job> job) {
        job.forEach(this::executeJob);
    }

    @Override
    public void scheduleJob(Job job, long period) {
        job.changeJobState(JobState.SCHEDULED);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new ScheduleJobTask(this, job), period, period);
        scheduledJobs.put(job, timer);
    }

    @Override
    public void disableScheduleJob(Job job) {
        Timer timer = scheduledJobs.remove(job);
        if (timer != null) {
            timer.cancel();
            job.changeJobState(JobState.CANCELED);
        }
    }

    @Override
    public boolean cancelJob(Job job) {
        boolean cancel = runningJobs.get(job).cancel(true);
        if (cancel) {
            runningJobs.remove(job);
            job.changeJobState(JobState.CANCELED);
            return true;
        }
        return false;
    }

    @Override
    public List<Job> getRunningJobs() {
        return runningJobs.keySet().stream().filter(e -> JobState.RUNNING.equals(e.getJobState())).collect(Collectors.toList());
    }

    @Override
    public int getQueueSize() {
        return runningJobs.size();
    }

    public void removeFinishedJobs() {
        List<Job> toDelete = runningJobs.keySet().stream()
                .filter(e -> JobState.FINISHED.equals(e.getJobState())
                        || JobState.CANCELED.equals(e.getJobState())
                        || JobState.FAILED.equals(e.getJobState()))
                .collect(Collectors.toList());
        toDelete.forEach(runningJobs::remove);
    }

    private void scheduleRemoveFinishedJobTask() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new RemoveFinishedJobTask(this), 30000, 30000);
    }

    private static class RemoveFinishedJobTask extends TimerTask {
        private final JobExecutor jobExecutor;

        public RemoveFinishedJobTask(JobExecutor executor) {
            this.jobExecutor = executor;
        }

        @Override
        public void run() {
            jobExecutor.removeFinishedJobs();
        }
    }

    private static class ScheduleJobTask extends TimerTask {
        private final JobExecutor jobExecutor;
        private final Job job;

        public ScheduleJobTask(JobExecutor executor, Job job) {
            this.jobExecutor = executor;
            this.job = job;
        }

        @Override
        public void run() {
            jobExecutor.executeJob(job);
        }
    }
}

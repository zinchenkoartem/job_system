package com.task.jobsystem;

import com.task.jobsystem.core.InMemoryJobStorage;
import com.task.jobsystem.core.JobExecutor;
import com.task.jobsystem.core.JobExecutorImpl;
import com.task.jobsystem.core.JobState;
import com.task.jobsystem.core.JobStorage;
import com.task.jobsystem.core.TestJob;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class JobSystemApplicationTests {

    static private JobExecutor jobExecutor;
    static private JobStorage jobStorage;

    @BeforeAll
    static void before() {
        jobExecutor = new JobExecutorImpl(Executors.newFixedThreadPool(5));
        jobStorage = new InMemoryJobStorage();
    }

    @AfterEach
    void after() {
        jobExecutor.getRunningJobs().forEach(e -> jobExecutor.cancelJob(e));
        jobExecutor.removeFinishedJobs();
        jobStorage.clear();
    }

    @Test
    void main_executing() {
        TestJob job1 = new TestJob(1000);
        TestJob job2 = new TestJob(2000);
        assertEquals(JobState.CREATED, job1.getJobState());

        jobStorage.addJob(job1);
        assertEquals(1, jobStorage.getAllJobs().size());
        jobStorage.addJob(job2);
        jobStorage.addJob(job1);
        sleep(100);
        assertEquals(2, jobStorage.getAllJobs().size());
        jobExecutor.executeJob(job1);
        jobExecutor.executeJob(job2);
        sleep(100);
        assertEquals(JobState.RUNNING, job1.getJobState());
        assertEquals(JobState.RUNNING, job2.getJobState());

        sleep(3000);
        assertEquals(JobState.FINISHED, job1.getJobState());
        assertEquals(JobState.FINISHED, job2.getJobState());
    }

    @Test
    void canceling() {
        TestJob job1 = new TestJob(1000);
        TestJob job2 = new TestJob(2000);
        jobExecutor.executeJob(job1);
        jobExecutor.executeJob(job2);
        sleep(100);
        assertEquals(JobState.RUNNING, job1.getJobState());
        assertEquals(JobState.RUNNING, job2.getJobState());
        assertEquals(2, jobExecutor.getRunningJobs().size());
        sleep(200);
        jobExecutor.cancelJob(job2);
        assertEquals(1, jobExecutor.getRunningJobs().size());
        assertEquals(JobState.RUNNING, job1.getJobState());
        assertEquals(JobState.CANCELED, job2.getJobState());
    }

    @Test
    void scheduling() {
        TestJob job1 = new TestJob(1000);
        jobExecutor.scheduleJob(job1, 2000);
        assertEquals(JobState.SCHEDULED, job1.getJobState());
        sleep(2100);
        assertEquals(JobState.RUNNING, job1.getJobState());
        sleep(1100);
        assertEquals(JobState.FINISHED, job1.getJobState());
        sleep(1100);
        assertEquals(JobState.RUNNING, job1.getJobState());
        sleep(1100);
        assertEquals(JobState.FINISHED, job1.getJobState());
        jobExecutor.disableScheduleJob(job1);
        assertEquals(JobState.CANCELED, job1.getJobState());
    }

    @Test
    void no_run_for_same_job() {
        TestJob job1 = new TestJob(1000);
        jobExecutor.executeJob(job1);
        jobExecutor.executeJob(job1);
        sleep(100);
        assertEquals(1, jobExecutor.getRunningJobs().size());
        sleep(100);
        assertEquals(JobState.RUNNING, job1.getJobState());
    }

    @Test
    void pool() {
        IntStream.range(1, 8).boxed().forEach(e -> {
            jobStorage.addJob(new TestJob(1000));
        });
        assertEquals(7, jobStorage.getAllJobs().size());
        jobExecutor.executeBatch(jobStorage.getAllJobs());
        sleep(100);
        assertEquals(5, jobExecutor.getRunningJobs().size());
        assertEquals(5, jobStorage.getJobsByState(JobState.RUNNING).size());
        assertEquals(2, jobStorage.getJobsByState(JobState.PENDING).size());
        sleep(1100);
        assertEquals(2, jobExecutor.getRunningJobs().size());
        assertEquals(0, jobStorage.getJobsByState(JobState.PENDING).size());
        sleep(2100);
        assertEquals(0, jobExecutor.getRunningJobs().size());
        assertEquals(7, jobStorage.getJobsByState(JobState.FINISHED).size());
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

package com.task.jobsystem.core;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestJob extends Job {

    private Integer timeLag;

    public TestJob(Integer timeLag) {
        this.timeLag = timeLag;
    }

    @Override
    public void job() throws Throwable {
        log.info("JobId:" + this.getUuid() + " -> " + " -> working...");
        Thread.sleep(timeLag);
        log.info("JobId:" + this.getUuid() + " -> " + " -> time's up");
    }

}

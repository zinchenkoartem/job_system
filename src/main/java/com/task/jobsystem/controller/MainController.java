package com.task.jobsystem.controller;

import com.task.jobsystem.service.JobSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MainController {
    @Autowired
    private JobSystemService jobSystemService;

    @GetMapping("/run")
    public void run() {
        jobSystemService.runAll();
    }
    @GetMapping("/getAll")
    public List<String> getAll() {
        return jobSystemService.getStates();

    }    @GetMapping("/getQueueSize")
    public Integer getQueueSize() {
        return jobSystemService.getQueueSize();
    }
    @GetMapping("/addjobs")
    public void addjobs() {
        jobSystemService.addJobToStorage();
    }
    @GetMapping("/new/{ms}")
    public String cancel(@PathVariable Integer ms) {
        return jobSystemService.newJob(ms);
    }
    @GetMapping("/cancel/{jobId}")
    public Boolean cancel(@PathVariable String jobId) {
        return jobSystemService.cancelJob(jobId);
    }
    @GetMapping("/schedule/{jobId}/{period}")
    public void schedule(@PathVariable String jobId, @PathVariable Long period) {
        jobSystemService.scheduleJob(jobId, period);
    }
    @GetMapping("/disableSchedule/{jobId}")
    public void disableSchedule(@PathVariable String jobId) {
        jobSystemService.disableScheduleJob(jobId);
    }
}

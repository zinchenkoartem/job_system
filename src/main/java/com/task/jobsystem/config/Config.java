package com.task.jobsystem.config;

import com.task.jobsystem.core.JobExecutor;
import com.task.jobsystem.core.JobExecutorImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class Config {

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(10);
    }

    @Bean
    public JobExecutor jobExecutor() {
        return new JobExecutorImpl(executorService());
    }
}

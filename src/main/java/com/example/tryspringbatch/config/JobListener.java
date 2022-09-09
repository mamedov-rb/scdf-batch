package com.example.tryspringbatch.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JobListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Callback before job");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("Callback after job");
    }
}

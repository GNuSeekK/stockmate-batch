package com.stockmate.batch.config;


import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class MyJobExecutionListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(MyJobExecutionListener.class);

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Job started at {}", LocalDateTime.now());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("Job finished at {}", LocalDateTime.now());
    }

}

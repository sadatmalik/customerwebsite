package com.sadatmalik.customerwebsite.services;

import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
public class BatchLookup {

    private static JobExplorer jobExplorer;
    private static RunIdIncrementer runIdIncrementer;

    @Autowired
    public BatchLookup(JobExplorer jobExplorer, RunIdIncrementer runIdIncrementer) {
        this.jobExplorer = jobExplorer;
        this.runIdIncrementer = runIdIncrementer;
    }

    public static List<JobInstance> getJobsFor(Job job) {
        return jobExplorer.getJobInstances(job.getName(), 0, 3);
    }

    public static JobInstance getJobByExecId(Long execId) {
        return jobExplorer.getJobExecution(execId).getJobInstance();
    }

    public static List<JobExecution> getExecutions(JobInstance job) {
        return jobExplorer.getJobExecutions(Objects.requireNonNull(job));
    }

    public static List<JobExecution> getExecutions(Long jobId) {
        JobInstance job = jobExplorer.getJobInstance(jobId);
        return jobExplorer.getJobExecutions(Objects.requireNonNull(job));
    }

    public static Collection<StepExecution> getSteps(JobExecution exec) {
        return Objects.requireNonNull(exec.getStepExecutions());
    }

    public static Collection<StepExecution> getSteps(Long execId) {
        return Objects.requireNonNull(jobExplorer.getJobExecution(execId).getStepExecutions());
    }

    public static JobParameters getLastParams(Job job) {
        JobInstance jobInstance = jobExplorer.getLastJobInstance(job.getName());
        if (jobInstance != null) {
            return jobExplorer.getLastJobExecution(jobInstance).getJobParameters();
        }
        return null;
    }

    public static JobParameters getNextParams(Job job) {
        JobParameters lastParams = getLastParams(job);
        if (lastParams != null) {
            return runIdIncrementer.getNext(lastParams);
        }

        return new JobParameters();
    }

    public static JobParameters getJobParams(Long jobId) {
        return getLastExecution(jobId).getJobParameters();
    }

    public static JobExecution getLastExecution(Long jobId) {
        JobInstance job = jobExplorer.getJobInstance(jobId);
        return jobExplorer.getLastJobExecution(job);
    }
}

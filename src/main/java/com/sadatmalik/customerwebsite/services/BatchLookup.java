package com.sadatmalik.customerwebsite.services;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
public class BatchLookup {

    private static JobExplorer jobExplorer;

    @Autowired
    public BatchLookup(JobExplorer jobExplorer) {
        this.jobExplorer = jobExplorer;
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

}

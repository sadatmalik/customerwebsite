package com.sadatmalik.customerwebsite.controllers;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/batch")
public class BatchController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobRegistry jobRegistry;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private Job eodCustomerBalances;

    @Autowired
    private RunIdIncrementer runIdIncrementer;

    @Bean
    public RunIdIncrementer runIdIncrementer() {
        return new RunIdIncrementer();
    }

    @GetMapping({"", "/"})
    public String showBatchDashboard(Model model) {
        // just have some button on the top for start new EOD job
        // and a table for the jobinstances; corresponding jobexecution; corresponding steps
        // start with one table for job instances and build upon that
        List<JobInstance> jobs = jobExplorer.getJobInstances(eodCustomerBalances.getName(), 0, 10);
        model.addAttribute("jobs", jobs);

        Map<JobInstance, List<JobExecution>> executionsByJob = new HashMap<>();
        for (JobInstance job : jobs) {
            List<JobExecution> execs = jobExplorer.getJobExecutions(job);
            executionsByJob.put(job, execs);
        }
        model.addAttribute("execsByJob", executionsByJob);

        return "batch-dashboard";
    }

    @GetMapping("/run/eod")
    public String executeJob(Model model) {

        try {
            Job job = eodCustomerBalances;
            JobParameters lastParams =
                    jobExplorer.getLastJobExecution(jobExplorer.getLastJobInstance(job.getName()))
                            .getJobParameters();
            JobParameters jobParams = runIdIncrementer.getNext(lastParams);
            JobExecution jobExecution = jobLauncher.run(job, jobParams);

            return showBatchDashboard(model);

        } catch (JobInstanceAlreadyCompleteException e) {
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (JobExecutionAlreadyRunningException e) {
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (JobParametersInvalidException e) {
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (JobRestartException e) {
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            return "error";
        }

    }

    @GetMapping(value = "/eod")
    public String testJob(@RequestParam(name = "id") String jobId) {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        if (StringUtils.hasLength(jobId)) {
            jobParametersBuilder.addString("jobId", jobId);
        }
        JobExecution jobExecution;
        try {
            jobExecution = jobLauncher.run(eodCustomerBalances, jobParametersBuilder.toJobParameters());
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            e.printStackTrace();
            // return exception message
            return e.getMessage();
        }
        // return job execution status
        return jobExecution.getStatus().name();
    }
}
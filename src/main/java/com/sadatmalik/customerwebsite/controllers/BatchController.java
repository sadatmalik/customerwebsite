package com.sadatmalik.customerwebsite.controllers;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/batch")
public class BatchController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobRegistry jobRegistry;

    @Autowired
    private Job eodCustomerBalances;

    @GetMapping({"", "/"})
    public String showBatchDashboard(Model model) {
        List<Job> jobs = Collections.singletonList(eodCustomerBalances);
        model.addAttribute("jobs", jobs);
        return "batch-dashboard";
    }

    @GetMapping("/run/{job_name}")
    public String executeJob(@PathVariable(name = "job_name") String jobName, Model model) {

        try {
            // TODO: 16/11/2021 - how to lookup jobs from the registry/explorer?
            //Job job = jobRegistry.getJob(jobName);
            Job job = eodCustomerBalances;
            JobParameters jobParams = job.getJobParametersIncrementer().getNext(null);
            JobExecution jobExecution = jobLauncher.run(job, jobParams);

            return "batch-dashboard";
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
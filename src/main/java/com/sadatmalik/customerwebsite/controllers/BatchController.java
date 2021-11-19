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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;

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
        // start with populating table for job instances and build upon that
        List<JobInstance> jobs = jobExplorer.getJobInstances(eodCustomerBalances.getName(), 0, 3);
        model.addAttribute("jobs", jobs);
        return "batch-dashboard";
    }

    @GetMapping("/execs/{job_id}")
    public String showBatchDashboardWithExecs(@PathVariable(name = "job_id") Long jobId,
                                       Model model) {
        List<JobInstance> jobs = jobExplorer.getJobInstances(eodCustomerBalances.getName(), 0, 3);
        model.addAttribute("jobs", jobs);

        List<JobExecution> executions = jobExplorer.getJobExecutions(jobExplorer.getJobInstance(jobId));
        model.addAttribute("execs", executions);

        return "batch-dashboard";
    }

    @GetMapping("/steps/{exec_id}")
    public String showBatchDashboardWithExecsAndSteps(@PathVariable(name = "exec_id") Long execId,
                                              Model model) {

        List<JobInstance> jobs = jobExplorer.getJobInstances(eodCustomerBalances.getName(), 0, 3);
        model.addAttribute("jobs", jobs);

        JobInstance job = jobExplorer.getJobExecution(execId).getJobInstance();

        List<JobExecution> executions = jobExplorer.getJobExecutions(job);
        model.addAttribute("execs", executions);

        Collection<StepExecution> steps = jobExplorer.getJobExecution(execId).getStepExecutions();
        model.addAttribute("steps", steps);

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
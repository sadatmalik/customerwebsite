package com.sadatmalik.customerwebsite.controllers;

import com.sadatmalik.customerwebsite.services.BatchLookup;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.*;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
@RequestMapping("/batch")
public class BatchController {

    @Autowired
    @Qualifier("asyncJobLauncher")
    private JobLauncher jobLauncher;

    @Autowired
    private JobOperator jobOperator;

    @Autowired
    private Job eodCustomerBalances;

    @GetMapping({"", "/"})
    public String showBatchDashboard(Model model) {
        // start with populating table for job instances and build upon that
        List<JobInstance> jobs = BatchLookup.getJobsFor(eodCustomerBalances);
        model.addAttribute("jobs", jobs);

        // default return execs for first job
        List<JobExecution> executions = null;
        if ( jobs != null && jobs.size() >= 1 ) {
            executions = BatchLookup.getExecutions(jobs.get(0));
        }
        model.addAttribute("execs", executions);

        // default return steps for first exec
        Collection<StepExecution> steps = null;
        if ( executions != null && executions.size() != 1 ) {
            steps = BatchLookup.getSteps(executions.get(0));
        }
        model.addAttribute("steps", steps);

        return "batch-dashboard";
    }

    @GetMapping("/execs/{job_id}")
    public String showBatchDashboardWithExecs(@PathVariable(name = "job_id") Long jobId,
                                       Model model) {
        model.addAttribute("jobs", BatchLookup.getJobsFor(eodCustomerBalances));

        List<JobExecution> executions = BatchLookup.getExecutions(jobId);
        model.addAttribute("execs", executions);

        // default to steps for first execution
        Collection<StepExecution> steps = BatchLookup.getSteps(executions.get(0));
        model.addAttribute("steps", steps);

        return "batch-dashboard";
    }

    @GetMapping("/steps/{exec_id}")
    public String showBatchDashboardWithExecsAndSteps(@PathVariable(name = "exec_id") Long execId,
                                              Model model) {
        model.addAttribute("jobs", BatchLookup.getJobsFor(eodCustomerBalances));

        JobInstance job = BatchLookup.getJobByExecId(execId);
        List<JobExecution> executions = BatchLookup.getExecutions(job);
        model.addAttribute("execs", executions);

        Collection<StepExecution> steps = BatchLookup.getSteps(execId);
        model.addAttribute("steps", steps);

        return "batch-dashboard";
    }

    @GetMapping({"/run/eod", "/run/eod/{job_id}"})
    public String executeJob(Model model, @PathVariable(name = "job_id", required = false) String jobId) {

        try {
            JobParameters jobParams;

            //restart existing job
            if (StringUtils.hasLength(jobId)) {
                JobExecution lastExec = BatchLookup.getLastExecution(Long.parseLong(jobId));
                // stop previous execution if still running
                if (lastExec.getExitStatus().getExitCode().startsWith("UNKNOWN")) {
                    for (StepExecution step : lastExec.getStepExecutions()) {
                        step.setStatus(BatchStatus.STOPPED);
                        step.setExitStatus(ExitStatus.STOPPED);
                    }
                    lastExec.setStatus(BatchStatus.STOPPED);
                    lastExec.setExitStatus(ExitStatus.STOPPED);
                    lastExec.setEndTime(new Date());
                }
                jobParams = BatchLookup.getJobParams(Long.parseLong(jobId));
            }

            // start new job instance
            else {
                jobParams = BatchLookup.getNextParams(eodCustomerBalances);
            }

            jobLauncher.run(eodCustomerBalances, jobParams);

            return showBatchDashboard(model);

        } catch (JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException |
                JobParametersInvalidException | JobRestartException e) {
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/stop/all")
    public String stopRunningExecutions(Model model) {
        try {
            Set<Long> executions = jobOperator.getRunningExecutions(eodCustomerBalances.getName());
            jobOperator.stop(executions.iterator().next());
        } catch (NoSuchJobException | NoSuchJobExecutionException | JobExecutionNotRunningException e) {
            e.printStackTrace();
        }

        return showBatchDashboard(model);
    }

}
package com.serba.controller;

import com.serba.domain.jobs.Job;
import com.serba.service.JobService;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import lombok.RequiredArgsConstructor;

@Controller("jobs")
@RequiredArgsConstructor
public class JobController {
  private final JobService jobService;

  @Get("{jobId}")
  Job getJobById(@PathVariable String jobId) {
    return jobService.findById(jobId);
  }
}

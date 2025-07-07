package com.serba.service;

import java.util.List;

import com.serba.domain.jobs.Job;
import com.serba.domain.jobs.JobType;
import com.serba.repository.JobRepository;
import jakarta.annotation.Nullable;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor
public class JobService {
  private final JobRepository jobRepository;

  public Job save(Job job) {
    return jobRepository.save(job);
  }

  public Job findById(String uuid) {
    return jobRepository.findById(uuid);
  }

  public List<Job> findByUserIdAndJobType(Long userId, @Nullable JobType jobType) {
    return jobRepository.findByUserIdAndJobType(userId, jobType);
  }

  public List<Job> findByAttrNameValue(String name, Object value) {
    return jobRepository.findByAttrNameValue(name, value);
  }

  public void deletebyId(String uuid) {
    jobRepository.deleteById(uuid);
  }
}

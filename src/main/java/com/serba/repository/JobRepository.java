package com.serba.repository;

import java.util.List;

import com.serba.domain.jobs.Job;
import com.serba.domain.jobs.JobType;

import jakarta.annotation.Nullable;

public interface JobRepository {
  Job save(Job job);
  Job findById(String uuid);
  List<Job> findByUserIdAndJobType(Long userId, @Nullable JobType jobType);
  List<Job> findByAttrNameValue(String name, Object value);
  void deleteById(String uuid);
}

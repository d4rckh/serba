package com.serba.repository;

import com.serba.domain.jobs.Job;
import com.serba.domain.jobs.JobType;
import jakarta.annotation.Nullable;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class InMemoryJobRepository implements JobRepository {
  private final Map<String, Job> jobs = new ConcurrentHashMap<>();

  @Override
  public Job save(Job job) {
    if (job.getId() == null) {
      job.setId(UUID.randomUUID().toString());
    }
    jobs.put(job.getId(), job);
    return job;
  }

  @Override
  public Job findById(String uuid) {
    return jobs.get(uuid);
  }

  @Override
  public List<Job> findByUserIdAndJobType(Long userId, @Nullable JobType jobType) {
    return jobs.values().stream()
        .filter(job -> job.getOwnedByUserIds().contains(userId))
        .filter(job -> jobType == null || job.getType() == jobType)
        .toList();
  }

  @Override
  public List<Job> findByAttrNameValue(String name, Object value) {
    return jobs.values().stream()
        .filter(job -> job.getAttrs().containsKey(name))
        .filter(job -> job.getAttrs().get(name).equals(value))
        .toList();
  }

  @Override
  public void deleteById(String uuid) {
    jobs.remove(uuid);
  }
}

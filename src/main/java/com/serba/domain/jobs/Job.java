package com.serba.domain.jobs;

import java.util.List;
import java.util.Map;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;

@Data
@Serdeable
public class Job {
  private String id;
  private JobType type;
  private List<Long> ownedByUserIds;
  private int progress;
  private Map<String, Object> attrs;
}
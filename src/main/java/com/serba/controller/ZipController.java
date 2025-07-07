package com.serba.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.serba.domain.downloads.FileDownload;
import com.serba.domain.jobs.Job;
import com.serba.domain.jobs.JobType;
import com.serba.domain.zip.ZipFileRequest;
import com.serba.entity.LibraryEntity;
import com.serba.entity.UserEntity;
import com.serba.service.DownloadService;
import com.serba.service.JobService;
import com.serba.service.LibraryService;
import com.serba.service.UserService;
import com.serba.service.ZipService;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.server.types.files.StreamedFile;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import lombok.RequiredArgsConstructor;

@Controller("zip")
@RequiredArgsConstructor
public class ZipController {
  private final DownloadService downloadService;
  private final UserService userService;
  private final ZipService zipService;
  private final LibraryService libraryService;
  private final JobService jobService;

  @Get("/job/{id}/download")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  HttpResponse<StreamedFile> downloadFile(
      @PathVariable String id,
      Authentication authentication)
      throws IOException {
    UserEntity user = userService.findById((Long) authentication.getAttributes().get("UID"));
    FileDownload fileDownload = downloadService.downloadZippedJob(user, id);
    InputStream stream = fileDownload.getStream();
    String filename = fileDownload.getFilename();

    return HttpResponse.ok(
        new StreamedFile(stream, MediaType.APPLICATION_OCTET_STREAM_TYPE).attach(filename));
  }

  @Get("/job")
  @Secured(SecurityRule.IS_AUTHENTICATED)
  public List<Job> listUserJobs(Authentication authentication) {
    Long userId = (Long) authentication.getAttributes().get("UID");
    return jobService.findByUserIdAndJobType(userId, JobType.ZIP);
  }

  @Post
  @Secured(SecurityRule.IS_AUTHENTICATED)
  Job zipFile(
      @Body ZipFileRequest zipFileRequest,
      Authentication authentication)
      throws IOException {

    LibraryEntity libraryEntity = libraryService.findById(zipFileRequest.getLibraryId()).orElseThrow();
    Job zipJob = zipService.createZipJob(
        libraryEntity,
        zipFileRequest.getLibraryPath(),
        (Long) authentication.getAttributes().get("UID"));

    return zipJob;
  }

}

package com.serba.service;

import com.serba.entity.UserLibraryAccessEntity;
import com.serba.repository.UserLibraryAccessRepository;
import jakarta.inject.Singleton;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor
public class UserLibraryAccessService {
  private final UserLibraryAccessRepository userLibraryAccessRepository;
  private final LibraryService libraryService;
  private final UserService userService;

  private UserLibraryAccessEntity findByUserIdAndLibraryId(Long userId, Long libraryId) {
    return this.userLibraryAccessRepository
        .findByUserIdAndLibraryId(userId, libraryId)
        .orElseGet(
            () -> {
              UserLibraryAccessEntity access = new UserLibraryAccessEntity();
              access.setUser(this.userService.findById(userId));
              access.setLibrary(this.libraryService.findById(libraryId).orElseThrow());
              access.setViewLibrary(false);
              return this.userLibraryAccessRepository.save(access);
            });
  }

  public boolean hasViewAccess(Long userId, Long libraryId) {
    return this.findByUserIdAndLibraryId(userId, libraryId).isViewLibrary();
  }

  public List<UserLibraryAccessEntity> findByLibraryId(Long libraryId) {
    return this.userLibraryAccessRepository.findByLibraryId(libraryId);
  }

  public List<UserLibraryAccessEntity> findByUserId(Long userId) {
    return this.libraryService.findAll().stream()
        .map(library -> findByUserIdAndLibraryId(userId, library.getId()))
        .toList();
  }

  public UserLibraryAccessEntity updateUserLibraryAccess(
      UserLibraryAccessEntity userLibraryAccess) {
    UserLibraryAccessEntity existing =
        this.findByUserIdAndLibraryId(
            userLibraryAccess.getUser().getId(), userLibraryAccess.getLibrary().getId());

    existing.setViewLibrary(userLibraryAccess.isViewLibrary());

    return this.userLibraryAccessRepository.update(existing);
  }
}

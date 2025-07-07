package com.serba.service;

import com.serba.domain.CreateUserRequest;
import com.serba.entity.UserEntity;
import com.serba.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;

@Singleton
@RequiredArgsConstructor
@Slf4j
public class UserService {
  private final UserRepository userRepository;

  @PostConstruct
  void init() {
    if (this.findAll().isEmpty()) {
      UserEntity admin = new UserEntity();
      admin.setUsername("admin");
      admin.setSuperUser(true);
      String password = UUID.randomUUID().toString();
      admin.setHashedPassword(hashPassword(password));
      log.info("Creating default admin user with password: {}", password);
      userRepository.save(admin);
    }
  }

  private String hashPassword(String password) {
    return BCrypt.hashpw(password, BCrypt.gensalt());
  }

  private boolean checkPassword(String password, String hashedPassword) {
    return BCrypt.checkpw(password, hashedPassword);
  }

  public UserEntity createUser(CreateUserRequest createUserRequest) {
    UserEntity user = new UserEntity();

    user.setUsername(createUserRequest.getUsername());
    user.setHashedPassword(hashPassword(createUserRequest.getPassword()));
    user.setSuperUser(false);

    return userRepository.save(user);
  }

  public Optional<UserEntity> findByUsernameAndPassword(String username, String password) {
    Optional<UserEntity> user = this.userRepository.findByUsername(username);

    if (user.isEmpty() || !checkPassword(password, user.get().getHashedPassword()))
      return Optional.empty();

    return user;
  }

  public UserEntity findByUsername(String username) {
    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new RuntimeException("User not found"));
  }

  public UserEntity findById(Long id) {
    return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
  }

  public List<UserEntity> findAll() {
    return userRepository.findAll();
  }

  public void deleteUser(UserEntity user) {
    userRepository.delete(user);
  }

  public UserEntity updatePassword(CreateUserRequest request) {
    UserEntity user = findByUsername(request.getUsername());
    user.setHashedPassword(hashPassword(request.getPassword()));
    return userRepository.update(user);
  }
}

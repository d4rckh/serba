package com.serba.service;

import java.util.List;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;

import com.serba.domain.CreateUserRequest;
import com.serba.entity.UserEntity;
import com.serba.repository.UserRepository;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Singleton
@RequiredArgsConstructor
@Slf4j
public class UserService {

  private final UserRepository userRepository;

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

  public UserEntity findByUsernameAndPassword(String username, String password) {
    UserEntity user = userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("User not found"));

    if (!checkPassword(password, user.getHashedPassword()))
      throw new RuntimeException("Invalid password");

    return user;
  }

  public UserEntity findByUsername(String username) {
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("User not found"));
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
}

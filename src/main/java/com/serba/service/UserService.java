package com.serba.service;

import org.mindrot.jbcrypt.BCrypt;

import com.serba.domain.CreateUserRequest;
import com.serba.entity.UserEntity;
import com.serba.repository.UserRepository;

import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor
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

}

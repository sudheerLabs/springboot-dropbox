package com.cmpe273.dropbox.repository;

import com.cmpe273.dropbox.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository

public interface UserRepository extends CrudRepository<User, Integer> {
    List<User> findByUsernameAndPassword(String username,String password);
}

package com.cmpe273.dropbox.repository;

import com.cmpe273.dropbox.entity.UserFile;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserFileRepository extends CrudRepository<UserFile, Integer> {
    List<UserFile> findByPath(String author);
}

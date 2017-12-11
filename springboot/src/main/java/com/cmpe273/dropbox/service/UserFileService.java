package com.cmpe273.dropbox.service;

import com.cmpe273.dropbox.entity.UserFile;
import com.cmpe273.dropbox.repository.UserFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserFileService {

    @Autowired
    private UserFileRepository userFileRepository;

    public void addFile(UserFile file) {
        userFileRepository.save(file);
    }

    public Iterable<UserFile> getAllFiles() {
        return userFileRepository.findAll();
    }

    public List<UserFile> getFiles(String dir) {
        return userFileRepository.findByPath(dir);
    }

    public UserFile toggleStar(Integer fileId) {
        UserFile file = userFileRepository.findOne(fileId);
        //char s = file.getStarred() == 'Y'? 'N':'Y';
        file.setStarred(file.getStarred() == 'Y' ? 'N' : 'Y');

        userFileRepository.save(file);

        return file;
    }

    public UserFile deleteFile(Integer fileId) {
        UserFile file = userFileRepository.findOne(fileId);
        //char s = file.getStarred() == 'Y'? 'N':'Y';
        file.setDeleted('Y');

        userFileRepository.save(file);

        return file;
    }
/*
    public UserFile addFolder(String path, String fileName, String name){
        UserFile folder = new UserFile();
        folder.setAuthor(name);
        folder.setType("folder");
        folder.setPath(path);
        folder.setFilename(fileName);
        folder.setDeleted('N');
        folder.setStarred('N');
        file.setShared('N');

        userFileRepository.save(folder);

        return folder;
    }
    */

    public UserFile addFile(String path, String fileName, String name, String type, char shared) {
        UserFile file = new UserFile();
        file.setAuthor(name);
        file.setType(type);
        file.setPath(path);
        file.setFilename(fileName);
        file.setDeleted('N');
        file.setStarred('N');
        file.setShared(shared);

        userFileRepository.save(file);

        return file;
    }
}
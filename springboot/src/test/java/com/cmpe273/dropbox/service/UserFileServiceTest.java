package com.cmpe273.dropbox;


import com.cmpe273.dropbox.repository.UserFileRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;


import com.cmpe273.dropbox.entity.UserFile;
import com.cmpe273.dropbox.service.UserFileService;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
public class UserFileServiceTest {

    @TestConfiguration
    public static class UserFileServiceTestContextConfig {
        @Bean
        public UserFileService fileService() {
            return new UserFileService();
        }
    }

    @Autowired
    private UserFileService fileService;

    @MockBean
    private UserFileRepository fileRepository;


    @Before
    public void setUp() throws Exception {
        UserFile file = new UserFile();
        file.setAuthor("steve@com.au");
        file.setType("file");
        file.setPath(",root,steve@com.au,");
        file.setFilename("ashes.txt");
        file.setDeleted('N');
        file.setStarred('N');
        file.setShared('N');
        file.setFileId(1);

        List<UserFile> files = new ArrayList<>();
        files.add(file);

        Mockito.when(fileRepository.findByPath(",root,steve@com.au,"))
                .thenReturn(files);
        Mockito.when(fileRepository.findOne(1))
                .thenReturn(file);
    }

    @Test
    public void addFile() throws Exception {
        UserFile file = fileService.addFile(",root,steve@com.au,", "bigbash", "steve@com.au", "file",'N');
        assertThat(file).hasFieldOrPropertyWithValue("filename","bigbash");
    }

    @Test
    public void getFiles() throws Exception {
        List<UserFile> list =  fileService.getFiles(",root,steve@com.au,");
        assertThat(list).isNotEmpty();
        assertThat(list.size()).isEqualTo(1);
    }

    @Test
    public void toggleStar() throws Exception {
        UserFile file = fileService.toggleStar(1);
        assertThat(file.getStarred()).isEqualTo('Y');
    }

    @Test
    public void deleteFile() throws Exception {
        UserFile file = fileService.deleteFile(1);
        assertThat(file.getDeleted()).isEqualTo('Y');
    }

    @Test
    public void addFolder() throws Exception {
        UserFile file = fileService.addFile(",root,steve@com.au,", "cal2016", "steve@com.au", "folder",'N');
        assertThat(file).hasFieldOrPropertyWithValue("filename","cal2016");
    }

    @Test
    public void addGroup() throws Exception {
        UserFile file = fileService.addFile(",root,steve@com.au,", "teamaussies", "steve@com.au", "group",'N');
        assertThat(file).hasFieldOrPropertyWithValue("filename","teamaussies");
    }
}

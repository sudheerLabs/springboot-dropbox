package com.cmpe273.dropbox;


import com.cmpe273.dropbox.repository.UserRepository;
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


import com.cmpe273.dropbox.entity.User;
import com.cmpe273.dropbox.service.UserService;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
public class UserControllerTest {

    @TestConfiguration
    public static class UserControllerTestContextConfig {
        @Bean
        public UserService fileService() {
            return new UserService();
        }
    }

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;


    @Before
    public void setUp() throws Exception {
        User user = new User();
        user.setUsername("smith@com.au");
        user.setPassword("asdf@123");
        user.setFirstname("Steve");
        user.setLastname("Smith");
        List<User> users = new ArrayList<>();
        users.add(user);

        Mockito.when(userRepository.findByUsernameAndPassword("smith@com.au","551f1d70451500e9025390b180974abc64efdc2f"))
                .thenReturn(users);
    }


    @Test
    public void logInTest() throws Exception{
        List<User> user = userService.login("smith@com.au","asdf@123");
        assertThat(user).isNotEmpty();
        assertThat(user.size()).isEqualTo(1);
    }

    @Test
    public void invalidLoginTest() {
        List<User> user = userService.login("smith@com.au","steve@123");
        assertThat(user).isEmpty();
    }

}

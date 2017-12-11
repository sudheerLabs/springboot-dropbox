package com.cmpe273.dropbox.service;

import com.cmpe273.dropbox.entity.User;
import com.cmpe273.dropbox.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public static final String SALT = "CCHIKPNO83FY";

    public Iterable<User> getAllUsers(){
        return userRepository.findAll();
    }

    public void addUser(User user){
        String hashedPassword = generateHash(SALT + user.getPassword());
        user.setPassword(hashedPassword);
        userRepository.save(user);
    }

    public List<User> login(String username, String password){
        String hashedPassword = generateHash(SALT + password);
        return userRepository.findByUsernameAndPassword(username,hashedPassword);
    }

    public static String generateHash(String input) {
        StringBuilder hash = new StringBuilder();

        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            byte[] hashedBytes = sha.digest(input.getBytes());
            char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                    'a', 'b', 'c', 'd', 'e', 'f' };
            for (int idx = 0; idx < hashedBytes.length; ++idx) {
                byte b = hashedBytes[idx];
                hash.append(digits[(b & 0xf0) >> 4]);
                hash.append(digits[b & 0x0f]);
            }
        } catch (NoSuchAlgorithmException e) {
            // handle error here.
        }

        return hash.toString();
    }
}

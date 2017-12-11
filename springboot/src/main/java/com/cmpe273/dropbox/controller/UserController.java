package com.cmpe273.dropbox.controller;

import com.cmpe273.dropbox.entity.User;
import com.cmpe273.dropbox.service.UserService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.html.HTMLParagraphElement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


@Controller    // This means that this class is a Controller
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping(path="/user") // This means URL's start with /demo (after Application path)
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping(path="/signup",consumes = MediaType.APPLICATION_JSON_VALUE) // Map ONLY POST Requests
    public  ResponseEntity<?> addNewUser (@RequestBody User user,  HttpSession session) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request
        userService.addUser(user);
        System.out.println("Saved");
        Path path = Paths.get("./root/" + user.getUsername());

        try {
            Path newDir = Files.createDirectory(path);
        } catch(FileAlreadyExistsException e){
            // the directory already exists.
        } catch (IOException e) {
            //something else went wrong
            e.printStackTrace();
        }
        //System.out.println(currentDir.toAbsolutePath().normalize());
        return new ResponseEntity(null,HttpStatus.CREATED);
    }

    @GetMapping(path="/all",produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Iterable<User> getAllUsers() {
        // This returns a JSON with the users
        return userService.getAllUsers();
    }

    @PostMapping(path="/login",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody String user, HttpSession session)
    {
        JSONObject jsonObject = new JSONObject(user);
        System.out.println(jsonObject.getString("username"));
        session.setAttribute("name", jsonObject.getString("username") );
        System.out.println(session.getAttribute("name"));
        return new ResponseEntity(userService.login(jsonObject.getString("username"),jsonObject.getString("password")),HttpStatus.OK);
    }

    @PostMapping(value = "/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> logout(HttpSession session) {
        System.out.println(session.getAttribute("name") + "session name here");
        session.invalidate();
        return  new ResponseEntity(HttpStatus.OK);
    }
}
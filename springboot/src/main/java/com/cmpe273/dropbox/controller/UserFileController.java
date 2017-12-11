package com.cmpe273.dropbox.controller;

import com.cmpe273.dropbox.entity.UserFile;
import com.cmpe273.dropbox.service.UserFileService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.HTTP;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.html.HTMLParagraphElement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller    // This means that this class is a Controller
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping(path="/user") // This means URL's start with /demo (after Application path)
public class UserFileController {
    @Autowired
    private UserFileService userFileService;

    @GetMapping(path="/getAllFiles",produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Iterable<UserFile> getAllFiles() {
        // This returns a JSON with the files
        return userFileService.getAllFiles();
    }

    @PostMapping(path="/getFiles")
    public @ResponseBody Iterable<UserFile> getFiles (@RequestBody String path, HttpSession session)
    {
        System.out.println(path + "in getFiles");
        JSONObject jsonObject = new JSONObject(path);
        System.out.println(jsonObject.getString("dir"));
        //return new ResponseEntity(userFileService.getFiles(jsonObject.getString("author")),HttpStatus.OK);
        return userFileService.getFiles(jsonObject.getString("dir"));
    }

    @PostMapping("/addFile")
    public ResponseEntity<?> addFile(@RequestBody UserFile file) {
        userFileService.addFile(file);
        return new ResponseEntity(null,HttpStatus.CREATED);
    }

    @PostMapping("/toggleStar")
    public ResponseEntity<UserFile> toggleStar(@RequestBody String jsonFileId, HttpSession session) {
        System.out.println("In togggle star method" + jsonFileId + "is there something in between");
        JSONObject jsonObject = new JSONObject(jsonFileId);
        System.out.println(jsonObject.getInt("fileId"));
        //return new ResponseEntity(userFileService.getFiles(jsonObject.getString("author")),HttpStatus.OK);
        //userFileService.toggleStar(jsonObject.getInt("fileId"));
        //return ResponseEntity.ok().body("updated successfully");
        System.out.println(session.getAttribute("name") + "session name here");
        return ResponseEntity.ok(userFileService.toggleStar(jsonObject.getInt("fileId")));
    }


    @PostMapping("/deleteFile")
    public ResponseEntity<UserFile> deleteFile(@RequestBody String jsonFileId) {
        System.out.println(jsonFileId);
        JSONObject jsonObject = new JSONObject(jsonFileId);
        System.out.println(jsonObject.getInt("fileId"));
        //return new ResponseEntity(userFileService.getFiles(jsonObject.getString("author")),HttpStatus.OK);
        //userFileService.deleteFile(jsonObject.getInt("fileId"));
        //return ResponseEntity.ok().body("deleted successfully");
        return ResponseEntity.ok(userFileService.deleteFile(jsonObject.getInt("fileId")));
    }

    @PostMapping("/addFolder")
    public ResponseEntity<UserFile> addFolder(@RequestBody String jsonpath, HttpSession session) {
        JSONObject jsonObject = new JSONObject(jsonpath);
        System.out.println(jsonObject.getString("path"));
        System.out.println(jsonObject.getString("folderName"));

        //return new ResponseEntity(userFileService.getFiles(jsonObject.getString("author")),HttpStatus.OK);
        UserFile folder = userFileService.addFile(jsonObject.getString("path"), jsonObject.getString("folderName"), session.getAttribute("name").toString(), "folder", 'N');
        String path = jsonObject.getString("path").replace(',','/')  + jsonObject.getString("folderName");
        System.out.println(path);

        //Path path = Paths.get("./root/" + user.getUsername());

         Path filepath = Paths.get("." + path);

        try {
            Path newDir = Files.createDirectory(filepath);
        } catch(FileAlreadyExistsException e){
            // the directory already exists.
        } catch (IOException e) {
            //something else went wrong
            e.printStackTrace();
        }
        //System.out.println(currentDir.toAbsolutePath().normalize());

        return  ResponseEntity.ok(folder);
    }



    @PostMapping("/uploadFile")
    public ResponseEntity<?> uploadFile(@RequestParam("files") MultipartFile file, @RequestParam("path") String filepath, HttpSession session) {
        System.out.println(file);
        System.out.println(filepath);
        Path currentDir = Paths.get("./" + filepath.replace(',', '/') + file.getOriginalFilename());
        System.out.println(currentDir.toAbsolutePath().normalize());

        System.out.println(file.getOriginalFilename());
        try {
            //System.out.println(file.getBytes());
            Files.write(currentDir, file.getBytes());
            userFileService.addFile(filepath, file.getOriginalFilename(), session.getAttribute("name").toString(), "file", 'N');
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity(null,HttpStatus.CREATED);
    }


    @PostMapping("/downloadFile")
    public ResponseEntity<?> downloadFile(@RequestBody String filepath, HttpSession session, HttpServletResponse response) {
        System.out.println(filepath);
        JSONObject jsonObject = new JSONObject(filepath);
        System.out.println(jsonObject.getString("path"));
        Path currentDir = Paths.get("." + jsonObject.getString("path").replace(',', '/'));
        System.out.println(currentDir.toAbsolutePath().normalize());

        try {
            InputStream is = new FileInputStream(currentDir.toAbsolutePath().normalize().toString());
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(HttpStatus.CREATED);
    }


    @PostMapping("/shareFile")
    public ResponseEntity<?> shareFile(@RequestBody String details) {
        System.out.println(details);
        JSONObject jsonObject = new JSONObject(details);
        System.out.println(jsonObject.getString("filename"));
        System.out.println(jsonObject.getString("userlist"));
        System.out.println(jsonObject.getString("path"));

        String[] userList = jsonObject.getString("userlist").split(",");
        String filename = jsonObject.getString("filename");
        String path = jsonObject.getString("path");

        Path srcFile = Paths.get("./" + path.replace(',', '/') + filename);
        Path destPath;
        System.out.println(srcFile.toAbsolutePath().normalize());

        for(String user : userList){
            destPath = Paths.get("./root/" + user + "/" + filename);
            try {
                Files.copy(srcFile, destPath);
                userFileService.addFile(",root," + user + ",", filename, user, "file", 'Y');
            } catch(FileAlreadyExistsException e) {
                //destination file already exists
            } catch (IOException e) {
                //something else went wrong
                e.printStackTrace();
            }
        }

        return ResponseEntity.ok(HttpStatus.CREATED);
    }


    @PostMapping("/createGroup")
    public ResponseEntity<?> createGroup(@RequestBody String details, HttpSession session) {
        System.out.println(details);
        JSONObject jsonObject = new JSONObject(details);
        System.out.println(jsonObject.getString("groupname"));
        System.out.println(jsonObject.getString("userlist"));
        System.out.println(jsonObject.getString("path"));

        String[] userList = jsonObject.getString("userlist").split(",");
        String groupname = jsonObject.getString("groupname");
        String path = jsonObject.getString("path");

        Path srcFolder = Paths.get("./root/groups/" + groupname);
        System.out.println(srcFolder.toAbsolutePath().normalize());

        try {
            Path newDir = Files.createDirectory(srcFolder);
        } catch(FileAlreadyExistsException e){
            // the directory already exists.
        } catch (IOException e) {
            //something else went wrong
            e.printStackTrace();
        }

        for(String user : userList){
            userFileService.addFile(",root,groups,", groupname, user, "group", 'Y');
        }

        return ResponseEntity.ok(HttpStatus.CREATED);
    }

}

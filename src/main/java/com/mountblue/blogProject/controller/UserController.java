package com.mountblue.blogProject.controller;

import com.mountblue.blogProject.entity.User;
import com.mountblue.blogProject.service.UserService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Api(description = "Endpoint for Registration of Users")
@RequestMapping("/blogApp")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/users")
    @ApiOperation("Registers a new user")
    public ResponseEntity<?> saveUser(@RequestBody Map<String, User> userMap) {
        User user = null;

        for(Map.Entry<String, User> userEntry : userMap.entrySet()) {
            user = userEntry.getValue();
        }

        if(user == null || user.getName() == null || user.getEmail() == null || user.getPassword() == null) {
            return new ResponseEntity<>("Username/Email/Password cannot be null!", HttpStatus.BAD_REQUEST);
        }

        if(userService.existsByName(user.getName()) || userService.existsByEmail(user.getEmail())) {
            return new ResponseEntity<>("Username/Email already taken!", HttpStatus.FORBIDDEN);
        }

        user = userService.saveUser(user);

        if(user == null) {
            return new ResponseEntity<>("User could not be registered due to server issues! Please try again later!",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        HttpHeaders header = new HttpHeaders();
        header.add("Location", "users/" + user.getId());

        return new ResponseEntity<>("User registered successfully!", HttpStatus.ACCEPTED);
    }
}

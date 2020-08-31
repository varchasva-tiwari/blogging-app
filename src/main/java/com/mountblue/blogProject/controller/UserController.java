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

    private final String USER_REQUIRED_FIELDS_NULL = "Username/Email/Password cannot be null!";
    private final String USER_ALREADY_EXISTS = "Username/Email already taken!";
    private final String USER_WRONG_FORMAT = "User is wrong/wrongly formatted. Please check the provided JSON!";
    private final String REGISTER_USER = "Registers a new user";
    private final String USER_REGISTERED = "User registered successfully!";

    @PostMapping("/users")
    @ApiOperation(REGISTER_USER)
    public ResponseEntity<?> saveUser(@RequestBody Map<String, User> userMap) {
        User user = null;

        for(Map.Entry<String, User> userEntry : userMap.entrySet()) {
            user = userEntry.getValue();
        }

        if(user == null || user.getName() == null || user.getEmail() == null || user.getPassword() == null) {
            return new ResponseEntity<>(USER_REQUIRED_FIELDS_NULL, HttpStatus.BAD_REQUEST);
        }

        if(userService.existsByName(user.getName()) || userService.existsByEmail(user.getEmail())) {
            return new ResponseEntity<>(USER_ALREADY_EXISTS, HttpStatus.FORBIDDEN);
        }

        user = userService.saveUser(user);

        if(user == null) {
            return new ResponseEntity<>(USER_WRONG_FORMAT,
                    HttpStatus.BAD_REQUEST);
        }

        HttpHeaders header = new HttpHeaders();
        header.add("Location", "users/" + user.getId());

        return new ResponseEntity<>(USER_REGISTERED, HttpStatus.ACCEPTED);
    }
}

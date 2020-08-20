package com.mountblue.blogProject.controller;

import com.mountblue.blogProject.entity.User;
import com.mountblue.blogProject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/createUser")
    public String createUser(Model model) {
        model.addAttribute("newUser", new User());
        return "register";
    }

    @PostMapping("/createUser")
    public String createUser(@ModelAttribute("newUser") User user) {
        userService.create(user);
        return "login";
    }
}

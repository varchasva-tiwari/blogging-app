package com.mountblue.blogProject.service;

import com.mountblue.blogProject.security.MyUserDetails;
import com.mountblue.blogProject.entity.User;
import com.mountblue.blogProject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByName(username);

        if(user == null)
            throw new UsernameNotFoundException("Incorrect credentials");

        return new MyUserDetails(user);
    }

    public int getUserId(String name) {
        return userRepository.getId(name);
    }

    public void saveUser(User user) {
        user.setRole("ROLE_author");
        userRepository.save(user);
    }
}

package com.mountblue.blogProject.service;

import com.mountblue.blogProject.security.MyUserDetails;
import com.mountblue.blogProject.entity.User;
import com.mountblue.blogProject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements org.springframework.security.core.userdetails.UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);

        if(user.get() == null)
            throw new UsernameNotFoundException("Username not found");

        return new MyUserDetails(user.get());
    }

    public int getUserId(String name) {
        return userRepository.getId(name);
    }

    public void saveUser(User user) {
        user.setRole("ROLE_author");
        userRepository.save(user);
    }
}

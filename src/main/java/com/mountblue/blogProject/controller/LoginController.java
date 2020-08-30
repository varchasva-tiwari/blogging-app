package com.mountblue.blogProject.controller;

import com.mountblue.blogProject.entity.AuthenticationRequest;
import com.mountblue.blogProject.entity.AuthenticationResponse;
import com.mountblue.blogProject.service.JwtService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(description = "Endpoint for User Login")
@RequestMapping("/blogApp")
public class LoginController {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    @ApiOperation("Authenticates a user based on username & password, assigns him a JWT needed for other operations")
    private ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        if(authenticationRequest.getUsername() == null || authenticationRequest.getPassword() == null) {
            return new ResponseEntity<>("Username/Password cannot be null!", HttpStatus.BAD_REQUEST);
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken
                    (authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password!", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        final String jwt = jwtService.generateToken(userDetails);

        return new ResponseEntity<>(new AuthenticationResponse(jwt), HttpStatus.OK);
    }
}

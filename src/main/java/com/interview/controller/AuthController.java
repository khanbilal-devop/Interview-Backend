/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.interview.controller;


import com.interview.model.GenericServiceResponse;
import com.interview.model.User;
import com.interview.service.UserServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/app")
@AllArgsConstructor
public class AuthController {

    private final UserServiceImpl userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping(value = "/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody JwtRequest authenticationRequest) {
        GenericServiceResponse response = userService.loadUserByUsername(authenticationRequest.getEmail());
        if (response.getHttpStatus().equals(HttpStatus.OK)) {
            User user = (User) response.getTempData();
            if (passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword())) {
                response.setMessage("Login success");
                return new ResponseEntity<>(response, response.getHttpStatus());
            }else{
                response.setMessage("Invalid credentials");
                response.setData("");
                response.setHttpStatus(HttpStatus.UNAUTHORIZED);
            }
        }
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @Data
    public static class JwtRequest{
        String email;
        String password;
    }
}

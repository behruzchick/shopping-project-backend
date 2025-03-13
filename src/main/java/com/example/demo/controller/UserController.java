package com.example.demo.controller;

import com.example.demo.dtos.LoginUserDto;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.SecurityUtils;
import com.example.demo.security.TokenProvider;
import com.example.demo.service.UserService;
import com.example.demo.validator.UserValidator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final UserValidator userValidator;


    public UserController(UserRepository userRepository, UserService userService, AuthenticationManager authenticationManager, TokenProvider tokenProvider, UserValidator userValidator) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userValidator = userValidator;
    }

    @RequestMapping(value = "/user/register" , method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody User user) {
        ResponseEntity<String> validateResponse = userValidator.registerValid(user);

        if (validateResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok().body(userService.save(user));
        } else {
            return ResponseEntity.status(validateResponse.getStatusCode())
                    .body(validateResponse.getBody());
        }

    }

    @RequestMapping(value = "/user/all" , method = RequestMethod.GET)
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepository.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @RequestMapping(value = "/user/delete" , method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@RequestParam("id") Long id) {
        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @RequestMapping(value = "/user/login" , method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody LoginUserDto loginUserDto) {
        try{
            ResponseEntity<String> validateResponse = userValidator.loginValid(loginUserDto);
            if(validateResponse.getStatusCode().is2xxSuccessful()) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        loginUserDto.getEmail(),
                        loginUserDto.getPassword()
                );

                Authentication authentication = authenticationManager.authenticate(authenticationToken);

                SecurityContextHolder.getContext().setAuthentication(authentication);

                String jwt = tokenProvider.createToken(authentication , loginUserDto.isRememberMe());

                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);

                return ResponseEntity.ok(new JWTToken(jwt));
            }else{
                return ResponseEntity.status(validateResponse.getStatusCode())
                        .body(validateResponse.getBody());
            }
        }catch (Exception e) {
            throw e;
        }
    }


    @RequestMapping(value = "/user/account" , method = RequestMethod.GET)
    public ResponseEntity<?> getAccount() {
        String login = SecurityUtils.getCurrentUser().get();
        User user = userService.findByUsername(login);
        return ResponseEntity.ok(user);
    }

    static class JWTToken {
        private String token;

        public JWTToken(String token) {
            this.token = token;
        }

        @JsonProperty("jwt_token")
        public String getToken() {
            return token;
        }
    }

}

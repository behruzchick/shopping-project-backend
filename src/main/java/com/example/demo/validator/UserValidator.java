package com.example.demo.validator;

import com.example.demo.dtos.LoginUserDto;
import com.example.demo.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UserValidator{

    public ResponseEntity<String> registerValid(User user){
        if (user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
        }

        if(user.getEmail() == null || user.getEmail().isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is required!");
        }

        if(!user.getEmail().endsWith(".com")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email address has been @gmail.com or .com!");
        }
        if (user.getPassword().length() < 8){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password must be at least 8 characters!");
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password is required!");
        }

//        if (user.getFullName() == null || user.getFullName().isEmpty()){
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Full name is required!");
//        }

        if (user.getRoles() == null || user.getRoles().isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Roles are required!");
        }
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<String> loginValid(LoginUserDto loginUserDto){
        if (loginUserDto == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found!");
        }
        if ((loginUserDto.getEmail() == null || loginUserDto.getEmail().isEmpty())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is required!");
        }
        if (loginUserDto.getPassword() == null || loginUserDto.getPassword().isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password is required!");
        }
        return ResponseEntity.ok().build();
    }
}

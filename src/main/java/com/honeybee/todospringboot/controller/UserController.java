package com.honeybee.todospringboot.controller;

import com.honeybee.todospringboot.dto.ResponseDTO;
import com.honeybee.todospringboot.dto.UserDTO;
import com.honeybee.todospringboot.entity.UserEntity;
import com.honeybee.todospringboot.security.TokenProvider;
import com.honeybee.todospringboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private TokenProvider tokenProvider;
    
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO){
        try {
            if (userDTO == null || userDTO.getPassword() == null){
                throw new RuntimeException("Invalid Password value.");
            }
            UserEntity user = UserEntity.builder()
                    .username(userDTO.getUsername())
                    .password(passwordEncoder.encode(userDTO.getPassword()))
                    .build();

            UserEntity registerUser = userService.create(user);
            UserDTO responseDTO = UserDTO.builder()
                    .id(registerUser.getId())
                    .username(registerUser.getUsername())
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        }catch (Exception e){
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticate(@RequestBody UserDTO userDTO){
        UserEntity user = userService.getByCredentials(
                userDTO.getUsername(), userDTO.getPassword(),passwordEncoder
        );
        if (user != null){
            final String token = tokenProvider.create(user);
            final UserDTO responseUserDTO = UserDTO.builder()
                    .username(user.getUsername())
                    .id(user.getId())
                    .token(token)
                    .build();
            return ResponseEntity.ok().body(responseUserDTO);
        }else {
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .error("Login failed.")
                    .build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }


}

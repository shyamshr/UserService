package com.example.userservice.controllers;

import com.example.userservice.dtos.*;
import com.example.userservice.models.SessionStatus;
import com.example.userservice.services.AuthService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private AuthService authService;
    @Autowired
    public AuthController(AuthService authService){
        this.authService = authService;
    }
    @GetMapping("/home")
    public String home(){

        return "Welcome, You have logged in";
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequestDto logoutRequestDto){
        this.authService.logout(logoutRequestDto.getToken(),logoutRequestDto.getUserId());
        return new ResponseEntity<>(null);
    }
    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpRequestDto signUpRequestDto){
        UserDto userDto = UserDto.from(this.authService.signUp(signUpRequestDto.getEmail(),signUpRequestDto.getPassword()));
        ResponseEntity<UserDto> responseEntity = new ResponseEntity<>(userDto, HttpStatus.CREATED);
        return responseEntity;
    }
    @PostMapping("/validate")
    public ResponseEntity<SessionStatus> validateToken(@RequestBody ValidateTokenRequestDto request) {
        SessionStatus sessionStatus = authService.validate(request.getToken(), request.getUserId());

        return new ResponseEntity<>(sessionStatus, HttpStatus.OK);
    }
}

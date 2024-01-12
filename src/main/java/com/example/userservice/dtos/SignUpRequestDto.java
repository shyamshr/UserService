package com.example.userservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequestDto {

    private String firstName;


    private String lastName;


    private String password;
    private String matchingPassword;


    private String email;
}

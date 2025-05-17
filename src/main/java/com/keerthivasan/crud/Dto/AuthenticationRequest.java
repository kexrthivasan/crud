package com.keerthivasan.crud.Dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    private String username;
    private String password;
    private String email;
    private String role;
}

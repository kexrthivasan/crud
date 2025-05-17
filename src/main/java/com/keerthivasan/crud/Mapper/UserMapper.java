package com.keerthivasan.crud.Mapper;

import com.keerthivasan.crud.Dto.RegisterRequest;
import com.keerthivasan.crud.Model.User;

public class UserMapper {
    public static User toEntity(RegisterRequest request, String encodedPassword) {
        return User.builder()
                .username(request.getUsername())
                .password(encodedPassword)
                .email(request.getEmail())
                .build();
    }
}
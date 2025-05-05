package com.keerthivasan.crud.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentDto {
    private Long id;
    private String name;
    private String email;
    private Integer age;
    private String course;
    private String address;
    private String phoneNumber;
}

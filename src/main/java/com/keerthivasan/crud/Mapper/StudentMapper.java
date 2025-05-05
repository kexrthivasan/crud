package com.keerthivasan.crud.Mapper;

import com.keerthivasan.crud.Dto.StudentDto;
import com.keerthivasan.crud.Model.Student;

public class StudentMapper {

    public static StudentDto toDto(Student student) {
        return new StudentDto(
                student.getId(),
                student.getName(),
                student.getEmail(),
                student.getAge(),
                student.getCourse(),
                student.getAddress(),
                student.getPhoneNumber()
        );
    }

    public static Student toEntity(StudentDto dto) {
        Student student = new Student();
        student.setId(dto.getId());
        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        student.setAge(dto.getAge());
        student.setCourse(dto.getCourse());
        student.setAddress(dto.getAddress());
        student.setPhoneNumber(dto.getPhoneNumber());
        return student;
    }
}

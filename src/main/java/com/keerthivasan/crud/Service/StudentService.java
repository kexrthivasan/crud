package com.keerthivasan.crud.Service;

import com.keerthivasan.crud.Dto.StudentDto;
import com.keerthivasan.crud.Mapper.StudentMapper;
import com.keerthivasan.crud.Model.Student;
import com.keerthivasan.crud.Repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public List<StudentDto> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(StudentMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<StudentDto> getStudentById(Long id) {
        return studentRepository.findById(id).map(StudentMapper::toDto);
    }

    public StudentDto createStudent(StudentDto studentDto) {
        Student student = StudentMapper.toEntity(studentDto);
        Student saved = studentRepository.save(student);
        return StudentMapper.toDto(saved);
    }

    public StudentDto updateStudent(Long id, StudentDto dto) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id " + id));

        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        student.setAge(dto.getAge());
        student.setCourse(dto.getCourse());
        student.setAddress(dto.getAddress());
        student.setPhoneNumber(dto.getPhoneNumber());

        Student updated = studentRepository.save(student);
        return StudentMapper.toDto(updated);
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }
}

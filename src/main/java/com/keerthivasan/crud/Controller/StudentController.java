package com.keerthivasan.crud.Controller;

import com.keerthivasan.crud.Dto.StudentDto;
import com.keerthivasan.crud.Service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN' , 'STUDENT')")
    public List<StudentDto> getAllStudents() {
        return studentService.getAllStudents();
    }
    @PreAuthorize("hasAnyRole('ADMIN' , 'STUDENT')")
    @GetMapping("/{id}")
    public ResponseEntity<StudentDto> getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<StudentDto> createStudent(@RequestBody StudentDto dto) {
        StudentDto saved = studentService.createStudent(dto);
        return ResponseEntity.ok(saved);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<StudentDto> updateStudent(@PathVariable Long id, @RequestBody StudentDto dto) {
        try {
            StudentDto updated = studentService.updateStudent(id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        System.out.println("Deleted success fully"+ getStudentById(id));
        System.out.println("Student with ID " + id + " deleted successfully.");
        return ResponseEntity.noContent().build();
    }
}

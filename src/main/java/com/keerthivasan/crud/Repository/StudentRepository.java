package com.keerthivasan.crud.Repository;

import com.keerthivasan.crud.Model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}

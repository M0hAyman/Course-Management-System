package com.mohamed.coursemanagement.repository;

import com.mohamed.coursemanagement.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}

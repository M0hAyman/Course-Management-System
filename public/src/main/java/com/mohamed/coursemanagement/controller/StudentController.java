package com.mohamed.coursemanagement.controller;

import com.mohamed.coursemanagement.dto.StudentRequestDto;
import com.mohamed.coursemanagement.dto.StudentResponseDto;
import com.mohamed.coursemanagement.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<StudentResponseDto> create(@Valid @RequestBody StudentRequestDto request) {
        StudentResponseDto created = studentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<StudentResponseDto>> getAll(
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(studentService.getAll(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDto> update(
            @PathVariable Long id, @Valid @RequestBody StudentRequestDto request) {
        return ResponseEntity.ok(studentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

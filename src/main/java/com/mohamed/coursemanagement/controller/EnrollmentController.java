package com.mohamed.coursemanagement.controller;

import com.mohamed.coursemanagement.dto.EnrollmentRequestDto;
import com.mohamed.coursemanagement.dto.EnrollmentResponseDto;
import com.mohamed.coursemanagement.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<EnrollmentResponseDto> enroll(@Valid @RequestBody EnrollmentRequestDto request) {
        EnrollmentResponseDto created = enrollmentService.enroll(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> unenroll(@PathVariable Long id) {
        enrollmentService.unenroll(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<Page<EnrollmentResponseDto>> getByStudent(
            @PathVariable Long studentId,
            @PageableDefault(size = 10, sort = "enrolledAt") Pageable pageable) {
        return ResponseEntity.ok(enrollmentService.getByStudent(studentId, pageable));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<Page<EnrollmentResponseDto>> getByCourse(
            @PathVariable Long courseId,
            @PageableDefault(size = 10, sort = "enrolledAt") Pageable pageable) {
        return ResponseEntity.ok(enrollmentService.getByCourse(courseId, pageable));
    }
}

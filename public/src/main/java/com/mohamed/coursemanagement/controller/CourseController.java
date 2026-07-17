package com.mohamed.coursemanagement.controller;

import com.mohamed.coursemanagement.dto.CourseResponseDto;
import com.mohamed.coursemanagement.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<CourseResponseDto>> getAll(
            @RequestParam(required = false) Long instructorId,
            @PageableDefault(size = 10, sort = "title") Pageable pageable) {
        Page<CourseResponseDto> courses = instructorId != null
                ? courseService.getByInstructor(instructorId, pageable)
                : courseService.getAll(pageable);
        return ResponseEntity.ok(courses);
    }
}

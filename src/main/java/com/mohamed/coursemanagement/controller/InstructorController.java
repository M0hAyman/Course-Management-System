package com.mohamed.coursemanagement.controller;

import com.mohamed.coursemanagement.dto.InstructorRequestDto;
import com.mohamed.coursemanagement.dto.InstructorResponseDto;
import com.mohamed.coursemanagement.service.InstructorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/instructors")
@RequiredArgsConstructor
public class InstructorController {

    private final InstructorService instructorService;

    @PostMapping
    public ResponseEntity<InstructorResponseDto> create(@Valid @RequestBody InstructorRequestDto request) {
        InstructorResponseDto created = instructorService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstructorResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(instructorService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<InstructorResponseDto>> getAll(
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(instructorService.getAll(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InstructorResponseDto> update(
            @PathVariable Long id, @Valid @RequestBody InstructorRequestDto request) {
        return ResponseEntity.ok(instructorService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        instructorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

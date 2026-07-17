package com.mohamed.coursemanagement.service;

import com.mohamed.coursemanagement.dto.CourseResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseService {

    CourseResponseDto getById(Long id);

    Page<CourseResponseDto> getAll(Pageable pageable);

    Page<CourseResponseDto> getByInstructor(Long instructorId, Pageable pageable);
}

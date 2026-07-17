package com.mohamed.coursemanagement.service.impl;

import com.mohamed.coursemanagement.dto.CourseResponseDto;
import com.mohamed.coursemanagement.entity.Course;
import com.mohamed.coursemanagement.exception.ResourceNotFoundException;
import com.mohamed.coursemanagement.repository.CourseRepository;
import com.mohamed.coursemanagement.repository.InstructorRepository;
import com.mohamed.coursemanagement.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;

    @Override
    public CourseResponseDto getById(Long id) {
        Course course = courseRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        return toResponseDto(course);
    }

    @Override
    public Page<CourseResponseDto> getAll(Pageable pageable) {
        return courseRepository.findByDeletedFalse(pageable).map(this::toResponseDto);
    }

    @Override
    public Page<CourseResponseDto> getByInstructor(Long instructorId, Pageable pageable) {
        if (!instructorRepository.existsById(instructorId)) {
            throw new ResourceNotFoundException("Instructor not found with id: " + instructorId);
        }
        return courseRepository.findByInstructorIdAndDeletedFalse(instructorId, pageable).map(this::toResponseDto);
    }

    private CourseResponseDto toResponseDto(Course course) {
        return new CourseResponseDto(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getCredits(),
                course.getInstructor().getId(),
                course.getInstructor().getName()
        );
    }
}

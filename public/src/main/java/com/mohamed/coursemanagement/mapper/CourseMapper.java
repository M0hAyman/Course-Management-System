package com.mohamed.coursemanagement.mapper;

import com.mohamed.coursemanagement.dto.CourseResponseDto;
import com.mohamed.coursemanagement.entity.Course;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper {

    public CourseResponseDto toDto(Course course) {
        return new CourseResponseDto(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getCredits(),
                course.getInstructor().getId(),
                course.getInstructor().getName(),
                course.getRegistrationStartTime(),
                course.getRegistrationEndTime()
        );
    }
}

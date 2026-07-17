package com.mohamed.coursemanagement.dto;

public record CourseResponseDto(
        Long id,
        String title,
        String description,
        Integer credits,
        Long instructorId,
        String instructorName
) {
}

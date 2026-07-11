package com.mohamed.coursemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CourseRequestDto(

        @NotBlank(message = "Title is required")
        String title,

        String description,

        @NotNull(message = "Credits is required")
        @Positive(message = "Credits must be a positive number")
        Integer credits,

        @NotNull(message = "Instructor id is required")
        Long instructorId
) {
}

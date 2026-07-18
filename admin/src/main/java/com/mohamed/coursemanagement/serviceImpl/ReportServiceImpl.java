package com.mohamed.coursemanagement.serviceImpl;

import com.mohamed.coursemanagement.dto.CourseReportDto;
import com.mohamed.coursemanagement.entity.Course;
import com.mohamed.coursemanagement.repository.CourseRepository;
import com.mohamed.coursemanagement.repository.EnrollmentRepository;
import com.mohamed.coursemanagement.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public Page<CourseReportDto> courseEnrollmentReport(Pageable pageable) {
        return courseRepository.findByDeletedFalse(pageable).map(this::toReportDto);
    }

    private CourseReportDto toReportDto(Course course) {
        return new CourseReportDto(
                course.getId(),
                course.getTitle(),
                course.getInstructor().getName(),
                enrollmentRepository.countByCourseId(course.getId()),
                course.getRegistrationStartTime(),
                course.getRegistrationEndTime()
        );
    }
}

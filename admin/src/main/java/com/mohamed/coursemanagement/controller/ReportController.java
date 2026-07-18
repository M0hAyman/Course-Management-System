package com.mohamed.coursemanagement.controller;

import com.mohamed.coursemanagement.dto.CourseReportDto;
import com.mohamed.coursemanagement.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/courses")
    public ResponseEntity<Page<CourseReportDto>> courseEnrollmentReport(
            @PageableDefault(size = 10, sort = "title") Pageable pageable) {
        return ResponseEntity.ok(reportService.courseEnrollmentReport(pageable));
    }
}

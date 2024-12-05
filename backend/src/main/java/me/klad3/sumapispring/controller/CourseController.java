package me.klad3.sumapispring.controller;

import lombok.RequiredArgsConstructor;
import me.klad3.sumapispring.dto.ApiResponse;
import me.klad3.sumapispring.dto.CourseResponse;
import me.klad3.sumapispring.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getCourses(HttpServletRequest request) {
        String cookies = request.getHeader("Cookie");

        if (cookies == null || cookies.isEmpty()) {
            return new ResponseEntity<>(ApiResponse.error("Missing cookies", null), HttpStatus.UNAUTHORIZED);
        }

        List<CourseResponse> courses = courseService.getCourses(cookies);

        ApiResponse<List<CourseResponse>> apiResponse = ApiResponse.success("Courses fetched successfully", courses);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}

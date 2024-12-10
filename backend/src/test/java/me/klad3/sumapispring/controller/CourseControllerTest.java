package me.klad3.sumapispring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.klad3.sumapispring.dto.CourseResponse;
import me.klad3.sumapispring.service.CourseService;
import me.klad3.sumapispring.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<CourseResponse> mockCourses;

    @BeforeEach
    void setUp() {
        mockCourses = Arrays.asList(
                CourseResponse.builder()
                        .carrera("Ingeniería")
                        .plan("Plan 2023")
                        .ciclo("Ciclo 1")
                        .curso("Matemáticas")
                        .seccion("A")
                        .profesor("Juan Pérez")
                        .build(),
                CourseResponse.builder()
                        .carrera("Ingeniería")
                        .plan("Plan 2023")
                        .ciclo("Ciclo 1")
                        .curso("Física")
                        .seccion("B")
                        .profesor("María García")
                        .build()
        );
    }

    @Test
    void getCourses_ShouldReturnCourses_WhenCookiesPresent() throws Exception {
        String cookies = "SESSIONID=abc123; CSRF-TOKEN=def456";

        when(courseService.getCourses(cookies)).thenReturn(mockCourses);

        mockMvc.perform(get("/api/courses")
                        .header("Cookie", cookies)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Courses fetched successfully")))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].carrera", is("Ingeniería")))
                .andExpect(jsonPath("$.data[0].plan", is("Plan 2023")))
                .andExpect(jsonPath("$.data[0].ciclo", is("Ciclo 1")))
                .andExpect(jsonPath("$.data[0].curso", is("Matemáticas")))
                .andExpect(jsonPath("$.data[0].seccion", is("A")))
                .andExpect(jsonPath("$.data[0].profesor", is("Juan Pérez")))
                .andExpect(jsonPath("$.data[1].carrera", is("Ingeniería")))
                .andExpect(jsonPath("$.data[1].plan", is("Plan 2023")))
                .andExpect(jsonPath("$.data[1].ciclo", is("Ciclo 1")))
                .andExpect(jsonPath("$.data[1].curso", is("Física")))
                .andExpect(jsonPath("$.data[1].seccion", is("B")))
                .andExpect(jsonPath("$.data[1].profesor", is("María García")));

        ArgumentCaptor<String> cookiesCaptor = ArgumentCaptor.forClass(String.class);
        verify(courseService, times(1)).getCourses(cookiesCaptor.capture());
        assertEquals(cookies, cookiesCaptor.getValue());
    }

    @Test
    void getCourses_ShouldReturnUnauthorized_WhenCookiesMissing() throws Exception {
        mockMvc.perform(get("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Missing cookies")))
                .andExpect(jsonPath("$.data", is(nullValue())));

        verify(courseService, never()).getCourses(anyString());
    }

    @Test
    void getCourses_ShouldReturnUnauthorized_WhenCookiesEmpty() throws Exception {
        mockMvc.perform(get("/api/courses")
                        .header("Cookie", "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Missing cookies")))
                .andExpect(jsonPath("$.data", is(nullValue())));

        verify(courseService, never()).getCourses(anyString());
    }

    @Test
    void getCourses_ShouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
        String cookies = "SESSIONID=abc123; CSRF-TOKEN=def456";

        when(courseService.getCourses(cookies)).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/courses")
                        .header("Cookie", cookies)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Internal Server Error")))
                .andExpect(jsonPath("$.data.message", is("An unexpected error occurred")))
                .andExpect(jsonPath("$.data.error", is("Database error")));

        verify(courseService, times(1)).getCourses(cookies);
    }
}

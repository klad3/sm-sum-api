package me.klad3.sumapispring.service;

import me.klad3.sumapispring.dto.CourseResponse;
import me.klad3.sumapispring.exception.ExternalApiException;
import me.klad3.sumapispring.util.HttpClientUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseServiceTest {

    @Mock
    private HttpClientUtil httpClientUtil;

    @InjectMocks
    private CourseService courseService;

    @Captor
    private ArgumentCaptor<String> urlCaptor;

    @Captor
    private ArgumentCaptor<String> cookiesCaptor;

    private static final String TEST_COURSES_URL = "https://sum.unmsm.edu.pe/alumnoWebSum/v2/reportes/matricula?accion=obtenerAlumnoMatricula";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getCourses_Success() throws IOException, InterruptedException {
        String cookies = "SESSIONID=abc123";

        String jsonResponse = "{ \"data\": { \"matricula\": [ " +
                "{ \"desEscuela\": \"Ingeniería\", \"codPlan\": \"INF-2021\", \"cicloEstudio\": \"2023-1\", " +
                "\"desAsignatura\": \"Programación\", \"codSeccion\": \"01\", " +
                "\"nomDocente\": \"Juan\", \"apePatDocente\": \"Pérez\", \"apeMatDocente\": \"Gómez\" } " +
                "] } }";

        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(jsonResponse);
        when(httpClientUtil.getWithCookies(TEST_COURSES_URL, cookies)).thenReturn(mockResponse);

        List<CourseResponse> courses = courseService.getCourses(cookies);

        assertNotNull(courses);
        assertEquals(1, courses.size());

        CourseResponse course = courses.get(0);
        assertEquals("Ingeniería", course.getCarrera());
        assertEquals("INF-2021", course.getPlan());
        assertEquals("2023-1", course.getCiclo());
        assertEquals("Programación", course.getCurso());
        assertEquals("01", course.getSeccion());
        assertEquals("Juan Pérez Gómez", course.getProfesor());

        verify(httpClientUtil, times(1)).getWithCookies(TEST_COURSES_URL, cookies);
        verifyNoMoreInteractions(httpClientUtil);
    }

    @Test
    void getCourses_HttpError() throws IOException, InterruptedException {
        String cookies = "SESSIONID=abc123";

        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(500);
        when(mockResponse.body()).thenReturn("Internal Server Error");
        when(httpClientUtil.getWithCookies(TEST_COURSES_URL, cookies)).thenReturn(mockResponse);

        ExternalApiException exception = assertThrows(ExternalApiException.class, () -> {
            courseService.getCourses(cookies);
        });

        assertEquals("Failed to fetch courses from external API", exception.getMessage());

        verify(httpClientUtil, times(1)).getWithCookies(TEST_COURSES_URL, cookies);
        verifyNoMoreInteractions(httpClientUtil);
    }

    @Test
    void getCourses_ParseError() throws IOException, InterruptedException {
        String cookies = "SESSIONID=abc123";

        String invalidJsonResponse = "{ invalid json }";

        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(invalidJsonResponse);
        when(httpClientUtil.getWithCookies(TEST_COURSES_URL, cookies)).thenReturn(mockResponse);

        ExternalApiException exception = assertThrows(ExternalApiException.class, () -> {
            courseService.getCourses(cookies);
        });

        assertTrue(exception.getMessage().contains("Error parsing courses response"));

        verify(httpClientUtil, times(1)).getWithCookies(TEST_COURSES_URL, cookies);
        verifyNoMoreInteractions(httpClientUtil);
    }

    @Test
    void getCourses_IOException() throws IOException, InterruptedException {
        String cookies = "SESSIONID=abc123";

        when(httpClientUtil.getWithCookies(TEST_COURSES_URL, cookies)).thenThrow(new IOException("IO error"));

        ExternalApiException exception = assertThrows(ExternalApiException.class, () -> {
            courseService.getCourses(cookies);
        });

        assertTrue(exception.getMessage().contains("Error fetching courses from external API"));

        verify(httpClientUtil, times(1)).getWithCookies(TEST_COURSES_URL, cookies);
        verifyNoMoreInteractions(httpClientUtil);
    }

    @Test
    void getCourses_InterruptedException() throws IOException, InterruptedException {
        String cookies = "SESSIONID=abc123";

        when(httpClientUtil.getWithCookies(TEST_COURSES_URL, cookies)).thenThrow(new InterruptedException("Interrupted"));

        ExternalApiException exception = assertThrows(ExternalApiException.class, () -> {
            courseService.getCourses(cookies);
        });

        assertTrue(exception.getMessage().contains("Error fetching courses from external API"));

        verify(httpClientUtil, times(1)).getWithCookies(TEST_COURSES_URL, cookies);
        verifyNoMoreInteractions(httpClientUtil);
    }
}
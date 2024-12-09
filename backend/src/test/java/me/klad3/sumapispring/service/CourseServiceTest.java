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
        // Arrange
        String cookies = "SESSIONID=abc123";

        // Crear una respuesta JSON simulada
        String jsonResponse = "{ \"data\": { \"matricula\": [ " +
                "{ \"desEscuela\": \"Ingeniería\", \"codPlan\": \"INF-2021\", \"cicloEstudio\": \"2023-1\", " +
                "\"desAsignatura\": \"Programación\", \"codSeccion\": \"01\", " +
                "\"nomDocente\": \"Juan\", \"apePatDocente\": \"Pérez\", \"apeMatDocente\": \"Gómez\" } " +
                "] } }";

        // Simular la respuesta HTTP exitosa
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(jsonResponse);
        when(httpClientUtil.getWithCookies(TEST_COURSES_URL, cookies)).thenReturn(mockResponse);

        // Act
        List<CourseResponse> courses = courseService.getCourses(cookies);

        // Assert
        assertNotNull(courses);
        assertEquals(1, courses.size());

        CourseResponse course = courses.get(0);
        assertEquals("Ingeniería", course.getCarrera());
        assertEquals("INF-2021", course.getPlan());
        assertEquals("2023-1", course.getCiclo());
        assertEquals("Programación", course.getCurso());
        assertEquals("01", course.getSeccion());
        assertEquals("Juan Pérez Gómez", course.getProfesor());

        // Verificar que la URL y las cookies fueron llamadas correctamente
        verify(httpClientUtil, times(1)).getWithCookies(TEST_COURSES_URL, cookies);
        verifyNoMoreInteractions(httpClientUtil);
    }

    @Test
    void getCourses_HttpError() throws IOException, InterruptedException {
        // Arrange
        String cookies = "SESSIONID=abc123";

        // Simular una respuesta HTTP con código 500
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(500);
        when(mockResponse.body()).thenReturn("Internal Server Error");
        when(httpClientUtil.getWithCookies(TEST_COURSES_URL, cookies)).thenReturn(mockResponse);

        // Act & Assert
        ExternalApiException exception = assertThrows(ExternalApiException.class, () -> {
            courseService.getCourses(cookies);
        });

        assertEquals("Failed to fetch courses from external API", exception.getMessage());

        // Verificar que solo se llamó a getWithCookies
        verify(httpClientUtil, times(1)).getWithCookies(TEST_COURSES_URL, cookies);
        verifyNoMoreInteractions(httpClientUtil);
    }

    @Test
    void getCourses_ParseError() throws IOException, InterruptedException {
        // Arrange
        String cookies = "SESSIONID=abc123";

        // Simular una respuesta HTTP con JSON inválido
        String invalidJsonResponse = "{ invalid json }";

        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(invalidJsonResponse);
        when(httpClientUtil.getWithCookies(TEST_COURSES_URL, cookies)).thenReturn(mockResponse);

        // Act & Assert
        ExternalApiException exception = assertThrows(ExternalApiException.class, () -> {
            courseService.getCourses(cookies);
        });

        assertTrue(exception.getMessage().contains("Error parsing courses response"));

        // Verificar que solo se llamó a getWithCookies
        verify(httpClientUtil, times(1)).getWithCookies(TEST_COURSES_URL, cookies);
        verifyNoMoreInteractions(httpClientUtil);
    }
}

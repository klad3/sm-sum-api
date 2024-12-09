package me.klad3.sumapispring.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.klad3.sumapispring.dto.CourseResponse;
import me.klad3.sumapispring.dto.MatriculaResponse;
import me.klad3.sumapispring.exception.ExternalApiException;
import me.klad3.sumapispring.util.HttpClientUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final HttpClientUtil httpClientUtil;

    private static final String COURSES_URL = "https://sum.unmsm.edu.pe/alumnoWebSum/v2/reportes/matricula?accion=obtenerAlumnoMatricula";

    public List<CourseResponse> getCourses(String cookies) {
        try {
            HttpResponse<String> coursesResponse = httpClientUtil.getWithCookies(COURSES_URL, cookies);

            if (coursesResponse.statusCode() != 200) {
                throw new ExternalApiException("Failed to fetch courses from external API");
            }

            MatriculaResponse matriculaResponse = parseCoursesResponse(coursesResponse.body());

            return matriculaResponse.getData().getMatricula().stream()
                    .map(element -> new CourseResponse(
                            element.getDesEscuela(),
                            element.getCodPlan(),
                            element.getCicloEstudio(),
                            element.getDesAsignatura(),
                            element.getCodSeccion(),
                            String.format("%s %s %s", element.getNomDocente(), element.getApePatDocente(), element.getApeMatDocente())
                    ))
                    .toList();

        } catch (IOException e) {
            throw new ExternalApiException("Error fetching courses from external API", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ExternalApiException("Error fetching courses from external API", e);
        }
    }

    private MatriculaResponse parseCoursesResponse(String responseBody) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(responseBody, MatriculaResponse.class);
        } catch (JsonProcessingException e) {
            throw new ExternalApiException("Error parsing courses response", e);
        }
    }
}
package me.klad3.sumapispring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponse {
    private String carrera;
    private String plan;
    private String ciclo;
    private String curso;
    private String seccion;
    private String profesor;
}

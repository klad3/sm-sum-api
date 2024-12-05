package me.klad3.sumapispring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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

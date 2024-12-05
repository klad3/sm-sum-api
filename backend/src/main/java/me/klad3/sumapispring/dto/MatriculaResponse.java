package me.klad3.sumapispring.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MatriculaResponse {

    @JsonProperty("data")
    private MatriculaData data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MatriculaData {
        @JsonProperty("matricula")
        private List<Matricula> matricula;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Matricula {
        @JsonProperty("desEscuela")
        private String desEscuela;

        @JsonProperty("codPlan")
        private String codPlan;

        @JsonProperty("cicloEstudio")
        private String cicloEstudio;

        @JsonProperty("desAsignatura")
        private String desAsignatura;

        @JsonProperty("codSeccion")
        private String codSeccion;

        @JsonProperty("nomDocente")
        private String nomDocente;

        @JsonProperty("apePatDocente")
        private String apePatDocente;

        @JsonProperty("apeMatDocente")
        private String apeMatDocente;
    }
}
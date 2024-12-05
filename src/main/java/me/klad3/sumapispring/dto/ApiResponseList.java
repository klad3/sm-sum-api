package me.klad3.sumapispring.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiResponseList<T> {
    private boolean success;
    private String message;
    private List<T> data;
}

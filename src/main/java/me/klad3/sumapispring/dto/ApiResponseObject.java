package me.klad3.sumapispring.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponseObject<T> {
    private boolean success;
    private String message;
    private T data;
}

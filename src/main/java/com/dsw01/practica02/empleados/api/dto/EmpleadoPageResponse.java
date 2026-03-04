package com.dsw01.practica02.empleados.api.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record EmpleadoPageResponse(
    List<EmpleadoResponse> content,
    int number,
    int size,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last
) {
    public static EmpleadoPageResponse from(Page<EmpleadoResponse> page) {
        return new EmpleadoPageResponse(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isFirst(),
            page.isLast()
        );
    }
}

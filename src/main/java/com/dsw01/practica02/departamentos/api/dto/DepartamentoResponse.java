package com.dsw01.practica02.departamentos.api.dto;

public record DepartamentoResponse(
    Long id,
    String nombre,
    Boolean activo
) {
}

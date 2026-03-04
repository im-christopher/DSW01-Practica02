package com.dsw01.practica02.empleados.api.dto;

public record EmpleadoResponse(
    String clave,
    String nombre,
    String direccion,
    String telefono,
    Long version
) {
}

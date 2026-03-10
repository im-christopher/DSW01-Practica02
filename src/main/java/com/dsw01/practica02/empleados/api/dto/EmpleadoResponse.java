package com.dsw01.practica02.empleados.api.dto;

import com.dsw01.practica02.departamentos.api.dto.DepartamentoSummaryDTO;

public record EmpleadoResponse(
    String clave,
    String nombre,
    String direccion,
    String telefono,
    Long version,
    DepartamentoSummaryDTO departamento
) {
    public EmpleadoResponse(String clave, String nombre, String direccion, String telefono, Long version) {
        this(clave, nombre, direccion, telefono, version, null);
    }
}

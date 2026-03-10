package com.dsw01.practica02.empleados.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EmpleadoCreateRequest(
    @NotNull(message = "nombre es obligatorio")
    @Size(max = 100, message = "nombre debe tener maximo 100 caracteres")
    String nombre,

    @NotNull(message = "direccion es obligatoria")
    @Size(max = 100, message = "direccion debe tener como máximo 100 caracteres")
    String direccion,

    @NotNull(message = "telefono es obligatorio")
    @Size(min = 10, message = "telefono debe tener como mínimo 10 caracteres")
    String telefono,

    Long departamentoId
) {
    public EmpleadoCreateRequest(String nombre, String direccion, String telefono) {
        this(nombre, direccion, telefono, null);
    }
}

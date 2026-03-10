package com.dsw01.practica02.empleados.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EmpleadoUpdateRequest(
    @NotNull(message = "nombre es obligatorio")
    @Size(max = 100, message = "nombre debe tener maximo 100 caracteres")
    String nombre,

    @NotNull(message = "direccion es obligatoria")
    @Size(max = 100, message = "direccion debe tener como máximo 100 caracteres")
    String direccion,

    @NotNull(message = "telefono es obligatorio")
    @Size(min = 10, message = "telefono debe tener exactamente 10 caracteres")
    String telefono,

    @NotNull(message = "version es obligatoria")
    Long version,

    Long departamentoId
) {
    public EmpleadoUpdateRequest(String nombre, String direccion, String telefono, Long version) {
        this(nombre, direccion, telefono, version, null);
    }
}

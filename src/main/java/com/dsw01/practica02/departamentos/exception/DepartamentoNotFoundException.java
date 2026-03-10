package com.dsw01.practica02.departamentos.exception;

import com.dsw01.practica02.common.exception.NotFoundException;

public class DepartamentoNotFoundException extends NotFoundException {

    public DepartamentoNotFoundException(Long id) {
        super("Departamento no encontrado: " + id);
    }
}

package com.dsw01.practica02.departamentos.exception;

import com.dsw01.practica02.common.exception.ConflictException;

public class DepartamentoConEmpleadosException extends ConflictException {

    public DepartamentoConEmpleadosException(Long id) {
        super("No se puede eliminar el departamento porque tiene empleados asignados: " + id);
    }
}

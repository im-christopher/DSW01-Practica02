package com.dsw01.practica02.empleados.service;

import com.dsw01.practica02.common.exception.ConflictException;
import com.dsw01.practica02.empleados.repository.EmpleadoRepository;
import org.springframework.stereotype.Service;

@Service
public class ClaveGeneratorService {

    private static final int MAX_CONSECUTIVO = 999;

    private final EmpleadoRepository empleadoRepository;

    public ClaveGeneratorService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    public synchronized String generarSiguienteClave() {
        for (int numero = 1; numero <= MAX_CONSECUTIVO; numero++) {
            String clave = "E-" + String.format("%03d", numero);
            if (!empleadoRepository.existsById(clave)) {
                return clave;
            }
        }
        throw new ConflictException("Capacidad agotada: se alcanzó E-999");
    }
}

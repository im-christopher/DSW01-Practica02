package com.dsw01.practica02.empleados.service;

import com.dsw01.practica02.common.exception.NotFoundException;
import com.dsw01.practica02.empleados.api.dto.EmpleadoCreateRequest;
import com.dsw01.practica02.empleados.api.dto.EmpleadoPageResponse;
import com.dsw01.practica02.empleados.api.dto.EmpleadoResponse;
import com.dsw01.practica02.empleados.api.dto.EmpleadoUpdateRequest;
import com.dsw01.practica02.empleados.domain.Empleado;
import com.dsw01.practica02.empleados.repository.EmpleadoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.dsw01.practica02.common.exception.ConflictException;

import java.util.Locale;

@Service
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final ClaveGeneratorService claveGeneratorService;

    public EmpleadoService(EmpleadoRepository empleadoRepository, ClaveGeneratorService claveGeneratorService) {
        this.empleadoRepository = empleadoRepository;
        this.claveGeneratorService = claveGeneratorService;
    }

    public EmpleadoResponse crearEmpleado(EmpleadoCreateRequest request) {
        Empleado empleado = new Empleado();
        empleado.setClave(claveGeneratorService.generarSiguienteClave());
        empleado.setNombre(request.nombre());
        empleado.setDireccion(request.direccion());
        empleado.setTelefono(request.telefono());

        Empleado guardado = empleadoRepository.save(empleado);
        return toResponse(guardado);
    }

    public EmpleadoResponse obtenerPorClave(String clave) {
        Empleado empleado = empleadoRepository.findById(clave)
            .orElseThrow(() -> new NotFoundException("Empleado no encontrado"));
        return toResponse(empleado);
    }

    public EmpleadoResponse actualizarEmpleado(String clave, EmpleadoUpdateRequest request) {
        Empleado empleado = empleadoRepository.findById(clave)
            .orElseThrow(() -> new NotFoundException("Empleado no encontrado"));

        if (!empleado.getVersion().equals(request.version())) {
            throw new ConflictException("Conflicto de concurrencia");
        }

        empleado.setNombre(request.nombre());
        empleado.setDireccion(request.direccion());
        empleado.setTelefono(request.telefono());

        Empleado actualizado = empleadoRepository.save(empleado);
        return toResponse(actualizado);
    }

    public void eliminarEmpleado(String clave) {
        if (!empleadoRepository.existsById(clave)) {
            throw new NotFoundException("Empleado no encontrado");
        }
        empleadoRepository.deleteById(clave);
    }

    public EmpleadoPageResponse listarEmpleados(int page, int size, String nombre, String clave, String sort) {
        if (page < 0) {
            throw new IllegalArgumentException("page debe ser mayor o igual a 0");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("size debe ser entre 1 y 100");
        }

        String sortDirection = sort == null ? "asc" : sort.toLowerCase(Locale.ROOT);
        if (!sortDirection.equals("asc") && !sortDirection.equals("desc")) {
            throw new IllegalArgumentException("sort debe ser asc o desc");
        }

        Sort.Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, "clave"));

        Specification<Empleado> spec = Specification.where(null);

        if (nombre != null && !nombre.isBlank()) {
            String nombreLike = "%" + nombre.trim().toLowerCase(Locale.ROOT) + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("nombre")), nombreLike));
        }

        if (clave != null && !clave.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("clave"), clave));
        }

        Page<EmpleadoResponse> responsePage = empleadoRepository.findAll(spec, pageRequest).map(this::toResponse);
        return EmpleadoPageResponse.from(responsePage);
    }

    private EmpleadoResponse toResponse(Empleado empleado) {
        return new EmpleadoResponse(
            empleado.getClave(),
            empleado.getNombre(),
            empleado.getDireccion(),
            empleado.getTelefono(),
            empleado.getVersion()
        );
    }
}

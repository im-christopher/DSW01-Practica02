package com.dsw01.practica02.departamentos.service;

import com.dsw01.practica02.common.exception.ConflictException;
import com.dsw01.practica02.departamentos.api.dto.DepartamentoCreateRequest;
import com.dsw01.practica02.departamentos.api.dto.DepartamentoPageResponse;
import com.dsw01.practica02.departamentos.api.dto.DepartamentoResponse;
import com.dsw01.practica02.departamentos.api.dto.DepartamentoSummaryDTO;
import com.dsw01.practica02.departamentos.api.dto.DepartamentoUpdateRequest;
import com.dsw01.practica02.departamentos.domain.Departamento;
import com.dsw01.practica02.departamentos.exception.DepartamentoConEmpleadosException;
import com.dsw01.practica02.departamentos.exception.DepartamentoNotFoundException;
import com.dsw01.practica02.departamentos.repository.DepartamentoRepository;
import com.dsw01.practica02.empleados.api.dto.EmpleadoPageResponse;
import com.dsw01.practica02.empleados.api.dto.EmpleadoResponse;
import com.dsw01.practica02.empleados.domain.Empleado;
import com.dsw01.practica02.empleados.repository.EmpleadoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class DepartamentoService {

    private final DepartamentoRepository departamentoRepository;
    private final EmpleadoRepository empleadoRepository;

    public DepartamentoService(DepartamentoRepository departamentoRepository, EmpleadoRepository empleadoRepository) {
        this.departamentoRepository = departamentoRepository;
        this.empleadoRepository = empleadoRepository;
    }

    public DepartamentoResponse crearDepartamento(DepartamentoCreateRequest request) {
        if (departamentoRepository.existsByNombreIgnoreCaseAndActivoTrue(request.nombre())) {
            throw new ConflictException("Ya existe un departamento con ese nombre");
        }

        Departamento departamento = new Departamento();
        departamento.setNombre(request.nombre().trim());
        departamento.setActivo(true);

        return toResponse(departamentoRepository.save(departamento));
    }

    public DepartamentoResponse obtenerPorId(Long id) {
        return toResponse(findActivoById(id));
    }

    public DepartamentoPageResponse listarDepartamentos(int page, int size, String sort) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sort) ? Sort.Direction.DESC : Sort.Direction.ASC;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, "nombre"));

        Page<DepartamentoResponse> result = departamentoRepository.findByActivoTrue(pageRequest)
            .map(this::toResponse);
        return DepartamentoPageResponse.from(result);
    }

    public DepartamentoResponse actualizarDepartamento(Long id, DepartamentoUpdateRequest request) {
        Departamento departamento = findActivoById(id);

        if (departamentoRepository.existsByNombreIgnoreCaseAndActivoTrueAndIdNot(request.nombre(), id)) {
            throw new ConflictException("Ya existe un departamento con ese nombre");
        }

        departamento.setNombre(request.nombre().trim());
        return toResponse(departamentoRepository.save(departamento));
    }

    public void eliminarDepartamento(Long id) {
        Departamento departamento = findActivoById(id);

        long empleadosAsignados = empleadoRepository.countByDepartamentoId(id);
        if (empleadosAsignados > 0) {
            throw new DepartamentoConEmpleadosException(id);
        }

        departamento.setActivo(false);
        departamentoRepository.save(departamento);
    }

    public EmpleadoPageResponse listarEmpleadosPorDepartamento(Long departamentoId, int page, int size, String sort) {
        findActivoById(departamentoId);

        Sort.Direction direction = "desc".equalsIgnoreCase(sort) ? Sort.Direction.DESC : Sort.Direction.ASC;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, "clave"));

        Page<EmpleadoResponse> result = empleadoRepository.findByDepartamentoId(departamentoId, pageRequest)
            .map(this::toEmpleadoResponse);

        return EmpleadoPageResponse.from(result);
    }

    public Departamento findActivoById(Long id) {
        return departamentoRepository.findByIdAndActivoTrue(id)
            .orElseThrow(() -> new DepartamentoNotFoundException(id));
    }

    public DepartamentoSummaryDTO toSummary(Departamento departamento) {
        if (departamento == null) {
            return null;
        }
        return new DepartamentoSummaryDTO(departamento.getId(), departamento.getNombre());
    }

    private DepartamentoResponse toResponse(Departamento departamento) {
        return new DepartamentoResponse(departamento.getId(), departamento.getNombre(), departamento.getActivo());
    }

    private EmpleadoResponse toEmpleadoResponse(Empleado empleado) {
        return new EmpleadoResponse(
            empleado.getClave(),
            empleado.getNombre(),
            empleado.getDireccion(),
            empleado.getTelefono(),
            empleado.getVersion(),
            toSummary(empleado.getDepartamento())
        );
    }
}

package com.dsw01.practica02.empleados.api;

import com.dsw01.practica02.empleados.api.dto.EmpleadoCreateRequest;
import com.dsw01.practica02.empleados.api.dto.EmpleadoPageResponse;
import com.dsw01.practica02.empleados.api.dto.EmpleadoResponse;
import com.dsw01.practica02.empleados.api.dto.EmpleadoUpdateRequest;
import com.dsw01.practica02.empleados.service.EmpleadoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @PostMapping
    public ResponseEntity<EmpleadoResponse> crearEmpleado(@Valid @RequestBody EmpleadoCreateRequest request) {
        EmpleadoResponse response = empleadoService.crearEmpleado(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{clave}")
    public ResponseEntity<EmpleadoResponse> obtenerEmpleado(
        @PathVariable
        @Pattern(regexp = "^E-\\d{3}$", message = "clave debe cumplir formato E-001") String clave
    ) {
        EmpleadoResponse response = empleadoService.obtenerPorClave(clave);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<EmpleadoPageResponse> listarEmpleados(
        @RequestParam(defaultValue = "0") @Min(value = 0, message = "page debe ser mayor o igual a 0") int page,
        @RequestParam(defaultValue = "20") @Min(value = 1, message = "size debe ser mayor o igual a 1") @Max(value = 100, message = "size debe ser menor o igual a 100") int size,
        @RequestParam(required = false) String nombre,
        @RequestParam(required = false) @Pattern(regexp = "^E-\\d{3}$", message = "clave debe cumplir formato E-001") String clave,
        @RequestParam(defaultValue = "asc") @Pattern(regexp = "^(asc|desc)$", message = "sort debe ser asc o desc") String sort,
        @RequestParam(required = false) Long departamentoId
    ) {
        EmpleadoPageResponse response = departamentoId == null
            ? empleadoService.listarEmpleados(page, size, nombre, clave, sort)
            : empleadoService.listarEmpleados(page, size, nombre, clave, sort, departamentoId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{clave}")
    public ResponseEntity<EmpleadoResponse> actualizarEmpleado(
        @PathVariable
        @Pattern(regexp = "^E-\\d{3}$", message = "clave debe cumplir formato E-001") String clave,
        @Valid @RequestBody EmpleadoUpdateRequest request
    ) {
        EmpleadoResponse response = empleadoService.actualizarEmpleado(clave, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{clave}")
    public ResponseEntity<Void> eliminarEmpleado(
        @PathVariable
        @Pattern(regexp = "^E-\\d{3}$", message = "clave debe cumplir formato E-001") String clave
    ) {
        empleadoService.eliminarEmpleado(clave);
        return ResponseEntity.noContent().build();
    }
}

package com.dsw01.practica02.departamentos.api;

import com.dsw01.practica02.departamentos.api.dto.DepartamentoCreateRequest;
import com.dsw01.practica02.departamentos.api.dto.DepartamentoPageResponse;
import com.dsw01.practica02.departamentos.api.dto.DepartamentoResponse;
import com.dsw01.practica02.departamentos.api.dto.DepartamentoUpdateRequest;
import com.dsw01.practica02.departamentos.service.DepartamentoService;
import com.dsw01.practica02.empleados.api.dto.EmpleadoPageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/departamentos")
public class DepartamentoController {

    private final DepartamentoService departamentoService;

    public DepartamentoController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    @PostMapping
    public ResponseEntity<DepartamentoResponse> crear(@Valid @RequestBody DepartamentoCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(departamentoService.crearDepartamento(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartamentoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(departamentoService.obtenerPorId(id));
    }

    @GetMapping
    public ResponseEntity<DepartamentoPageResponse> listar(
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
        @RequestParam(defaultValue = "asc") String sort
    ) {
        return ResponseEntity.ok(departamentoService.listarDepartamentos(page, size, sort));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartamentoResponse> actualizar(
        @PathVariable Long id,
        @Valid @RequestBody DepartamentoUpdateRequest request
    ) {
        return ResponseEntity.ok(departamentoService.actualizarDepartamento(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        departamentoService.eliminarDepartamento(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/empleados")
    public ResponseEntity<EmpleadoPageResponse> listarEmpleadosPorDepartamento(
        @PathVariable Long id,
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
        @RequestParam(defaultValue = "asc") String sort
    ) {
        return ResponseEntity.ok(departamentoService.listarEmpleadosPorDepartamento(id, page, size, sort));
    }
}

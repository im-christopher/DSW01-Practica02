package com.dsw01.practica02.empleados.repository;

import com.dsw01.practica02.empleados.domain.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface EmpleadoRepository extends JpaRepository<Empleado, String>, JpaSpecificationExecutor<Empleado> {
	Optional<Empleado> findByClaveAndActivoTrue(String clave);
}

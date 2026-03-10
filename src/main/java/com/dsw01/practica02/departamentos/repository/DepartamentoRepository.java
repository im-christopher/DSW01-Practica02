package com.dsw01.practica02.departamentos.repository;

import com.dsw01.practica02.departamentos.domain.Departamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {

    Optional<Departamento> findByIdAndActivoTrue(Long id);

    Page<Departamento> findByActivoTrue(Pageable pageable);

    boolean existsByNombreIgnoreCaseAndActivoTrue(String nombre);

    boolean existsByNombreIgnoreCaseAndActivoTrueAndIdNot(String nombre, Long id);
}

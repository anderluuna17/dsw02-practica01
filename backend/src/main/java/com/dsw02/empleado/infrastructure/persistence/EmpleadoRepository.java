package com.dsw02.empleado.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpleadoRepository extends JpaRepository<EmpleadoEntity, EmpleadoId> {
}

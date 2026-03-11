package com.dsw02.empleado.application;

import com.dsw02.empleado.api.dto.EmpleadoCreateRequest;
import com.dsw02.empleado.domain.ClaveParser;
import com.dsw02.empleado.domain.Empleado;
import com.dsw02.empleado.infrastructure.persistence.ConsecutivoRepository;
import com.dsw02.empleado.infrastructure.persistence.EmpleadoEntity;
import com.dsw02.empleado.infrastructure.persistence.EmpleadoId;
import com.dsw02.empleado.infrastructure.persistence.EmpleadoRepository;
import org.springframework.stereotype.Service;

@Service
public class CrearEmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final ConsecutivoRepository consecutivoRepository;
    private final ClaveParser claveParser;

    public CrearEmpleadoService(
        EmpleadoRepository empleadoRepository,
        ConsecutivoRepository consecutivoRepository,
        ClaveParser claveParser
    ) {
        this.empleadoRepository = empleadoRepository;
        this.consecutivoRepository = consecutivoRepository;
        this.claveParser = claveParser;
    }

    public Empleado crear(EmpleadoCreateRequest request) {
        // Ignore client-provided clave; authoritative value is always generated server-side.

        long consecutivo = consecutivoRepository.siguienteConsecutivo();
        EmpleadoId id = new EmpleadoId(ClaveParser.PREFIJO, consecutivo);

        EmpleadoEntity entity = new EmpleadoEntity(id, request.getNombre(), request.getDireccion(), request.getTelefono());
        EmpleadoEntity saved = empleadoRepository.save(entity);

        return new Empleado(
            claveParser.buildClave(saved.getId().getConsecutivo()),
            saved.getNombre(),
            saved.getDireccion(),
            saved.getTelefono()
        );
    }
}

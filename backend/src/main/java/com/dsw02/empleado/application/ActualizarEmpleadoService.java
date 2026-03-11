package com.dsw02.empleado.application;

import com.dsw02.empleado.api.dto.EmpleadoUpdateRequest;
import com.dsw02.empleado.domain.ClaveParser;
import com.dsw02.empleado.domain.Empleado;
import com.dsw02.empleado.domain.exception.ClaveNoEditableException;
import com.dsw02.empleado.domain.exception.NotFoundException;
import com.dsw02.empleado.infrastructure.persistence.EmpleadoEntity;
import com.dsw02.empleado.infrastructure.persistence.EmpleadoId;
import com.dsw02.empleado.infrastructure.persistence.EmpleadoRepository;
import org.springframework.stereotype.Service;

@Service
public class ActualizarEmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final ClaveParser claveParser;

    public ActualizarEmpleadoService(EmpleadoRepository empleadoRepository, ClaveParser claveParser) {
        this.empleadoRepository = empleadoRepository;
        this.claveParser = claveParser;
    }

    public Empleado actualizar(String clave, EmpleadoUpdateRequest request) {
        if (request.getClave() != null && !request.getClave().isBlank()) {
            throw new ClaveNoEditableException("La clave no puede modificarse");
        }

        long consecutivo = claveParser.parseConsecutivo(clave);
        EmpleadoId id = new EmpleadoId(ClaveParser.PREFIJO, consecutivo);

        EmpleadoEntity entity = empleadoRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Empleado no encontrado para clave: " + clave));

        entity.setNombre(request.getNombre());
        entity.setDireccion(request.getDireccion());
        entity.setTelefono(request.getTelefono());

        EmpleadoEntity saved = empleadoRepository.save(entity);

        return new Empleado(
            claveParser.buildClave(saved.getId().getConsecutivo()),
            saved.getNombre(),
            saved.getDireccion(),
            saved.getTelefono()
        );
    }
}

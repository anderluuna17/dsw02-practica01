package com.dsw02.empleado.application;

import com.dsw02.empleado.domain.ClaveParser;
import com.dsw02.empleado.domain.Empleado;
import com.dsw02.empleado.infrastructure.persistence.EmpleadoEntity;
import com.dsw02.empleado.infrastructure.persistence.EmpleadoRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ListarEmpleadosService {

    private final EmpleadoRepository empleadoRepository;
    private final ClaveParser claveParser;

    public ListarEmpleadosService(EmpleadoRepository empleadoRepository, ClaveParser claveParser) {
        this.empleadoRepository = empleadoRepository;
        this.claveParser = claveParser;
    }

    public List<Empleado> listar() {
        return empleadoRepository.findAll()
            .stream()
            .map(this::toDomain)
            .toList();
    }

    private Empleado toDomain(EmpleadoEntity entity) {
        return new Empleado(
            claveParser.buildClave(entity.getId().getConsecutivo()),
            entity.getNombre(),
            entity.getDireccion(),
            entity.getTelefono()
        );
    }
}

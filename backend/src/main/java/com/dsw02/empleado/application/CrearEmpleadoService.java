package com.dsw02.empleado.application;

import com.dsw02.empleado.api.dto.EmpleadoCreateRequest;
import com.dsw02.empleado.domain.ClaveParser;
import com.dsw02.empleado.domain.CorreoNormalizer;
import com.dsw02.empleado.domain.Empleado;
import com.dsw02.empleado.domain.ErrorCode;
import com.dsw02.empleado.domain.exception.BusinessException;
import com.dsw02.empleado.infrastructure.persistence.ConsecutivoRepository;
import com.dsw02.empleado.infrastructure.persistence.EmpleadoEntity;
import com.dsw02.empleado.infrastructure.persistence.EmpleadoId;
import com.dsw02.empleado.infrastructure.persistence.EmpleadoRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CrearEmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final ConsecutivoRepository consecutivoRepository;
    private final ClaveParser claveParser;
    private final CorreoNormalizer correoNormalizer;
    private final PasswordEncoder passwordEncoder;

    public CrearEmpleadoService(
        EmpleadoRepository empleadoRepository,
        ConsecutivoRepository consecutivoRepository,
        ClaveParser claveParser,
        CorreoNormalizer correoNormalizer,
        PasswordEncoder passwordEncoder
    ) {
        this.empleadoRepository = empleadoRepository;
        this.consecutivoRepository = consecutivoRepository;
        this.claveParser = claveParser;
        this.correoNormalizer = correoNormalizer;
        this.passwordEncoder = passwordEncoder;
    }

    public Empleado crear(EmpleadoCreateRequest request) {
        // Ignore client-provided clave; authoritative value is always generated server-side.
        if (request.getDepartamentoClave() != null && !request.getDepartamentoClave().isBlank()) {
            throw new BusinessException(ErrorCode.VALIDACION, "departamentoClave no se permite en el alta inicial de empleado");
        }

        long consecutivo = consecutivoRepository.siguienteConsecutivo();
        EmpleadoId id = new EmpleadoId(ClaveParser.PREFIJO, consecutivo);

        String correoNormalizado = correoNormalizer.normalize(request.getCorreo());
        if (empleadoRepository.existsByCorreoIgnoreCase(correoNormalizado)) {
            throw new BusinessException(ErrorCode.VALIDACION, "El correo ya esta registrado");
        }

        EmpleadoEntity entity = new EmpleadoEntity(
            id,
            request.getNombre(),
            request.getDireccion(),
            request.getTelefono(),
            correoNormalizado,
            passwordEncoder.encode(request.getContrasena()),
            true,
            "DEP-0000"
        );
        EmpleadoEntity saved = empleadoRepository.save(entity);

        return new Empleado(
            claveParser.buildClave(saved.getId().getConsecutivo()),
            saved.getNombre(),
            saved.getDireccion(),
            saved.getTelefono(),
            saved.getDepartamentoClave(),
            saved.getCorreo(),
            saved.isActivo()
        );
    }
}

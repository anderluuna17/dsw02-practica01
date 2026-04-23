package com.dsw02.empleado.infrastructure.security;

import com.dsw02.empleado.application.RegistrarEventoAutenticacionService;
import com.dsw02.empleado.domain.CorreoNormalizer;
import com.dsw02.empleado.infrastructure.persistence.EmpleadoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class AuthenticationAuditListener {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationAuditListener.class);

    private final RegistrarEventoAutenticacionService registrarEventoAutenticacionService;
    private final CorreoNormalizer correoNormalizer;
    private final EmpleadoRepository empleadoRepository;

    public AuthenticationAuditListener(
        RegistrarEventoAutenticacionService registrarEventoAutenticacionService,
        CorreoNormalizer correoNormalizer,
        EmpleadoRepository empleadoRepository
    ) {
        this.registrarEventoAutenticacionService = registrarEventoAutenticacionService;
        this.correoNormalizer = correoNormalizer;
        this.empleadoRepository = empleadoRepository;
    }

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        try {
            registrarEventoAutenticacionService.registrar(
                correoNormalizer.normalize(event.getAuthentication().getName()),
                "EXITO",
                "AUTENTICACION_OK",
                origenSolicitud()
            );
        } catch (RuntimeException ex) {
            LOG.warn("No se pudo registrar auditoria de autenticacion exitosa", ex);
        }
    }

    @EventListener
    public void onAuthenticationFailure(AbstractAuthenticationFailureEvent event) {
        try {
            String correo = null;
            if (event.getAuthentication() != null && event.getAuthentication().getName() != null) {
                correo = correoNormalizer.normalize(event.getAuthentication().getName());
            }

            String motivo = "CREDENCIALES_INVALIDAS";
            if (event.getException() instanceof DisabledException) {
                motivo = "CUENTA_INACTIVA";
            } else if (correo != null) {
                boolean cuentaInactiva = empleadoRepository.findByCorreoIgnoreCase(correo)
                    .map(entity -> !entity.isActivo())
                    .orElse(false);
                if (cuentaInactiva) {
                    motivo = "CUENTA_INACTIVA";
                }
            }

            registrarEventoAutenticacionService.registrar(
                correo,
                "FALLO",
                motivo,
                origenSolicitud()
            );
        } catch (RuntimeException ex) {
            LOG.warn("No se pudo registrar auditoria de autenticacion fallida", ex);
        }
    }

    private String origenSolicitud() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs) {
            return attrs.getRequest().getRemoteAddr();
        }
        return "UNKNOWN";
    }
}

package com.dsw02.empleado.infrastructure.security;

import com.dsw02.empleado.application.RegistrarEventoAutenticacionService;
import com.dsw02.empleado.domain.CorreoNormalizer;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class AuthenticationAuditListener {

    private final RegistrarEventoAutenticacionService registrarEventoAutenticacionService;
    private final CorreoNormalizer correoNormalizer;

    public AuthenticationAuditListener(
        RegistrarEventoAutenticacionService registrarEventoAutenticacionService,
        CorreoNormalizer correoNormalizer
    ) {
        this.registrarEventoAutenticacionService = registrarEventoAutenticacionService;
        this.correoNormalizer = correoNormalizer;
    }

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        registrarEventoAutenticacionService.registrar(
            correoNormalizer.normalize(event.getAuthentication().getName()),
            "EXITO",
            "AUTENTICACION_OK",
            origenSolicitud()
        );
    }

    @EventListener
    public void onAuthenticationFailure(AbstractAuthenticationFailureEvent event) {
        String correo = null;
        if (event.getAuthentication() != null && event.getAuthentication().getName() != null) {
            correo = correoNormalizer.normalize(event.getAuthentication().getName());
        }

        registrarEventoAutenticacionService.registrar(
            correo,
            "FALLO",
            "CREDENCIALES_INVALIDAS",
            origenSolicitud()
        );
    }

    private String origenSolicitud() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs) {
            return attrs.getRequest().getRemoteAddr();
        }
        return "UNKNOWN";
    }
}

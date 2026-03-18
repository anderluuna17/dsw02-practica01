package com.dsw02.empleado.api.dto;

import com.dsw02.empleado.domain.Empleado;

public record EmpleadoAuthProfileResponse(
    String clave,
    String correo,
    String nombre,
    String direccion,
    String telefono,
    String departamentoClave,
    boolean activo
) {

    public static EmpleadoAuthProfileResponse fromDomain(Empleado empleado) {
        return new EmpleadoAuthProfileResponse(
            empleado.clave(),
            empleado.correo(),
            empleado.nombre(),
            empleado.direccion(),
            empleado.telefono(),
            empleado.departamentoClave(),
            empleado.activo()
        );
    }
}

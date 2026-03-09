package com.dsw02.empleado.api;

import com.dsw02.empleado.api.dto.EmpleadoCreateRequest;
import com.dsw02.empleado.api.dto.EmpleadoResponse;
import com.dsw02.empleado.api.dto.EmpleadoUpdateRequest;
import com.dsw02.empleado.application.ActualizarEmpleadoService;
import com.dsw02.empleado.application.CrearEmpleadoService;
import com.dsw02.empleado.application.EliminarEmpleadoService;
import com.dsw02.empleado.application.ListarEmpleadosService;
import com.dsw02.empleado.application.ObtenerEmpleadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/empleados")
@Tag(name = "Empleados")
public class EmpleadoController {

    private final CrearEmpleadoService crearEmpleadoService;
    private final ObtenerEmpleadoService obtenerEmpleadoService;
    private final ListarEmpleadosService listarEmpleadosService;
    private final ActualizarEmpleadoService actualizarEmpleadoService;
    private final EliminarEmpleadoService eliminarEmpleadoService;

    public EmpleadoController(
        CrearEmpleadoService crearEmpleadoService,
        ObtenerEmpleadoService obtenerEmpleadoService,
        ListarEmpleadosService listarEmpleadosService,
        ActualizarEmpleadoService actualizarEmpleadoService,
        EliminarEmpleadoService eliminarEmpleadoService
    ) {
        this.crearEmpleadoService = crearEmpleadoService;
        this.obtenerEmpleadoService = obtenerEmpleadoService;
        this.listarEmpleadosService = listarEmpleadosService;
        this.actualizarEmpleadoService = actualizarEmpleadoService;
        this.eliminarEmpleadoService = eliminarEmpleadoService;
    }

    @PostMapping
    @Operation(summary = "Crear empleado")
    public ResponseEntity<EmpleadoResponse> crear(@Valid @RequestBody EmpleadoCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(EmpleadoResponse.fromDomain(crearEmpleadoService.crear(request)));
    }

    @GetMapping("/{clave}")
    @Operation(summary = "Consultar empleado por clave")
    public EmpleadoResponse obtener(@PathVariable String clave) {
        return EmpleadoResponse.fromDomain(obtenerEmpleadoService.obtenerPorClave(clave));
    }

    @GetMapping
    @Operation(summary = "Listar empleados")
    public List<EmpleadoResponse> listar() {
        return listarEmpleadosService.listar().stream().map(EmpleadoResponse::fromDomain).toList();
    }

    @PutMapping("/{clave}")
    @Operation(summary = "Actualizar empleado")
    public EmpleadoResponse actualizar(@PathVariable String clave, @Valid @RequestBody EmpleadoUpdateRequest request) {
        return EmpleadoResponse.fromDomain(actualizarEmpleadoService.actualizar(clave, request));
    }

    @DeleteMapping("/{clave}")
    @Operation(summary = "Eliminar empleado")
    public ResponseEntity<Void> eliminar(@PathVariable String clave) {
        eliminarEmpleadoService.eliminar(clave);
        return ResponseEntity.noContent().build();
    }
}

package com.dsw02.empleado.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest extends PostgresIntegrationBase {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRequireAuthenticationOnBusinessEndpoints() throws Exception {
        mockMvc.perform(get("/api/v1/empleados"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("AUTH_INVALIDA"));
    }

    @Test
    void shouldAllowPublicSwaggerAndHealth() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
            .andExpect(status().is3xxRedirection());

        mockMvc.perform(get("/actuator/health"))
            .andExpect(status().isOk());
    }

    @Test
    void shouldForbidPatchEstadoForNonAdminActor() throws Exception {
        String createPayload = """
            {
              "nombre":"Demo",
              "direccion":"Calle Demo 123",
              "telefono":"5551112222",
              "correo":"demo@empresa.com",
              "contrasena":"MiPassword123"
            }
            """;

        mockMvc.perform(post("/api/v1/empleados")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(createPayload))
            .andExpect(status().isCreated());

        String patchPayload = """
            {
              "activo": false
            }
            """;

        mockMvc.perform(patch("/api/v1/empleados/EMP-1001/estado")
                .with(httpBasic("demo@empresa.com", "MiPassword123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(patchPayload))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value("NO_AUTORIZADO"));

        mockMvc.perform(get("/api/v1/empleados")
                .with(httpBasic("demo@empresa.com", "MiPassword123")))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.code").value("NO_AUTORIZADO"));
    }

    @Test
    void shouldNotExposeUnversionedListEndpoints() throws Exception {
        mockMvc.perform(get("/empleados")
                .with(httpBasic("admin", "admin123")))
            .andExpect(status().isNotFound());

        mockMvc.perform(get("/departamentos")
                .with(httpBasic("admin", "admin123")))
            .andExpect(status().isNotFound());

        mockMvc.perform(get("/departamentos/DEP-0000/empleados")
                .with(httpBasic("admin", "admin123")))
            .andExpect(status().isNotFound());
    }
}

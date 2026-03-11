package com.dsw02.empleado.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.dsw02.empleado.infrastructure.persistence.EmpleadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class EmpleadoCrudIntegrationTest extends PostgresIntegrationBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @BeforeEach
    void cleanDatabase() {
        empleadoRepository.deleteAll();
    }

    @Test
    void shouldCreateGetListUpdateAndDeleteEmpleado() throws Exception {
        String createPayload = """
            {
              "nombre":"Juan Pérez",
              "direccion":"Calle Uno 123",
              "telefono":"5551234567"
            }
            """;

        MvcResult createResult = mockMvc.perform(post("/api/v1/empleados")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(createPayload))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.clave").value(org.hamcrest.Matchers.matchesPattern("EMP-[0-9]+")))
            .andExpect(jsonPath("$.nombre").value("Juan Pérez"))
            .andReturn();

        JsonNode created = objectMapper.readTree(createResult.getResponse().getContentAsString());
        String clave = created.get("clave").asText();

        mockMvc.perform(get("/api/v1/empleados/{clave}", clave)
                .with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.clave").value(clave));

        mockMvc.perform(get("/api/v1/empleados")
                .with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].clave").value(clave))
            .andExpect(jsonPath("$.size").value(5));

        String updatePayload = """
            {
              "nombre":"Juan Actualizado",
              "direccion":"Av Principal 45",
              "telefono":"5557654321"
            }
            """;

        mockMvc.perform(put("/api/v1/empleados/{clave}", clave)
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatePayload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("Juan Actualizado"));

        mockMvc.perform(delete("/api/v1/empleados/{clave}", clave)
                .with(httpBasic("admin", "admin123")))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/empleados/{clave}", clave)
                .with(httpBasic("admin", "admin123")))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldIgnoreClaveInCreatePayload() throws Exception {
        String payload = """
            {
              "clave":"EMP-9999",
              "nombre":"Ana",
              "direccion":"Centro 1",
              "telefono":"5550000000"
            }
            """;

        mockMvc.perform(post("/api/v1/empleados")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.clave").value(org.hamcrest.Matchers.not("EMP-9999")))
            .andExpect(jsonPath("$.clave").value(org.hamcrest.Matchers.matchesPattern("EMP-[0-9]+")));
    }
}

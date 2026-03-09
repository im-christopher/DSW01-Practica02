package com.dsw01.practica02.empleados.contract;

import com.dsw01.practica02.common.GlobalExceptionHandler;
import com.dsw01.practica02.config.SecurityConfig;
import com.dsw01.practica02.empleados.api.EmpleadoController;
import com.dsw01.practica02.empleados.api.dto.EmpleadoCreateRequest;
import com.dsw01.practica02.empleados.api.dto.EmpleadoResponse;
import com.dsw01.practica02.empleados.service.EmpleadoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmpleadoController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
@TestPropertySource(properties = {
    "app.security.user=admin",
    "app.security.password=admin123"
})
class EmpleadoControllerContractTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmpleadoService empleadoService;

    @Test
    void shouldCreateEmpleadoAndReturn201() throws Exception {
        EmpleadoResponse response = new EmpleadoResponse(
            "E-001",
            "N".repeat(100),
            "D".repeat(100),
            "T".repeat(100),
            0L
        );

        when(empleadoService.crearEmpleado(any(EmpleadoCreateRequest.class))).thenReturn(response);

        EmpleadoCreateRequest request = new EmpleadoCreateRequest(
            "N".repeat(100),
            "D".repeat(100),
            "T".repeat(100)
        );

        mockMvc.perform(post("/api/v1/empleados")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.clave").value("E-001"))
            .andExpect(jsonPath("$.version").value(0));
    }

    @Test
    void shouldGetEmpleadoAndReturn200() throws Exception {
        EmpleadoResponse response = new EmpleadoResponse(
            "E-001",
            "N".repeat(100),
            "D".repeat(100),
            "T".repeat(100),
            0L
        );

        when(empleadoService.obtenerPorClave(eq("E-001"))).thenReturn(response);

        mockMvc.perform(get("/api/v1/empleados/E-001")
                .with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.clave").value("E-001"));
    }
}

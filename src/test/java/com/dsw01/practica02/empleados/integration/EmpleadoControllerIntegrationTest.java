package com.dsw01.practica02.empleados.integration;

import com.dsw01.practica02.common.GlobalExceptionHandler;
import com.dsw01.practica02.config.SecurityConfig;
import com.dsw01.practica02.common.exception.NotFoundException;
import com.dsw01.practica02.empleados.api.EmpleadoController;
import com.dsw01.practica02.empleados.service.EmpleadoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

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
class EmpleadoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmpleadoService empleadoService;

    @Test
    void shouldRejectPostWhenPayloadContainsClave() throws Exception {
        String payload = """
            {
              \"clave\": \"E-001\",
              \"nombre\": \"%s\",
              \"direccion\": \"%s\",
              \"telefono\": \"%s\"
            }
            """.formatted("N".repeat(100), "D".repeat(100), "T".repeat(100));

        mockMvc.perform(post("/api/empleados")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenEmpleadoNotFound() throws Exception {
        when(empleadoService.obtenerPorClave(eq("E-999"))).thenThrow(new NotFoundException("Empleado no encontrado"));

        mockMvc.perform(get("/api/empleados/E-999")
                .with(httpBasic("admin", "admin123")))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void shouldReturn400WhenClavePatternInvalid() throws Exception {
        mockMvc.perform(get("/api/empleados/ABC123")
                .with(httpBasic("admin", "admin123")))
            .andExpect(status().isBadRequest());
    }
}

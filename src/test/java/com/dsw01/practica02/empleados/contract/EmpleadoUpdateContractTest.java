package com.dsw01.practica02.empleados.contract;

import com.dsw01.practica02.common.GlobalExceptionHandler;
import com.dsw01.practica02.config.SecurityConfig;
import com.dsw01.practica02.empleados.api.EmpleadoController;
import com.dsw01.practica02.empleados.api.dto.EmpleadoResponse;
import com.dsw01.practica02.empleados.service.EmpleadoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmpleadoController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
@TestPropertySource(properties = {
    "app.security.user=admin",
    "app.security.password=admin123"
})
class EmpleadoUpdateContractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmpleadoService empleadoService;

    @Test
    void shouldUpdateEmpleadoAndReturn200() throws Exception {
        EmpleadoResponse response = new EmpleadoResponse(
            "E-001",
            "N".repeat(100),
            "D".repeat(100),
            "T".repeat(100),
            1L
        );
        when(empleadoService.actualizarEmpleado(any(), any())).thenReturn(response);

        String payload = """
            {
              \"nombre\": \"%s\",
              \"direccion\": \"%s\",
              \"telefono\": \"%s\",
              \"version\": 0
            }
            """.formatted("N".repeat(100), "D".repeat(100), "T".repeat(100));

        mockMvc.perform(put("/api/v1/empleados/E-001")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.version").value(1));
    }
}

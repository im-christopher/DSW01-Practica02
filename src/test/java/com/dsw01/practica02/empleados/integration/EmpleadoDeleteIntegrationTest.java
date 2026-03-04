package com.dsw01.practica02.empleados.integration;

import com.dsw01.practica02.common.GlobalExceptionHandler;
import com.dsw01.practica02.common.exception.NotFoundException;
import com.dsw01.practica02.config.SecurityConfig;
import com.dsw01.practica02.empleados.api.EmpleadoController;
import com.dsw01.practica02.empleados.service.EmpleadoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmpleadoController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
@TestPropertySource(properties = {
    "app.security.user=admin",
    "app.security.password=admin123"
})
class EmpleadoDeleteIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmpleadoService empleadoService;

    @Test
    void shouldReturn404WhenDeletingMissingEmpleado() throws Exception {
        doThrow(new NotFoundException("Empleado no encontrado"))
            .when(empleadoService).eliminarEmpleado("E-999");

        mockMvc.perform(delete("/api/empleados/E-999")
                .with(httpBasic("admin", "admin123")))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }
}

package com.dsw01.practica02.empleados.integration;

import com.dsw01.practica02.common.GlobalExceptionHandler;
import com.dsw01.practica02.config.SecurityConfig;
import com.dsw01.practica02.empleados.api.EmpleadoController;
import com.dsw01.practica02.empleados.api.dto.EmpleadoPageResponse;
import com.dsw01.practica02.empleados.api.dto.EmpleadoResponse;
import com.dsw01.practica02.empleados.service.EmpleadoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmpleadoController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
@TestPropertySource(properties = {
    "app.security.user=admin",
    "app.security.password=admin123"
})
class EmpleadoListIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmpleadoService empleadoService;

    @Test
    void shouldUseDefaultPaginationWhenParamsMissing() throws Exception {
        EmpleadoPageResponse emptyPage = new EmpleadoPageResponse(List.<EmpleadoResponse>of(), 0, 20, 0, 0, true, true);
        when(empleadoService.listarEmpleados(anyInt(), anyInt(), isNull(), isNull(), anyString()))
            .thenReturn(emptyPage);

        mockMvc.perform(get("/api/empleados")
                .with(httpBasic("admin", "admin123")))
            .andExpect(status().isOk());

        verify(empleadoService).listarEmpleados(0, 20, null, null, "asc");
    }

    @Test
    void shouldReturn400WhenSizeGreaterThan100() throws Exception {
        mockMvc.perform(get("/api/empleados")
                .with(httpBasic("admin", "admin123"))
                .param("size", "101"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenSortInvalid() throws Exception {
        mockMvc.perform(get("/api/empleados")
                .with(httpBasic("admin", "admin123"))
                .param("sort", "up"))
            .andExpect(status().isBadRequest());
    }
}

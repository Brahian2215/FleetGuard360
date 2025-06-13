package com.fleetguard360.adminpanel.controller;

import com.fleetguard360.adminpanel.model.DashboardMetric;
import com.fleetguard360.adminpanel.payload.response.DashboardSummaryResponse;
import com.fleetguard360.adminpanel.repository.DashboardMetricRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardMetricRepository metricRepository;

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getAllMetrics() throws Exception {
        DashboardMetric metric = new DashboardMetric();
        when(metricRepository.findAll()).thenReturn(List.of(metric));

        mockMvc.perform(get("/api/dashboard/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getMetricsByType() throws Exception {
        DashboardMetric metric = new DashboardMetric();
        when(metricRepository.findByMetricType("SPEED")).thenReturn(List.of(metric));

        mockMvc.perform(get("/api/dashboard/metrics/type/SPEED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getMetricsByDateRange() throws Exception {
        DashboardMetric metric = new DashboardMetric();
        when(metricRepository.findByTimestampBetween(Mockito.any(), Mockito.any())).thenReturn(List.of(metric));

        mockMvc.perform(get("/api/dashboard/metrics/date-range")
                        .param("startDate", "2025-01-01T00:00:00")
                        .param("endDate", "2025-12-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getMetricsByVehicle() throws Exception {
        DashboardMetric metric = new DashboardMetric();
        when(metricRepository.findByVehicleId(1L)).thenReturn(List.of(metric));

        mockMvc.perform(get("/api/dashboard/metrics/vehicle/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getMetricsByDriver() throws Exception {
        DashboardMetric metric = new DashboardMetric();
        when(metricRepository.findByDriverId(1L)).thenReturn(List.of(metric));

        mockMvc.perform(get("/api/dashboard/metrics/driver/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getDashboardSummary() throws Exception {
        DashboardMetric speed = new DashboardMetric();
        DashboardMetric location = new DashboardMetric();
        DashboardMetric alert = new DashboardMetric();
        DashboardMetric rotation = new DashboardMetric();

        when(metricRepository.findLatestByMetricType("SPEED")).thenReturn(speed);
        when(metricRepository.findLatestByMetricType("LOCATION")).thenReturn(location);
        when(metricRepository.findLatestByMetricType("ALERT")).thenReturn(alert);
        when(metricRepository.findLatestByMetricType("ROTATION")).thenReturn(rotation);

        mockMvc.perform(get("/api/dashboard/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.latestMetrics").exists());
    }
}

package com.fleetguard360.adminpanel.controller;

import com.fleetguard360.adminpanel.model.DashboardMetric;
import com.fleetguard360.adminpanel.payload.response.DashboardSummaryResponse;
import com.fleetguard360.adminpanel.repository.DashboardMetricRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

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
    @DisplayName("Debe devolver todas las métricas")
    void getAllMetrics() throws Exception {
        DashboardMetric mockMetric = new DashboardMetric();
        when(metricRepository.findAll()).thenReturn(List.of(mockMetric));

        mockMvc.perform(get("/api/dashboard/metrics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("Debe devolver métricas por tipo")
    void getMetricsByType() throws Exception {
        DashboardMetric mockMetric = new DashboardMetric();
        when(metricRepository.findByMetricType("SPEED")).thenReturn(List.of(mockMetric));

        mockMvc.perform(get("/api/dashboard/metrics/type/SPEED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("Debe devolver métricas por rango de fechas")
    void getMetricsByDateRange() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        DashboardMetric mockMetric = new DashboardMetric();
        when(metricRepository.findByTimestampBetween(now.minusDays(1), now)).thenReturn(List.of(mockMetric));

        mockMvc.perform(get("/api/dashboard/metrics/date-range")
                        .param("startDate", now.minusDays(1).toString())
                        .param("endDate", now.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("Debe devolver métricas por vehículo")
    void getMetricsByVehicle() throws Exception {
        DashboardMetric mockMetric = new DashboardMetric();
        when(metricRepository.findByVehicleId(1L)).thenReturn(List.of(mockMetric));

        mockMvc.perform(get("/api/dashboard/metrics/vehicle/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("Debe devolver métricas por conductor")
    void getMetricsByDriver() throws Exception {
        DashboardMetric mockMetric = new DashboardMetric();
        when(metricRepository.findByDriverId(1L)).thenReturn(List.of(mockMetric));

        mockMvc.perform(get("/api/dashboard/metrics/driver/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("Debe devolver el resumen del dashboard")
    void getDashboardSummary() throws Exception {
        DashboardMetric mockMetric = new DashboardMetric();
        when(metricRepository.findLatestByMetricType("SPEED")).thenReturn(mockMetric);
        when(metricRepository.findLatestByMetricType("LOCATION")).thenReturn(mockMetric);
        when(metricRepository.findLatestByMetricType("ALERT")).thenReturn(mockMetric);
        when(metricRepository.findLatestByMetricType("ROTATION")).thenReturn(mockMetric);

        mockMvc.perform(get("/api/dashboard/summary")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.latestMetrics.speed").exists())
                .andExpect(jsonPath("$.latestMetrics.location").exists())
                .andExpect(jsonPath("$.latestMetrics.alert").exists())
                .andExpect(jsonPath("$.latestMetrics.rotation").exists());
    }
}

package com.fleetguard360.adminpanel.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(description = "Respuesta con el resumen de métricas para el dashboard")
public class DashboardSummaryResponse {
    @Schema(description = "Métricas más recientes por tipo")
    private Map<String, Object> latestMetrics;
    // Add more summary fields as needed


    public Map<String, Object> getLatestMetrics() {
        return latestMetrics;
    }

    public void setLatestMetrics(Map<String, Object> latestMetrics) {
        this.latestMetrics = latestMetrics;
    }
}

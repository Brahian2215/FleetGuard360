package com.fleetguard360.adminpanel.controller;

import com.fleetguard360.adminpanel.model.DashboardMetric;
import com.fleetguard360.adminpanel.payload.response.DashboardSummaryResponse;
import com.fleetguard360.adminpanel.repository.DashboardMetricRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "API para el panel de control y métricas en tiempo real")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {
    @Autowired
    DashboardMetricRepository metricRepository;

    @GetMapping("/metrics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR') or hasRole('VIEWER')")
    @Operation(summary = "Obtener todas las métricas", description = "Retorna una lista de todas las métricas disponibles")
    @ApiResponse(responseCode = "200", description = "Lista de métricas obtenida exitosamente")
    public ResponseEntity<List<DashboardMetric>> getAllMetrics() {
        List<DashboardMetric> metrics = metricRepository.findAll();
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/metrics/type/{metricType}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR') or hasRole('VIEWER')")
    @Operation(summary = "Obtener métricas por tipo", description = "Retorna una lista de métricas filtradas por tipo")
    @ApiResponse(responseCode = "200", description = "Lista de métricas obtenida exitosamente")
    public ResponseEntity<List<DashboardMetric>> getMetricsByType(
            @Parameter(description = "Tipo de métrica (SPEED, LOCATION, ALERT, ROTATION)")
            @PathVariable String metricType) {
        List<DashboardMetric> metrics = metricRepository.findByMetricType(metricType);
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/metrics/date-range")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR') or hasRole('VIEWER')")
    @Operation(summary = "Obtener métricas por rango de fechas", description = "Retorna una lista de métricas dentro de un rango de fechas")
    @ApiResponse(responseCode = "200", description = "Lista de métricas obtenida exitosamente")
    public ResponseEntity<List<DashboardMetric>> getMetricsByDateRange(
            @Parameter(description = "Fecha de inicio (formato ISO)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Fecha de fin (formato ISO)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<DashboardMetric> metrics = metricRepository.findByTimestampBetween(startDate, endDate);
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/metrics/vehicle/{vehicleId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR') or hasRole('VIEWER')")
    @Operation(summary = "Obtener métricas por vehículo", description = "Retorna una lista de métricas para un vehículo específico")
    @ApiResponse(responseCode = "200", description = "Lista de métricas obtenida exitosamente")
    public ResponseEntity<List<DashboardMetric>> getMetricsByVehicle(
            @Parameter(description = "ID del vehículo") @PathVariable Long vehicleId) {
        List<DashboardMetric> metrics = metricRepository.findByVehicleId(vehicleId);
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/metrics/driver/{driverId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR') or hasRole('VIEWER')")
    @Operation(summary = "Obtener métricas por conductor", description = "Retorna una lista de métricas para un conductor específico")
    @ApiResponse(responseCode = "200", description = "Lista de métricas obtenida exitosamente")
    public ResponseEntity<List<DashboardMetric>> getMetricsByDriver(
            @Parameter(description = "ID del conductor") @PathVariable Long driverId) {
        List<DashboardMetric> metrics = metricRepository.findByDriverId(driverId);
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR') or hasRole('VIEWER')")
    @Operation(summary = "Obtener resumen del dashboard", description = "Retorna un resumen con las métricas más recientes para el dashboard")
    @ApiResponse(responseCode = "200", description = "Resumen obtenido exitosamente",
            content = @Content(schema = @Schema(implementation = DashboardSummaryResponse.class)))
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary() {
        // Get latest metrics for each type
        DashboardMetric speedMetric = metricRepository.findLatestByMetricType("SPEED");
        DashboardMetric locationMetric = metricRepository.findLatestByMetricType("LOCATION");
        DashboardMetric alertMetric = metricRepository.findLatestByMetricType("ALERT");
        DashboardMetric rotationMetric = metricRepository.findLatestByMetricType("ROTATION");

        // Create summary response
        DashboardSummaryResponse summary = new DashboardSummaryResponse();

        Map<String, Object> latestMetrics = new HashMap<>();
        if (speedMetric != null) latestMetrics.put("speed", speedMetric);
        if (locationMetric != null) latestMetrics.put("location", locationMetric);
        if (alertMetric != null) latestMetrics.put("alert", alertMetric);
        if (rotationMetric != null) latestMetrics.put("rotation", rotationMetric);

        summary.setLatestMetrics(latestMetrics);

        // Add additional summary data as needed
        // This could include counts, averages, etc.

        return ResponseEntity.ok(summary);
    }
}

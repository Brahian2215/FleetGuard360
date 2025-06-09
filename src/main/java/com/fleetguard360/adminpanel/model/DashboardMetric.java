package com.fleetguard360.adminpanel.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "dashboard_metrics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa una métrica para el dashboard")
public class DashboardMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la métrica")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Nombre de la métrica", example = "Velocidad Promedio")
    private String metricName;

    @Column(nullable = false)
    @Schema(description = "Valor de la métrica", example = "85.5")
    private String metricValue;

    @Column(nullable = false)
    @Schema(description = "Tipo de métrica", example = "SPEED, LOCATION, ALERT, ROTATION")
    private String metricType;

    @Column(nullable = false)
    @Schema(description = "Fecha y hora de la métrica")
    private LocalDateTime timestamp;

    @Column(nullable = true)
    @Schema(description = "ID del vehículo asociado (opcional)")
    private Long vehicleId;

    @Column(nullable = true)
    @Schema(description = "ID del conductor asociado (opcional)")
    private Long driverId;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}

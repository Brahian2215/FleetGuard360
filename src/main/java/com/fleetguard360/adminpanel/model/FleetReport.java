package com.fleetguard360.adminpanel.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "fleet_reports")
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa un reporte de operación de flota")
public class FleetReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del reporte")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Nombre del reporte", example = "Reporte Mensual de Velocidades")
    private String reportName;

    @Column(nullable = false)
    @Schema(description = "Fecha de generación del reporte")
    private LocalDateTime reportDate;

    @Column(nullable = false)
    @Schema(description = "Fecha de inicio del período del reporte")
    private LocalDateTime startDate;

    @Column(nullable = false)
    @Schema(description = "Fecha de fin del período del reporte")
    private LocalDateTime endDate;

    @Column(nullable = false)
    @Schema(description = "Tipo de reporte", example = "DAILY, WEEKLY, MONTHLY")
    private String reportType;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "Datos del reporte en formato JSON")
    private String reportData;

    @ManyToOne
    @JoinColumn(name = "created_by")
    @Schema(description = "Usuario que creó el reporte")
    private User createdBy;

    @Schema(description = "Indica si el reporte está archivado", example = "false")
    private boolean archived = false;

    @Column(name = "created_at")
    @Schema(description = "Fecha y hora de creación del reporte")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public LocalDateTime getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDateTime reportDate) {
        this.reportDate = reportDate;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getReportData() {
        return reportData;
    }

    public void setReportData(String reportData) {
        this.reportData = reportData;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

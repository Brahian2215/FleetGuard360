package com.fleetguard360.adminpanel.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "Solicitud para crear un nuevo reporte")
public class CreateReportRequest {
    @NotBlank
    @Schema(description = "Nombre del reporte", example = "Reporte Mensual de Velocidades", required = true)
    private String reportName;

    @NotNull
    @Schema(description = "Fecha de inicio del período del reporte", example = "2023-01-01T00:00:00", required = true)
    private LocalDateTime startDate;

    @NotNull
    @Schema(description = "Fecha de fin del período del reporte", example = "2023-01-31T23:59:59", required = true)
    private LocalDateTime endDate;

    @NotBlank
    @Schema(description = "Tipo de reporte", example = "MONTHLY", required = true, allowableValues = {"DAILY", "WEEKLY", "MONTHLY"})
    private String reportType;

    @NotBlank
    @Schema(description = "Datos del reporte en formato JSON", example = "{\"tableData\": [{\"vehicleId\": 1, \"speed\": 85.5}], \"summary\": \"Resumen del reporte\"}", required = true)
    private String reportData;

    public @NotBlank String getReportName() {
        return reportName;
    }

    public void setReportName(@NotBlank String reportName) {
        this.reportName = reportName;
    }

    public @NotNull LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(@NotNull LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public @NotNull LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(@NotNull LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public @NotBlank String getReportType() {
        return reportType;
    }

    public void setReportType(@NotBlank String reportType) {
        this.reportType = reportType;
    }

    public @NotBlank String getReportData() {
        return reportData;
    }

    public void setReportData(@NotBlank String reportData) {
        this.reportData = reportData;
    }
}

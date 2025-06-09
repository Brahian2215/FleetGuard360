package com.fleetguard360.adminpanel.controller;

import com.fleetguard360.adminpanel.model.FleetReport;
import com.fleetguard360.adminpanel.model.User;
import com.fleetguard360.adminpanel.payload.request.CreateReportRequest;
import com.fleetguard360.adminpanel.payload.response.MessageResponse;
import com.fleetguard360.adminpanel.repository.FleetReportRepository;
import com.fleetguard360.adminpanel.repository.UserRepository;
import com.fleetguard360.adminpanel.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/reports")
@Tag(name = "Reportes", description = "API para la gestión de reportes de operación de flotas")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {
    @Autowired
    FleetReportRepository reportRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ExportService exportService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR') or hasRole('VIEWER')")
    @Operation(summary = "Obtener todos los reportes", description = "Retorna una lista de todos los reportes disponibles")
    @ApiResponse(responseCode = "200", description = "Lista de reportes obtenida exitosamente")
    public ResponseEntity<List<FleetReport>> getAllReports() {
        List<FleetReport> reports = reportRepository.findAll();
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR') or hasRole('VIEWER')")
    @Operation(summary = "Obtener reporte por ID", description = "Retorna un reporte específico según su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte encontrado"),
            @ApiResponse(responseCode = "404", description = "Reporte no encontrado")
    })
    public ResponseEntity<FleetReport> getReportById(
            @Parameter(description = "ID del reporte a buscar") @PathVariable Long id) {
        return reportRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/type/{reportType}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR') or hasRole('VIEWER')")
    @Operation(summary = "Obtener reportes por tipo", description = "Retorna una lista de reportes filtrados por tipo")
    @ApiResponse(responseCode = "200", description = "Lista de reportes obtenida exitosamente")
    public ResponseEntity<List<FleetReport>> getReportsByType(
            @Parameter(description = "Tipo de reporte (DAILY, WEEKLY, MONTHLY)") @PathVariable String reportType) {
        List<FleetReport> reports = reportRepository.findByReportType(reportType);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR') or hasRole('VIEWER')")
    @Operation(summary = "Obtener reportes por rango de fechas", description = "Retorna una lista de reportes dentro de un rango de fechas")
    @ApiResponse(responseCode = "200", description = "Lista de reportes obtenida exitosamente")
    public ResponseEntity<List<FleetReport>> getReportsByDateRange(
            @Parameter(description = "Fecha de inicio (formato ISO)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Fecha de fin (formato ISO)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<FleetReport> reports = reportRepository.findByReportDateBetween(startDate, endDate);
        return ResponseEntity.ok(reports);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @Operation(summary = "Crear nuevo reporte", description = "Crea un nuevo reporte en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de reporte inválidos")
    })
    public ResponseEntity<?> createReport(@Valid @RequestBody CreateReportRequest reportRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));

        FleetReport report = new FleetReport();
        report.setReportName(reportRequest.getReportName());
        report.setReportDate(LocalDateTime.now());
        report.setStartDate(reportRequest.getStartDate());
        report.setEndDate(reportRequest.getEndDate());
        report.setReportType(reportRequest.getReportType());
        report.setReportData(reportRequest.getReportData());
        report.setCreatedBy(currentUser);

        reportRepository.save(report);

        return ResponseEntity.ok(new MessageResponse("Report created successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar reporte", description = "Elimina un reporte del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Reporte no encontrado")
    })
    public ResponseEntity<?> deleteReport(
            @Parameter(description = "ID del reporte a eliminar") @PathVariable Long id) {
        return reportRepository.findById(id)
                .map(report -> {
                    reportRepository.delete(report);
                    return ResponseEntity.ok(new MessageResponse("Report deleted successfully"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/export/pdf")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR') or hasRole('VIEWER')")
    @Operation(summary = "Exportar reporte a PDF", description = "Genera un archivo PDF con el contenido del reporte")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF generado exitosamente",
                    content = @Content(mediaType = "application/pdf")),
            @ApiResponse(responseCode = "404", description = "Reporte no encontrado")
    })
    public void exportReportToPdf(
            @Parameter(description = "ID del reporte a exportar") @PathVariable Long id,
            HttpServletResponse response) throws IOException {
        FleetReport report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Report not found."));

        exportService.exportReportToPdf(report, response);
    }

    @GetMapping("/{id}/export/excel")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR') or hasRole('VIEWER')")
    @Operation(summary = "Exportar reporte a Excel", description = "Genera un archivo Excel con el contenido del reporte")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Excel generado exitosamente",
                    content = @Content(mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
            @ApiResponse(responseCode = "404", description = "Reporte no encontrado")
    })
    public void exportReportToExcel(
            @Parameter(description = "ID del reporte a exportar") @PathVariable Long id,
            HttpServletResponse response) throws IOException {
        FleetReport report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Report not found."));

        exportService.exportReportToExcel(report, response);
    }
}

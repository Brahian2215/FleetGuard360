package com.fleetguard360.adminpanel.service;

import com.fleetguard360.adminpanel.model.FleetReport;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
public class ExportService {

    public void exportReportToPdf(FleetReport report, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + report.getReportName().replaceAll("\\s+", "_") + ".pdf";
        response.setHeader(headerKey, headerValue);

        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph(report.getReportName(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            com.itextpdf.text.Font metaFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            document.add(new Paragraph("Report Type: " + report.getReportType(), metaFont));
            document.add(new Paragraph("Report Date: " + report.getReportDate().format(formatter), metaFont));
            document.add(new Paragraph("Period: " + report.getStartDate().format(formatter) + " to " + report.getEndDate().format(formatter), metaFont));
            document.add(new Paragraph("Created By: " + report.getCreatedBy().getFullName(), metaFont));
            document.add(Chunk.NEWLINE);

            addReportDataToDocument(document, report, metaFont);

        } catch (DocumentException e) {
            throw new IOException("Error creating PDF document", e);
        } finally {
            document.close();
        }
    }

    private void addReportDataToDocument(Document document, FleetReport report, com.itextpdf.text.Font metaFont) {
        try {
            JSONObject jsonData = new JSONObject(report.getReportData());

            if (jsonData.has("tableData") && jsonData.get("tableData") instanceof JSONArray) {
                JSONArray tableData = jsonData.getJSONArray("tableData");
                if (tableData.length() > 0) {
                    PdfPTable table = new PdfPTable(tableData.getJSONObject(0).length());
                    table.setWidthPercentage(100);

                    JSONObject firstRow = tableData.getJSONObject(0);
                    com.itextpdf.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

                    for (String key : firstRow.keySet()) {
                        PdfPCell headerCell = new PdfPCell(new Phrase(key, headerFont));
                        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        table.addCell(headerCell);
                    }

                    for (int i = 0; i < tableData.length(); i++) {
                        JSONObject row = tableData.getJSONObject(i);
                        for (String key : row.keySet()) {
                            table.addCell(row.get(key).toString());
                        }
                    }

                    document.add(table);
                }
            }

            if (jsonData.has("summary") && jsonData.get("summary") instanceof String) {
                document.add(Chunk.NEWLINE);
                com.itextpdf.text.Font summaryFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
                document.add(new Paragraph("Summary", summaryFont));
                document.add(new Paragraph(jsonData.getString("summary"), metaFont));
            }

        } catch (Exception e) {
            try {
                document.add(new Paragraph("Error parsing report data: " + e.getMessage(), metaFont));
            } catch (DocumentException docEx) {
                // Ignorado
            }
        }
    }

    public void exportReportToExcel(FleetReport report, HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + report.getReportName().replaceAll("\\s+", "_") + ".xlsx";
        response.setHeader(headerKey, headerValue);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(report.getReportName());

            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = workbook.createCellStyle();

            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            Cell headerCell = headerRow.createCell(0);
            headerCell.setCellValue("Report Name");
            headerCell.setCellStyle(headerStyle);

            // Completar Excel según sea necesario

            workbook.write(response.getOutputStream());
        }
    }
}

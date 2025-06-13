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

    private static final String TABLE_DATA = "tableData";
    private static final String SUMMARY = "summary";

    public void exportReportToPdf(FleetReport report, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        String headerValue = "attachment; filename=" + report.getReportName().replaceAll("\\s+", "_") + ".pdf";
        response.setHeader("Content-Disposition", headerValue);

        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            addReportMetaData(document, report);
            addReportDataToDocument(document, report);

        } catch (DocumentException e) {
            throw new IOException("Error creating PDF document", e);
        } finally {
            document.close();
        }
    }

    private void addReportMetaData(Document document, FleetReport report) throws DocumentException {
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph(report.getReportName(), titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);

        Font metaFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        document.add(new Paragraph("Report Type: " + report.getReportType(), metaFont));
        document.add(new Paragraph("Report Date: " + report.getReportDate().format(formatter), metaFont));
        document.add(new Paragraph("Period: " + report.getStartDate().format(formatter) + " to " + report.getEndDate().format(formatter), metaFont));
        document.add(new Paragraph("Created By: " + report.getCreatedBy().getFullName(), metaFont));
        document.add(Chunk.NEWLINE);
    }

    private void addReportDataToDocument(Document document, FleetReport report) {
        try {
            JSONObject jsonData = new JSONObject(report.getReportData());
            Font metaFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            if (jsonData.has(TABLE_DATA) && jsonData.get(TABLE_DATA) instanceof JSONArray tableData && tableData.length() > 0) {
                PdfPTable table = createPdfTableFromJsonArray(tableData);
                document.add(table);
            }

            if (jsonData.has(SUMMARY) && jsonData.get(SUMMARY) instanceof String) {
                addSummaryToDocument(document, jsonData.getString(SUMMARY), metaFont);
            }

        } catch (Exception e) {
            try {
                Font metaFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
                document.add(new Paragraph("Error parsing report data: " + e.getMessage(), metaFont));
            } catch (DocumentException ignored) {
            }
        }
    }

    private PdfPTable createPdfTableFromJsonArray(JSONArray tableData) throws DocumentException {
        JSONObject firstRow = tableData.getJSONObject(0);
        PdfPTable table = new PdfPTable(firstRow.length());
        table.setWidthPercentage(100);

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

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

        return table;
    }

    private void addSummaryToDocument(Document document, String summary, Font metaFont) throws DocumentException {
        Font summaryFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        document.add(Chunk.NEWLINE);
        document.add(new Paragraph("Summary", summaryFont));
        document.add(new Paragraph(summary, metaFont));
    }

    public void exportReportToExcel(FleetReport report, HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        String headerValue = "attachment; filename=" + report.getReportName().replaceAll("\\s+", "_") + ".xlsx";
        response.setHeader("Content-Disposition", headerValue);

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

            workbook.write(response.getOutputStream());
        }
    }
}

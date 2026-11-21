package usta.pharmacy.infrastructure.adapter.in.web;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.enterprise.context.ApplicationScoped;
import usta.pharmacy.domain.model.MonthlySummary;
import usta.pharmacy.domain.model.Product;
import usta.pharmacy.domain.model.Sale;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Genera el reporte PDF del mes (ventas + productos por agotarse) con OpenPDF. */
@ApplicationScoped
public class SalesPdfGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
    private final Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13);
    private final Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
    private final Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

    public byte[] generate(YearMonth month, List<Sale> sales, MonthlySummary summary, List<Product> lowStock) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, out);
            document.open();

            Paragraph title = new Paragraph("Reporte mensual - Droguería", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph("Mes: " + WebSupport.monthLabel(month), textFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(15);
            document.add(subtitle);

            // ----- Resumen del mes -----
            document.add(new Paragraph("Resumen de ventas del mes", sectionFont));
            Paragraph resume = new Paragraph(
                    "Total vendido: $" + summary.getTotalAmount()
                            + "\nUnidades vendidas: " + summary.getUnitsSold()
                            + "\nNúmero de ventas: " + summary.getSalesCount()
                            + "\nProducto más vendido: " + summary.getTopProduct(), textFont);
            resume.setSpacingAfter(12);
            document.add(resume);

            // ----- Detalle de ventas -----
            document.add(new Paragraph("Detalle de ventas", sectionFont));
            PdfPTable salesTable = new PdfPTable(5);
            salesTable.setWidthPercentage(100);
            salesTable.setSpacingBefore(6);
            salesTable.setWidths(new float[]{1.6f, 4f, 1.2f, 2f, 2f});
            for (String header : new String[]{"Fecha", "Producto", "Cant.", "Precio unit.", "Total"}) {
                salesTable.addCell(headerCell(header));
            }
            if (sales.isEmpty()) {
                PdfPCell empty = new PdfPCell(new Phrase("No hubo ventas en este mes.", textFont));
                empty.setColspan(5);
                empty.setPadding(6);
                salesTable.addCell(empty);
            }
            for (Sale sale : sales) {
                salesTable.addCell(cell(sale.getSaleDate().format(DATE_FORMAT)));
                salesTable.addCell(cell(sale.getProduct().getName()));
                salesTable.addCell(cell(String.valueOf(sale.getQuantity())));
                salesTable.addCell(cell("$" + sale.getUnitPrice()));
                salesTable.addCell(cell("$" + sale.getTotal()));
            }
            document.add(salesTable);

            // ----- Productos por agotarse -----
            Paragraph lowTitle = new Paragraph("Productos por agotarse", sectionFont);
            lowTitle.setSpacingBefore(18);
            document.add(lowTitle);
            PdfPTable lowTable = new PdfPTable(5);
            lowTable.setWidthPercentage(100);
            lowTable.setSpacingBefore(6);
            lowTable.setWidths(new float[]{4f, 2.4f, 1.4f, 1.6f, 1.8f});
            for (String header : new String[]{"Producto", "Categoría", "Stock", "Mínimo", "Estado"}) {
                lowTable.addCell(headerCell(header));
            }
            if (lowStock.isEmpty()) {
                PdfPCell empty = new PdfPCell(new Phrase("No hay productos por agotarse.", textFont));
                empty.setColspan(5);
                empty.setPadding(6);
                lowTable.addCell(empty);
            }
            for (Product product : lowStock) {
                lowTable.addCell(cell(product.getName()));
                lowTable.addCell(cell(product.getCategory()));
                lowTable.addCell(cell(String.valueOf(product.getStock())));
                lowTable.addCell(cell(String.valueOf(product.getMinStock())));
                lowTable.addCell(cell(product.isOutOfStock() ? "Agotado" : "Por agotarse"));
            }
            document.add(lowTable);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo generar el reporte PDF.", e);
        }
    }

    private PdfPCell headerCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, headerFont));
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        cell.setPadding(5);
        return cell;
    }

    private PdfPCell cell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, textFont));
        cell.setPadding(5);
        return cell;
    }
}
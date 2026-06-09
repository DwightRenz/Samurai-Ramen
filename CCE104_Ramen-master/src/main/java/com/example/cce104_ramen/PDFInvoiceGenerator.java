package com.example.cce104_ramen;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.properties.UnitValue;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PDFInvoiceGenerator {

    public static void generateInvoice(String filePath, List<Order> orders, int totalPrice) {
        try {
            // Create PDF writer and document
            PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);

            // Add shop details
            Paragraph header = new Paragraph("Samurai Ramen")
                    .setBold()
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER);
            Paragraph address = new Paragraph("123 Samurai Street, Tokyo, Japanacan")
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER);
            Paragraph contact = new Paragraph("Phone: +63 123-456-789 | Email: SamuraiRamen@gmail.com")
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER);

            document.add(header);
            document.add(address);
            document.add(contact);

            // Add date and invoice number
            Paragraph dateAndInvoice = new Paragraph("Date: " + java.time.LocalDate.now() + "\nInvoice #: " + System.nanoTime())
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT);
            document.add(dateAndInvoice);

            // Add title
            Paragraph title = new Paragraph("Receipt")
                    .setBold()
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10);
            document.add(title);

            // Create a table with columns for Order Name, Quantity, Price, and Amount
            float[] columnWidths = {200f, 100f, 100f, 100f}; // Adjust column widths
            Table table = new Table(columnWidths);
            table.setWidth(UnitValue.createPercentValue(100)); // Set the table width to 100% of the page

            // Add table headers
            table.addHeaderCell(new Paragraph("Order Name").setBold());
            table.addHeaderCell(new Paragraph("Quantity").setBold().setTextAlignment(TextAlignment.RIGHT));
            table.addHeaderCell(new Paragraph("Price (₱)").setBold().setTextAlignment(TextAlignment.RIGHT));
            table.addHeaderCell(new Paragraph("Amount (₱)").setBold().setTextAlignment(TextAlignment.RIGHT));

            // Populate table with order data
            Map<String, Integer> orderQuantities = new HashMap<>(); // Calculate quantities of each item
            // Populate table with order data
            for (Order order : orders) {
                table.addCell(new Paragraph(order.getOrderName()).setPadding(5));
                table.addCell(new Paragraph(String.valueOf(order.getQuantity()))
                        .setTextAlignment(TextAlignment.RIGHT).setPadding(5));
                table.addCell(new Paragraph(String.valueOf(order.getPrice()))
                        .setTextAlignment(TextAlignment.RIGHT).setPadding(5));
                table.addCell(new Paragraph(String.valueOf(order.getAmount()))
                        .setTextAlignment(TextAlignment.RIGHT).setPadding(5));
            }

            for (Map.Entry<String, Integer> entry : orderQuantities.entrySet()) {
                String orderName = entry.getKey();
                int quantity = entry.getValue();
                int price = orders.stream()
                        .filter(o -> o.getOrderName().equals(orderName))
                        .findFirst()
                        .map(Order::getPrice)
                        .orElse(0);
                int amount = quantity * price;

                table.addCell(new Paragraph(orderName).setPadding(5));
                table.addCell(new Paragraph(String.valueOf(quantity))
                        .setTextAlignment(TextAlignment.RIGHT).setPadding(5));
                table.addCell(new Paragraph(String.valueOf(price))
                        .setTextAlignment(TextAlignment.RIGHT).setPadding(5));
                table.addCell(new Paragraph(String.valueOf(amount))
                        .setTextAlignment(TextAlignment.RIGHT).setPadding(5));
            }

            // Add total price row
            Cell totalLabelCell = new Cell(1, 3); // 1 row, spans 3 columns
            totalLabelCell.add(new Paragraph("Total").setBold().setPadding(5).setTextAlignment(TextAlignment.RIGHT));
            totalLabelCell.setPadding(5);
            table.addCell(totalLabelCell);

// Create a cell for the total price
            Cell totalAmountCell = new Cell(); // Default span is 1
            totalAmountCell.add(new Paragraph("₱" + totalPrice).setBold().setTextAlignment(TextAlignment.RIGHT).setPadding(5));
            table.addCell(totalAmountCell);



            // Add the table to the document
            document.add(table);

            // Add a thank-you message at the bottom
            Paragraph footer = new Paragraph("Please proceed to the counter. \n Thank you for dining with Samurai Ramen!")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(20);
            document.add(footer);

            // Close the document
            document.close();

            System.out.println("PDF generated: " + filePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

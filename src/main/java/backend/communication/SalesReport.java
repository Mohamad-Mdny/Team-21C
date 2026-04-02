package backend.communication;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.Document;
import java.io.FileOutputStream;
import java.util.List;

public class SalesReport {

    public static void generateReport(List<ProductStats> products, String Path) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(Path));
            document.open();

            //Title
        Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
        Paragraph title = new Paragraph("Sales Report");
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);

        // creating the columns headers
        for (String header : new String[]{"Product Name", "Quantity", "Unit Price", "Total Revenue"}) {
            PdfPCell cell = new PdfPCell(new Phrase(header, new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setPadding(8);
            table.addCell(cell);
        }

        // loop to put out stats
        for (ProductStats product : products) {
            table.addCell(product.getProductName());
            table.addCell(String.valueOf(product.getQuantitySold()));
            table.addCell(String.format("%.2f", product.getUnitPrice()));
            table.addCell(String.format("%.2f", product.getTotalRevenue()));
        }


        document.add(table);
        document.close();
        System.out.println("Report made at: " + Path);



    } catch (Exception e) {
        e.printStackTrace();
    }
    }
}

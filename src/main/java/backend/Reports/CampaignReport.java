package backend.Reports;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.Document;
import java.io.FileOutputStream;
import java.util.List;

public class CampaignReport {

    public static void generateReport(List<CampaignItem> items, String path, String startPeriod, String endPeriod) {
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();

            // header layout
            Font metaFont = new Font(Font.FontFamily.TIMES_ROMAN, 11);
            Font adressFont = new Font(Font.FontFamily.TIMES_ROMAN, 10);

            PdfPTable headerLayout = new PdfPTable(2);
            headerLayout.setWidthPercentage(100);
            headerLayout.setWidths(new float[]{1, 1});


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

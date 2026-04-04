package backend.Reports;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.Document;

import javax.swing.text.StyledEditorKit;
import java.io.FileOutputStream;
import java.util.List;

public class CampaignReport {

    public static void generateReport(List<CampaignStats> campaigns, String path, String startPeriod, String endPeriod) {
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
            headerLayout.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            PdfPCell leftCell = new PdfPCell();
            leftCell.setBorder(Rectangle.NO_BORDER);
            leftCell.addElement(new Paragraph("Start Period: " + startPeriod, metaFont));
            leftCell.addElement(new Paragraph("End Period: " + endPeriod, metaFont));

            StringBuilder activeCampaigns = new StringBuilder();
            for (int i = 0 ; i < campaigns.size(); i++) {
                activeCampaigns.append(campaigns.get(i).getCampaignId());
                if (i < campaigns.size() - 1) activeCampaigns.append(" and ");
            }
            leftCell.addElement(new Paragraph("Active campaigns: " + campaigns.size() + " (" + activeCampaigns + ")", metaFont));
            headerLayout.addCell(leftCell);

            PdfPCell rightCell = new PdfPCell();
            rightCell.setBorder(Rectangle.NO_BORDER);
            Paragraph addr = new Paragraph("Infinite Solution Ltd., \nNorthampton Square, \nLondon, \nEC1V 0HB");
            addr.setAlignment(Element.ALIGN_RIGHT);
            rightCell.addElement(addr);
            headerLayout.addCell(rightCell);

            document.add(headerLayout);
            document.add(Chunk.NEWLINE);

            //title
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2, 2, 2, 2, 2});

            Font boldFont = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.TIMES_ROMAN, 11);

            for (String header : new String[]{"Campaign ID", "Start Date/Time", "End Date/Time", "Items included", "Discount, %"}) {
                PdfPCell cell = new PdfPCell(new Phrase(header, boldFont));
                cell.setPadding(6);
                table.addCell(cell);
            }

            // looping through campains
            for (CampaignStats campaign : campaigns) {

                //the campaign rows
                table.addCell(new PdfPCell(new Phrase(campaign.getCampaignId(), normalFont)));
                table.addCell(new PdfPCell(new Phrase(campaign.getStartDate(), normalFont)));
                table.addCell(new PdfPCell(new Phrase(campaign.getEndDate(), normalFont)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(campaign.getItemsIncluded()), normalFont)));
                table.addCell(new PdfPCell(new Phrase(campaign.getDiscountType(), normalFont)));

                //sold header when campaign ends
                PdfPCell soldHeader = new PdfPCell(new Phrase(new Phrase("Sold", boldFont)));
                soldHeader.setColspan(5);
                soldHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
                soldHeader.setPadding(4);
                table.addCell(soldHeader);

                //header for items

                for (String header : new String[]{"ID", "Description", "Discount", "Items Sold", "Total Sales, £"}) {
                    PdfPCell cell = new PdfPCell(new Phrase(header, boldFont));
                    cell.setPadding(4);
                    table.addCell(cell);
                }

                for (CampaignItem item : campaign.getItems()) {
                    table.addCell(new PdfPCell(new Phrase(item.getItemId(), normalFont)));
                    table.addCell(new PdfPCell(new Phrase(item.getDescription(), normalFont)));
                    table.addCell(new PdfPCell(new Phrase(item.getDiscount(), normalFont)));
                    table.addCell(new PdfPCell(new Phrase(String.valueOf(item.getItemSold()), normalFont)));
                    table.addCell(new PdfPCell(new Phrase(String.format("%.2f", item.getTotalSales()), normalFont)));
                }

                PdfPCell totalLabel = new PdfPCell((new Phrase("Total Sales in campaign", boldFont)));
                totalLabel.setColspan(4);
                totalLabel.setPadding(6);
                table.addCell(totalLabel);

                PdfPCell totalValue = new PdfPCell((new Phrase(String.format("%.2f", campaign.getTotalSales()), boldFont)));
                totalValue.setColspan(5);
                table.addCell(totalValue);

                //Empty row between different campagins
                PdfPCell space = new PdfPCell(new Phrase(" "));
                space.setColspan(5);
                space.setBorder(Rectangle.NO_BORDER);
                table.addCell(space);
            }

            document.add(table);
            document.add(Chunk.NEWLINE);

            //footer
            // Footer
            Font footerFont = new Font(Font.FontFamily.TIMES_ROMAN, 10);
            document.add(new Paragraph("Generated: " + java.time.LocalDate.now(), footerFont));
            document.add(new Paragraph("Generated by IPOS-PU Team 21", footerFont));

            document.close();
            System.out.println("SalesReport made at: " + path);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

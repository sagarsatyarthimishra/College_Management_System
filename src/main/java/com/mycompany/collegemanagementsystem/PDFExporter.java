package com.mycompany.collegemanagementsystem;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.*;
import java.sql.*;
import java.awt.Desktop;
import javax.swing.JOptionPane;

public class PDFExporter {

    public void exportStudentDataToPDF() {
        String url = "jdbc:mysql://127.0.0.1:3306/college";
        String user = "root";
        String pass = "Mishra@2004";

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream("StudentData.pdf"));
            document.open();

            // Optional: Add logo (replace path if you have a real image)
            // Image logo = Image.getInstance("src/resources/logo.png");
            // logo.scaleToFit(80, 80);
            // logo.setAlignment(Image.ALIGN_LEFT);
            // document.add(logo);

            // Title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, BaseColor.BLUE);
            Paragraph title = new Paragraph("📘 Student Data", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Create table
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Header styling
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
            BaseColor headerColor = new BaseColor(0, 121, 182); // Blue

            String[] headers = {"Roll No", "Name", "Year", "Course", "Branch"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(headerColor);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(8);
                table.addCell(cell);
            }

            // Fetch student data
            Connection con = DriverManager.getConnection(url, user, pass);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM student");

            // Row styling
            Font rowFont = new Font(Font.FontFamily.HELVETICA, 11);
            boolean alternate = false;

            while (rs.next()) {
                BaseColor bgColor = alternate ? new BaseColor(245, 245, 245) : BaseColor.WHITE;
                alternate = !alternate;

                table.addCell(createStyledCell(String.valueOf(rs.getInt("rollno")), rowFont, bgColor));
                table.addCell(createStyledCell(rs.getString("name"), rowFont, bgColor));
                table.addCell(createStyledCell(String.valueOf(rs.getInt("year")), rowFont, bgColor));
                table.addCell(createStyledCell(rs.getString("course"), rowFont, bgColor));
                table.addCell(createStyledCell(rs.getString("branch"), rowFont, bgColor));
            }

            document.add(table);

            // Timestamp
            Paragraph timestamp = new Paragraph("📅 Exported on: " + new java.util.Date(),
                    new Font(Font.FontFamily.COURIER, 10, Font.ITALIC, BaseColor.DARK_GRAY));
            timestamp.setAlignment(Element.ALIGN_RIGHT);
            document.add(timestamp);

            document.close();
            con.close();

            // Open PDF
            if (Desktop.isDesktopSupported()) {
                File file = new File("StudentData.pdf");
                if (file.exists()) {
                    Desktop.getDesktop().open(file);
                }
            }

            JOptionPane.showMessageDialog(null, "✅ Student data exported to StudentData.pdf!");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "❌ Error exporting data to PDF.");
        }
    }

    private PdfPCell createStyledCell(String text, Font font, BaseColor bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bgColor);
        cell.setPadding(6);
        return cell;
    }
}

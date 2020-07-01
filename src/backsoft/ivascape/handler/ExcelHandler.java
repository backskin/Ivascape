package backsoft.ivascape.handler;

import backsoft.ivascape.model.Company;
import backsoft.ivascape.model.IvascapeGraph;
import backsoft.ivascape.model.Link;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import static backsoft.ivascape.handler.AlertHandler.AlertType.ISSUE;

class ExcelHandler {

    private static final Preferences prefs = Preferences.get();
    
    private static HSSFCellStyle createStyleForTitle(HSSFWorkbook workbook) {
        HSSFFont font = workbook.createFont();
        font.setBold(true);
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);
        return style;
    }

    private static HSSFCellStyle createRegularStyle(HSSFWorkbook workbook) {
        HSSFFont font = workbook.createFont();
        font.setBold(false);
        HSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFont(font);
        return style;
    }

    static void saveItAsXLS(IvascapeGraph graph, File file){

        if (file == null) return;

        HSSFWorkbook workbook = new HSSFWorkbook();

        try {
            createFirstSheet(workbook, graph);
            createSecondSheet(workbook, graph);

            FileOutputStream stream = new FileOutputStream(file);
            workbook.unwriteProtectWorkbook();
            workbook.write(stream);
            stream.close();
        }
        catch (IOException e){
            AlertHandler.makeAlert(ISSUE).customContent(e.getLocalizedMessage()).show();
            throw new RuntimeException();
        }
    }

    private static void setRow(Row row, int length, HSSFCellStyle style){
        for (int i = 0; i < length; i++) row.createCell(i).setCellStyle(style);
    }

    private static void autosizecols(HSSFSheet sheet, int end){
        for (int i = 0; i <= end; i++) sheet.autoSizeColumn(i);
    }

    private static void createFirstSheet(HSSFWorkbook workbook, IvascapeGraph graph){

            HSSFSheet sheet = workbook.createSheet(prefs.getStringFromBundle("excelsheet.cmps"));
            Row row = sheet.createRow(0);
            setRow(row, 4, createStyleForTitle(workbook));
            row.getCell(0).setCellValue(prefs.getStringFromBundle("tabletext.title"));
            row.getCell(1).setCellValue(prefs.getStringFromBundle("tabletext.money"));
            row.getCell(2).setCellValue(prefs.getStringFromBundle("tabletext.address"));
            row.getCell(3).setCellValue(prefs.getStringFromBundle("tabletext.date"));

            for (Iterator<Company> iterator = graph.getVertexIterator(); iterator.hasNext();){
                Company company = iterator.next();
                row = sheet.createRow(sheet.getLastRowNum()+1);
                setRow(row, 4, createRegularStyle(workbook));
                row.getCell(0).setCellValue(company.getTitle());
                row.getCell(1).setCellValue(company.getMoney());
                row.getCell(2).setCellValue(company.getAddress());
                row.getCell(3).setCellValue(company.getDate().toString());
            }

           autosizecols(sheet, 3);
    }

    private static void createSecondSheet(HSSFWorkbook workbook, IvascapeGraph graph){

            HSSFSheet sheet = workbook.createSheet(prefs.getStringFromBundle("excelsheet.links"));
            Row row = sheet.createRow(0);
            setRow(row,5,createStyleForTitle(workbook));

            row.getCell(0).setCellValue(prefs.getStringFromBundle("edittabs.neighbour"));
            row.getCell(1).setCellValue(prefs.getStringFromBundle("edittabs.neighbour"));
            row.getCell(2).setCellValue(prefs.getStringFromBundle("edittabs.price"));
            row.getCell(4).setCellValue(prefs.getStringFromBundle("excelsheet.summ"));

            int count = 1;
            if (graph.getEdgeSize() == 0) sheet.createRow(1);
            for(Iterator<Link> iterator = graph.getEdgeIterator(); iterator.hasNext();) {

                Link link = iterator.next();
                row = sheet.createRow(count++);
                setRow(row,3, createRegularStyle(workbook));
                row.getCell(0).setCellValue(link.one().getTitle());
                row.getCell(1).setCellValue(link.two().getTitle());
                row.getCell(2).setCellValue(link.getPrice());
            }

            sheet.getRow(1).createCell(4)
                    .setCellFormula("SUM(C2:C" + count + ")");

            autosizecols(sheet, 4);
    }
}

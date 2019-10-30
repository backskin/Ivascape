package backsoft.ivascape.handler;

import backsoft.ivascape.model.Company;
import backsoft.ivascape.model.IvascapeGraph;
import backsoft.ivascape.model.Link;
import backsoft.ivascape.viewcontrol.MyAlerts;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

class ExcelHandler {

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

            FileOutputStream outFile = new FileOutputStream(file);
            workbook.unwriteProtectWorkbook();
            workbook.write(outFile);
            outFile.close();
        }
        catch (NullPointerException | IOException npE){

            MyAlerts.getAlert(MyAlerts.AlertType.UNKNOWN, (String[]) null);
            npE.printStackTrace();
        }

    }

    private static void createFirstSheet(HSSFWorkbook workbook, IvascapeGraph graph){

        try {
            
            HSSFSheet sheet = workbook.createSheet(Preferences.getBundle().getString("excelsheet.cmps"));
            Cell cell;
            Row row = sheet.createRow(0);

            HSSFCellStyle styleForTitle = createStyleForTitle(workbook);
            HSSFCellStyle styleRegular = createRegularStyle(workbook);

            cell = row.createCell(0);
            cell.setCellValue(Preferences.getBundle().getString("tabletext.title"));
            cell.setCellStyle(styleForTitle);

            cell = row.createCell(1);
            cell.setCellValue(Preferences.getBundle().getString("tabletext.money"));
            cell.setCellStyle(styleForTitle);

            cell = row.createCell( 2);
            cell.setCellValue(Preferences.getBundle().getString("tabletext.address"));
            cell.setCellStyle(styleForTitle);

            cell = row.createCell(3);
            cell.setCellValue(Preferences.getBundle().getString("tabletext.date"));
            cell.setCellStyle(styleForTitle);

            Iterator<Company> iterator = graph.getVertexIterator();

            while(iterator.hasNext()){

                Company company = iterator.next();

                row = sheet.createRow(sheet.getLastRowNum()+1);

                cell = row.createCell(0);
                cell.setCellValue(company.getTitle());

                cell = row.createCell(1);
                cell.setCellValue(company.getMoneyCapital());
                cell.setCellStyle(styleRegular);

                cell = row.createCell( 2);
                cell.setCellValue(company.getAddress());
                cell.setCellStyle(styleRegular);

                cell = row.createCell(3);
                cell.setCellValue(company.getDate().toString());
                cell.setCellStyle(styleRegular);
            }
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);

        } catch (NullPointerException npE){

            MyAlerts.getAlert(MyAlerts.AlertType.UNKNOWN);
            npE.printStackTrace();
        }
    }


    private static void createSecondSheet(HSSFWorkbook workbook, IvascapeGraph graph){

        try {
            HSSFSheet sheet = workbook.createSheet(Preferences.getBundle().getString("excelsheet.links"));
            Cell cell;
            Row row = sheet.createRow(0);
            HSSFCellStyle styleForTitle = createStyleForTitle(workbook);
            HSSFCellStyle styleRegular = createRegularStyle(workbook);
            cell = row.createCell(0);
            cell.setCellValue(Preferences.getBundle().getString("edittabs.neighbour"));
            cell.setCellStyle(styleForTitle);

            cell = row.createCell(1);
            cell.setCellValue(Preferences.getBundle().getString("edittabs.neighbour"));
            cell.setCellStyle(styleForTitle);

            cell = row.createCell(2);
            cell.setCellValue(Preferences.getBundle().getString("edittabs.price"));
            cell.setCellStyle(styleForTitle);

            cell = row.createCell(4);
            cell.setCellValue(Preferences.getBundle().getString("excelsheet.summ"));
            cell.setCellStyle(styleForTitle);

            int count = 1;

            for(Iterator<Link> iterator = graph.getEdgeIterator(); iterator.hasNext();) {

                Link link = iterator.next();

                row = sheet.createRow(count++);

                cell = row.createCell(0);
                cell.setCellValue(link.one().getTitle());
                cell.setCellStyle(styleRegular);

                cell = row.createCell(1);
                cell.setCellValue(link.two().getTitle());
                cell.setCellStyle(styleRegular);

                cell = row.createCell(2);
                cell.setCellValue(link.getPrice());
                cell.setCellStyle(styleRegular);
            }

            for (int i = 0; i < graph.size(); i ++) {

                for (int j = i; j < graph.size(); j ++){

                    if (graph.getEdge(i,j) != null){

                        row = sheet.createRow(count++);

                        cell = row.createCell(0);
                        cell.setCellValue(graph.getEdge(i,j).one().getTitle());
                        cell.setCellStyle(styleRegular);

                        cell = row.createCell(1);
                        cell.setCellValue(graph.getEdge(i,j).two().getTitle());
                        cell.setCellStyle(styleRegular);

                        cell = row.createCell(2);
                        cell.setCellValue(graph.getEdge(i,j).getPrice());
                        cell.setCellStyle(styleRegular);
                    }
                }
            }

            row = sheet.createRow(count);
            cell = row.createCell(4);
            cell.setCellFormula("SUM(C2:C" + count + ")");

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(4);

        } catch (NullPointerException npE){

            MyAlerts.getAlert(MyAlerts.AlertType.UNKNOWN);
            npE.printStackTrace();
        }
    }
}

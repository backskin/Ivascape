package ivascape.controller;

import ivascape.MainApp;
import ivascape.model.Company;
import ivascape.model.Graph;
import ivascape.model.Link;

import javafx.stage.Stage;

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

import static ivascape.view.serve.MyAlerts.*;

public class ExcelWorker {

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


    public static boolean saveItAsXLS(Graph<Company, Link> graph, File file){

        if (file == null) return false;

        HSSFWorkbook workbook = new HSSFWorkbook();

        try {
            createFirstSheet(workbook,graph);
            createSecondSheet(workbook,graph);
            FileOutputStream outFile = new FileOutputStream(file);
            workbook.unwriteProtectWorkbook();
            workbook.write(outFile);
            outFile.close();
        }
        catch (NullPointerException | IOException npE){

            getAlert(MyAlertType.UNKNOWN, null);
            npE.printStackTrace();
        }

        return false;
    }

    private static void createFirstSheet(HSSFWorkbook workbook, Graph<Company, Link> graph){

        try {

            HSSFSheet sheet = workbook.createSheet(MainApp.bundle.getString("excelsheet.cmps"));
            Cell cell;
            Row row = sheet.createRow(0);

            HSSFCellStyle styleForTitle = createStyleForTitle(workbook);
            HSSFCellStyle styleRegular = createRegularStyle(workbook);

            cell = row.createCell(0);
            cell.setCellValue(MainApp.bundle.getString("tabletext.title"));
            cell.setCellStyle(styleForTitle);

            cell = row.createCell(1);
            cell.setCellValue(MainApp.bundle.getString("tabletext.money"));
            cell.setCellStyle(styleForTitle);

            cell = row.createCell( 2);
            cell.setCellValue(MainApp.bundle.getString("tabletext.address"));
            cell.setCellStyle(styleForTitle);

            cell = row.createCell(3);
            cell.setCellValue(MainApp.bundle.getString("tabletext.date"));
            cell.setCellStyle(styleForTitle);

            for (int i = 0; i < graph.getVerSize(); i ++){

                row = sheet.createRow(i+1);

                cell = row.createCell(0);
                cell.setCellValue(graph.getVer(i).getTitle());

                cell = row.createCell(1);
                cell.setCellValue(graph.getVer(i).getMoneyCapital());
                cell.setCellStyle(styleRegular);

                cell = row.createCell( 2);
                cell.setCellValue(graph.getVer(i).getAddress());
                cell.setCellStyle(styleRegular);

                cell = row.createCell(3);
                cell.setCellValue(graph.getVer(i).getDate().toString());
                cell.setCellStyle(styleRegular);
            }
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);

        } catch (NullPointerException npE){

            getAlert(MyAlertType.UNKNOWN,new Stage());
            npE.printStackTrace();
        }
    }


    private static void createSecondSheet(HSSFWorkbook workbook, Graph<Company,Link> graph){

        try {
            HSSFSheet sheet = workbook.createSheet(MainApp.bundle.getString("excelsheet.links"));
            Cell cell;
            Row row = sheet.createRow(0);
            HSSFCellStyle styleForTitle = createStyleForTitle(workbook);
            HSSFCellStyle styleRegular = createRegularStyle(workbook);
            cell = row.createCell(0);
            cell.setCellValue(MainApp.bundle.getString("edittabs.neighbour"));
            cell.setCellStyle(styleForTitle);

            cell = row.createCell(1);
            cell.setCellValue(MainApp.bundle.getString("edittabs.neighbour"));
            cell.setCellStyle(styleForTitle);

            cell = row.createCell(2);
            cell.setCellValue(MainApp.bundle.getString("edittabs.price"));
            cell.setCellStyle(styleForTitle);

            cell = row.createCell(4);
            cell.setCellValue(MainApp.bundle.getString("excelsheet.summ"));
            cell.setCellStyle(styleForTitle);

            int count = 1;
            for (int i = 0; i < graph.getVerSize(); i ++) {

                for (int j = i; j < graph.getVerSize(); j ++){

                    if (graph.getEdge(i,j) != null){

                        row = sheet.createRow(count++);

                        cell = row.createCell(0);
                        cell.setCellValue(graph.getEdge(i,j).getOne().getTitle());
                        cell.setCellStyle(styleRegular);

                        cell = row.createCell(1);
                        cell.setCellValue(graph.getEdge(i,j).getTwo().getTitle());
                        cell.setCellStyle(styleRegular);

                        cell = row.createCell(2);
                        cell.setCellValue(graph.getEdge(i,j).getPrice());
                        cell.setCellStyle(styleRegular);
                    }
                }
            }

            row = sheet.createRow(count+1);
            cell = row.createCell(4);
            cell.setCellFormula("SUM(C2:C" + count + ")");

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(4);

        } catch (NullPointerException npE){

            getAlert(MyAlertType.UNKNOWN, null);
            npE.printStackTrace();
        }
    }
}

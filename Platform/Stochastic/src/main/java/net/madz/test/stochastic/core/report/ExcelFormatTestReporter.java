package net.madz.test.stochastic.core.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExcelFormatTestReporter {

    private final HSSFWorkbook workbook;
    private volatile int currentSheetNumber = 0;
    private volatile HSSFSheet currentSheet;
    private final OutputStream targetOutputStream;

    private void log(String message) {
        System.out.println(message);
    }

    public ExcelFormatTestReporter(final String fileName) {
        final Date date = new Date();
        final SimpleDateFormat formater = new SimpleDateFormat("yyyyMMddHHmmss");
        final String suffix = formater.format(date) + ".xls";
        final String generatedName;
        if ( fileName.endsWith(".xsl") ) {
            final int lastIndex = fileName.lastIndexOf(".xls");
            generatedName = fileName.substring(0, lastIndex) + suffix;
        } else {
            generatedName = fileName + suffix;
        }
        final File targetFile = new File(generatedName);
        log("Report File: " + targetFile.getAbsolutePath());
        if ( targetFile.exists() ) {
            throw new IllegalStateException();
        } else {
            try {
                if ( !targetFile.createNewFile() ) {
                    throw new IllegalStateException("Failed to create File: " + targetFile.getAbsolutePath());
                }
                targetOutputStream = new FileOutputStream(targetFile);
                workbook = new HSSFWorkbook();// Workbook.createWorkbook(targetOutputStream);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public synchronized void createWorksheet(String sheetName) {
        currentSheet = workbook.createSheet(sheetName);
        workbook.setSheetOrder(sheetName, currentSheetNumber);
        currentSheetNumber++;
    }

    public synchronized void createWorksheet(String sheetName, Cell[] headers) throws Exception {
        createWorksheet(sheetName);
        createHeader(headers);
    }

    public synchronized void createHeader(Cell[] headers) throws Exception {
        writeCellsInRow(headers);
        // final CellView cv = new CellView();
        // cv.setAutosize(true);
        // for (int i = 0; i < headers.length; i++) {
        // currentSheet.setColumnView(i, cv);
        // }
    }

    public synchronized void createRow(Cell[] cellsInRow) throws Exception {
        writeCellsInRow(cellsInRow);
    }

    public synchronized void writeCellsInRow(Cell[] headers) throws Exception {
        if ( null == currentSheet ) {
            throw new IllegalStateException("Please createWorksheet first.");
        }
        if ( null == headers ) {
            throw new IllegalArgumentException("Please provide not-null headers");
        }
        for ( Cell cell : headers ) {
            cell.writeTo(currentSheet);
        }
    }

    public synchronized void close() throws Exception {
        workbook.write(targetOutputStream);
        targetOutputStream.close();
    }

    public static void main(String[] args) {
        ExcelFormatTestReporter reporter = new ExcelFormatTestReporter("CHS-TestReport");
        Cell[] headers = new Cell[10];
        headers[0] = new MergeableCell("Status", 0, 0, 0, 1);
        headers[1] = new MergeableCell("Reason", 1, 0, 1, 1);
        List<String> validateList = new ArrayList<String>();
        validateList.add("Enabled");
        validateList.add("Disabled");
        headers[2] = new MergeableCell("SCA Perm", validateList, 2, 0, 2, 1);
        headers[3] = new MergeableCell("CHS Perm", validateList, 3, 0, 3, 1);
        headers[4] = new MergeableCell("Z-A", 4, 0, 5, 0);
        headers[5] = new MergeableCell("Z-B", 6, 0, 7, 0);
        validateList = new ArrayList<String>();
        validateList.add("Draft");
        validateList.add("Active");
        validateList.add("Canceled");
        headers[6] = new Cell("Status", validateList, 4, 1);
        headers[8] = new Cell("Status", validateList, 6, 1);
        validateList = new ArrayList<String>();
        validateList.add("Available");
        validateList.add("Unavailable");
        headers[7] = new Cell("CrmAccountId", validateList, 5, 1);
        headers[9] = new Cell("CrmAccountId", validateList, 7, 1);
        final String sheetName = "AB";
        try {
            reporter.createWorksheet(sheetName, headers);
            // CellView cv = new CellView();
            // cv.setAutosize(true);
            //
            // reporter.currentSheet.setColumnView(0, cv);
            // // reporter.currentSheet.setColumnView(1, cv);
            // reporter.currentSheet.setColumnView(2, cv);
            // reporter.currentSheet.setColumnView(3, cv);
            // reporter.currentSheet.setColumnView(4, cv);
            // reporter.currentSheet.setColumnView(5, cv);
            // reporter.currentSheet.setColumnView(6, cv);
            // reporter.currentSheet.setColumnView(7, cv);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reporter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

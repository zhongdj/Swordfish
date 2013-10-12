package net.madz.test.stochastic.core.report;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

public class Cell {

    protected final String label;
    protected final List<String> validateList = new ArrayList<String>();
    protected final int column;
    protected final int row;
    @SuppressWarnings("unused")
    private boolean wrapped;

    public Cell(String label, int column, int row) {
        this.label = label;
        this.column = column;
        this.row = row;
    }

    public Cell(String label, List<String> validateList, int column, int row) {
        this(label, column, row);
        if ( null != validateList && 0 < validateList.size() ) {
            this.validateList.addAll(validateList);
        }
    }

    public void setWrapped(boolean wrapped) {
        this.wrapped = wrapped;
    }

    @SuppressWarnings("deprecation")
    public HSSFCell writeTo(HSSFSheet currentSheet) throws Exception {
        HSSFRow rowInSheet = currentSheet.getRow(getRow());
        if ( null == rowInSheet ) {
            rowInSheet = currentSheet.createRow(getRow());
        }
        HSSFCell hssfCell = rowInSheet.createCell((short) getColumn());
        hssfCell.setCellValue(label);
        hssfCell.setCellType(HSSFCell.CELL_TYPE_STRING);
        return hssfCell;
        // if (0 < this.validateList.size()) {
        // CellRangeAddressList addressList = new CellRangeAddressList(
        // 0, 0, 0, 0);
        // DVConstraint dvConstraint =
        // DVConstraint.createExplicitListConstraint(
        // new String[]{"10", "20", "30"});
        // DataValidation dataValidation = new HSSFDataValidation
        // (addressList, dvConstraint);
        // dataValidation.setSuppressDropDownArrow(true);
        // currentSheet.addValidationData(dataValidation);
        // }
        // final Label labelCell = new Label(getColumn(), getRow(), label);
        // if (wrapped) {
        // WritableCellFormat cf = new WritableCellFormat();
        // cf.setWrap(true);
        // labelCell.setCellFormat(cf);
        // }
        // final WritableCellFeatures feature = new WritableCellFeatures();
        // feature.setDataValidationList(validateList);
        // labelCell.setCellFeatures(feature);
        // }
        // currentSheet.addCell(labelCell);
        // return labelCell;
    }

    protected int getColumn() {
        return column;
    }

    protected int getRow() {
        return row;
    }
}
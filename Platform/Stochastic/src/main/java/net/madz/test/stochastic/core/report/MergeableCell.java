package net.madz.test.stochastic.core.report;

import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.util.Region;

@SuppressWarnings("deprecation")
public class MergeableCell extends Cell {

    private final int bottomRightColumn;
    private final int bottomRightRow;

    public MergeableCell(String label, int topLeftColumn, int topLeftRow, int bottomRightColumn, int bottomRightRow) {
        super(label, topLeftColumn, topLeftRow);
        this.bottomRightColumn = bottomRightColumn;
        this.bottomRightRow = bottomRightRow;
    }

    public MergeableCell(String label, List<String> validateList, int topLeftColumn, int topLeftRow, int bottomRightColumn, int bottomRightRow) {
        super(label, validateList, topLeftColumn, topLeftRow);
        this.bottomRightColumn = bottomRightColumn;
        this.bottomRightRow = bottomRightRow;
    }

    @Override
    public HSSFCell writeTo(HSSFSheet currentSheet) throws Exception {
        final HSSFCell labelCell = super.writeTo(currentSheet);
        Region region = new Region();
        region.setColumnFrom((short) this.column);
        region.setColumnTo((short) this.bottomRightColumn);
        region.setRowFrom((short) this.row);
        region.setRowTo((short) this.bottomRightRow);
        currentSheet.addMergedRegion(region);
        return labelCell;
    }
}

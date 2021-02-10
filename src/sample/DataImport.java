package sample;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;



public class DataImport {

    public void importData (String fileName) throws IOException{
        Workbook wb = WorkbookFactory.create(new File(fileName));
        Sheet dataIn = wb.getSheetAt(0);
        DataFormatter df = new DataFormatter();
        for (int i = 1; i < dataIn.getLastRowNum() + 1; i++) {
            for (int k = 1; k < dataIn.getRow(i).getLastCellNum(); k++) {
                String cellValue = df.formatCellValue(dataIn.getRow(i).getCell(k));
            }
        }
        wb.close();
    }
}

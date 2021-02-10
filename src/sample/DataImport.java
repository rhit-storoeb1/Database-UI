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
        Sheet trainingLog = wb.getSheetAt(0);
        Sheet performanceList = wb.getSheetAt(1);
        DataFormatter df = new DataFormatter();
        for(int i = 1; i < performanceList.getLastRowNum() + 1; i++){
            for(int k = 0; k < performanceList.getRow(i).getLastCellNum(); k++){
                String cellValue = df.formatCellValue(performanceList.getRow(i).getCell(k));
                //System.out.println(cellValue);
            }
        }
        for (int i = 1; i < trainingLog.getLastRowNum() + 1; i++){
            for (int k = 0; k < trainingLog.getRow(i).getLastCellNum(); k++) {
                String cellValue = df.formatCellValue(trainingLog.getRow(i).getCell(k));
                //System.out.println(cellValue);
            }
        }
        wb.close();
    }


}

package utils.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtility {
    public static void saveResultsBasedOnTemplate(Map<String, String> allCurrencies, String templatePath) throws IOException {
        XSSFWorkbook workbook = readFromWorkbook(templatePath);
        XSSFSheet sheet = workbook.getSheetAt(0);

        Map<String, Integer> currenciesWithRowNums = getCurrenciesWithRowNumsFromTemplate(sheet);

        currenciesWithRowNums.forEach((currency, rowNum) -> {
            Cell cell = sheet.getRow(rowNum).createCell(2);
            cell.setCellValue(allCurrencies.getOrDefault(currency, "N/A"));
        });

        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
        writeToWorkbook(workbook, "src/main/resources/results/" + dateTime.format(formatter) + ".xlsx");
    }

    private static XSSFWorkbook readFromWorkbook(String path) throws IOException {
        FileInputStream file = new FileInputStream(path);
        return new XSSFWorkbook(file);
    }

    private static void writeToWorkbook(XSSFWorkbook workbook, String path) throws IOException {
        FileOutputStream out = new FileOutputStream(path);
        workbook.write(out);
        out.close();
    }

    private static Map<String, Integer> getCurrenciesWithRowNumsFromTemplate(XSSFSheet sheet) {
        Map<String, Integer> currencies = new HashMap<>();
        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;
            String value = row.getCell(row.getFirstCellNum()).getStringCellValue();
            currencies.put(value, row.getRowNum());
        }
        return currencies;
    }
}

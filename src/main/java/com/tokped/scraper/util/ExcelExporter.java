package com.tokped.scraper.util;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ExcelExporter<T> {
    private final Class<?>[] primitiveClasses = {Double.class, Float.class, Long.class, Integer.class, Short.class, Character.class,
            Byte.class, Boolean.class, void.class, String.class};

    private final XSSFWorkbook workbook;

    private XSSFSheet sheet;

    private final List<T> listObject;

    private final CellStyle headerStyle;

    private final CellStyle rowStyle;

    private String[] headers;

    private String[] fields;

    private final String sheetName;

    public ExcelExporter(String sheetName, Collection<T> listObjects) {
        this.listObject = new ArrayList<>(listObjects);
        this.sheetName = sheetName;
        this.workbook = new XSSFWorkbook();

        XSSFFont headerFont = workbook.createFont();
        headerFont.setFontHeight(14);
        headerFont.setBold(true);

        XSSFFont rowFont = workbook.createFont();
        rowFont.setFontHeight(12);

        this.headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setFillBackgroundColor(IndexedColors.GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.BRICKS);

        this.rowStyle = workbook.createCellStyle();
        rowStyle.setFont(rowFont);
    }

    public void writeHeaderLine() {
        sheet = workbook.createSheet(listObject.getClass().getName());
        Row row = sheet.createRow(0);

        int columnCount = 0;

        for(String header : headers) {
            createCell(row, columnCount++, header, headerStyle);
        }
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);

        Cell cell = row.createCell(columnCount);

        if(value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if(value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue((String) value);
        }

        cell.setCellStyle(style);
    }

    public void writeDataLines() {
        int rowCount = 1;

        try {
            for(Object object : listObject) {
                int columnCount = 0;
                Row row = sheet.createRow(rowCount++);

                for(String fieldName : fields) {
                    String value;

                    if(fieldName.contains(".")) {
                        value = getNestedValue(object, Arrays.asList(fieldName.split("\\.")));
                    } else {
                        Field field = object.getClass().getDeclaredField(fieldName);
                        field.setAccessible(true);

                        value = field.get(object).toString();
                    }

                    createCell(row, columnCount++, value, rowStyle);
                }
            }
        } catch (Exception e){
            LoggerFactory.getLogger(this.getClass()).error(e.getMessage());
        }
    }

    private String getNestedValue(Object param, List<String> fields) {
        Field resultField = null;
        Object value = param;
        String result = "";

        try{
            for(String field : fields) {
                resultField = value.getClass().getDeclaredField(field);
                resultField.setAccessible(true);

                if(Boolean.valueOf(Arrays.asList(primitiveClasses).contains(resultField.getType())).equals(true)) {
                    break;
                } else {
                    value = resultField.get(value);
                }
            }

            assert resultField != null;
            result = String.valueOf(resultField.get(value));

        } catch (Exception e){
            LoggerFactory.getLogger(this.getClass()).error(e.getMessage());
        }

        return result;
    }

    public void createTable(String[] headers, String[] fields){
        this.headers = headers;
        this.fields = fields;

        writeHeaderLine();

        writeDataLines();
    }

    public ByteArrayInputStream export() {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (Exception e) {
            LoggerFactory.getLogger(this.getClass()).error(e.getMessage());

            return null;
        }
    }
}

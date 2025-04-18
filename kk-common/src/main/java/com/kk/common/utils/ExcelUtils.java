package com.kk.common.utils;

import com.kk.common.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author kk
 * @since 2024/9/29
 */
public class ExcelUtils {
    private static DataFormatter dataFormatter = new DataFormatter();
    /**
     * 读取excel文档数据到内存
     *
     * @param filePath  文件路劲
     * @param sheetName sheet（为空，则默认读取第一个）
     * @return
     */
    public static List<Map<String, Object>> readExcel(String filePath, String sheetName,int headRowNum) throws Exception {
        if(StringUtils.isEmpty(filePath))
        {
            throw new BusinessException("文件路径为空！");
        }
        List<Map<String, Object>> data = new ArrayList<>();
        FileInputStream fileInputStream = new FileInputStream(new File(filePath));
        Workbook workbook =   new XSSFWorkbook(fileInputStream);
        Sheet sheet = null;
        if(workbook==null) return null;
        if(StringUtils.isEmpty(sheetName))
        {
            sheet = workbook.getSheetAt(0);
        }else {
            sheet = workbook.getSheet(sheetName);
        }
        if(sheet == null)
        {
            return null;
        }

        Map<String,String> headMap = new HashMap<>();
        if(headRowNum!=-1) { //headRowNum 为-1，不设置表头，默认用列数为表头
            if(headRowNum >sheet.getLastRowNum() || headRowNum < sheet.getFirstRowNum())
            {
                headRowNum = sheet.getFirstRowNum();
            }
            Row headRow = sheet.getRow(headRowNum);
            int headRowCellNums = headRow.getLastCellNum();
            System.out.println("headRowCellNums"+headRowCellNums);
            for (int i = 0; i < headRowCellNums; i++) {
                headMap.put(i + "", headRow.getCell(i).getStringCellValue());

            }
        }
        int rowNums = sheet.getLastRowNum();


        for(int i=headRowNum+1;i<=rowNums;i++)
        {
            Map<String, Object> rowData = new HashMap<>();
            Row innRow = sheet.getRow(i);
            if(innRow != null)
            {
                for(int j =0;j<innRow.getLastCellNum();j++)
                {
                    String headKey = headMap==null || StringUtils.isEmpty( headMap.get(j+"") )? j+"":headMap.get(j+""); //找不到表头的，默认列数
                    rowData.put(headKey,getStringValueFromCell(innRow.getCell(j)));

                }
                data.add(rowData);
            }

        }
        fileInputStream.close();

        return data;
    }

    /**
     * 对表格中数值进行格式化
     * @param cell cell格
     * @return 返回String类型
     */
    public static Object getStringValueFromCell(Cell cell) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        Object cellValue = "";
        if(cell == null) {
            return cellValue;
        }
        else if(cell.getCellType() == CellType.STRING) {
            cellValue = cell.getStringCellValue();
        }

        else if(cell.getCellType() == CellType.NUMERIC) {
            if(HSSFDateUtil.isCellDateFormatted(cell)) {
                //日期类型
                cellValue = dataFormatter.formatCellValue(cell);
            } else {
                //数字格式
                cellValue = decimalFormat.format(cell.getNumericCellValue());
            }
        }
        else if(cell.getCellType() == CellType.BLANK) {
            cellValue = "";
        }
        else if(cell.getCellType() == CellType.BOOLEAN) {
            cellValue = String.valueOf(cell.getBooleanCellValue());
        }
        else if(cell.getCellType() == CellType.ERROR) {
            cellValue = "";
        }
        else if(cell.getCellType() == CellType.FORMULA) {
            // 处理公式，获取表格的值而并非表格的公式
            try {
                cellValue = decimalFormat.format(cell.getNumericCellValue());
            } catch (IllegalStateException e) {
                cellValue = decimalFormat.format(cell.getRichStringCellValue());
            }
        }
        return cellValue;
    }



}

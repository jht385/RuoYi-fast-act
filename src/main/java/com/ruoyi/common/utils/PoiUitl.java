package com.ruoyi.common.utils;

import static org.apache.poi.ss.usermodel.CellType.NUMERIC;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Writer: loong
 */

public class PoiUitl {

	/**
	 * 替换Excel模板文件内容
	 * 
	 * @param item 文档数据
	 */
	public static Workbook replaceModel(Map<String, Object> item, InputStream is) {
		try {
			Workbook wb = new XSSFWorkbook(is);
			Sheet sheet = wb.getSheetAt(0);
			Iterator<Row> rows = sheet.rowIterator();
			while (rows.hasNext()) {
				Row row = rows.next();
				if (row != null) {
					int num = row.getLastCellNum();
					if (row.getRowNum() > 33) {
						break;
					}
					for (int i = 0; i < num; i++) {
						Cell cell = row.getCell(i);
						// 设置单元格为String格式。excel是有多种类型的单元格的。
						if (cell != null) {
							cell.setCellType(CellType.STRING);
						}
						if (cell == null || cell.getStringCellValue() == null) {
							continue;
						}
						String value = cell.getStringCellValue();
						if (!"".equals(value)) {
							if (null != item.get(value)) {
								cell.setCellValue(item.get(value) + "");
							} else if (value.indexOf('$') != -1) {
								cell.setCellValue(getValue(value, item));
							}
						} else {
							cell.setCellValue("");
						}
					}
				}
			}
			// 输出文件
			return wb;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getValue(String value, Map<String, Object> item) {

		// 获取$出现的次数。
		int num = 0;
		for (int i = 0; i < value.length(); i++) {
			if ("$".equals(value.substring(i, i + 1))) {
				num++;
			}
		}
		for (int i = 0; i < num; i++) {
			String str = value.substring(value.indexOf('{') + 1, value.indexOf('}'));
			if (null == item.get(str)) {
				value = value.replace("${" + str + "}", "");
			} else {
				value = value.replace("${" + str + "}", (String) item.get(str));
			}
		}
		return value;
	}

	public static String getStr(Cell cell) {
		String str = "";
		if (cell.getCellType() == NUMERIC) {
			DecimalFormat df = new DecimalFormat("0");
			str = df.format(cell.getNumericCellValue());

//            str = cell.getNumericCellValue()+"";
//            if(str.indexOf(".")!=-1){
//                str = str.substring(0,str.indexOf(".")-1);
//            }
		} else {
			str = cell.getStringCellValue();
		}
		return str;
	}

}

package gameart.tools.excel;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExcelUtil {
	/**
	 * 读取Excel有效内容
	 *
	 * @param sheet
	 * @return
	 * @throws Exception
	 */
	public static List<List<Object>> readExcel(XSSFSheet sheet) {
		int rowNum = sheet.getLastRowNum(); // 总行数

		List<List<Object>> content = new ArrayList<>();
		Set<Integer> ignoreSet = new HashSet<>();
		List<Object> tempRowList = new ArrayList<>();

		Object serverConf = getCellValue(sheet.getRow(0).getCell(1));
		if(serverConf != null){
			String str = serverConf.toString();
			if(str.split("\\|").length < 3) {
				if(str.endsWith("bean|") || str.endsWith("bean")
					|| str.endsWith("json") || str.endsWith("json|")
					|| str.endsWith("common") || str.endsWith("common|")) {
					ignoreSet.add(1);
				}
			}
		}

		XSSFRow tempRow = sheet.getRow(1);
		int cellSize = tempRow.getLastCellNum();
		for (int i = 0; i < cellSize; i++) {
			Object data = getCellValue(tempRow.getCell(i));
			if (data == null || "".equals(data.toString().trim()) || "null".equals(data.toString().trim())) {
				ignoreSet.add(i);
				continue;
			}
			if(ignoreSet.contains(i))
				continue;
			tempRowList.add(data);
		}

		for (int j = 0; j <= 4; j++) {
			if (j == 1) {
				content.add(tempRowList);
				continue;
			}
			List<Object> one = new ArrayList<>();
			XSSFRow oneRow = sheet.getRow(j);
			if (j == 3 && oneRow == null) {
				content.add(one);
				continue;
			}
			for (int i = 0; i < cellSize; i++) {
				if (ignoreSet.contains(i))
					continue;
				Object data = getCellValue(oneRow.getCell(i));
				one.add(data);
			}
			content.add(one);
		}

		for (int i = 4; i <= rowNum; i++) {
			List<Object> rowData = new ArrayList<>();
			XSSFRow row = sheet.getRow(i);
			if (row == null)
				continue;
			for (int j = 0; j < cellSize; j++) {
				if (ignoreSet.contains(j))
					continue;
				Object data = getCellValue(row.getCell(j));
				if (j == 0 && (data == null || "".equals(data.toString().trim()) || "null".equals(data.toString().trim()))) {//一行都是空的
					break;
				}
				rowData.add(data);
			}
			if (rowData.size() <= 0)
				continue;
			content.add(rowData);
		}
		return content;
	}

	/**
	 * 获取单元格数据内容
	 *
	 * @param xssfCell HSSFCell Excel单元格
	 * @return Object
	 */
	public static Object getCellValue(XSSFCell xssfCell) {
		Object strCell = null;
		if (xssfCell == null) {
			return strCell;
		}
		switch (xssfCell.getCellType()) {
			case XSSFCell.CELL_TYPE_BLANK: // blank类型
				break;
			case XSSFCell.CELL_TYPE_STRING: // String类型
				strCell = xssfCell.getStringCellValue();
				break;
			case XSSFCell.CELL_TYPE_BOOLEAN: // booelan类型
				strCell = xssfCell.getBooleanCellValue();
				break;
			case XSSFCell.CELL_TYPE_NUMERIC: // numeric类型(包含了日期类型)
				boolean isDate = HSSFDateUtil.isCellDateFormatted(xssfCell);
				if (isDate) {
					strCell = xssfCell.getDateCellValue();
				} else {
					strCell = xssfCell.getNumericCellValue();
					DecimalFormat df = new DecimalFormat("#.######");    //格式化为四位小数，按自己需求选择；
					strCell = df.format(strCell);
				}
				break;
			case XSSFCell.CELL_TYPE_FORMULA: // 公式类型
				try {
					strCell = xssfCell.getNumericCellValue();
					DecimalFormat df = new DecimalFormat("#.######");    //格式化为四位小数，按自己需求选择；
					strCell = df.format(strCell);
				} catch (Exception e) {
					strCell = xssfCell.getStringCellValue();
				}
				break;
		}
		return strCell;
	}
}

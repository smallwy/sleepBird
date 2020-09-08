package gameart.tools.excel;

import com.alibaba.fastjson.JSON;
import com.sun.media.sound.InvalidDataException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Operate {
	/**
	 * @param files
	 */
	public static void operStart(File[] files, String exportPath) throws Exception {
		long start = System.currentTimeMillis();
		// 循环操作文件
		for (File file : files) {
			try {
				oneFileMake(file, exportPath);
			} catch (Exception e) {
				throw new Exception("错误表数据:" + file.getName() + " | " + e.getMessage(), e);
			}
		}
		// 本次操作总时长
		long total = (System.currentTimeMillis() - start) / 1000;
		if (total != 0)
			System.out.println("##成功操作完所有配表--用时：" + total + "秒##");
	}


	/**
	 * 一个文件生成
	 *
	 * @param file
	 * @throws Exception
	 */
	public static void oneFileMake(File file, String exportPath) throws Exception {
		String fileName = file.getName();
		System.out.println("-> 开始处理文件[" + fileName + "]");
		XSSFWorkbook workBook;
		try {
			workBook = new XSSFWorkbook(initStream(file));
		} catch (IOException e) {
			System.err.println("init Excel read util error-" + e.getMessage());
			throw new Exception("init Excel read util error-" + e.getMessage());
		}
		// 当前文件中的所有sheet
		for (int i = 0; i < workBook.getNumberOfSheets(); i++) {
			String sname = workBook.getSheetName(i);
			try {
				XSSFSheet sheet = workBook.getSheetAt(i);
				if (isChineseChar(sname))
					continue;
				if (sheet.getRow(0) == null || sheet.getRow(0).getCell(1) == null)
					continue;

				String json = sheet.getRow(0).getCell(1).toString().toLowerCase().trim();

				boolean jsonBoolean = json.contains("json|") || json.endsWith("json");
				boolean commonBoolean = json.contains("common");

				if (!jsonBoolean && !commonBoolean)
					continue;

				System.out.println("-> " + fileName + "开始处理sheet表[" + sname + "]");

				List<List<Object>> sheetData = ExcelUtil.readExcel(sheet);
				if (sheetData == null)
					continue;
				String field = makeField(sheetData);
				String array = makeArray(fileName + "==>表的字表[" + sname + "]", sheetData);
				String data = "{\r\n" + field + "\r\n" + array + "}";


				boolean makeBean = sheet.getRow(0).getCell(1).toString().toLowerCase().trim().contains("bean");

				if (jsonBoolean) {
					String packageName ="gameart.bean";
					String jsonFileOutPath = exportPath + "src/main/resources/config/";
					String javaFilePath = exportPath + "src/main/java/gameart/bean/";
					write2File(jsonFileOutPath + sname + ".json", data);
					if (makeBean) {
						Json2BeanUtil2.makeJavaFile(fileName,false, packageName, javaFilePath, sname, sheetData, data);
					}
					System.out.println("===> game 表数据生成成功！");
				}

				if (commonBoolean) {
					String packageName = "com.gameart.common.config";
					String jsonFileOutPath = exportPath + "../game-common-all/game-common/src/main/resources/config/";
					String javaFilePath = exportPath + "../game-common-all/game-common/src/main/java/com/gameart/common/config/";
					write2File(jsonFileOutPath + sname + ".json", data);
					if (makeBean) {
						Json2BeanUtil2.makeJavaFile(fileName,true, packageName, javaFilePath, sname, sheetData, data);
					}
					System.out.println("===> common 表数据生成成功！");
				}
			} catch (Exception e) {
				throw new Exception("错误子表：" + sname + " | " + e.getMessage(), e);
			}
		}
	}


	/**
	 * 写入文件
	 *
	 * @param jsonFileOutPath
	 * @param data
	 * @throws IOException
	 */
	public static void write2File(String jsonFileOutPath, String data) throws IOException {
		File file = new File(jsonFileOutPath);
		File fileParent = file.getParentFile();
		if (!fileParent.exists()) {
			fileParent.mkdirs();
		}
		if (file.exists()) {
			file.delete();
		}

		file.createNewFile();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
		bw.write(data);
		// 关闭输出流
		bw.close();
	}


	/**
	 * 判断是否是中文
	 *
	 * @param str
	 * @return
	 */
	public static boolean isChineseChar(String str) {
		boolean temp = false;
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(str);
		if (m.find()) {
			temp = true;
		}
		return temp;
	}

	/**
	 * 输入流初始化
	 *
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	public static InputStream initStream(File file) throws FileNotFoundException {
		InputStream inputStream = new FileInputStream(file);
		return inputStream;
	}


	/**
	 * @param sheetData
	 * @throws Exception
	 * @throws SQLException
	 */
	private static String makeField(List<List<Object>> sheetData) {
		List<Object> columnName = sheetData.get(1);
		List<Object> columnType = sheetData.get(2);
		LinkedHashMap<String, String> tableStructure = new LinkedHashMap<>();
		for (int i = 0; i < columnName.size(); i++) {
			// 获得表头（表字段）
			String field = columnName.get(i).toString();
			// 获得表结构
			String structure = columnType.get(i).toString();
			tableStructure.put(field, structure);
		}
		return "\t\"fields\":" + JSON.toJSONString(tableStructure) + ",";
	}

	private static String makeArray(String name, List<List<Object>> sheetData) throws InvalidDataException {
		StringBuilder allsb = new StringBuilder();
		allsb.append("\t\"array\":[ \r\n");

		List<Object> columnType = sheetData.get(0);
		List<Object> columnName = sheetData.get(1);
		String friType = sheetData.get(2).get(0).toString();
		List<Object> defaultValueList = sheetData.get(3);

		for (int i = 5; i < sheetData.size(); i++) {
			List<Object> objectsList = sheetData.get(i);
			StringBuilder newsb = new StringBuilder();

			for (int j = 0; j < columnName.size(); j++) {
				String field = columnName.get(j).toString();

				boolean have = false;
				if (columnType.get(j) != null) {
					String structure;
					String tempColType = columnType.get(j).toString().trim();

					if (j == 1 && (tempColType.contains("common") || tempColType.contains("json") || tempColType.contains("bean"))) {
						String[] split = columnType.get(j).toString().split("\\|");
						String str = split[split.length - 1].trim();
						if (str.equalsIgnoreCase("common") || str.equalsIgnoreCase("json") || str.equalsIgnoreCase("bean")) {
							structure = null;
						} else {
							structure = str;
						}
					} else {
						structure = columnType.get(j).toString().split("\\|")[0];
					}
					if (structure != null) {
						Object defaultValue = defaultValueList.size() <= 0 ? null : defaultValueList.get(j);
						Object o = objectsList.get(j);

						boolean useDefaultValue = false;
						boolean b = o == null || "".equals(o.toString().trim()) || "null".equals(o.toString().trim());
						if ("string".equalsIgnoreCase(structure)) {
							if (o == null && defaultValue == null) {
								useDefaultValue = true;
							}
						} else {
							if (b && (defaultValue == null || "".equals(defaultValue.toString().trim()) || "null".equals(defaultValue.toString().trim()))) {
								useDefaultValue = true;
							}
						}

						if(j == 0){
							structure = friType;
						}

						String value = useDefaultValue ? getDefaultValue(structure) : (b ? getValue(defaultValue) : getValue(o));
						if ("string".equalsIgnoreCase(structure)) {
							if (value.startsWith("[")) {
								newsb.append("\"" + field + "\":" + value);
							} else {
								newsb.append("\"" + field + "\":\"" + value + "\"");
							}
						} else {
							newsb.append("\"" + field + "\":" + value);
						}
						have = true;
					}
				}
				if (columnName.size() - 1 != j) {
					if (have) {
						newsb.append(",");
					}
				} else {
					boolean b = newsb.toString().endsWith(",");
					if (b) {
						newsb = newsb.delete(newsb.length() - 1, newsb.length());
					}
					newsb.append("},");
				}
			}
			if (i == sheetData.size() - 1) {
				newsb = newsb.deleteCharAt(newsb.length() - 1);
				allsb.append("\t\t{" + newsb + "\r\n");
			} else {
				allsb.append("\t\t{" + newsb + "\r\n");
			}
		}
		allsb.append("\t]\r\n");
		return allsb.toString();
	}

	private static String getValue(Object valueStr) {
		String value = valueStr.toString().trim();
//		if(valueStr instanceof Number){
//			if(value.endsWith(".0") || value.endsWith(".00") || value.endsWith(".000") || value.endsWith(".0000") | value.endsWith(".00000")){
//				return value.split("\\.")[0];
//			}
//		}
		return value;
	}


	/**
	 * 获取默认值
	 *
	 * @param structure
	 * @return
	 * @throws InvalidDataException
	 */
	private static String getDefaultValue(String structure) throws InvalidDataException {
		if ("string".equalsIgnoreCase(structure)) {
			return "";
		} else if (structure.equals("byte") || structure.equals("short") || structure.equals("int") || structure.equals("long") || structure.equals("float") || structure.equals("double") || structure.equals("boolean")) {
			return "0";
		} else if (structure.contains("[]")) {
			return "[]";
		}
		throw new InvalidDataException("未定义的数据解析类型:" + structure);
	}


}

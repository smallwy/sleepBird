package gameart.tools.excel;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

public class Excel2JsonAndJavaUtil {
	public static Map<String, String> parseMethod = new HashMap<>();

	public static void readConfig() throws IOException {
		InputStream is = Excel2JsonAndJavaUtil.class.getResourceAsStream("/config.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line;

		while((line = reader.readLine()) != null){
			String[] split = line.split("&");
			parseMethod.put(split[0].trim(), split[1].trim());
		}
	}


	/**
	 * 生成所有文件
	 * @throws Exception
	 */
	public static void makeAll(String configPath, String exportPath) throws Exception{
		readConfig();
		File dirFile = new File(configPath);
		if (!dirFile.isDirectory()) {
			return;
		}
		File[] files = pathScan(configPath);
		Operate.operStart(files, exportPath);
		makeErrorCode(configPath, exportPath);
	}

	/**
	 * 生成一个文件
	 * @param fileName
	 * @throws Exception
	 */
	public static void makeOne(String fileName, String configPath, String exportPath) throws Exception {
		readConfig();
		File file = new File(configPath + fileName);
		Operate.oneFileMake(file, exportPath);
		makeErrorCode(configPath, exportPath);
	}

	/**
	 * 错误码导入
	 */
	private static void makeErrorCode(String sourceFilePath, String exportPath) {
		try {
			String fileName = "提示信息.xlsx";
			String subTable = "CGameMessage";
			String filePath = sourceFilePath + fileName;
			File file = new File(filePath);
			if (!file.exists())
				return;
			StringBuilder builder = new StringBuilder();
			builder.append("/**\r\n * 错误码\r\n */\r\n");
			builder.append("public class ErrorCode {\r\n");

			Set<Integer> idSet = new HashSet<>();
			Set<String> nameSet = new HashSet<>();
			int size = 0;
			XSSFWorkbook workBook = new XSSFWorkbook(Operate.initStream(file));
			for (int i = 0; i < workBook.getNumberOfSheets(); i++) {
				XSSFSheet sheet = workBook.getSheetAt(i);
				String sname = workBook.getSheetName(i);
				if (subTable.equals(sname)) {
					int index = 5;
					List<List<Object>> sheetData = ExcelUtil.readExcel(sheet);
					for (List<Object> objects : sheetData) {
						if (index > 0) {
							index--;
							continue;
						}
						if (objects.size() < 5)
							continue;
						int messageId = Integer.parseInt(objects.get(0).toString().split("\\.")[0]);
						Object enObj = objects.get(1);
						if(enObj == null)
							continue;
						String en = enObj.toString();
						if(StringUtils.isEmpty(en))
							continue;
						String desc = objects.get(4).toString();
						if (desc == null)
							continue;
						if(idSet.contains(messageId)){
							throw new RuntimeException("提示信息存在相同的ID="+messageId);
						}
						if(nameSet.contains(en)){
							throw new RuntimeException("提示信息存在相同的英文名字="+en);
						}
						idSet.add(messageId);
						nameSet.add(en);
						builder.append("\t// " + desc + "\r\n");
						builder.append("\tpublic static int " + en + " = " + messageId + ";\r\n");
						builder.append("\r\n");
						size++;
					}
				}
			}
			builder.append("}\r\n");

			StringBuilder newBuilder = new StringBuilder();
			newBuilder.append("package com.gameart.config;\r\n\r\n");
			newBuilder.append(builder.toString());
			String outFilePath1 = exportPath + "game-config/src/main/java/com/gameart/config/ErrorCode.java";
			Json2BeanUtil2.writeJavaBean(newBuilder.toString(), outFilePath1);

			newBuilder = new StringBuilder();
			newBuilder.append("package com.gameart.common.config;\r\n\r\n");
			newBuilder.append(builder.toString());
			String outFilePath2 = exportPath + "../game-common-all/game-common/src/main/java/com/gameart/common/config/ErrorCode.java";
			Json2BeanUtil2.writeJavaBean(newBuilder.toString(), outFilePath2);
			System.out.println("成功写入错误码数量=" + size);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 扫描文件夹下的所有文件
	 *
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public static File[] pathScan(String filePath) throws Exception {
		File[] files;
		File file = new File(filePath);
		if (file.exists() && file.isDirectory()) {
			files = file.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					String filename = pathname.getName();
					if (filename.lastIndexOf(".xlsx") != -1) {
						return true;
					} else {
						return false;
					}
				}
			});
		} else {
			System.out.println("file path:[" + filePath + "] not exists");
			throw new Exception("file path:[" + filePath + "] not exists");
		}
		return files;
	}

}

package gameart.tools.excel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author loong
 * @Data 2019/2/28 10:50
 * @Version 1.0
 */
public class Json2BeanUtil2 {
	public static class fieldData {
		private String name;
		private String typeName;
		private String parseMethod;
		private String desc;

		public fieldData(String name, String typeName, String parseMethod, String desc) {
			this.name = name;
			this.typeName = typeName;
			this.parseMethod = parseMethod == null ? null : parseMethod.trim();
			this.desc = desc == null ? null : desc.replace("\n", " ").replace("\r\n", " ");
		}

		public String getName() {
			return name;
		}

		public String getTypeName() {
			return typeName;
		}

		public String getParseMethod() {
			return parseMethod;
		}

		public String getDesc() {
			return desc;
		}
	}

	public static void makeJavaFile(String excelFileName, boolean common, String packageName, String javaFilePath, String fileName, List<List<Object>> sheetData, String jsonStr) throws IOException {
		List<fieldData> filedList = parseFieldName(sheetData);
		JSONObject jsonObject = JSON.parseObject(jsonStr);
		JSONObject fieldsJsonObject = jsonObject.getJSONObject("fields");
		boolean b = template(excelFileName, common, packageName, fieldsJsonObject, javaFilePath, fileName, filedList, hasParseMethod(filedList));
		if (b) {
			System.out.println("json生成JavaBean " + fileName.replace(".json", ".java") + "成功！");
		}
	}

	private static boolean hasParseMethod(List<fieldData> filedList) {
		for (fieldData data : filedList) {
			if (data.parseMethod != null) {
				return true;
			}
		}
		return false;
	}

	private static List<fieldData> parseFieldName(List<List<Object>> sheetData) {
		List<Object> oneLineList = sheetData.get(0);
		List<Object> excelFiledList = sheetData.get(1);
		List<Object> descList = sheetData.get(3);

		List<fieldData> fieldDataList = new ArrayList<>();
		fieldDataList.add(new fieldData("id", "int", null, (descList == null || descList.isEmpty() || descList.get(0) == null) ? null : descList.get(0).toString()));
		for (int i = 1; i < oneLineList.size(); i++) {
			if (oneLineList.get(i) == null)
				continue;
			String name = excelFiledList.get(i).toString();
			String typeName;
			String parseMethod = null;
			String inDesc = oneLineList.get(i).toString();
			String[] blocks;
			if (i == 1) {
				String[] blocksss = inDesc.replace("json|", "&&&").replace("common|", "&&&").split("&&&");
				if (blocksss.length > 1) {
					blocks = blocksss[1].split("\\|");
				} else {
					blocks = blocksss[0].split("\\|");
				}
			} else {
				blocks = inDesc.split("\\|");
			}
			typeName = blocks[0];
			if (blocks.length > 1) {
				parseMethod = getParseMethod(blocks[1].trim());
			}
			String desc = (descList == null || descList.isEmpty() || descList.get(i) == null) ? null : descList.get(i).toString();
			fieldData fieldData = new fieldData(name, typeName, parseMethod, desc);
			fieldDataList.add(fieldData);
		}
		return fieldDataList;
	}

	private static String getParseMethod(String block) {
		String parseMethod = Excel2JsonAndJavaUtil.parseMethod.get(block);
		return parseMethod == null ? block : parseMethod;
	}

	/**
	 * IO写入JavaBean文件
	 *
	 * @param bean JavaBean字符串
	 * @param path JavaBean文件路径
	 * @return
	 * @throws IOException
	 */
	public static boolean writeJavaBean(String bean, String path) throws IOException {
		File file = new File(path);
		File fileParent = file.getParentFile();
		if (!fileParent.exists()) {
			fileParent.mkdirs();
		}
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
		bw.write(bean);
		bw.close();
		return true;
	}


	/**
	 * 首字母大写
	 *
	 * @param string
	 * @return
	 */
	private static String firstUpcase(String string) {
		if (null == string) {
			return "";
		}
		String str1 = string.substring(0, 1);
		String str2 = string.substring(1);
		return str1.toUpperCase() + str2;
	}

	/**
	 * 首字母小写
	 *
	 * @param string
	 * @return
	 */
	private static String firstLower(String string) {
		if (null == string) {
			return "";
		}
		String str1 = string.substring(0, 1);
		String str2 = string.substring(1);
		return str1.toLowerCase() + str2;
	}

	/**
	 * 生成所有字段
	 *
	 * @param jsonObject  源json对象
	 * @param builderHead
	 * @return
	 * @throws IOException
	 */
	private static String generateFields(JSONObject jsonObject, StringBuilder builderHead, List<fieldData> columnName, boolean common) {
		if (null == jsonObject || jsonObject.size() == 0) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		for (fieldData data : columnName) {
			String fieldName = data.getName();
			String fieldType = data.getTypeName();

			if (fieldType.equals("string")) {
				fieldType = formatBeanName(fieldType);
			} else if (isBaseType(fieldType)) {

			} else {
				if (isBaseType(fieldType.replace("[]", ""))) {
					fieldType = formatBeanNameLower(fieldType);
				} else {
					fieldType = formatBeanName(fieldType);
					if (!common) {
						builderHead.append("\r\n");
					}
				}
			}
			builder.append(generateField(fieldName, fieldType, data));
		}
		return builder.toString();
	}

	private static boolean isBaseType(String fieldType) {
		if (fieldType.equals("byte") || fieldType.equals("short") || fieldType.equals("int") || fieldType.equals("long") ||
				fieldType.equals("float") || fieldType.equals("double") || fieldType.equals("char") || fieldType.equals("boolean") ||
				fieldType.equals("String") || fieldType.equals("Boolean")) {
			return true;
		}
		return false;
	}

	/**
	 * 生成所有get/set方法
	 *
	 * @param jsonObject 源json对象
	 * @return
	 * @throws IOException
	 */
	private static String generateMethods(JSONObject jsonObject, List<fieldData> columnName) throws IOException {
		if (null == jsonObject || jsonObject.size() == 0) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		for (fieldData data : columnName) {
			String fieldName = data.getName();
			String fieldType = data.getTypeName();
			if (fieldType.equals("string") || fieldType.equals("string[]")) {
				fieldType = formatBeanName(fieldType);
			} else if (isBaseType(fieldType)) {

			} else {
				if (isBaseType(fieldType.replace("[]", ""))) {
					fieldType = formatBeanNameLower(fieldType);
				} else {
					fieldType = formatBeanName(fieldType);
				}
			}
			builder.append(generateMethod(fieldName, fieldType, data));
		}
		return builder.toString();
	}

	/**
	 * 生成单个字段
	 *
	 * @param field     字段名
	 * @param fieldType 字段类型
	 * @return
	 */
	private static String generateField(String field, String fieldType, fieldData data) {
		if (null == field || "" == field || null == fieldType || "" == fieldType) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		builder.append("\t//" + data.getDesc() + "\r\n");
		if (data.getParseMethod() != null) {
			int start = data.getParseMethod().lastIndexOf(" ");
			String parseMethod = data.getParseMethod().substring(start).trim();
			fieldType = data.getParseMethod().substring(0, start).trim();
			builder.append("\t@Config(parser = \"" + parseMethod + "\")\r\n");
			builder.append("\tprivate " + fieldType + " " + formatBeanNameLower(field) + ";\r\n");
		} else {
			builder.append("\tprivate " + fieldType + " " + formatBeanNameLower(field) + ";\r\n");
		}
		return builder.toString();
	}

	/**
	 * 生成单个get/set方法
	 *
	 * @param field     字段名
	 * @param fieldType 字段类型
	 * @return
	 */
	private static String generateMethod(String field, String fieldType, fieldData data) {
		if (null == field || "" == field || null == fieldType || "" == fieldType) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		if (data.getParseMethod() != null) {
			int start = data.getParseMethod().lastIndexOf(" ");
			fieldType = data.getParseMethod().substring(0, start).trim();
			builder.append("\tpublic " + fieldType + " get" + firstUpcase(field) + "(){\r\n").append("\t\treturn " + formatBeanNameLower(field) + ";\r\n").append("\t").append("}");
			builder.append("\r\n");
		} else {
			builder.append("\tpublic " + fieldType + " get" + firstUpcase(field) + "(){\r\n").append("\t\treturn " + formatBeanNameLower(field) + ";\r\n").append("\t").append("}");
			builder.append("\r\n");
			builder.append("\tpublic void set" + firstUpcase(field) + "(" + fieldType + " " + formatBeanNameLower(field) + "){\r\n").append("\t\tthis." + formatBeanNameLower(field) + " = " + formatBeanNameLower(field) + ";\r\n").append("\t").append("}");
			builder.append("\r\n");
		}
		return builder.toString();
	}

	/**
	 * 生成JavaBean
	 *
	 * @param jsonObject 源json对象
	 * @param targetPath JavaBean生成路径
	 * @param beanName   JavaBean名
	 * @return
	 * @throws IOException
	 */
	private static boolean template(String excelFileName, boolean common, String packageName, JSONObject jsonObject, String targetPath, String beanName, List<fieldData> columnName, boolean hasParseMethod) throws IOException {
		if (null == beanName || "" == beanName) {
			beanName = "JsonBean";
		}

		StringBuilder builder = new StringBuilder();

		StringBuilder builderHead = new StringBuilder();
		builderHead.append("package " + packageName + ";\r\n");
	/*	builderHead.append("com.gameart.config.*;\r\n");*/
		if (hasParseMethod) {
			if (haveListAndMap(columnName)) {
				builderHead.append("import java.util.*;\r\n");
			}
		}
		builderHead.append("\r\n\r\n");

		StringBuilder builderClass = new StringBuilder();
		builderClass.append("/** \r\n * 表名字【" + excelFileName+"】 \r\n */\r\n");
		builderClass.append("public class ").append(beanName).append(" extends SimpleConfig {");
		builderClass.append("\r\n");

		if (null != jsonObject && jsonObject.size() > 0) {
			String fields = generateFields(jsonObject, builderHead, columnName, common);
			String methods = generateMethods(jsonObject, columnName);
			builderClass.append(fields + "\r\n").append(methods);
		}

		builderClass.append(customParse(columnName, hasParseMethod));

		builderClass.append("}");
		builder.append(builderHead);
		builder.append(builderClass);
		String path = targetPath + beanName + ".java";
		boolean b = writeJavaBean(builder.toString(), path);
		return b;
	}

	private static String customParse(List<fieldData> columnName, boolean hasParseMethod) {
		if (!hasParseMethod)
			return "";

		Set<String> mothedNameSet = new HashSet<>();
		StringBuilder builder = new StringBuilder();
		builder.append("\r\n\t//====================== 自定义解析 =====================//\r\n");
		for (fieldData data : columnName) {
			String parseMethod = data.getParseMethod();
			if (parseMethod == null)
				continue;
			int start = data.getParseMethod().lastIndexOf(" ");
			String mothedName = data.getParseMethod().substring(start).trim();
			if (mothedNameSet.contains(mothedName)) {
				continue;
			}
			mothedNameSet.add(mothedName);
			builder.append("\tprivate " + parseMethod + "(String data){\r\n");
			builder.append("\t\t return ConfParseUtils." + mothedName + "(data);\r\n");
			builder.append("\t}\r\n");
		}

		return builder.toString();
	}

	private static boolean haveListAndMap(List<fieldData> columnName) {
		for (fieldData data : columnName) {
			if (data.getParseMethod() != null) {
				String block = data.getParseMethod().split(" ")[0];
				if (block.startsWith("List") || block.startsWith("Map")) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 字段格式化，去除_并大写首字母
	 *
	 * @param beanName JavaBean名
	 * @return
	 */
	private static String formatBeanName(String beanName) {
		if (null == beanName || "".equals(beanName)) {
			return "";
		}
//		String[] split = beanName.split("_");
//		StringBuilder builder = new StringBuilder();
//		if (null != split && split.length > 0) {
//			for (String str : split) {
//				builder.append(firstUpcase(str));
//			}
//		}
//		return builder.toString();
		return firstUpcase(beanName);
	}


	/**
	 * 字段格式化，去除_并小写首字母
	 *
	 * @param beanName JavaBean名
	 * @return
	 */
	private static String formatBeanNameLower(String beanName) {
		if (null == beanName || "".equals(beanName)) {
			return "";
		}
//		String[] split = beanName.split("_");
//		StringBuilder builder = new StringBuilder();
//		if (null != split && split.length > 0) {
//			for (String str : split) {
//				builder.append(firstLower(str));
//			}
//		}
//		return builder.toString();
		return firstLower(beanName);
	}
}
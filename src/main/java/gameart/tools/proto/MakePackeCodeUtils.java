package gameart.tools.proto;

import com.sun.media.sound.InvalidDataException;

import java.io.*;
import java.util.*;

public class MakePackeCodeUtils {
	public static String ProtoFilePath;
	public static String reqPacketJavaCodePath;
	public static String messageCodePath;
	public static String JavaPath;

//	public static void main(String[] args) throws Exception {
//		makeOne("BattleProtoNew.proto", "C:/Users/user/Desktop/data/", "C:/Users/user/Desktop/data/");
//	}

	private static void init(String exportPath, String protocolPath) {
		MakePackeCodeUtils.ProtoFilePath = protocolPath;
		MakePackeCodeUtils.reqPacketJavaCodePath = exportPath + "game-controller/src/main/java/com/gameart/controller/packets";
		MakePackeCodeUtils.messageCodePath = exportPath + "game-controller/src/main/java/com/gameart/controller/api/GamePacketID.java";
		MakePackeCodeUtils.JavaPath = "com.gameart.controller.packets.";
	}

	public static void makeAll(String exportPath, String protocolPath) throws Exception {
		init(exportPath, protocolPath);
		System.out.println("开始自动化协议执行.......\r\n");
		Map<Integer, PacketData.OneFilePacketData> allPacketList = allPacket(null);
		System.out.println("成功获取所有的协议数据，准备生成协议...");
		makePacket(allPacketList);
		writeMessageCode(allPacketList);
	}

	public static void makeOne(String fileName, String exportPath, String protocolPath) throws Exception {
		init(exportPath, protocolPath);
		Map<Integer, PacketData.OneFilePacketData> simpleMap = allPacket(fileName);
		makePacket(simpleMap);

		//只写协议code
		Map<Integer, PacketData.OneFilePacketData> allPacketList = readGamePacketIdFile();
		allPacketList.putAll(simpleMap);
		writeMessageCode(allPacketList);
	}

	/**
	 * 读取GamePacket
	 *
	 * @return
	 */
	private static Map<Integer, PacketData.OneFilePacketData> readGamePacketIdFile() throws Exception {
		File file = new File(messageCodePath);
		boolean exists = file.exists();
		if (!exists) {
			return new HashMap<>();
		}
		String importStr = "import ";
		String message = "Message";
		String client = "C2G_";
		String java_multiple_filesStr = "java_multiple_files";

		String modeleNameStart = "//================";
		String modeleNameEnd = "================";

		Map<Integer, PacketData.OneFilePacketData> packetMap = new TreeMap<>();
		Map<String, List<PacketData.SimplePacketData>> packetNameList = new LinkedHashMap<>();
		Map<String, String> importPackageMap = new LinkedHashMap<>();
		Map<String, String> moduleNameMap = new LinkedHashMap<>();
		Map<String, String> mainClassMap = new LinkedHashMap<>();

		boolean java_multiple_files = false;
		String moduleName = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		String line = null;
		while ((line = br.readLine()) != null) {
			if (line.trim().startsWith(importStr)) {
				line = line.trim();
				String tempLine = line.substring(0, line.length() - 1).replace(importStr, "").trim();
				String[] split = tempLine.split("\\.");
				String packetName = tempLine.substring(0, tempLine.lastIndexOf("."));
				importPackageMap.put(split[split.length - 1], packetName);
				mainClassMap.put(split[split.length - 1], split[split.length - 2]);
			} else if (line.trim().startsWith(message)) {
				line = line.trim();
				String desc = line.split("//")[1].trim();

				int start = line.indexOf("(");
				int end = line.indexOf(")");
				String blocks = line.substring(start + 1, end);
				String[] split = blocks.split(",");
				int messageId = Integer.parseInt(split[0]);
				String proto = split[1].replace(".class", "").trim();

				String importProtoPacketName = importPackageMap.get(proto);
				PacketData.SimplePacketData data = new PacketData.SimplePacketData(importProtoPacketName, proto, messageId, desc, proto.startsWith(client));
				List<PacketData.SimplePacketData> list = packetNameList.get(importProtoPacketName);
				if (list == null) {
					list = new ArrayList<>();
					packetNameList.put(importProtoPacketName, list);
				}
				list.add(data);

				if (moduleName != null) {
					moduleNameMap.put(importProtoPacketName, moduleName);
					moduleName = null;
				}

			} else if (line.trim().startsWith(modeleNameStart)) {
				line = line.trim();
				moduleName = line.replace(modeleNameStart, "");
				moduleName = moduleName.substring(0, moduleName.lastIndexOf(modeleNameEnd));
				moduleName = moduleName.trim();
			} else if (line.trim().contains(java_multiple_filesStr)) {
				java_multiple_files = true;
			}
		}

		for (Map.Entry<String, List<PacketData.SimplePacketData>> entry : packetNameList.entrySet()) {
			List<PacketData.SimplePacketData> value = entry.getValue();
			Collections.sort(value);
			PacketData.SimplePacketData next = value.iterator().next();
			int minMessageCode = next.getMassageCode();
			String mainClass = mainClassMap.get(next.getClassName());
			String moduleNameX = "//" + moduleNameMap.get(next.getImportProtoPacketName() + "." + mainClass);

			String packetInfolder = null;
			PacketData.SimplePacketData simplePacketData = null;

			for (PacketData.SimplePacketData data : value) {
				if (data.getClassName().startsWith("C2G_")) {
					simplePacketData = data;
					break;
				}
			}

			if (simplePacketData != null) {
				String[] blcoks = importPackageMap.get(simplePacketData.getClassName() + "Packet").split("\\.");
				packetInfolder = blcoks[blcoks.length - 1].trim();
			} else {
				String[] blcoks = importPackageMap.get(next.getClassName()).split("\\.");
				packetInfolder = blcoks[blcoks.length - 1].trim();
			}

			PacketData.OneFilePacketData data = new PacketData.OneFilePacketData(java_multiple_files, mainClass, packetInfolder, minMessageCode, moduleNameX, value);
			packetMap.put(minMessageCode, data);
		}
		return packetMap;
	}

	private static void makePacket(Map<Integer, PacketData.OneFilePacketData> packetList) throws Exception {
		System.out.println("\r\n开始生成C2GPacket处理对象.....");
		for (PacketData.OneFilePacketData oneFilePacketData : packetList.values()) {
			writeJavaFile(oneFilePacketData);
		}
		System.out.println("\r\nMessage 协议生成已完成，运行结束!");
	}

	private static void writeMessageCode(Map<Integer, PacketData.OneFilePacketData> packetList) throws Exception {
		int size = packetList.values().stream().mapToInt(data -> data.getPacketList().size()).sum();

		System.out.println("\r\n协议code正在生成中, 总共模块[" + packetList.size() + "]个, 协议数量{" + size + "}条");
		StringBuilder sb = new StringBuilder();
		sb.append("package com.gameart.controller.api;\r\n\r\n");
		sb.append("import com.gameart.network.packet.RequestProtobufPacket;\r\n");
		sb.append("import com.gameart.network.packet.annotation.Packet;\r\n");
		sb.append("import java.util.*;\r\n");
		sb.append("import com.gameart.base.utils.PacketSendUtility;\r\n");
		sb.append("import com.gameart.network.packet.RequestPacketWithSessionId;\r\n");
		sb.append("import org.springframework.context.ApplicationContext;\r\n");
		sb.append("import com.gameart.utils.SpringContextHolder;\r\n");
		sb.append("import com.google.protobuf.AbstractMessage;\r\n");

		for (PacketData.OneFilePacketData oneFilePacketData : packetList.values()) {
			Collections.sort(oneFilePacketData.getPacketList());
			for (PacketData.SimplePacketData data : oneFilePacketData.getPacketList()) {
				if (data.getClassName().indexOf("C2G_") >= 0) {
					String packetname = (reqPacketJavaCodePath.split("/java/")[1] + "." + oneFilePacketData.getPacketInfolder() + "." + data.getClassName()).replace("/", ".") + "Packet";
					sb.append("import " + packetname + ";\r\n");
				}
				if(oneFilePacketData.java_multiple_files){
					sb.append("import " + data.getImportProtoPacketName() + "." + data.getClassName() + ";\r\n");
				}else {
					sb.append("import " + data.getImportProtoPacketName() + "." + oneFilePacketData.getMainClass() + "." + data.getClassName() + ";\r\n");
				}
			}
		}

		sb.append("\r\n\r\npublic enum GamePacketID {");

		int messageIndex = 1;
		Map<Integer, String> messageCodeMap = new HashMap<>();
		for (PacketData.OneFilePacketData oneFilePacketData : packetList.values()) {
			if (oneFilePacketData.packetList.size() <= 0)
				continue;
			sb.append("\r\n\r\n");
			sb.append(oneFilePacketData.getModuleName().replace("//", "\t//================ ") + " ================" + "\r\n");
			Collections.sort(oneFilePacketData.getPacketList());
			for (PacketData.SimplePacketData data : oneFilePacketData.getPacketList()) {
				//包检测
				if (messageCodeMap.containsKey(data.getMassageCode())) {
					throw new InvalidDataException("协议含有重复的messageCode, 请检查！[" + messageCodeMap.get(data.getMassageCode()) + "," + data.getClassName() + "]");
				}
				messageCodeMap.put(data.getMassageCode(), data.getClassName());
				String reqPacket = data.getClassName() + "Packet";

				if (reqPacket.indexOf("C2G") == 0) {
					sb.append("\tMessage" + messageIndex + "(" + data.getMassageCode() + ", " + data.getClassName() + ".class, " + reqPacket + ".class)," + "// " + data.getDesc() + "\r\n");
				} else {
					sb.append("\tMessage" + messageIndex + "(" + data.getMassageCode() + ", " + data.getClassName() + ".class, null)," + "// " + data.getDesc() + "\r\n");
				}
				messageIndex++;
			}
		}
		sb.append("\t;\r\n\r\n");


		sb.append("\tint messageId;\r\n");
		sb.append("\tClass<? extends AbstractMessage> msgClass;\r\n");
		sb.append("\tClass<? extends RequestPacketWithSessionId> reqClass;\r\n\r\n");
		sb.append("\tGamePacketID(int messageId, Class<? extends AbstractMessage> msgClass,Class<? extends RequestPacketWithSessionId> reqClass) {\r\n");
		sb.append("\t\tthis.messageId = messageId;\r\n");
		sb.append("\t\tthis.msgClass = msgClass;\r\n");
		sb.append("\t\tthis.reqClass = reqClass;\r\n");
		sb.append("\t}");
		sb.append("\r\n");
		sb.append("\r\n");
		sb.append("\r\n");
		sb.append("\r\n");
		sb.append("\r\n");
		sb.append("\r\n");
		sb.append("\r\n");
		sb.append("\r\n");

		//读取表尾
		File file = new File(messageCodePath);
		boolean exists = file.exists();

		if (!exists) {
			File fileParent = file.getParentFile();
			if (!fileParent.exists()) {
				fileParent.mkdirs();
			}
			file.createNewFile();
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		String line = null;
		List<String> sourceList = new ArrayList<>();
		while ((line = br.readLine()) != null) {
			sourceList.add(line);
		}

		if (exists) {
			for (int i = sourceList.size() - 28; i < sourceList.size() - 1; i++) {
				sb.append(sourceList.get(i) + "\r\n");
			}
			sb.append("}");
		}

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
		bw.write(sb.toString());
		bw.flush();
		bw.close();

		System.out.println("协议code生成成功!!!");
	}


	/**
	 * 写出java文件
	 *
	 * @param oneFilePacketData
	 * @throws Exception
	 */
	private static void writeJavaFile(PacketData.OneFilePacketData oneFilePacketData) throws Exception {
		for (PacketData.SimplePacketData data : oneFilePacketData.getPacketList()) {
			if (data.isClient()) {
				write2JavaC2GPacketFile(oneFilePacketData, data);
			}
		}
	}

	/**
	 * 创建java c2g文件
	 *
	 * @param oneFilePacketData
	 * @param data
	 * @throws Exception
	 */
	private static void write2JavaC2GPacketFile(PacketData.OneFilePacketData oneFilePacketData, PacketData.SimplePacketData data) throws Exception {
		File file = new File(reqPacketJavaCodePath + ("." + oneFilePacketData.getPacketInfolder() + "." + data.getClassName()).replace(".", "/") + "Packet" + ".java");
		File fileParent = file.getParentFile();
		if (!fileParent.exists()) {
			fileParent.mkdirs();
		}
		if (file.exists())
			return;
		file.createNewFile();

		String fanxing = null;

		String filed = Character.toLowerCase(data.getClassName().charAt(0)) + data.getClassName().substring(1);

		StringBuilder sb = new StringBuilder();
		sb.append("package " + JavaPath + oneFilePacketData.getPacketInfolder() + ";\r\n\r\n");
		sb.append("import com.gameart.controller.handler.RequestPacketWithPlayerId;\r\n");
		sb.append("import com.gameart.network.packet.ResponsePacketWithSessionId;\r\n");
		if (oneFilePacketData.java_multiple_files) {
			fanxing = data.getClassName();
			sb.append("import " + data.getImportProtoPacketName() + "." + data.getClassName() + ";\r\n\r\n\r\n");
		} else {
			fanxing = oneFilePacketData.getMainClass() + "." + data.getClassName();
			sb.append("import " + data.getImportProtoPacketName() + "." + oneFilePacketData.getMainClass() + ";\r\n\r\n\r\n");
		}
		sb.append("/**\r\n");
		sb.append(" * \r\n");
		sb.append(" * [" + oneFilePacketData.getModuleName().replace("//", "").split(" ")[0] + "]" + "\r\n");
		sb.append(" * " + data.getDesc() + "\r\n");
		sb.append(" * \r\n");
		sb.append(" **/\r\n");
		sb.append("public class " + data.getClassName() + "Packet extends RequestPacketWithPlayerId<" + fanxing + "> {\r\n\r\n");
		sb.append("\t@Override\r\n");
		sb.append("\tpublic ResponsePacketWithSessionId execute(long playerId, " + fanxing + " " + filed + ") {\r\n");
		sb.append("\r\n");
		String str = data.getClassName();
		StringBuilder ss4tr = new StringBuilder(str);
		str = ss4tr.replace(0, 3, "G2C").toString();
		List<PacketData.SimplePacketData> packetData = oneFilePacketData.getPacketList();
		boolean flag = false;
		PacketData.SimplePacketData currentData = null;
		for (PacketData.SimplePacketData simplePacketData : packetData) {
			if (simplePacketData.getClassName().equals(str)) {
				flag = true;
				currentData = simplePacketData;
				break;
			}
		}
		if (flag) {
			sb.append("  \t\t" + oneFilePacketData.getMainClass() + "." + str + ".Builder builder =" + oneFilePacketData.getMainClass() + "." + str + ".newBuilder();" + "\r\n");

			Set<DateType> dateTypes = currentData.getDateTypes();
			for (DateType dateType : dateTypes) {
				if (dateType.getType().equals("int32") || dateType.getType().equals("string")) {
					sb.append("  \t\tbuilder.set" + toUpperCaseFirstOne(dateType.getData()) + "(" + dateType.getData() + ");".trim() + "\r\n");
				} else if (dateType.getType().equals("repeated")) {
					sb.append("  \t\tbuilder.add" + toUpperCaseFirstOne(dateType.getData()) + "(" + dateType.getData() + ");".trim() + "\r\n");
				}
			}
			sb.append("  \t\tResponsePacketWithSessionId packet = PacketSendUtility.createResponsePacket(playerId, builder.build());" + "\r\n");
			sb.append("  \t\treturn packet;\n");
		} else {
			sb.append("   \t\treturn null;\r\n");
		}
		sb.append("\t}\r\n");
		sb.append("}\r\n");

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
		bw.write(sb.toString());
		bw.flush();
		bw.close();
		System.out.println("生成C2GPacket处理对象[" + oneFilePacketData.getMainClass() + "] ==> " + data.getClassName() + "Packet");
	}

	/**
	 * 获取所有的包
	 *
	 * @return
	 * @throws Exception
	 */
	private static Map<Integer, PacketData.OneFilePacketData> allPacket(String fileName) throws Exception {
		Map<Integer, PacketData.OneFilePacketData> packetList = new TreeMap<>();
		List<File> fileList = new ArrayList<>();
		pathScan(fileList, new File(ProtoFilePath));

		for (File file : fileList) {
			if (fileName == null || (fileName != null && fileName.contains(file.getName()))) {
				PacketData.OneFilePacketData oneFilePacketData = readPacketFromProto(file);
				packetList.put(oneFilePacketData.getMinMessageCode(), oneFilePacketData);
			}
		}

		return packetList;
	}

	/**
	 * 读取协议文件
	 *
	 * @param file
	 * @return
	 * @throws Exception
	 */
	private static PacketData.OneFilePacketData readPacketFromProto(File file) throws Exception {
		List<PacketData.SimplePacketData> packetList = new ArrayList<>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));

		String packet = "package ";
		String message = "message ";
		String messageZSStr = "//";
		String messageCodeStr = "//[";
		String messagerepeated = "repeated";
		String messageInt = "int32";
		String messageString = "string";
		String java_multiple_filesStr = "java_multiple_files";
		String line = null;

		int minCode = 0;
		String moduleName = null;

		boolean java_multiple_files = false;
		String importProtoPacketName = null;
		int messageCode = 0;
		String desc = null;
		int index = 0;

		String packetInfolder = file.getName().replace("proto", "").replace("Proto", "").replace(".", "").replace("Packet", "").toLowerCase();

		while ((line = br.readLine()) != null) {
			if (index == 0 && line.startsWith(messageZSStr)) {
				moduleName = line;
				index++;
			}
			if (line.startsWith(packet)) {
				importProtoPacketName = "com.gameart.serialize.proto";
				if (!line.contains(importProtoPacketName + ";")) {
					String[] split = line.split("\\.");
					packetInfolder = split[split.length - 1].replace(";", "");
				}
			} else if (desc != null && line.startsWith(message)) {
				String className = line.trim().replace(message, "").replace("{", "").trim();
				PacketData.SimplePacketData data = new PacketData.SimplePacketData(importProtoPacketName, className, messageCode, desc, line.contains("C2G_"));
				packetList.add(data);
				minCode = (minCode == 0 ? messageCode : ((minCode > messageCode ? messageCode : minCode)));
				messageCode = 0;
				desc = null;
			} else if (line.startsWith(messageCodeStr)) {
				String[] split = line.replace(messageCodeStr, "").split("]");
				messageCode = Integer.parseInt(split[0]);
				desc = split[1].trim();
			} else if (line.trim().startsWith(messagerepeated) || line.trim().startsWith(messageInt) || line.trim().startsWith(messageString)) {
				String type = null;
				if (line.trim().startsWith(messagerepeated)) {
					type = messagerepeated;
				} else if (line.trim().startsWith(messageInt)) {
					type = messageInt;
				} else if (line.trim().startsWith(messageString)) {
					type = messageString;
				}
				if (packetList.get(packetList.size() - 1).getClassName().contains("G2C_")) {
					String[] strings = line.trim().replace(type, "").trim().split("=");
					PacketData.SimplePacketData packetData = packetList.get(packetList.size() - 1);
					DateType dateType = DateType.valueOf(type, strings[0]);
					packetData.getDateTypes().add(dateType);
				}
			} else if (line.trim().contains(java_multiple_filesStr)) {
				java_multiple_files = true;
			}
		}
		String mainClass = file.getName().split("\\.")[0];
		return new PacketData.OneFilePacketData(java_multiple_files, mainClass, packetInfolder, minCode, moduleName, packetList);
	}

	public static String toUpperCaseFirstOne(String s) {
		if (Character.isUpperCase(s.charAt(0)))
			return s;
		else
			return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
	}

	/**
	 * 读取某个文件夹下的所有文件
	 */
	private static void pathScan(List<File> fileList, File file) throws Exception {
		try {
			if (file.isFile()) {
				fileList.add(file);
			} else {
				for (File tempFile : file.listFiles()) {
					if (tempFile.isFile()) {
						fileList.add(tempFile);
					} else {
						pathScan(fileList, tempFile);
					}
				}
			}
		} catch (Exception e) {
			throw new Exception("协议错误! path=" + file.getPath());
		}
	}
}

package gameart.tools.proto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PacketData {

	public static class SimplePacketData implements Comparable<SimplePacketData> {
		private String importProtoPacketName;
		private String className;
		private int massageCode;
		private String desc;
		private boolean isClient;
		private Set<DateType> dateTypes = new HashSet<>();

		public Set<DateType> getDateTypes() {
			return dateTypes;
		}

		public SimplePacketData(String importProtoPacketName, String className, int massageCode, String desc, boolean isClient) {
			this.importProtoPacketName = "com.gameart.serialize.proto";
			this.className = className;
			this.massageCode = massageCode;
			this.desc = desc;
			this.isClient = isClient;
		}

		public String getImportProtoPacketName() {
			return importProtoPacketName;
		}

		public String getClassName() {
			return className;
		}

		public int getMassageCode() {
			return massageCode;
		}

		public String getDesc() {
			return desc;
		}

		public boolean isClient() {
			return isClient;
		}

		@Override
		public int compareTo(SimplePacketData o) {
			return o.getMassageCode() > this.getMassageCode() ? -1 : 1;
		}
	}

	public static class OneFilePacketData {
		private String mainClass;

		private String packetInfolder;

		public int minMessageCode;

		public String moduleName;

		public boolean java_multiple_files;

		public List<SimplePacketData> packetList;

		public OneFilePacketData(boolean java_multiple_files, String mainClass, String packetInfolder, int minMessageCode, String moduleName, List<SimplePacketData> packetList) {
			this.java_multiple_files = java_multiple_files;
			this.mainClass = mainClass.trim();
			this.packetInfolder = packetInfolder.trim();
			this.minMessageCode = minMessageCode;
			this.moduleName = moduleName.trim();
			this.packetList = packetList;
		}

		public String getMainClass() {
			return mainClass;
		}

		public String getPacketInfolder() {
			return packetInfolder;
		}

		public int getMinMessageCode() {
			return minMessageCode;
		}

		public String getModuleName() {
			return moduleName;
		}

		public List<SimplePacketData> getPacketList() {
			return packetList;
		}


		@Override
		public String toString() {
			return "OneFilePacketData{" +
					"mainClass='" + mainClass + '\'' +
					", packetInfolder='" + packetInfolder + '\'' +
					", minMessageCode=" + minMessageCode +
					", moduleName='" + moduleName + '\'' +
					", packetList=" + packetList.size() +
					'}';
		}
	}

}

package gameart.tools;


import gameart.tools.excel.Excel2JsonAndJavaUtil;
import gameart.tools.proto.MakePackeCodeUtils;

public class Main {
	public static void mainxx(String[] args) throws Exception {
		int type = Integer.parseInt(args[0]);

		String args1 = args[1];
		String args2 = args[2];
		String args3 = null;
		if (args.length >= 4) {
			args3 = args[3];
		}
		if (type == 1) {
			Excel2JsonAndJavaUtil.makeOne(args1, args2, args3);
		} else if (type == 2) {
			Excel2JsonAndJavaUtil.makeAll(args1, args2);
		} else if (type == 3) {
			MakePackeCodeUtils.makeOne(args1, args2, args3);
		} else if (type == 4) {
			MakePackeCodeUtils.makeAll(args1, args2);
		}
	}

	public static void main(String[] args) throws Exception {
		mainxx(new String[]{"1", "item.xlsx", "D:/Project/excel/", "C:/project/"});
	/*	mainxx(new String[]{"2", "D:/Project/Excel/", "D:/Project/"});*/

//		mainxx(new String[]{"3", "ChapterProto.proto", "F:/project/server/Kiseki/game-all/", "F:/project/server/Kiseki/conf/proto/game/"});
	}
}

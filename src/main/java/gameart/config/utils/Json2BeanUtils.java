package gameart.config.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Json2BeanUtils {
	/**
	 * IO读取json源文件
	 *
	 * @param path json源文件地址
	 * @return
	 * @throws IOException
	 */
	public static String getJsonStr(String path) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(path)));
		StringBuilder builder = new StringBuilder();
		String line = null;
		while ((line = br.readLine()) != null) {
			builder.append(line);
		}
		br.close();
		return builder.toString();
	}
}

package gameart.config.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import gameart.annotation.TypeManagered;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


/**
 * 配置装配父类(配置控制)<br/>
 * 子类必须覆盖setValue方法
 */
@TypeManagered
public class SimpleConfig {

	/**
	 * 加载配置并且放入集合
	 *
	 * @return
	 * @throws Exception
	 */
	public Map<Integer, Object> loadValue() throws Exception {
		final Map<Integer, Object> map = new LinkedHashMap<>();
		Class<?> clazz = this.getClass();
		try {
			URL resource = Thread.currentThread().getContextClassLoader().getResource("config/" + clazz.getSimpleName() + ".json");
			String jsonStr = Json2BeanUtils.getJsonStr(resource.getPath());
			JSONObject jsonObject = JSON.parseObject(jsonStr);
			JSONArray jsonArray = jsonObject.getJSONArray("array");
			jsonArray.forEach(jo -> {
				Set<Map.Entry<String, Object>> entries = ((JSONObject) jo).entrySet();
				Properties prop = new Properties();
				entries.forEach(entry -> {
					String fieldName = firstLower(entry.getKey());
					prop.setProperty(fieldName, entry.getValue().toString());
				});
				try {
					Object change = ConfigUtils.autowired(prop, clazz);
					Object cast = clazz.cast(change);
					Method method = clazz.getMethod("getId");
					int id = (int) method.invoke(cast);
					map.put(id, change);
				} catch (Exception e) {
					throw new InvalidParameterException(clazz.getSimpleName() + "错误 | " + e.getMessage());
				}
			});
		} catch (Exception e) {
			throw new Exception(clazz.getSimpleName() + "错误 | " + e.getMessage());
		}
		return map;
	}

	/**
	 * 首字母小写
	 *
	 * @param string
	 * @return
	 */
	private static String firstLower(String string) {
		String str1 = string.substring(0, 1);
		String str2 = string.substring(1);
		return str1.toLowerCase() + str2;
	}

	/**
	 * 扫描文件夹下的所有文件
	 *
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public File[] pathScan(String filePath) {
		File[] files;
		File file = new File(filePath);
		if (file.exists() && file.isDirectory()) {
			files = file.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return true;
				}
			});
		} else {
			throw new RuntimeException("file path:[" + filePath + "] not exists");
		}
		return files;
	}
}

package gameart.config.utils;

import com.google.common.base.Strings;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class ConfigUtils {
	/**
	 * 将PropertiesProxy转换成相应的配置类
	 *
	 * @param prop
	 * @param clazz
	 * @return
	 * @throws Exception
	 * @since 2015年7月17日 上午10:41:57
	 */
	public static Object autowired(Properties prop, Class<?> clazz) throws Exception {
		Object instance = clazz.newInstance();
		Field[] fields = clazz.getDeclaredFields();
		Field[] fields2 = clazz.getFields();

		Set<Field> fieldSet = new HashSet<>();
		fieldSet.addAll(Arrays.asList(fields));
		fieldSet.addAll(Arrays.asList(fields2));

		for (Field field : fieldSet) {
			field.setAccessible(true);
			try {
				setValue(prop, clazz, instance, field, field.getAnnotation(Config.class));
			} catch (Exception e) {
				throw new Exception("字段:" + clazz.getName() + "." + field.getName() + " 解析异常 ==> " + e.getMessage());
			}
		}
		return instance;
	}

	/**
	 * 字段赋值
	 *
	 * @param prop
	 * @param clazz
	 * @param instance
	 * @param field
	 * @param conf
	 * @throws Exception
	 * @since 2015年7月17日 上午10:41:34
	 */
	private static void setValue(Properties prop, Class<?> clazz, Object instance, Field field, Config conf) throws Exception {
		Class<?> type = field.getType();
		String propValue = prop.getProperty(field.getName());
		if (Strings.isNullOrEmpty(propValue)) {
			return;
		}
		if (conf != null && !"".equals(conf.parser())) {
			String parser = conf.parser();
			Object fieldValue = parseMe(prop, clazz, instance, field, parser, propValue);
			field.set(instance, fieldValue);
		} else if (type == Integer.TYPE || type == Integer.class) {
			field.set(instance, Integer.parseInt(propValue));
		} else if (type == Long.TYPE || type == Long.class) {
			field.set(instance, Long.parseLong(propValue));
		} else if (type == Boolean.TYPE || type == Boolean.class) {
			field.set(instance, Boolean.parseBoolean(propValue));
		} else if (type == Byte.TYPE || type == Byte.class) {
			field.set(instance, Byte.parseByte(propValue));
		} else if (type == Double.TYPE || type == Double.class) {
			field.set(instance, Double.parseDouble(propValue));
		} else if (type == Short.TYPE || type == Short.class) {
			field.set(instance, Short.parseShort(propValue));
		} else if (type == Float.TYPE || type == Float.class) {
			field.set(instance, Float.parseFloat(propValue));
		} else if (type == String.class) {
			field.set(instance, propValue);
		} else {
			throw new Exception(clazz.getName() + "." + field.getName() + "非基本类型，请指定解析方法.");

		}
	}

	/**
	 * 自定义解析
	 */
	private static Object parseMe(Properties prop, Class<?> clazz, Object instance, Field field, String parser, String propValue) throws Exception {
		Method method;
		Object fieldValue;
		try {
			method = clazz.getDeclaredMethod(parser, String.class);
			method.setAccessible(true);
			fieldValue = method.invoke(instance, propValue);
		} catch (Exception e) {
			method = clazz.getMethod(parser, Properties.class);
			fieldValue = method.invoke(instance, prop);
		}
		return fieldValue;
	}
}

package gameart.config.utils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import gameart.annotation.TypeManagered;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TypeManagered
public abstract class IConfig {
	public abstract void loadConf() throws Exception;

	protected <K, T> Multimap<K, T> parseMapListT(Map<Integer, T> tempMap, String mapKey) throws Exception {
		if (tempMap == null)
			return null;
		Multimap<K, T> newMap = ArrayListMultimap.create();
		for (T t : tempMap.values()) {
			Field field = t.getClass().getDeclaredField(mapKey);
			field.setAccessible(true);
			K o = (K) field.get(t);
			newMap.put(o, t);
		}
		return newMap;
	}

	protected <K, T> Map<K, List<T>> parseMapListT(Map<Integer, T> tempMap, Class<? extends Map> mapClass, String mapKey) throws Exception {
		if (tempMap == null)
			return null;
		Map<K, List<T>> newMap = mapClass.newInstance();
		for (T t : tempMap.values()) {
			Field field = t.getClass().getDeclaredField(mapKey);
			field.setAccessible(true);
			Object o = field.get(t);

			List<T> ts = newMap.get(o);
			if (ts == null) {
				ts = new ArrayList<>();
				newMap.put((K) o, ts);
			}
			ts.add(t);
		}
		return newMap;
	}


	protected Map toMap(Map sourceMap, String... keyNames) throws Exception {
		return this.toMap(sourceMap, HashMap.class, keyNames);
	}

	protected Map toMap(Map sourceMap, Class<? extends Map> mapClazz, String... keyNames) throws Exception {
		return ConfParseUtils.toMap(sourceMap, mapClazz, keyNames);
	}
}

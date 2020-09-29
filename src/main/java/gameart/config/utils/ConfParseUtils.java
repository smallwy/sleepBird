package gameart.config.utils;

import com.alibaba.fastjson.JSONArray;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;

public class ConfParseUtils {
	/**
	 * 字符串转物品列表类型
	 * 格式：[[key,value],[key,value]]
	 *
	 * @param data
	 * @return
	 */
	public static List<ItemPair<Integer, Long>> toItemPairList(String data) {
		List<ItemPair<Integer, Long>> list = new ArrayList<>();
		for (long[] blocks : toLongArrayList(data)) {
			list.add(new ItemPair<>((int) blocks[0], blocks[1]));
		}
		return list;
	}

	/**
	 * 字符串转int数组列表类型
	 * 格式：{[array1,array1,array1],[array2,array2,array2]}
	 *
	 * @param data
	 * @return
	 */
	public static List<int[]> toIntArrayList(String data) {
		return JSONArray.parseArray(data, int[].class);
	}

	/**
	 * 字符串转pair列表类型
	 * 格式：[[key,value],[key,value]]
	 *
	 * @param data
	 * @return
	 */
	public static List<Pair<Integer, Integer>> toPairList(String data) {
		List<Pair<Integer, Integer>> list = new ArrayList<>();
		for (int[] blocks : toIntArrayList(data)) {
			list.add(new Pair<>(blocks[0], blocks[1]));
		}
		return list;
	}

	/**
	 * 字符串转long数组列表类型
	 * 格式：{[array1,array1,array1],[array2,array2,array2]}
	 *
	 * @param data
	 * @return
	 */
	public static List<long[]> toLongArrayList(String data) {
		return JSONArray.parseArray(data, long[].class);
	}

	public static int[] toIntArray(String data) {
		return JSONArray.parseObject(data, int[].class);
	}

	public static float[] toFloatArray(String data) {
		return JSONArray.parseObject(data, float[].class);
	}

	public static String[] toStrArray(String data) {
		return JSONArray.parseObject(data, String[].class);
	}

	public static List<Pos> toPosList(String data) {
		return JSONArray.parseArray(data, Pos.class);
	}

	public static List<Integer> toIntList(String data) {
		if (Strings.isNullOrEmpty(data))
			return null;
		List<Integer> list = new ArrayList<>();
		String[] split = data.split(",");
		for (String intStr : split) {
			list.add(Integer.parseInt(intStr));
		}
		return list;
	}

	public static List<Integer> str2IdList(String str) {
		List<Integer> list = new ArrayList();
		String[] rewardArr = str.split(";");
		String[] var3 = rewardArr;
		int var4 = rewardArr.length;

		for (int var5 = 0; var5 < var4; ++var5) {
			String ss = var3[var5];
			list.add(Integer.parseInt(ss));
		}
		return list;
	}

	/**
	 * @param str (24|300,25|310,26|330)
	 * @return
	 */
	public static List<int[]> str2IntArrayList(String str) {
		List<int[]> list = new ArrayList();
		String[] strArr = str.split(",");
		for (String proStr : strArr) {
			String[] proArr = proStr.split("\\|");
			list.add(new int[]{Integer.parseInt(proArr[0]), Integer.parseInt(proArr[1])});
		}
		return list;
	}

	/**
	 * @param str (24|300,25|310,26|330)
	 * @return
	 */
	public static List<Pair<Integer, Integer>> str2IntPair(String str) {
		List<Pair<Integer, Integer>> list = new ArrayList();
		String[] strArr = str.split(",");
		for (String proStr : strArr) {
			String[] proArr = proStr.split("\\|");
			list.add(new Pair<>(Integer.parseInt(proArr[0]), Integer.parseInt(proArr[1])));
		}
		return list;
	}

	/**
	 * @param str (24|300,25|310,26|330)
	 * @return
	 */
	public static List<ItemPair<Integer, Integer>> str2IntItemPair(String str) {
		List<ItemPair<Integer, Integer>> list = new ArrayList();
		String[] strArr = str.split(",");
		for (String proStr : strArr) {
			String[] proArr = proStr.split("\\|");
			list.add(new ItemPair(Integer.parseInt(proArr[0]), Integer.parseInt(proArr[1])));
		}
		return list;
	}


	public static List<int[]> IntArray(String str) {
		List<int[]> list = new ArrayList();
		String[] strArr = str.split(",");
		for (String proStr : strArr) {
			String[] proArr = proStr.split("\\|");
			list.add(new int[]{Integer.parseInt(proArr[0]), Integer.parseInt(proArr[1])});
		}
		return list;
	}


	/**
	 * @return
	 */
	public static List<ItemPair<Integer, Long>> str2LongItemPair(String str) {
		List<ItemPair<Integer, Long>> list = new ArrayList();
		String[] strArr = str.split(",");
		for (String proStr : strArr) {
			String[] proArr = proStr.split("\\|");
			list.add(new ItemPair(Integer.parseInt(proArr[0]), Long.parseLong(proArr[1])));
		}
		return list;
	}


	public static Map<Integer, Integer> str2Map(String strParam) {
		Map<Integer, Integer> map = new HashMap();
		String[] rewardArr = strParam.split(";");
		String[] var3 = rewardArr;
		int var4 = rewardArr.length;

		for (int var5 = 0; var5 < var4; ++var5) {
			String str = var3[var5];
			String[] strArr = str.split("=");
			map.put(Integer.parseInt(strArr[0]), Integer.parseInt(strArr[1]));
		}

		return map;
	}


	public static int parseArray2Int(String str) {
		if (StringUtils.isEmpty(str))
			return 0;
		String substring = str.substring(1, str.length() - 1);
		if (StringUtils.isEmpty(substring))
			return 0;
		return Integer.parseInt(substring);
	}


	/**
	 * MAP合并
	 *
	 * @param <K>
	 * @return
	 */
	public static <K> Map<K, Float> megerMap(Map<K, Float> targetMap, Map<K, Float> sourceMap, boolean add) {
		Map<K, Float> newMap = new HashMap<>(targetMap);
		for (Entry<K, Float> entry : sourceMap.entrySet()) {
			Float v = newMap.get(entry.getKey());
			if (add) {
				v = v == null ? entry.getValue() : entry.getValue() + v;
			} else {
				v = v == null ? -entry.getValue() : -entry.getValue() + v;
			}
			newMap.put(entry.getKey(), v);
		}
		return newMap;
	}


	/**
	 * 奖励物品翻倍
	 *
	 * @param itemList
	 * @param ratio
	 * @return
	 */
	public static List<ItemPair<Integer, Long>> ratioItemPairList(List<ItemPair<Integer, Long>> itemList, int ratio) {
		List<ItemPair<Integer, Long>> newItemList = new ArrayList<>();
		if (itemList == null || itemList.isEmpty())
			return newItemList;
		itemList.forEach(item -> {
			ItemPair<Integer, Long> itemPair = new ItemPair<>(item.getKey(), item.getValue() * ratio);
			newItemList.add(itemPair);
		});
		return newItemList;
	}

	/**
	 * 奖励物品合并
	 *
	 * @param itemList
	 * @return
	 */
	public static List<ItemPair<Integer, Long>> megerItemPairList(List<ItemPair<Integer, Long>> itemList) {
		List<ItemPair<Integer, Long>> newItemList = new ArrayList<>();
		if (itemList == null || itemList.isEmpty()) {
			return newItemList;
		}
		Map<Integer, Long> itmaps = new HashMap<>();
		//相同道具合并数量
		for (ItemPair<Integer, Long> itemPair : itemList) {
			if (itemPair.getValue() <= 0) {
				continue;
			}
			if (itmaps.containsKey(itemPair.getKey())) {
				itmaps.put(itemPair.getKey(), itmaps.get(itemPair.getKey()) + itemPair.getValue());
			} else {
				itmaps.put(itemPair.getKey(), itemPair.getValue());
			}
		}
		if (itmaps.size() > 0) {
			Iterator iterator = itmaps.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry entry = (Entry) iterator.next();
				newItemList.add(new ItemPair<>((Integer) entry.getKey(), (Long) entry.getValue()));
			}
		}
		return newItemList;
	}

	public static Map toMap(Map sourceMap, String... keyNames) throws Exception {
		return toMap(sourceMap, HashMap.class, keyNames);
	}

	public static Map toMap(Map sourceMap, Class<? extends Map> mapClazz, String... keyNames) throws Exception {
		Map newMap = mapClazz.newInstance();
		for (Object obj : sourceMap.values()) {
			Map tempMap = newMap;
			Object[] keyValue = new Object[keyNames.length];
			for (int i = 0; i < keyNames.length; i++) {
				Field field = obj.getClass().getDeclaredField(keyNames[i]);
				field.setAccessible(true);
				Object key = field.get(obj);
				keyValue[i] = key;
				if (keyNames.length > 1) {
					if (i == keyNames.length - 1) {
						tempMap.put(key, obj);
					} else {
						Map temp = (Map) tempMap.get(key);
						if (temp == null) {
							temp = mapClazz.newInstance();
							tempMap.put(key, temp);
						}
						tempMap = temp;
					}
				} else {
					tempMap.put(key, obj);
				}
			}
		}
		return newMap;
	}

	/**
	 * 物品列表转MAP
	 *
	 * @param itemPairList
	 * @return Map
	 * @Author Jimmy
	 * @Date 2019/7/3 23:01
	 */
	public static Map<Integer, Long> items2Map(List<ItemPair<Integer, Long>> itemPairList) {
		List<ItemPair<Integer, Long>> newList = megerItemPairList(itemPairList);
		Map<Integer, Long> newMap = new LinkedHashMap<>();
		newList.forEach(data -> newMap.put(data.getKey(), data.getValue()));
		return newMap;
	}

	/**
	 * 物品列表转MAP
	 *
	 * @param itemMap
	 * @return List
	 * @Author Jimmy
	 * @Date 2019/7/3 23:01
	 */
	public static List<ItemPair<Integer, Long>> map2Items(Map<Integer, Long> itemMap) {
		List<ItemPair<Integer, Long>> itemPairList = new ArrayList<>();
		itemMap.entrySet().forEach(data -> itemPairList.add(new ItemPair<>(data.getKey(), data.getValue())));
		return itemPairList;
	}
}

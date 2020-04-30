package concurrent;

/**
 * 跳表
 *
 * @author wy
 * @create 2018-11-29 19:58
 **/

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class ConcurrentSkipListMapTest {

    public static void main(String[] args) {
        ConcurrentSkipListMap<String,Integer> cslMap = new ConcurrentSkipListMap<String,Integer>();
        cslMap.put("2017-05-22 16:18:10_key1", 1);
        cslMap.put("2017-05-22 16:18:08_key2", 2);
        cslMap.put("2017-05-22 16:18:20_key3", 1);
        cslMap.put("2017-05-22 16:18:18_key4", 2);
        cslMap.put("2017-05-22 16:18:30_key5", 1);
        cslMap.put("2017-05-22 16:18:28_key2", 2);
        cslMap.put("2017-05-22 16:18:40_key2", 1);
        cslMap.put("2017-05-22 16:18:38_key1", 2);
        cslMap.put("2017-05-22 16:18:59_key1", 2);
        cslMap.put("2017-05-22 17:18:10_key1", 2);
        cslMap.put("2017-05-22 17:18:08_key1", 2);
        cslMap.put("2017-05-23 17:18:08_key1", 2);

        String startKey = "2017-05-22 16:18:08";
        String endKey = "2017-05-22 16:18:60";

        ConcurrentNavigableMap<String, Integer> subMap = cslMap.subMap(startKey, endKey); //前闭后开
        for (Entry<String, Integer> entry : subMap.entrySet()) { //取一定范围的集合
            System.out.println("key:" + entry.getKey() + " value:" + entry.getValue());
        }
        System.out.println("----------------------------------------------");
        String firstKey = cslMap.firstKey(); //第一个键值（最小）
        String lastKey = cslMap.lastKey();//最后一个键值（最大）
        System.out.println("firstKey:" + firstKey + "=" + cslMap.get(firstKey) + "  lastKey:" + lastKey + "=" + cslMap.get(lastKey));
        System.out.println("----------------------------------------------");
        ConcurrentNavigableMap<String, Integer> headMap = cslMap.headMap("2017-05-23");//截止到指定key的集合（toKey 开区间）
//		ConcurrentNavigableMap<String, Integer> headMap = cslMap.headMap("2017-05-22",true);//截止到指定key的集合（toKey 闭区间）
        for (Entry<String, Integer> entry : headMap.entrySet()) {
            System.out.println("key:" + entry.getKey() + " value:" + entry.getValue());
        }
        System.out.println("----------------------------------------------");
		/*Iterator<Entry<String, Integer>> it = cslMap.entrySet().iterator();
		Set<String> deleteKeySet = headMap.keySet(); //要删除的key
		while (it.hasNext()) {
		      Entry<String, Integer> entry = it.next();
		      if (deleteKeySet.contains(entry.getKey()))
		      it.remove();
		}*/
        for (String key : headMap.keySet()) {
            cslMap.remove(key);
        }
        for (Entry<String, Integer> entry : cslMap.entrySet()) { //删除后的集合
            System.out.println("key:" + entry.getKey() + " value:" + entry.getValue());
        }
    }
}

/*
// 构造一个新的空映射，该映射按照键的自然顺序进行排序。
        ConcurrentSkipListMap()
// 构造一个新的空映射，该映射按照指定的比较器进行排序。
        ConcurrentSkipListMap(Comparator<? super K> comparator)
// 构造一个新映射，该映射所包含的映射关系与给定映射包含的映射关系相同，并按照键的自然顺序进行排序。
        ConcurrentSkipListMap(Map<? extends K,? extends V> m)
// 构造一个新映射，该映射所包含的映射关系与指定的有序映射包含的映射关系相同，使用的顺序也相同。
        ConcurrentSkipListMap(SortedMap<K,? extends V> m)

// 返回与大于等于给定键的最小键关联的键-值映射关系；如果不存在这样的条目，则返回 null。
        Map.Entry<K,V> ceilingEntry(K key)
// 返回大于等于给定键的最小键；如果不存在这样的键，则返回 null。
        K ceilingKey(K key)
// 从此映射中移除所有映射关系。
        void clear()
// 返回此 ConcurrentSkipListMap 实例的浅表副本。
        ConcurrentSkipListMap<K,V> clone()
// 返回对此映射中的键进行排序的比较器；如果此映射使用键的自然顺序，则返回 null。
        Comparator<? super K> comparator()
// 如果此映射包含指定键的映射关系，则返回 true。
        boolean containsKey(Object key)
// 如果此映射为指定值映射一个或多个键，则返回 true。
        boolean containsValue(Object value)
// 返回此映射中所包含键的逆序 NavigableSet 视图。
        NavigableSet<K> descendingKeySet()
// 返回此映射中所包含映射关系的逆序视图。
        ConcurrentNavigableMap<K,V> descendingMap()
// 返回此映射中所包含的映射关系的 Set 视图。
        Set<Map.Entry<K,V>> entrySet()
// 比较指定对象与此映射的相等性。
        boolean equals(Object o)
// 返回与此映射中的最小键关联的键-值映射关系；如果该映射为空，则返回 null。
        Map.Entry<K,V> firstEntry()
// 返回此映射中当前第一个（最低）键。
        K firstKey()
// 返回与小于等于给定键的最大键关联的键-值映射关系；如果不存在这样的键，则返回 null。
        Map.Entry<K,V> floorEntry(K key)
// 返回小于等于给定键的最大键；如果不存在这样的键，则返回 null。
        K floorKey(K key)
// 返回指定键所映射到的值；如果此映射不包含该键的映射关系，则返回 null。
        V get(Object key)
// 返回此映射的部分视图，其键值严格小于 toKey。
        ConcurrentNavigableMap<K,V> headMap(K toKey)
// 返回此映射的部分视图，其键小于（或等于，如果 inclusive 为 true）toKey。
        ConcurrentNavigableMap<K,V> headMap(K toKey, boolean inclusive)
// 返回与严格大于给定键的最小键关联的键-值映射关系；如果不存在这样的键，则返回 null。
        Map.Entry<K,V> higherEntry(K key)
// 返回严格大于给定键的最小键；如果不存在这样的键，则返回 null。
        K higherKey(K key)
// 如果此映射未包含键-值映射关系，则返回 true。
        boolean isEmpty()
// 返回此映射中所包含键的 NavigableSet 视图。
        NavigableSet<K> keySet()
// 返回与此映射中的最大键关联的键-值映射关系；如果该映射为空，则返回 null。
        Map.Entry<K,V> lastEntry()
// 返回映射中当前最后一个（最高）键。
        K lastKey()
// 返回与严格小于给定键的最大键关联的键-值映射关系；如果不存在这样的键，则返回 null。
        Map.Entry<K,V> lowerEntry(K key)
// 返回严格小于给定键的最大键；如果不存在这样的键，则返回 null。
        K lowerKey(K key)
// 返回此映射中所包含键的 NavigableSet 视图。
        NavigableSet<K> navigableKeySet()
// 移除并返回与此映射中的最小键关联的键-值映射关系；如果该映射为空，则返回 null。
        Map.Entry<K,V> pollFirstEntry()
// 移除并返回与此映射中的最大键关联的键-值映射关系；如果该映射为空，则返回 null。
        Map.Entry<K,V> pollLastEntry()
// 将指定值与此映射中的指定键关联。
        V put(K key, V value)
// 如果指定键已经不再与某个值相关联，则将它与给定值关联。
        V putIfAbsent(K key, V value)
// 从此映射中移除指定键的映射关系（如果存在）。
        V remove(Object key)
// 只有目前将键的条目映射到给定值时，才移除该键的条目。
        boolean remove(Object key, Object value)
// 只有目前将键的条目映射到某一值时，才替换该键的条目。
        V replace(K key, V value)
// 只有目前将键的条目映射到给定值时，才替换该键的条目。
        boolean replace(K key, V oldValue, V newValue)
// 返回此映射中的键-值映射关系数。
        int size()
// 返回此映射的部分视图，其键的范围从 fromKey 到 toKey。
        ConcurrentNavigableMap<K,V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive)
// 返回此映射的部分视图，其键值的范围从 fromKey（包括）到 toKey（不包括）。
        ConcurrentNavigableMap<K,V> subMap(K fromKey, K toKey)
// 返回此映射的部分视图，其键大于等于 fromKey。
        ConcurrentNavigableMap<K,V> tailMap(K fromKey)
// 返回此映射的部分视图，其键大于（或等于，如果 inclusive 为 true）fromKey。
        ConcurrentNavigableMap<K,V> tailMap(K fromKey, boolean inclusive)
// 返回此映射中所包含值的 Collection 视图。
        Collection<V> values()*/



package concurrent;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * 跳表
 *
 * @author wy
 * @create 2018-11-29 20:06
 **/
public class ConcurrentSkipListMapTest2 {


  public static void main(String[] args) {
      ConcurrentSkipListMap<Integer,Integer> cslMap = new ConcurrentSkipListMap<Integer, Integer>();
      cslMap.put(11111,322212);
      cslMap.put(2222,322322);
      cslMap.put(33,323122);
      cslMap.put(44412,34222);
      cslMap.put(56,322212);
      cslMap.put(1234,3212322);
      Iterator iterator=cslMap.entrySet().iterator();
      while(iterator.hasNext()){
          Map.Entry entry=(Map.Entry)iterator.next();
          System.out.println(entry.getValue());
      }
    //
  }
}

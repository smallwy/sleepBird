package concurrent.collections;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class MapTest {

    public static void main(String[] args) {
        Map<Integer, Integer> testMap = new HashMap<>(1);

        testMap.put(1, 1);

        testMap.put(2, 2);
    }

    @Test
    public void bit() {
        int number = 100 * 1000;
        int a = 1;
        long start = System.currentTimeMillis();
        for (int i = number; i > 0; i++) {
            /*  a = a & i;*/
            /*  a %= i;*/
            a = a | i;
            System.out.println(a);
        }
        long end = System.currentTimeMillis();
        System.out.println("一共消耗时间" + (end - start));
    }
}

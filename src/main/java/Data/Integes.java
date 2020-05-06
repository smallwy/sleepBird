package Data;

import java.util.Random;

/**
 * 产生随机数
 *
 * @auther stone
 * @date 2020/4/30 0030 21:33
 */
public class Integes {

    public static int[] randomInt(int count, int min, int max) {
        int[] arrays = new int[count];
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            int randomNum = rand.nextInt(max - min + 1) + min;
            arrays[i] = randomNum;
        }
        return arrays;
    }

    public static void ptint(int[] ints) {
        for (int a = 0; a < ints.length; a++)
            System.out.print(ints[a]+"--");
    }
}

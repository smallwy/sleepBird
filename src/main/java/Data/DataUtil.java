package Data;

public class DataUtil {

    public static void cmp(int[] arrays, int start, int end) {
        int temp = arrays[start];
        arrays[start] = arrays[end];
        arrays[end] = temp;
    }
}
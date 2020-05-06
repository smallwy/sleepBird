package Data;

/**
 * 选择排序
 *
 * @auther stone
 * @date 2020/5/3 0003 17:03
 */
public class SelectSort {

    static int[] arrays = Integes.randomInt(100, 3, 60);

    //选择排序  每次选择最大的值放在后面
    public void SelectTest3() {
        //首先 最外层循环有n个数  则循环n-1次
        for (int end = arrays.length - 1; end > 0; end--) {
            //假如下标为0的是最大数
            int maxIndex = 0;
            for (int start = 1; start <= end; start++) {
                if (arrays[start] > arrays[maxIndex]) {
                    maxIndex = start;
                }
            }
            int temp = arrays[maxIndex];
            arrays[maxIndex] = arrays[end];
            arrays[end] = temp;
        }
    }
}

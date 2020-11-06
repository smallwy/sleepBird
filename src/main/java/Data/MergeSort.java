package Data;


import org.junit.Test;

import java.util.Arrays;

/**
 * @Description:
 * @Param:
 * @return: 归并排序
 * @Author: stone
 * @Date: 2020/7/8
 */
public class MergeSort {

    int[] arr = {9, 18, 27, 6, 15, 34,11};

    private int[] leftArray;

    @Test
    public void test() {
        sort(arr);
        System.out.println(Arrays.toString(arr));
    }

    public void sort(int[] arr) {
        leftArray = new int[arr.length >> 1];//在排序前，先建好一个长度等于原数组长度的临时数组，避免递归中频繁开辟空间
        sort(0, arr.length);
    }

    private void sort(int begin, int end) {
        if (end - begin < 2) return;
        int mid = (begin + end) >> 1;
        sort(begin, mid);//左边归并排序，使得左子序列有序
        sort(mid, end);//右边归并排序，使得右子序列有序
        merge(begin, mid, end);//将两个有序子数组合并操作

    }

    private void merge(int begin, int mid, int end) {
        System.out.println("begin++++" + begin + "---mid++++" + mid + "---end++++" + end);
        int li = 0, le = mid - begin;
        int ri = mid, re = end;
        int ai = begin;

        for (int i = li; i < le; i++) {
            leftArray[i] = arr[begin + i];
        }
        while (li < le) {
            if (ri < re && arr[ri] - leftArray[li] < 0) {
                arr[ai++] = arr[ri++];

            } else {
                arr[ai++] = leftArray[li++];
            }
        }
    }
}
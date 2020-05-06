package Data;

public class HeadSort {
    public static void main(String[] args) {
        int[] array = {19, 38, 7, 36, 5, 5, 3, 2, 1, 0, 56};
        System.out.println("排序前:");
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i] + ",");
        }
        System.out.println();
        System.out.println("分割线---------------");
        heapSort(array);
        System.out.println("排序后:");
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i] + ",");
        }
    }

    public static void heapSort(int[] array) {
        if (array == null || array.length == 1)
            return;
        buildArrayToHeap(array); //将数组元素转化为大顶堆/小顶堆
        for (int i = array.length - 1; i >= 1; i--) {
            // 经过上面的一些列操作，目前array[0]是当前数组里最大的元素，需要和末尾的元素交换，然后拿出最大的元素
            swap(array, 0, i);
            /**
             * 交换完后，下次遍历的时候，就应该跳过最后一个元素，也就是最大的那个
             * 值，然后开始重新构建最大堆堆的大小就减去1，然后从0的位置开始最大堆
             */
            buildMaxHeap(array, i, 0);
            // buildMinHeap(array, i, 0);
        }
    }

    // 构建堆
    public static void buildArrayToHeap(int[] array) {
        if (array == null || array.length == 1)
            return;
        //递推公式就是 int root = 2*i, int left = 2*i+1, int right = 2*i+2;
        int cursor = array.length / 2;
        for (int i = cursor; i >= 0; i--) { // 这样for循环下，就可以第一次排序完成
			buildMaxHeap(array, array.length, i);
            //buildMinHeap(array, array.length, i);
        }
    }

    //大顶堆
    public static void buildMaxHeap(int[] array, int heapSieze, int index) {
        int left = index * 2 + 1; // 左子节点
        int right = index * 2 + 2; // 右子节点
        int maxValue = index; // 暂时定在Index的位置就是最大值
        // 如果左子节点的值，比当前最大的值大，就把最大值的位置换成左子节点的位置
        if (left < heapSieze && array[left] > array[maxValue]) {
            maxValue = left;
        }
        // 如果右子节点的值，比当前最大的值大，就把最大值的位置换成右子节点的位置
        if (right < heapSieze && array[right] > array[maxValue]) {
            maxValue = right;
        }
        // 如果不相等说明，这个子节点的值有比自己大的，位置发生了交换了位置
        if (maxValue != index) {
            swap(array, index, maxValue); // 就要交换位置元素
            // 交换完位置后还需要判断子节点是否打破了最大堆的性质。最大堆性质：两个子节点都比父节点小。
            buildMaxHeap(array, heapSieze, maxValue);
        }
    }

    //小顶堆
    public static void buildMinHeap(int[] array, int heapSieze, int index) {
        int left = index * 2 + 1; // 左子节点
        int right = index * 2 + 2; // 右子节点
        int maxValue = index; // 暂时定在Index的位置就是最小值
        // 如果左子节点的值，比当前最小的值小，就把最小值的位置换成左子节点的位置
        if (left < heapSieze && array[left] < array[maxValue]) {
            maxValue = left;
        }
        // 如果右子节点的值，比当前最小的值小，就把最小值的位置换成左子节点的位置
        if (right < heapSieze && array[right] < array[maxValue]) {
            maxValue = right;
        }
        // 如果不相等说明这个子节点的值有比自己小的，位置发生了交换了位置
        if (maxValue != index) {
            swap(array, index, maxValue); // 就要交换位置元素
            // 交换完位置后还需要判断子节点是否打破了最小堆的性质。最小性质：两个子节点都比父节点大。
            buildMinHeap(array, heapSieze, maxValue);
        }
    }

    // 数组元素交换
    public static void swap(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }
}

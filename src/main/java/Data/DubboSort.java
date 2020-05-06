package Data;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.RunnerException;

import java.util.concurrent.TimeUnit;

/**
 * @auther stone
 * @date 2020/5/3 0003 16:40
 */
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 1)
@Measurement(iterations = 1)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class DubboSort {

    static int[] arrays = Integes.randomInt(100, 3, 60);


    @Benchmark
    //冒泡排序1
    public void DobbleTest1() {
        //首先 最外层循环有n个数  则循环n-1次
        for (int end = arrays.length - 1; end > 0; end--) {
            //内层 i和i+1作比较 循环次数以end为基准
            for (int start = 0; start < end; start++) {
                if (arrays[start] > arrays[start + 1]) {
                    int temp = arrays[start];
                    arrays[start] = arrays[start + 1];
                    arrays[start + 1] = temp;
                }
            }
        }
    }


    /*如果后面的数是连续的  则结束运算*/
    @Benchmark
    //冒泡排序2
    public void DobbleTest2() {
        //首先 最外层循环有n个数  则循环n-1次
        for (int end = arrays.length - 1; end > 0; end--) {
            boolean flag = true;
            //内层 i和i+1作比较 循环次数以end为基准
            for (int start = 0; start < end; start++) {
                if (arrays[start] > arrays[start + 1]) {
                    int temp = arrays[start];
                    arrays[start] = arrays[start + 1];
                    arrays[start + 1] = temp;
                    flag = false;
                }
            }
            if (flag) {
                break;
            }
        }
    }


    /*记录最大值*/
    @Benchmark
    //冒泡排序3
    public void DobbleTest3() {
        //首先 最外层循环有n个数  则循环n-1次
        for (int end = arrays.length - 1; end > 0; end--) {
            int endIndex = 1;
            for (int start = 1; start <= end; start++) {
                if (arrays[start] < arrays[start - 1]) {
                    int temp = arrays[start];
                    arrays[start] = arrays[start - 1];
                    arrays[start - 1] = temp;
                    endIndex = start;
                }
            }
            end = endIndex;
        }
    }

    /*记录最大值*/
    @Benchmark
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

    public static void main(String[] args) throws RunnerException {
        /*Integes.ptint(arrays);
        Options opt = new OptionsBuilder()
                .include(maopao_1.class.getSimpleName())
                .build();
        new Runner(opt).run();*/
      /*  maopao_1 m=new maopao_1();
        m.test2();
        Integes.ptint(arrays);*/

        DubboSort m = new DubboSort();
        m.SelectTest3();
        Integes.ptint(arrays);

    }
}

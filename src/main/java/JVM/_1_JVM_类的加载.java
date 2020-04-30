package JVM;

/**
 * 加载
 *
 * @author wy
 * @create 2018-11-28 16:46
 **/
public class _1_JVM_类的加载 {


    private static _1_JVM_类的加载 sigton=new _1_JVM_类的加载();//(1)

    private static  int i=0;

    private  static int j;
    /*(1)和（2）输出的结果分别不一样  new 的时候调用构造函数 会使类初始化*/
    /*private static _1_JVM_类的加载 sigton=new _1_JVM_类的加载();(2)*/

    public static _1_JVM_类的加载 getSigton() {
        return sigton;
    }

    private  _1_JVM_类的加载(){
        i++;
        j++;
    }

    public static void main(String[] args){
        _1_JVM_类的加载 ss=_1_JVM_类的加载.getSigton();
        System.out.println(ss.i);
        System.out.println(ss.j);
    }



    /*
    *类的加载过程
    *加载阶段--》（验证，准备，解析）--》初始化阶段
    *   加载主要负责加载二进制文件 这里指的主要是class文件
    *   验证：确保文件的正确性 比如class版本
    *   准备：为类的静态变量分配内存 并且为其初始化默认值
    *   解析：把类中的符号引用转化为直接引用
    *初始化阶段：为类的静态变量赋予真正的值
    * */

    /*
    * 类的主动使用和被动使用
    *（1）使用new关键字会导致类的初始化
    *（2）访问类的静态变量 会导致类的初始化
    *（3）访问类的静态方法 会导致类的初始化
    *（4）使用反射操作 会导致类的初始化
    *（5）初始化子类会导致初始化父类
    * */



}

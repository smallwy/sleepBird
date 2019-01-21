package 设计模式.单例;

import org.junit.jupiter.api.Test;

import java.sql.Connection;

/**
 * 单例模式
 * 4中单例模式的实现
 * @author wy
 *
 * 如果一个类的成员比较少 切占用的内存资源不够多  可以用饿汉式  相反就不是很实用 因为饿汉式是类加载的时候就初始化 你没有用它就加载好了
 * @create 2018-11-30 11:02
 **/
public  class Gof23之单例模式 {

    Connection conn;


    /*------------------------饿汉式创建实例对象-----------*/

    private static Gof23之单例模式 sigton = new Gof23之单例模式();

  /*  public  Gof23之单例模式(){

    }*/

    private static Gof23之单例模式 getSigton() {
        return sigton;
    }




      /*------------------------懒汉式式创建实例对象-----------*/

    private static Gof23之单例模式 lanhanshi = null;


    public static synchronized Gof23之单例模式 getLanhanshi() {
        if (lanhanshi == null) {
            lanhanshi = new Gof23之单例模式();
        }
        return lanhanshi;
    }


     /*------------------------double check创建单例----------*/

    private static Gof23之单例模式 doublecheck = null;

    public Gof23之单例模式() {
        //这里的null没有赋值  实际上是赋值了
        this.conn = null;
    }

    public static Gof23之单例模式 getSington() {
        if (doublecheck == null) {
            synchronized (Gof23之单例模式.class) {
                if (doublecheck == null) {
                    doublecheck = new Gof23之单例模式();
                }

            }
        }
        return doublecheck;
    }

  /*------------------------holder创建单例----------*/

  /** 使用内部类创建对象*/
  private static  class holder {
      private static Gof23之单例模式 sigton = new Gof23之单例模式();
  }
  private static Gof23之单例模式 getHolderSington(){
      return holder.sigton;
  }




    /*------------------------枚举方式创建单例（此处不做介绍）----------*/

    /**
   * 什么是饿汉式设计模式
   * 饿汉式是类主动初始化的时候就将对象实例化出来
   * 类被jvm一加载 对象就创建出来 并可以直接使用 但不是懒加载
   * */
  @Test
  public void test_饿汉式单列() {
    System.out.println(
        Gof23之单例模式.getSigton());

    }
  /**/

  /**
   *什么是懒汉式设计模式
   * 懒汉式设计模式是使用的使用才加载对象 Gof23之单例模式实例化的时候 并不会创建对象
   * 在单线程环境下 懒汉式可以获得唯一的一个对象
   * 那么在多线程下 就会出现两个线程或者多个线程同时创建对象 导致并不能很好的实现单例
   * 所以我们这里可以加上synchronized锁机制创建唯一的实例
   *
   * */
  @Test
  public void test_懒汉式单列() {
        System.out.println(Gof23之单例模式.getLanhanshi());
    }

  /** 什么是checkDouble单例模式
   * 就是我们先检查对象存在与否  不存在我们加锁去创建  与懒汉式相比 是我创建对象之前先判断下有没有该实例 有的话我就不去创建  没有的话就去创建
   * 避免了每次都要进入同步代码块 提高效率
   * 获得锁机制的时候再判断一次是否实例化  再去尝试创建
   *
   * 注意：这里会存在空指针的情况  但我们刚创建完对象 但是我们的无参构造函数实例化并未及时更新到内存中
   * 那么另外一个线程拿到改实例化对象进行调用的时候 就会出现空指针异常情况
   * 所以这里需要加上一个可见性的关键字volitile  它可以将对象的创建和实例化立即推到主内存供其他线程可见
   *
   * */
  @Test
  public void test_checkDouble单列() {
        System.out.println(Gof23之单例模式.getSington());
    }

  /**通过内部类创建对象来实现懒加载  类在初始化的时候并不会实例化对象  只要我们调用的时候才开始创建 */
  @Test
  public void test_holder单列() {
        System.out.println(Gof23之单例模式.getHolderSington());
    }



}

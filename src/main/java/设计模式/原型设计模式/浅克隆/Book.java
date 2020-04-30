package 设计模式.原型设计模式.浅克隆;

import java.util.ArrayList;

/**
 * @author wy
 * @create 2019-01-21 20:19
 **/
public class Book extends Object implements Cloneable{

    private String title;

    private int age;

    private ArrayList<String> imgs=new ArrayList <String>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public ArrayList <String> getImgs() {
        return imgs;
    }

    public void setImgs(ArrayList <String> imgs) {
        this.imgs = imgs;
    }

  /**
   * 浅克隆  只是克隆基本类型  但是克隆不了String类型  因为String类型是final修饰的常亮 也无法深克隆 通常默认的是浅克隆
   *
   * 深克隆 将当前要可能的引用对象调用clone()方法即可创建新的引用  深克隆克隆之前的对象 但是不会改变之前的对象
   * @return */
  @Override
  protected Book clone() {
        try {
            Book book = (Book) super.clone();
            book.imgs=(ArrayList<String>)book.imgs.clone();
            return book;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void show(){
        System.out.println(this.age);
        System.out.println(this.title);
        for(String s:imgs) {
        System.out.println(s);
        }
    }
}

package 设计模式.原型设计模式.浅克隆;

/**
 * @author wy
 * @create 2019-01-21 20:23
 **/
public class TestClone {
  public static void main(String[] args) {

      Book book=new Book();
      book.setTitle("书1");
      book.setAge(1);
      book.getImgs().add("1111111");
      book.show();
      System.out.println("开始克隆");
      Book book1=book.clone();

      book1.setAge(2);
      book1.setTitle("书2");
      book1.getImgs().add("22222222");
      book1.show();
      System.out.println("克隆之后");
      book.show();
  }
}

package 设计模式.工厂设计模式.简单工厂;

/**
 * @author wy
 * @create 2019-01-18 14:25
 **/
public class CarTest {

  public static void main(String[] args) {

       Car audi=CarFactory.createCar("奥迪");
       audi.run();

       Car jili=CarFactory.createCar("吉利");
       jili.run();
  }
}

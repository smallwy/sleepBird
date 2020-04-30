package 设计模式.工厂设计模式.工厂方法模式;

/**
 * @author wy
 * @create 2019-01-18 14:44
 **/
public class CarTest {
  public static void main(String[] args) {
    Car audiCar=new AudiFacyory().createCar();
    audiCar.run();

    Car jiliCar=new JiliFactory().createCar();
    jiliCar.run();
  }
}

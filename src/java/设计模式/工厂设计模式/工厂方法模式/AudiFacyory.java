package 设计模式.工厂设计模式.工厂方法模式;

/**
 * @author wy
 * @create 2019-01-18 14:40
 **/
public class AudiFacyory implements CarFactory  {
    @Override
    public Car createCar() {
        return new AudiCar();
    }
}

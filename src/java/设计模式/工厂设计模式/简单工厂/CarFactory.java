package 设计模式.工厂设计模式.简单工厂;

/**
 *
 *  简单工厂模式能够根据外界给定的信息，
 *  决定究竟应该创建哪个具体类的对象。
 *  明确区分了各自的职责和权力，有利于整个软件体系结构的优化。
 *  缺点：很明显工厂类集中了所有实例的创建逻辑，容易违反GRASPR的高内聚的责任分配原则
 *
 * */
public class CarFactory {

    public static Car createCar(String name){

        if(name.equals("奥迪")){
            return new AudiCar();
        }else  if(name.equals("吉利")){
            return  new JiliCar();
        }
        return null;
    }
}

package 设计模式.模板方法设计模式;

/**
 * @author wy
 * @create 2019-01-21 15:35
 **/
public class PersonB extends BankTemplateMethod {
    @Override
    public void makeAct() {
    System.out.println("客户B处理相应的业务");
    }
}

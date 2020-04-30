package 设计模式.模板方法设计模式;

/**
 * @author wy
 * @create 2019-01-21 15:34
 **/
public class PersonA extends BankTemplateMethod {

    @Override
    public void makeAct() {
    System.out.println("客户A办理相关的业务");
    }
}

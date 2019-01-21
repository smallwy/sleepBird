package 设计模式.模板方法设计模式;

/**
 * @author wy
 * @create 2019-01-21 15:30
 **/
public abstract class BankTemplateMethod {

  /**排队取号  */
  public void takeNumber() {
        System.out.println("排队取号");
    }

  /** 业务处理 */
  public abstract void makeAct();

  /**离开走人 */
  public void takeLeft() {
    System.out.println("离开走人");
  }

  public void progress(){
      takeNumber();
      makeAct();
      takeLeft();
  }

}

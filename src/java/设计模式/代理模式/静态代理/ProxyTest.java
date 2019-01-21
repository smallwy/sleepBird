package 设计模式.代理模式.静态代理;

/**
 * @author wy
 * @create 2019-01-18 15:01
 **/
public class ProxyTest {
  public static void main(String[] args) {
    IUserDao userDao=new UserImpProxy(new UserImp());
    userDao.save();
  }
}

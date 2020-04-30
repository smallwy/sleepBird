package 设计模式.代理模式.静态代理;

/**
 * @author wy
 * @create 2019-01-18 14:58
 **/
public class UserImp implements IUserDao {
    @Override
    public void save() {
    System.out.println("开始保存数据");
    }
}

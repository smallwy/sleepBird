package 设计模式.代理模式.静态代理;

/**
 *
 * 代理类
 * @author wy
 * 谓静态也就是在程序运行前就已经存在代理类的字节码文件，代理类和委托类的关系在运行前就确定了。
 * @create 2019-01-18 14:59
 **/
public class UserImpProxy implements IUserDao {

    private UserImp userImp;

    public  UserImpProxy(UserImp userImp){
        this.userImp=userImp;
    }

    @Override
    public void save() {
    System.out.println("开始开启事务");
    userImp.save();
    System.out.println("事务开启完毕");
    }
}

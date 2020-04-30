package annotion_注解;

/**
 * @author wy
 * @create 2018-12-27 17:26
 **/
public class PasswordUtil {
    @annotion_定义(id=1,name="检查")
    public void checkPassword(){
    System.out.println("开始检查账号密码");
    }

    @annotion_定义(id=2,name="通过")
    public void passPassword(){
        System.out.println("通过检查账号密码");
    }
}

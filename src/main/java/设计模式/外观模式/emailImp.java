package 设计模式.外观模式;

/**
 * @author wy
 * @create 2019-01-21 16:10
 **/
public class emailImp implements EmialService {

    @Override
    public void sendEmial() {
    System.out.println("开始发送email");
    }
}

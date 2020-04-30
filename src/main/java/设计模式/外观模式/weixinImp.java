package 设计模式.外观模式;

/**
 * @author wy
 * @create 2019-01-21 16:14
 **/
public class weixinImp implements WeiXinService {
    @Override
    public void sendWeinxin() {
    System.out.println("开始发送微信");
    }
}

package 设计模式.适配器模式;

/**
 * @author wy
 * @create 2019-01-21 15:53
 **/
public class CN220VImp implements CN220VInterface {

    @Override
    public void connect() {
        System.out.println("中国220V,接通电源,开始工作");
    }
}

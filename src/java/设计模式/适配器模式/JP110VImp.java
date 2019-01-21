package 设计模式.适配器模式;

/**
 * @author wy
 * @create 2019-01-21 15:53
 **/
public class JP110VImp implements JP110VInterface {
    @Override
    public void connect() {
        System.out.println("日本110V,接通电源,开始工作..");
    }
}

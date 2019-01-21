package 设计模式.适配器模式;

/**
 * @author wy
 * @create 2019-01-21 15:59
 **/
public class testAdapter {
    public static void main(String[] args) {
        CN220VInterface cn220VInterface = new CN220VImp();
        PowerAdaptor powerAdaptor = new PowerAdaptor(cn220VInterface);
        // 电饭煲
        ElectricCooker cooker = new ElectricCooker(powerAdaptor);
        cooker.cook();//使用了适配器,在220V的环境可以工作了。
    }
}

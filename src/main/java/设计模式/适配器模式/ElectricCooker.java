package 设计模式.适配器模式;

/**
 * @author wy
 * @create 2019-01-21 15:54
 **/
public class ElectricCooker {

    private JP110VInterface jp110VInterface;

    ElectricCooker(JP110VInterface jp110VInterface){
        this.jp110VInterface=jp110VInterface;
    }

    public void cook(){
        jp110VInterface.connect();
        System.out.println("开始做饭了..");
    }

}

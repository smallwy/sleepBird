package 设计模式.适配器模式;

public class PowerAdaptor implements JP110VInterface {
    private CN220VInterface cn220VInterface;

    public PowerAdaptor(CN220VInterface cn220VInterface) {
        this.cn220VInterface = cn220VInterface;
    }

    @Override
    public void connect() {
        cn220VInterface.connect();
    }

}
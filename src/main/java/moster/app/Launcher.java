package moster.app;

import moster.infras.core.context.ServerContext;
import moster.infras.util.type.TypeRegistry;

/**
 * @author zhangfei
 */
public class Launcher {

    public static void main(String[] args) {
        launch();
    }

    // 初始化环境：比如环境变量
//    public void init()
//    public void start()

    public static void launch() {
        ServerContext.init("applicationContext.xml");
        TypeRegistry.init("com.gameart");

        ClientSimulator clientSimulator = ServerContext.getBean(ClientSimulator.class);
        boolean success = clientSimulator.init();
        if (!success) {
            System.exit(-1);
            return;
        }

        clientSimulator.start();
    }

}

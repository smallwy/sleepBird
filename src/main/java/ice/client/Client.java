package ice.client;

import ice.demo.HelloPrx;
import ice.demo.HelloPrxHelper;

public class Client {
    public static void main(String[] args) {
        int status = 0;
        Ice.Communicator ic = null;
        try {
            ic = Ice.Util.initialize(args);
            Ice.ObjectPrx base = ic.stringToProxy("hello:default -p 10006");
            HelloPrx hello = HelloPrxHelper.checkedCast(base);
            if (hello == null) {
                throw new Error("Invalid proxy");
            }

            String s = hello.sayHello("World!");
            System.out.println(">>" + s);
        } catch (Ice.LocalException e) {
            e.printStackTrace();
            status = 1;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            status = 1;
        }
        if (ic != null) {
            try {
                ic.destroy();
            } catch (Exception e) {
                System.err.println(e.getMessage());
                status = 1;
            }
        }
        System.exit(status);
    }
}

package ice.imp;

import Ice.Current;
import ice.demo._HelloDisp;

public class HelloImp extends _HelloDisp {
    @Override
    public String sayHello(String s, Current __current) {
        System.out.println(s);
        return "hello" + s;
    }
}

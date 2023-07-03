package ice.server;

import Ice.Communicator;
import Ice.ObjectAdapter;
import ice.imp.HelloImp;

public class Server {
    public static void main(String[] args) {
        int status = 0;
        Communicator ic = null;
        try{
            System.out.println("Server starting...");
            ic = Ice.Util.initialize(args);
            ObjectAdapter adapter = ic.createObjectAdapterWithEndpoints("iceTest", "default -p 10006");
            Ice.Object object = new HelloImp() {
            };
            adapter.add(object, ic.stringToIdentity("hello"));
            adapter.activate();
            System.out.println("Server start success.");
            ic.waitForShutdown();
        }catch(Ice.LocalException e){
            e.printStackTrace();
            status = 1;
        }catch(Exception e){
            System.err.println(e.getMessage());
            status = 1;
        }
        if(ic != null){
            try{
                ic.destroy();
            }catch(Exception e){
                System.err.println(e.getMessage());
                status = 1;
            }
        }
        System.exit(status);
    }
}

package netty;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 同步阻塞式请求
 *
 * @author wy
 * @create 2019-01-02 14:42
 **/
public class nio_同步阻塞 {



  public static void main(String[] args) throws IOException {
      int port = 8080;
      ServerSocket socket = null;
      if (args != null && args.length > 0) {
          port = Integer.parseInt(args[0]);
      }
      try {
          socket = new ServerSocket(port);
          System.out.println("the time 同步阻塞端口已经开启了...........");
          Socket socket1=null;
          while (true){
              socket1=socket.accept();
              new Thread(new ThreadHandleer(socket1)).start();
          }
      }finally{
          if(socket!=null) {
                System.out.println("nio_同步阻塞 已经关闭");
                socket.close();
          }
      }
  }
}

package netty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author wy
 * @create 2019-01-02 16:10
 **/



public class nio_client同步阻塞 {
  public static void main(String[] args) throws IOException {
    int port=8080;
      Socket socket=null;
      BufferedReader in=null;
      PrintWriter out=null;
      try{
          socket=new Socket("127.0.0.1",port);
          in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
          out=new PrintWriter(socket.getOutputStream(),true);
          out.println("客户端开始发送数据");
          System.out.println("客户端开始发送数据");
          String respp=in.readLine();
          System.out.println("now is"+respp);
      }finally{
          if(out!=null)
          { out.close();}
          if(in!=null){
              in.close();
          }
          if(socket!=null){
              socket.close();
          }
      }
  }
}

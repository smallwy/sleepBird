package netty;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author wy
 * @create 2019-01-02 15:48
 */
public class ThreadHandleer implements Runnable {
    private Socket socket;

    public ThreadHandleer(Socket socket1) {
        this.socket = socket1;
    }


    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            out = new PrintWriter(this.socket.getOutputStream(), true);
            String currentTime = null;
            String body = null;
            while (true) {
                body = in.readLine();
                if (body == null) {
                    break;
                }
                System.out.println("the time server recevice order " + body);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

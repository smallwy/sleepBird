package netty.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Reactor implements Runnable {
    final Selector selector;
    final ServerSocketChannel serverSocket;

    //Reactor初始化
    Reactor(int port) throws IOException {
        selector = Selector.open();
        serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(port));
        serverSocket.configureBlocking(false);

        //第一步,接收accept事件，监听新连接
        SelectionKey sk = serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        //SelectionKey的attach(Object o)方法可以将任何java对象作为附件添加到SelectionKey实例中，在单线程Reactor中用于添加处理器实例，此处添加了一个带有处理器实例的线程对象
        sk.attach((Runnable) () -> {
            try {
                SocketChannel channel = serverSocket.accept();
                if (channel != null)
                    new Handler(selector, channel);
            } catch (IOException ex) { /* ... */ }
        });
    }

    public void run() {
        try {
            while (true) {
                //获取就绪选择器
                selector.select();
                Set selected = selector.selectedKeys();
                Iterator it = selected.iterator();
                while (it.hasNext()) {
                    //分发收到的事件（Reactor主要任务）
                    SelectionKey k = (SelectionKey) (it.next());
                    //SelectionKey的attachment()可以获取attach(Object o)所添加的附件（java对象）
                    Runnable r = (Runnable) (k.attachment());
                    //调用之前注册的callback对象
                    if (r != null) {
                        r.run();
                    }
                }
                selected.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

//处理器线程
class Handler implements Runnable {
    final SocketChannel channel;
    final SelectionKey sk;
    ByteBuffer input = ByteBuffer.allocate(1024);
    ByteBuffer output = ByteBuffer.allocate(1024);
    static final int READING = 0, SENDING = 1;
    int state = READING;

    Handler(Selector selector, SocketChannel c) throws IOException {
        channel = c;
        c.configureBlocking(false);
        //注册到选择器，监听读就绪事件
        sk = channel.register(selector, SelectionKey.OP_READ);
        //将处理器自身作为附件
        sk.attach(this);
        //            //第二步,注册Read就绪事件
        //            sk.interestOps(SelectionKey.OP_READ);
        //唤醒可能存在的select()带来的阻塞状态
        selector.wakeup();
    }

    boolean inputIsComplete() {
        /* ... */
        return false;
    }

    boolean outputIsComplete() {

        /* ... */
        return false;
    }

    void process(int read) {
        ByteBuffer allocate = ByteBuffer.allocate(1024);
        int length = 0;
        while ((length = read) > 0) {
            allocate.flip();
            System.out.println(new String(allocate.array(), 0, length));
            allocate.clear();
        }
    }

    //业务逻辑处理
    public void run() {
        try {
            if (state == READING) {
                read();
            } else if (state == SENDING) {
                send();
            }
        } catch (IOException ex) { /* ... */ }
    }

    void read() throws IOException {
        int length = 0;
        while ((length = channel.read(input)) > 0) {
            input.flip();
            System.out.println(new String(input.array(), 0, length));
            input.clear();
        }
        state = SENDING;
        // Normally also do first write now

        //第三步,接收write就绪事件
        sk.interestOps(SelectionKey.OP_WRITE);
    }

    void send() throws IOException {
        channel.write(output);

        //write完就结束了, 关闭select key
        //这里不能关闭，因为该选择器在反应器线程中还有用
        sk.cancel();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new Thread(new Reactor(9888)).start();

        Thread.sleep(100000000);
    }
}

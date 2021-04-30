package netty._粘包拆包;

public class ServerNetty {


    public static void main(String[] args) {
      /*  EventLoopGroup boss = new NioEventLoopGroup(8);
        EventLoopGroup work = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, work).channel((Class<? extends ServerChannel>) TimeChannel.class).childOption(ChannelOption.TCP_NODELAY, true).childOption(ChannelOption.SO_KEEPALIVE, true);
        try {
            bootstrap.bind(8890).sync();
        } catch (InterruptedException e) {
            boss.shutdownGracefully();
            work.shutdownGracefully();
            e.printStackTrace();
        }*/
    }
}

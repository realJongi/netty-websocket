package de.realjongi.netty_websocket.server;

import de.realjongi.netty_websocket.NettyWebSocket;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;


public class WebSocketSslServer {

    private final NettyWebSocket nettyWebSocket;

    public WebSocketSslServer(NettyWebSocket nettyWebSocket) {
        this.nettyWebSocket = nettyWebSocket;
    }

    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private ServerBootstrap b = new ServerBootstrap();

    private Channel ch;

    public WebSocketSslServer run() throws Exception {

        Thread t = new Thread(new Runnable() {

            public void run() {
                bossGroup = new NioEventLoopGroup();
                workerGroup = new NioEventLoopGroup();

                try {
                    b.group(bossGroup, workerGroup)
                            .channel(NioServerSocketChannel.class)
                            .childHandler(new WebSocketSslServerInitializer(new WebSocketSslServerSslContext(nettyWebSocket), nettyWebSocket));

                    ch = b.bind(nettyWebSocket.getPort()).sync().channel();
                    ch.closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    bossGroup.shutdownGracefully();
                    workerGroup.shutdownGracefully();
                }
            }

        });
        t.start();

        return this;
    }

    public void close() {
        try {
            ch.close();
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
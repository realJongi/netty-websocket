package de.realjongi.netty_websocket.server;

import de.realjongi.netty_websocket.NettyWebSocket;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

public class WebSocketSslServerInitializer extends ChannelInitializer<SocketChannel> {

    private final NettyWebSocket nettyWebSocket;
    private final WebSocketSslServerSslContext webSocketSslServerSslContext;

    public WebSocketSslServerInitializer(WebSocketSslServerSslContext webSocketSslServerSslContext, NettyWebSocket nettyWebSocket) {
        this.webSocketSslServerSslContext = webSocketSslServerSslContext;
        this.nettyWebSocket = nettyWebSocket;
    }

    public NettyWebSocket getNettyWebSocket() {
        return nettyWebSocket;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        SSLEngine engine = webSocketSslServerSslContext.serverContext().createSSLEngine();
        engine.setUseClientMode(false);
        pipeline.addLast("ssl", new SslHandler(engine));

        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("handler", new WebSocketSslServerHandler(nettyWebSocket));
    }

}
package de.realjongi.netty_websocket;

import de.realjongi.netty_websocket.handler.WebSocketHandlerLine;
import de.realjongi.netty_websocket.server.WebSocketSslServer;
import de.realjongi.netty_websocket.utils.WebSocketKeystore;

public class NettyWebSocket {

    private final int port;

    private final WebSocketKeystore webSocketKeystore;
    private WebSocketHandlerLine webSocketHandlerLine;

    public NettyWebSocket(int port, WebSocketKeystore webSocketKeystore, WebSocketHandlerLine webSocketHandlerLine) {
        this.port = port;
        this.webSocketKeystore = webSocketKeystore;
        this.webSocketHandlerLine = webSocketHandlerLine;
    }

    protected WebSocketSslServer webSocketSslServer;

    public void init0() {
        this.webSocketSslServer = new WebSocketSslServer(this);
        this.webSocketSslServer.run();
    }

    public void close0() {
        this.webSocketSslServer.close();
    }

    public int getPort() {
        return port;
    }

    public WebSocketKeystore getWebSocketKeystore() {
        return webSocketKeystore;
    }

    /**
     * Only changing for new incoming connections
     */
    @Deprecated
    public void setWebSocketHandlerLine(WebSocketHandlerLine webSocketHandlerLine) {
        this.webSocketHandlerLine = webSocketHandlerLine;
    }

    public WebSocketHandlerLine getWebSocketHandlerLine() {
        return webSocketHandlerLine;
    }
}

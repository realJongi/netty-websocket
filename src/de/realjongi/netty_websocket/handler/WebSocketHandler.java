package de.realjongi.netty_websocket.handler;

public interface WebSocketHandler {

    void handle0(WebSocketHandlerLine webSocketHandlerLine, String in);

    void error0(WebSocketHandlerLine webSocketHandlerLine, Throwable err);

    void connect0(WebSocketHandlerLine webSocketHandlerLine);

    void close0(WebSocketHandlerLine webSocketHandlerLine);

}

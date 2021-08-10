package de.realjongi.netty_websocket.handler;

import de.realjongi.netty_websocket.NettyWebSocket;
import de.realjongi.netty_websocket.utils.WebSocketException;
import de.realjongi.netty_websocket.utils.WebSocketID;
import de.realjongi.netty_websocket.utils.WebSocketObject;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.ArrayList;
import java.util.List;

public class WebSocketHandlerLine extends WebSocketObject {

    private static List<WebSocketHandlerLine> webSocketHandlerLines = new ArrayList<>();

    public static WebSocketHandlerLine getWebSocketHandlerLine(Channel channel) {
        if(channel == null) {
            return null;
        }
        for(int i = 0; i < webSocketHandlerLines.size(); i++) {
            if(webSocketHandlerLines.get(i).getChannel().equals(channel)) {
                return webSocketHandlerLines.get(i);
            }
        }
        return null;
    }

    public static WebSocketHandlerLine getWebSocketHandlerLine(WebSocketID webSocketID) {
        if(webSocketID == null) {
            return null;
        }
        for(int i = 0; i < webSocketHandlerLines.size(); i++) {
            if(webSocketHandlerLines.get(i).getWebSocketID().equals(webSocketID)) {
                return webSocketHandlerLines.get(i);
            }
        }
        return null;
    }

    public static WebSocketHandlerLine initWebSocketHandlerLine(Channel channel, NettyWebSocket nettyWebSocket) {
        WebSocketHandlerLine check = getWebSocketHandlerLine(channel);
        if(check == null) {
            check = new WebSocketHandlerLine(channel);
            for(int i = 0; i < nettyWebSocket.getWebSocketHandlerLine().getWebSocketHandlers().size(); i++) {
                check.add(nettyWebSocket.getWebSocketHandlerLine().getWebSocketHandlers().get(i));
            }
            webSocketHandlerLines.add(check);
        }
        return check;
    }

    private Channel channel;

    private final List<WebSocketHandler> webSocketHandlers = new ArrayList<>();

    @Deprecated
    public List<WebSocketHandler> getWebSocketHandlers() {
        return webSocketHandlers;
    }

    public WebSocketHandlerLine(Channel channel) {
        this.channel = channel;
    }

    public void writeAndFlush(String write) {
        getChannel().writeAndFlush(new TextWebSocketFrame(write));
    }

    @Deprecated
    public void handle0(String in) throws WebSocketException {
        if(webSocketHandlers.size() > 0) {
            webSocketHandlers.get(0).handle0(this, in);
        } else {
            throw new WebSocketException("handling in: reached end of handlerLine");
        }
    }

    @Deprecated
    public void error0(Throwable err) throws WebSocketException {
        if(webSocketHandlers.size() > 0) {
            webSocketHandlers.get(0).error0(this, err);
        } else {
            throw new WebSocketException("handling error: reached end of handlerLine");
        }
    }

    @Deprecated
    public void connect0() {
        if(webSocketHandlers.size() > 0) {
            webSocketHandlers.get(0).connect0(this);
        }
    }

    @Deprecated
    public void close0() {
        if(webSocketHandlers.size() > 0) {
            webSocketHandlers.get(0).close0(this);
        }
        channel = null;
        webSocketHandlerLines.remove(this);
    }

    public void add(WebSocketHandler webSocketHandler) {
        if(!webSocketHandlers.contains(webSocketHandler)) {
            webSocketHandlers.add(webSocketHandler);
        }
    }

    public void remove(WebSocketHandler webSocketHandler) {
        webSocketHandlers.remove(webSocketHandler);
    }

    public void close() {
        getChannel().close();
        getChannel().closeFuture();
        channel = null;
        webSocketHandlerLines.remove(this);
    }

    @Deprecated
    public Channel getChannel() {
        return channel;
    }
}

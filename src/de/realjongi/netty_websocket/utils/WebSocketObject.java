package de.realjongi.netty_websocket.utils;

public class WebSocketObject {

    private final WebSocketID webSocketID = WebSocketID.generate();

    public WebSocketID getWebSocketID() {
        return webSocketID;
    }
}

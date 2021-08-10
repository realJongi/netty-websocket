package de.realjongi.netty_websocket.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class WebSocketID {

    private static List<WebSocketID> webSocketIDs = new ArrayList<>();

    @Deprecated
    public static WebSocketID getWebSocketID(String toString) {
        for(int i = 0; i < webSocketIDs.size(); i++) {
            if(webSocketIDs.get(i).toString().equalsIgnoreCase(toString)) {
                return webSocketIDs.get(i);
            }
        }
        return null;
    }

    public static WebSocketID generate() {
        String id = UUID.randomUUID().toString();
        while(getWebSocketID(System.currentTimeMillis() + "@" + id) != null) {
            id = UUID.randomUUID().toString();
        }
        List<WebSocketID> toRemove = new ArrayList<>();
        for(int i = 0; i < webSocketIDs.size(); i++) {
            if((webSocketIDs.get(i).getTimestamp()+5L) < System.currentTimeMillis()) {
                toRemove.add(webSocketIDs.get(i));
            }
        }
        for(int i = 0; i < toRemove.size(); i++) {
            webSocketIDs.remove(toRemove.get(i));
        }
        WebSocketID rtrn = new WebSocketID(id);
        webSocketIDs.add(rtrn);
        return rtrn;
    }

    private final Long timestamp;
    private final String id;

    public WebSocketID(String id) {
        this.timestamp = System.currentTimeMillis();
        this.id = id;
    }

    @Deprecated
    public Long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return timestamp + "@" + id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebSocketID webSocketID = (WebSocketID) o;
        return timestamp.equals(webSocketID.timestamp) && id.equals(webSocketID.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, id);
    }
}

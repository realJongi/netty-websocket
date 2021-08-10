package de.realjongi.netty_websocket.utils;

public class WebSocketKeystore {

    private final String filePath;
    private final String password;

    public WebSocketKeystore(String filePath, String password) {
        this.filePath = filePath;
        this.password = password;
    }

    @Deprecated
    public String getFilePath() {
        return filePath;
    }

    @Deprecated
    public String getPassword() {
        return password;
    }
}

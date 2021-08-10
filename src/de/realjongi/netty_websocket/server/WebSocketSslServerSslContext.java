package de.realjongi.netty_websocket.server;

import de.realjongi.netty_websocket.NettyWebSocket;
import de.realjongi.netty_websocket.utils.WebSocketKeystore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.Security;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class WebSocketSslServerSslContext {

    private static final Logger logger = Logger.getLogger(WebSocketSslServerSslContext.class.getName());
    private static final String PROTOCOL = "TLS";
    private final SSLContext _serverContext;

    private final NettyWebSocket nettyWebSocket;

    public WebSocketSslServerSslContext(NettyWebSocket nettyWebSocket) {
        this.nettyWebSocket = nettyWebSocket;
        SSLContext serverContext = null;
        try {

            String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
            if (algorithm == null) {
                algorithm = "SunX509";
            }

            try {
                String keyStoreFilePath = nettyWebSocket.getWebSocketKeystore().getFilePath();
                String keyStoreFilePassword = nettyWebSocket.getWebSocketKeystore().getPassword();

                KeyStore ks = KeyStore.getInstance("JKS");
                FileInputStream fin = new FileInputStream(keyStoreFilePath);
                ks.load(fin, keyStoreFilePassword.toCharArray());

                KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
                kmf.init(ks, keyStoreFilePassword.toCharArray());

                serverContext = SSLContext.getInstance(PROTOCOL);
                serverContext.init(kmf.getKeyManagers(), null, null);
            } catch (Exception e) {
                throw new Error("Failed to initialize the server-side SSLContext", e);
            }
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Error initializing SslContextManager.", ex);
            System.exit(1);
        } finally {
            _serverContext = serverContext;
        }
    }

    public NettyWebSocket getNettyWebSocket() {
        return nettyWebSocket;
    }

    public SSLContext serverContext() {
        return _serverContext;
    }

}
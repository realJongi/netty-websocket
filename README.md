# NettyWebSocket

## About

This library will help you create a websocket server, secured with a X.509 (SSL) keystore with Netty, so that you can handle websocket connections in Java.

## How to

1. Create a WebSocketHandlerLine without a given Channel:

      ```java
      WebSocketHandlerLine webSocketHandlerLine = new WebSocketHandlerLine(null);
      ```
   
2. Create and add the WebSocketHandler's you want to add:

      * Create ExampleHandler.java class:
      
    
      ```java
      import de.realjongi.netty_websocket.handler.WebSocketHandler;
      import de.realjongi.netty_websocket.handler.WebSocketHandlerLine;

      public class ExampleHandler implements WebSocketHandler {

          @Override
          public void handle0(WebSocketHandlerLine webSocketHandlerLine, String in) {
              System.out.println("Incoming:");
              System.out.println(in);
          }

          @Override
          public void error0(WebSocketHandlerLine webSocketHandlerLine, Throwable err) {
              System.err.println("Error:");
              err.printStackTrace();
          }

          @Override
          public void connect0(WebSocketHandlerLine webSocketHandlerLine) {
              System.out.println("Connected");
          }

          @Override
          public void close0(WebSocketHandlerLine webSocketHandlerLine) {
              System.out.println("Closed");
          }
      }
      ```
      
      You can add as many handlers as you want, while the line will follow the order the handlers have been added.
      You can remove a handler, for example a authentication handler after a successful authentication:
      
      ```java
      webSocketHandlerLine.remove(this);
      ```
      (in the handler itself OR save the handler instance)
      
      You can identify incoming/established connections by the WebSocketID OR by the Netty Channel:
      
      ```java
      webSocketHandlerLine.getWebSocketID();
      webSocketHandlerLine.getChannel();
      ```
      
      * Add ExampleHandler:
      
      
      ```java
      webSocketHandlerLine.add(new ExampleHandler());
      ```
      
3. Create a NettyWebSocket instance and initialize the server:

     ```java
     NettyWebSocket nettyWebSocket = new NettyWebSocket(/*port:*/4000, new WebSocketKeystore(/*path to keystore file:*/"server.keystore", /*password for keystore file:*/"password"), webSocketHandlerLine);
      nettyWebSocket.init0();
      ```
      
4. Close the NettyWebSocket (optional):

     ```java
     nettyWebSocket.close0();
     ```

## Libraries used

* https://github.com/mbakkar/netty-1/tree/master/example/src/main/java/io/netty/example/http/websocketx/sslserver

## Copyright

(except used libraries) Copyright 2021 by realJongi

Licensed under the [MIT License]

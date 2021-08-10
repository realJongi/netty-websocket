package de.realjongi.netty_websocket.server;

import de.realjongi.netty_websocket.NettyWebSocket;
import de.realjongi.netty_websocket.handler.WebSocketHandlerLine;
import de.realjongi.netty_websocket.utils.WebSocketException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.HOST;
import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.setContentLength;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class WebSocketSslServerHandler extends SimpleChannelInboundHandler<Object> {

    private final NettyWebSocket nettyWebSocket;

    public WebSocketSslServerHandler(NettyWebSocket nettyWebSocket) {
        this.nettyWebSocket = nettyWebSocket;
    }

    public NettyWebSocket getNettyWebSocket() {
        return nettyWebSocket;
    }

    private static final Logger logger = Logger.getLogger(WebSocketSslServerHandler.class.getName());

    private static final String WEBSOCKET_PATH = "/websocket";

    private WebSocketServerHandshaker handshaker;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        if (!req.getDecoderResult().isSuccess()) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
            return;
        }

        if (req.getMethod() != GET) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
            return;
        }

        if ("/".equals(req.getUri())) {
            ByteBuf content = Unpooled.copiedBuffer("NettyWebSocket, open-source library by https://github.com/realJongi", StandardCharsets.US_ASCII);
            FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK, content);

            res.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
            setContentLength(res, content.readableBytes());

            sendHttpResponse(ctx, req, res);
            return;
        }

        if ("/favicon.ico".equals(req.getUri())) {
            FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND);
            sendHttpResponse(ctx, req, res);
            return;
        }

        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(req), null, false);
        handshaker = wsFactory.newHandshaker(req);

        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) throws WebSocketException {

        if(frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }

        if(frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }

        if(!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
        }

        WebSocketHandlerLine line = WebSocketHandlerLine.getWebSocketHandlerLine(ctx.channel());

        if(line == null) {
            line = WebSocketHandlerLine.initWebSocketHandlerLine(ctx.channel(), nettyWebSocket);
            line.connect0();
        }

        String request = ((TextWebSocketFrame) frame).text();

        line.handle0(request);

    }

    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {

        if(res.getStatus().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            setContentLength(res, res.content().readableBytes());
        }

        ChannelFuture f = ctx.channel().write(res);
        if(!isKeepAlive(req) || res.getStatus().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        WebSocketHandlerLine line = WebSocketHandlerLine.getWebSocketHandlerLine(ctx.channel());

        if(line == null) {
            line = WebSocketHandlerLine.initWebSocketHandlerLine(ctx.channel(), nettyWebSocket);
        }

        line.close0();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        WebSocketHandlerLine line = WebSocketHandlerLine.getWebSocketHandlerLine(ctx.channel());

        if(line == null) {
            line = WebSocketHandlerLine.initWebSocketHandlerLine(ctx.channel(), nettyWebSocket);
        }

        line.error0(cause);
    }

    private static String getWebSocketLocation(FullHttpRequest req) {
        return "wss://" + req.headers().get(HOST) + WEBSOCKET_PATH;
    }

}
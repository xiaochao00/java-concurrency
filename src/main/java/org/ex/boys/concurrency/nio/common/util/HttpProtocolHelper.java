package org.ex.boys.concurrency.nio.common.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_0;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * http protocol
 *
 * @author shichao
 * @since 1.0.0
 * 2021/7/18 23:40
 */
public class HttpProtocolHelper {

    public static final AttributeKey<HttpVersion> PROTOCOL_VERSION_KEY = AttributeKey.valueOf("PROTOCOL_VERSION");
    public static final AttributeKey<Boolean> KEEP_ALIVE_KEY = AttributeKey.valueOf("KEEP_ALIVE_KEY");

    private HttpProtocolHelper() {

    }

    /**
     * 发送响应信息
     *
     * @param ctx     管道上下文
     * @param content 响应的内容
     */
    public static void sendJsonContent(ChannelHandlerContext ctx, String content) {
        HttpVersion version = getHttpVersion(ctx);
        ByteBuf byteBuf = Unpooled.copiedBuffer(content, CharsetUtil.UTF_8);
        // 构建一个默认的FullHttpResponse实例
        FullHttpResponse response = new DefaultFullHttpResponse(version, HttpResponseStatus.OK, byteBuf);
        // 设置响应头信息
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json;charset=UTF-8");
        // 发送内容
        sendAndCleanupConnection(ctx, response);
    }

    public static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        HttpVersion version = getHttpVersion(ctx);
        FullHttpResponse response = new DefaultFullHttpResponse(
                version, status, Unpooled.copiedBuffer("Failure: " + status + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        sendAndCleanupConnection(ctx, response);
    }

    public static void sendAndCleanupConnection(ChannelHandlerContext ctx, FullHttpResponse response) {
        final boolean keepAlive = ctx.channel().attr(KEEP_ALIVE_KEY).get();
        HttpUtil.setContentLength(response, response.content().readableBytes());
        if (!keepAlive) {
            // 如果不是长连接，设置 Connection: close
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        } else if (isHTTP_1_0(ctx)) {
            // 如果是1.0版本的http协议，就设置connection:keep-alive
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        // 发送内容
        ChannelFuture future = ctx.writeAndFlush(response);
        if (!keepAlive) {
            // 如果不是长连接，发送完成后关闭连接
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    public static HttpVersion getHttpVersion(ChannelHandlerContext ctx) {
        if (isHTTP_1_0(ctx)) {
            return HTTP_1_0;
        }
        return HTTP_1_1;
    }

    public static boolean isHTTP_1_0(ChannelHandlerContext ctx) {
        HttpVersion protocol_version = ctx.channel().attr(PROTOCOL_VERSION_KEY).get();
        if (null == protocol_version) {
            return false;
        }
        return protocol_version.equals(HTTP_1_0);
    }

    public static void cacheHttpProtocol(ChannelHandlerContext ctx, final FullHttpRequest request) {
        //每一个连接设置一次即可，不需要重复设置
        if (ctx.channel().attr(KEEP_ALIVE_KEY).get() == null) {
            ctx.channel().attr(PROTOCOL_VERSION_KEY).set(request.protocolVersion());
            final boolean keepAlive = HttpUtil.isKeepAlive(request);
            ctx.channel().attr(KEEP_ALIVE_KEY).set(keepAlive);
        }
    }
}

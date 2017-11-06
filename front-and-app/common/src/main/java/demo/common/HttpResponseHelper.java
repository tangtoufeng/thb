package demo.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public final class HttpResponseHelper {
    private HttpResponseHelper() {
    }

    public static void responseOK(ChannelHandlerContext ctx, String responseData) {
        response(ctx, responseData, HttpResponseStatus.OK);
    }

    public static void responseTooMany(ChannelHandlerContext ctx, String responseData) {
        response(ctx, responseData, HttpResponseStatus.TOO_MANY_REQUESTS);
    }

    public static void responseTimeout(ChannelHandlerContext ctx, String responseData) {
        response(ctx, responseData, HttpResponseStatus.GATEWAY_TIMEOUT);
    }

    public static void responseForbidden(ChannelHandlerContext ctx, String responseData) {
        response(ctx, responseData, HttpResponseStatus.FORBIDDEN);
    }

    public static void responseInnerSrvError(ChannelHandlerContext ctx, String responseData) {
        response(ctx, responseData, HttpResponseStatus.INTERNAL_SERVER_ERROR);
    }

    private static void response(ChannelHandlerContext ctx, String responseData, HttpResponseStatus status) {
        ByteBuf content = Unpooled.copiedBuffer(responseData, CharsetUtil.UTF_8);
        DefaultFullHttpResponse response =
                new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, content);

        response.headers().set(HttpHeaderNames.CONNECTION, "keep-alive");
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

        ctx.writeAndFlush(response);
    }
}

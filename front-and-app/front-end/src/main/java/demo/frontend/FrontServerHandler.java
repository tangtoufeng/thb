package demo.frontend;

import demo.appcli.AppClientWrapper;
import demo.common.HttpResponseHelper;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.multipart.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ChannelHandler.Sharable
public class FrontServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private HttpDataFactory factory = new DefaultHttpDataFactory(false);

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        HttpMethod method = request.method();
        if (HttpMethod.POST.equals(method)) {
            Map<String, String> param = parseHttp(request);
            if (!AppClientWrapper.getClient().putHttpReq(ctx, param)) {
                HttpResponseHelper.responseTooMany(ctx, "server is full, request later");
            }
        } else {
            HttpResponseHelper.responseForbidden(ctx, "not POST method");
        }
    }

    private Map<String, String> parseHttp(FullHttpRequest request) {
        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(factory, request, Charset.forName("UTF-8"));
        HashMap<String, String> parameters = new HashMap<>();
        List<InterfaceHttpData> list = decoder.getBodyHttpDatas();
        for (InterfaceHttpData data : list) {
            if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                try {
                    Attribute attribute = (Attribute) data;
                    String name = attribute.getName();
                    String value = attribute.getValue();
                    parameters.put(name, value);
                } catch (IOException e) {
                    //
                }
            }
        }
        decoder.destroy();
        return parameters;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        HttpResponseHelper.responseInnerSrvError(ctx, "server error!");
    }
}

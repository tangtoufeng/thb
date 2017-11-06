package demo.frontend;

import demo.common.DefaultConfig;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;

import static demo.common.ConfigEntry.FRONT_END_HTTP_MAX_CONTENT_LENGTH;

public class FrontServerInitializer extends ChannelInitializer<SocketChannel> {
    private static final DefaultConfig CONFIG = DefaultConfig.getConfig();
    private final FrontServerHandler handler;

    public FrontServerInitializer(FrontServerHandler handler) {
        this.handler = handler;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new HttpServerCodec());
        p.addLast(new HttpServerExpectContinueHandler());
        p.addLast("aggregator",
                new HttpObjectAggregator(CONFIG.getIntByKey(FRONT_END_HTTP_MAX_CONTENT_LENGTH)));//处理Post消息体时需要加上
        p.addLast(handler);
    }
}

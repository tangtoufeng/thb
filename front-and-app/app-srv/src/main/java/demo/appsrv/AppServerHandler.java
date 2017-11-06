package demo.appsrv;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class AppServerHandler extends SimpleChannelInboundHandler<String> {
    private AppProcessor processor;

    public AppServerHandler(AppProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String msg) {
        if ("{}".equals(msg)) {
            ctx.writeAndFlush("{}\r\n");
        } else {
            processor.push(ctx, msg);
        }
    }

}

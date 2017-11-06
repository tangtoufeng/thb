package demo.appcli;

import demo.common.HttpResponseHelper;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

class TimeoutRunnable implements Runnable {
    private Map<String, ChannelHandlerContext> map;
    private String id;

    public TimeoutRunnable(Map<String, ChannelHandlerContext> map, String id) {
        this.map = map;
        this.id = id;
    }

    @Override
    public void run() {
        ChannelHandlerContext ctx = map.remove(id);
        if (ctx != null) {
            HttpResponseHelper.responseTimeout(ctx, "request time out");
        }
    }
}

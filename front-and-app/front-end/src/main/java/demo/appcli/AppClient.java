package demo.appcli;

import demo.common.DefaultConfig;
import demo.common.OSDetect;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static demo.common.ConfigEntry.*;

public class AppClient {
    private static final DefaultConfig CONFIG = DefaultConfig.getConfig();

    private static final ConcurrentHashMap<String, ChannelHandlerContext> MAP = new ConcurrentHashMap<>(CONFIG.getIntByKey(APP_CLI_QUEUE_SIZE));
    private static final Bootstrap BOOTSTRAP = new Bootstrap();
    private static final EventLoopGroup EVENT_LOOP_GROUP = OSDetect.chooseEventLoopGroup(CONFIG.getIntByKey(APP_CLI_GROUP_SIZE));
    private static final ThreadLocal<Channel> CHANNEL_THREAD_LOCAL = new ThreadLocal<>();
    private static final AppClientHandler CLIENT_HANDLER = new AppClientHandler(MAP);

    public static void init() {
        BOOTSTRAP.group(EVENT_LOOP_GROUP)
                .channel(OSDetect.chooseChannel())
                .handler(new AppClientInitializer(CLIENT_HANDLER));
    }

    public static void destroy() {
        EVENT_LOOP_GROUP.shutdownGracefully();
    }

    public void heartbeat() {
        try {
            Channel ch = getChannel();
            ch.writeAndFlush("{}\r\n");
        } catch (InterruptedException e) {
            //
        }
    }

    public void post(final ChannelHandlerContext ctx, final String json, final String id) {
        MAP.put(id, ctx);
        try {
            Channel ch = getChannel();
            ch.writeAndFlush(json + "\r\n");
        } catch (InterruptedException e) {
            //
        }
        EVENT_LOOP_GROUP.schedule(new TimeoutRunnable(MAP, id), CONFIG.getIntByKey(APP_CLI_TIMEOUT), TimeUnit.MILLISECONDS);
    }

    private Channel getChannel() throws InterruptedException {
        Channel ch = CHANNEL_THREAD_LOCAL.get();
        if (ch == null || !ch.isActive()) {
            ch = BOOTSTRAP.connect(CONFIG.getStrByKey(APP_CLI_HOST), CONFIG.getIntByKey(APP_CLI_PORT)).channel();
            CHANNEL_THREAD_LOCAL.set(ch);
        }
        return ch;
    }

}

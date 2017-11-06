package demo.appsrv;

import demo.common.DefaultConfig;
import demo.common.OSDetect;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;

import static demo.common.ConfigEntry.*;

public class AppServer {
    private static final DefaultConfig CONFIG = DefaultConfig.getConfig();

    public void serve() {
        AppProcessor appProcessor = new AppProcessor();
        appProcessor.startProcess();
        AppServerHandler handler = new AppServerHandler(appProcessor);
        // Configure the server.
        EventLoopGroup bossGroup = OSDetect.chooseEventLoopGroup(CONFIG.getIntByKey(APP_SRV_BOOS_GROUP_SIZE));
        EventLoopGroup workerGroup = OSDetect.chooseEventLoopGroup(CONFIG.getIntByKey(APP_SRV_WORK_GROUP_SIZE));
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(OSDetect.chooseServerChannel())
                    .option(ChannelOption.SO_BACKLOG, CONFIG.getIntByKey(APP_SRV_SO_BACKLOG))
                    .childHandler(new AppServerInitializer(handler));

            // Start the server.
            ChannelFuture f = b.bind(CONFIG.getIntByKey(APP_SRV_PORT)).sync();

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            //
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
        appProcessor.endProcess();
    }
}

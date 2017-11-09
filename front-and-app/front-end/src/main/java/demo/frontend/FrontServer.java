package demo.frontend;

import demo.appcli.AppClientWrapper;
import demo.common.DefaultConfig;
import demo.common.OSDetect;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;

import static demo.common.ConfigEntry.*;

public class FrontServer {
    private static final DefaultConfig CONFIG = DefaultConfig.getConfig();

    public FrontServer() {
    }

    public void serve(int port) {
        AppClientWrapper wrapper = AppClientWrapper.getClient();
        wrapper.startLoop();
        final FrontServerHandler handler = new FrontServerHandler(port+"");
        // Configure the server.
        EventLoopGroup bossGroup =
                OSDetect.chooseEventLoopGroup(CONFIG.getIntByKey(FRONT_END_BOSS_GROUP_SIZE));
        EventLoopGroup workerGroup =
                OSDetect.chooseEventLoopGroup(CONFIG.getIntByKey(FRONT_END_WORK_GROUP_SIZE));
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(OSDetect.chooseServerChannel())
                    .option(ChannelOption.SO_BACKLOG, CONFIG.getIntByKey(FRONT_END_SO_BACKLOG))
                    .childHandler(new FrontServerInitializer(handler));

            // Start the server.
            ChannelFuture f = b.bind(port).sync();

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            //
        } finally {
            wrapper.stopLoop();
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}

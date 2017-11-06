package demo.common;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class OSDetect {
    private static String OS = System.getProperty("os.name").toLowerCase();

    private static boolean isLinux() {
        return (OS.contains("linux"));
    }

    public static Class<? extends Channel> chooseChannel() {
        if (isLinux()) {
            return EpollSocketChannel.class;
        } else {
            return NioSocketChannel.class;
        }
    }

    public static Class<? extends ServerChannel> chooseServerChannel() {
        if (isLinux()) {
            return EpollServerSocketChannel.class;
        } else {
            return NioServerSocketChannel.class;
        }
    }


    public static EventLoopGroup chooseEventLoopGroup() {
        if (isLinux()) {
            return new EpollEventLoopGroup();
        } else {
            return new NioEventLoopGroup();
        }
    }

    public static EventLoopGroup chooseEventLoopGroup(int n) {
        if (isLinux()) {
            return new EpollEventLoopGroup(n);
        } else {
            return new NioEventLoopGroup(n);
        }
    }
}

package demo.appsrv;

import demo.common.CommonThreadFactory;
import demo.common.DefaultConfig;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static demo.common.ConfigEntry.*;

public class AppProcessor {

    private static final DefaultConfig CONFIG = DefaultConfig.getConfig();

    private static final int TRY_TIMES = 3;
    private BlockingQueue<Object[]> queue = new ArrayBlockingQueue<>(CONFIG.getIntByKey(APP_SRV_QUEUE_SIZE));
    private AtomicBoolean active = new AtomicBoolean(true);
    private int size = CONFIG.getIntByKey(APP_SRV_POOL_SIZE);
    private ExecutorService pool;

    public boolean push(ChannelHandlerContext ctx, String json) {
        for (int i = 0; i < TRY_TIMES; i++) {
            if (active.get() && queue.offer(new Object[]{ctx, json})) {
                return true;
            }
        }
        return false;
    }

    public void startProcess() {
        pool = Executors.newFixedThreadPool(size, new CommonThreadFactory("AppProcess"));
        for (int i = 0; i < size; i++) {
            pool.submit(new ProcessRunnable(active, queue));
        }
    }

    public void endProcess() {
        active.set(false);
        if (pool != null) {
            try {
                pool.awaitTermination(CONFIG.getIntByKey(APP_SRV_POOL_QUIT_TIMEOUT), TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                //
            }
        }
    }

}

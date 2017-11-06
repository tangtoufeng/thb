package demo.appcli;

import demo.common.CommonThreadFactory;
import demo.common.DefaultConfig;
import demo.common.SnowflakeIdWorker;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static demo.common.ConfigEntry.*;

public class AppClientWrapper {
    private static final DefaultConfig CONFIG = DefaultConfig.getConfig();

    private static final int TRY_TIMES = 3;
    private static final AppClientWrapper CLIENT = new AppClientWrapper();

    private AtomicBoolean active = new AtomicBoolean(true);
    private int size = CONFIG.getIntByKey(APP_CLI_POOL_SIZE);
    private int workerId = CONFIG.getIntByKey(APP_CLI_WORK_ID);
    private int dataCenterId = CONFIG.getIntByKey(APP_CLI_DATA_CENTER_ID);
    private ExecutorService pool;
    private BlockingQueue<Object[]> queue = new ArrayBlockingQueue<>(CONFIG.getIntByKey(APP_CLI_QUEUE_SIZE));
    private SnowflakeIdWorker idWorker;

    public static AppClientWrapper getClient() {
        return CLIENT;
    }

    private AppClientWrapper() {
    }

    public boolean putHttpReq(ChannelHandlerContext ctx, Map<String, String> param) {
        for (int i = 0; i < TRY_TIMES; i++) {
            if (active.get() && queue.offer(new Object[]{ctx, param})) {
                return true;
            }
        }
        return false;
    }

    public void startLoop() {
        idWorker = new SnowflakeIdWorker(workerId, dataCenterId);
        pool = Executors.newFixedThreadPool(size, new CommonThreadFactory("AppClient"));
        AppClient.init();
        for (int i = 0; i < size; i++) {
            pool.submit(new PostRunnable(active, queue, idWorker));
        }
    }

    public void stopLoop() {
        active.set(false);
        if (pool != null) {
            try {
                pool.awaitTermination(CONFIG.getIntByKey(APP_CLI_POOL_QUIT_TIMEOUT), TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                //
            }
        }
        AppClient.destroy();
    }

}

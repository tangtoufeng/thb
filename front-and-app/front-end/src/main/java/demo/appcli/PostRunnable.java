package demo.appcli;

import com.alibaba.fastjson.JSON;
import demo.common.DefaultConfig;
import demo.common.SnowflakeIdWorker;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static demo.common.ConfigEntry.APP_CLI_HEARTBEAT_INTERVAL;
import static demo.common.ConfigEntry.APP_CLI_QUEUE_WAIT_TIMEOUT;

class PostRunnable implements Runnable {
    private static final DefaultConfig CONFIG = DefaultConfig.getConfig();
    private AtomicBoolean active;
    private BlockingQueue<Object[]> queue;
    private SnowflakeIdWorker idWorker;

    public PostRunnable(AtomicBoolean active, BlockingQueue<Object[]> queue, SnowflakeIdWorker idWorker) {
        this.active = active;
        this.queue = queue;
        this.idWorker = idWorker;
    }

    @Override
    public void run() {
        long last = System.currentTimeMillis();
        AppClient client = new AppClient();
        Object[] array;
        String[] jsonAndId;
        String json;
        String id;
        long now;
        while (active.get()) {
            try {
                array = queue.poll(CONFIG.getIntByKey(APP_CLI_QUEUE_WAIT_TIMEOUT), TimeUnit.MILLISECONDS);
                if (array == null) {//保活
                    now = System.currentTimeMillis();
                    if (now - last > CONFIG.getIntByKey(APP_CLI_HEARTBEAT_INTERVAL)) {
                        last = now;
                        client.heartbeat();
                    }
                } else {
                    jsonAndId = mapToJson((Map<String, String>) array[1]);
                    json = jsonAndId[0];
                    id = jsonAndId[1];
                    client.post((ChannelHandlerContext) array[0], json, id);
                    last = System.currentTimeMillis();
                }
            } catch (InterruptedException e) {
                //
            }
        }
    }

    private String[] mapToJson(Map<String, String> parameters) {
        String innerId = Long.toString(idWorker.nextId());
        parameters.put("InnerId", innerId);
        return new String[]{JSON.toJSONString(parameters), innerId};
    }
}

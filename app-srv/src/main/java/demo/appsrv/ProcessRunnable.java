package demo.appsrv;

import com.alibaba.fastjson.JSON;
import demo.common.DefaultConfig;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static demo.common.ConfigEntry.APP_SRV_QUEUE_WAIT_TIMEOUT;

class ProcessRunnable implements Runnable {
    private static final DefaultConfig CONFIG = DefaultConfig.getConfig();
    private AtomicBoolean active;
    private BlockingQueue<Object[]> queue;

    public ProcessRunnable(AtomicBoolean active, BlockingQueue<Object[]> queue) {
        this.active = active;
        this.queue = queue;
    }

    @Override
    public void run() {
        Object[] array;
        ChannelHandlerContext ctx;
        String json;
        String retJson;
        while (active.get()) {
            try {
                array = queue.poll(CONFIG.getIntByKey(APP_SRV_QUEUE_WAIT_TIMEOUT), TimeUnit.MILLISECONDS);
                if (array != null) {
                    ctx = (ChannelHandlerContext) array[0];
                    json = (String) array[1];
                    Map<String, String> map = JSON.parseObject(json, HashMap.class);
                    String id = map.get("InnerId");
                    retJson = process(json);
                    ctx.writeAndFlush(id + "$|$" + retJson + "\r\n");
                }
            } catch (InterruptedException e) {
                //
            }
        }
    }

    private String process(String json) {
        mockInvoke();//TODO:假执行，需要改为真实逻辑
        return json;
    }

    /**
     * 假执行
     */
    private void mockInvoke() {
        try {
            Thread.sleep(40L);//测试平均耗时
        } catch (InterruptedException e) {
            //
        }
    }
}

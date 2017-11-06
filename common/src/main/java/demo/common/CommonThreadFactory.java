package demo.common;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by hellyguo on 2016/6/16.
 */
public class CommonThreadFactory implements ThreadFactory {
    private AtomicLong count = new AtomicLong(0);

    private String prefix;

    public CommonThreadFactory(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r, prefix + '_' + count.incrementAndGet());
        thread.setDaemon(true);
        return thread;
    }
}

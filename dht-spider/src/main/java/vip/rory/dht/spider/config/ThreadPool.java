package vip.rory.dht.spider.config;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhanghangtian
 * @date 2019年7月9日 上午11:23:00
 */
public class ThreadPool {

    public static ThreadPoolExecutor findNodeSend   = new ThreadPoolExecutor(4, 8, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(128), Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.DiscardPolicy());

    public static ThreadPoolExecutor messageSend    = new ThreadPoolExecutor(4, 8, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(1024), Executors.defaultThreadFactory());

    public static ThreadPoolExecutor messageReceive = new ThreadPoolExecutor(4, 8, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(4));

}

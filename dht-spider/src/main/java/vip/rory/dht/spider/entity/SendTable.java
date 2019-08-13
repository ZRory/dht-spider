package vip.rory.dht.spider.entity;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.stereotype.Component;

/**
 * 发送消息表
 * 
 * @author zhanghangtian
 * @date 2019年7月10日 上午10:33:43
 */
@Component
public class SendTable extends LinkedHashMap<String, String> {

    private static final long            serialVersionUID = 9167425038246905952L;

    //线程锁

    private final ReentrantReadWriteLock rwl              = new ReentrantReadWriteLock();

    private final Lock                   rLock            = rwl.readLock();

    private final Lock                   wLock            = rwl.writeLock();

    @Override
    public String get(Object key) {
        rLock.lock();
        try {
            return super.get(key);
        } finally {
            rLock.unlock();
        }
    }

    @Override
    public String remove(Object key) {
        wLock.lock();
        try {
            return super.remove(key);
        } finally {
            wLock.unlock();
        }
    }

    @Override
    public String put(String key, String value) {
        wLock.lock();
        try {
            return super.put(key, value);
        } finally {
            wLock.unlock();
        }
    }

    public void clearSendTable() {
        wLock.lock();
        try {
            if (this.size() > 20000) {
                int i = 0;
                //如果sendTable大于20000条则删除前面10000条元素
                Iterator<Entry<String, String>> iterator = this.entrySet().iterator();
                while (iterator.hasNext() && i < 10000) {
                    iterator.next();
                    iterator.remove();
                    i++;
                }
            }
        } finally {
            wLock.unlock();
        }
    }

}

package vip.rory.dht.spider.schedule;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import vip.rory.dht.spider.config.ThreadPool;
import vip.rory.dht.spider.entity.Node;
import vip.rory.dht.spider.entity.RoutingTable;
import vip.rory.dht.spider.entity.SendTable;
import vip.rory.dht.spider.service.MessageSenderService;

/**
 * @author zhanghangtian
 * @date 2019年7月9日 上午9:20:56
 */
@Slf4j
@Component
public class ScheduleTask {

    @Autowired
    private MessageSenderService messageSenderService;

    @Autowired
    private RoutingTable         routingTable;

    @Autowired
    private SendTable            sendTable;

    /**
     * 清理发送表任务
     */
    @Scheduled(initialDelay = 60 * 1000, fixedDelay = 60 * 1000)
    public void clearSendTableTask() {
        long taskStart = System.currentTimeMillis();
        sendTable.clearSendTable();
        log.info("清理发送表任务执行完毕，执行时长：{}，发送表大小：{}", System.currentTimeMillis() - taskStart, sendTable.size());
    }

    /**
     * 路由表ping请求
     */
    @Scheduled(initialDelay = 30 * 1000, fixedDelay = 5 * 60 * 1000)
    public void routingTablePingTask() {
        LocalDateTime ago = LocalDateTime.now().minusMinutes(15L);
        routingTable.wLock.lock();
        try {
            Iterator<Node> iterator = routingTable.iterator();
            while (iterator.hasNext()) {
                Node node = iterator.next();
                if (node.getLastContactTime().isBefore(ago)) {
                    iterator.remove();
                    continue;
                }
                messageSenderService.ping(node);
            }
        } finally {
            routingTable.wLock.unlock();
        }
        log.info("路由表检测任务执行完毕，执行时长：{}，路由表大小：{}", System.currentTimeMillis() - Timestamp.valueOf(ago).getTime(),
                routingTable.size());
    }

    /**
     * 任务监控队列/每60秒处理一次
     */
    @Scheduled(initialDelay = 30 * 1000, fixedDelay = 60 * 1000)
    public void appControlTask() {
        log.info("发送表大小：{}，路由表大小：{}，发送线程队列：{}，find_node线程队列：{}", sendTable.size(), routingTable.size(),
                ThreadPool.messageSend.getQueue().size(), ThreadPool.findNodeSend.getQueue().size());
    }

}

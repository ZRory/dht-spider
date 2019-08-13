package vip.rory.dht.spider.entity;

import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhanghangtian
 * @date 2019年7月9日 上午11:05:02
 */
@Component
public class RoutingTable extends TreeSet<Node> {

    private static final long            serialVersionUID = -2136316213657671100L;

    private static final Integer         maxSize          = 160;

    //线程锁

    private final ReentrantReadWriteLock rwl              = new ReentrantReadWriteLock();

    private final Lock                   rLock            = rwl.readLock();

    public final Lock                    wLock            = rwl.writeLock();

    @Autowired
    private Node                         localNode;

    public boolean add(Node node) {
        wLock.lock();
        try {
            if (super.contains(node)) {
                //遍历叠加权重
                for (Node tempNode : this) {
                    if (tempNode.equals(node)) {
                        node.setRank(tempNode.getRank() + node.getRank());
                    }
                }
            } else {
                //判断路由表是否超限
                if (this.size() > maxSize) {
                    for (int i = 0; i < this.size() - maxSize; i++) {
                        //丢弃权重最小的
                        this.remove(this.last());
                    }

                }
            }
            return super.add(node);
        } finally {
            wLock.unlock();
        }
    }

    /**
     * @return
     */
    public byte[] getEightNodes(boolean putSelf) {
        rLock.lock();
        try {
            byte[] nodes = new byte[8 * Node.NODE_LENGTH];
            int i = 0;
            if (putSelf) {
                //将自身放入返回节点中
                System.arraycopy(localNode.getNodeInfo(), 0, nodes, Node.NODE_LENGTH * i, Node.NODE_LENGTH);
                i++;
            }
            for (Node tempNode : this) {
                if (Math.random() < 0.2 && i < 8) {
                    System.arraycopy(tempNode.getNodeInfo(), 0, nodes, Node.NODE_LENGTH * i, Node.NODE_LENGTH);
                }
            }
            return nodes;
        } finally {
            rLock.unlock();
        }

    }

}

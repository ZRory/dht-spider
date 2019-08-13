package vip.rory.dht.spider.entity.message;

import lombok.Getter;
import lombok.Setter;
import vip.rory.dht.common.util.GenerateUtils;
import vip.rory.dht.spider.entity.message.body.AnnouncePeerRequest;
import vip.rory.dht.spider.entity.message.body.FindNodeRequest;
import vip.rory.dht.spider.entity.message.body.GetPeersRequest;
import vip.rory.dht.spider.entity.message.body.PingRequest;
import vip.rory.dht.spider.enumerate.DhtMessageMethodEnum;
import vip.rory.dht.spider.enumerate.DhtMessageTypeEnum;

/**
 * 查询消息
 * 
 * @author zhanghangtian
 * @date 2019年6月28日 下午6:45:49
 */
@Getter
@Setter
public class RequestMessage<T> extends BaseMessage {

    /**
     * method name of the query
     */
    private String q;
    /**
     * body
     */
    private T      a;

    /**
     * 无参构造器 初始化请求类型
     */
    public RequestMessage() {
        super();
        this.y = DhtMessageTypeEnum.REQUEST.getCode();
    }

    /**
     * 范型构造器 做一些初始化工作
     * 
     * @param q
     * @param a
     */
    public RequestMessage(T a) {
        super();
        this.a = a;
        this.t = GenerateUtils.generateTransactionId();
        this.y = DhtMessageTypeEnum.REQUEST.getCode();
        Class<? extends Object> aClass = a.getClass();
        if (aClass.equals(PingRequest.class)) {
            this.q = (DhtMessageMethodEnum.PING.getCode());
        } else if (aClass.equals(FindNodeRequest.class)) {
            this.q = (DhtMessageMethodEnum.FIND_NODE.getCode());
        } else if (aClass.equals(GetPeersRequest.class)) {
            this.q = (DhtMessageMethodEnum.GET_PEERS.getCode());
        } else if (aClass.equals(AnnouncePeerRequest.class)) {
            this.q = (DhtMessageMethodEnum.ANNOUNCE_PEER.getCode());
        }
    }

}

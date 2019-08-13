package vip.rory.dht.spider.entity.message;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhanghangtian
 * @date 2019年6月28日 下午6:51:37
 */
@Getter
@Setter
public class BaseMessage {

    /**
     * Transaction ID
     */
    protected String t;
    /**
     * Type of message DhtMessageTypeEnum
     */
    protected String y;

    /**
     * 
     */
    public BaseMessage() {
        super();
    }

}

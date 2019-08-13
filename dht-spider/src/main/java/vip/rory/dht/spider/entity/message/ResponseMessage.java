package vip.rory.dht.spider.entity.message;

import lombok.Getter;
import lombok.Setter;
import vip.rory.dht.spider.enumerate.DhtMessageTypeEnum;

/**
 * @author zhanghangtian
 * @date 2019年6月28日 下午6:45:59
 */
@Getter
@Setter
public class ResponseMessage<T> extends BaseMessage {

    /**
     * body
     */
    T r;

    /**
     * 无参构造器 初始化请求类型
     */
    public ResponseMessage() {
        super();
        this.y = DhtMessageTypeEnum.RESPONSE.getCode();
    }

    /**
     * @param r
     */
    public ResponseMessage(T r) {
        super();
        this.r = r;
        this.y = DhtMessageTypeEnum.RESPONSE.getCode();
    }

}

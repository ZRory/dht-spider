package vip.rory.dht.spider.entity.message;

import lombok.Getter;
import lombok.Setter;
import vip.rory.dht.spider.entity.message.body.ErrorMessageBody;
import vip.rory.dht.spider.enumerate.DhtMessageTypeEnum;

/**
 * @author zhanghangtian
 * @date 2019年7月1日 上午10:26:05
 */
@Getter
@Setter
public class ErrorMessage extends BaseMessage {

    private ErrorMessageBody e;

    /**
     * 无参构造器初始化请求类型
     */
    public ErrorMessage() {
        super();
        this.y = DhtMessageTypeEnum.ERROR.getCode();
    }

    /**
     * @param e
     */
    public ErrorMessage(ErrorMessageBody e) {
        super();
        this.e = e;
        this.y = DhtMessageTypeEnum.ERROR.getCode();
    }

}

package vip.rory.dht.spider.entity.message.body;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhanghangtian
 * @date 2019年7月1日 上午10:29:59
 */
@Getter
@Setter
public class ErrorMessageBody {

    private Integer errorCode;
    private String  errorDesc;

}

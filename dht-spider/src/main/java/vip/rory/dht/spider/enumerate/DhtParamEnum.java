package vip.rory.dht.spider.enumerate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import vip.rory.dht.common.enumerate.CodeEnum;

/**
 * @author zhanghangtian
 * @date 2019年7月2日 下午5:59:37
 */
@Getter
@AllArgsConstructor
public enum DhtParamEnum implements CodeEnum<String> {
    TRANSACTIONID("t", "事务Id"),
    TYPE("y", "消息类型"),
    METHOD("q", "查询类型"),
    ARGUMENTS("a", "请求参数"),
    RESPONSE("r", "相应参数");
    private String code;
    private String message;

}

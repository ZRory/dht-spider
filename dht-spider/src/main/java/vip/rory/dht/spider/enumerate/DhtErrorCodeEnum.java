package vip.rory.dht.spider.enumerate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import vip.rory.dht.common.enumerate.CodeEnum;

/**
 * DHT协议错误枚举类
 * 
 * @author zhanghangtian
 * @date 2019年6月28日 下午1:14:27
 */
@Getter
@AllArgsConstructor
public enum DhtErrorCodeEnum implements CodeEnum<Integer> {
    GENERIC_ERROR(201, "一般错误"),
    SERVER_ERROR(202, "服务错误"),
    PROTOCOL_ERROR(203, "协议错误"),
    METHOD_UNKNOWN(204, "未知方法");

    private Integer code;
    private String  message;

}

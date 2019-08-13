package vip.rory.dht.spider.enumerate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import vip.rory.dht.common.enumerate.CodeEnum;

/**
 * DHT消息类型
 * 
 * @author zhanghangtian
 * @date 2019年6月28日 下午1:58:25
 */
@Getter
@AllArgsConstructor
public enum DhtMessageTypeEnum implements CodeEnum<String> {
    /**
     * Queries, or KRPC message dictionaries with a "y" value of "q", contain
     * two additional keys; "q" and "a". Key "q" has a string value containing
     * the method name of the query. Key "a" has a dictionary value containing
     * named arguments to the query.
     */
    REQUEST("q", "请求消息"),
    /**
     * Responses, or KRPC message dictionaries with a "y" value of "r", contain
     * one additional key "r". The value of "r" is a dictionary containing named
     * return values. Response messages are sent upon successful completion of a
     * query.
     */
    RESPONSE("r", "响应消息"),
    /**
     * Errors, or KRPC message dictionaries with a "y" value of "e", contain one
     * additional key "e". The value of "e" is a list. The first element is an
     * integer representing the error code. The second element is a string
     * containing the error message. Errors are sent when a query cannot be
     * fulfilled. The following table describes the possible error
     * codes:201,202,203,204.
     */
    ERROR("e", "错误消息");

    private String code;
    private String message;

    public static DhtMessageTypeEnum getEnumByCode(String code) {
        for (DhtMessageTypeEnum e : DhtMessageTypeEnum.values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }

}

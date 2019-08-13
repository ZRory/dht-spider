package vip.rory.dht.spider.entity.message.body;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhanghangtian
 * @date 2019年6月28日 下午7:00:15
 */
@Getter
@Setter
public class PingRequest {

    public static final String ID = "id";
    /**
     * querying nodes id
     */
    private byte[]       id;

}

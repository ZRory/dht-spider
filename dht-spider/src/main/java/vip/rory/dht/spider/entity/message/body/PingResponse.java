package vip.rory.dht.spider.entity.message.body;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhanghangtian
 * @date 2019年6月28日 下午7:00:56
 */
@Getter
@Setter
public class PingResponse {

    public static final String ID = "id";

    /**
     * queried nodes id
     */
    private byte[]       id;

}

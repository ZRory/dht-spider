package vip.rory.dht.spider.entity.message.body;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhanghangtian
 * @date 2019年7月1日 上午9:11:48
 */
@Getter
@Setter
public class FindNodeResponse {

    public static final String ID    = "id";
    public static final String NODES = "nodes";

    /**
     * queried nodes id
     */
    private byte[]             id;
    /**
     * compact node info
     */
    private byte[]             nodes;

}

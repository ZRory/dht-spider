package vip.rory.dht.spider.entity.message.body;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhanghangtian
 * @date 2019年7月1日 上午9:07:19
 */
@Getter
@Setter
public class FindNodeRequest {

    public static final String ID     = "id";
    public static final String TARGET = "target";

    /**
     * querying nodes id
     */
    private byte[]             id;
    /**
     * id of target node
     */
    private byte[]             target;

}
